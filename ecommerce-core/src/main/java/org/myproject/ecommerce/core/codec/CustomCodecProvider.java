package org.myproject.ecommerce.core.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.myproject.ecommerce.core.domain.StoreInventory;

import java.time.LocalDateTime;

@SuppressWarnings("unchecked")
public class CustomCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == StoreInventory.class) {
            return (Codec<T>) new StoreInventoryCodec(registry);
        }
        if(clazz == LocalDateTime.class) {
            return (Codec<T>) new LocalDateTimeCodec();
        }
        return null;

    }
}
