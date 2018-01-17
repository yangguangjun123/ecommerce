package org.myproject.ecommerce.hvdfclient;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class UserInsightsAnalysisServiceIT {
    @Autowired
    private UserInsightsAnalysisService userInsightsAnalysisService;

    @Test
    @Ignore
    public void shouldReturnUserActivities() {
        // when
        String userId = "";
        long ts1 = 1301284969946L;
        long ts2 = 1425657300L;

        // given
        List<Activity> userActivities = userInsightsAnalysisService.findUserActivities(userId, ts1, ts2);

        // verify
        assertTrue(userActivities.size() > 0);
    }
}
