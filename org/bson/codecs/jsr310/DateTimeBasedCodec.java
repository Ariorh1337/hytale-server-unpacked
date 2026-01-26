/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.jsr310;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecConfigurationException;

abstract class DateTimeBasedCodec<T>
implements Codec<T> {
    DateTimeBasedCodec() {
    }

    long validateAndReadDateTime(BsonReader reader) {
        BsonType currentType = reader.getCurrentBsonType();
        if (!currentType.equals((Object)BsonType.DATE_TIME)) {
            throw new CodecConfigurationException(String.format("Could not decode into %s, expected '%s' BsonType but got '%s'.", new Object[]{this.getEncoderClass().getSimpleName(), BsonType.DATE_TIME, currentType}));
        }
        return reader.readDateTime();
    }
}

