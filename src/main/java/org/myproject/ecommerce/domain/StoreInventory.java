package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.ArrayList;
import java.util.List;

public class StoreInventory {
    @BsonId
    private String id;

    private String storeId;
    private List<Double> location;
    private String productId;

    @BsonProperty("vars")
    List<StoreVariation> storeVariations;

    public StoreInventory() {
    }

    public StoreInventory(String id, String storeId, List<Double> location, String productId,
                          List<StoreVariation> storeVariations) {
        this.id = id;
        this.storeId = storeId;
        this.location = location;
        this.productId = productId;
        this.storeVariations = storeVariations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<StoreVariation> getStoreVariations() {
        return storeVariations;
    }

    public void setStoreVariations(List<StoreVariation> storeVariations) {
        this.storeVariations = storeVariations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreInventory that = (StoreInventory) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        return storeVariations != null ? storeVariations.equals(that.storeVariations) : that.storeVariations == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (storeId != null ? storeId.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (storeVariations != null ? storeVariations.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StoreInventory{" +
                "id=" + id +
                ", storeId='" + storeId + '\'' +
                ", location=" + location +
                ", productId='" + productId + '\'' +
                ", storeVariations=" + storeVariations +
                '}';
    }

    public static class StoreVariation {
        private String sku;
        private int quantity;

        public StoreVariation() {
        }

        public StoreVariation(String sku, int quantity) {
            this.sku = sku;
            this.quantity = quantity;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            StoreVariation that = (StoreVariation) o;

            if (quantity != that.quantity) return false;
            return sku != null ? sku.equals(that.sku) : that.sku == null;
        }

        @Override
        public int hashCode() {
            int result = sku != null ? sku.hashCode() : 0;
            result = 31 * result + quantity;
            return result;
        }

        @Override
        public String toString() {
            return "StoreVariation{" +
                    "sku='" + sku + '\'' +
                    ", quantity=" + quantity +
                    '}';
        }
    }
}
