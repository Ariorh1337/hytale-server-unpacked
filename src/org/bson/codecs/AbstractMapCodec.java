package org.bson.codecs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecConfigurationException;

abstract class AbstractMapCodec<T, M extends Map<String, T>> implements Codec<M> {
   private final Supplier<M> supplier;
   private final Class<M> clazz;

   AbstractMapCodec(@Nullable Class<M> clazz) {
      this.clazz = Assertions.notNull("clazz", clazz);
      Class rawClass = clazz;
      if (rawClass == Map.class || rawClass == AbstractMap.class || rawClass == HashMap.class) {
         this.supplier = () -> (M)(new HashMap());
      } else if (rawClass != NavigableMap.class && rawClass != TreeMap.class) {
         Supplier<M> supplier;
         try {
            Constructor<? extends Map<?, ?>> constructor = clazz.getDeclaredConstructor();
            supplier = (Supplier<M>)(() -> {
               try {
                  return constructor.newInstance();
               } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                  throw new CodecConfigurationException("Can not invoke no-args constructor for Map class %s", e);
               }
            });
         } catch (NoSuchMethodException e) {
            supplier = () -> {
               throw new CodecConfigurationException(String.format("Map class %s has no public no-args constructor", clazz), e);
            };
         }

         this.supplier = supplier;
      } else {
         this.supplier = () -> (M)(new TreeMap());
      }
   }

   abstract T readValue(BsonReader var1, DecoderContext var2);

   abstract void writeValue(BsonWriter var1, T var2, EncoderContext var3);

   public void encode(BsonWriter writer, M map, EncoderContext encoderContext) {
      writer.writeStartDocument();

      for (Entry<String, T> entry : map.entrySet()) {
         writer.writeName(entry.getKey());
         T value = entry.getValue();
         if (value == null) {
            writer.writeNull();
         } else {
            this.writeValue(writer, value, encoderContext);
         }
      }

      writer.writeEndDocument();
   }

   public M decode(BsonReader reader, DecoderContext decoderContext) {
      M map = this.supplier.get();
      reader.readStartDocument();

      while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
         String fieldName = reader.readName();
         if (reader.getCurrentBsonType() == BsonType.NULL) {
            reader.readNull();
            map.put(fieldName, null);
         } else {
            map.put(fieldName, this.readValue(reader, decoderContext));
         }
      }

      reader.readEndDocument();
      return map;
   }

   @Override
   public Class<M> getEncoderClass() {
      return this.clazz;
   }
}
