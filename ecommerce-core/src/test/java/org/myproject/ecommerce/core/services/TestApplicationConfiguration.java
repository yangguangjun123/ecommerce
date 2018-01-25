package org.myproject.ecommerce.core.services;

import org.bson.codecs.configuration.CodecProvider;
import org.myproject.ecommerce.core.codec.CustomCodecProvider;
import org.myproject.ecommerce.core.utilities.SKUCodeProductIdGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestApplicationConfiguration {

    @Bean(name = "codecProvider")
    public List<CodecProvider> codecProvider() {
        List<CodecProvider> codecProvider = new ArrayList<>();
        codecProvider.add(new CustomCodecProvider());
        return codecProvider;
    }

    @Bean
    public MongoDBService mongoDBService() {
        return new MongoDBService(codecProvider());
    }

    @Bean
    public StoreService storeService() {
        return new StoreService(mongoDBService());
    }

    @Bean
    public ProductCategoryService productCategoryService() {
        return new ProductCategoryService(mongoDBService());
    }

    @Bean
    public PaymentService paymentService() {
        return new PaymentService();
    }

    @Bean
    public ProductInventoryService productInventoryService() {
        return new ProductInventoryService(mongoDBService(), paymentService());
    }

    @Bean
    public SKUCodeProductIdGenerator skuCodeProductIdGenerator() {
        return new SKUCodeProductIdGenerator(mongoDBService());
    }

    @Bean
    ProductCatalogService productCatalogService() {
        return new ProductCatalogService(mongoDBService(), skuCodeProductIdGenerator());
    }

    @Bean
    public StoreInventoryService storeInventoryService() {
        return new StoreInventoryService(mongoDBService(), productCatalogService());
    }

    @Bean
    public PriceService priceService() {
        return new PriceService(mongoDBService(), productCatalogService(), storeService());
    }

}
