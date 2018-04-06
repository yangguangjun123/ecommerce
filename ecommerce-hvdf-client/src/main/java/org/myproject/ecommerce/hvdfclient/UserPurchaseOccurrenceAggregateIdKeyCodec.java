package org.myproject.ecommerce.hvdfclient;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class UserPurchaseOccurrenceAggregateIdKeyCodec implements Codec<UserPurchaseOccurrenceAggregate.IdKey> {
    private CodecRegistry codecRegistry;

    public UserPurchaseOccurrenceAggregateIdKeyCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public UserPurchaseOccurrenceAggregate.IdKey decode(BsonReader reader, DecoderContext decoderContext) {

        Document document = codecRegistry.get(Document.class).decode(reader, decoderContext);
        return new UserPurchaseOccurrenceAggregate.IdKey(document.getString("a"), document.getString("b"));
    }

    @Override
    public void encode(BsonWriter writer, UserPurchaseOccurrenceAggregate.IdKey value, EncoderContext encoderContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<UserPurchaseOccurrenceAggregate.IdKey> getEncoderClass() {
        return UserPurchaseOccurrenceAggregate.IdKey.class;
    }
}
