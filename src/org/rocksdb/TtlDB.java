package org.rocksdb;

import java.util.Arrays;
import java.util.List;

public class TtlDB extends RocksDB {
   public static TtlDB open(Options var0, String var1) throws RocksDBException {
      return open(var0, var1, 0, false);
   }

   public static TtlDB open(Options var0, String var1, int var2, boolean var3) throws RocksDBException {
      TtlDB var4 = new TtlDB(open(var0.nativeHandle_, var1, var2, var3));
      var4.storeOptionsInstance(var0);
      var4.storeDefaultColumnFamilyHandle(var4.makeDefaultColumnFamilyHandle());
      return var4;
   }

   public static TtlDB open(DBOptions var0, String var1, List<ColumnFamilyDescriptor> var2, List<ColumnFamilyHandle> var3, List<Integer> var4, boolean var5) throws RocksDBException {
      if (var2.size() != var4.size()) {
         throw new IllegalArgumentException("There must be a ttl value per column family handle.");
      }

      int var6 = -1;
      byte[][] var7 = new byte[var2.size()][];
      long[] var8 = new long[var2.size()];

      for (int var9 = 0; var9 < var2.size(); var9++) {
         ColumnFamilyDescriptor var10 = (ColumnFamilyDescriptor)var2.get(var9);
         var7[var9] = var10.getName();
         var8[var9] = var10.getOptions().nativeHandle_;
         if (Arrays.equals(var10.getName(), RocksDB.DEFAULT_COLUMN_FAMILY)) {
            var6 = var9;
         }
      }

      if (var6 < 0) {
         throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
      }

      int[] var13 = new int[var4.size()];

      for (int var14 = 0; var14 < var4.size(); var14++) {
         var13[var14] = (Integer)var4.get(var14);
      }

      long[] var15 = openCF(var0.nativeHandle_, var1, var7, var8, var13, var5);
      TtlDB var11 = new TtlDB(var15[0]);

      for (int var12 = 1; var12 < var15.length; var12++) {
         var3.add(new ColumnFamilyHandle(var11, var15[var12]));
      }

      var11.storeOptionsInstance(var0);
      var11.ownedColumnFamilyHandles.addAll(var3);
      var11.storeDefaultColumnFamilyHandle((ColumnFamilyHandle)var3.get(var6));
      return var11;
   }

   @Override
   public void closeE() throws RocksDBException {
      if (this.owningHandle_.compareAndSet(true, false)) {
         try {
            closeDatabase(this.nativeHandle_);
         } finally {
            this.disposeInternal();
         }
      }
   }

   @Override
   public void close() {
      for (ColumnFamilyHandle var2 : this.ownedColumnFamilyHandles) {
         var2.close();
      }

      this.ownedColumnFamilyHandles.clear();
      if (this.owningHandle_.compareAndSet(true, false)) {
         try {
            closeDatabase(this.nativeHandle_);
         } catch (RocksDBException var6) {
         } finally {
            this.disposeInternal();
         }
      }
   }

   public ColumnFamilyHandle createColumnFamilyWithTtl(ColumnFamilyDescriptor var1, int var2) throws RocksDBException {
      return new ColumnFamilyHandle(this, createColumnFamilyWithTtl(this.nativeHandle_, var1.getName(), var1.getOptions().nativeHandle_, var2));
   }

   protected TtlDB(long var1) {
      super(var1);
   }

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native long open(long var0, String var2, int var3, boolean var4) throws RocksDBException;

   private static native long[] openCF(long var0, String var2, byte[][] var3, long[] var4, int[] var5, boolean var6) throws RocksDBException;

   private static native long createColumnFamilyWithTtl(long var0, byte[] var2, long var3, int var5) throws RocksDBException;

   private static native void closeDatabase(long var0) throws RocksDBException;
}
