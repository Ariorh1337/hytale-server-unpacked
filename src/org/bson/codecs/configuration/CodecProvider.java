package org.bson.codecs.configuration;

import java.lang.reflect.Type;
import java.util.List;
import org.bson.codecs.Codec;

public interface CodecProvider {
   <T> Codec<T> get(Class<T> var1, CodecRegistry var2);

   default <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
      return this.get(clazz, registry);
   }
}
