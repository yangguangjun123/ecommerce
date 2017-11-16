package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductInventoryServiceIT.CustomConfiguration.class})
public class ProductInventoryServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ProductInventoryService productInventoryService;

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
    static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        ProductInventoryService productInventoryService() {
            return new ProductInventoryService(mongoDBService);
        }

    }

}
