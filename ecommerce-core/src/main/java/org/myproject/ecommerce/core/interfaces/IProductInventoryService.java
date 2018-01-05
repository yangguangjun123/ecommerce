package org.myproject.ecommerce.core.interfaces;

import org.myproject.ecommerce.core.domain.ShoppingCart;
import org.myproject.ecommerce.core.domain.ShoppingCartItemDetails;
import org.myproject.ecommerce.core.services.EcommerceException;
import org.myproject.ecommerce.core.services.CartInactiveException;

import javax.annotation.PostConstruct;

public interface IProductInventoryService {
    @PostConstruct
    void initialise();

    void deleteAllCarts(String database);

    void addItemToCart(int cartId, String sku, int quantity, ShoppingCartItemDetails details)
            throws EcommerceException;

    void updateCartQuantity(int cartId, String sku, int oldQty, int newQty) throws EcommerceException;

    void processCheckout(int cartId) throws CartInactiveException;

    void processExpiringCarts(long timeout);

    ShoppingCart getCartByCartId(int cartId);

    // The function is safe for use because it checks to ensure that the cart has expired before returning
    // items from the cart to inventory. However, it could be long-running and slow other updates and queries.
    // Use judiciously.
    void cleanupInventory(long timeout);
}
