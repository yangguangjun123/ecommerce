package org.myproject.ecommerce.hvdfclient;

import com.mongodb.MongoClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HVDFClientServiceIT.CustomConfiguration.class})
public class HVDFClientServiceIT {
    @Autowired
    private HVDFClientService hvdfClientService;

    @Test
    public void shouldReturnUserActivitiesWhenUserIdAndTimeStampReceived() {
        // when

        // given

        // verify
        fail("to implement");
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
