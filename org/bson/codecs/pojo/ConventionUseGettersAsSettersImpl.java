/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.Collection;
import java.util.Map;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyAccessor;
import org.bson.codecs.pojo.PropertyAccessorImpl;
import org.bson.codecs.pojo.PropertyMetadata;
import org.bson.codecs.pojo.PropertyModelBuilder;

final class ConventionUseGettersAsSettersImpl
implements Convention {
    ConventionUseGettersAsSettersImpl() {
    }

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            if (!(propertyModelBuilder.getPropertyAccessor() instanceof PropertyAccessorImpl)) {
                throw new CodecConfigurationException(String.format("The USE_GETTER_AS_SETTER_CONVENTION is not compatible with propertyModelBuilder instance that have custom implementations of org.bson.codecs.pojo.PropertyAccessor: %s", propertyModelBuilder.getPropertyAccessor().getClass().getName()));
            }
            PropertyAccessorImpl defaultAccessor = (PropertyAccessorImpl)propertyModelBuilder.getPropertyAccessor();
            PropertyMetadata propertyMetaData = defaultAccessor.getPropertyMetadata();
            if (propertyMetaData.isDeserializable() || !propertyMetaData.isSerializable() || !this.isMapOrCollection(propertyMetaData.getTypeData().getType())) continue;
            this.setPropertyAccessor(propertyModelBuilder);
        }
    }

    private <T> boolean isMapOrCollection(Class<T> clazz) {
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }

    private <T> void setPropertyAccessor(PropertyModelBuilder<T> propertyModelBuilder) {
        propertyModelBuilder.propertyAccessor(new PrivatePropertyAccessor((PropertyAccessorImpl)propertyModelBuilder.getPropertyAccessor()));
    }

    private static final class PrivatePropertyAccessor<T>
    implements PropertyAccessor<T> {
        private final PropertyAccessorImpl<T> wrapped;

        private PrivatePropertyAccessor(PropertyAccessorImpl<T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public <S> T get(S instance) {
            return this.wrapped.get(instance);
        }

        @Override
        public <S> void set(S instance, T value) {
            if (value instanceof Collection) {
                this.mutateCollection(instance, (Collection)value);
            } else if (value instanceof Map) {
                this.mutateMap(instance, (Map)value);
            } else {
                this.throwCodecConfigurationException(String.format("Unexpected type: '%s'", value.getClass()), null);
            }
        }

        private <S> void mutateCollection(S instance, Collection value) {
            T originalCollection = this.get(instance);
            Collection collection = (Collection)originalCollection;
            if (collection == null) {
                this.throwCodecConfigurationException("The getter returned null.", null);
            } else if (!collection.isEmpty()) {
                this.throwCodecConfigurationException("The getter returned a non empty collection.", null);
            } else {
                try {
                    collection.addAll(value);
                }
                catch (Exception e) {
                    this.throwCodecConfigurationException("collection#addAll failed.", e);
                }
            }
        }

        private <S> void mutateMap(S instance, Map value) {
            T originalMap = this.get(instance);
            Map map = (Map)originalMap;
            if (map == null) {
                this.throwCodecConfigurationException("The getter returned null.", null);
            } else if (!map.isEmpty()) {
                this.throwCodecConfigurationException("The getter returned a non empty map.", null);
            } else {
                try {
                    map.putAll(value);
                }
                catch (Exception e) {
                    this.throwCodecConfigurationException("map#putAll failed.", e);
                }
            }
        }

        private void throwCodecConfigurationException(String reason, Exception cause) {
            throw new CodecConfigurationException(String.format("Cannot use getter in '%s' to set '%s'. %s", this.wrapped.getPropertyMetadata().getDeclaringClassName(), this.wrapped.getPropertyMetadata().getName(), reason), cause);
        }
    }
}

