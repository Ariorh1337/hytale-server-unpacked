/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.BsonType;
import org.bson.codecs.Codec;

public interface RepresentationConfigurable<T> {
    public BsonType getRepresentation();

    public Codec<T> withRepresentation(BsonType var1);
}

