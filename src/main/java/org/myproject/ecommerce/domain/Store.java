package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;

import java.util.List;

public class Store {
    @BsonId
    private String id;

    private String name;
    private List<String> storeGroups;

    public Store() {
    }

    public Store(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStoreGroups() {
        return storeGroups;
    }

    public void setStoreGroups(List<String> storeGroups) {
        this.storeGroups = storeGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Store store = (Store) o;

        if (id != null ? !id.equals(store.id) : store.id != null) return false;
        if (name != null ? !name.equals(store.name) : store.name != null) return false;
        return storeGroups != null ? storeGroups.equals(store.storeGroups) : store.storeGroups == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (storeGroups != null ? storeGroups.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", storeGroups=" + storeGroups +
                '}';
    }
}
