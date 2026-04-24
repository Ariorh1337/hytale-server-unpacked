package org.bson.internal;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;

final class CodecCache {
   private final ConcurrentMap<CodecCache.CodecCacheKey, Codec<?>> codecCache = new ConcurrentHashMap<>();

   public <T> Codec<T> putIfAbsent(CodecCache.CodecCacheKey codecCacheKey, Codec<T> codec) {
      Assertions.assertNotNull(codec);
      Codec<T> prevCodec = (Codec<T>)this.codecCache.putIfAbsent(codecCacheKey, codec);
      return prevCodec == null ? codec : prevCodec;
   }

   public <T> Optional<Codec<T>> get(CodecCache.CodecCacheKey codecCacheKey) {
      Codec<T> codec = (Codec<T>)this.codecCache.get(codecCacheKey);
      return Optional.ofNullable(codec);
   }

   static final class CodecCacheKey {
      private final Class<?> clazz;
      private final List<Type> types;

      CodecCacheKey(Class<?> clazz, List<Type> types) {
         this.clazz = clazz;
         this.types = types;
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            CodecCache.CodecCacheKey that = (CodecCache.CodecCacheKey)o;
            return this.clazz.equals(that.clazz) && Objects.equals(this.types, that.types);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.clazz, this.types);
      }

      @Override
      public String toString() {
         return "CodecCacheKey{clazz=" + this.clazz + ", types=" + this.types + '}';
      }
   }
}
