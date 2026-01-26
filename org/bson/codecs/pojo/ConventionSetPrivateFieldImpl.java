/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.lang.reflect.Modifier;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.pojo.ClassModelBuilder;
import org.bson.codecs.pojo.Convention;
import org.bson.codecs.pojo.PropertyAccessor;
import org.bson.codecs.pojo.PropertyAccessorImpl;
import org.bson.codecs.pojo.PropertyMetadata;
import org.bson.codecs.pojo.PropertyModelBuilder;

final class ConventionSetPrivateFieldImpl
implements Convention {
    ConventionSetPrivateFieldImpl() {
    }

    @Override
    public void apply(ClassModelBuilder<?> classModelBuilder) {
        for (PropertyModelBuilder<?> propertyModelBuilder : classModelBuilder.getPropertyModelBuilders()) {
            if (!(propertyModelBuilder.getPropertyAccessor() instanceof PropertyAccessorImpl)) {
                throw new CodecConfigurationException(String.format("The SET_PRIVATE_FIELDS_CONVENTION is not compatible with propertyModelBuilder instance that have custom implementations of org.bson.codecs.pojo.PropertyAccessor: %s", propertyModelBuilder.getPropertyAccessor().getClass().getName()));
            }
            PropertyAccessorImpl defaultAccessor = (PropertyAccessorImpl)propertyModelBuilder.getPropertyAccessor();
            PropertyMetadata propertyMetaData = defaultAccessor.getPropertyMetadata();
            if (propertyMetaData.isDeserializable() || propertyMetaData.getField() == null || !Modifier.isPrivate(propertyMetaData.getField().getModifiers())) continue;
            this.setPropertyAccessor(propertyModelBuilder);
        }
    }

    private <T> void setPropertyAccessor(PropertyModelBuilder<T> propertyModelBuilder) {
        propertyModelBuilder.propertyAccessor(new PrivatePropertyAccessor((PropertyAccessorImpl)propertyModelBuilder.getPropertyAccessor()));
    }

    private static final class PrivatePropertyAccessor<T>
    implements PropertyAccessor<T> {
        private final PropertyAccessorImpl<T> wrapped;

        private PrivatePropertyAccessor(PropertyAccessorImpl<T> wrapped) {
            this.wrapped = wrapped;
            try {
                wrapped.getPropertyMetadata().getField().setAccessible(true);
            }
            catch (Exception e) {
                throw new CodecConfigurationException(String.format("Unable to make private field accessible '%s' in %s", wrapped.getPropertyMetadata().getName(), wrapped.getPropertyMetadata().getDeclaringClassName()), e);
            }
        }

        @Override
        public <S> T get(S instance) {
            return this.wrapped.get(instance);
        }

        @Override
        public <S> void set(S instance, T value) {
            try {
                this.wrapped.getPropertyMetadata().getField().set(instance, value);
            }
            catch (Exception e) {
                throw new CodecConfigurationException(String.format("Unable to set value for property '%s' in %s", this.wrapped.getPropertyMetadata().getName(), this.wrapped.getPropertyMetadata().getDeclaringClassName()), e);
            }
        }
    }
}

