package org.myproject.ecommerce.hvdfclient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.core.utilities.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationConfiguration.class})
public class UserInsightsAnalysisServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Autowired
    private UserInsightsAnalysisService userInsightsAnalysisService;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private HVDFClientPropertyService hvdfClientPropertyService;

    private List<Long> times = List.of(1516181741620L, 1516182790560L, 1516182882582L,
            1516184589023L, 1516184589524L, 1516535591361L,
            1516535610283L, 1516535706984L, 1516535808443L,
            1516535944773L);

    private static boolean isSetupDone = false;
    private static final Logger logger = LoggerFactory.getLogger(UserInsightsAnalysisServiceIT.class);

    @Before
    public void setUp() throws InterruptedException {
        if(!isSetupDone) {
            if(!checkTestData()) {
                times.stream()
                        .forEach(t -> {
                            setupTestData(t, Activity.Type.VIEW);
                            setupTestData(t, Activity.Type.ORDER);
                        });
                Thread.sleep(10000);
            }

            Stream<String> items = Stream.of("2", "2","3", "3", "3", "3", "8", "8", "8", "8", "8", "8", "8", "8");
            List<Activity.Type> types = List.of(Activity.Type.VIEW, Activity.Type.ORDER);
            LocalDateTime now = LocalDateTime.now();
            long startTime = now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"))
                    .toInstant().toEpochMilli();
            long endTime = now.plusSeconds(4).atZone(ZoneId.systemDefault())
                    .withZoneSameInstant(ZoneId.of("UTC")).toInstant().toEpochMilli();
            List<String> userIds = List.of("u123", "u457");

            LongStream.rangeClosed(startTime / hvdfClientPropertyService.getPeriod(),
                    endTime / hvdfClientPropertyService.getPeriod()).boxed()
                    .sorted(reverseOrder())
                    .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                    .peek(coll -> LoggingUtils.info(logger, "drop collection: " + coll))
                    .forEach(collection -> mongoDBService.dropCollection("ecommerce", collection));

            items.forEach(itemId -> {
                types.stream()
                        .forEach(type -> {
                            userIds.stream()
                                    .forEach(userId -> {
                                        LoggingUtils.info(logger, "itemId/type/userId: " +
                                                String.format("%s/%s/%s", itemId, type, userId));
                                        setupTestData(itemId, type, userId);
                                    });
                        });
            });
            Thread.sleep(20000);

            isSetupDone = true;
        }
    }

    private void setupTestData(String itemId, Activity.Type type, String userId) {
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId(userId).setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(type).setItemId(itemId).setSku("730223104376")
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime now = LocalDateTime.now();
        ZonedDateTime ldtZoned = now.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        builder.setTime(now)
                .setTimeStamp(utcZoned.toInstant().toEpochMilli());
        Activity activity = new Activity(userId, utcZoned.toInstant().toEpochMilli(), builder.createActivity());
        if(!hvdfClientService.record(activity)) {
            fail("unable to setup test data");
        }
    }

    private boolean checkTestData() {
        List<Long> activitiesForU123 =
                LongStream.rangeClosed(times.get(0) / hvdfClientPropertyService.getPeriod(),
                        times.get(times.size() - 1) / hvdfClientPropertyService.getPeriod()).boxed()
                .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                .map(collection -> {
                    Map<String, Object> filterMap = new HashMap<>();
                    filterMap.put("data.userId", "u123");
                    HashMap<String, Object> startTimeQueryMap = new HashMap<>();
                    startTimeQueryMap.put("data.ts", times.get(0));
                    filterMap.put("$gte", startTimeQueryMap);
                    HashMap<String, Object> endTimeQueryMap = new HashMap<>();
                    endTimeQueryMap.put("data.ts", times.get(times.size() - 1));
                    filterMap.put("$lte", endTimeQueryMap);
                    Map<String, Integer> sortMap = new LinkedHashMap<>();
                    sortMap.put("data.ts", -1);
                    return mongoDBService.readAll("ecommerce", collection,
                            Activity.class, filterMap, Optional.of(sortMap));
                })
                .flatMap(List::stream)
                .map(Activity::getData)
                .map(Activity.Data::getTimeStamp)
                .sorted(Comparator.naturalOrder())
                .collect(toList());
        List<Long> activitiesForU457 =
                LongStream.rangeClosed(times.get(0) / hvdfClientPropertyService.getPeriod(),
                            times.get(times.size() - 1) / hvdfClientPropertyService.getPeriod()).boxed()
                        .map(time -> hvdfClientPropertyService.getChannelPrefix() + String.valueOf(time))
                        .map(collection -> {
                            Map<String, Object> filterMap = new HashMap<>();
                            filterMap.put("data.userId", "u457");
                            HashMap<String, Object> startTimeQueryMap = new HashMap<>();
                            startTimeQueryMap.put("data.ts", times.get(0));
                            filterMap.put("$gte", startTimeQueryMap);
                            HashMap<String, Object> endTimeQueryMap = new HashMap<>();
                            endTimeQueryMap.put("data.ts", times.get(times.size() - 1));
                            filterMap.put("$lte", endTimeQueryMap);
                            Map<String, Integer> sortMap = new LinkedHashMap<>();
                            sortMap.put("data.ts", -1);
                            return mongoDBService.readAll("ecommerce", collection,
                                    Activity.class, filterMap, Optional.of(sortMap));
                        })
                        .flatMap(List::stream)
                        .map(Activity::getData)
                        .map(Activity.Data::getTimeStamp)
                        .sorted(Comparator.naturalOrder())
                        .collect(toList());
        return Objects.equals(activitiesForU123, times) && Objects.equals(activitiesForU457, times);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnUserActivities() {
        // given
        String userId = "u123";
        long timeStart = 1516181741620L;
        long timeEnd = 1516535944773L;
        long numberOfUserActivities = 100;
        List<Long> expectedTimes = new ArrayList<>();
        expectedTimes.addAll(times.subList(1, times.size() - 1));
        expectedTimes.sort(Comparator.reverseOrder());

        // when
        List<Activity> userActivities = userInsightsAnalysisService.findUserActivities(userId, timeStart,
                timeEnd, numberOfUserActivities);

        // verify
        assertTrue(userActivities.size() == times.size() - 2);
        userActivities.stream()
                .forEach(a -> {
                    Assert.assertEquals(userId, a.getSource());
                    Assert.assertEquals(userId, a.getData().getUserId());
                });
        Assert.assertEquals(expectedTimes, userActivities.stream().map(Activity::getTimeStamp)
                .collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnProductActivities() {
        // given
        String itemId = "301671";
        long timeStart = 1516181741620L;
        long timeEnd = 1516535944773L;
        long numberOfPrductActivities = 100;
        List<Long> expectedTimes = new ArrayList<>();
        expectedTimes.addAll(times.subList(1, times.size() - 1));
        expectedTimes.sort(Comparator.reverseOrder());

        // when
        List<Activity> productActivities = userInsightsAnalysisService.findProductActivities(itemId, timeStart,
                timeEnd, numberOfPrductActivities);

        // verify
        assertTrue(productActivities.size() == times.size() * 2 - 4);
        productActivities.stream()
                .forEach(a -> Assert.assertEquals(itemId, a.getData().getItemId()));
        Assert.assertEquals(expectedTimes, productActivities.stream().map(Activity::getTimeStamp).distinct()
                .collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnUserActivityInsights() {
        // given
        String userId = "u123";
        LocalDate date = LocalDate.parse("2018-01-17");

        // when
        List<UserInsights> userInsights = userInsightsAnalysisService.getUserInsights(userId, date);

        // verify
        assertTrue(userInsights.size() >= 2);
        IntStream.rangeClosed(0, userInsights.size() - 1).boxed()
                 .sorted(Collections.reverseOrder())
                 .limit(2)
                 .map(index -> userInsights.get(index))
                 .forEach(insight -> {
                     assertEquals(Activity.Type.VIEW.name(), insight.getId());
                     assertTrue(insight.getCount() == 5);
                 });
    }

    @Test
    public void shouldReturnTotalSalesInsightsForUser() {
        // gievn
        String userId = "u457";
        LocalDate date = LocalDate.parse("2018-01-17");

        // when
        UserInsights userInsights = userInsightsAnalysisService.getUserTotalSalesInsights(userId, date);

        // verify
        assertTrue(userInsights.getCount() >= 1200 * 5 * 2);
        assertEquals("result", userInsights.getId());
    }


    @Test
    public void shouldReturnProductInsights() {
        // gievn
        String itemId = "301671";
        LocalDate date = LocalDate.parse("2018-01-17");

        // when
        List<UserInsights> userInsights = userInsightsAnalysisService.getProductInsights(itemId, date);

        // verify
        assertTrue(userInsights.size() == 2 );
        userInsights.stream()
                    .filter(u -> Activity.Type.VIEW.name().equals(u.getId()))
                    .forEach(u -> assertTrue(u.getCount() >= 10));
        userInsights.stream()
                .filter(u -> Activity.Type.ORDER.name().equals(u.getId()))
                .forEach(u -> assertTrue(u.getCount() >= 10));
    }

    @Test
    public void shouldPerformUserActivityAnalysis() throws InterruptedException {
        // given
        String outputType = "reduce";
        String output = "lastHourUniques";
        long hoursBefore = 1;
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserActivityAnalysis(outputType, output, hoursBefore, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldPerformUserActivityAnalysisWhenMapReduceFunctionsReceived() throws InterruptedException {
        // given
        String mapFunc = "function() {" +
                            "var key = this.data.userId;" +
                            "var value = {" +
                                "userId: this.data.userId," +
                                "count: 1" +
                            "};" +
                         "emit( key, value );" +
                         "}";
        String reduceFunc = "function(key, values) {" +
                                "var reducedObject = {" +
                                    "userId: key," +
                                    "count:0" +
                                "};" +
                                "values.forEach( function(value) {" +
                                    "reducedObject.count += value.count;" +
                                "});" +
                                "return reducedObject;" +
                                "}";
        String finalizeFunc = "function(key, reduced_value) {" +
                                "return reduced_value;"  +
                             "}";
        String outputType = "reduce";
        String output = "lastHourUniques";
        long hoursBefore = 1;
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserActivityAnalysis(mapFunc, reduceFunc,
                Optional.ofNullable(finalizeFunc), outputType, output, hoursBefore, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldReturnListOfUserActivityAggregates() {
        // given
        String inputName = "lastHourUniques";

        // when
        List<UserActivityAggregate> userActivityAggregates =
                userInsightsAnalysisService.getUserAggregates(inputName, UserActivityAggregate.class);

        // verify
        userActivityAggregates.stream()
                              .forEach(u -> {
                                  assertTrue(u.getValue().getUserId().length() > 0);
                                  assertTrue(u.getValue().getCount() >= 1);
                                  LoggingUtils.info(logger, u.toString());
                              });
    }

    @Test
    public void shouldPerformUserPurchaseActivityAnalysis() {
        // given
        String outputType = "reduce";
        String output = "lastDayOrders";
        long daysBefore = 1;
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserPurchaseActivityAnalysis(outputType, output,
                daysBefore, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldReturnNumberOfUniqueUsersFromUserActivityAggregate() {
        // given
        String inputName = "lastHourUniques";

        // when
        long count = userInsightsAnalysisService.getNumberOfUniqueUserAggregates(inputName);

        // verify
        assertTrue(count >= 2);
    }

    @Test
    public void shouldPerformUserPurchaseActivityAnalysisWhenMapAndReduceFunctionsReceived() {
        // given
        String mapFunc = "function() {" +
                            "var key = this.data.userId;" +
                            "var value = {" +
                                "userId: this.data.userId," +
                                "items: this.data.itemId" +
                            "};" +
                            "emit( key, value );" +
                            "}";
        String reduceFunc = "function(key, values) {" +
                                "var reducedObject = {" +
                                    "userId: key," +
                                    "items: []" +
                                "};" +
                                "values.forEach( function(value) {" +
                                "reducedObject.items = reducedObject.items.concat(value.items)" +
                                "});" +
                                "return reducedObject;" +
                                "}";
        String finalizeFunc = "function(key, reduced_value) {" +
                              "return reduced_value;" +
                              "}";
        String outputType = "reduce";
        String output = "lastDayOrders";
        long daysBefore = 1;
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserPurchaseActivityAnalysis(mapFunc, reduceFunc,
                Optional.ofNullable(finalizeFunc), outputType, output, daysBefore, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldReturnListofUserPurchaseAggregates() {
        // given
        String inputName = "lastDayOrders";

        // when
        List<UserPurchaseAggregate> userPurchaseAggregates =
                userInsightsAnalysisService.getUserAggregates(inputName, UserPurchaseAggregate.class);

        // verify
        userPurchaseAggregates.stream()
                .forEach(u -> {
                    assertTrue(u.getValue().getUserId().length() > 0);
                    assertTrue(u.getValue().getItems().size() >= 0);
                    LoggingUtils.info(logger, u.toString());
                });
    }

    @Test
    public void shouldPerformUserPurchaseOccurrenceAnalysis() {
        // given
        String input = "lastDayOrders";
        String outputType = "reduce";
        String output = "pairs";
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserPurchaseOccurrenceAnalysis(input, outputType, output, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldPerformUserPurchaseOccurrenceAnalysisWhenMapAndReduceFunctionsReceived() {
        // given
        String mapFunc = "function() {" +
                            "for (i = 0; i < this.value.items.length; i++) {" +
                                "for (j = i + 1; j <= this.value.items.length; j++) {" +
                                    "if (typeof this.value.items[j] != 'undefined') {" +
                                        "emit({a: this.value.items[i] ,b: this.value.items[j] }, 1);" +
                                    "}" +
                                "}" +
                            "}" +
                          "}";
        String reduceFunc = "function(key, values) {" +
                                "return Array.sum(values);" +
                            "}";
        String input = "lastDayOrders";
        String outputType = "reduce";
        String output = "pairs";
        boolean sharded = true;

        // when
        userInsightsAnalysisService.performUserPurchaseOccurrenceAnalysis(input, mapFunc, reduceFunc,
                Optional.empty(), outputType, output, sharded);

        // verify
        assertTrue(mongoDBService.count("ecommerce", output) > 0);
    }

    @Test
    public void shouldReturnAllUserPurchaseOccurrenceAggregates() {
        // given

        // when
        List<UserPurchaseOccurrenceAggregate> userPurchaseOccurrenceAggregates =
                userInsightsAnalysisService.getAllUserPurchaseOccurrenceAggregates();

        // verify
        assertTrue(userPurchaseOccurrenceAggregates.size() > 0);
        userPurchaseOccurrenceAggregates.stream()
                .forEach(u -> {
                    assertTrue(Long.parseLong(u.getId().getItem1()) > 0);
                    assertTrue(Long.parseLong(u.getId().getItem2()) > 0);
                    assertTrue(u.getCount() > 0);
                    LoggingUtils.info(logger, u.toString());
                });
    }

    @Test
    public void shouldReturnListOfUserPurchaseOccurrenceAggregates() {
        // given
        String itemId = "2";
        long count = 1L;

        // when
        List<UserPurchaseOccurrenceAggregate> userPurchaseOccurrenceAggregates =
                userInsightsAnalysisService.getAllUserPurchaseOccurrenceAggregates(itemId, count);

        // verify
        assertTrue(userPurchaseOccurrenceAggregates.size() > 0);
        userPurchaseOccurrenceAggregates.stream()
                .forEach(u -> {
                    assertTrue(Long.parseLong(u.getId().getItem1()) > 0);
                    assertTrue(Long.parseLong(u.getId().getItem2()) > 0);
                    assertTrue(u.getCount() > 0);
                    LoggingUtils.info(logger, u.toString());
                });
        List<Long> counts = userPurchaseOccurrenceAggregates
                .stream()
                .map(UserPurchaseOccurrenceAggregate::getCount)
                .collect(toList());
        List<Long> sortedCounts = counts.stream().collect(toList());
        sortedCounts.sort(Comparator.reverseOrder());
        assertEquals(counts, sortedCounts);
     }

    private void setupTestData(long time, Activity.Type type) {
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(type).setItemId("301671").setSku("730223104376")
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
        builder.setTime(localDateTime)
                .setTimeStamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Activity activity = new Activity("u123",
                localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity());
        if(!hvdfClientService.record(activity)) {
            fail("unable to setup test data");
        }

        builder = new ActivityDataBuilder();
        builder.setUserId("u457").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(type).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185", 1200))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
        builder.setTime(localDateTime)
                .setTimeStamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        activity = new Activity("u457",
                localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity());
        if(!hvdfClientService.record(activity)) {
            fail("unable to setup test data");
        }
    }
}
