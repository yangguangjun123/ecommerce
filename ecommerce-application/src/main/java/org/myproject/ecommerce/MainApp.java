package org.myproject.ecommerce;

import org.bson.codecs.configuration.CodecProvider;
import org.myproject.ecommerce.core.interfaces.IProductCatalogService;
import org.myproject.ecommerce.core.interfaces.IProductInventoryService;
import org.myproject.ecommerce.core.services.MongoDBService;
import org.myproject.ecommerce.core.services.ProductCategoryService;
import org.myproject.ecommerce.core.services.StoreInventoryService;
import org.myproject.ecommerce.core.services.StoreService;
import org.myproject.ecommerce.core.utilities.LoggingUtils;
import org.myproject.ecommerce.hvdfclient.HVDFClientService;
import org.myproject.ecommerce.hvdfclient.UserInsightsAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class MainApp implements CommandLineRunner {
    private List<CodecProvider> codecProvider;
    private MongoDBService mongoDBService;
    private IProductCatalogService productCatalogService;
    private IProductInventoryService productInventoryService;
    private ProductCategoryService productCategoryService;
    private StoreInventoryService storeInventoryService;
    private StoreService storeService;
    private HVDFClientService hvdfClientService;
    private UserInsightsAnalysisService userInsightsAnalysisService;

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) {
        // run Spring boot application
        SpringApplication.run(MainApp.class, args).close();

    }

    @Override
    public void run(String... strings) throws Exception {
        LoggingUtils.info(logger, "Start E-Commerce/MongoDB Sharding Application ...");

        LoggingUtils.info(logger, "E-Commerce/MongoDB Sharding Application Complete ...");
    }

    @Autowired
    public void setCodecProvider(List<CodecProvider> codecProvider) {
        this.codecProvider = codecProvider;
    }

    @Autowired
    public void setMongoDBService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @Autowired
    public void setProductCatalogService(IProductCatalogService productCatalogService) {
        this.productCatalogService = productCatalogService;
    }

    @Autowired
    public void setProductInventoryService(IProductInventoryService productInventoryService) {
        this.productInventoryService = productInventoryService;
    }

    @Autowired
    public void setProductCategoryService(ProductCategoryService productCategoryService) {
        this.productCategoryService = productCategoryService;
    }

    @Autowired
    public void setStoreInventoryService(StoreInventoryService storeInventoryService) {
        this.storeInventoryService = storeInventoryService;
    }

    @Autowired
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }

    @Autowired
    public void setHvdfClientService(HVDFClientService hvdfClientService) {
        this.hvdfClientService = hvdfClientService;
    }

    @Autowired
    public void setUserInsightsAnalysisService(UserInsightsAnalysisService userInsightsAnalysisService) {
        this.userInsightsAnalysisService = userInsightsAnalysisService;
    }
}