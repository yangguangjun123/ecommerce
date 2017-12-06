package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.Product;
import org.myproject.ecommerce.utilities.SKUCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductCatalogServiceIT.CustomConfiguration.class})
public class ProductCatalogServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private SKUCodeService skuCodeGeneratorService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnCorrectNumberOfProducts() {
        // given

        // when
        long number = mongoDBService.count("ecommerce", "product", Product.class);

        // verify
        assertEquals(100003, number);
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private SKUCodeService skuCodeGeneratorService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        ProductCatalogService productCatalogService() {
            return new ProductCatalogService(mongoDBService);
        }

        @Bean
        SKUCodeService skuCodeGeneratorService() {
            return new SKUCodeService();
        }
    }

}
