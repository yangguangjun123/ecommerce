package org.myproject.ecommerce.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;
import org.myproject.ecommerce.domain.StoreInventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.myproject.ecommerce.domain.StoreInventory.StoreVariation;

public class StoreInventoryCodec implements Codec<StoreInventory> {
    private CodecRegistry codecRegistry;
    private static final Logger logger = LoggerFactory.getLogger(StoreInventoryCodec.class);

    public StoreInventoryCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public StoreInventory decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codecRegistry.get(Document.class).decode(reader, decoderContext);
        logger.info("document "+ document);
        StoreInventory storeInventory = new StoreInventory();
        storeInventory.setId(document.getString("_id"));
        storeInventory.setStoreId(document.getString("storeId"));
        storeInventory.setLocation((List<Double>) document.get("location"));
        storeInventory.setProductId(document.getString("productId"));
        if(document.get("vars") != null && document.get("vars") instanceof ArrayList) {
            List<StoreVariation> storeVariations =
                    ((ArrayList<Document>) document.get("vars"))
                        .stream()
                        .map(d -> new StoreVariation(d.getString("sku"),
                                d.getInteger("quantity")))
                        .collect(toList());
            storeInventory.setStoreVariations(storeVariations);
        }
        if(document.get("vars") != null && document.get("vars") instanceof Map) {
            List<StoreVariation> storeVariations = new ArrayList<>();
            Map<String, Object> varsMap = (Map<String, Object>) document.get("vars");
            storeVariations.add(new StoreVariation((String) varsMap.get("sku"),
                    (Integer) varsMap.get("quantity")));
            storeInventory.setStoreVariations(storeVariations);
        }
        return storeInventory;
    }

    @Override
    public void encode(BsonWriter writer, StoreInventory value, EncoderContext encoderContext) {
        String id = value.getId();
        String storeId = value.getStoreId();
        List<Double> location = value.getLocation();
        String productId = value.getProductId();
        List<StoreVariation> storeVariations = value.getStoreVariations();

        writer.writeStartDocument();
        if (null != id) {
            writer.writeName("_id");
            writer.writeString(id);
        }

        if (null != storeId) {
            writer.writeName("storeId");
            writer.writeString(storeId);        }

        if (null != location) {
            writer.writeName("location");
            writer.writeStartArray();
            location.stream()
                    .forEach(l -> writer.writeDouble(l));
            writer.writeEndArray();
        }

        if (null != productId) {
            writer.writeName("productId");
            writer.writeString(productId);
        }

        if(null != storeVariations && storeVariations.size() > 0) {
            writer.writeName("vars");
            writer.writeStartArray();
            storeVariations.stream()
                           .forEach(s -> {
                               writer.writeStartDocument();
                               writer.writeName("sku");
                               writer.writeString(s.getSku());
                               writer.writeName("quantity");
                               writer.writeInt32(s.getQuantity());
                               writer.writeEndDocument();
                           });
            writer.writeEndArray();
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<StoreInventory> getEncoderClass() {
        return StoreInventory.class;
    }

}
