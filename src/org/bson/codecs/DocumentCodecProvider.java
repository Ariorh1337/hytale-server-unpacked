package org.bson.codecs;

import java.util.Objects;
import org.bson.Document;
import org.bson.Transformer;
import org.bson.assertions.Assertions;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.types.CodeWithScope;

public class DocumentCodecProvider implements CodecProvider {
   private final BsonTypeClassMap bsonTypeClassMap;
   private final Transformer valueTransformer;

   public DocumentCodecProvider() {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP);
   }

   public DocumentCodecProvider(Transformer valueTransformer) {
      this(BsonTypeClassMap.DEFAULT_BSON_TYPE_CLASS_MAP, valueTransformer);
   }

   public DocumentCodecProvider(BsonTypeClassMap bsonTypeClassMap) {
      this(bsonTypeClassMap, null);
   }

   public DocumentCodecProvider(BsonTypeClassMap bsonTypeClassMap, Transformer valueTransformer) {
      this.bsonTypeClassMap = Assertions.notNull("bsonTypeClassMap", bsonTypeClassMap);
      this.valueTransformer = valueTransformer;
   }

   @Override
   public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
      if (clazz == CodeWithScope.class) {
         return new CodeWithScopeCodec(registry.get(Document.class));
      } else {
         return clazz == Document.class ? new DocumentCodec(registry, this.bsonTypeClassMap, this.valueTransformer) : null;
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         DocumentCodecProvider that = (DocumentCodecProvider)o;
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
      return "DocumentCodecProvider{}";
   }
}
