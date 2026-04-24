package org.bson.codecs;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.UUID;
import org.bson.BinaryVector;
import org.bson.BsonBinarySubType;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.Transformer;
import org.bson.UuidRepresentation;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.internal.UuidHelper;

final class ContainerCodecHelper {
   static Object readValue(
      BsonReader reader,
      DecoderContext decoderContext,
      BsonTypeCodecMap bsonTypeCodecMap,
      UuidRepresentation uuidRepresentation,
      CodecRegistry registry,
      Transformer valueTransformer
   ) {
      BsonType bsonType = reader.getCurrentBsonType();
      if (bsonType == BsonType.NULL) {
         reader.readNull();
         return null;
      }

      Codec<?> currentCodec = bsonTypeCodecMap.get(bsonType);
      if (bsonType == BsonType.BINARY) {
         byte binarySubType = reader.peekBinarySubType();
         currentCodec = getBinarySubTypeCodec(reader, uuidRepresentation, registry, binarySubType, currentCodec);
      }

      return valueTransformer.transform(currentCodec.decode(reader, decoderContext));
   }

   private static Codec<?> getBinarySubTypeCodec(
      BsonReader reader, UuidRepresentation uuidRepresentation, CodecRegistry registry, byte binarySubType, Codec<?> binaryTypeCodec
   ) {
      if (binarySubType == BsonBinarySubType.VECTOR.getValue()) {
         Codec<BinaryVector> vectorCodec = registry.get(BinaryVector.class, registry);
         if (vectorCodec != null) {
            return vectorCodec;
         }
      } else if (reader.peekBinarySize() == 16) {
         switch (binarySubType) {
            case 3:
               if (UuidHelper.isLegacyUUID(uuidRepresentation)) {
                  return registry.get(UUID.class);
               }
               break;
            case 4:
               if (uuidRepresentation == UuidRepresentation.STANDARD) {
                  return registry.get(UUID.class);
               }
         }
      }

      return binaryTypeCodec;
   }

   static Codec<?> getCodec(CodecRegistry codecRegistry, Type type) {
      if (type instanceof Class) {
         return codecRegistry.get((Class)type);
      } else if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         return codecRegistry.get((Class)parameterizedType.getRawType(), Arrays.asList(parameterizedType.getActualTypeArguments()));
      } else {
         throw new CodecConfigurationException("Unsupported generic type of container: " + type);
      }
   }

   private ContainerCodecHelper() {
   }
}
