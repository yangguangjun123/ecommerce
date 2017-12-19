package org.myproject.ecommerce.domain;

import org.bson.types.ObjectId;

import java.util.List;

public class StoreBuilder {
    private ObjectId id;
    private String storeId;
    private String className;
    private String name;
    private Store.StoreAddress address;
    private List<Double> location;
    private List<String> storeGroups;

    public StoreBuilder setId(ObjectId id) {
        this.id = id;
        return this;
    }

    public StoreBuilder setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public StoreBuilder setClassName(String className) {
        this.className = className;
        return this;
    }

    public StoreBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public StoreBuilder setAddress(Store.StoreAddress address) {
        this.address = address;
        return this;
    }

    public StoreBuilder setLocation(List<Double> location) {
        this.location = location;
        return this;
    }

    public StoreBuilder setStoreGroups(List<String> storeGroups) {
        this.storeGroups = storeGroups;
        return this;
    }

    public Store createStore() {
        return new Store(id, storeId, className, name, address, location, storeGroups);
    }
}