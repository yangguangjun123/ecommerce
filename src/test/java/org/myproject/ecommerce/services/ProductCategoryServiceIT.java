package org.myproject.ecommerce.services;

import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.ProductCategory;
import org.myproject.ecommerce.interfaces.IProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductCategoryServiceIT.CustomConfiguration.class } )
public class ProductCategoryServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private IProductCategoryService productCategoryService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldUpdateProductCategoryHierarchy() {
        // given
        ObjectId bopId = new ObjectId("4f5ec858eb03303a11000001");
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("slug", "swing");
        ProductCategory swing = mongoDBService.readOne("ecommerce", "categories",
                ProductCategory.class, filterMap).get();

        // when
        productCategoryService.updateAncestry(bopId, Optional.of(swing.getId()));

        // verify
        filterMap.clear();
        filterMap.put("slug", "bop");
        ProductCategory bop = mongoDBService.readOne("ecommerce", "categories",
                ProductCategory.class, filterMap).get();
        assertEquals(swing.getId(), bop.getParent());
        List<ProductCategory.Ancestor> expected = new ArrayList<>();
        expected.add(new ProductCategory.Ancestor(bop.getId(), bop.getSlug(), bop.getName()));
        expected.addAll(bop.getAncestors());
        filterMap.clear();
        filterMap.put("ancestors._id", bop.getId());
        List<ProductCategory>  descendants  = mongoDBService.readAll("ecommerce",
                "categories", ProductCategory.class, filterMap);
        descendants.stream()
                   .forEach(d -> assertEquals(expected, d.getAncestors()));
    }

    @Test
    public void shouldRenameCategory() {
        // given
        String newName = "BeBop";

        // when
        ObjectId bopId = new ObjectId("4f5ec858eb03303a11000001");
        productCategoryService.renameCategory(bopId, newName);

        // verify
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("slug", "bop");
        ProductCategory bop = mongoDBService.readOne("ecommerce", "categories",
                ProductCategory.class, filterMap).get();
        assertEquals(newName, bop.getName());
        filterMap.clear();
        filterMap.put("ancestors._id", bop.getId());
        List<ProductCategory>  descendants  = mongoDBService.readAll("ecommerce",
                "categories", ProductCategory.class, filterMap);
        descendants.stream()
                   .map(ProductCategory::getAncestors)
                   .forEach(ancestors -> {
                       assertTrue(ancestors.stream()
                                           .anyMatch(a -> newName.equals(a.getName())));
                   });
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private IProductCategoryService productCategoryService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        IProductCategoryService productCategoryService() {
            return  new ProductCategoryService();
        }
    }
}