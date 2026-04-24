package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.internal.NumberCodecHelper;

public class ShortCodec implements Codec<Short> {
   public void encode(BsonWriter writer, Short value, EncoderContext encoderContext) {
      writer.writeInt32(value);
   }

   public Short decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeShort(reader);
   }

   @Override
   public Class<Short> getEncoderClass() {
      return Short.class;
   }
}
