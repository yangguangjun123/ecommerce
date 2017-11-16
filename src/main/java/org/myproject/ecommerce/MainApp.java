package org.myproject.ecommerce;

import org.myproject.ecommerce.interfaces.IProductCatalogService;
import org.myproject.ecommerce.interfaces.IProductInventoryService;
import org.myproject.ecommerce.services.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MainApp(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    public static void main(String[] args) {
        // run Spring boot application
        SpringApplication.run(MainApp.class, args).close();

    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("Start MongoDB Sharding Application ...");

        System.out.println("MongoDB Sharding Application Complete ...");
    }

}