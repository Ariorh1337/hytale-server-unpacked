package com.hypixel.hytale.component;

import com.hypixel.hytale.component.query.Query;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ComponentType<ECS_TYPE, T extends Component<ECS_TYPE>> implements Comparable<ComponentType<ECS_TYPE, ?>>, Query<ECS_TYPE> {
   @Nonnull
   public static final ComponentType[] EMPTY_ARRAY = new ComponentType[0];
   private ComponentRegistry<ECS_TYPE> registry;
   private Class<? super T> tClass;
   private int index;
   private int hashCode;
   private boolean valid = false;

   void init(@Nonnull ComponentRegistry<ECS_TYPE> registry, @Nonnull Class<? super T> tClass, int index) {
      this.registry = registry;
      this.tClass = tClass;
      this.index = index;
      this.hashCode = 31 * registry.hashCode() + index;
      this.valid = true;
   }

   @Nonnull
   public ComponentRegistry<ECS_TYPE> getRegistry() {
      return this.registry;
   }

   @Nonnull
   public Class<? super T> getTypeClass() {
      return this.tClass;
   }

   public int getIndex() {
      return this.index;
   }

   void invalidate() {
      this.valid = false;
   }

   boolean isValid() {
      return this.valid;
   }

   @Override
   public boolean test(@Nonnull Archetype<ECS_TYPE> archetype) {
      return archetype.contains(this);
   }

   @Override
   public boolean requiresComponentType(ComponentType<ECS_TYPE, ?> componentType) {
      return this.equals(componentType);
   }

   @Override
   public void validateRegistry(@Nonnull ComponentRegistry<ECS_TYPE> registry) {
      if (this.registry != registry) {
         throw new IllegalArgumentException("ComponentType is for a different registry! " + this);
      }
   }

   @Override
   public void validate() {
      if (!this.valid) {
         throw new IllegalStateException("ComponentType is invalid!");
      }
   }

   public int compareTo(@Nonnull ComponentType<ECS_TYPE, ?> o) {
      return Integer.compare(this.index, o.index);
   }

   @Override
   public boolean equals(@Nullable Object o) {
      if (this == o) {
         return true;
      } else if (o instanceof ComponentType<?, ?> that) {
         return this.index != that.index ? false : this.registry == that.registry;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   @Nonnull
   @Override
   public String toString() {
      return "ComponentType{registry="
         + this.registry.getClass()
         + "@"
         + this.registry.hashCode()
         + ", typeClass="
         + this.tClass
         + ", index="
         + this.index
         + "}";
   }
}
