package org.myproject.ecommerce.hvdfclient;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UserInsightsAnalysisServiceIT.CustomConfiguration.class})
public class UserInsightsAnalysisServiceIT {
    @Autowired
    private UserInsightsAnalysisService userInsightsAnalysisService;

    @Ignore
    @Test
    public void shouldReturnUserActivities() {
        // when
        String userId = "u123";
        long ts1 = 1516131360922L;
        long ts2 = 1516131407242L;

        // given
        List<Activity> userActivities = userInsightsAnalysisService.findUserActivities(userId, ts1, ts2);

        // verify
        assertTrue(userActivities.size() > 0);
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private UserInsightsAnalysisService userInsightsAnalysisService;

        @Bean
        MongoDBService mongoDBService(){
            return new MongoDBService();
        }

        @Bean
        UserInsightsAnalysisService userInsightsAnalysisService() {
            return new UserInsightsAnalysisService();
        }

    }

}
