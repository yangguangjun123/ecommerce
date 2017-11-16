package org.myproject.ecommerce;

import org.myproject.ecommerce.services.ProductCatalogServiceIT;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                value = ProductCatalogServiceIT.CustomConfiguration.class)
})
public class TestConfiguration {
}
