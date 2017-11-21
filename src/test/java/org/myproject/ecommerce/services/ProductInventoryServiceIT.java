package org.myproject.ecommerce.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductInventoryServiceIT.CustomConfiguration.class})
public class ProductInventoryServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
        Method resetMethod =  ProductInventoryService.class.getDeclaredMethod(
                "initialise",null); // methodName,parameters
        resetMethod.setAccessible(true);
        resetMethod.invoke(productInventoryService);

        ProductInventoryService.class.getDeclaredMethod(
                "populateCarts",null); // methodName,parameters
        resetMethod.setAccessible(true);
        resetMethod.invoke(productInventoryService);
    }

    @Test
    public void shouldAddItemToCart() throws EcommerceException {
        // when
        int cartId = 42;
        String sku = "00e8da9b";
        int quantity = 1;
        ShoppingCartItemDetails details = new ShoppingCartItemDetails("add a new item to cart");
        AudioAlbum beforeAdded = productCatalogService.readBySku(sku, AudioAlbum.class);

        // given
        productInventoryService.addItemToCart(cartId, sku, quantity, details);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(cartId, cart.getCartId());
        assertEquals(sku, cart.getItems().get(cart.getItems().size() - 1).getSku());
        assertEquals(quantity, cart.getItems().get(cart.getItems().size() - 1).getQuantity());
        assertEquals(details, cart.getItems().get(cart.getItems().size() - 1).getItemDetails());
        AudioAlbum afterAdded = productCatalogService.readBySku(sku, AudioAlbum.class);
        assertEquals(beforeAdded.getQuantity() - 1, afterAdded.getQuantity());
    }

    @Test
    public void shouldModifyCartQuantity() throws EcommerceException {
        // when
        int cartId = 42;
        String sku = "00e8da9b";
        int oldQuantity = 1;
        int newQuantity = 2;
        AudioAlbum beforeUpdated = productCatalogService.readBySku(sku, AudioAlbum.class);

        // when
        productInventoryService.updateCartQuantity(cartId, sku,oldQuantity, newQuantity);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(cartId, cart.getCartId());
        assertEquals(sku, cart.getItems().get(0).getSku());
        assertEquals(newQuantity, cart.getItems().get(0).getQuantity());
        AudioAlbum afterUpdated = productCatalogService.readBySku(sku, AudioAlbum.class);
        assertEquals(beforeUpdated.getQuantity() + Math.negateExact(newQuantity - oldQuantity),
                afterUpdated.getQuantity());
    }

    @Test
    public void shouldProcessCheckout() throws CartInactiveException {
        // given
        int cartId = 42;

        // when
        productInventoryService.processCheckout(cartId);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(ShoppingCartStatus.COMPLETE.toString(), cart.getStatus());
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("carted.cart_id", cartId);
        List<Product> cartedProducts = mongoDBService.readAllByFiltering("ecommerce",
                "product", Product.class, filterMap);
        assertTrue(cartedProducts.size() == 0);
    }

    @Test
    public void shouldProcessExpiringCarts() {
        // given
        Instant instantCarted = Instant.parse("2012-03-09T20:55:36Z");
        Instant instantNow = Instant.now();
        long timeout = Duration.between(instantCarted, instantNow).getSeconds() - 1;
        int expectedQty = 17;

        // when
        productInventoryService.processExpiringCarts(timeout);

        // verify
        int cartId = 42;
        String sku = "00e8da9b";
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(ShoppingCartStatus.EXPIRED.toString(), cart.getStatus());
        AudioAlbum product = productCatalogService.readBySku(sku, AudioAlbum.class);
        assertEquals(expectedQty, product.getQuantity());
    }

    @Configuration
    static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        ProductInventoryService productInventoryService() {
            return new ProductInventoryService(mongoDBService);
        }

        @Bean
        ProductCatalogService productCatalogService() {
            return new ProductCatalogService(mongoDBService);
        }

        @Bean
        PaymentService paymentService() {
            return new PaymentService();
        }

    }

}
