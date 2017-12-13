package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ProductVariation {
    @BsonId
    private String sku;

    private String productId;
    private List<String> attributes = new ArrayList<>();

    public ProductVariation() {
    }

    public ProductVariation(String sku, String productId) {
        this.sku = sku;
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attribute) {
        attributes.add(attribute);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductVariation that = (ProductVariation) o;

        if (sku != null ? !sku.equals(that.sku) : that.sku != null) return false;
        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        return attributes != null ? attributes.equals(that.attributes) : that.attributes == null;
    }

    @Override
    public int hashCode() {
        int result = sku != null ? sku.hashCode() : 0;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ProductVariation{" +
                "sku='" + sku + '\'' +
                ", productId=" + productId +
                ", attributes=" + attributes +
                '}';
    }
}
