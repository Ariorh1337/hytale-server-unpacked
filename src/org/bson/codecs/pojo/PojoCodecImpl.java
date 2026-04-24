package org.bson.codecs.pojo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bson.BsonDocument;
import org.bson.BsonDocumentReader;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonReaderMark;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.BsonWriter;
import org.bson.codecs.BsonValueCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.diagnostics.Logger;
import org.bson.diagnostics.Loggers;

final class PojoCodecImpl<T> extends PojoCodec<T> {
   private static final Logger LOGGER = Loggers.getLogger("PojoCodec");
   private static final Codec<BsonValue> BSON_VALUE_CODEC = new BsonValueCodec();
   private final ClassModel<T> classModel;
   private final CodecRegistry registry;
   private final PropertyCodecRegistry propertyCodecRegistry;
   private final DiscriminatorLookup discriminatorLookup;

   PojoCodecImpl(
      ClassModel<T> classModel, CodecRegistry codecRegistry, List<PropertyCodecProvider> propertyCodecProviders, DiscriminatorLookup discriminatorLookup
   ) {
      this.classModel = classModel;
      this.registry = codecRegistry;
      this.discriminatorLookup = discriminatorLookup;
      this.propertyCodecRegistry = new PropertyCodecRegistryImpl(this, this.registry, propertyCodecProviders);
      this.specialize();
   }

   PojoCodecImpl(ClassModel<T> classModel, CodecRegistry codecRegistry, PropertyCodecRegistry propertyCodecRegistry, DiscriminatorLookup discriminatorLookup) {
      this.classModel = classModel;
      this.registry = codecRegistry;
      this.discriminatorLookup = discriminatorLookup;
      this.propertyCodecRegistry = propertyCodecRegistry;
      this.specialize();
   }

   @Override
   public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
      if (this.areEquivalentTypes(value.getClass(), this.classModel.getType())) {
         writer.writeStartDocument();
         this.encodeIdProperty(writer, value, encoderContext, this.classModel.getIdPropertyModelHolder());
         this.encodeDiscriminatorProperty(writer);

         for (PropertyModel<?> propertyModel : this.classModel.getPropertyModels()) {
            if (!this.idProperty(propertyModel)) {
               this.encodeProperty(writer, value, encoderContext, propertyModel);
            }
         }

         writer.writeEndDocument();
      } else {
         ((Codec<T>)this.registry.get(value.getClass())).encode(writer, value, encoderContext);
      }
   }

   @Override
   public T decode(BsonReader reader, DecoderContext decoderContext) {
      if (decoderContext.hasCheckedDiscriminator()) {
         InstanceCreator<T> instanceCreator = this.classModel.getInstanceCreator();
         this.decodeProperties(reader, decoderContext, instanceCreator);
         return instanceCreator.getInstance();
      } else {
         return getCodecFromDocument(
               reader,
               this.classModel.useDiscriminator(),
               this.classModel.getDiscriminatorKey(),
               this.registry,
               this.discriminatorLookup,
               this,
               this.classModel.getName()
            )
            .decode(reader, DecoderContext.builder().checkedDiscriminator(true).build());
      }
   }

   @Override
   public Class<T> getEncoderClass() {
      return this.classModel.getType();
   }

   @Override
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
               } catch (Exception var7) {
               }
            }

            this.encodeValue(writer, encoderContext, propertyModelHolder.getPropertyModel(), id);
         }
      }
   }

   private boolean idProperty(PropertyModel<?> propertyModel) {
      return propertyModel.equals(this.classModel.getIdPropertyModel());
   }

   private void encodeDiscriminatorProperty(BsonWriter writer) {
      if (this.classModel.useDiscriminator()) {
         writer.writeString(this.classModel.getDiscriminatorKey(), this.classModel.getDiscriminator());
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
         try {
            if (propertyModel.getPropertySerialization().inline()) {
               if (propertyValue != null) {
                  new BsonDocumentWrapper<>(propertyValue, propertyModel.getCachedCodec()).forEach((k, v) -> {
                     writer.writeName(k);
                     encoderContext.encodeWithChildContext(this.registry.get((Class<T>)v.getClass()), writer, (T)v);
                  });
               }
            } else {
               writer.writeName(propertyModel.getReadName());
               if (propertyValue == null) {
                  writer.writeNull();
               } else {
                  encoderContext.encodeWithChildContext(propertyModel.getCachedCodec(), writer, propertyValue);
               }
            }
         } catch (CodecConfigurationException e) {
            throw new CodecConfigurationException(
               String.format("Failed to encode '%s'. Encoding '%s' errored with: %s", this.classModel.getName(), propertyModel.getReadName(), e.getMessage()),
               e
            );
         }
      }
   }

   private void decodeProperties(BsonReader reader, DecoderContext decoderContext, InstanceCreator<T> instanceCreator) {
      PropertyModel<?> inlineElementsPropertyModel = this.classModel
         .getPropertyModels()
         .stream()
         .filter(p -> p.getPropertySerialization().inline())
         .findFirst()
         .orElse(null);
      BsonDocument extraElements = inlineElementsPropertyModel == null ? null : new BsonDocument();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         String name = reader.readName();
         if (this.classModel.useDiscriminator() && this.classModel.getDiscriminatorKey().equals(name)) {
            reader.readString();
         } else {
            this.decodePropertyModel(reader, decoderContext, instanceCreator, name, this.getPropertyModelByWriteName(this.classModel, name), extraElements);
         }
      }

      reader.readEndDocument();
      this.setPropertyValueBsonExtraElements(instanceCreator, extraElements, inlineElementsPropertyModel);
   }

   private <S> void decodePropertyModel(
      BsonReader reader,
      DecoderContext decoderContext,
      InstanceCreator<T> instanceCreator,
      String name,
      PropertyModel<S> propertyModel,
      @Nullable BsonDocument extraElements
   ) {
      if (propertyModel != null) {
         this.setPropertyValue(instanceCreator, () -> {
            S value = null;
            if (reader.getCurrentBsonType() == BsonType.NULL) {
               reader.readNull();
            } else {
               Codec<S> codec = propertyModel.getCachedCodec();
               if (codec == null) {
                  throw new CodecConfigurationException(String.format("Missing codec in '%s' for '%s'", this.classModel.getName(), propertyModel.getName()));
               }

               value = decoderContext.decodeWithChildContext(codec, reader);
            }

            return value;
         }, propertyModel);
      } else if (extraElements == null) {
         if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(String.format("Found property not present in the ClassModel: %s", name));
         }

         reader.skipValue();
      } else {
         try {
            extraElements.append(name, decoderContext.decodeWithChildContext(BSON_VALUE_CODEC, reader));
         } catch (CodecConfigurationException e) {
            throw new CodecConfigurationException(
               String.format("Failed to decode '%s'. Decoding '%s' errored with: %s", this.classModel.getName(), name, e.getMessage()), e
            );
         }
      }
   }

   private <S> void setPropertyValue(InstanceCreator<T> instanceCreator, Supplier<S> valueSupplier, PropertyModel<S> propertyModel) {
      try {
         instanceCreator.set(valueSupplier.get(), propertyModel);
      } catch (BsonInvalidOperationException | CodecConfigurationException e) {
         throw new CodecConfigurationException(
            String.format("Failed to decode '%s'. Decoding '%s' errored with: %s", this.classModel.getName(), propertyModel.getName(), e.getMessage()), e
         );
      }
   }

   private <S> void setPropertyValueBsonExtraElements(
      InstanceCreator<T> instanceCreator, @Nullable BsonDocument extraElements, PropertyModel<S> inlineElementsPropertyModel
   ) {
      if (extraElements != null && !extraElements.isEmpty() && inlineElementsPropertyModel != null && inlineElementsPropertyModel.isWritable()) {
         this.setPropertyValue(
            instanceCreator,
            () -> inlineElementsPropertyModel.getCachedCodec().decode(new BsonDocumentReader(extraElements), DecoderContext.builder().build()),
            inlineElementsPropertyModel
         );
      }
   }

   private void specialize() {
      this.classModel.getPropertyModels().forEach(this::cachePropertyModelCodec);
   }

   private <S> void cachePropertyModelCodec(PropertyModel<S> propertyModel) {
      if (propertyModel.getCachedCodec() == null) {
         Codec<S> codec = propertyModel.getCodec() != null
            ? propertyModel.getCodec()
            : new LazyPropertyModelCodec<>(propertyModel, this.registry, this.propertyCodecRegistry);
         propertyModel.cachedCodec(codec);
      }
   }

   private <S, V> boolean areEquivalentTypes(Class<S> t1, Class<V> t2) {
      if (t1.equals(t2)) {
         return true;
      } else {
         return Collection.class.isAssignableFrom(t1) && Collection.class.isAssignableFrom(t2)
            ? true
            : Map.class.isAssignableFrom(t1) && Map.class.isAssignableFrom(t2);
      }
   }

   @Nullable
   static <C> Codec<C> getCodecFromDocument(
      BsonReader reader,
      boolean useDiscriminator,
      String discriminatorKey,
      CodecRegistry registry,
      DiscriminatorLookup discriminatorLookup,
      @Nullable Codec<C> defaultCodec,
      String simpleClassName
   ) {
      Codec<C> codec = defaultCodec;
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
                  if (codec == null || !codec.getEncoderClass().equals(discriminatorClass)) {
                     codec = (Codec<C>)registry.get(discriminatorClass);
                  }
               } catch (Exception e) {
                  throw new CodecConfigurationException(String.format("Failed to decode '%s'. Decoding errored with: %s", simpleClassName, e.getMessage()), e);
               }
            } else {
               reader.skipValue();
            }
         }

         mark.reset();
      }

      return codec;
   }

   private PropertyModel<?> getPropertyModelByWriteName(ClassModel<T> classModel, String readName) {
      for (PropertyModel<?> propertyModel : classModel.getPropertyModels()) {
         if (propertyModel.isWritable() && propertyModel.getWriteName().equals(readName)) {
            return propertyModel;
         }
      }

      return null;
   }

   @Override
   DiscriminatorLookup getDiscriminatorLookup() {
      return this.discriminatorLookup;
   }
}
