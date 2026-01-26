/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Key;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.Serialization;
import com.google.crypto.tink.util.Bytes;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

public final class SerializationRegistry {
    private final Map<SerializerIndex, KeySerializer<?, ?>> keySerializerMap;
    private final Map<ParserIndex, KeyParser<?>> keyParserMap;
    private final Map<SerializerIndex, ParametersSerializer<?, ?>> parametersSerializerMap;
    private final Map<ParserIndex, ParametersParser<?>> parametersParserMap;

    private SerializationRegistry(Builder builder) {
        this.keySerializerMap = new HashMap(builder.keySerializerMap);
        this.keyParserMap = new HashMap(builder.keyParserMap);
        this.parametersSerializerMap = new HashMap(builder.parametersSerializerMap);
        this.parametersParserMap = new HashMap(builder.parametersParserMap);
    }

    public <SerializationT extends Serialization> boolean hasParserForKey(SerializationT serializedKey) {
        ParserIndex index = new ParserIndex(serializedKey.getClass(), serializedKey.getObjectIdentifier());
        return this.keyParserMap.containsKey(index);
    }

    public <SerializationT extends Serialization> Key parseKey(SerializationT serializedKey, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        ParserIndex index = new ParserIndex(serializedKey.getClass(), serializedKey.getObjectIdentifier());
        if (!this.keyParserMap.containsKey(index)) {
            throw new GeneralSecurityException("No Key Parser for requested key type " + index + " available");
        }
        KeyParser<?> parser = this.keyParserMap.get(index);
        return parser.parseKey(serializedKey, access);
    }

    public <KeyT extends Key, SerializationT extends Serialization> boolean hasSerializerForKey(KeyT key, Class<SerializationT> serializationClass) {
        SerializerIndex index = new SerializerIndex(key.getClass(), serializationClass);
        return this.keySerializerMap.containsKey(index);
    }

    public <KeyT extends Key, SerializationT extends Serialization> SerializationT serializeKey(KeyT key, Class<SerializationT> serializationClass, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        SerializerIndex index = new SerializerIndex(key.getClass(), serializationClass);
        if (!this.keySerializerMap.containsKey(index)) {
            throw new GeneralSecurityException("No Key serializer for " + index + " available");
        }
        KeySerializer<?, ?> serializer = this.keySerializerMap.get(index);
        return (SerializationT)serializer.serializeKey(key, access);
    }

    public <SerializationT extends Serialization> boolean hasParserForParameters(SerializationT serializedParameters) {
        ParserIndex index = new ParserIndex(serializedParameters.getClass(), serializedParameters.getObjectIdentifier());
        return this.parametersParserMap.containsKey(index);
    }

    public <SerializationT extends Serialization> Parameters parseParameters(SerializationT serializedParameters) throws GeneralSecurityException {
        ParserIndex index = new ParserIndex(serializedParameters.getClass(), serializedParameters.getObjectIdentifier());
        if (!this.parametersParserMap.containsKey(index)) {
            throw new GeneralSecurityException("No Parameters Parser for requested key type " + index + " available");
        }
        ParametersParser<?> parser = this.parametersParserMap.get(index);
        return parser.parseParameters(serializedParameters);
    }

    public <ParametersT extends Parameters, SerializationT extends Serialization> boolean hasSerializerForParameters(ParametersT parameters, Class<SerializationT> serializationClass) {
        SerializerIndex index = new SerializerIndex(parameters.getClass(), serializationClass);
        return this.parametersSerializerMap.containsKey(index);
    }

    public <ParametersT extends Parameters, SerializationT extends Serialization> SerializationT serializeParameters(ParametersT parameters, Class<SerializationT> serializationClass) throws GeneralSecurityException {
        SerializerIndex index = new SerializerIndex(parameters.getClass(), serializationClass);
        if (!this.parametersSerializerMap.containsKey(index)) {
            throw new GeneralSecurityException("No Key Format serializer for " + index + " available");
        }
        ParametersSerializer<?, ?> serializer = this.parametersSerializerMap.get(index);
        return (SerializationT)serializer.serializeParameters(parameters);
    }

    public static final class Builder {
        private final Map<SerializerIndex, KeySerializer<?, ?>> keySerializerMap;
        private final Map<ParserIndex, KeyParser<?>> keyParserMap;
        private final Map<SerializerIndex, ParametersSerializer<?, ?>> parametersSerializerMap;
        private final Map<ParserIndex, ParametersParser<?>> parametersParserMap;

        public Builder() {
            this.keySerializerMap = new HashMap();
            this.keyParserMap = new HashMap();
            this.parametersSerializerMap = new HashMap();
            this.parametersParserMap = new HashMap();
        }

        public Builder(SerializationRegistry registry) {
            this.keySerializerMap = new HashMap(registry.keySerializerMap);
            this.keyParserMap = new HashMap(registry.keyParserMap);
            this.parametersSerializerMap = new HashMap(registry.parametersSerializerMap);
            this.parametersParserMap = new HashMap(registry.parametersParserMap);
        }

        @CanIgnoreReturnValue
        public <KeyT extends Key, SerializationT extends Serialization> Builder registerKeySerializer(KeySerializer<KeyT, SerializationT> serializer) throws GeneralSecurityException {
            SerializerIndex index = new SerializerIndex(serializer.getKeyClass(), serializer.getSerializationClass());
            if (this.keySerializerMap.containsKey(index)) {
                KeySerializer<?, ?> existingSerializer = this.keySerializerMap.get(index);
                if (!existingSerializer.equals(serializer) || !serializer.equals(existingSerializer)) {
                    throw new GeneralSecurityException("Attempt to register non-equal serializer for already existing object of type: " + index);
                }
            } else {
                this.keySerializerMap.put(index, serializer);
            }
            return this;
        }

        @CanIgnoreReturnValue
        public <SerializationT extends Serialization> Builder registerKeyParser(KeyParser<SerializationT> parser) throws GeneralSecurityException {
            ParserIndex index = new ParserIndex(parser.getSerializationClass(), parser.getObjectIdentifier());
            if (this.keyParserMap.containsKey(index)) {
                KeyParser<?> existingParser = this.keyParserMap.get(index);
                if (!existingParser.equals(parser) || !parser.equals(existingParser)) {
                    throw new GeneralSecurityException("Attempt to register non-equal parser for already existing object of type: " + index);
                }
            } else {
                this.keyParserMap.put(index, parser);
            }
            return this;
        }

        @CanIgnoreReturnValue
        public <ParametersT extends Parameters, SerializationT extends Serialization> Builder registerParametersSerializer(ParametersSerializer<ParametersT, SerializationT> serializer) throws GeneralSecurityException {
            SerializerIndex index = new SerializerIndex(serializer.getParametersClass(), serializer.getSerializationClass());
            if (this.parametersSerializerMap.containsKey(index)) {
                ParametersSerializer<?, ?> existingSerializer = this.parametersSerializerMap.get(index);
                if (!existingSerializer.equals(serializer) || !serializer.equals(existingSerializer)) {
                    throw new GeneralSecurityException("Attempt to register non-equal serializer for already existing object of type: " + index);
                }
            } else {
                this.parametersSerializerMap.put(index, serializer);
            }
            return this;
        }

        @CanIgnoreReturnValue
        public <SerializationT extends Serialization> Builder registerParametersParser(ParametersParser<SerializationT> parser) throws GeneralSecurityException {
            ParserIndex index = new ParserIndex(parser.getSerializationClass(), parser.getObjectIdentifier());
            if (this.parametersParserMap.containsKey(index)) {
                ParametersParser<?> existingParser = this.parametersParserMap.get(index);
                if (!existingParser.equals(parser) || !parser.equals(existingParser)) {
                    throw new GeneralSecurityException("Attempt to register non-equal parser for already existing object of type: " + index);
                }
            } else {
                this.parametersParserMap.put(index, parser);
            }
            return this;
        }

        public SerializationRegistry build() {
            return new SerializationRegistry(this);
        }
    }

    private static class ParserIndex {
        private final Class<? extends Serialization> keySerializationClass;
        private final Bytes serializationIdentifier;

        private ParserIndex(Class<? extends Serialization> keySerializationClass, Bytes serializationIdentifier) {
            this.keySerializationClass = keySerializationClass;
            this.serializationIdentifier = serializationIdentifier;
        }

        public boolean equals(Object o) {
            if (!(o instanceof ParserIndex)) {
                return false;
            }
            ParserIndex other = (ParserIndex)o;
            return other.keySerializationClass.equals(this.keySerializationClass) && other.serializationIdentifier.equals(this.serializationIdentifier);
        }

        public int hashCode() {
            return Objects.hash(this.keySerializationClass, this.serializationIdentifier);
        }

        public String toString() {
            return this.keySerializationClass.getSimpleName() + ", object identifier: " + this.serializationIdentifier;
        }
    }

    private static class SerializerIndex {
        private final Class<?> keyClass;
        private final Class<? extends Serialization> keySerializationClass;

        private SerializerIndex(Class<?> keyClass, Class<? extends Serialization> keySerializationClass) {
            this.keyClass = keyClass;
            this.keySerializationClass = keySerializationClass;
        }

        public boolean equals(Object o) {
            if (!(o instanceof SerializerIndex)) {
                return false;
            }
            SerializerIndex other = (SerializerIndex)o;
            return other.keyClass.equals(this.keyClass) && other.keySerializationClass.equals(this.keySerializationClass);
        }

        public int hashCode() {
            return Objects.hash(this.keyClass, this.keySerializationClass);
        }

        public String toString() {
            return this.keyClass.getSimpleName() + " with serialization type: " + this.keySerializationClass.getSimpleName();
        }
    }
}

