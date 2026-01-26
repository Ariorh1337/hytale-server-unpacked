/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.jsr310;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.jsr310.DateTimeBasedCodec;

public class LocalDateTimeCodec
extends DateTimeBasedCodec<LocalDateTime> {
    @Override
    public LocalDateTime decode(BsonReader reader, DecoderContext decoderContext) {
        return Instant.ofEpochMilli(this.validateAndReadDateTime(reader)).atZone(ZoneOffset.UTC).toLocalDateTime();
    }

    @Override
    public void encode(BsonWriter writer, LocalDateTime value, EncoderContext encoderContext) {
        try {
            writer.writeDateTime(value.toInstant(ZoneOffset.UTC).toEpochMilli());
        }
        catch (ArithmeticException e) {
            throw new CodecConfigurationException(String.format("Unsupported LocalDateTime value '%s' could not be converted to milliseconds: %s", value, e.getMessage()), e);
        }
    }

    @Override
    public Class<LocalDateTime> getEncoderClass() {
        return LocalDateTime.class;
    }
}

