package org.bson.codecs.configuration;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import org.bson.UuidRepresentation;
import org.bson.assertions.Assertions;
import org.bson.codecs.Codec;
import org.bson.codecs.OverridableUuidRepresentationCodec;

final class OverridableUuidRepresentationCodecProvider implements CodecProvider {
   private final CodecProvider wrapped;
   private final UuidRepresentation uuidRepresentation;

   OverridableUuidRepresentationCodecProvider(CodecProvider wrapped, UuidRepresentation uuidRepresentation) {
      this.uuidRepresentation = Assertions.notNull("uuidRepresentation", uuidRepresentation);
      this.wrapped = Assertions.notNull("wrapped", wrapped);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return this.get(clazz, Collections.emptyList(), registry);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
      Codec<T> codec = this.wrapped.get(clazz, typeArguments, registry);
      if (codec instanceof OverridableUuidRepresentationCodec) {
         Codec<T> codecWithUuidRepresentation = ((OverridableUuidRepresentationCodec)codec).withUuidRepresentation(this.uuidRepresentation);
         codec = codecWithUuidRepresentation;
      }

      return codec;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         OverridableUuidRepresentationCodecProvider that = (OverridableUuidRepresentationCodecProvider)o;
         return !this.wrapped.equals(that.wrapped) ? false : this.uuidRepresentation == that.uuidRepresentation;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.wrapped.hashCode();
      return 31 * result + this.uuidRepresentation.hashCode();
   }

   @Override
   public String toString() {
      return "OverridableUuidRepresentationCodecRegistry{wrapped=" + this.wrapped + ", uuidRepresentation=" + this.uuidRepresentation + '}';
   }
}
