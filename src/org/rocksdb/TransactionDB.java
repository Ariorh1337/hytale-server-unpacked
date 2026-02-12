package org.rocksdb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TransactionDB extends RocksDB implements TransactionalDB<TransactionOptions> {
   private TransactionDBOptions transactionDbOptions_;

   private TransactionDB(long var1) {
      super(var1);
   }

   public static TransactionDB open(Options var0, TransactionDBOptions var1, String var2) throws RocksDBException {
      TransactionDB var3 = new TransactionDB(open(var0.nativeHandle_, var1.nativeHandle_, var2));
      var3.storeOptionsInstance(var0);
      var3.storeTransactionDbOptions(var1);
      var3.storeDefaultColumnFamilyHandle(var3.makeDefaultColumnFamilyHandle());
      return var3;
   }

   public static TransactionDB open(DBOptions var0, TransactionDBOptions var1, String var2, List<ColumnFamilyDescriptor> var3, List<ColumnFamilyHandle> var4) throws RocksDBException {
      int var5 = -1;
      byte[][] var6 = new byte[var3.size()][];
      long[] var7 = new long[var3.size()];

      for (int var8 = 0; var8 < var3.size(); var8++) {
         ColumnFamilyDescriptor var9 = (ColumnFamilyDescriptor)var3.get(var8);
         var6[var8] = var9.getName();
         var7[var8] = var9.getOptions().nativeHandle_;
         if (Arrays.equals(var9.getName(), RocksDB.DEFAULT_COLUMN_FAMILY)) {
            var5 = var8;
         }
      }

      if (var5 < 0) {
         throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
      }

      long[] var11 = open(var0.nativeHandle_, var1.nativeHandle_, var2, var6, var7);
      TransactionDB var12 = new TransactionDB(var11[0]);
      var12.storeOptionsInstance(var0);
      var12.storeTransactionDbOptions(var1);

      for (int var10 = 1; var10 < var11.length; var10++) {
         var4.add(new ColumnFamilyHandle(var12, var11[var10]));
      }

      var12.ownedColumnFamilyHandles.addAll(var4);
      var12.storeDefaultColumnFamilyHandle((ColumnFamilyHandle)var4.get(var5));
      return var12;
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

   public Transaction beginTransaction(WriteOptions var1, TransactionOptions var2) {
      return new Transaction(this, beginTransaction(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_));
   }

   @Override
   public Transaction beginTransaction(WriteOptions var1, Transaction var2) {
      long var3 = beginTransaction_withOld(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_);
      assert var3 == var2.nativeHandle_;
      return var2;
   }

   public Transaction beginTransaction(WriteOptions var1, TransactionOptions var2, Transaction var3) {
      long var4 = beginTransaction_withOld(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_, var3.nativeHandle_);
      assert var4 == var3.nativeHandle_;
      return var3;
   }

   public Transaction getTransactionByName(String var1) {
      long var2 = getTransactionByName(this.nativeHandle_, var1);
      if (var2 == 0L) {
         return null;
      }

      Transaction var4 = new Transaction(this, var2);
      var4.disOwnNativeHandle();
      return var4;
   }

   public List<Transaction> getAllPreparedTransactions() {
      long[] var1 = getAllPreparedTransactions(this.nativeHandle_);
      ArrayList var2 = new ArrayList();

      for (long var6 : var1) {
         Transaction var8 = new Transaction(this, var6);
         var8.disOwnNativeHandle();
         var2.add(var8);
      }

      return var2;
   }

   public Map<Long, TransactionDB.KeyLockInfo> getLockStatusData() {
      return getLockStatusData(this.nativeHandle_);
   }

   private TransactionDB.DeadlockInfo newDeadlockInfo(long var1, long var3, String var5, boolean var6) {
      return new TransactionDB.DeadlockInfo(var1, var3, var5, var6);
   }

   public TransactionDB.DeadlockPath[] getDeadlockInfoBuffer() {
      return getDeadlockInfoBuffer(this.nativeHandle_);
   }

   public void setDeadlockInfoBufferSize(int var1) {
      setDeadlockInfoBufferSize(this.nativeHandle_, var1);
   }

   private void storeTransactionDbOptions(TransactionDBOptions var1) {
      this.transactionDbOptions_ = var1;
   }

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native long open(long var0, long var2, String var4) throws RocksDBException;

   private static native long[] open(long var0, long var2, String var4, byte[][] var5, long[] var6);

   private static native void closeDatabase(long var0) throws RocksDBException;

   private static native long beginTransaction(long var0, long var2);

   private static native long beginTransaction(long var0, long var2, long var4);

   private static native long beginTransaction_withOld(long var0, long var2, long var4);

   private static native long beginTransaction_withOld(long var0, long var2, long var4, long var6);

   private static native long getTransactionByName(long var0, String var2);

   private static native long[] getAllPreparedTransactions(long var0);

   private static native Map<Long, TransactionDB.KeyLockInfo> getLockStatusData(long var0);

   private static native TransactionDB.DeadlockPath[] getDeadlockInfoBuffer(long var0);

   private static native void setDeadlockInfoBufferSize(long var0, int var2);

   public static class DeadlockInfo {
      private final long transactionID;
      private final long columnFamilyId;
      private final String waitingKey;
      private final boolean exclusive;

      private DeadlockInfo(long var1, long var3, String var5, boolean var6) {
         this.transactionID = var1;
         this.columnFamilyId = var3;
         this.waitingKey = var5;
         this.exclusive = var6;
      }

      public long getTransactionID() {
         return this.transactionID;
      }

      public long getColumnFamilyId() {
         return this.columnFamilyId;
      }

      public String getWaitingKey() {
         return this.waitingKey;
      }

      public boolean isExclusive() {
         return this.exclusive;
      }
   }

   public static class DeadlockPath {
      final TransactionDB.DeadlockInfo[] path;
      final boolean limitExceeded;

      public DeadlockPath(TransactionDB.DeadlockInfo[] var1, boolean var2) {
         this.path = var1;
         this.limitExceeded = var2;
      }

      public boolean isEmpty() {
         return this.path.length == 0 && !this.limitExceeded;
      }
   }

   public static class KeyLockInfo {
      private final String key;
      private final long[] transactionIDs;
      private final boolean exclusive;

      public KeyLockInfo(String var1, long[] var2, boolean var3) {
         this.key = var1;
         this.transactionIDs = var2;
         this.exclusive = var3;
      }

      public String getKey() {
         return this.key;
      }

      public long[] getTransactionIDs() {
         return this.transactionIDs;
      }

      public boolean isExclusive() {
         return this.exclusive;
      }
   }
}
