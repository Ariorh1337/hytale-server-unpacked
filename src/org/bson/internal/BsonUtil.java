package org.bson.internal;

import org.bson.BsonArray;
import org.bson.BsonBinary;
import org.bson.BsonDocument;
import org.bson.BsonJavaScriptWithScope;
import org.bson.BsonValue;

public final class BsonUtil {
   public static BsonDocument mutableDeepCopy(BsonDocument original) {
      BsonDocument copy = new BsonDocument(original.size());
      original.forEach((key, value) -> copy.put(key, mutableDeepCopy(value)));
      return copy;
   }

   private static BsonArray mutableDeepCopy(BsonArray original) {
      BsonArray copy = new BsonArray(original.size());
      original.forEach(element -> copy.add(mutableDeepCopy(element)));
      return copy;
   }

   private static BsonBinary mutableDeepCopy(BsonBinary original) {
      return new BsonBinary(original.getType(), (byte[])original.getData().clone());
   }

   private static BsonJavaScriptWithScope mutableDeepCopy(BsonJavaScriptWithScope original) {
      return new BsonJavaScriptWithScope(original.getCode(), mutableDeepCopy(original.getScope()));
   }

   private static BsonValue mutableDeepCopy(BsonValue original) {
      switch (original.getBsonType()) {
         case DOCUMENT:
            return mutableDeepCopy(original.asDocument());
         case ARRAY:
            return mutableDeepCopy(original.asArray());
         case BINARY:
            return mutableDeepCopy(original.asBinary());
         case JAVASCRIPT_WITH_SCOPE:
            return mutableDeepCopy(original.asJavaScriptWithScope());
         default:
            return original;
      }
   }

   private BsonUtil() {
   }
}
