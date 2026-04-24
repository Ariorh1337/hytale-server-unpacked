package org.bson.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;

public final class EnumCodec<T extends Enum<T>> implements Codec<T> {
   private final Class<T> clazz;

   public EnumCodec(Class<T> clazz) {
      this.clazz = clazz;
   }

   public T decode(BsonReader reader, DecoderContext decoderContext) {
      return Enum.valueOf(this.clazz, reader.readString());
   }

   public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
      writer.writeString(value.name());
   }

   @Override
   public Class<T> getEncoderClass() {
      return this.clazz;
   }
}
