package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.configuration.CodecProvider;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestApplicationConfiguration {

    @Bean(name="hvdfServiceUrl")
    public String serviceUrl(){
        return "http://localhost:8080";
    }


    @Bean(name = "codecProvider")
    public List<CodecProvider> codecProvider() {
        List<CodecProvider> codecProvider = new ArrayList<>();
        codecProvider.add(new HVDFCustomCodecProvider());
        return codecProvider;
    }

    @Bean
    public MongoDBService mongoDBService() {
        return new MongoDBService(codecProvider());
    }
    @Bean
    HVDFClientService hvdfClientService() {
        return new HVDFClientService(serviceUrl(), mongoDBService(), restTemplate(),
                mappingJackson2HttpMessageConverter());
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        return new MappingJackson2HttpMessageConverter();
    }

    @Bean
    HVDFClientPropertyService hvdfClientPropertyService() {
        return new HVDFClientPropertyService(mongoDBService());
    }

    @Bean
    UserInsightsAnalysisService userInsightsAnalysisService() {
        return new UserInsightsAnalysisService(mongoDBService(), hvdfClientPropertyService());
    }

}
