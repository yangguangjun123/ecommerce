package org.myproject.ecommerce;

import org.myproject.ecommerce.interfaces.IProductCatalogService;
import org.myproject.ecommerce.interfaces.IProductInventoryService;
import org.myproject.ecommerce.services.MongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class MainApp implements CommandLineRunner {
    private final MongoDBService mongoDBService;

    @Autowired
    private IProductCatalogService productCatalogService;

    @Autowired
    private IProductInventoryService productInventoryService;

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    @Autowired
    public MainApp(@Qualifier("mongoDBService") MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    public static void main(String[] args) {
        // run Spring boot application
        SpringApplication.run(MainApp.class, args).close();

    }

    @Override
    public void run(String... strings) throws Exception {
        logger.info("Start MongoDB Sharding Application ...");

        logger.info("MongoDB Sharding Application Complete ...");
    }

}