package org.myproject.ecommerce.services;

import org.myproject.ecommerce.domain.ShoppingCart;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public void collectPayment(ShoppingCart cart) throws EcommerceException {
        System.out.println("Payment has bee collected for the cart: " + cart.toString());
    }
}
