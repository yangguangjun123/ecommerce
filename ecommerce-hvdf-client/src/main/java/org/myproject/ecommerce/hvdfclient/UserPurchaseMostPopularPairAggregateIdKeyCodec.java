package org.myproject.ecommerce.hvdfclient;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class UserPurchaseMostPopularPairAggregateIdKeyCodec implements
        Codec<UserPurchaseMostPopularPairAggregate.IdKey> {
    private CodecRegistry codecRegistry;

    public UserPurchaseMostPopularPairAggregateIdKeyCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public UserPurchaseMostPopularPairAggregate.IdKey decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codecRegistry.get(Document.class).decode(reader, decoderContext);
        return new UserPurchaseMostPopularPairAggregate.IdKey(document.getString("itemId"));
    }

    @Override
    public void encode(BsonWriter writer, UserPurchaseMostPopularPairAggregate.IdKey value,
                       EncoderContext encoderContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<UserPurchaseMostPopularPairAggregate.IdKey> getEncoderClass() {
        return UserPurchaseMostPopularPairAggregate.IdKey.class;
    }
}
