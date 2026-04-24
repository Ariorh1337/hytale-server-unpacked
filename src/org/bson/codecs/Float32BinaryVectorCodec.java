package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Float32BinaryVector;

final class Float32BinaryVectorCodec implements Codec<Float32BinaryVector> {
   public void encode(BsonWriter writer, Float32BinaryVector vectorToEncode, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(vectorToEncode));
   }

   public Float32BinaryVector decode(BsonReader reader, DecoderContext decoderContext) {
      byte subType = reader.peekBinarySubType();
      if (subType != BsonBinarySubType.VECTOR.getValue()) {
         throw new BsonInvalidOperationException("Expected vector binary subtype " + BsonBinarySubType.VECTOR.getValue() + " but found: " + subType);
      } else {
         return reader.readBinaryData().asBinary().asVector().asFloat32Vector();
      }
   }

   @Override
   public Class<Float32BinaryVector> getEncoderClass() {
      return Float32BinaryVector.class;
   }
}
