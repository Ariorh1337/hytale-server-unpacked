package org.bson.codecs.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.RepresentationConfigurable;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;

class LazyPropertyModelCodec<T> implements Codec<T> {
   private final PropertyModel<T> propertyModel;
   private final CodecRegistry registry;
   private final PropertyCodecRegistry propertyCodecRegistry;
   private final Lock codecLock = new ReentrantLock();
   private volatile Codec<T> codec;

   LazyPropertyModelCodec(PropertyModel<T> propertyModel, CodecRegistry registry, PropertyCodecRegistry propertyCodecRegistry) {
      this.propertyModel = propertyModel;
      this.registry = registry;
      this.propertyCodecRegistry = propertyCodecRegistry;
   }

   @Override
   public T decode(BsonReader reader, DecoderContext decoderContext) {
      return this.getPropertyModelCodec().decode(reader, decoderContext);
   }

   @Override
   public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
      this.getPropertyModelCodec().encode(writer, value, encoderContext);
   }

   @Override
   public Class<T> getEncoderClass() {
      return this.propertyModel.getTypeData().getType();
   }

   private Codec<T> getPropertyModelCodec() {
      Codec<T> codec = this.codec;
      if (codec == null) {
         this.codecLock.lock();

         try {
            codec = this.codec;
            if (codec == null) {
               codec = this.createCodec();
               this.codec = codec;
            }
         } finally {
            this.codecLock.unlock();
         }
      }

      return codec;
   }

   private Codec<T> createCodec() {
      Codec<T> localCodec = this.getCodecFromPropertyRegistry(this.propertyModel);
      if (localCodec instanceof PojoCodec) {
         PojoCodec<T> pojoCodec = (PojoCodec<T>)localCodec;
         ClassModel<T> specialized = this.getSpecializedClassModel(pojoCodec.getClassModel(), this.propertyModel);
         localCodec = new PojoCodecImpl<>(specialized, this.registry, this.propertyCodecRegistry, pojoCodec.getDiscriminatorLookup());
      }

      return localCodec;
   }

   private Codec<T> getCodecFromPropertyRegistry(PropertyModel<T> propertyModel) {
      Codec<T> localCodec;
      try {
         localCodec = this.propertyCodecRegistry.get(propertyModel.getTypeData());
      } catch (CodecConfigurationException e) {
         return new LazyMissingCodec<>(propertyModel.getTypeData().getType(), e);
      }

      if (localCodec == null) {
         localCodec = new LazyMissingCodec<>(
            propertyModel.getTypeData().getType(), new CodecConfigurationException("Unexpected missing codec for: " + propertyModel.getName())
         );
      }

      BsonType representation = propertyModel.getBsonRepresentation();
      if (representation != null) {
         if (localCodec instanceof RepresentationConfigurable) {
            return ((RepresentationConfigurable)localCodec).withRepresentation(representation);
         } else {
            throw new CodecConfigurationException("Codec must implement RepresentationConfigurable to support BsonRepresentation");
         }
      } else {
         return localCodec;
      }
   }

   private <V> ClassModel<T> getSpecializedClassModel(ClassModel<T> clazzModel, PropertyModel<V> propertyModel) {
      boolean useDiscriminator = propertyModel.useDiscriminator() == null ? clazzModel.useDiscriminator() : propertyModel.useDiscriminator();
      boolean validDiscriminator = clazzModel.getDiscriminatorKey() != null && clazzModel.getDiscriminator() != null;
      boolean changeTheDiscriminator = useDiscriminator != clazzModel.useDiscriminator() && validDiscriminator;
      if (propertyModel.getTypeData().getTypeParameters().isEmpty() && !changeTheDiscriminator) {
         return clazzModel;
      }

      ArrayList<PropertyModel<?>> concretePropertyModels = new ArrayList<>(clazzModel.getPropertyModels());
      PropertyModel<?> concreteIdProperty = clazzModel.getIdPropertyModel();
      List<TypeData<?>> propertyTypeParameters = propertyModel.getTypeData().getTypeParameters();

      for (int i = 0; i < concretePropertyModels.size(); i++) {
         PropertyModel<?> model = concretePropertyModels.get(i);
         String propertyName = model.getName();
         TypeParameterMap typeParameterMap = clazzModel.getPropertyNameToTypeParameterMap().get(propertyName);
         if (typeParameterMap.hasTypeParameters()) {
            PropertyModel<?> concretePropertyModel = this.getSpecializedPropertyModel(model, propertyTypeParameters, typeParameterMap);
            concretePropertyModels.set(i, concretePropertyModel);
            if (concreteIdProperty != null && concreteIdProperty.getName().equals(propertyName)) {
               concreteIdProperty = concretePropertyModel;
            }
         }
      }

      boolean discriminatorEnabled = changeTheDiscriminator ? propertyModel.useDiscriminator() : clazzModel.useDiscriminator();
      return new ClassModel<>(
         clazzModel.getType(),
         clazzModel.getPropertyNameToTypeParameterMap(),
         clazzModel.getInstanceCreatorFactory(),
         discriminatorEnabled,
         clazzModel.getDiscriminatorKey(),
         clazzModel.getDiscriminator(),
         IdPropertyModelHolder.create(clazzModel, concreteIdProperty),
         concretePropertyModels
      );
   }

   private <V> PropertyModel<V> getSpecializedPropertyModel(
      PropertyModel<V> propertyModel, List<TypeData<?>> propertyTypeParameters, TypeParameterMap typeParameterMap
   ) {
      TypeData<V> specializedPropertyType = PojoSpecializationHelper.specializeTypeData(propertyModel.getTypeData(), propertyTypeParameters, typeParameterMap);
      return propertyModel.getTypeData().equals(specializedPropertyType)
         ? propertyModel
         : new PropertyModel<>(
            propertyModel.getName(),
            propertyModel.getReadName(),
            propertyModel.getWriteName(),
            specializedPropertyType,
            null,
            propertyModel.getPropertySerialization(),
            propertyModel.useDiscriminator(),
            propertyModel.getPropertyAccessor(),
            propertyModel.getError(),
            propertyModel.getBsonRepresentation()
         );
   }

   static final class NeedSpecializationCodec<T> extends PojoCodec<T> {
      private final ClassModel<T> classModel;
      private final DiscriminatorLookup discriminatorLookup;
      private final CodecRegistry codecRegistry;

      NeedSpecializationCodec(ClassModel<T> classModel, DiscriminatorLookup discriminatorLookup, CodecRegistry codecRegistry) {
         this.classModel = classModel;
         this.discriminatorLookup = discriminatorLookup;
         this.codecRegistry = codecRegistry;
      }

      @Override
      public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
         if (value.getClass().equals(this.classModel.getType())) {
            throw this.exception();
         }

         this.tryEncode(this.codecRegistry.get(value.getClass()), writer, value, encoderContext);
      }

      @Override
      public T decode(BsonReader reader, DecoderContext decoderContext) {
         return this.tryDecode(reader, decoderContext);
      }

      private <A> void tryEncode(Codec<A> codec, BsonWriter writer, T value, EncoderContext encoderContext) {
         try {
            codec.encode(writer, (A)value, encoderContext);
         } catch (Exception e) {
            throw this.exception();
         }
      }

      public T tryDecode(BsonReader reader, DecoderContext decoderContext) {
         Codec<T> codec = PojoCodecImpl.getCodecFromDocument(
            reader,
            this.classModel.useDiscriminator(),
            this.classModel.getDiscriminatorKey(),
            this.codecRegistry,
            this.discriminatorLookup,
            null,
            this.classModel.getName()
         );
         if (codec != null) {
            return codec.decode(reader, decoderContext);
         } else {
            throw this.exception();
         }
      }

      @Override
      public Class<T> getEncoderClass() {
         return this.classModel.getType();
      }

      private CodecConfigurationException exception() {
         return new CodecConfigurationException(
            String.format(
               "%s contains generic types that have not been specialised.%nTop level classes with generic types are not supported by the PojoCodec.",
               this.classModel.getName()
            )
         );
      }

      @Override
      ClassModel<T> getClassModel() {
         return this.classModel;
      }

      @Override
      DiscriminatorLookup getDiscriminatorLookup() {
         return this.discriminatorLookup;
      }
   }
}
