/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.codecs.Codec;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.TypeWithTypeParameters;

public interface PropertyCodecProvider {
    public <T> Codec<T> get(TypeWithTypeParameters<T> var1, PropertyCodecRegistry var2);
}

