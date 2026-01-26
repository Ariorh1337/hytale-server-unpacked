/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.PropertyAccessor;
import org.bson.codecs.pojo.PropertyMetadata;

final class PropertyAccessorImpl<T>
implements PropertyAccessor<T> {
    private final PropertyMetadata<T> propertyMetadata;

    PropertyAccessorImpl(PropertyMetadata<T> propertyMetadata) {
        this.propertyMetadata = propertyMetadata;
    }

    @Override
    public <S> T get(S instance) {
        try {
            if (this.propertyMetadata.isSerializable()) {
                if (this.propertyMetadata.getGetter() != null) {
                    return (T)this.propertyMetadata.getGetter().invoke(instance, new Object[0]);
                }
                return (T)this.propertyMetadata.getField().get(instance);
            }
            throw this.getError(null);
        }
        catch (Exception e) {
            throw this.getError(e);
        }
    }

    @Override
    public <S> void set(S instance, T value) {
        try {
            if (this.propertyMetadata.isDeserializable()) {
                if (this.propertyMetadata.getSetter() != null) {
                    this.propertyMetadata.getSetter().invoke(instance, value);
                } else {
                    this.propertyMetadata.getField().set(instance, value);
                }
            }
        }
        catch (Exception e) {
            throw this.setError(e);
        }
    }

    PropertyMetadata<T> getPropertyMetadata() {
        return this.propertyMetadata;
    }

    private CodecConfigurationException getError(Exception cause) {
        return new CodecConfigurationException(String.format("Unable to get value for property '%s' in %s", this.propertyMetadata.getName(), this.propertyMetadata.getDeclaringClassName()), cause);
    }

    private CodecConfigurationException setError(Exception cause) {
        return new CodecConfigurationException(String.format("Unable to set value for property '%s' in %s", this.propertyMetadata.getName(), this.propertyMetadata.getDeclaringClassName()), cause);
    }
}

