/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.Serialization;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

public abstract class KeySerializer<KeyT extends Key, SerializationT extends Serialization> {
    private final Class<KeyT> keyClass;
    private final Class<SerializationT> serializationClass;

    private KeySerializer(Class<KeyT> keyClass, Class<SerializationT> serializationClass) {
        this.keyClass = keyClass;
        this.serializationClass = serializationClass;
    }

    public abstract SerializationT serializeKey(KeyT var1, @Nullable SecretKeyAccess var2) throws GeneralSecurityException;

    public Class<KeyT> getKeyClass() {
        return this.keyClass;
    }

    public Class<SerializationT> getSerializationClass() {
        return this.serializationClass;
    }

    public static <KeyT extends Key, SerializationT extends Serialization> KeySerializer<KeyT, SerializationT> create(final KeySerializationFunction<KeyT, SerializationT> function, Class<KeyT> keyClass, Class<SerializationT> serializationClass) {
        return new KeySerializer<KeyT, SerializationT>(keyClass, serializationClass){

            @Override
            public SerializationT serializeKey(KeyT key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
                return function.serializeKey(key, access);
            }
        };
    }

    public static interface KeySerializationFunction<KeyT extends Key, SerializationT extends Serialization> {
        public SerializationT serializeKey(KeyT var1, @Nullable SecretKeyAccess var2) throws GeneralSecurityException;
    }
}

