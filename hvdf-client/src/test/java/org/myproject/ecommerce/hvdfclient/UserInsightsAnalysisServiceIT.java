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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationConfiguration.class})
public class UserInsightsAnalysisServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Autowired
    private UserInsightsAnalysisService userInsightsAnalysisService;

    @Before
    public void setUp() throws InterruptedException {
        List<Long> times = List.of(1516181741620L, 1516182790560L, 1516182882582L,
                1516184589023L, 1516184589524L, 1516535591361L,
                1516535610283L, 1516535706984L, 1516535808443L,
                1516535944773L);
        times.stream()
                .forEach(this::setupTestData);
        Thread.sleep(10000);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnUserActivities() {
        // when
        String userId = "u123";
        long timeStart = 1516181741620L;
        long timeEnd = 1516535944773L;
        List<Long> times = Arrays.asList(1516181741620L, 1516182790560L, 1516182882582L,
                1516184589023L, 1516184589524L, 1516535591361L,
                1516535610283L, 1516535706984L, 1516535808443L,
                1516535944773L);
        List<Long> expectedTimes = times.subList(1, times.size() -1);
        expectedTimes.sort(Comparator.reverseOrder());

                // given
        List<Activity> userActivities = userInsightsAnalysisService.findUserActivities(userId, timeStart, timeEnd);

        // verify
        assertTrue(userActivities.size() == times.size() - 2);
        userActivities.stream()
                .forEach(a -> {
                    Assert.assertEquals(userId, a.getSource());
                    Assert.assertEquals(userId, a.getData().getUserId());
                });
        Assert.assertEquals(expectedTimes, userActivities.stream()
                .map(Activity::getTimeStamp)
                .collect(Collectors.toList()));
    }

    private void setupTestData(long time) {
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
                .setLocations(Arrays.asList(-86.95444, 33.40178))
                .setTags(Arrays.asList("smartphone", "iphone"));
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.of("UTC"));
        builder.setTime(localDateTime)
                .setTimeStamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        Activity activity = new Activity("u123",
                localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), builder.createActivity());
        hvdfClientService.record(activity);
    }

}
