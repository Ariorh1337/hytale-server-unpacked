package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.internal.NumberCodecHelper;

public class DoubleCodec implements Codec<Double> {
   public void encode(BsonWriter writer, Double value, EncoderContext encoderContext) {
      writer.writeDouble(value);
   }

   public Double decode(BsonReader reader, DecoderContext decoderContext) {
      return NumberCodecHelper.decodeDouble(reader);
   }

   @Override
   public Class<Double> getEncoderClass() {
      return Double.class;
   }
}
