package org.myproject.mongodb.sharding.utilities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneOffset;

public class IsoDateSerializer extends JsonSerializer<LocalDate> {
    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        String isoDate = ISODateTimeFormat.dateTime().print(
                value.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli());
        jgen.writeRaw(" : { \"$date\" : \"" + isoDate + "\"}");
    }
}