package org.myproject.ecommerce.core.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.myproject.ecommerce.core.domain.AudioAlbum;
import org.myproject.ecommerce.core.domain.Film;
import org.myproject.ecommerce.core.utilities.SKUCodeProductIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class ProductCatalogJsonDataService {
    private final SKUCodeProductIdGenerator skuCodeGeneratorService;

    private ObjectMapper mapper = null;

    private static final Logger logger = LoggerFactory.getLogger((ProductCatalogJsonDataService.class));

    @Autowired
    public ProductCatalogJsonDataService(SKUCodeProductIdGenerator skuCodeGeneratorService) {
        this.skuCodeGeneratorService = skuCodeGeneratorService;
        mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public String createAudioAlbumJsonString(Map<String, String> productDetails) {
        String audioAlbum = "";
        try {
            AudioAlbum product = mapper.readValue(this.getClass().getResourceAsStream(
                    "/audio_album.json"), AudioAlbum.class);
            product.setSku(skuCodeGeneratorService.createProductSKUCode());
            product.getDetails().setIssueDate(Date.from(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC)));
            audioAlbum = mapper.writeValueAsString(product);
            logger.info(audioAlbum);
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