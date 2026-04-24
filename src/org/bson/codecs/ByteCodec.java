package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.internal.NumberCodecHelper;

public class ByteCodec implements Codec<Byte> {
   public void encode(BsonWriter writer, Byte value, EncoderContext encoderContext) {
      writer.writeInt32(value);
   }

   public Byte decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeByte(reader);
   }

   @Override
   public Class<Byte> getEncoderClass() {
      return Byte.class;
   }
}
