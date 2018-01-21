package org.myproject.ecommerce.core.interfaces;

import org.bson.types.ObjectId;

import javax.annotation.PostConstruct;
import java.util.Optional;

public interface IProductCategoryService {
    @PostConstruct
    void initialise();

    void buildAncesters(ObjectId id, Optional<ObjectId> parentId);

    void buildAncestersFull(ObjectId id, Optional<ObjectId> parentId);

    void updateAncestry(ObjectId id, Optional<ObjectId> parentId);

    void renameCategory(ObjectId id, String newName);

    void deleteAllCategories();
}
