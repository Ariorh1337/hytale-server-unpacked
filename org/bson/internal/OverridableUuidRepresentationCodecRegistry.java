/*
 * Decompiled with CFR 0.152.
 */
package org.bson.internal;

import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.OverridableUuidRepresentationCodec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.ChildCodecRegistry;
import org.bson.internal.CodecCache;
import org.bson.internal.CycleDetectingCodecRegistry;

public class OverridableUuidRepresentationCodecRegistry
implements CycleDetectingCodecRegistry {
    private final CodecProvider wrapped;
    private final CodecCache codecCache = new CodecCache();
    private final UuidRepresentation uuidRepresentation;

    OverridableUuidRepresentationCodecRegistry(CodecProvider wrapped, UuidRepresentation uuidRepresentation) {
        this.uuidRepresentation = Assertions.notNull("uuidRepresentation", uuidRepresentation);
        this.wrapped = Assertions.notNull("wrapped", wrapped);
    }

    public UuidRepresentation getUuidRepresentation() {
        return this.uuidRepresentation;
    }

    public CodecProvider getWrapped() {
        return this.wrapped;
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz) {
        return this.get(new ChildCodecRegistry<T>(this, clazz));
    }

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        Codec<T> codec = this.wrapped.get(clazz, registry);
        if (codec instanceof OverridableUuidRepresentationCodec) {
            return ((OverridableUuidRepresentationCodec)((Object)codec)).withUuidRepresentation(this.uuidRepresentation);
        }
        return codec;
    }

    @Override
    public <T> Codec<T> get(ChildCodecRegistry<T> context) {
        if (!this.codecCache.containsKey(context.getCodecClass())) {
            Codec<T> codec = this.wrapped.get(context.getCodecClass(), context);
            if (codec instanceof OverridableUuidRepresentationCodec) {
                codec = ((OverridableUuidRepresentationCodec)((Object)codec)).withUuidRepresentation(this.uuidRepresentation);
            }
            this.codecCache.put(context.getCodecClass(), codec);
        }
        return this.codecCache.getOrThrow(context.getCodecClass());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        OverridableUuidRepresentationCodecRegistry that = (OverridableUuidRepresentationCodecRegistry)o;
        if (!this.wrapped.equals(that.wrapped)) {
            return false;
        }
        return this.uuidRepresentation == that.uuidRepresentation;
    }

    public int hashCode() {
        int result = this.wrapped.hashCode();
        result = 31 * result + this.uuidRepresentation.hashCode();
        return result;
    }
}

