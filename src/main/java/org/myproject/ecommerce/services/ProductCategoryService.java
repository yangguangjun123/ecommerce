package org.myproject.ecommerce.services;

import org.bson.types.ObjectId;
import org.myproject.ecommerce.domain.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProductCategoryService {
    @Autowired
    private MongoDBService mongoDBService;

    @PostConstruct
    public void initialise() {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("slug", "swing");
        Optional<ProductCategory> swing = mongoDBService.readOne("ecommerce", "categories",
                ProductCategory.class, filterMap);
        if(!swing.isPresent()) {
            filterMap.clear();
            filterMap.put("slug", "ragtime");
            ProductCategory ragtime = mongoDBService.readOne("ecommerce", "categories",
                    ProductCategory.class, filterMap).get();
            ProductCategory created = new ProductCategory("Swing", ragtime.getId(), "swing");
            created.setId(new ObjectId());
            mongoDBService.createOne("ecommerce", "categories",
                    ProductCategory.class, created);
            buildAncesters(created.getId(), ragtime.getId());
        }
    }

    public void buildAncesters(ObjectId id, ObjectId parentId) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", parentId);
        ProductCategory parentCategory = mongoDBService.readOne("ecommerce", "categories",
                ProductCategory.class, filterMap).get();
        List<ProductCategory.Ancestor> ancestorList = new ArrayList<>();
        ancestorList.add(new ProductCategory.Ancestor(parentCategory.getId(),
                parentCategory.getSlug(), parentCategory.getName()));
        if(!Objects.isNull(parentCategory.getAncestors()) && parentCategory.getAncestors().size() > 0) {
            ancestorList.addAll(ancestorList.subList(0, ancestorList.size() - 1));
        }

        filterMap.clear();
        filterMap.put("_id", id);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("ancestors", ancestorList);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());
    }

}
