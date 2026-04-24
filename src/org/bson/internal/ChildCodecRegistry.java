package org.bson.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistry;

class ChildCodecRegistry<T> implements CodecRegistry {
   private final ChildCodecRegistry<?> parent;
   private final CycleDetectingCodecRegistry registry;
   private final Class<T> codecClass;
   private final List<Type> types;

   ChildCodecRegistry(CycleDetectingCodecRegistry registry, Class<T> codecClass, List<Type> types) {
      this.codecClass = codecClass;
      this.parent = null;
      this.registry = registry;
      this.types = types;
   }

   private ChildCodecRegistry(ChildCodecRegistry<?> parent, Class<T> codecClass, List<Type> types) {
      this.parent = parent;
      this.codecClass = codecClass;
      this.registry = parent.registry;
      this.types = types;
   }

   public Class<T> getCodecClass() {
      return this.codecClass;
   }

   public Optional<List<Type>> getTypes() {
      return Optional.ofNullable(this.types);
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz) {
      return this.hasCycles(clazz) ? new LazyCodec<>(this.registry, clazz, null) : this.registry.get(new ChildCodecRegistry<>(this, clazz, null));
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz, List<Type> typeArguments) {
      Assertions.notNull("typeArguments", typeArguments);
      Assertions.isTrueArgument(
         String.format("typeArguments size should equal the number of type parameters in class %s, but is %d", clazz, typeArguments.size()),
         clazz.getTypeParameters().length == typeArguments.size()
      );
      return this.hasCycles(clazz)
         ? new LazyCodec<>(this.registry, clazz, typeArguments)
         : this.registry.get(new ChildCodecRegistry<>(this, clazz, typeArguments));
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz, CodecRegistry registry) {
      return this.get(clazz, Collections.emptyList(), registry);
   }

   @Override
   public <U> Codec<U> get(Class<U> clazz, List<Type> typeArguments, CodecRegistry registry) {
      return this.registry.get(clazz, typeArguments, registry);
   }

   private <U> Boolean hasCycles(Class<U> theClass) {
      for (ChildCodecRegistry<?> current = this; current != null; current = current.parent) {
         if (current.codecClass.equals(theClass)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (o != null && this.getClass() == o.getClass()) {
         ChildCodecRegistry<?> that = (ChildCodecRegistry<?>)o;
         if (!this.codecClass.equals(that.codecClass)) {
            return false;
         } else {
            return !Objects.equals(this.parent, that.parent) ? false : this.registry.equals(that.registry);
         }
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.parent != null ? this.parent.hashCode() : 0;
      result = 31 * result + this.registry.hashCode();
      return 31 * result + this.codecClass.hashCode();
   }
}
