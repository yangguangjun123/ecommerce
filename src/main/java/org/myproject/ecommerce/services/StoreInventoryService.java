package org.myproject.ecommerce.services;

import static java.util.stream.Collectors.toList;

import org.myproject.ecommerce.domain.Product;
import org.myproject.ecommerce.domain.ProductVariation;
import org.myproject.ecommerce.domain.StoreInventory;
import org.myproject.ecommerce.domain.StoreInventory.StoreVariation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger("chapters.introduction.HelloWorld1");

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

            int total = storeVariations.stream()
                                        .mapToInt(s -> s.getQuantity())
                                        .sum();
            Optional<Product> product20034 = productCatalogService.getProductByProductId(
                    "20034", Product.class);
            if(product20034.isPresent()) {
                Map<String, Object> filterMap = new HashMap<>();
                filterMap.put("_id", product20034.get().getId());
                Map<String, Object> valueMap = new HashMap<>();
                valueMap.put("qty", total);
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("addOrRemove", valueMap);
                mongoDBService.updateOne("ecommerce", "product",
                        Product.class, filterMap, updateMap, new HashMap<>());
            }
        }
    }

    public Optional<StoreVariation> getStoreInventoryVariation(String storeId, String productId, String sku) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", productId);
        filterMap.put("storeId", storeId);
        filterMap.put("vars.sku", sku);
        List<StoreVariation> storeVariations = new ArrayList<>();
        Consumer<StoreInventory> consumer = s -> storeVariations.addAll(s.getStoreVariations());
        mongoDBService.readAll("ecommerce", "store_inventory",
                StoreInventory.class, filterMap, consumer );
        return storeVariations.stream()
                              .filter(s -> sku.equals(s.getSku()))
                              .findFirst();
    }

    public void deleteAll() {
        mongoDBService.deleteAll("ecommerce", "store_inventory");
    }

    public void updateStoreInventoryQuantity(String storeId, String productId, String sku, int quantity) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", productId);
        filterMap.put("storeId", storeId);
        filterMap.put("vars.sku", sku);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("vars.$.quantity", quantity);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("inc", valueMap);
        if(!mongoDBService.updateOne("ecommerce", "store_inventory",
                Product.class, filterMap, updateMap, new HashMap<>())) {
            logger.error("unable to update store_inventory quantity: " +
                    String.format("[storeId - %s], [productId - %s], [sku - %s], [quantity - %d]",
                            storeId, productId, sku, quantity));
        }

        Optional<Product> product = productCatalogService.getProductByProductId(productId, Product.class);
        if(product.isPresent()) {
            filterMap.clear();
            filterMap.put("_id", product.get().getId());
            valueMap.clear();
            valueMap.put("qty", quantity);
            updateMap.clear();
            updateMap.put("inc", valueMap);
            if(!mongoDBService.updateOne("ecommerce", "product",
                    Product.class, filterMap, updateMap, new HashMap<>())) {
                logger.error("unable to update product quantity: " +
                        String.format("[id - %s], [productId - %s], [sku - %s], [quantity - %d]",
                                product.get().getId(), productId, sku, quantity));
            }
        } else {
            logger.error("fail to query product collection by productId: " + productId);
        }
    }
}
