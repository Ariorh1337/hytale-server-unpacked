package org.rocksdb;

import java.util.List;

public interface AdvancedColumnFamilyOptionsInterface<T extends AdvancedColumnFamilyOptionsInterface<T> & ColumnFamilyOptionsInterface<T>> {
   T setMinWriteBufferNumberToMerge(int var1);

   int minWriteBufferNumberToMerge();

   T setInplaceUpdateSupport(boolean var1);

   boolean inplaceUpdateSupport();

   T setBloomLocality(int var1);

   int bloomLocality();

   T setCompressionPerLevel(List<CompressionType> var1);

   List<CompressionType> compressionPerLevel();

   T setNumLevels(int var1);

   int numLevels();

   T setLevelCompactionDynamicLevelBytes(boolean var1);

   boolean levelCompactionDynamicLevelBytes();

   T setMaxCompactionBytes(long var1);

   long maxCompactionBytes();

   ColumnFamilyOptionsInterface<T> setCompactionStyle(CompactionStyle var1);

   CompactionStyle compactionStyle();

   T setCompactionPriority(CompactionPriority var1);

   CompactionPriority compactionPriority();

   T setCompactionOptionsUniversal(CompactionOptionsUniversal var1);

   CompactionOptionsUniversal compactionOptionsUniversal();

   T setCompactionOptionsFIFO(CompactionOptionsFIFO var1);

   CompactionOptionsFIFO compactionOptionsFIFO();

   T setOptimizeFiltersForHits(boolean var1);

   boolean optimizeFiltersForHits();

   T setForceConsistencyChecks(boolean var1);

   boolean forceConsistencyChecks();
}
