package org.myproject.ecommerce.hvdfclient;

import org.apache.commons.io.IOUtils;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.json.JsonReader;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.core.utilities.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;

@Service
public class UserInsightsAnalysisService {
    private final MongoDBService mongoDBService;
    private final HVDFClientPropertyService hvdfClientPropertyService;

    private static final Logger logger = LoggerFactory.getLogger(UserInsightsAnalysisService.class);

    @Autowired
    public UserInsightsAnalysisService(MongoDBService mongoDBService,
                                       HVDFClientPropertyService hvdfClientPropertyService) {
        this.mongoDBService = mongoDBService;
        this.hvdfClientPropertyService = hvdfClientPropertyService;
    }

    @PostConstruct
    public void initilaise() {
        checkData();
    }

    private void checkData() {
        try {
            Properties mapReduceProps = new Properties();
            mapReduceProps.load(getClass().getResourceAsStream("/mapreduce/mapreduce.properties"));
            mapReduceProps.keySet()
                    .stream()
                    .map(Object::toString)
                    .filter(key -> key != null && key.startsWith("mapreduce"))
                    .map(key -> mapReduceProps.getProperty(key))
                    .filter(value -> value != null && value.length() > 0)
                    .map(value -> value.split(","))
                    .flatMap(Arrays::stream)
                    .peek(file -> LoggingUtils.info(logger, "file name: " + ("/mapreduce/" + file)))
                    .forEach(file -> {
                                try {
                                    Objects.requireNonNull(IOUtils.toString(getClass().getResourceAsStream(
                                            "/mapreduce/" + file.trim()), StandardCharsets.UTF_8.name()));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    throw new RuntimeException("cannot read default map/reduce function: " + file);
                                }
                            }
                        );
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot read mapreduce property file: " + "/mapreduce/mapreduce.properties");
        }
    }

    public List<Activity> findUserActivities(String userId, long startTime, long endTime,
                                             long numberOfUserActivities) {
        return LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                    endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .map(collection -> findUserActivities(userId, startTime, endTime, collection))
                .flatMap(List::stream)
                .limit(numberOfUserActivities)
                .collect(toList());
    }

    private List<Activity> findUserActivities(String userId, long startTime, long endTime,
                                              String collectionName) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(collectionName);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.userId", userId);
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("data.ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);
        HashMap<String, Object> endTimeQueryMap = new HashMap<>();
        endTimeQueryMap.put("data.ts", endTime);
        filterMap.put("$lt", endTimeQueryMap);
        Map<String, Integer> sortMap = new LinkedHashMap<>();
        sortMap.put("data.ts", -1);
        return mongoDBService.readAll("ecommerce", collectionName,
                Activity.class, filterMap, Optional.of(sortMap));
    }

    public List<Activity> findProductActivities(String itemId, long startTime, long endTime,
                                                long numberOfProductActivities) {
        return LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                    endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .map(collection -> findProductActivities(itemId, startTime, endTime, collection))
                .flatMap(List::stream)
                .limit(numberOfProductActivities)
                .collect(toList());
    }

    public void performUserActivityAnalysis(String outputType, String output, long hoursBefore, boolean sharded) {
        LoggingUtils.info(logger, "outputType: " + outputType);
        Objects.requireNonNull(outputType);
        try {
            final String mapFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/map_activity.js"), StandardCharsets.UTF_8.name());
            final String reduceFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/reduce_activity.js"), StandardCharsets.UTF_8.name());
            final String finalizeFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/finalize_activity.js"), StandardCharsets.UTF_8.name());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime before = now.minusHours(hoursBefore);
            long startTime = before.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                    .toInstant().toEpochMilli();
            long endTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                    .toInstant().toEpochMilli();
            Map<String, Object> filterMap = new HashMap<>();
            HashMap<String, Object> startTimeQueryMap = new HashMap<>();
            startTimeQueryMap.put("data.ts", startTime);
            filterMap.put("$gt", startTimeQueryMap);
            mongoDBService.dropCollection("ecommerce", output);
            LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                    endTime / hvdfClientPropertyService.getPeriod()).boxed()
                    .sorted(reverseOrder())
                    .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                    .peek(coll -> LoggingUtils.info(logger, "perform map/reduce on: " + coll))
                    .forEach(coll -> mongoDBService.performMapReduce("ecommerce", coll, mapFunc,
                            reduceFunc, Optional.ofNullable(finalizeFunc), filterMap, outputType.toUpperCase(),
                            output,sharded));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void performUserActivityAnalysis(String mapFunc, String reduceFunc, Optional<String> finalizeFunc,
                                            String outputType, String output, long hoursBefore, boolean sharded) {
        Objects.requireNonNull(outputType);
        Objects.requireNonNull(mapFunc);
        Objects.requireNonNull(reduceFunc);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusHours(hoursBefore);
        long startTime = before.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();
        long endTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();
        Map<String, Object> filterMap = new HashMap<>();
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("data.ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);
        mongoDBService.dropCollection("ecommerce", output);
        LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                    endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .forEach(coll -> mongoDBService.performMapReduce("ecommerce", coll, mapFunc,
                        reduceFunc, finalizeFunc, filterMap, outputType.toUpperCase(), output, sharded));
    }

    public void performUserPurchaseActivityAnalysis(String mapFunc, String reduceFunc, Optional<String> finalizeFunc,
                                                    String outputType, String output, long daysBefore,
                                                    boolean sharded) {
        Objects.requireNonNull(outputType);
        Objects.requireNonNull(mapFunc);
        Objects.requireNonNull(reduceFunc);
        mongoDBService.dropCollection("ecommerce", output);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime before = now.minusDays(daysBefore);
        long startTime = before.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();
        long endTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                .toInstant().toEpochMilli();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.type", Activity.Type.ORDER.name());
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("data.ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);
        LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                endTime / hvdfClientPropertyService.getPeriod()).boxed()
                .sorted(reverseOrder())
                .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .peek(coll -> LoggingUtils.info(logger, "perform map/reduce on collection: " + coll))
                .forEach(coll -> mongoDBService.performMapReduce("ecommerce", coll, mapFunc,
                        reduceFunc, finalizeFunc, filterMap, outputType.toUpperCase(), output,sharded));
    }

    public void performUserPurchaseActivityAnalysis(String outputType, String output, long daysBefore,
                                                    boolean sharded) {
        Objects.requireNonNull(outputType);
        mongoDBService.dropCollection("ecommerce", output);
        try {
            final String mapFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/map_purchase.js"), StandardCharsets.UTF_8.name());
            final String reduceFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/reduce_purchase.js"), StandardCharsets.UTF_8.name());
            final String finalizeFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/finalize_purchase.js"), StandardCharsets.UTF_8.name());
            performUserPurchaseActivityAnalysis(mapFunc, reduceFunc, Optional.ofNullable(finalizeFunc), outputType,
                    output, daysBefore, sharded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Activity> findProductActivities(String itemId, long startTime, long endTime,
                                                 String collectionName) {
        Objects.requireNonNull(itemId);
        Objects.requireNonNull(collectionName);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.itemId", itemId);
        HashMap<String, Object> startTimeQueryMap = new HashMap<>();
        startTimeQueryMap.put("data.ts", startTime);
        filterMap.put("$gt", startTimeQueryMap);
        HashMap<String, Object> endTimeQueryMap = new HashMap<>();
        endTimeQueryMap.put("data.ts", endTime);
        filterMap.put("$lt", endTimeQueryMap);
        Map<String, Integer> sortMap = new LinkedHashMap<>();
        sortMap.put("data.ts", -1);
        return mongoDBService.readAll("ecommerce", collectionName,
                Activity.class, filterMap, Optional.of(sortMap));
    }

    public List<UserInsights> getUserInsights(String userId, LocalDate date) {
        try {
            String query = IOUtils.toString(getClass().getResourceAsStream("/user_insights.json"),
                    "UTF-8");
            List<BsonDocument> documents = parse(query);
            documents.get(0).getDocument("$match").put("data.userId", new BsonString(userId));
            ZonedDateTime ldtZoned = date.atStartOfDay().atZone(ZoneId.systemDefault());
            ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
            documents.get(0).getDocument("$match").getDocument("ts").put("$gt",
                    new BsonInt64(utcZoned.toInstant().toEpochMilli()));
            LocalDateTime now = LocalDateTime.now();
            long startTime = utcZoned.toInstant().toEpochMilli();
            ldtZoned = now.atZone(ZoneId.systemDefault());
            utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
            long endTime = utcZoned.toInstant().toEpochMilli();
            List<Bson> pipeline = documents.stream().filter(d -> d instanceof BsonDocument)
                    .map(d -> (Bson) d).collect(toList());
            return LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                        endTime / hvdfClientPropertyService.getPeriod()).boxed()
                    .sorted(reverseOrder())
                    .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                    .map(collection -> mongoDBService.executeAggregatePipineline("ecommerce",
                            collection, (List<Bson>) pipeline, UserInsights.class))
                    .flatMap(List::stream)
                    .collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private List<BsonDocument> parse(String jsonArray) {
        final CodecRegistry codecRegistry = CodecRegistries.fromProviders(Arrays.asList(new ValueCodecProvider(),
                new BsonValueCodecProvider(),
                new DocumentCodecProvider()));
        JsonReader reader = new JsonReader(jsonArray);
        BsonArrayCodec arrayReader = new BsonArrayCodec(codecRegistry);
        BsonArray docArray = arrayReader.decode(reader, DecoderContext.builder().build());
        return docArray.getValues().stream()
                .filter(d -> d instanceof BsonDocument)
                .map(d -> (BsonDocument) d)
                .collect(toList());
    }

    public UserInsights getUserTotalSalesInsights(String userId, LocalDate date) {
        Map<String, Map<String, Object>> pipelineStageMap = new LinkedHashMap<>();

        // match stage
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.userId", userId);
        HashMap<String, Object> timeQueryMap = new HashMap<>();
        ZonedDateTime ldtZoned = date.atStartOfDay().atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        timeQueryMap.put("ts", utcZoned.toInstant().toEpochMilli());
        filterMap.put("$gt", timeQueryMap);
        filterMap.put("data.type", Activity.Type.ORDER.name());
        pipelineStageMap.put("match", filterMap);

        // group stage
        Map<String, Object> groupMap = new HashMap<>();
        Map<String, String> groupOperationMap = new HashMap<>();
        groupMap.put("_id", "result");
        groupOperationMap.put("$sum", "$data.order.total");
        groupMap.put("count", groupOperationMap);
        pipelineStageMap.put("group", groupMap);

        LocalDateTime now = LocalDateTime.now();
        long startTime = utcZoned.toInstant().toEpochMilli();
        ldtZoned = now.atZone(ZoneId.systemDefault());
        utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        long endTime = utcZoned.toInstant().toEpochMilli();

        Optional<UserInsights> userInsights =
                LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                        endTime / hvdfClientPropertyService.getPeriod()).boxed()
                    .sorted(reverseOrder())
                    .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                    .map(collection -> mongoDBService.executeAggregatePipineline("ecommerce",
                            collection, pipelineStageMap, UserInsights.class))
                    .flatMap(List::stream)
                    .reduce((u1, u2) -> new UserInsights("result", u1.getCount() + u2.getCount()));
        return userInsights.orElse(new UserInsights("result", 0L));
    }

    public List<UserInsights> getProductInsights(String itemId, LocalDate date) {
        Map<String, Map<String, Object>> pipelineStageMap = new LinkedHashMap<>();

        // match stage
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("data.itemId", itemId);
        HashMap<String, Object> timeQueryMap = new HashMap<>();
        ZonedDateTime ldtZoned = date.atStartOfDay().atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        timeQueryMap.put("ts", utcZoned.toInstant().toEpochMilli());
        filterMap.put("$gt", timeQueryMap);
        pipelineStageMap.put("match", filterMap);

        // group stage
        Map<String, Object> groupMap = new HashMap<>();
        Map<String, Integer> groupOperationMap = new HashMap<>();
        groupMap.put("_id", "$data.type");
        groupOperationMap.put("$sum", 1);
        groupMap.put("count", groupOperationMap);
        pipelineStageMap.put("group", groupMap);

        LocalDateTime now = LocalDateTime.now();
        long startTime = utcZoned.toInstant().toEpochMilli();
        ldtZoned = now.atZone(ZoneId.systemDefault());
        utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        long endTime = utcZoned.toInstant().toEpochMilli();

        return LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                            endTime / hvdfClientPropertyService.getPeriod()).boxed()
                        .sorted(reverseOrder())
                        .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                        .map(collection -> mongoDBService.executeAggregatePipineline("ecommerce",
                                collection, pipelineStageMap, UserInsights.class))
                        .flatMap(List::stream)
                .collect(Collectors.groupingBy(
                        UserInsights::getId,
                        HashMap::new,
                        Collectors.summingLong(UserInsights::getCount)))
                .entrySet().stream()
                           .map(e -> new UserInsights(e.getKey(), e.getValue())).collect(toList());
    }

    public <T> List<T> getUserAggregates(String inputName, Class<T> clazz) {
        return mongoDBService.readAll("ecommerce", inputName, clazz);
    }

    public long getNumberOfUniqueUserAggregates(String inputName) {
        return mongoDBService.count("ecommerce", inputName);
    }

    public void performUserPurchaseOccurrenceAnalysis(String input, String outputType, String output,
                                                      boolean sharded) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(output);
        Objects.requireNonNull(outputType);
        try {
            final String mapFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/map_purchase_occurrence.js"), StandardCharsets.UTF_8.name());
            final String reduceFunc = IOUtils.toString(getClass().getResourceAsStream(
                    "/mapreduce/reduce_purchase_occurrence.js"), StandardCharsets.UTF_8.name());
            performUserPurchaseOccurrenceAnalysis(input, mapFunc, reduceFunc, Optional.empty(), outputType,
                    output, sharded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void performUserPurchaseOccurrenceAnalysis(String input, String mapFunc, String reduceFunc,
                                                      Optional<String> finalizeFunc, String outputType,
                                                      String output, boolean sharded) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(output);
        Objects.requireNonNull(outputType);
        Objects.requireNonNull(mapFunc);
        Objects.requireNonNull(reduceFunc);
        mongoDBService.dropCollection("ecommerce", output);
        mongoDBService.performMapReduce("ecommerce", input, mapFunc,
                reduceFunc, finalizeFunc, new HashMap<>(), outputType.toUpperCase(), output,sharded);
    }

    public List<UserPurchaseOccurrenceAggregate> getAllUserPurchaseOccurrenceAggregates() {
        return getUserAggregates("pairs", UserPurchaseOccurrenceAggregate.class);
    }

    public List<UserPurchaseOccurrenceAggregate> getAllUserPurchaseOccurrenceAggregates(String itemId, long count) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id.a", itemId);
        HashMap<String, Object> countQueryMap = new HashMap<>();
        countQueryMap.put("value", count);
        filterMap.put("$gt", countQueryMap);
        Map<String, Integer> sortMap = new LinkedHashMap<>();
        sortMap.put("value", -1);
        return mongoDBService.readAll("ecommerce", "pairs",
                UserPurchaseOccurrenceAggregate.class, filterMap, Optional.of(sortMap));

    }
}
