package org.myproject.ecommerce.hvdfclient;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class HVDFCustomCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if(clazz == ActivityId.class) {
            return (Codec<T>) new ActivityIdCodec(registry);
        }
        if(clazz == Activity.Order.class) {
            return (Codec<T>) new ActivityOrderCodec(registry);
        }
        if(clazz == UserPurchaseOccurrenceAggregate.IdKey.class) {
            return (Codec<T>) new UserPurchaseOccurrenceAggregateIdKeyCodec(registry);
        }
        if(clazz == UserPurchaseMostPopularPairAggregate.IdKey.class) {
            return (Codec<T>) new UserPurchaseMostPopularPairAggregateIdKeyCodec(registry);
        }
        return null;
    }
}
