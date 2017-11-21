package org.myproject.ecommerce.services;

import org.myproject.ecommerce.domain.*;
import org.myproject.ecommerce.interfaces.IProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ProductInventoryService implements IProductInventoryService {
    private final MongoDBService mongoDBService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    public ProductInventoryService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initialise() {
        deleteAllCarts("ecommerce");
        populateCarts();
        Map<String, Object> queryFilterMap = new HashMap<>();
        Map<String, Object> fieldValueMap = new HashMap<>();
        fieldValueMap.put("sku", "00e8da9b");
        queryFilterMap.put("$eq", fieldValueMap);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("qty", 16);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        boolean result = mongoDBService.updateOne("ecommerce", "product", Product.class,
                queryFilterMap, updateMap, new HashMap<>());
    }

    public void deleteAllCarts(String database) {
        mongoDBService.delete(database, "cart");

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("sku", "00e8da9b");
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("qty", 16);
        valueMap.put("carted", Optional.empty());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("addOrRemove", valueMap);
        boolean result = mongoDBService.updateOne("ecommerce", "product", Product.class,
                filterMap, updateMap, new HashMap<>());
        if(!result) {
            throw new RuntimeException("cannot remove all carts");
        }
    }

    public ShoppingCart createNewShppoingCart(int cartId, List<ShoppingCartItem> items, ZoneOffset zoneOffset) {
        Objects.requireNonNull(items, "shopping items cannot be null");
        ZoneOffset shppingCartZoneOffset = zoneOffset == null ? ZoneOffset.UTC : zoneOffset;
        Date lastModified = Date.from(LocalDateTime.now().atOffset(shppingCartZoneOffset).toInstant());
        ShoppingCart shoppingCart = new ShoppingCart(cartId, lastModified,
                ShoppingCartStatus.ACTIVE.toString(), items);
        mongoDBService.write("test", "cart", ShoppingCart.class, shoppingCart);
        return shoppingCart;
    }


    public void addItemToCart(int cartId, String sku, int quantity, ShoppingCartItemDetails details)
            throws EcommerceException {
        Date now = new Date();

        // Make sure the cart is still active and add the line item
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", cartId);
        filterMap.put("status", ShoppingCartStatus.ACTIVE.toString());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("last_modified", now);
        valueMap.put("items", Arrays.asList(new ShoppingCartItem(sku, quantity, details)));
        Map<String, Object> added = new HashMap<>();
        added.put("addOrRemove", valueMap);
        boolean result = mongoDBService.addOne("ecommerce", "cart", ShoppingCart.class,
                filterMap, added);
        if(!result) {
            throw new CartInactiveException("Cart Inactive: " + cartId);
        }

        // Update inventory
        HashMap<String, Object> quantityQueryMap = new HashMap<>();
        quantityQueryMap.put("qty", quantity);
        filterMap.clear();
        filterMap.put("sku", sku);
        filterMap.put("$gte", quantityQueryMap);
        HashMap<String, Object> quantityUpdateMap = new HashMap<>();
        quantityUpdateMap.put("qty", Math.negateExact(quantity));
        valueMap.clear();
        valueMap.put("carted", Arrays.asList(new Product.CartedItem(quantity, cartId, now)));
        Map<String, Object> combined = new HashMap<>();
        combined.put("addOrRemove", valueMap);
        combined.put("inc", quantityUpdateMap);
        result = mongoDBService.updateOne("ecommerce", "product", Product.class,
                                            filterMap, combined, new HashMap<>());
        if(!result) {
            // roll back our cart update and return item to product
            filterMap.clear();
            filterMap.put("_id", cartId);
            valueMap.clear();
            valueMap.put("items", new ArrayList<>());
            combined.clear();
            combined.put("addOrRemove", valueMap);
            result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                    filterMap, combined, new HashMap<>());
            if(!result) {
                throw new EcommerceException("roll back failed: " + ", cart_id: " +
                        cartId + ", details.sku: " + sku);
            }

            throw new InadequateInventoryException("Inadquate Inventory: " + "cart id: " +
                    cartId + ", quantity: " + quantity);
        }
    }

    public void updateCartQuantity(int cartId, String sku, int oldQty, int newQty) throws EcommerceException {
        Date now = new Date();
        int deltaQty = newQty - oldQty;

        // Make sure the cart is still active and add the line item
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", cartId);
        filterMap.put("status", ShoppingCartStatus.ACTIVE.toString());
        filterMap.put("items.sku", sku);
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("last_modified", now);
        valueMap.put("items.$.qty", newQty);
        Map<String, Object> updated = new HashMap<>();
        updated.put("addOrRemove", valueMap);
        boolean result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                filterMap, updated, new HashMap<>());
        if(!result) {
            throw new CartInactiveException("Cart Inactive: " + cartId);
        }

        // Update the inventory
        HashMap<String, Object> quantityQueryMap = new HashMap<>();
        quantityQueryMap.put("qty", deltaQty);
        filterMap.clear();
        filterMap.put("sku", sku);
        filterMap.put("carted.cart_id", cartId);
        filterMap.put("$gte", quantityQueryMap);
        HashMap<String, Object> quantityUpdateMap = new HashMap<>();
        quantityUpdateMap.put("qty", Math.negateExact(deltaQty));
        valueMap.clear();
        valueMap.put("carted.$.qty", newQty);
        valueMap.put("carted.$.timestamp", now);
        Map<String, Object> combined = new HashMap<>();
        combined.put("addOrRemove", valueMap);
        combined.put("inc", quantityUpdateMap);
        Map<String, Object> updateOptions = new HashMap<>();
        updateOptions.put("writeConcern", "W1");
        result = mongoDBService.updateOne("ecommerce", "product", Product.class,
                filterMap, combined, updateOptions);
        if(!result) {
            // roll back our cart update
            filterMap.clear();
            filterMap.put("_id", cartId);
            filterMap.put("items.sku", sku);
            valueMap.clear();
            valueMap.put("items.$.qty", oldQty);
            result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                    filterMap, updated, new HashMap<>());
            if(!result) {
                throw new EcommerceException("Unable to roll back changes for cart: " + cartId);
            }

            throw new InadequateInventoryException("Inadquate Inventory: " + "cart id: " +
                    cartId + ", old quantity: " + oldQty + ", new quantity: " + newQty);
        }
    }

    public void processCheckout(int cartId) throws CartInactiveException {
        Date now = new Date();

        // Make sure the cart is still active and add the line item
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", cartId);
        filterMap.put("status", ShoppingCartStatus.ACTIVE.toString());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("last_modified", now);
        valueMap.put("status", ShoppingCartStatus.PENDING.toString());
        Map<String, Object> updated = new HashMap<>();
        updated.put("addOrRemove", valueMap);
        boolean result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                filterMap, updated, new HashMap<>());
        if(!result) {
            throw new CartInactiveException("Cart Inactive: " + cartId);
        }

        // Validate payment details; collect payment
        try {
            ShoppingCart cart = getCartByCartId(cartId);
            paymentService.collectPayment(cart);

            filterMap.clear();
            filterMap.put("_id", cartId);
            valueMap.clear();
            valueMap.put("status", ShoppingCartStatus.COMPLETE.toString());
            updated.clear();
            updated.put("addOrRemove", valueMap);
            result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                    filterMap, updated, new HashMap<>());
            if(!result) {
                throw new EcommerceException("Cart status update failed, cart id: " + cartId);
            }

            filterMap.clear();
            filterMap.put("carted.cart_id", cartId);
            valueMap.clear();
            valueMap.put("carted.cart_id", cartId);
            Map<String, Object> combined = new HashMap<>();
            combined.put("pull", valueMap);
            result = mongoDBService.updateMany("ecommerce", "product",
                    filterMap, combined);

            if(!result) {
                throw new EcommerceException("Cart status update failed, cart id: " + cartId);
            }

        } catch(EcommerceException e) {
            filterMap.clear();
            filterMap.put("_id", cartId);
            valueMap.clear();
            valueMap.put("status", ShoppingCartStatus.ACTIVE.toString());
            updated.clear();
            updated.put("addOrRemove", valueMap);
            result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                    filterMap, updated, new HashMap<>());
            if(!result) {
                throw new RuntimeException("Rollback cart status update failed, cart id: " + cartId);
            }
        }
    }

    public void processExpiringCarts(long timeout) {
        Date threshold = Date.from(Instant.now().minusSeconds(timeout));

        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("status", ShoppingCartStatus.ACTIVE.toString());
        Map<String, Object> lastModifiedMapFilter = new HashMap<>();
        lastModifiedMapFilter.put("last_modified", threshold);
        filterMap.put("$lt", lastModifiedMapFilter);
        Map<String, Object> updated = new HashMap<>();
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", ShoppingCartStatus.EXPIRING.toString());
        updated.put("addOrRemove", statusUpdate);
        mongoDBService.updateMany("ecommerce", "cart",
                filterMap, updated);

        filterMap.clear();
        filterMap.put("status", ShoppingCartStatus.EXPIRING.toString());
        List<ShoppingCart> carts = mongoDBService.readAllByFiltering("ecommerce", "cart",
                ShoppingCart.class, filterMap);
        carts.stream()
             .forEach(cart -> {
                 cart.getItems().stream()
                                .forEach(item -> returnCartItem(cart.getCartId(), item));
             });
    }

    public ShoppingCart getCartByCartId(int cartId) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", cartId);
        return mongoDBService.readOne("ecommerce", "cart", ShoppingCart.class, filterMap);
    }

    private void returnCartItem(int cartId, ShoppingCartItem item) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("sku", item.getSku());
        filterMap.put("carted.cart_id", cartId);
        filterMap.put("carted.qty", item.getQuantity());
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("carted.cart_id", cartId);
        Map<String, Object> quantityUpdateMap = new HashMap<>();
        quantityUpdateMap.put("qty", item.getQuantity());
        Map<String, Object> combined = new HashMap<>();
        combined.put("inc", quantityUpdateMap);
        combined.put("pull", valueMap);
        mongoDBService.updateOne("ecommerce", "product", Product.class,
                filterMap, combined, new HashMap<>());
        filterMap.clear();
        filterMap.put("_id", cartId);
        combined.clear();
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", ShoppingCartStatus.EXPIRED.toString());
        combined.put("addOrRemove", statusUpdate);
        mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                filterMap, combined, new HashMap<>());
    }

    private void populateCarts() {
        Date lastModified = Date.from(Instant.parse("2012-03-09T20:55:36Z"));
        ShoppingCart cart = new ShoppingCart(42, lastModified, ShoppingCartStatus.ACTIVE.toString(),
                Arrays.asList(new ShoppingCartItem("00e8da9b", 1,
                        new ShoppingCartItemDetails("some details")),
                        new ShoppingCartItem("0ab42f88", 4,
                        new ShoppingCartItemDetails("some details"))));
        mongoDBService.write("ecommerce", "cart", ShoppingCart.class, cart);
        Map<String, Object> filterMap= new HashMap<>();
        filterMap.put("sku", "00e8da9b");
        Map<String, Object> valueUpdateMap = new HashMap<>();
        Product.CartedItem cartedItem = new Product.CartedItem(1,42, lastModified);
        valueUpdateMap.clear();
        valueUpdateMap.put("carted", Arrays.asList(cartedItem));
        Map<String, Object> added = new HashMap<>();
        added.put("addOrRemove", valueUpdateMap);
        mongoDBService.addOne("ecommerce", "product", AudioAlbum.class,
                filterMap, added);
    }
}