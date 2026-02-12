package org.rocksdb;

import java.util.List;

public class OptionsUtil {
   public static void loadLatestOptions(ConfigOptions var0, String var1, DBOptions var2, List<ColumnFamilyDescriptor> var3) throws RocksDBException {
      loadLatestOptions(var0.nativeHandle_, var1, var2.nativeHandle_, var3);
      loadTableFormatConfig(var3);
   }

   public static void loadOptionsFromFile(ConfigOptions var0, String var1, DBOptions var2, List<ColumnFamilyDescriptor> var3) throws RocksDBException {
      loadOptionsFromFile(var0.nativeHandle_, var1, var2.nativeHandle_, var3);
      loadTableFormatConfig(var3);
   }

   public static String getLatestOptionsFileName(String var0, Env var1) throws RocksDBException {
      return getLatestOptionsFileName(var0, var1.nativeHandle_);
   }

   private static void loadTableFormatConfig(List<ColumnFamilyDescriptor> var0) {
      for (ColumnFamilyDescriptor var2 : var0) {
         ColumnFamilyOptions var3 = var2.getOptions();
         var3.setFetchedTableFormatConfig(readTableFormatConfig(var3.nativeHandle_));
      }
   }

   private OptionsUtil() {
   }

   private static native void loadLatestOptions(long var0, String var2, long var3, List<ColumnFamilyDescriptor> var5) throws RocksDBException;

   private static native void loadOptionsFromFile(long var0, String var2, long var3, List<ColumnFamilyDescriptor> var5) throws RocksDBException;

   private static native String getLatestOptionsFileName(String var0, long var1) throws RocksDBException;

   private static native TableFormatConfig readTableFormatConfig(long var0);
}
