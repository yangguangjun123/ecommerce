package org.myproject.ecommerce.hvdfclient;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class ActivityIdCodec implements Codec<ActivityId> {
    private CodecRegistry codecRegistry;

    public ActivityIdCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public ActivityId decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codecRegistry.get(Document.class).decode(reader, decoderContext);
        return new ActivityId(document.getString("source"), document.getLong("ts"));
    }

    @Override
    public void encode(BsonWriter writer, ActivityId idKey, EncoderContext encoderContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<ActivityId> getEncoderClass() {
        return ActivityId.class;
    }
}
