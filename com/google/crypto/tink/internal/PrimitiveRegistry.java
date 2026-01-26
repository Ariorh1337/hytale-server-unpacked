/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.InsecureSecretKeyAccess;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.LegacyProtoKey;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.PrimitiveConstructor;
import com.google.crypto.tink.internal.PrimitiveWrapper;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrimitiveRegistry {
    private final Map<PrimitiveConstructorIndex, PrimitiveConstructor<?, ?>> primitiveConstructorMap;
    private final Map<Class<?>, PrimitiveWrapper<?, ?>> primitiveWrapperMap;
    private final boolean reparseLegacyKeys;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(PrimitiveRegistry registry) {
        return new Builder(registry);
    }

    private PrimitiveRegistry(Builder builder) {
        this.primitiveConstructorMap = new HashMap(builder.primitiveConstructorMap);
        this.primitiveWrapperMap = new HashMap(builder.primitiveWrapperMap);
        this.reparseLegacyKeys = builder.reparseLegacyKeys;
    }

    public <KeyT extends Key, PrimitiveT> PrimitiveT getPrimitive(KeyT key, Class<PrimitiveT> primitiveClass) throws GeneralSecurityException {
        if (this.reparseLegacyKeys && key instanceof LegacyProtoKey) {
            Key reparsedKey = MutableSerializationRegistry.globalInstance().parseKey(((LegacyProtoKey)key).getSerialization(InsecureSecretKeyAccess.get()), InsecureSecretKeyAccess.get());
            return this.getPrimitiveWithoutReparsing(reparsedKey, primitiveClass);
        }
        return this.getPrimitiveWithoutReparsing(key, primitiveClass);
    }

    private <KeyT extends Key, PrimitiveT> PrimitiveT getPrimitiveWithoutReparsing(KeyT key, Class<PrimitiveT> primitiveClass) throws GeneralSecurityException {
        PrimitiveConstructorIndex index = new PrimitiveConstructorIndex(key.getClass(), primitiveClass);
        if (!this.primitiveConstructorMap.containsKey(index)) {
            throw new GeneralSecurityException("No PrimitiveConstructor for " + index + " available, see https://developers.google.com/tink/faq/registration_errors");
        }
        PrimitiveConstructor<?, ?> primitiveConstructor = this.primitiveConstructorMap.get(index);
        return (PrimitiveT)primitiveConstructor.constructPrimitive(key);
    }

    private <InnerPrimitiveT, WrappedPrimitiveT> WrappedPrimitiveT wrapWithPrimitiveWrapper(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, PrimitiveWrapper<InnerPrimitiveT, WrappedPrimitiveT> wrapper) throws GeneralSecurityException {
        return wrapper.wrap(keysetHandle, annotations, (KeysetHandleInterface.Entry entry) -> this.getPrimitive(entry.getKey(), wrapper.getInputPrimitiveClass()));
    }

    public <WrappedPrimitiveT> WrappedPrimitiveT wrap(KeysetHandleInterface keysetHandle, MonitoringAnnotations annotations, Class<WrappedPrimitiveT> wrapperClassObject) throws GeneralSecurityException {
        if (!this.primitiveWrapperMap.containsKey(wrapperClassObject)) {
            throw new GeneralSecurityException("No wrapper found for " + wrapperClassObject);
        }
        PrimitiveWrapper<?, ?> wrapper = this.primitiveWrapperMap.get(wrapperClassObject);
        return (WrappedPrimitiveT)this.wrapWithPrimitiveWrapper(keysetHandle, annotations, wrapper);
    }

    public static final class Builder {
        private final Map<PrimitiveConstructorIndex, PrimitiveConstructor<?, ?>> primitiveConstructorMap;
        private final Map<Class<?>, PrimitiveWrapper<?, ?>> primitiveWrapperMap;
        private boolean reparseLegacyKeys = false;

        private Builder() {
            this.primitiveConstructorMap = new HashMap();
            this.primitiveWrapperMap = new HashMap();
        }

        private Builder(PrimitiveRegistry registry) {
            this.primitiveConstructorMap = new HashMap(registry.primitiveConstructorMap);
            this.primitiveWrapperMap = new HashMap(registry.primitiveWrapperMap);
        }

        @CanIgnoreReturnValue
        public <KeyT extends Key, PrimitiveT> Builder registerPrimitiveConstructor(PrimitiveConstructor<KeyT, PrimitiveT> primitiveConstructor) throws GeneralSecurityException {
            if (primitiveConstructor == null) {
                throw new NullPointerException("primitive constructor must be non-null");
            }
            PrimitiveConstructorIndex index = new PrimitiveConstructorIndex(primitiveConstructor.getKeyClass(), primitiveConstructor.getPrimitiveClass());
            if (this.primitiveConstructorMap.containsKey(index)) {
                PrimitiveConstructor<?, ?> existingConstructor = this.primitiveConstructorMap.get(index);
                if (!existingConstructor.equals(primitiveConstructor) || !primitiveConstructor.equals(existingConstructor)) {
                    throw new GeneralSecurityException("Attempt to register non-equal PrimitiveConstructor object for already existing object of type: " + index);
                }
            } else {
                this.primitiveConstructorMap.put(index, primitiveConstructor);
            }
            return this;
        }

        @CanIgnoreReturnValue
        public <InputPrimitiveT, WrapperPrimitiveT> Builder registerPrimitiveWrapper(PrimitiveWrapper<InputPrimitiveT, WrapperPrimitiveT> wrapper) throws GeneralSecurityException {
            if (wrapper == null) {
                throw new NullPointerException("wrapper must be non-null");
            }
            Class<WrapperPrimitiveT> wrapperClassObject = wrapper.getPrimitiveClass();
            if (this.primitiveWrapperMap.containsKey(wrapperClassObject)) {
                PrimitiveWrapper<?, ?> existingPrimitiveWrapper = this.primitiveWrapperMap.get(wrapperClassObject);
                if (!existingPrimitiveWrapper.equals(wrapper) || !wrapper.equals(existingPrimitiveWrapper)) {
                    throw new GeneralSecurityException("Attempt to register non-equal PrimitiveWrapper object or input class object for already existing object of type" + wrapperClassObject);
                }
            } else {
                this.primitiveWrapperMap.put(wrapperClassObject, wrapper);
            }
            return this;
        }

        @CanIgnoreReturnValue
        public Builder allowReparsingLegacyKeys() {
            this.reparseLegacyKeys = true;
            return this;
        }

        public PrimitiveRegistry build() {
            return new PrimitiveRegistry(this);
        }
    }

    private static final class PrimitiveConstructorIndex {
        private final Class<?> keyClass;
        private final Class<?> primitiveClass;

        private PrimitiveConstructorIndex(Class<?> keyClass, Class<?> primitiveClass) {
            this.keyClass = keyClass;
            this.primitiveClass = primitiveClass;
        }

        public boolean equals(Object o) {
            if (!(o instanceof PrimitiveConstructorIndex)) {
                return false;
            }
            PrimitiveConstructorIndex other = (PrimitiveConstructorIndex)o;
            return other.keyClass.equals(this.keyClass) && other.primitiveClass.equals(this.primitiveClass);
        }

        public int hashCode() {
            return Objects.hash(this.keyClass, this.primitiveClass);
        }

        public String toString() {
            return this.keyClass.getSimpleName() + " with primitive type: " + this.primitiveClass.getSimpleName();
        }
    }
}

