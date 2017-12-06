package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

public class SKUCode {
    @BsonId
    private ObjectId id;

    private String productSkuCode;
    private String productVariationSkuCode;

    public SKUCode() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SKUCode skuCode = (SKUCode) o;

        if (id != null ? !id.equals(skuCode.id) : skuCode.id != null) return false;
        if (productSkuCode != null ? !productSkuCode.equals(skuCode.productSkuCode) : skuCode.productSkuCode != null)
            return false;
        return productVariationSkuCode != null ? productVariationSkuCode.equals(skuCode.productVariationSkuCode) : skuCode.productVariationSkuCode == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (productSkuCode != null ? productSkuCode.hashCode() : 0);
        result = 31 * result + (productVariationSkuCode != null ? productVariationSkuCode.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SKUCode{" +
                "id=" + id +
                ", productSkuCode='" + productSkuCode + '\'' +
                ", productVariationSkuCode='" + productVariationSkuCode + '\'' +
                '}';
    }
}
