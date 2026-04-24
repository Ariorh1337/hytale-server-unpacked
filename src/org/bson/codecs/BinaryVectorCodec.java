package org.bson.codecs;

import org.bson.BinaryVector;
import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;

final class BinaryVectorCodec implements Codec<BinaryVector> {
   public void encode(BsonWriter writer, BinaryVector vectorToEncode, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(vectorToEncode));
   }

   public BinaryVector decode(BsonReader reader, DecoderContext decoderContext) {
      byte subType = reader.peekBinarySubType();
      if (subType != BsonBinarySubType.VECTOR.getValue()) {
         throw new BsonInvalidOperationException("Expected vector binary subtype " + BsonBinarySubType.VECTOR.getValue() + " but found " + subType);
      } else {
         return reader.readBinaryData().asBinary().asVector();
      }
   }

   @Override
   public Class<BinaryVector> getEncoderClass() {
      return BinaryVector.class;
   }
}
