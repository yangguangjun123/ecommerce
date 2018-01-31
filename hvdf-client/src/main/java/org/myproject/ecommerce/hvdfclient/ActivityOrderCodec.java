package org.myproject.ecommerce.hvdfclient;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

public class ActivityOrderCodec implements Codec<Activity.Order> {
    private CodecRegistry codecRegistry;

    public ActivityOrderCodec(CodecRegistry codecRegistry) {
        this.codecRegistry = codecRegistry;
    }

    @Override
    public Activity.Order decode(BsonReader reader, DecoderContext decoderContext) {
        Document document = codecRegistry.get(Document.class).decode(reader, decoderContext);
        return new Activity.Order(document.getString("id"), document.getInteger("total"));
    }

    @Override
    public void encode(BsonWriter writer, Activity.Order value, EncoderContext encoderContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<Activity.Order> getEncoderClass() {
        return Activity.Order.class;
    }
}
