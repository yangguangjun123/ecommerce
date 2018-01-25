package org.myproject.ecommerce.core.services;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.core.domain.Store;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationConfiguration.class})
public class StoreServiceIT {
    @Autowired
    private StoreService storeService;

    @Before
    public void setUp() {
    }

    @After
    public void clearDown() {
    }

    @Test
    public void shouldReturnTheStoresWithinSpecificDistanceWhenAGivenPointIsReceived() {
        // given
        double[] coordinates = new double[] { -82.8006,40.0908 };
        double maxDistance = 831441.6134602465;
        double minDistance = 0.0;

        // when
        List<Store> stores = storeService.getStores(coordinates, maxDistance, minDistance);

        // verify
        assertTrue(stores.size() > 0);
    }

}
