package org.myproject.ecommerce.app;

import org.myproject.ecommerce.core.interfaces.IProductCatalogService;
import org.myproject.ecommerce.core.interfaces.IProductInventoryService;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.myproject.ecommerce")
public class MainApp implements CommandLineRunner {
    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private IProductCatalogService productCatalogService;

    @Autowired
    private IProductInventoryService productInventoryService;

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

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