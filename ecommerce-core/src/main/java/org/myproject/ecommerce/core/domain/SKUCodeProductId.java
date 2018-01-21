package org.myproject.ecommerce.core.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class SKUCodeProductId {
    @BsonId
    private ObjectId id;

    private String productSkuCode;
    private String productVariationSkuCode;
    private String productId;

    public SKUCodeProductId() {
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getProductSkuCode() {
        return productSkuCode;
    }

    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    public String getProductVariationSkuCode() {
        return productVariationSkuCode;
    }

    public void setProductVariationSkuCode(String productVariationSkuCode) {
        this.productVariationSkuCode = productVariationSkuCode;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SKUCodeProductId that = (SKUCodeProductId) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (productSkuCode != null ? !productSkuCode.equals(that.productSkuCode) : that.productSkuCode != null)
            return false;
        if (productVariationSkuCode != null ? !productVariationSkuCode.equals(that.productVariationSkuCode) : that.productVariationSkuCode != null)
            return false;
        return productId != null ? productId.equals(that.productId) : that.productId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productSkuCode != null ? productSkuCode.hashCode() : 0);
        result = 31 * result + (productVariationSkuCode != null ? productVariationSkuCode.hashCode() : 0);
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SKUCodeProductId{" +
                "id=" + id +
                ", productSkuCode='" + productSkuCode + '\'' +
                ", productVariationSkuCode='" + productVariationSkuCode + '\'' +
                ", productId='" + productId + '\'' +
                '}';
    }
}
