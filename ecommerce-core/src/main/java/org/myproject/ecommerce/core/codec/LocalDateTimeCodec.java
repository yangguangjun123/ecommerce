package org.myproject.ecommerce.core.codec;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class LocalDateTimeCodec implements Codec<LocalDateTime> {

    @Override
    public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return Instant.ofEpochMilli(reader.readDateTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
    }

    @Override
    public void encode(BsonWriter writer, LocalDateTime localDateTime, EncoderContext encoderContext) {
        ZonedDateTime ldtZoned = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = ldtZoned.withZoneSameInstant(ZoneId.of("UTC"));
        writer.writeString(Date.from(utcZoned.toInstant()).toInstant().toString());
    }

    @Override
    public Class<LocalDateTime> getEncoderClass() {
        return LocalDateTime.class;
    }
}
