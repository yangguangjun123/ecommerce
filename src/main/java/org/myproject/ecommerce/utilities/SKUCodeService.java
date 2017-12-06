package org.myproject.ecommerce.utilities;

import org.myproject.ecommerce.domain.SKUCode;
import org.myproject.ecommerce.services.MongoDBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

// SKU - Stock Keeping Unit
@Service
public class SKUCodeService {
    @Autowired
    private MongoDBService mongoDBService;

    private long productSku = 0L;
    private long productvariationSku = 0L;
    private SKUCode skuCode;

    @PostConstruct
    public void initialise() {
        skuCode = mongoDBService.readOne("ecommerce", "productSkuCode",
                SKUCode.class, new HashMap<>()).get();
        productSku = Long.parseLong(skuCode.getProductSkuCode(), 16);
        productvariationSku = Long.parseLong(skuCode.getProductSkuCode());
    }

    public void resetSKU() {
        skuCode = mongoDBService.readOne("ecommerce", "productSkuCode",
                SKUCode.class, new HashMap<>()).get();
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCode.getId());
        skuCode.setProductSkuCode("00e8da9d");
        skuCode.setProductVariationSkuCode("93284847362823");
        mongoDBService.replaceOne("ecommerce", "productSkuCode", SKUCode.class, filterMap, skuCode);
        productSku = Long.parseLong("00e8da9d", 16);
    }

    public String createProductSKUCode() {
        productSku++;
        String newCode = String.format("%08x", productSku);
        skuCode.setProductSkuCode(newCode);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCode.getId());
        mongoDBService.replaceOne("ecommerce", "productSkuCode", SKUCode.class, filterMap, skuCode);
        return newCode;
    }

    public String createProductVariationSKUCode() {
        productvariationSku++;
        String newCode = String.format("%08x", productvariationSku);
        skuCode.setProductVariationSkuCode(newCode);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", skuCode.getId());
        mongoDBService.replaceOne("ecommerce", "productSkuCode", SKUCode.class, filterMap, skuCode);
        return newCode;
    }
}
