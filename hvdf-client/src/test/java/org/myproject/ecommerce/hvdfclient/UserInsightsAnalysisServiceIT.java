package org.myproject.ecommerce.hvdfclient;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationConfiguration.class})
public class UserInsightsAnalysisServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Autowired
    private UserInsightsAnalysisService userInsightsAnalysisService;

    private List<Long> times = List.of(1516181741620L, 1516182790560L, 1516182882582L,
            1516184589023L, 1516184589524L, 1516535591361L,
            1516535610283L, 1516535706984L, 1516535808443L,
            1516535944773L);

    @Before
    public void setUp() throws InterruptedException {
        times.stream()
             .forEach(this::setupTestData);
        Thread.sleep(10000);
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
        assertEquals(1200 * 5 * 2, userInsights.getCount());
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
