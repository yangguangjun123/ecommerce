package org.myproject.ecommerce.domain;

import org.bson.codecs.pojo.annotations.BsonProperty;

public class ShoppingCartItem {
    private String sku;

    @BsonProperty(value = "qty")
    private int quantity;

    @BsonProperty(value = "item_details")
    private ShoppingCartItemDetails itemDetails;

    public ShoppingCartItem() {
    }

    public ShoppingCartItem(String sku, int quantity, ShoppingCartItemDetails details) {
        this.sku = sku;
        this.quantity = quantity;
        this.itemDetails = details;
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

    public ShoppingCartItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ShoppingCartItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingCartItem that = (ShoppingCartItem) o;

        if (quantity != that.quantity) return false;
        if (sku != null ? !sku.equals(that.sku) : that.sku != null) return false;
        return itemDetails != null ? itemDetails.equals(that.itemDetails) : that.itemDetails == null;
    }

    @Override
    public int hashCode() {
        int result = sku != null ? sku.hashCode() : 0;
        result = 31 * result + quantity;
        result = 31 * result + (itemDetails != null ? itemDetails.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShoppingCartItem{" +
                "sku='" + sku + '\'' +
                ", quantity=" + quantity +
                ", itemDetails=" + itemDetails +
                '}';
    }

}