/*
 * Decompiled with CFR 0.152.
 */
package org.bson.internal;

import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.OverridableUuidRepresentationCodecRegistry;

public final class CodecRegistryHelper {
    public static CodecRegistry createRegistry(CodecRegistry codecRegistry, UuidRepresentation uuidRepresentation) {
        if (uuidRepresentation == UuidRepresentation.UNSPECIFIED) {
            return codecRegistry;
        }
        return new OverridableUuidRepresentationCodecRegistry(codecRegistry, uuidRepresentation);
    }

    private CodecRegistryHelper() {
    }
}

