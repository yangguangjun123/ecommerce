package org.myproject.ecommerce.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.myproject.ecommerce.domain.StoreInventory;

public class CustomCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == StoreInventory.class) {
            return (Codec<T>) new StoreInventoryCodec(registry);
        }
        return null;

    }
}
