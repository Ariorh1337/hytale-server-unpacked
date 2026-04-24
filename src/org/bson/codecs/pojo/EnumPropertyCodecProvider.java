package org.bson.codecs.pojo;

import org.bson.codecs.Codec;
import org.bson.codecs.EnumCodec;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecRegistry;

final class EnumPropertyCodecProvider implements PropertyCodecProvider {
   private final CodecRegistry codecRegistry;

   EnumPropertyCodecProvider(CodecRegistry codecRegistry) {
      this.codecRegistry = codecRegistry;
   }

   @Override
   public <T> Codec<T> get(TypeWithTypeParameters<T> type, PropertyCodecRegistry propertyCodecRegistry) {
      Class<T> clazz = type.getType();
      if (Enum.class.isAssignableFrom(clazz)) {
         try {
            return this.codecRegistry.get(clazz);
         } catch (CodecConfigurationException e) {
            return new EnumCodec(clazz);
         }
      } else {
         return null;
      }
   }
}
