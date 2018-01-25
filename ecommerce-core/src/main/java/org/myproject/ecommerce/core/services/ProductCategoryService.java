package org.myproject.ecommerce.core.services;

import org.bson.types.ObjectId;
import org.myproject.ecommerce.core.domain.ProductCategory;
import org.myproject.ecommerce.core.interfaces.IProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProductCategoryService implements IProductCategoryService {
    private final MongoDBService mongoDBService;

    @Autowired
    public ProductCategoryService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @Override
    @PostConstruct
    public void initialise() {
        deleteAllCategories();
        populateCategories();
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
            created.setParent(new ObjectId("4f5ec858eb03303a11000000"));
            mongoDBService.createOne("ecommerce", "categories",
                    ProductCategory.class, created);
            buildAncesters(created.getId(), Optional.of(ragtime.getId()));
        }
    }

    @Override
    public void buildAncesters(ObjectId id, Optional<ObjectId> parentId) {
        List<ProductCategory.Ancestor> ancestorList = new ArrayList<>();
        if(parentId.isPresent()) {
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("_id", parentId.get());
            ProductCategory parentCategory = mongoDBService.readOne("ecommerce", "categories",
                    ProductCategory.class, filterMap).get();
            ancestorList.add(new ProductCategory.Ancestor(parentCategory.getId(),
                    parentCategory.getSlug(), parentCategory.getName()));
            if(!Objects.isNull(parentCategory.getAncestors()) && parentCategory.getAncestors().size() > 0) {
                ancestorList.addAll(parentCategory.getAncestors());
            }
        }

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", id);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("ancestors", Optional.empty());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());

        filterMap.clear();
        filterMap.put("_id", id);
        valueMap.clear();
        valueMap.put("ancestors", ancestorList);
        updateMap.clear();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());
    }

    @Override
    public void buildAncestersFull(ObjectId id, Optional<ObjectId> parentId) {
        Objects.requireNonNull(id, "id cannot be null");
        List<ProductCategory.Ancestor> ancestorList = new ArrayList<>();
        if(parentId.isPresent()) {
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("_id", parentId.get());
            ProductCategory parentCategory = mongoDBService.readOne("ecommerce", "categories",
                    ProductCategory.class, filterMap).get();
            ancestorList.add(new ProductCategory.Ancestor(parentCategory.getId(), parentCategory.getSlug(),
                    parentCategory.getName()));
            if(!Objects.isNull(parentCategory.getAncestors()) && parentCategory.getAncestors().size() > 0) {
                ancestorList.addAll(parentCategory.getAncestors());
            }
        }
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", id);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("ancestors", Optional.empty());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());

        filterMap.clear();
        filterMap.put("_id", id);
        valueMap.clear();
        valueMap.put("ancestors", ancestorList);
        updateMap.clear();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());
    }

    @Override
    public void updateAncestry(ObjectId id, Optional<ObjectId> parentId) {
        Objects.requireNonNull(id, "id cannot be null");
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", id);
        Map<String, Object> valueMap = new HashMap<>();
        if(parentId.isPresent()) {
            valueMap.put("parent", parentId.get());
        } else {
            valueMap.put("parent", parentId);
        }
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());

        buildAncesters(id, parentId);

        filterMap.clear();
        filterMap.put("ancestors._id", id);
        List<ProductCategory>  descendants  = mongoDBService.readAll("ecommerce",
                "categories", ProductCategory.class, filterMap);
        descendants .stream()
                  .forEach(c -> buildAncestersFull(c.getId(), Optional.of(c.getParent())));
    }

    @Override
    public void renameCategory(ObjectId id, String newName) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(newName);

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", id);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("name", newName);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateOne("ecommerce", "categories", ProductCategory.class,
                filterMap, updateMap, new HashMap<>());

        filterMap.clear();
        filterMap.put("ancestors._id", id);
        valueMap.clear();
        valueMap.put("ancestors.$.name", newName);
        updateMap.clear();
        updateMap.put("addOrRemove", valueMap);
        mongoDBService.updateMany("ecommerce", "categories", filterMap, updateMap);
    }

    @Override
    public void deleteAllCategories() {
        mongoDBService.deleteAll("ecommerce", "categories");
    }

    private void populateCategories() {
        List<ProductCategory> categories = new ArrayList<>();
        ProductCategory ragtime = new ProductCategory("Ragtime",
                new ObjectId("4f5ec858eb03303a11000000"), "ragtime");
        categories.add(ragtime);

        ProductCategory bop = new ProductCategory("Bop",
                new ObjectId("4f5ec858eb03303a11000001"), "bop");
        bop.setParent(ragtime.getId());
        bop.setAncestors(Arrays.asList(new ProductCategory.Ancestor(ragtime.getId(),
                ragtime.getSlug(), ragtime.getName())));
        categories.add(bop);

        ProductCategory modalJazz = new ProductCategory("Modal Jazz",
                new ObjectId("4f5ec858eb03303a11000002"), "modal-jazz");
        modalJazz.setParent(bop.getId());
        modalJazz.setAncestors(Arrays.asList(new ProductCategory.Ancestor(bop.getId(), bop.getSlug(), bop.getName()),
                new ProductCategory.Ancestor(ragtime.getId(), ragtime.getSlug(), ragtime.getName())));
        categories.add(modalJazz);

        ProductCategory hardBop = new ProductCategory("Hard Bop",
                new ObjectId("4f5ec858eb03303a11000003"), "hard-bop");
        hardBop.setParent(bop.getId());
        hardBop.setAncestors(Arrays.asList(new ProductCategory.Ancestor(bop.getId(), bop.getSlug(), bop.getName()),
                new ProductCategory.Ancestor(ragtime.getId(), ragtime.getSlug(), ragtime.getName())));
        categories.add(hardBop);

        ProductCategory freeJazz = new ProductCategory("Free Jazz",
                new ObjectId("4f5ec858eb03303a11000004"), "free-jazz");
        freeJazz.setParent(bop.getId());
        freeJazz.setAncestors(Arrays.asList(new ProductCategory.Ancestor(bop.getId(), bop.getSlug(), bop.getName()),
                new ProductCategory.Ancestor(ragtime.getId(), ragtime.getSlug(), ragtime.getName())));
        categories.add(freeJazz);

        mongoDBService.createAll("ecommerce", "categories",
                ProductCategory.class, categories);
    }
}
