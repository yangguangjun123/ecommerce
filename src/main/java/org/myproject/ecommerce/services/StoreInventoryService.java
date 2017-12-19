package org.myproject.ecommerce.services;

import static java.util.stream.Collectors.toList;

import org.myproject.ecommerce.domain.ProductVariation;
import org.myproject.ecommerce.domain.StoreInventory;
import org.myproject.ecommerce.domain.StoreInventory.StoreVariation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;

@Service
public class StoreInventoryService {
    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @PostConstruct
    public void initialise() {
        if(mongoDBService.getDocumentCount("ecommerce", "store_inventory",
                StoreInventory.class) != 1) {
            StoreInventory store100Inventory = new StoreInventory();
            store100Inventory.setId("902372093572409542jbf42r2f2432");
            store100Inventory.setStoreId("store100");
            store100Inventory.setLocation(Arrays.asList(-86.95444, 33.40178));
            store100Inventory.setProductId("20034");

            List<ProductVariation> product20034Variations =
                    productCatalogService.getAllProductVariationsByProductId("20034",
                            ProductVariation.class);
            List<StoreInventory.StoreVariation> storeVariations =
                    product20034Variations.stream()
                                          .map(p -> {
                                                  if("sku1".equals(p.getSku())) {
                                                      return new StoreVariation("sku1", 5);
                                                  } else if("sku2".equals(p.getSku())) {
                                                      return new StoreVariation("sku2", 23);
                                                  } else if("sku3".equals(p.getSku())) {
                                                      return new StoreVariation("sku3", 2);
                                                  } else {
                                                      return new StoreVariation(p.getSku(), 1);
                                                  }
                                                })
                                          .collect(toList());
            store100Inventory.setStoreVariations(storeVariations);
            mongoDBService.createOne("ecommerce", "store_inventory",
                    StoreInventory.class, store100Inventory);
        }
    }

    public List<StoreVariation> getStoreInventoryVariations(String storeId, String productId, String sku) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", productId);
        filterMap.put("storeId", storeId);
        filterMap.put("vars.sku", sku);
        List<StoreVariation> storeVariations = new ArrayList<>();
        Consumer<StoreInventory> consumer = s -> storeVariations.addAll(s.getStoreVariations());
        mongoDBService.readAll("ecommerce", "store_inventory",
                StoreInventory.class, filterMap, consumer );
        return storeVariations;
    }

    public void deleteAll() {
        mongoDBService.deleteAll("ecommerce", "store_inventory");
    }
}
