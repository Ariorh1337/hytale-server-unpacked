package org.bson.codecs;

import java.util.ArrayList;
import java.util.List;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecRegistry;

class IterableCodec implements Codec<Iterable>, OverridableUuidRepresentationCodec<Iterable> {
   private final CodecRegistry registry;
   private final BsonTypeCodecMap bsonTypeCodecMap;
   private final Transformer valueTransformer;
   private final UuidRepresentation uuidRepresentation;

   IterableCodec(CodecRegistry registry, BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this(registry, new BsonTypeCodecMap(Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap), registry), valueTransformer, UuidRepresentation.UNSPECIFIED);
   }

   private IterableCodec(CodecRegistry registry, BsonTypeCodecMap bsonTypeCodecMap, Transformer valueTransformer, UuidRepresentation uuidRepresentation) {
      this.registry = Assertions.notNull("registry", registry);
      this.bsonTypeCodecMap = bsonTypeCodecMap;
      this.valueTransformer = valueTransformer != null ? valueTransformer : objectToTransform -> objectToTransform;
      this.uuidRepresentation = uuidRepresentation;
   }

   @Override
   public Codec<Iterable> withUuidRepresentation(UuidRepresentation uuidRepresentation) {
      return new IterableCodec(this.registry, this.bsonTypeCodecMap, this.valueTransformer, uuidRepresentation);
   }

   public Iterable decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readStartArray();
      List<Object> list = new ArrayList<>();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         list.add(ContainerCodecHelper.readValue(reader, decoderContext, this.bsonTypeCodecMap, this.uuidRepresentation, this.registry, this.valueTransformer));
      }

      reader.readEndArray();
      return list;
   }

   public void encode(BsonWriter writer, Iterable value, EncoderContext encoderContext) {
      writer.writeStartArray();

      for (Object cur : value) {
         this.writeValue(writer, encoderContext, cur);
      }

      writer.writeEndArray();
   }

   @Override
   public Class<Iterable> getEncoderClass() {
      return Iterable.class;
   }

   private void writeValue(BsonWriter writer, EncoderContext encoderContext, Object value) {
      if (value == null) {
         writer.writeNull();
      } else {
         Codec codec = this.registry.get(value.getClass());
         encoderContext.encodeWithChildContext(codec, writer, value);
      }
   }
}
