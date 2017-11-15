package org.myproject.ecommerce.datamodel;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProductType {
    FILM("Film"), AUDIOALBUM("Audio Album");

    private final String type;

    private static final Map<String, ProductType> MAP = Stream.of(ProductType.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    ProductType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

    public static ProductType fromValue(String value){
        return MAP.get(value);
    }

}
