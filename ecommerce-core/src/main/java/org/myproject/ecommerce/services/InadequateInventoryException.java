package org.myproject.ecommerce.services;

public class InadequateInventoryException extends EcommerceException {

    public InadequateInventoryException(String message) {
        super(message);
    }
}
