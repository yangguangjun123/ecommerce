package org.myproject.mongodb.sharding.services;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(BlockJUnit4ClassRunner.class)
public class ProductCatalogDataServiceTest {
    private ProductCatalogJsonDataService service;

    @Before
    public void setUp() {
        service = new ProductCatalogJsonDataService();
    }

    @After
    public void tearDown() {
    }

    @Test
    @Ignore
    public void shouldCreateAudioAlbumProduct() {
        // when
        String issue_date = "31-10-2017";
        Map<String, String> productDetails = new HashMap<>();
        productDetails.put("issue_date", issue_date);

        // given
        String product = service.createAudioAlbumJsonString(new HashMap<>());

        // verify
//        Assert.assertEquals("issue date is wrong", issue_date,
//                product.getDetails().getIssueDate().toString());
    }
}
