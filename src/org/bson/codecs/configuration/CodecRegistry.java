package org.bson.codecs.configuration;

import java.lang.reflect.Type;
import java.util.List;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;

public interface CodecRegistry extends CodecProvider {
   <T> Codec<T> get(Class<T> var1);

   default <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments) {
      throw Assertions.fail("This method should have been overridden but was not.");
   }
}
