package org.myproject.ecommerce.core.services;

import org.myproject.ecommerce.core.domain.ShoppingCart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public void collectPayment(ShoppingCart cart) throws EcommerceException {
        logger.info("Payment has bee collected for the cart: " + cart.toString());
    }
}
