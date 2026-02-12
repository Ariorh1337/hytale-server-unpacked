package org.rocksdb;

import java.util.Arrays;
import java.util.List;

public class OptimisticTransactionDB extends RocksDB implements TransactionalDB<OptimisticTransactionOptions> {
   private OptimisticTransactionDB(long var1) {
      super(var1);
   }

   public static OptimisticTransactionDB open(Options var0, String var1) throws RocksDBException {
      OptimisticTransactionDB var2 = new OptimisticTransactionDB(open(var0.nativeHandle_, var1));
      var2.storeOptionsInstance(var0);
      var2.storeDefaultColumnFamilyHandle(var2.makeDefaultColumnFamilyHandle());
      return var2;
   }

   public static OptimisticTransactionDB open(DBOptions var0, String var1, List<ColumnFamilyDescriptor> var2, List<ColumnFamilyHandle> var3) throws RocksDBException {
      int var4 = -1;
      byte[][] var5 = new byte[var2.size()][];
      long[] var6 = new long[var2.size()];

      for (int var7 = 0; var7 < var2.size(); var7++) {
         ColumnFamilyDescriptor var8 = (ColumnFamilyDescriptor)var2.get(var7);
         var5[var7] = var8.getName();
         var6[var7] = var8.getOptions().nativeHandle_;
         if (Arrays.equals(var8.getName(), RocksDB.DEFAULT_COLUMN_FAMILY)) {
            var4 = var7;
         }
      }

      if (var4 < 0) {
         throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
      }

      long[] var10 = open(var0.nativeHandle_, var1, var5, var6);
      OptimisticTransactionDB var11 = new OptimisticTransactionDB(var10[0]);
      var11.storeOptionsInstance(var0);

      for (int var9 = 1; var9 < var10.length; var9++) {
         var3.add(new ColumnFamilyHandle(var11, var10[var9]));
      }

      var11.ownedColumnFamilyHandles.addAll(var3);
      var11.storeDefaultColumnFamilyHandle((ColumnFamilyHandle)var3.get(var4));
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

   @Override
   public Transaction beginTransaction(WriteOptions var1) {
      return new Transaction(this, beginTransaction(this.nativeHandle_, var1.nativeHandle_));
   }

   public Transaction beginTransaction(WriteOptions var1, OptimisticTransactionOptions var2) {
      return new Transaction(this, beginTransaction(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_));
   }

   @Override
   public Transaction beginTransaction(WriteOptions var1, Transaction var2) {
      long var3 = beginTransaction_withOld(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_);
      assert var3 == var2.nativeHandle_;
      return var2;
   }

   public Transaction beginTransaction(WriteOptions var1, OptimisticTransactionOptions var2, Transaction var3) {
      long var4 = beginTransaction_withOld(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_, var3.nativeHandle_);
      assert var4 == var3.nativeHandle_;
      return var3;
   }

   public RocksDB getBaseDB() {
      RocksDB var1 = new RocksDB(getBaseDB(this.nativeHandle_));
      var1.disOwnNativeHandle();
      return var1;
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   protected static native long open(long var0, String var2) throws RocksDBException;

   protected static native long[] open(long var0, String var2, byte[][] var3, long[] var4);

   private static native void closeDatabase(long var0) throws RocksDBException;

   private static native long beginTransaction(long var0, long var2);

   private static native long beginTransaction(long var0, long var2, long var4);

   private static native long beginTransaction_withOld(long var0, long var2, long var4);

   private static native long beginTransaction_withOld(long var0, long var2, long var4, long var6);

   private static native long getBaseDB(long var0);
}
