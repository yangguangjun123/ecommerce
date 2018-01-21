package org.myproject.ecommerce.core.domain;

public class ShoppingCartItemDetails {

    private String details;

    public ShoppingCartItemDetails() {
    }

    public ShoppingCartItemDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingCartItemDetails that = (ShoppingCartItemDetails) o;

        return details != null ? details.equals(that.details) : that.details == null;
    }

    @Override
    public int hashCode() {
        return details != null ? details.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ItemDetails{}";
    }
}