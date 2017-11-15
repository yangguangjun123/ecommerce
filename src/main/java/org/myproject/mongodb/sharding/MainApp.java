package org.myproject.mongodb.sharding;

import org.myproject.mongodb.sharding.datamodel.AudioAlbum;
import org.myproject.mongodb.sharding.datamodel.Film;
import org.myproject.mongodb.sharding.datamodel.Product;
import org.myproject.mongodb.sharding.services.MongoDBService;
import org.myproject.mongodb.sharding.services.ProductCatalogService;
import org.myproject.mongodb.sharding.services.ProductCatalogJsonDataService;
import org.myproject.mongodb.sharding.services.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EnableAutoConfiguration
@Component
@ComponentScan
public class MainApp implements CommandLineRunner {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Autowired
    private ProductInventoryService productInventoryService;

    public static void main(String[] args) {
        // run Spring boot application
        SpringApplication.run(MainApp.class, args).close();

//        ConfigurableApplicationContext ctx = SpringApplication.run(MainApp.class, args);
//        MainApp mainObj = ctx.getBean(MainApp.class);
//        mainObj.init();
//        System.out.println("Application exited");
    }

//    public void init() {
//        System.out.println("inside init method");
//    }

    @Override
    public void run(String... strings) throws Exception {
        System.out.println("Start MongoDB Sharding Application ...");

        System.out.println("MongoDB Sharding Application Complete ...");
    }

}