package org.bson.codecs.pojo;

class PropertyModelSerializationInlineImpl<T> implements PropertySerialization<T> {
   private final PropertySerialization<T> wrapped;

   PropertyModelSerializationInlineImpl(PropertySerialization<T> wrapped) {
      this.wrapped = wrapped;
   }

   @Override
   public boolean shouldSerialize(T value) {
      return this.wrapped.shouldSerialize(value);
   }

   @Override
   public boolean inline() {
      return true;
   }
}
