package org.myproject.ecommerce.services;

import org.bson.types.ObjectId;
import org.myproject.ecommerce.domain.*;
import org.myproject.ecommerce.interfaces.IProductCatalogService;
import org.myproject.ecommerce.utilities.SKUCodeProductIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class ProductCatalogService implements IProductCatalogService {

    @Autowired
    private final MongoDBService mongoDBService;

    @Autowired
    private SKUCodeProductIdGenerator skuCodeGeneratorService;

    @Autowired
    public ProductCatalogService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initialise() {
        if(mongoDBService.getDocumentCount("ecommerce", "product",
                Product.class) != 100004) {
            deleteAllProductCatalog();
            deleteAllProductVariations();
            skuCodeGeneratorService.reset();
            for(int i=0; i<50000; i++) {
                Product audioAlbum = createAudioAlbumProduct();
                createAudioAlbumProductVariation(audioAlbum);
                Product film = createFilmProduct();
                createFilmProductVariation(film);
            }

            // build prep-populated products
            Film matrix = createMatrixFilmProduct();
            matrix.setSku("0ab42f88");
            mongoDBService.createOne("ecommerce", "product",
                    Product.class, matrix);
            List<ProductVariation> productVariations = createFilmProductVariation(matrix);

            AudioAlbum loveSupreme = createLoveSupremeAudioProduct();
            loveSupreme.setSku("00e8da9b");
            mongoDBService.createOne("ecommerce", "product",
                    Product.class, loveSupreme);
            productVariations = createAudioAlbumProductVariation(loveSupreme);

            Film anotherMatrix = createMatrixFilmProduct();
            anotherMatrix.setSku("00e8da9c");
            mongoDBService.createOne("ecommerce", "product",
                    Product.class, anotherMatrix);
            productVariations = createFilmProductVariation(anotherMatrix);

            AudioAlbum anotherLoveSupreme = createLoveSupremeAudioProduct();
            anotherLoveSupreme.setSku("00e8da9d");
            mongoDBService.createOne("ecommerce", "product",
                    Product.class, anotherLoveSupreme);
            productVariations = createAudioAlbumProductVariation(anotherLoveSupreme);
        }
    }

    public void deleteAllProductCatalog() {
        mongoDBService.deleteAll("ecommerce", "product");
    }

    public void deleteAllProductVariations() {
        mongoDBService.deleteAll("ecommerce", "variations");
    }

    public void deleteProductById(ObjectId id) {
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("_id", id);
        mongoDBService.deleteOne("ecommerce", "product", filterMap);
    }

    public Product createAudioAlbumProduct() {
        Product audioAlbum = buildAudioAlbumProduct();
        mongoDBService.createOne("ecommerce", "product", Product.class, audioAlbum);
        return audioAlbum;
    }

    public Product createFilmProduct() {
        Product film = buildFilmProduct();
        mongoDBService.createOne("ecommerce", "product", Product.class, film);
        return film;
    }

    public <T> Optional<T> getProductBySku(String sku, Class<T> clazz) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("sku", sku);
        return mongoDBService.readOne("ecommerce", "product", clazz, filter);
    }

    public <T> Optional<T> getProductVariationBySku(String sku, Class<T> clazz) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("_id", sku);
        return mongoDBService.readOne("ecommerce", "variations", clazz, filter);
    }

    public <T> Optional<T> getProductByProductId(String productId, Class<T> clazz) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("productId", productId);
        return mongoDBService.readOne("ecommerce", "product", clazz, filter);
    }

    public <T> List<T> getProducts(List<String> productIds, Class<T> clazz) {
        Map<String, Object> valueMap = new HashMap<>();
        valueMap.put("productId", productIds);
        Map<String, Object> filterMap = new HashMap<>();
        filterMap.put("$in", valueMap);
        return mongoDBService.readAll("ecommerce", "product", clazz, filterMap);
    }

    public <T> List<T> getAllProductVariationsByProductId(String productId, Class<T> clazz) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("productId", productId);
        return mongoDBService.readAll("ecommerce", "variations", clazz, filter);
    }

    private AudioAlbum buildAudioAlbumProduct() {
        String productId = skuCodeGeneratorService.createProductId();
        String sku = skuCodeGeneratorService.createProductSKUCode();
        String genre = AudioAlbumGenreType.JAZZ.toString();
        AudioAlbum.AudioAlbumBuilder builder = new AudioAlbum.AudioAlbumBuilder(productId, sku,
                ProductType.AUDIOALBUM.toString());
        builder.buildGenre(genre).buildTitle("A Love Supreme").buildDescription("by John Coltrane")
                .buildAsin("B0000A118M").buildShipping(new Shipping(6,
                    new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 8))
                .buildQuantity(16).buildDepartment(DepartmentType.DIGITAL_MUSIC.toString());
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
        return audioAlbum;
    }

    private Film buildFilmProduct() {
        String productId = skuCodeGeneratorService.createProductId();
        String sku = skuCodeGeneratorService.createProductSKUCode();
        String genre = FilmGenreType.THRILLER.toString();
        Film.FilmBuilder builder = new Film.FilmBuilder(productId, sku, ProductType.FILM.toString());
        builder.buildGenre(genre).buildTitle("The Matrix [1999][DVD]").buildDescription("by Joel Silver")
                .buildAsin("B000P0J0AQ").buildShipping(new Shipping(6,
                new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 0))
                .buildQuantity(16).buildDepartment(DepartmentType.DVD_BLUERAY.toString());
        builder.buildFilmTitle("The Matrix")
                .buildFilmDirector(Arrays.asList("Andy Wachowski", "Larry Wachowski"))
                .buildFilmWriter(Arrays.asList("Andy Wachowski", "Larry Wachowski") )
                .buildAspectRatio("1.66:1")
                .buildFilmIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                        .atStartOfDay().toInstant(ZoneOffset.UTC)))
                .buildFilmOtherGenres(Arrays.asList("Science Fiction", "Action & Adventure"))
                .buildActor("Keanu Reeves");
        Film film = builder.build();
        film.setId(new ObjectId());
        return film;
    }

    private Film createMatrixFilmProduct() {
        String productId = skuCodeGeneratorService.createProductId();
        String genre = FilmGenreType.THRILLER.toString();
        Film.FilmBuilder builder = new Film.FilmBuilder(productId,"", ProductType.FILM.toString());
        builder.buildGenre(genre).buildTitle("The Matrix").buildDescription("by Joel Silver")
                .buildAsin("B000P0J0AQ").buildShipping(new Shipping(6,
                new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 0))
                .buildQuantity(16).buildDepartment(DepartmentType.DVD_BLUERAY.toString());
        builder.buildFilmTitle("The Matrix")
                .buildFilmDirector(Arrays.asList("Andy Wachowski", "Larry Wachowski"))
                .buildFilmWriter(Arrays.asList("Andy Wachowski", "Larry Wachowski") )
                .buildAspectRatio("1.66:1")
                .buildFilmIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                        .atStartOfDay().toInstant(ZoneOffset.UTC)))
                .buildFilmOtherGenres(Arrays.asList("Science Fiction", "Action & Adventure"))
                .buildActor("Keanu Reeves");
        Film matrix = builder.build();
        matrix.setId(new ObjectId());
        return matrix;
    }

    private AudioAlbum createLoveSupremeAudioProduct() {
        String productId = skuCodeGeneratorService.createProductId();
        String genre = AudioAlbumGenreType.JAZZ.toString();
        AudioAlbum.AudioAlbumBuilder builder = new AudioAlbum.AudioAlbumBuilder(productId, "",
                ProductType.AUDIOALBUM.toString());
        builder.buildGenre(genre).buildTitle("A Love Supreme").buildDescription("by John Coltrane")
                .buildAsin("B0000A118M").buildShipping(new Shipping(6,
                new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 8))
                .buildQuantity(16).buildDepartment(DepartmentType.DIGITAL_MUSIC.toString());
        builder.buildAudioAlbumTitle("A Love Supreme [Original Recording Reissued]")
                .buildAudioAlbumArtist("John Coltrane").buildAudioAlbumOtherGenres(Arrays.asList( "General" ))
                .buildAudioAlbumTracks(Arrays.asList( "A Love Supreme Part I: Acknowledgement",
                        "A Love Supreme Part II - Resolution",
                        "A Love Supreme, Part III: Pursuance",
                        "A Love Supreme, Part IV-Psalm" ) )
                .buildAudioAlbumIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                        .atStartOfDay().toInstant(ZoneOffset.UTC)));
        AudioAlbum loveSupreme = builder.build();
        loveSupreme.setId(new ObjectId());
        return loveSupreme;
    }

    private List<ProductVariation> createAudioAlbumProductVariation(Product audioAlbum) {
        List<ProductVariation> variations = new ArrayList<>();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(audioAlbum.getProductId());
        productVariation.addAttribute("MP3");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(audioAlbum.getProductId());
        productVariation.addAttribute("Audio CD");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(audioAlbum.getProductId());
        productVariation.addAttribute("Vinyl");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(audioAlbum.getProductId());
        productVariation.addAttribute("Blu-ray Audio");
        variations.add(productVariation);

        mongoDBService.createAll("ecommerce", "variations",
                ProductVariation.class, variations);

        return variations;
    }

    private List<ProductVariation> createFilmProductVariation(Product film) {
        List<ProductVariation> variations = new ArrayList<>();
        ProductVariation productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(film.getProductId());
        productVariation.addAttribute("Blu-ray");
        productVariation.addAttribute("1999");
        productVariation.addAttribute("Region Free");
        productVariation.addAttribute("3-Disc Version");
        productVariation.addAttribute("New");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(film.getProductId());
        productVariation.addAttribute("Blu-ray");
        productVariation.addAttribute("1999");
        productVariation.addAttribute("Region Free");
        productVariation.addAttribute("3-Disc Version");
        productVariation.addAttribute("Used");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(film.getProductId());
        productVariation.addAttribute("Blu-ray");
        productVariation.addAttribute("1999");
        productVariation.addAttribute("Region Free");
        productVariation.addAttribute("1-Disc Version");
        productVariation.addAttribute("New");
        variations.add(productVariation);

        productVariation = new ProductVariation();
        productVariation.setSku(skuCodeGeneratorService.createProductVariationSKUCode());
        productVariation.setProductId(film.getProductId());
        productVariation.addAttribute("Blu-ray");
        productVariation.addAttribute("1999");
        productVariation.addAttribute("Region Free");
        productVariation.addAttribute("1-Disc Version");
        productVariation.addAttribute("Used");
        variations.add(productVariation);

        mongoDBService.createAll("ecommerce", "variations",
                ProductVariation.class, variations);

        return variations;
    }
}