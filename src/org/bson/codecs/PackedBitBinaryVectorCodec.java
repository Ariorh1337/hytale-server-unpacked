package org.bson.codecs;

import org.bson.BsonBinary;
import org.bson.BsonBinarySubType;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.PackedBitBinaryVector;

final class PackedBitBinaryVectorCodec implements Codec<PackedBitBinaryVector> {
   public void encode(BsonWriter writer, PackedBitBinaryVector vectorToEncode, EncoderContext encoderContext) {
      writer.writeBinaryData(new BsonBinary(vectorToEncode));
   }

   public PackedBitBinaryVector decode(BsonReader reader, DecoderContext decoderContext) {
      byte subType = reader.peekBinarySubType();
      if (subType != BsonBinarySubType.VECTOR.getValue()) {
         throw new BsonInvalidOperationException("Expected vector binary subtype " + BsonBinarySubType.VECTOR.getValue() + " but found: " + subType);
      } else {
         return reader.readBinaryData().asBinary().asVector().asPackedBitVector();
      }
   }

   @Override
   public Class<PackedBitBinaryVector> getEncoderClass() {
      return PackedBitBinaryVector.class;
   }
}
