package org.myproject.mongodb.sharding.services;

public class CartInactiveException extends EcommerceException {

    public CartInactiveException(String message) {
        super(message);
    }
}
