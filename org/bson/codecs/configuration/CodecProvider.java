/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.configuration;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

public interface CodecProvider {
    public <T> Codec<T> get(Class<T> var1, CodecRegistry var2);
}

