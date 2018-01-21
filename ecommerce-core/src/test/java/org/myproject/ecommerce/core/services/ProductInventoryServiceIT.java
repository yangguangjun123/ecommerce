package org.myproject.ecommerce.core.services;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.core.domain.*;
import org.myproject.ecommerce.core.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
    private SKUCodeProductIdGenerator skuCodeGeneratorService;

    @Autowired
    private ProductInventoryService productInventoryService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
        productInventoryService.initialise();
    }

    @Test
    public void shouldAddItemToCart() throws EcommerceException {
        // when
        int cartId = 42;
        String sku = "00e8da9b";
        int quantity = 1;
        ShoppingCartItemDetails details = new ShoppingCartItemDetails("add a new item to cart");
        AudioAlbum beforeAdded = productCatalogService.getProductBySku(sku, AudioAlbum.class).get();

        // given
        productInventoryService.addItemToCart(cartId, sku, quantity, details);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(cartId, cart.getCartId());
        assertEquals(sku, cart.getItems().get(cart.getItems().size() - 1).getSku());
        assertEquals(quantity, cart.getItems().get(cart.getItems().size() - 1).getQuantity());
        assertEquals(details, cart.getItems().get(cart.getItems().size() - 1).getItemDetails());
        AudioAlbum afterAdded = productCatalogService.getProductBySku(sku, AudioAlbum.class).get();
        assertEquals(beforeAdded.getQuantity() - 1, afterAdded.getQuantity());
    }

    @Test
    public void shouldModifyCartQuantity() throws EcommerceException {
        // when
        int cartId = 42;
        String sku = "00e8da9b";
        int oldQuantity = 1;
        int newQuantity = 2;
        AudioAlbum beforeUpdated = productCatalogService.getProductBySku(sku, AudioAlbum.class).get();

        // when
        productInventoryService.updateCartQuantity(cartId, sku,oldQuantity, newQuantity);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(cartId);
        assertEquals(cartId, cart.getCartId());
        assertEquals(sku, cart.getItems().get(0).getSku());
        assertEquals(newQuantity, cart.getItems().get(0).getQuantity());
        AudioAlbum afterUpdated = productCatalogService.getProductBySku(sku, AudioAlbum.class).get();
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
        Assert.assertEquals(ShoppingCartStatus.COMPLETE.toString(), cart.getStatus());
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("carted.cart_id", cartId);
        List<Product> cartedProducts = mongoDBService.readAll("ecommerce",
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
        ShoppingCart cart = productInventoryService.getCartByCartId(42);
        assertEquals(ShoppingCartStatus.EXPIRED.toString(), cart.getStatus());
        AudioAlbum product = productCatalogService.getProductBySku("00e8da9b", AudioAlbum.class).get();
        assertEquals(expectedQty, product.getQuantity());
    }

    @Test
    public void shouldProcessCleanupCarts() {
        // given
        Instant instantCarted = Instant.parse("2012-03-09T20:55:37Z");
        Instant instantNow = Instant.now();
        long timeout = Duration.between(instantCarted, instantNow).getSeconds() - 1;
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", 42);
        Map<String, Object> combined = new HashMap<>();
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("status", ShoppingCartStatus.PENDING.toString());
        combined.put("addOrRemove", statusUpdate);
        mongoDBService.updateOne("ecommerce", "cart", ShoppingCart.class,
                filterMap, combined, new HashMap<>());

        // when
        productInventoryService.cleanupInventory(timeout);

        // verify
        ShoppingCart cart = productInventoryService.getCartByCartId(42);
        assertEquals(0, cart.getItems().size());
        assertEquals(17, productCatalogService.getProductBySku("00e8da9b",
                AudioAlbum.class).get().getQuantity());
        assertEquals(20, productCatalogService.getProductBySku("0ab42f88",
                AudioAlbum.class).get().getQuantity());
    }

    @Configuration
    static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private StoreService storeService;

        @Autowired
        private PriceService priceService;

        @Autowired
        private StoreInventoryService storeInventoryService;

        @Autowired
        private SKUCodeProductIdGenerator skuCodeGeneratorService;

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

        @Bean
        SKUCodeProductIdGenerator skuCodeGeneratorService() {
            return new SKUCodeProductIdGenerator();
        }

        @Bean
        StoreService storeService() {
            return new StoreService();
        }

        @Bean
        PriceService priceService() {
            return new PriceService();
        }

        @Bean
        StoreInventoryService storeInventoryService() {
            return new StoreInventoryService();
        }
    }

}