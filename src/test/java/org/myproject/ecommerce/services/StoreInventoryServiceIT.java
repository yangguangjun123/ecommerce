package org.myproject.ecommerce.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.Product;
import org.myproject.ecommerce.domain.StoreInventory;
import org.myproject.ecommerce.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { StoreInventoryServiceIT.CustomConfiguration.class } )
public class StoreInventoryServiceIT {
    @Autowired
    private ProductCatalogService productCatalogService;

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private StoreInventoryService storeInventoryService;

    private int quantityOfStoreInventory = 0;
    private int getQuantityOfProductCatalog = 0;

    @Before
    public void setUp() {
        quantityOfStoreInventory = storeInventoryService.getStoreInventoryVariation(
                "store100", "20034", "sku11736").get().getQuantity();
        getQuantityOfProductCatalog = productCatalogService.getProductByProductId(
                "20034", Product.class).get().getQuantity();
    }

    @After
    public void tearDown() {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", "20034");
        filterMap.put("storeId", "store100");
        filterMap.put("vars.sku", "sku11736");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("vars.$.quantity", quantityOfStoreInventory);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "store_inventory",
                Product.class, filterMap, updateMap, new HashMap<>());
        Product product = productCatalogService.getProductByProductId(
                "20034", Product.class).get();
        filterMap.clear();
        filterMap.put("_id", product.getId());
        valueMap.clear();
        valueMap.put("qty", getQuantityOfProductCatalog);
        updateMap.clear();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "product",
                Product.class, filterMap, updateMap, new HashMap<>());
    }

    @Test
    public void shouldReturnAListOfStoreInventory() {
        // given
        String productId = "20034";
        String storeId = "store100";
        String sku = "sku11736";

        // when
        Optional<StoreInventory.StoreVariation> storeVariation =
                storeInventoryService.getStoreInventoryVariation(storeId, productId, sku);

        // verify
        assertEquals(sku, storeVariation.get().getSku());
    }

    @Test
    public void shouldUpdateStoreInventoryQuantity() {
        // given
        String storeId = "store100";
        String productId = "20034";
        String sku = "sku11736";
        int quantity = 20;
        int oldQuantityStoreInventory = storeInventoryService.getStoreInventoryVariation(
                storeId, productId, sku).get().getQuantity();
        int oldQuantityProductCatalog = productCatalogService.getProductByProductId(
                productId, Product.class).get().getQuantity();

        // when
        storeInventoryService.updateStoreInventoryQuantity(storeId, productId, sku, quantity);

        // verify
        int newQuantityStoreInventory = storeInventoryService.getStoreInventoryVariation(
                storeId, productId, sku).get().getQuantity();
        int newQuantityProductCatalog = productCatalogService.getProductByProductId(
                productId, Product.class).get().getQuantity();
        assertEquals(quantity + oldQuantityStoreInventory, newQuantityStoreInventory);
        assertEquals(quantity + oldQuantityProductCatalog, newQuantityProductCatalog);
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
