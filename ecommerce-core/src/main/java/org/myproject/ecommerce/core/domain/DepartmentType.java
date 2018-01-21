package org.myproject.ecommerce.core.domain;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum DepartmentType {
    DIGITAL_MUSIC("Digital Music"), DVD_BLUERAY("DVD & Blu-ray"), SHOES("Shoes"),UNKNOWN("Unknown");

    private final String type;

    private static final Map<String, DepartmentType> MAP = Stream.of(DepartmentType.values())
            .collect(Collectors.toMap(Object::toString, Function.identity()));

    DepartmentType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }

    public static DepartmentType fromValue(String value){
        return MAP.get(value);
    }

}
