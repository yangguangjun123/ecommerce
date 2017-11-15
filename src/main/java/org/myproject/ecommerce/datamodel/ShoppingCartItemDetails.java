package org.myproject.ecommerce.datamodel;

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
    public String toString() {
        return "ItemDetails{}";
    }
}