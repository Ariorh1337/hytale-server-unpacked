package org.bson.codecs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecConfigurationException;

abstract class AbstractCollectionCodec<T, C extends Collection<T>> implements Codec<C> {
   private final Class<C> clazz;
   private final Supplier<C> supplier;

   AbstractCollectionCodec(Class<C> clazz) {
      this.clazz = Assertions.notNull("clazz", clazz);
      Class rawClass = clazz;
      if (rawClass == Collection.class
         || rawClass == List.class
         || rawClass == AbstractCollection.class
         || rawClass == AbstractList.class
         || rawClass == ArrayList.class) {
         this.supplier = () -> (C)(new ArrayList());
      } else if (rawClass == Set.class || rawClass == AbstractSet.class || rawClass == HashSet.class) {
         this.supplier = () -> (C)(new HashSet());
      } else if (rawClass != NavigableSet.class && rawClass != SortedSet.class && rawClass != TreeSet.class) {
         Supplier<C> supplier;
         try {
            Constructor<? extends Collection<?>> constructor = clazz.getDeclaredConstructor();
            supplier = (Supplier<C>)(() -> {
               try {
                  return constructor.newInstance();
               } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                  throw new CodecConfigurationException(String.format("Can not invoke no-args constructor for Collection class %s", clazz), e);
               }
            });
         } catch (NoSuchMethodException e) {
            supplier = () -> {
               throw new CodecConfigurationException(String.format("No no-args constructor for Collection class %s", clazz), e);
            };
         }

         this.supplier = supplier;
      } else {
         this.supplier = () -> (C)(new TreeSet());
      }
   }

   abstract T readValue(BsonReader var1, DecoderContext var2);

   abstract void writeValue(BsonWriter var1, T var2, EncoderContext var3);

   public C decode(BsonReader reader, DecoderContext decoderContext) {
      reader.readStartArray();
      C collection = this.supplier.get();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         if (reader.getCurrentBsonType() == BsonType.NULL) {
            reader.readNull();
            collection.add(null);
         } else {
            collection.add(this.readValue(reader, decoderContext));
         }
      }

      reader.readEndArray();
      return collection;
   }

   public void encode(BsonWriter writer, C value, EncoderContext encoderContext) {
      writer.writeStartArray();

      for (T cur : value) {
         if (cur == null) {
            writer.writeNull();
         } else {
            this.writeValue(writer, cur, encoderContext);
         }
      }

      writer.writeEndArray();
   }

   @Override
   public Class<C> getEncoderClass() {
      return this.clazz;
   }
}
