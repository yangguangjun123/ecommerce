package org.myproject.ecommerce.services;

import org.myproject.ecommerce.domain.*;
import org.myproject.ecommerce.interfaces.IProductCatalogService;
import org.myproject.ecommerce.utilities.SKUCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;

@Service
public class ProductCatalogService implements IProductCatalogService {

    private final MongoDBService mongoDBService;

    @Autowired
    public ProductCatalogService(MongoDBService mongoDBService) {
        this.mongoDBService = mongoDBService;
    }

    @PostConstruct
    public void initialise() {
        if(mongoDBService.getDocumentCount("ecommerce", "product",
                Product.class) != 100001) {
            deleteAllProductCatalog();
            for(int i=0; i<50000; i++) {
                createAudioAlbumProducts();
                createFilmProducts();
            }
            String sku = "0ab42f88";
            String genre = FilmGenreType.THRILLER.toString();
            Film.FilmBuilder builder = new Film.FilmBuilder(sku, ProductType.FILM.toString());
            builder.buildGenre(genre).buildTitle("The Matrix [1999][DVD]").buildDescription("by Joel Silver")
                    .buildAsin("B000P0J0AQ").buildShipping(new Shipping(6,
                    new Shipping.Dimensions(10, 10, 1)))
                    .buildPricing(new Pricing(1200, 1100, 100, 0))
                    .buildQuantity(16);
            builder.buildFilmTitle("The Matrix")
                    .buildFilmDirector(Arrays.asList("Andy Wachowski", "Larry Wachowski"))
                    .buildFilmWriter(Arrays.asList("Andy Wachowski", "Larry Wachowski") )
                    .buildAspectRatio("1.66:1")
                    .buildFilmIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                            .atStartOfDay().toInstant(ZoneOffset.UTC)))
                    .buildFilmOtherGenres(Arrays.asList("Science Fiction", "Action & Adventure"))
                    .buildActor("Keanu Reeves");
            mongoDBService.createOne("ecommerce", "product", Product.class, builder.build());
        }
    }

    public void deleteAllProductCatalog() {
        mongoDBService.delete("ecommerce", "product");
    }

    public void createAudioAlbumProducts(int quantity) {
        for(int i=1; i<=quantity; i++) {
            createAudioAlbumProducts();
        }
    }

    public void createAudioAlbumProducts() {
        Product audioAlbum = createAudioAlbumProduct();
        mongoDBService.createOne("ecommerce", "product", Product.class, audioAlbum);
    }

    public void createFilmProducts(int quantity) {
        createFilmProducts();
    }

    public void createFilmProducts() {
        Product film = createFilmProduct();
        mongoDBService.createOne("ecommerce", "product", Product.class, film);
    }

    public void readAllAudioAlbumProducts(Consumer<AudioAlbum> consumer) {
        Map<String, Object> eqFilter = new HashMap<>();
        eqFilter.put("type", "Audio Album");
        List<AudioAlbum> audioAlbumList = mongoDBService.readAllByFiltering("ecommerce",
                "product", AudioAlbum.class, eqFilter);
        audioAlbumList.forEach(consumer);
    }

    public void readAllFilmProducts(Consumer<Film> consumer) {
        Map<String, Object> eqFilter = new HashMap<>();
        eqFilter.put("type", "Film");
        List<Film> filmList = mongoDBService.readAllByFiltering("ecommerce",
                "product", Film.class, eqFilter);
        filmList.forEach(consumer);
    }

    public <T> T readBySku(String sku, Class<T> clazz) {
        Map<String, Object> filter = new HashMap<>();
        filter.put("sku", sku);
        return mongoDBService.readOne("ecommerce", "product", clazz, filter).get();
    }

    private AudioAlbum createAudioAlbumProduct() {
        String sku = SKUCodeGenerator.createSKUCode(ProductType.AUDIOALBUM);
        String genre = AudioAlbumGenreType.JAZZ.toString();
        AudioAlbum.AudioAlbumBuilder builder = new AudioAlbum.AudioAlbumBuilder(sku, ProductType.AUDIOALBUM.toString());
        builder.buildGenre(genre).buildTitle("A Love Supreme").buildDescription("by John Coltrane")
                .buildAsin("B0000A118M").buildShipping(new Shipping(6,
                    new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 8))
                .buildQuantity(16);
        builder.buildAudioAlbumTitle("A Love Supreme [Original Recording Reissued]")
               .buildAudioAlbumArtist("John Coltrane").buildAudioAlbumOtherGenres(Arrays.asList( "General" ))
               .buildAudioAlbumTracks(Arrays.asList( "A Love Supreme Part I: Acknowledgement",
                       "A Love Supreme Part II - Resolution",
                       "A Love Supreme, Part III: Pursuance",
                       "A Love Supreme, Part IV-Psalm" ) )
               .buildAudioAlbumIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                       .atStartOfDay().toInstant(ZoneOffset.UTC)));
        return builder.build();
    }

    private Film createFilmProduct() {
        String sku = SKUCodeGenerator.createSKUCode(ProductType.FILM);
        String genre = FilmGenreType.THRILLER.toString();
        Film.FilmBuilder builder = new Film.FilmBuilder(sku, ProductType.FILM.toString());
        builder.buildGenre(genre).buildTitle("The Matrix [1999][DVD]").buildDescription("by Joel Silver")
                .buildAsin("B000P0J0AQ").buildShipping(new Shipping(6,
                new Shipping.Dimensions(10, 10, 1)))
                .buildPricing(new Pricing(1200, 1100, 100, 0))
                .buildQuantity(16);
        builder.buildFilmTitle("The Matrix")
                .buildFilmDirector(Arrays.asList("Andy Wachowski", "Larry Wachowski"))
                .buildFilmWriter(Arrays.asList("Andy Wachowski", "Larry Wachowski") )
                .buildAspectRatio("1.66:1")
                .buildFilmIssueDate(Date.from(LocalDate.of(1965, 1, 1)
                        .atStartOfDay().toInstant(ZoneOffset.UTC)))
                .buildFilmOtherGenres(Arrays.asList("Science Fiction", "Action & Adventure"))
                .buildActor("Keanu Reeves");
        return builder.build();
    }

}
