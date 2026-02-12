package org.rocksdb;

public interface MutableColumnFamilyOptionsInterface<T extends MutableColumnFamilyOptionsInterface<T>> extends AdvancedMutableColumnFamilyOptionsInterface<T> {
   T setWriteBufferSize(long var1);

   long writeBufferSize();

   T setDisableAutoCompactions(boolean var1);

   boolean disableAutoCompactions();

   T setLevel0FileNumCompactionTrigger(int var1);

   int level0FileNumCompactionTrigger();

   T setMaxCompactionBytes(long var1);

   long maxCompactionBytes();

   T setMaxBytesForLevelBase(long var1);

   long maxBytesForLevelBase();

   T setCompressionType(CompressionType var1);

   CompressionType compressionType();
}
