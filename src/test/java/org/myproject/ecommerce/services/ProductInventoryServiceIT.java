package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.AudioAlbum;
import org.myproject.ecommerce.domain.ShoppingCart;
import org.myproject.ecommerce.domain.ShoppingCartItemDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //call post-constructor
        Method postConstruct =  ProductInventoryService.class.getDeclaredMethod(
                "initialise",null); // methodName,parameters
        postConstruct.setAccessible(true);
        postConstruct.invoke(productInventoryService);
    }

    @After
    public void tearDown() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //call post-constructor
        Method postConstruct =  ProductInventoryService.class.getDeclaredMethod(
                "initialise",null); // methodName,parameters
        postConstruct.setAccessible(true);
        postConstruct.invoke(productInventoryService);

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
        ShoppingCart cart = productInventoryService.readOne(cartId);
        assertEquals(cartId, cart.getCartId());
        assertEquals(sku, cart.getItems().get(cart.getItems().size() - 1).getSku());
        assertEquals(quantity, cart.getItems().get(cart.getItems().size() - 1).getQuantity());
        assertEquals(details, cart.getItems().get(cart.getItems().size() - 1).getItemDetails());
        AudioAlbum afterAdded = productCatalogService.readBySku(sku, AudioAlbum.class);
        assertEquals(beforeAdded.getQuantity() - 1, afterAdded.getQuantity());
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

    }

}
