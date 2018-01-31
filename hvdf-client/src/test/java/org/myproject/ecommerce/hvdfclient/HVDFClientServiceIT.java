package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationConfiguration.class})
public class HVDFClientServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Autowired
    private MongoDBService mongoDBService;

    private String channelPrefix = "activity_";
    private long period = 0L;

    private static final Map<String, Long> timeValues = new HashMap<>();

    static {
        // All valid time units used in config
        timeValues.put("milliseconds", 1L);
        timeValues.put("millisecond", 1L);
        timeValues.put("seconds", 1000L);
        timeValues.put("second", 1000L);
        timeValues.put("minutes", 60 * 1000L);
        timeValues.put("minute", 60 * 1000L);
        timeValues.put("hours", 60 * 60 * 1000L);
        timeValues.put("hour", 60 * 60 * 1000L);
        timeValues.put("days", 24 * 60 * 60 * 1000L);
        timeValues.put("day", 24 * 60 * 60 * 1000L);
        timeValues.put("weeks", 7 * 24 * 60 * 60 * 1000L);
        timeValues.put("week", 7 * 24 * 60 * 60 * 1000L);
        timeValues.put("years", 365 * 24 * 60 * 60 * 1000L);
        timeValues.put("year", 365 * 24 * 60 * 60 * 1000L);
    }

    @Before
    public void setUp() throws InterruptedException, IOException {
        List<Long> times = List.of(1516181741620L, 1516182790560L, 1516182882582L,
                1516184589023L, 1516184589524L, 1516535591361L,
                1516535610283L, 1516535706984L, 1516535808443L,
                1516535944773L);
        times.stream()
                .forEach(this::setupTestData);
        Thread.sleep(10000);

        Optional<Document> configOptional = mongoDBService.readOne("config",
                "hvdf_channels_ecommerce", Document.class, new HashMap<>());
        String configJsonString = configOptional.get().toJson();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readValue(configJsonString, JsonNode.class);
        Objects.requireNonNull(rootNode.get("storage"));
        Objects.requireNonNull(rootNode.get("storage").get("type"));
        String storageType = rootNode.get("storage").get("type").textValue();
        channelPrefix = channelPrefix + storageType + "_";
        Objects.requireNonNull(rootNode.get("time_slicing"));
        Objects.requireNonNull(rootNode.get("time_slicing").get("config"));
        Objects.requireNonNull(rootNode.get("time_slicing").get("config").get("period"));
        JsonNode periodNode = rootNode.get("time_slicing").get("config").get("period");
        periodNode.fields().forEachRemaining(entry ->
                period = period + timeValues.get(entry.getKey()) * entry.getValue().asInt());
        if (period == 0) {
            throw new IllegalArgumentException("time slicing period cannot be zero");
        }
    }

    @After
    public void tearDown() {
        ZonedDateTime ldtZoned = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        long time = utcZoned.toInstant().toEpochMilli();
        String collection = channelPrefix + String.valueOf(time / period);
        mongoDBService.dropCollection("ecommerce", collection);
    }

    @Test
    public void shouldRecordAListOfAcivities() {
        // given
        List<Activity> activities = new ArrayList<>();
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185", 1200))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime now = LocalDateTime.now();
        builder.setTime(now)
                .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        activities.add(new Activity("u123",
                now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity()));
        builder = new ActivityDataBuilder();
        builder.setUserId("u457").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185", 1200))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        now = LocalDateTime.now();
        builder.setTime(now)
                .setTimeStamp(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        activities.add(new Activity("u457",
                now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity()));

        // when
        boolean result = hvdfClientService.record(activities, Activity.class);

        // verify
        assertTrue(result);
    }

    @Test
    public void shouldReturnAListofUserActivities() {
        // given
        String userId = "u123";
        long timeStart = 1516181741620L;
        long timeEnd = 1516535944773L;
        int limit = 20;
        Map<String, Object> queryParamMap = Map.of("source", userId, "ts", timeEnd,
                "range", (timeEnd - timeStart), "limit", limit);

        // when
        List<Activity> activities = hvdfClientService.query(queryParamMap);

        // verify
        assertTrue(activities.size() == 10);
        activities.stream()
                .forEach(a -> {
                    Assert.assertEquals(userId, a.getSource());
                    Assert.assertEquals(userId, a.getData().getUserId());
                    Assert.assertTrue(a.getTimeStamp() >= timeStart);
                    Assert.assertTrue(a.getTimeStamp() <= timeEnd);
                });
    }

    private void setupTestData(long time) {
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
        builder.setTime(localDateTime)
                .setTimeStamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Activity activity = new Activity("u123",
                localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity());
        hvdfClientService.record(activity);

        builder = new ActivityDataBuilder();
        builder.setUserId("u457").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.ORDER).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185", 1200))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
        builder.setTime(localDateTime)
                .setTimeStamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        activity = new Activity("u457",
                localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity());
        hvdfClientService.record(activity);
    }

}
