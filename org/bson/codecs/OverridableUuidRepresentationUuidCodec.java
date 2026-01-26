/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs;

import java.util.UUID;
import org.bson.UuidRepresentation;
import org.bson.codecs.Codec;
import org.bson.codecs.OverridableUuidRepresentationCodec;
import org.bson.codecs.UuidCodec;

public class OverridableUuidRepresentationUuidCodec
extends UuidCodec
implements OverridableUuidRepresentationCodec<UUID> {
    public OverridableUuidRepresentationUuidCodec() {
    }

    public OverridableUuidRepresentationUuidCodec(UuidRepresentation uuidRepresentation) {
        super(uuidRepresentation);
    }

    @Override
    public Codec<UUID> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
        return new OverridableUuidRepresentationUuidCodec(uuidRepresentation);
    }
}

