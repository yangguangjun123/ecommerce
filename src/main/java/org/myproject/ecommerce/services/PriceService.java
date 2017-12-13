package org.myproject.ecommerce.services;

import org.myproject.ecommerce.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class PriceService {
    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Autowired
    private StoreService storeService;

    @PostConstruct
    public void initialise() {
        if(getNumberOfPrices() != 14014) {
            deleteAllPrices();

            Optional<Product> product = productCatalogService.getProductByProductId("30671", Product.class);
            createPrice(product.get());

            Optional<ProductVariation> productVariation = productCatalogService.getProductVariationBySku(
                    "93284847362823", ProductVariation.class);
            createPrice(productVariation.get());

            Film matrix = productCatalogService.getProductBySku("0ab42f88", Film.class).get();
            createPrice(matrix);
            List<ProductVariation> productVariations = productCatalogService.getAllProductVariationsByProductId(
                    matrix.getProductId(), ProductVariation.class);
            productVariations.stream()
                    .limit(2)
                    .forEach(p -> createPrice(p));

            AudioAlbum loveSupreme = productCatalogService.getProductBySku("00e8da9b", AudioAlbum.class).get();
            createPrice(loveSupreme);
            productVariations = productCatalogService.getAllProductVariationsByProductId(
                    loveSupreme.getProductId(), ProductVariation.class);
            productVariations.stream()
                    .limit(2)
                    .forEach(p -> createPrice(p));

            Film anotherMatrix = productCatalogService.getProductBySku("00e8da9c", Film.class).get();
            createPrice(anotherMatrix);
            productVariations = productCatalogService.getAllProductVariationsByProductId(
                    anotherMatrix.getProductId(), ProductVariation.class);
            productVariations.stream()
                    .limit(2)
                    .forEach(p -> createPrice(p));

            AudioAlbum anotherLoveSupreme = productCatalogService.getProductBySku("00e8da9d", AudioAlbum.class).get();
            createPrice(anotherLoveSupreme);
            productVariations = productCatalogService.getAllProductVariationsByProductId(
                    anotherLoveSupreme.getProductId(), ProductVariation.class);
            productVariations.stream()
                    .limit(2)
                    .forEach(p -> createPrice(p));
        }
    }

    public long getNumberOfPrices() {
        return mongoDBService.count("ecommerce", "prices", Price.class);
    }

    public void deleteAllPrices() {
        mongoDBService.deleteAll("ecommerce", "prices");
    }

    public void createPrice(Product product) {
        storeService.getAllStores()
                .stream()
                .map(s -> new Price(product.getProductId() + "_" + s.getName(),
                        generatePrice(s, product)))
                .forEach(p -> mongoDBService.createOne("ecommerce",
                        "prices", Price.class, p));
    }

    public void createPrice(ProductVariation productVariation) {
        storeService.getAllStores()
                .stream()
                .map(s -> new Price(productVariation.getSku() + "_" + s.getName(),
                        generatePrice(s, productVariation)))
                .forEach(p -> mongoDBService.createOne("ecommerce",
                        "prices", Price.class, p));
    }

    public Optional<Integer> getProductPrice(Product product, Optional<Store> store) {
        Objects.requireNonNull(product);
        if(store.isPresent()) {
            return getPriceById(product.getProductId() + "_" + store.get().getName());
        } else {
            return getPriceById(product.getProductId());
        }
    }

    public Optional<Integer> getProductVariationPrice(ProductVariation productVariation,
                                                      Optional<Store> store) {
        Objects.requireNonNull(productVariation);
        if(store.isPresent()) {
            return getPriceById(productVariation.getSku() + "_" + store.get().getName());
        } else {
            return getPriceById(productVariation.getSku());
        }
    }

    public List<Price> getStorePrices(Product product, Store store) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(store);
        String regex = "^" + product.getProductId() + "_" + store.getName();
        return getPrices(regex);
    }

    public List<Price> getPrices(Product product) {
        Objects.requireNonNull(product);
        String regex = "^" + product.getProductId();
        return getPrices(regex);
    }

    public List<Price> getPrices(List<String> priceIds) {
        Objects.requireNonNull(priceIds);
        Map<String, Object> filterMap = new HashMap<>();
        Map<String, Object> fieldValueMap = new HashMap<>();
        fieldValueMap.put("_id", priceIds);
        filterMap.put("$in", fieldValueMap);
        return mongoDBService.readAll("ecommerce", "prices", Price.class, filterMap);
    }

    public List<Price> getPrices(ProductVariation productVariation) {
        Objects.requireNonNull(productVariation);
        String regex = "^" + productVariation.getSku();
        return getPrices(regex);
    }

    public List<Price> getStorePrices(ProductVariation productVariation, Store store) {
        Objects.requireNonNull(productVariation);
        Objects.requireNonNull(store);
        String regex = "^" + productVariation.getSku() + "_" + store.getName();
        return getPrices(regex);
    }

    private List<Price> getPrices(String regularExpression) {
        Map<String, Object> filterMap = new HashMap<>();
        Map<String, Object> fieldValueMap = new HashMap<>();
        fieldValueMap.put("_id", Pattern.compile(regularExpression));
        filterMap.put("$regex", fieldValueMap);
        return mongoDBService.readAll("ecommerce", "prices", Price.class, filterMap);
    }

    private Optional<Integer> getPriceById(String priceId) {
        Optional<Price> priceOptional = mongoDBService.readById("ecommerce",
                "prices", Price.class, priceId);
        return priceOptional.map(Price::getPrice);
    }

    private int generatePrice(Store store, Product product) {
        return 1200;
    }


    private int generatePrice(Store store, ProductVariation productVariation) {
        return 1200;
    }
}
