package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.TestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductCatalogServiceIT.CustomConfiguration.class, TestConfiguration.class})
public class ProductCatalogServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldAddItemToCart() {
        // when


        // given


        // verify
        fail("to implement");
    }

    @Configuration
    public static class CustomConfiguration {
        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

    }

}
