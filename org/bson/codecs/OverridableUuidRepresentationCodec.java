/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;

public interface OverridableUuidRepresentationCodec<T> {
    public Codec<T> withUuidRepresentation(UuidRepresentation var1);
}

