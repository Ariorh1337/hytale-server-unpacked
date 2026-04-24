package org.bson.codecs;

import java.util.Collection;
import org.bson.BsonReader;
import org.bson.BsonWriter;

class ParameterizedCollectionCodec<T, C extends Collection<T>> extends AbstractCollectionCodec<T, C> {
   private final Codec<T> codec;

   ParameterizedCollectionCodec(Codec<T> codec, Class<C> clazz) {
      super(clazz);
      this.codec = codec;
   }

   @Override
   T readValue(BsonReader reader, DecoderContext decoderContext) {
      return decoderContext.decodeWithChildContext(this.codec, reader);
   }

   @Override
   void writeValue(BsonWriter writer, T cur, EncoderContext encoderContext) {
      encoderContext.encodeWithChildContext(this.codec, writer, cur);
   }
}
