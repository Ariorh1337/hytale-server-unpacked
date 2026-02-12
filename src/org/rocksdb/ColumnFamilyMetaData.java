package org.rocksdb;

import java.util.Arrays;
import java.util.List;

public class ColumnFamilyMetaData {
   private final long size;
   private final long fileCount;
   private final byte[] name;
   private final LevelMetaData[] levels;

   private ColumnFamilyMetaData(long var1, long var3, byte[] var5, LevelMetaData[] var6) {
      this.size = var1;
      this.fileCount = var3;
      this.name = var5;
      this.levels = var6;
   }

   public long size() {
      return this.size;
   }

   public long fileCount() {
      return this.fileCount;
   }

   public byte[] name() {
      return this.name;
   }

   public List<LevelMetaData> levels() {
      return Arrays.asList(this.levels);
   }
}
