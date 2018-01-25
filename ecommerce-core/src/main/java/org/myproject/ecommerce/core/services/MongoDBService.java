package org.myproject.ecommerce.core.services;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.myproject.ecommerce.core.codec.CustomCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.unwind;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.nearSphere;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.inc;
import static com.mongodb.client.model.Updates.popLast;
import static com.mongodb.client.model.Updates.pushEach;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.unset;
import static java.util.stream.Collectors.toList;

@Service("mongoDBService")
@Qualifier("mongoDBService")
@SuppressWarnings("unchecked")
public class MongoDBService {
    private MongoClient mongoClient;
    private static final int SORT_ASCENDING_ORDER = 1;

    private static final Logger logger = LoggerFactory.getLogger(MongoDBService.class);

    @Autowired
    public MongoDBService(@Qualifier("codecProvider")  List<CodecProvider> codecProvider) {
        List<CodecProvider> allCodecProviders = new ArrayList<>();
        allCodecProviders.add(new CustomCodecProvider());
        allCodecProviders.addAll(codecProvider);
        configMongoClient(allCodecProviders);
    }

    private void configMongoClient(List<CodecProvider> codecProviderList) {
        CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClient.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(codecProviderList.toArray(new CodecProvider[codecProviderList.size()])),
                pojoCodecRegistry);
        MongoClientOptions options = MongoClientOptions.builder().codecRegistry(codecRegistry)
                .build();
        mongoClient = new MongoClient("localhost", options);
    }

    public <T> void createOne(String databaseName, String collectionName, Class<T> clazz, T document) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        collection.insertOne(document);
    }

    public <T> void createAll(String databaseName, String collectionName, Class<T> clazz, List<T> documents) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        collection.insertMany(documents);
    }

    public <T> List<T> readAll(String databaseName, String collectionName, Class<T> clazz,
                               Map<String, Object> filter) {
        return readAll(databaseName, collectionName, clazz, filter, Optional.empty());
    }

    public <T> List<T> readAll(String databaseName, String collectionName, Class<T> clazz,
                               Map<String, Object> filter, Optional<Map<String, Integer>> sortOptional) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<Bson> filters = filter.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filter))
                .collect(Collectors.toList());
        List<T> result = new ArrayList<>();
        Consumer<? super T> consumer = t -> result.add(t);
        if(sortOptional.isPresent()) {
            List<Bson> sort =
                    sortOptional.get().keySet().stream()
                                               .map(key -> sortOptional.get().get(key) == SORT_ASCENDING_ORDER ?
                                                       Sorts.ascending(key) : Sorts.descending(key))
                                               .collect(toList());
            collection.find(combine(filters)).sort(Sorts.orderBy(sort)).forEach(consumer);
        } else {
            collection.find(combine(filters)) .forEach(consumer);
        }
        return result;
    }

    public <T> void readAll(String databaseName, String collectionName, Class<T> clazz,
                            Map<String, Object> filter, Consumer<T> consumer) {
        readAll(databaseName, collectionName, clazz, filter)
                .stream()
                .forEach(consumer);
    }

    public <T> List<T> readAll(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<T> result = new ArrayList<>();
        Consumer<? super T> consumer = t -> result.add(t);
        collection.find().forEach(consumer);
        return result;
    }

    public <T> Optional<T> readById(String databaseName, String collectionName, Class<T> clazz, Object id) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("_id", id);
        return this.readOne(databaseName, collectionName, clazz, filter);
    }

    public <T> Optional<T> readOne(String databaseName, String collectionName, Class<T> clazz,
                                            Map<String, Object> filter) {
        List<T> results = readAll(databaseName, collectionName, clazz, filter);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public <T> long getDocumentCount(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        return collection.count();
    }

    public <T> boolean addOne(String databaseName, String collectionName, Class<T> clazz,
                              Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        UpdateResult updateResult = null;
        try {
            updateResult = process(databaseName, collectionName, clazz, queryFilterMap,
                    valueMap, new HashMap<>(), this::convert);
        } catch (EcommerceException e) {
            e.printStackTrace();
            return false;
        }
        return updateResult.getModifiedCount() == 1 ? true : false;
    }

    public <T> boolean removeOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap) {
        UpdateResult updateResult = null;
        try {
            updateResult = process(databaseName, collectionName, clazz, queryFilterMap,
                    valueMap, new HashMap<>(), this::convert);
        } catch (EcommerceException e) {
            e.printStackTrace();
            return false;
        }
        return updateResult.getModifiedCount() == 1 ? true : false;
    }

    public <T> boolean updateMany(String databaseName, String collectionName, Map<String, Object> queryFilterMap,
                                  Map<String, Object> valueMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = queryFilterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, queryFilterMap))
                .collect(toList());
        List<Bson>  updates = convert(valueMap);
        collection.updateMany(and(filters), combine(updates));
        return true;
    }

    public <T> boolean updateOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> queryFilterMap, Map<String, Object> valueMap,
                                 Map<String, Object> updateOptions) {
        UpdateResult updateResult = null;
        try {
            updateResult = process(databaseName, collectionName, clazz, queryFilterMap,
                    valueMap, updateOptions, this::convert);
        } catch (EcommerceException e) {
            e.printStackTrace();
            return false;
        }
        return updateResult.getModifiedCount() == 1 ? true : false;
    }

    public void deleteAll(String databaseName, String collectionName) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        collection.deleteMany(new Document());
    }

    public void deleteMany(String databaseName, String collectionName, Map<String, Object> filterMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        collection.deleteMany(and(filters));
    }

    public void deleteOne(String databaseName, String collectionName, Map<String, Object> filterMap) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection collection = mongoDatabase.getCollection(collectionName);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        collection.deleteOne(and(filters));
    }

    public <T> boolean replaceOne(String databaseName, String collectionName, Class<T> clazz,
                                 Map<String, Object> filterMap, T value) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        List<Bson> filters = filterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, filterMap))
                .collect(toList());
        UpdateResult result = collection.replaceOne(and(filters), value);
        return result.getModifiedCount() == 1L;
    }

    public <T> long count(String databaseName, String collectionName, Class<T> clazz) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);
        return collection.count();
    }

    public void writeJson(String databaseName, String collectionName, String jsonString) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        collection.insertOne(Document.parse(jsonString));
    }

    public Map<String, Object> processAggregatePipeline(String databaseName, String collectionName,
                                             List<Map<String, Object>> pipeline, List<String> resultFields) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        Map<String, Object> result = new HashMap<>();
        Consumer<Document> consumer = document ->
                resultFields.stream()
                            .forEach(field -> result.put(field, document.get(field)));
        List<Bson> pipelines = pipeline
                .stream()
                .map(m -> mapAggregatePipeline(m))
                .filter(o -> o.isPresent())
                .map(Optional::get)
                .collect(toList());
        collection.aggregate(pipelines).forEach(consumer);
        return result;
    }

    private Optional<Bson> mapAggregatePipeline(Map<String, Object> pipelineMap) {
        Objects.requireNonNull(pipelineMap);
        if(pipelineMap.keySet().size() == 0) {
            return Optional.empty();

        }
        String key = pipelineMap.keySet().iterator().next();
        if(key.equals("$match")) {
            Map<String, Object> filterMap = (Map<String, Object>) pipelineMap.get("$match");
            List<Bson> filters = filterMap.keySet()
                    .stream()
                    .map(k -> mapBsonFilter(k, filterMap))
                    .collect(toList());
            return Optional.of(match(and(filters)));
        } else if(key.equals("$unwind")) {
            return Optional.of(unwind((String) pipelineMap.get("$unwind")));
        } else if(key.equals("$group")) {
            List<Object> groupParameters = (List<Object>) pipelineMap.get("$group");
            String groupId = (String) groupParameters.get(0);
            Map<String, Object> groupMap = (Map<String, Object>) groupParameters.get(1);
            String groupOperator = groupMap.keySet().iterator().next();
            if("$sum".equals(groupOperator)) {
                List<String> sumParameters = (List<String>) groupMap.get("$sum");
                return Optional.of(group(groupId, sum((String) sumParameters.get(0),
                        sumParameters.get(1))));
            } else {
                logger.error("unknow group operator: " + groupOperator);
                return Optional.empty();
            }
        } else {
            logger.error("unknow aggregate operator: " + key);
            return Optional.empty();
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("dispose of mongoClient");
        mongoClient.close();
    }

    private <T> UpdateResult process(String databaseName, String collectionName, Class<T> clazz,
                                           Map<String, Object> queryFilterMap, Map<String, Object> updateMap,
                                           Map<String, Object> updateOptions,
                                           Function<Map<String, Object>, List<Bson>> convert)
            throws EcommerceException {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        List<Document> documents = readAll("ecommerce",
                collectionName, Document.class, queryFilterMap);

        if(documents.size() == 0) {
            logger.error("documents contain no record: " +
                    Objects.toString(queryFilterMap));
            throw new EcommerceException("documents contain no record: " +
                    Objects.toString(queryFilterMap));
        }

        if(documents.size() > 1) {
            logger.info("documents contain more than one record: " +
                    Objects.toString(queryFilterMap));
            throw new EcommerceException("documents contain more than one record: " +
                    Objects.toString(queryFilterMap));
        }

        Document document  = documents.get(0);
        queryFilterMap.put("_id", document.get("_id"));
        List<Bson> filters = queryFilterMap.keySet()
                .stream()
                .map(key -> mapBsonFilter(key, queryFilterMap))
                .collect(toList());
        List<Bson>  updates = convert.apply(updateMap);
        if(updateOptions.containsKey("writeConcern")) {
            collection = collection.withWriteConcern(WriteConcern.valueOf(
                    (String) updateOptions.get("writeConcern")));
        }
        return collection.updateOne(and(filters), combine(updates));
    }

    private Bson mapBsonFilter(String key, final Map<String, Object>  queryFilterMap) {
        Objects.requireNonNull(key);
        if(key.equals("$eq")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$eq");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return eq(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$gte")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$gte");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return gte(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$gt")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$gt");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return gt(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$lt")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$lt");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return lt(keys.get(0), fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$in")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$in");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return in(keys.get(0), (List) fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$regex")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$regex");
            List<String> keys = fieldValueMap.keySet().stream().collect(toList());
            return regex(keys.get(0), (Pattern) fieldValueMap.get(keys.get(0)));
        } else if(key.equals("$nearSphere")) {
            Map<String, Object> fieldValueMap = (Map<String, Object>) queryFilterMap.get("$nearSphere");
            return nearSphere((String) fieldValueMap.get("fieldName"), (Point) fieldValueMap.get("geometry"),
                    (Double) fieldValueMap.get("maxDistance"),
                    fieldValueMap.containsKey("minDistance") ? (Double) fieldValueMap.get("minDistance") : 0.0);
        } else {
            return eq(key, queryFilterMap.get(key));
        }
    }

    private List<Bson> convert(Map<String, Object> valueMap) {
        List<Bson> addOrRemoveOperators = convertAddOrRemove(Optional.ofNullable((Map<String, Object>)
                valueMap.get("addOrRemove")));

        List<Bson> incOperators = convertIncOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("inc")));

        Optional<Bson> pullOperators = convertPullOperators(Optional.ofNullable((Map<String, Object>)
                valueMap.get("pull")));

        List<Bson> combined = new ArrayList<>();
        combined.addAll(addOrRemoveOperators);
        combined.addAll(incOperators);
        if(pullOperators.isPresent()) {
            combined.add(pullOperators.get());
        }
        return combined;
    }

    private List<Bson> convertAddOrRemove(Optional<Map<String, Object>> valueMapOptional) {
        if(!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> {
                    if (valueMap.get(key) instanceof List) {
                        if(((List) valueMap.get(key)).size() > 0) {
                            return pushEach(key, (List) valueMap.get(key));
                        } else {
                            return popLast(key);
                        }
                    } else {
                        if(valueMap.get(key) instanceof Optional && !((Optional) valueMap.get(key)).isPresent()) {
                            return unset(key);
                        } else {
                            return set(key, valueMap.get(key));
                        }
                    }
                })
                .collect(toList());
    }

    private List<Bson> convertIncOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return new ArrayList<>();
        }
        Map<String, Object> valueMap = valueMapOptional.get();
        return valueMap.keySet()
                .stream()
                .map(key -> inc(key, (Integer) valueMap.get(key)))
                .collect(toList());
    }

    private Optional<Bson> convertPullOperators(Optional<Map<String, Object>> valueMapOptional) {
        if (!valueMapOptional.isPresent()) {
            return Optional.empty();
        }

        Map<String, Object> valueMap = valueMapOptional.get();
        Object[] keys = valueMap.keySet().toArray();
        String key = (String) keys[0];
        Object value = valueMap.get(key);
        String[] fields = key.split("\\.");
        Bson filter = Filters.eq(fields[fields.length - 1], value);
        for(int i = fields.length - 2; i >= 0; i--) {
            filter = Filters.eq(fields[i], filter);
        }

        //Bson filter = Filters.eq("carted", Filters.eq("cart_id", 42));
        return Optional.of(Updates.pullByFilter(filter));
    }

    public  <T> List<T> performGeoQuery(String databaseName, String collectionName, Class<T> clazz,
                                        Map<String, Object> geoQueryMap, Map<String, Object> filterMap,
                                        List<Map<String, Object>> aggregatePipelineMapList) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<T> collection = mongoDatabase.getCollection(collectionName, clazz);

        Document query = new Document();
        filterMap.keySet().stream()
                .forEach(key -> query.append(key, new Document("$eq", filterMap.get(key))));
        Document geoNearFields = new Document();
        geoNearFields.put("near", geoQueryMap.get("geometry"));
        geoNearFields.put("distanceField", geoQueryMap.get("distanceFieldName"));
        geoNearFields.put("maxDistance", geoQueryMap.get("maxDistance"));
        geoNearFields.put("spherical", true);
        geoNearFields.put("query", query);
        Document geoNear = new Document("$geoNear", geoNearFields);

        // build aggregate pipeline
        List<Bson> pipeline = new ArrayList<>();
        pipeline.add((Bson) geoNear);
        List<Bson> aggregatePipelineList =
                aggregatePipelineMapList
                        .stream()
                        .map(m -> {
                            if (m.containsKey("$limit")) {
                                return Optional.of((Bson) new Document("$limit", m.get("$limit")));
                            } else if (m.containsKey("$unwind")) {
                                return Optional.of(Aggregates.unwind((String) m.get("$unwind")));
                            } else if (m.containsKey("$match")) {
                                Map<String, Object> queryFilterMap = (Map<String, Object>) m.get("$match");
                                List<Bson> filters = queryFilterMap.keySet()
                                        .stream()
                                        .map(key -> mapBsonFilter(key, queryFilterMap))
                                        .collect(toList());
                                return Optional.of(Aggregates.match(and(filters)));
                            } else {
                                return Optional.<Bson>empty();
                            }
                        })
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(toList());
        pipeline.addAll(aggregatePipelineList);

        Bson excluded = Aggregates.project(
                Projections.fields(
                        Projections.exclude((String) geoQueryMap.get("distanceFieldName"))
                )
        );
        pipeline.add(excluded);

        List<T> result = new ArrayList<>();
        Consumer<T> consumer = document -> {
            result.add(document);
        };
        collection.aggregate(pipeline).forEach(consumer);
        return result;
    }
}
