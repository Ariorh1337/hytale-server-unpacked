package org.bson.internal;

import java.lang.reflect.Type;
import java.util.List;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecRegistry;

class LazyCodec<T> implements Codec<T> {
   private final CodecRegistry registry;
   private final Class<T> clazz;
   private final List<Type> types;
   private volatile Codec<T> wrapped;

   LazyCodec(CodecRegistry registry, Class<T> clazz, List<Type> types) {
      this.registry = registry;
      this.clazz = clazz;
      this.types = types;
   }

   @Override
   public void encode(BsonWriter writer, T value, EncoderContext encoderContext) {
      this.getWrapped().encode(writer, value, encoderContext);
   }

   @Override
   public Class<T> getEncoderClass() {
      return this.clazz;
   }

   @Override
   public T decode(BsonReader reader, DecoderContext decoderContext) {
      return this.getWrapped().decode(reader, decoderContext);
   }

   private Codec<T> getWrapped() {
      if (this.wrapped == null) {
         if (this.types == null) {
            this.wrapped = this.registry.get(this.clazz);
         } else {
            this.wrapped = this.registry.get(this.clazz, this.types);
         }
      }

      return this.wrapped;
   }
}
