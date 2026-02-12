package org.rocksdb;

import java.util.Arrays;
import java.util.List;

public class LevelMetaData {
   private final int level;
   private final long size;
   private final SstFileMetaData[] files;

   private LevelMetaData(int var1, long var2, SstFileMetaData[] var4) {
      this.level = var1;
      this.size = var2;
      this.files = var4;
   }

   public int level() {
      return this.level;
   }

   public long size() {
      return this.size;
   }

   public List<SstFileMetaData> files() {
      return Arrays.asList(this.files);
   }
}
