package org.rocksdb;

public enum FilterPolicyType {
   kUnknownFilterPolicy((byte)0),
   kBloomFilterPolicy((byte)1),
   kRibbonFilterPolicy((byte)2);

   private final byte value_;

   public Filter createFilter(long var1, double var3) {
      return this == kBloomFilterPolicy ? new BloomFilter(var1, var3) : null;
   }

   public byte getValue() {
      return this.value_;
   }

   FilterPolicyType(byte var3) {
      this.value_ = var3;
   }
}
