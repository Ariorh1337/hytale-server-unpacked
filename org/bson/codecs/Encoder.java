/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonWriter;
import org.bson.codecs.EncoderContext;

public interface Encoder<T> {
    public void encode(BsonWriter var1, T var2, EncoderContext var3);

    public Class<T> getEncoderClass();
}

