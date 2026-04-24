package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.internal.NumberCodecHelper;

public class FloatCodec implements Codec<Float> {
   public void encode(BsonWriter writer, Float value, EncoderContext encoderContext) {
      writer.writeDouble(value.floatValue());
   }

   public Float decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeFloat(reader);
   }

   @Override
   public Class<Float> getEncoderClass() {
      return Float.class;
   }
}
