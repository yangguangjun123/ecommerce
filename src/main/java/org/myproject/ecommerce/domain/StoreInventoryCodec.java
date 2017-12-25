package org.myproject.ecommerce.domain;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.EncoderContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class StoreInventoryCodec implements Codec<StoreInventory> {
    private Codec<Document> documentCodec;
    private static final Logger logger = LoggerFactory.getLogger(StoreInventoryCodec.class);

    public StoreInventoryCodec() {
        this.documentCodec = new DocumentCodec();
    }

    public StoreInventoryCodec(Codec<Document> codec) {
        this.documentCodec = codec;
    }

    @Override
    public StoreInventory decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = documentCodec.decode(reader, decoderContext);
        logger.info("document "+ document);
        StoreInventory storeInventory = new StoreInventory();
        storeInventory.setId(document.getString("_id"));
        storeInventory.setStoreId(document.getString("storeId"));
        storeInventory.setLocation((List<Double>) document.get("location"));
        storeInventory.setProductId(document.getString("productId"));
        if(document.get("vars") != null && document.get("vars") instanceof ArrayList) {
            List<StoreInventory.StoreVariation> storeVariations =
                    ((ArrayList<Document>) document.get("vars"))
                        .stream()
                        .map(d -> new StoreInventory.StoreVariation(d.getString("sku"),
                                d.getInteger("quantity")))
                        .collect(toList());
            storeInventory.setStoreVariations(storeVariations);
        }
        if(document.get("vars") != null && document.get("vars") instanceof Map) {
            List<StoreInventory.StoreVariation> storeVariations = new ArrayList<>();
            Map<String, Object> varsMap = (Map<String, Object>) document.get("vars");
            storeVariations.add(new StoreInventory.StoreVariation((String) varsMap.get("sku"),
                    (Integer) varsMap.get("quantity")));
            storeInventory.setStoreVariations(storeVariations);
        }
        return storeInventory;
    }

    @Override
    public void encode(BsonWriter writer, StoreInventory value, EncoderContext encoderContext) {
        Document document = new Document();

        String id = value.getId();
        String storeId = value.getStoreId();
        List<Double> location = value.getLocation();
        String productId = value.getProductId();
        List<StoreInventory.StoreVariation> storeVariations = value.getStoreVariations();

        if (null != id) {
            document.put("_id", id);
        }

        if (null != storeId) {
            document.put("storeid", storeId);
        }

        if (null != location) {
            document.put("location", location);
        }

        if (null != productId) {
            document.put("productId", productId);
        }

        if(null != storeVariations && storeVariations.size() > 0) {
            document.put("storeVariations", storeVariations);
        }

        documentCodec.encode(writer, document, encoderContext);
    }

    @Override
    public Class<StoreInventory> getEncoderClass() {
        return StoreInventory.class;
    }
}
