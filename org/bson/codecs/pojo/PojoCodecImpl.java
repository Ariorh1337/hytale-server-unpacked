/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonReaderMark;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.DiscriminatorLookup;
import org.bson.codecs.pojo.IdPropertyModelHolder;
import org.bson.codecs.pojo.InstanceCreator;
import org.bson.codecs.pojo.LazyPropertyModelCodec;
import org.bson.codecs.pojo.PojoCodec;
import org.bson.codecs.pojo.PropertyCodecProvider;
import org.bson.codecs.pojo.PropertyCodecRegistry;
import org.bson.codecs.pojo.PropertyCodecRegistryImpl;
import org.bson.codecs.pojo.PropertyModel;
import org.bson.codecs.pojo.TypeParameterMap;
import org.bson.diagnostics.Logger;
import org.bson.diagnostics.Loggers;

final class PojoCodecImpl<T>
extends PojoCodec<T> {
    private static final Logger LOGGER = Loggers.getLogger("PojoCodec");
    private final ClassModel<T> classModel;
    private final CodecRegistry registry;
    private final PropertyCodecRegistry propertyCodecRegistry;
    private final DiscriminatorLookup discriminatorLookup;
    private final boolean specialized;

    PojoCodecImpl(ClassModel<T> classModel, CodecRegistry codecRegistry, List<PropertyCodecProvider> propertyCodecProviders, DiscriminatorLookup discriminatorLookup) {
        this.classModel = classModel;
        this.registry = codecRegistry;
        this.discriminatorLookup = discriminatorLookup;
        this.propertyCodecRegistry = new PropertyCodecRegistryImpl(this, this.registry, propertyCodecProviders);
        this.specialized = PojoCodecImpl.shouldSpecialize(classModel);
        this.specialize();
    }

    PojoCodecImpl(ClassModel<T> classModel, CodecRegistry codecRegistry, PropertyCodecRegistry propertyCodecRegistry, DiscriminatorLookup discriminatorLookup, boolean specialized) {
        this.classModel = classModel;
        this.registry = codecRegistry;
        this.discriminatorLookup = discriminatorLookup;
        this.propertyCodecRegistry = propertyCodecRegistry;
        this.specialized = specialized;
        this.specialize();
    }

    @Override
    public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
        if (!this.specialized) {
            throw new CodecConfigurationException(String.format("%s contains generic types that have not been specialised.%nTop level classes with generic types are not supported by the PojoCodec.", this.classModel.getName()));
        }
        if (this.areEquivalentTypes(value.getClass(), this.classModel.getType())) {
            writer.writeStartDocument();
            this.encodeIdProperty(writer, value, encoderContext, this.classModel.getIdPropertyModelHolder());
            if (this.classModel.useDiscriminator()) {
                writer.writeString(this.classModel.getDiscriminatorKey(), this.classModel.getDiscriminator());
            }
            for (PropertyModel<?> propertyModel : this.classModel.getPropertyModels()) {
                if (propertyModel.equals(this.classModel.getIdPropertyModel())) continue;
                this.encodeProperty(writer, value, encoderContext, propertyModel);
            }
            writer.writeEndDocument();
        } else {
            this.registry.get(value.getClass()).encode(writer, value, encoderContext);
        }
    }

    @Override
    public T decode(BsonReader reader, DecoderContext decoderContext) {
        if (decoderContext.hasCheckedDiscriminator()) {
            if (!this.specialized) {
                throw new CodecConfigurationException(String.format("%s contains generic types that have not been specialised.%nTop level classes with generic types are not supported by the PojoCodec.", this.classModel.getName()));
            }
            InstanceCreator<T> instanceCreator = this.classModel.getInstanceCreator();
            this.decodeProperties(reader, decoderContext, instanceCreator);
            return instanceCreator.getInstance();
        }
        return this.getCodecFromDocument(reader, this.classModel.useDiscriminator(), this.classModel.getDiscriminatorKey(), this.registry, this.discriminatorLookup, this).decode(reader, DecoderContext.builder().checkedDiscriminator(true).build());
    }

    @Override
    public Class<T> getEncoderClass() {
        return this.classModel.getType();
    }

    public String toString() {
        return String.format("PojoCodec<%s>", this.classModel);
    }

    @Override
    ClassModel<T> getClassModel() {
        return this.classModel;
    }

    private <S> void encodeIdProperty(BsonWriter writer, T instance, EncoderContext encoderContext, IdPropertyModelHolder<S> propertyModelHolder) {
        if (propertyModelHolder.getPropertyModel() != null) {
            if (propertyModelHolder.getIdGenerator() == null) {
                this.encodeProperty(writer, instance, encoderContext, propertyModelHolder.getPropertyModel());
            } else {
                S id = propertyModelHolder.getPropertyModel().getPropertyAccessor().get(instance);
                if (id == null && encoderContext.isEncodingCollectibleDocument()) {
                    id = propertyModelHolder.getIdGenerator().generate();
                    try {
                        propertyModelHolder.getPropertyModel().getPropertyAccessor().set(instance, id);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
                this.encodeValue(writer, encoderContext, propertyModelHolder.getPropertyModel(), id);
            }
        }
    }

    private <S> void encodeProperty(BsonWriter writer, T instance, EncoderContext encoderContext, PropertyModel<S> propertyModel) {
        if (propertyModel != null && propertyModel.isReadable()) {
            S propertyValue = propertyModel.getPropertyAccessor().get(instance);
            this.encodeValue(writer, encoderContext, propertyModel, propertyValue);
        }
    }

    private <S> void encodeValue(BsonWriter writer, EncoderContext encoderContext, PropertyModel<S> propertyModel, S propertyValue) {
        if (propertyModel.shouldSerialize(propertyValue)) {
            writer.writeName(propertyModel.getReadName());
            if (propertyValue == null) {
                writer.writeNull();
            } else {
                try {
                    encoderContext.encodeWithChildContext(propertyModel.getCachedCodec(), writer, propertyValue);
                }
                catch (CodecConfigurationException e) {
                    throw new CodecConfigurationException(String.format("Failed to encode '%s'. Encoding '%s' errored with: %s", this.classModel.getName(), propertyModel.getReadName(), e.getMessage()), e);
                }
            }
        }
    }

    private void decodeProperties(BsonReader reader, DecoderContext decoderContext, InstanceCreator<T> instanceCreator) {
        reader.readStartDocument();
        while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
            String name = reader.readName();
            if (this.classModel.useDiscriminator() && this.classModel.getDiscriminatorKey().equals(name)) {
                reader.readString();
                continue;
            }
            this.decodePropertyModel(reader, decoderContext, instanceCreator, name, this.getPropertyModelByWriteName(this.classModel, name));
        }
        reader.readEndDocument();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private <S> void decodePropertyModel(BsonReader reader, DecoderContext decoderContext, InstanceCreator<T> instanceCreator, String name, PropertyModel<S> propertyModel) {
        if (propertyModel != null) {
            try {
                Object value = null;
                if (reader.getCurrentBsonType() == BsonType.NULL) {
                    reader.readNull();
                } else {
                    Codec<S> codec = propertyModel.getCachedCodec();
                    if (codec == null) {
                        throw new CodecConfigurationException(String.format("Missing codec in '%s' for '%s'", this.classModel.getName(), propertyModel.getName()));
                    }
                    value = decoderContext.decodeWithChildContext(codec, reader);
                }
                if (!propertyModel.isWritable()) return;
                instanceCreator.set(value, propertyModel);
                return;
            }
            catch (BsonInvalidOperationException | CodecConfigurationException e) {
                throw new CodecConfigurationException(String.format("Failed to decode '%s'. Decoding '%s' errored with: %s", this.classModel.getName(), name, e.getMessage()), e);
            }
        } else {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(String.format("Found property not present in the ClassModel: %s", name));
            }
            reader.skipValue();
        }
    }

    private void specialize() {
        if (this.specialized) {
            this.classModel.getPropertyModels().forEach(this::cachePropertyModelCodec);
        }
    }

    private <S> void cachePropertyModelCodec(PropertyModel<S> propertyModel) {
        if (propertyModel.getCachedCodec() == null) {
            LazyPropertyModelCodec<S> codec = propertyModel.getCodec() != null ? propertyModel.getCodec() : new LazyPropertyModelCodec<S>(propertyModel, this.registry, this.propertyCodecRegistry, this.discriminatorLookup);
            propertyModel.cachedCodec(codec);
        }
    }

    private <S, V> boolean areEquivalentTypes(Class<S> t1, Class<V> t2) {
        if (t1.equals(t2)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(t1) && Collection.class.isAssignableFrom(t2)) {
            return true;
        }
        return Map.class.isAssignableFrom(t1) && Map.class.isAssignableFrom(t2);
    }

    private Codec<T> getCodecFromDocument(BsonReader reader, boolean useDiscriminator, String discriminatorKey, CodecRegistry registry, DiscriminatorLookup discriminatorLookup, Codec<T> defaultCodec) {
        Codec<Object> codec = defaultCodec;
        if (useDiscriminator) {
            BsonReaderMark mark = reader.getMark();
            reader.readStartDocument();
            boolean discriminatorKeyFound = false;
            while (!discriminatorKeyFound && reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                String name = reader.readName();
                if (discriminatorKey.equals(name)) {
                    discriminatorKeyFound = true;
                    try {
                        Class<?> discriminatorClass = discriminatorLookup.lookup(reader.readString());
                        if (codec.getEncoderClass().equals(discriminatorClass)) continue;
                        codec = registry.get(discriminatorClass);
                        continue;
                    }
                    catch (Exception e) {
                        throw new CodecConfigurationException(String.format("Failed to decode '%s'. Decoding errored with: %s", this.classModel.getName(), e.getMessage()), e);
                    }
                }
                reader.skipValue();
            }
            mark.reset();
        }
        return codec;
    }

    private PropertyModel<?> getPropertyModelByWriteName(ClassModel<T> classModel, String readName) {
        for (PropertyModel<?> propertyModel : classModel.getPropertyModels()) {
            if (!propertyModel.isWritable() || !propertyModel.getWriteName().equals(readName)) continue;
            return propertyModel;
        }
        return null;
    }

    private static <T> boolean shouldSpecialize(ClassModel<T> classModel) {
        if (!classModel.hasTypeParameters()) {
            return true;
        }
        for (Map.Entry<String, TypeParameterMap> entry : classModel.getPropertyNameToTypeParameterMap().entrySet()) {
            TypeParameterMap typeParameterMap = entry.getValue();
            PropertyModel<?> propertyModel = classModel.getPropertyModel(entry.getKey());
            if (!typeParameterMap.hasTypeParameters() || propertyModel != null && propertyModel.getCodec() != null) continue;
            return false;
        }
        return true;
    }

    @Override
    DiscriminatorLookup getDiscriminatorLookup() {
        return this.discriminatorLookup;
    }
}

