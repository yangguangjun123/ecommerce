package org.myproject.ecommerce.utilities;

import org.bson.types.ObjectId;
import org.myproject.ecommerce.domain.SKUCodeProductId;
import org.myproject.ecommerce.services.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// SKU - Stock Keeping Unit
@Service
public class SKUCodeProductIdGenerator {
    @Autowired
    private MongoDBService mongoDBService;

    private long productSku = 0L;
    private long productvariationSku = 0L;
    private long productId = 0L;
    private SKUCodeProductId skuCodeProductId;

    @PostConstruct
    public void initialise() {
        Optional<SKUCodeProductId> result = mongoDBService.readOne("ecommerce", "skucode_productid",
                SKUCodeProductId.class, new HashMap<>());
        if(result.isPresent()) {
            skuCodeProductId = result.get();
            productSku = Long.parseLong(skuCodeProductId.getProductSkuCode(), 16);
            productvariationSku = Long.parseLong(skuCodeProductId.getProductSkuCode(),16);
            productId = Long.parseLong(skuCodeProductId.getProductId());
        } else {
            reset();
        }
    }

    public void reset() {
        mongoDBService.deleteAll("ecommerce", "skucode_productid");
        skuCodeProductId = new SKUCodeProductId();
        skuCodeProductId.setId(new ObjectId());
        skuCodeProductId.setProductSkuCode("00e8da9e");
        skuCodeProductId.setProductVariationSkuCode("93284847362823");
        skuCodeProductId.setProductId("30671");
        mongoDBService.createOne("ecommerce", "skucode_productid",
                SKUCodeProductId.class, skuCodeProductId);
        productSku = 0x00e8da9e;
        productvariationSku = 93284847362823L;
        productId = 30671L;
    }

    public String createProductSKUCode() {
        String skuCode = String.format("%08x", productSku);
        productSku++;
        skuCodeProductId.setProductSkuCode(String.format("%08x", productSku));
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCodeProductId.getId());
        mongoDBService.replaceOne("ecommerce", "skucode_productid", SKUCodeProductId.class,
                filterMap, skuCodeProductId);
        return skuCode;
    }

    public String createProductVariationSKUCode() {
        String skuCode = Long.toString(productvariationSku);
        productvariationSku++;
        skuCodeProductId.setProductVariationSkuCode(Long.toString(productvariationSku));
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCodeProductId.getId());
        mongoDBService.replaceOne("ecommerce", "skucode_productid", SKUCodeProductId.class,
                filterMap, skuCodeProductId);
        return skuCode;
    }

    public String createProductId() {
        String code = Long.toString(productId);
        productId++;
        skuCodeProductId.setProductId(Long.toString(productId));
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCodeProductId.getId());
        mongoDBService.replaceOne("ecommerce", "skucode_productid", SKUCodeProductId.class,
                filterMap, skuCodeProductId);
        return code;
    }
}
