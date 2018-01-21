package org.myproject.ecommerce.core.domain;

import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;
import java.util.List;

public class ShoppingCart {
    @BsonId
    private int cartId;

    @BsonProperty(value = "last_modified")
    private Date lastModified;

    private String status;
    List<ShoppingCartItem> items;

    public ShoppingCart() {
    }

    public ShoppingCart(int cartId, Date lastModified, String status, List<ShoppingCartItem> items) {
        this.cartId = cartId;
        this.lastModified = lastModified;
        this.status = status;
        this.items = items;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<ShoppingCartItem> getItems() {
        return items;
    }

    public void setItems(List<ShoppingCartItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingCart that = (ShoppingCart) o;

        if (cartId != that.cartId) return false;
        if (lastModified != null ? !lastModified.equals(that.lastModified) : that.lastModified != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = cartId;
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "cartId=" + cartId +
                ", lastModified=" + lastModified +
                ", status='" + status + '\'' +
                ", items=" + items +
                '}';
    }

}