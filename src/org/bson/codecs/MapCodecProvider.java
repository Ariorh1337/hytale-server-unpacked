package org.bson.codecs;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bson.Transformer;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecConfigurationException;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

public class MapCodecProvider implements CodecProvider {
   private final BsonTypeClassMap bsonTypeClassMap;
   private final Transformer valueTransformer;

   public MapCodecProvider() {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public MapCodecProvider(BsonTypeClassMap bsonTypeClassMap) {
      this(bsonTypeClassMap, null);
   }

   public MapCodecProvider(Transformer valueTransformer) {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, valueTransformer);
   }

   public MapCodecProvider(BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
      this.valueTransformer = valueTransformer;
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      return this.get(clazz, Collections.emptyList(), registry);
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, List<Type> typeArguments, CodecRegistry registry) {
      if (Map.class.isAssignableFrom(clazz)) {
         int typeArgumentsSize = typeArguments.size();
         switch (typeArgumentsSize) {
            case 0:
               return new MapCodec(registry, this.bsonTypeClassMap, this.valueTransformer, clazz);
            case 2:
               Type genericTypeOfMapKey = typeArguments.get(0);
               if (!genericTypeOfMapKey.getTypeName().equals("java.lang.String")) {
                  throw new CodecConfigurationException("Unsupported key type for Map: " + genericTypeOfMapKey.getTypeName());
               }

               return new ParameterizedMapCodec<>(ContainerCodecHelper.getCodec(registry, typeArguments.get(1)), clazz);
            default:
               throw new CodecConfigurationException("Expected two parameterized type for an Iterable, but found " + typeArgumentsSize);
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
         MapCodecProvider that = (MapCodecProvider)o;
         return !this.bsonTypeClassMap.equals(that.bsonTypeClassMap) ? false : Objects.equals(this.valueTransformer, that.valueTransformer);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int result = this.bsonTypeClassMap.hashCode();
      return 31 * result + (this.valueTransformer != null ? this.valueTransformer.hashCode() : 0);
   }

   @Override
   public String toString() {
      return "MapCodecProvider{}";
   }
}
