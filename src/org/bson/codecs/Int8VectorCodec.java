package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Int8BinaryVector;

final class Int8VectorCodec implements Codec<Int8BinaryVector> {
   public void encode(BsonWriter writer, Int8BinaryVector vectorToEncode, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(vectorToEncode));
   }

   public Int8BinaryVector decode(BsonReader reader, DecoderContext decoderContext) {
      byte subType = reader.peekBinarySubType();
      if (subType != BsonBinarySubType.VECTOR.getValue()) {
         throw new BsonInvalidOperationException("Expected vector binary subtype " + BsonBinarySubType.VECTOR.getValue() + " but found: " + subType);
      } else {
         return reader.readBinaryData().asBinary().asVector().asInt8Vector();
      }
   }

   @Override
   public Class<Int8BinaryVector> getEncoderClass() {
      return Int8BinaryVector.class;
   }
}
