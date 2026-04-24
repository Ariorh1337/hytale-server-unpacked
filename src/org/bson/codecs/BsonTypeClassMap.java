package org.bson.codecs;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.BsonDbPointer;
import org.bson.BsonRegularExpression;
import org.bson.BsonTimestamp;
import org.bson.BsonType;
import org.bson.BsonUndefined;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.CodeWithScope;
import org.bson.types.Decimal128;
import org.bson.types.MaxKey;
import org.bson.types.MinKey;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

public class BsonTypeClassMap {
   static final BsonTypeClassMap DEFAULT_BSON_TYPE_CLASS_MAP = new BsonTypeClassMap();
   private final Class<?>[] bsonTypeOrdinalToClassMap = new Class[256];

   public BsonTypeClassMap(Map<BsonType, Class<?>> replacementsForDefaults) {
      this.addDefaults();
      replacementsForDefaults.forEach((key, value) -> this.bsonTypeOrdinalToClassMap[key.getValue()] = (Class<?>)value);
   }

   public BsonTypeClassMap() {
      this(Collections.emptyMap());
   }

   public Class<?> get(BsonType bsonType) {
      return this.bsonTypeOrdinalToClassMap[bsonType.getValue()];
   }

   private void addDefaults() {
      this.bsonTypeOrdinalToClassMap[BsonType.ARRAY.getValue()] = List.class;
      this.bsonTypeOrdinalToClassMap[BsonType.BINARY.getValue()] = Binary.class;
      this.bsonTypeOrdinalToClassMap[BsonType.BOOLEAN.getValue()] = Boolean.class;
      this.bsonTypeOrdinalToClassMap[BsonType.DATE_TIME.getValue()] = Date.class;
      this.bsonTypeOrdinalToClassMap[BsonType.DB_POINTER.getValue()] = BsonDbPointer.class;
      this.bsonTypeOrdinalToClassMap[BsonType.DOCUMENT.getValue()] = Document.class;
      this.bsonTypeOrdinalToClassMap[BsonType.DOUBLE.getValue()] = Double.class;
      this.bsonTypeOrdinalToClassMap[BsonType.INT32.getValue()] = Integer.class;
      this.bsonTypeOrdinalToClassMap[BsonType.INT64.getValue()] = Long.class;
      this.bsonTypeOrdinalToClassMap[BsonType.DECIMAL128.getValue()] = Decimal128.class;
      this.bsonTypeOrdinalToClassMap[BsonType.MAX_KEY.getValue()] = MaxKey.class;
      this.bsonTypeOrdinalToClassMap[BsonType.MIN_KEY.getValue()] = MinKey.class;
      this.bsonTypeOrdinalToClassMap[BsonType.JAVASCRIPT.getValue()] = Code.class;
      this.bsonTypeOrdinalToClassMap[BsonType.JAVASCRIPT_WITH_SCOPE.getValue()] = CodeWithScope.class;
      this.bsonTypeOrdinalToClassMap[BsonType.OBJECT_ID.getValue()] = ObjectId.class;
      this.bsonTypeOrdinalToClassMap[BsonType.REGULAR_EXPRESSION.getValue()] = BsonRegularExpression.class;
      this.bsonTypeOrdinalToClassMap[BsonType.STRING.getValue()] = String.class;
      this.bsonTypeOrdinalToClassMap[BsonType.SYMBOL.getValue()] = Symbol.class;
      this.bsonTypeOrdinalToClassMap[BsonType.TIMESTAMP.getValue()] = BsonTimestamp.class;
      this.bsonTypeOrdinalToClassMap[BsonType.UNDEFINED.getValue()] = BsonUndefined.class;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         BsonTypeClassMap that = (BsonTypeClassMap)o;
         return Arrays.equals(this.bsonTypeOrdinalToClassMap, that.bsonTypeOrdinalToClassMap);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.bsonTypeOrdinalToClassMap);
   }
}
