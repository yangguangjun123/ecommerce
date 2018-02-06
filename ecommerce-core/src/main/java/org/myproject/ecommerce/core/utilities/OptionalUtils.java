package org.myproject.ecommerce.core.utilities;

import java.util.Optional;
import java.util.Properties;
import java.util.function.IntPredicate;

public class OptionalUtils {
    public static Optional<Integer> stringToInt(String s) {
        try {
            return Optional.of(Integer.parseInt(s));
        } catch(NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static int readProperty(Properties props, String name, IntPredicate test, int defaultValue) {
        return Optional.ofNullable(props.getProperty(name))
                       .flatMap(OptionalUtils::stringToInt)
                       .filter(i -> test.test(i))
                       .orElse(defaultValue);
    }
}
