package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.Price;
import org.myproject.ecommerce.domain.Product;
import org.myproject.ecommerce.domain.ProductVariation;
import org.myproject.ecommerce.domain.Store;
import org.myproject.ecommerce.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PriceServiceIT.CustomConfiguration.class})
public class PriceServiceIT {
    @Autowired
    private ProductCatalogService productCatalogService;

    @Autowired
    private PriceService priceService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnPriceForGivenProductAndStore() {
        // given
        String productId = "30671";
        String storeName = "store23";
        Product product = new Product();
        product.setProductId(productId);
        Store store = new Store();
        store.setId("23");
        store.setName(storeName);
        int expectedPrice = 1200;

        // when
        Optional<Integer> price = priceService.getProductPrice(product, Optional.of(store));

        // verify
        assertTrue(expectedPrice == price.get());
    }

    @Test
    public void shouldReturnPriceForGivenProductVariationAndStore() {
        // given
        String sku = "93284847362823";
        String storeName = "store23";
        ProductVariation productVariation = new ProductVariation();
        productVariation.setSku(sku);
        Store store = new Store();
        store.setId("23");
        store.setName(storeName);
        int expectedPrice = 1200;

        // when
        Optional<Integer> price = priceService.getProductVariationPrice(productVariation, Optional.of(store));

        // verify
        assertTrue(expectedPrice == price.get());
    }

    @Test
    public void shouldReturnStorePricesForGivenProduct() {
        // given
        String productId = "30671";
        String storeName = "store23";
        Product product = new Product();
        product.setProductId(productId);
        Store store = new Store();
        store.setId("23");
        store.setName(storeName);
        int expectedPrice = 1200;

        // when
        List<Price> prices = priceService.getStorePrices(product, store);

        // verify
        assertTrue(prices.size() > 0);
        prices.stream()
              .forEach(p -> assertTrue(expectedPrice == p.getPrice()));
    }

    @Test
    public void shouldReturnStorePricesForGivenProductvariation() {
        // given
        String sku = "93284847362823";
        String storeName = "store23";
        ProductVariation productVariation = new ProductVariation();
        productVariation.setSku(sku);
        Store store = new Store();
        store.setId("23");
        store.setName(storeName);
        int expectedPrice = 1200;

        // when
        List<Price> prices = priceService.getStorePrices(productVariation, store);

        // verify
        assertTrue(prices.size() > 0);
        prices.stream()
                .forEach(p -> assertTrue(expectedPrice == p.getPrice()));
    }

    @Test
    public void shouldReturnAllPricesForGivenProducts() {
        // given
        String productId = "30671";
        Product product = new Product();
        product.setProductId(productId);
        int expectedPrice = 1200;

        // when
        List<Price> prices = priceService.getPrices(product);

        // verify
        assertTrue(prices.size() > 0);
        prices.stream()
                .forEach(p -> assertTrue(expectedPrice == p.getPrice()));
    }

    @Test
    public void shouldReturnAllPricesForGivenProductVariation() {
        // given
        String sku = "93284847362823";
        ProductVariation productVariation = new ProductVariation();
        productVariation.setSku(sku);
        int expectedPrice = 1200;

        // when
        List<Price> prices = priceService.getPrices(productVariation);

        // verify
        assertTrue(prices.size() > 0);
        prices.stream()
                .forEach(p -> assertTrue(expectedPrice == p.getPrice()));
    }

    @Test
    public void shouldReturnAllPricesForSpecificItem() {
        // given
        List<String> priceIds = Arrays.asList("30671_store23", "30671_sgroup12",
                "93284847362823_store23", "93284847362823_sgroup12");
        int expectedPrice = 1200;

        // when
        List<Price> prices = priceService.getPrices(priceIds);

        // verify
        assertTrue(prices.size() == 4);
        prices.stream()
                .forEach(p -> assertTrue(expectedPrice == p.getPrice()));
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private ProductCatalogService productCatalogService;

        @Autowired
        private SKUCodeProductIdGenerator skuCodeGeneratorService;

        @Autowired
        private StoreService storeService;

        @Autowired
        private PriceService priceService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        PriceService priceService() {
            return new PriceService();
        }

        @Bean
        ProductCatalogService productCatalogService() {
            return new ProductCatalogService(mongoDBService);
        }

        @Bean
        SKUCodeProductIdGenerator skuCodeProductIdGenerator() {
            return new SKUCodeProductIdGenerator();
        }

        @Bean
        StoreService storeService() {
            return new StoreService();
        }
    }



}
