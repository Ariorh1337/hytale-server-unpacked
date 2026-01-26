/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Immutable
public final class EnumTypeProtoConverter<E extends Enum<E>, O> {
    private final Map<E, O> fromProtoEnumMap;
    private final Map<O, E> toProtoEnumMap;

    private EnumTypeProtoConverter(Map<E, O> fromProtoEnumMap, Map<O, E> toProtoEnumMap) {
        this.fromProtoEnumMap = fromProtoEnumMap;
        this.toProtoEnumMap = toProtoEnumMap;
    }

    public static <E extends Enum<E>, O> Builder<E, O> builder() {
        return new Builder();
    }

    public E toProtoEnum(O objectEnum) throws GeneralSecurityException {
        Enum protoEnum = (Enum)this.toProtoEnumMap.get(objectEnum);
        if (protoEnum == null) {
            throw new GeneralSecurityException("Unable to convert object enum: " + objectEnum);
        }
        return (E)protoEnum;
    }

    public O fromProtoEnum(E protoEnum) throws GeneralSecurityException {
        O objectEnum = this.fromProtoEnumMap.get(protoEnum);
        if (objectEnum == null) {
            throw new GeneralSecurityException("Unable to convert proto enum: " + protoEnum);
        }
        return objectEnum;
    }

    public static final class Builder<E extends Enum<E>, O> {
        Map<E, O> fromProtoEnumMap = new HashMap<E, O>();
        Map<O, E> toProtoEnumMap = new HashMap<O, E>();

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder<E, O> add(E protoEnum, O objectEnum) {
            this.fromProtoEnumMap.put(protoEnum, objectEnum);
            this.toProtoEnumMap.put(objectEnum, protoEnum);
            return this;
        }

        public EnumTypeProtoConverter<E, O> build() {
            return new EnumTypeProtoConverter(Collections.unmodifiableMap(this.fromProtoEnumMap), Collections.unmodifiableMap(this.toProtoEnumMap));
        }
    }
}

