package org.rocksdb;

public interface MutableOptionKey {
   String name();

   MutableOptionKey.ValueType getValueType();

   enum ValueType {
      DOUBLE,
      LONG,
      INT,
      BOOLEAN,
      INT_ARRAY,
      ENUM,
      STRING;
   }
}
