package org.bson.codecs;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public final class EnumCodecProvider implements CodecProvider {
   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return Enum.class.isAssignableFrom(clazz) ? new EnumCodec(clazz) : null;
   }

   @Override
   public String toString() {
      return "EnumCodecProvider{}";
   }
}
