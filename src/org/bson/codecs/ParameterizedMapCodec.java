package org.bson.codecs;

import java.util.Map;
import org.bson.BsonReader;
import org.bson.BsonWriter;

class ParameterizedMapCodec<T, M extends Map<String, T>> extends AbstractMapCodec<T, M> {
   private final Codec<T> codec;

   ParameterizedMapCodec(Codec<T> codec, Class<M> clazz) {
      super(clazz);
      this.codec = codec;
   }

   @Override
   T readValue(BsonReader reader, DecoderContext decoderContext) {
      return decoderContext.decodeWithChildContext(this.codec, reader);
   }

   @Override
   void writeValue(BsonWriter writer, T value, EncoderContext encoderContext) {
      encoderContext.encodeWithChildContext(this.codec, writer, value);
   }
}
