package org.myproject.ecommerce.hvdfclient;

import com.mongodb.MongoClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HVDFClientServiceIT.CustomConfiguration.class})
public class HVDFClientServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Test
    public void shouldRecordAListOfAcivities() {
        // given
        List<Activity> activities = new ArrayList<>();
        ActivityDataBuilder builder = new ActivityDataBuilder();
        builder.setUserId("u123").setGeoCode(1).setSessionId("2373BB")
                .setDevice(new Activity.Device("1234", "mobile/iphone", "Chrome/34.0.1847.131"))
                .setType(Activity.Type.VIEW).setItemId("301671").setSku("730223104376")
                .setOrder(new Activity.Order("12520185"))
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
                .setOrder(new Activity.Order("12520185"))
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
        long timeStart = 1516131360922L;
        long timeEnd = 1516131407242L;
        int limit = 10;
        Map<String, Object> queryParamMap = Map.of("source", userId, "ts", timeEnd,
                "range", (timeEnd - timeStart), "limit", limit);

        // when
        List<Activity> activities = hvdfClientService.query(queryParamMap);

        // verify
        assertTrue(activities.size() == 3);
        activities.stream()
                .forEach(a -> Assert.assertEquals(userId, a.getSource()));
        activities.stream()
                .forEach(a -> Assert.assertEquals(userId, a.getData().getUserId()));
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoClient mongoClient;

        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

        @Autowired
        private HVDFClientService hvdfClientService;

        @Bean
        HVDFClientService hvdfClientService() {
            return new HVDFClientService();
        }

        @Bean
        MongoClient mongoClient(){
            return new MongoClient();
        }

        @Bean
        RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
            return new MappingJackson2HttpMessageConverter();
        }

    }


}
