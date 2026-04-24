package org.bson.codecs;

import java.util.Map;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;

final class MapCodec<M extends Map<String, Object>> extends AbstractMapCodec<Object, M> implements OverridableUuidRepresentationCodec<M> {
   private final BsonTypeCodecMap bsonTypeCodecMap;
   private final CodecRegistry registry;
   private final Transformer valueTransformer;
   private final UuidRepresentation uuidRepresentation;

   MapCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer, Class<M> clazz) {
      this(
         registry,
         new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry),
         valueTransformer,
         UuidRepresentation.UNSPECIFIED,
         clazz
      );
   }

   private MapCodec(
      CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer, UuidRepresentation uuidRepresentation, Class<M> clazz
   ) {
      super(clazz);
      this.registry = Assertions.notNull("registry", registry);
      this.bsonTypeCodecMap = bsonTypeCodecMap;
      this.valueTransformer = valueTransformer != null ? valueTransformer : value -> value;
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public Codec<M> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return this.uuidRepresentation.equals(uuidRepresentation)
         ? this
         : new MapCodec<>(this.registry, this.bsonTypeCodecMap, this.valueTransformer, uuidRepresentation, this.getEncoderClass());
   }

   @Override
   Object readValue(BsonReader reader, DecoderContext decoderContext) {
      return ContainerCodecHelper.readValue(reader, decoderContext, this.bsonTypeCodecMap, this.uuidRepresentation, this.registry, this.valueTransformer);
   }

   @Override
   void writeValue(BsonWriter writer, Object value, EncoderContext encoderContext) {
      Codec codec = this.registry.get(value.getClass());
      encoderContext.encodeWithChildContext(codec, writer, value);
   }
}
