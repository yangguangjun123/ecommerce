package org.myproject.ecommerce.datamodel;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ShoppingCartStatus {
    ACTIVE("active");

    private String type;

    private static final Map<String, ShoppingCartStatus> MAP =
            Stream.of(ShoppingCartStatus.values())
                    .collect(Collectors.toMap(ShoppingCartStatus::toString, Function.identity()));

    ShoppingCartStatus(String type) {
        this.type = type;
    }

    public ShoppingCartStatus fromValue(String value) {
        return MAP.get(value);
    }

    @Override
    public String toString() {
        return type;
    }
}
