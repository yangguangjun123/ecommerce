package org.myproject.mongodb.sharding.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.myproject.mongodb.sharding.datamodel.AudioAlbum;
import org.myproject.mongodb.sharding.datamodel.Film;
import org.myproject.mongodb.sharding.datamodel.Product;
import org.myproject.mongodb.sharding.datamodel.ProductType;
import org.myproject.mongodb.sharding.utilities.IsoDateSerializer;
import org.myproject.mongodb.sharding.utilities.SKUCodeGenerator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProductCatalogJsonDataService {
    private ObjectMapper mapper = null;

    public ProductCatalogJsonDataService() {
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public String createAudioAlbumJsonString(Map<String, String> productDetails) {
        String audioAlbum = "";
        try {
            AudioAlbum product = mapper.readValue(this.getClass().getResourceAsStream(
                    "/audio_album.json"), AudioAlbum.class);
            product.setSku(SKUCodeGenerator.createSKUCode(ProductType.AUDIOALBUM));
            product.getDetails().setIssueDate(Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)));
            audioAlbum = mapper.writeValueAsString(product);
            System.out.println(audioAlbum);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioAlbum;
    }

    public Film createFilmProduct(Map<String, String> productDetails) {
        Film filmProduct = new Film();

        return filmProduct;
    }

//    public static void main(String[] args) {
//        ProductCatalogJsonDataService service = new ProductCatalogJsonDataService();
//        service.createAudioAlbumProduct(new HashMap<>());
//    }

}