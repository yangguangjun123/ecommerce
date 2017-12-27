package org.myproject.ecommerce.services;

import static org.myproject.ecommerce.domain.StoreInventory.StoreVariation;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import org.myproject.ecommerce.domain.Product;
import org.myproject.ecommerce.domain.ProductVariation;
import org.myproject.ecommerce.domain.StoreInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;

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
            List<StoreVariation> storeVariations =
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
        Objects.requireNonNull(storeId);
        Objects.requireNonNull(productId);
        Objects.requireNonNull(sku);
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
        Objects.requireNonNull(storeId);
        Objects.requireNonNull(productId);
        Objects.requireNonNull(sku);
        if(quantity < 0) {
            logger.error("quantity cannot be negative: " + quantity);
            return;
        }
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

    public int getQuantityOfAllProductVariations(String productId) {
        List<Map<String, Object>> pipeline = new ArrayList<>();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", productId);
        Map<String, Object> match = new HashMap<>();
        match.put("$match", filterMap);
        pipeline.add(match);
        Map<String, Object> unwind = new HashMap<>();
        unwind.put("$unwind", "$vars");
        pipeline.add(unwind);
        Map<String, Object> group = new HashMap<>();
        List<Object> groupParameters = new ArrayList<>();
        groupParameters.add("result");
        Map<String, Object> groupOperators = new HashMap<>();
        List<String> sumParameters = new ArrayList<>();
        sumParameters.add("count");
        sumParameters.add("$vars.quantity");
        groupOperators.put("$sum", sumParameters);
        groupParameters.add(groupOperators);
        group.put("$group", groupParameters);
        pipeline.add(group);
        List<String> resultFields = new ArrayList<>();
        resultFields.add("_id");
        resultFields.add("count");
        Map<String, Object> resultMap = mongoDBService.processAggregatePipeline("ecommerce",
                "store_inventory", pipeline, resultFields);
        return (int) resultMap.get("count");
    }

    public List<StoreInventory> getProductStoreInventory(String productId, String sku, double[] coordinates,
                                                         double maxDistance, int numberOfResultsReturned) {
        List<StoreInventory> result = getProductStoreInventory(productId, sku, coordinates, maxDistance,
                numberOfResultsReturned, 0);
        result.stream()
              .forEach(r -> r.setLocation(Arrays.asList(coordinates[0], coordinates[1])));
        return result;
    }

    public List<StoreInventory> getProductStoreInventory(String productId, String sku, double[] coordinates,
                                                            double maxDistance, int numberOfResultsReturned,
                                                                     int quantity) {
        Point refPoint = new Point(new Position(coordinates[0], coordinates[1]));
        Map<String, Object> geoQueryMap = new HashMap<>();
        geoQueryMap.put("distanceFieldName", "location");
        geoQueryMap.put("geometry", refPoint);
        geoQueryMap.put("maxDistance", maxDistance);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("productId", productId);
        filterMap.put("vars.sku", sku);

        List<Map<String, Object>> aggregatePipelineList = new ArrayList<>();
        Map<String, Object> unwindMap = new HashMap<>();
        unwindMap.put("$unwind", "$vars");
        aggregatePipelineList.add(unwindMap);
        Map<String, Object> matchMap = new HashMap<>();
        Map<String, Object> quantityFilterMap = new HashMap<>();
        quantityFilterMap.put("vars.quantity", 0);
        Map<String, Object> skuMap = new HashMap<>();
        skuMap.put("vars.sku", sku);
        Map<String, Object> combinedMap = new HashMap<>();
        combinedMap.put("$eq", skuMap);
        combinedMap.put("$gt", quantityFilterMap);
        matchMap.put("$match", combinedMap);
        aggregatePipelineList.add(matchMap);
        Map<String, Object> limitMap = new HashMap<>();
        limitMap.put("$limit", numberOfResultsReturned);
        aggregatePipelineList.add(limitMap);

        List<StoreInventory> result = mongoDBService.performGeoQuery("ecommerce", "store_inventory",
                StoreInventory.class, geoQueryMap, filterMap, aggregatePipelineList);
        result.stream()
                .forEach(r -> r.setLocation(Arrays.asList(coordinates[0], coordinates[1])));
        return result;
    }
}