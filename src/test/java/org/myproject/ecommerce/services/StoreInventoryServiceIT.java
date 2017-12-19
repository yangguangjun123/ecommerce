package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.StoreInventory;
import org.myproject.ecommerce.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { StoreInventoryServiceIT.CustomConfiguration.class } )
public class StoreInventoryServiceIT {

    @Autowired
    private StoreInventoryService storeInventoryService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnAListOfStoreInventory() {
        // given
        String productId = "20034";
        String storeId = "store100";
        String sku = "sku11736";

        // when
        List<StoreInventory.StoreVariation> storeInventories =
                storeInventoryService.getStoreInventoryVariations(storeId, productId, sku);

        // verify
        assertEquals(4, storeInventories.size());
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private SKUCodeProductIdGenerator skuCodeGeneratorService;

        @Autowired
        private StoreService storeService;

        @Autowired
        private PriceService priceService;

        @Autowired
        private StoreInventoryService storeInventoryService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        ProductCatalogService productCatalogService() {
            return new ProductCatalogService(mongoDBService);
        }

        @Bean
        SKUCodeProductIdGenerator skuCodeGeneratorService() {
            return new SKUCodeProductIdGenerator();
        }

        @Bean
        PriceService priceService() {
            return new PriceService();
        }

        @Bean
        StoreService storeService() {
            return new StoreService();
        }

        @Bean
        StoreInventoryService storeInventoryService() {
            return new StoreInventoryService();
        }

    }

}
