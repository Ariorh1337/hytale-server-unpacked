package org.bson.internal;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public final class ProvidersCodecRegistry implements CycleDetectingCodecRegistry {
   private final List<CodecProvider> codecProviders;
   private final CodecCache codecCache = new CodecCache();

   public ProvidersCodecRegistry(List<? extends CodecProvider> codecProviders) {
      Assertions.isTrueArgument("codecProviders must not be null or empty", codecProviders != null && codecProviders.size() > 0);
      this.codecProviders = new ArrayList<>(codecProviders);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz) {
      return this.get(new ChildCodecRegistry<>(this, clazz, null));
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments) {
      Assertions.notNull("typeArguments", typeArguments);
      Assertions.isTrueArgument(
         String.format("typeArguments size should equal the number of type parameters in class %s, but is %d", clazz, typeArguments.size()),
         clazz.getTypeParameters().length == typeArguments.size()
      );
      return this.get(new ChildCodecRegistry<>(this, clazz, typeArguments));
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return this.get(clazz, Collections.emptyList(), registry);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
      for (CodecProvider provider : this.codecProviders) {
         Codec<T> codec = provider.get(clazz, typeArguments, registry);
         if (codec != null) {
            return codec;
         }
      }

      return null;
   }

   @Override
   public <T> Codec<T> get(ChildCodecRegistry<T> context) {
      CodecCache.CodecCacheKey codecCacheKey = new CodecCache.CodecCacheKey(context.getCodecClass(), context.getTypes().orElse(null));
      return this.codecCache.<T>get(codecCacheKey).orElseGet(() -> {
         for (CodecProvider provider : this.codecProviders) {
            Codec<T> codec = provider.get(context.getCodecClass(), context.getTypes().orElse(Collections.emptyList()), context);
            if (codec != null) {
               return this.codecCache.putIfAbsent(codecCacheKey, codec);
            }
         }

         throw new CodecConfigurationException(String.format("Can't find a codec for %s.", codecCacheKey));
      });
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o != null && this.getClass() == o.getClass()) {
         ProvidersCodecRegistry that = (ProvidersCodecRegistry)o;
         if (this.codecProviders.size() != that.codecProviders.size()) {
            return false;
         }

         for (int i = 0; i < this.codecProviders.size(); i++) {
            if (this.codecProviders.get(i).getClass() != that.codecProviders.get(i).getClass()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.codecProviders.hashCode();
   }

   @Override
   public String toString() {
      return "ProvidersCodecRegistry{codecProviders=" + this.codecProviders + '}';
   }
}
