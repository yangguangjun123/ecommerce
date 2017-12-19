package org.myproject.ecommerce.services;

import static org.junit.Assert.*;

import org.bson.types.ObjectId;
import org.junit.*;
import org.junit.runner.RunWith;
import org.myproject.ecommerce.domain.*;
import org.myproject.ecommerce.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ProductCatalogServiceIT.CustomConfiguration.class})
public class ProductCatalogServiceIT {

    @Autowired
    private MongoDBService mongoDBService;

    @Autowired
    private SKUCodeProductIdGenerator skuCodeGeneratorService;

    @Autowired
    private StoreService storeService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private StoreInventoryService storeInventoryService;

    @Autowired
    private ProductCatalogService productCatalogService;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        Optional<Product> product = productCatalogService.getProductByProductId("452318", Product.class);
        if(product.isPresent()) {
            productCatalogService.deleteProductById(product.get().getId());
        }
    }

    @Test
    public void shouldReturnCorrectNumberOfProducts() {
        // given

        // when
        long number = mongoDBService.count("ecommerce", "product", Product.class);

        // verify
        assertEquals(100004, number);
    }

    @Test
    public void shouldReturnProductByProductId() {
        // given
        String productId = "30671";

        //when
        Optional<Product> product = productCatalogService.getProductByProductId(productId, Product.class);

        // vetify
        assertEquals(productId, product.get().getProductId());
    }

    @Test
    public void shouldReturnAllProductsWhenAListOfProductIdsReceived() {
        // given
        List<String> productIds = Arrays.asList("30671", "452318");
        String productId = "452318";
        String sku = "Unknown";
        String genre = AudioAlbumGenreType.JAZZ.toString();
        AudioAlbum.AudioAlbumBuilder builder = new AudioAlbum.AudioAlbumBuilder(productId, sku,
                ProductType.AUDIOALBUM.toString());
        builder.buildGenre(genre).buildTitle("A Love Supreme").buildDescription("by John Coltrane")
                .buildAsin("B0000A118M").buildShipping(new Shipping(6,
                new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 8))
                .buildQuantity(16).buildDepartment(DepartmentType.SHOES.toString());
        builder.buildAudioAlbumTitle("A Love Supreme [Original Recording Reissued]")
                .buildAudioAlbumArtist("John Coltrane").buildAudioAlbumOtherGenres(Arrays.asList( "General" ))
                .buildAudioAlbumTracks(Arrays.asList( "A Love Supreme Part I: Acknowledgement",
                        "A Love Supreme Part II - Resolution",
                        "A Love Supreme, Part III: Pursuance",
                        "A Love Supreme, Part IV-Psalm" ) )
                .buildAudioAlbumIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                        .atStartOfDay().toInstant(ZoneOffset.UTC)));
        AudioAlbum audioAlbum = builder.build();
        audioAlbum.setId(new ObjectId());
        mongoDBService.createOne("ecommerce", "product", Product.class, audioAlbum);

        // when
        List<Product> products = productCatalogService.getProducts(productIds, Product.class);

        // verify
        assertEquals(products.size(), products.size());
        products.stream()
                .forEach(p -> assertTrue(productIds.contains(p.getProductId())));
    }

    @Test
    public void shouldReturnAllProductVariationsForSpecificSku() {
        // gievn
        String sku = "93284847362823";

        // when
        ProductVariation productVariation = productCatalogService.getProductVariationBySku(sku,
                ProductVariation.class).get();

        // verify
        assertEquals(sku, productVariation.getSku());
    }

    @Test
    public void shouldReturnAllVariationsForSpecificProduct() {
        // gievn
        String productId = "30671";

        // when
        List<ProductVariation> productVariations = productCatalogService.getAllProductVariationsByProductId(productId,
                ProductVariation.class);

        // verify
        assertEquals(4, productVariations.size());
        productVariations.stream()
                         .forEach(p -> assertEquals(productId, p.getProductId()));
    }

    @Configuration
    public static class CustomConfiguration {
        @Autowired
        private MongoDBService mongoDBService;

        @Autowired
        private SKUCodeProductIdGenerator skuCodeGeneratorService;

        @Autowired
        private StoreService storeService;

        @Autowired
        private PriceService priceService;

        @Autowired
        private StoreInventoryService storeInventoryService;

        @Bean
        MongoDBService mongoDBService() {
            return new MongoDBService();
        }

        @Bean
        ProductCatalogService productCatalogService() {
            return new ProductCatalogService(mongoDBService);
        }

        @Bean
        SKUCodeProductIdGenerator skuCodeGeneratorService() {
            return new SKUCodeProductIdGenerator();
        }

        @Bean
        PriceService priceService() {
            return new PriceService();
        }

        @Bean
        StoreService storeService() {
            return new StoreService();
        }

        @Bean
        StoreInventoryService storeInventoryService() {
            return new StoreInventoryService();
        }

    }

}
