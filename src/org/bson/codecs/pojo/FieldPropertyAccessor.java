package org.bson.codecs.pojo;

import org.bson.codecs.configuration.CodecConfigurationException;

final class FieldPropertyAccessor<T> implements PropertyAccessor<T> {
   private final PropertyAccessorImpl<T> wrapped;

   FieldPropertyAccessor(PropertyAccessorImpl<T> wrapped) {
      this.wrapped = wrapped;

      try {
         wrapped.getPropertyMetadata().getField().setAccessible(true);
      } catch (Exception e) {
         throw new CodecConfigurationException(
            String.format(
               "Unable to make field accessible '%s' in %s", wrapped.getPropertyMetadata().getName(), wrapped.getPropertyMetadata().getDeclaringClassName()
            ),
            e
         );
      }
   }

   @Override
   public <S> T get(S instance) {
      return this.wrapped.get(instance);
   }

   @Override
   public <S> void set(S instance, T value) {
      try {
         this.wrapped.getPropertyMetadata().getField().set(instance, value);
      } catch (Exception e) {
         throw new CodecConfigurationException(
            String.format(
               "Unable to set value for property '%s' in %s",
               this.wrapped.getPropertyMetadata().getName(),
               this.wrapped.getPropertyMetadata().getDeclaringClassName()
            ),
            e
         );
      }
   }
}
