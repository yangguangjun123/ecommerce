package org.myproject.ecommerce;

import org.myproject.ecommerce.services.MongoDBService;
import org.myproject.ecommerce.services.ProductCatalogService;
import org.myproject.ecommerce.services.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

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