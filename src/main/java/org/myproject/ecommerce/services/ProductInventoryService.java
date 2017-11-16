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
    public ProductInventoryService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initialise() {
        deleteAllCarts("ecommerce");
        populateCarts();
    }

    public void deleteAllCarts(String database) {
        mongoDBService.delete(database, "cart");
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

        // Make sure the cart is still active and addOne the line item
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

        HashMap<String, Object> quantityMap = new HashMap<>();
        quantityMap.put("qty", quantity);
        valueMap.clear();
        valueMap.put("carted",new Product.CartedItem(quantity, cartId, now));
        Map<String, Object> combined = new HashMap<>();
        combined.put("addOrRemove", valueMap);
        combined.put("decrease", quantityMap);
        result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                                            filterMap, combined);
        if(!result) {
            // roll back our cart update
            filterMap.clear();
            filterMap.put("_id", cartId);
            valueMap.clear();
            valueMap.put("items.sku", sku);
            combined.clear();
            combined.put("pull", valueMap);
            result = mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                    filterMap, combined);
            if(!result) {
                throw new EcommerceException("roll back failed: " + ", cart_id: " +
                        cartId + ", details.sku: " + sku);
            }

            throw new InadequateInventoryException("Inadquate Inventory: " + "cart id: " +
                    cartId + ", quantity: " + quantity);
        }
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
        valueUpdateMap.put("carted", new ArrayList<>());
        Map<String, Object> removed = new HashMap<>();
        removed.put("addOrRemove", valueUpdateMap);
        mongoDBService.removeOne("ecommerce", "product", AudioAlbum.class,
                filterMap, removed);
        Product.CartedItem cartedItem = new Product.CartedItem(1,42, lastModified);
        valueUpdateMap.clear();
        valueUpdateMap.put("carted", Arrays.asList(cartedItem));
        Map<String, Object> added = new HashMap<>();
        added.put("addOrRemove", valueUpdateMap);
        mongoDBService.addOne("ecommerce", "product", AudioAlbum.class,
                filterMap, added);
    }
}