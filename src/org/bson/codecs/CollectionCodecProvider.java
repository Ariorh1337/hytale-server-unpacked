package org.bson.codecs;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.bson.Transformer;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class CollectionCodecProvider implements CodecProvider {
   private final BsonTypeClassMap bsonTypeClassMap;
   private final Transformer valueTransformer;

   public CollectionCodecProvider() {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public CollectionCodecProvider(Transformer valueTransformer) {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, valueTransformer);
   }

   public CollectionCodecProvider(BsonTypeClassMap bsonTypeClassMap) {
      this(bsonTypeClassMap, null);
   }

   public CollectionCodecProvider(BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
      this.valueTransformer = valueTransformer;
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return this.get(clazz, Collections.emptyList(), registry);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
      if (Collection.class.isAssignableFrom(clazz)) {
         int typeArgumentsSize = typeArguments.size();
         switch (typeArgumentsSize) {
            case 0:
               return new CollectionCodec(registry, this.bsonTypeClassMap, this.valueTransformer, clazz);
            case 1:
               return new ParameterizedCollectionCodec<>(ContainerCodecHelper.getCodec(registry, typeArguments.get(0)), clazz);
            default:
               throw new CodecConfigurationException("Expected only one type argument for a Collection, but found " + typeArgumentsSize);
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CollectionCodecProvider that = (CollectionCodecProvider)o;
         return !this.bsonTypeClassMap.equals(that.bsonTypeClassMap) ? false : Objects.equals(this.valueTransformer, that.valueTransformer);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.bsonTypeClassMap, this.valueTransformer);
   }

   @Override
   public String toString() {
      return "CollectionCodecProvider{}";
   }
}
