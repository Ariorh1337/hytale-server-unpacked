package org.rocksdb;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Transaction extends RocksObject {
   private static final String FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE = "For each key there must be a ColumnFamilyHandle.";
   private static final String BB_ALL_DIRECT_OR_INDIRECT = "ByteBuffer parameters must all be direct, or must all be indirect";
   private final RocksDB parent;
   private final ColumnFamilyHandle defaultColumnFamilyHandle;

   Transaction(RocksDB var1, long var2) {
      super(var2);
      this.parent = var1;
      this.defaultColumnFamilyHandle = var1.getDefaultColumnFamily();
   }

   public void setSnapshot() {
      assert this.isOwningHandle();
      setSnapshot(this.nativeHandle_);
   }

   public void setSnapshotOnNextOperation() {
      assert this.isOwningHandle();
      setSnapshotOnNextOperation(this.nativeHandle_);
   }

   public void setSnapshotOnNextOperation(AbstractTransactionNotifier var1) {
      assert this.isOwningHandle();
      setSnapshotOnNextOperation(this.nativeHandle_, var1.nativeHandle_);
   }

   public Snapshot getSnapshot() {
      assert this.isOwningHandle();
      long var1 = getSnapshot(this.nativeHandle_);
      return var1 == 0L ? null : new Snapshot(var1);
   }

   public void clearSnapshot() {
      assert this.isOwningHandle();
      clearSnapshot(this.nativeHandle_);
   }

   public void prepare() throws RocksDBException {
      assert this.isOwningHandle();
      prepare(this.nativeHandle_);
   }

   public void commit() throws RocksDBException {
      assert this.isOwningHandle();
      commit(this.nativeHandle_);
   }

   public void rollback() throws RocksDBException {
      assert this.isOwningHandle();
      rollback(this.nativeHandle_);
   }

   public void setSavePoint() throws RocksDBException {
      assert this.isOwningHandle();
      setSavePoint(this.nativeHandle_);
   }

   public void rollbackToSavePoint() throws RocksDBException {
      assert this.isOwningHandle();
      rollbackToSavePoint(this.nativeHandle_);
   }

   @Deprecated
   public byte[] get(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      return get(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var1.nativeHandle_);
   }

   public byte[] get(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      return get(this.nativeHandle_, var1.nativeHandle_, var3, 0, var3.length, var2.nativeHandle_);
   }

   public byte[] get(ReadOptions var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      return get(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, this.defaultColumnFamilyHandle.nativeHandle_);
   }

   public GetStatus get(ReadOptions var1, byte[] var2, byte[] var3) throws RocksDBException {
      int var4 = get(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, this.defaultColumnFamilyHandle.nativeHandle_);
      return var4 < 0 ? GetStatus.fromStatusCode(Status.Code.NotFound, 0) : GetStatus.fromStatusCode(Status.Code.Ok, var4);
   }

   public GetStatus get(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3, byte[] var4) throws RocksDBException {
      int var5 = get(this.nativeHandle_, var1.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var2.nativeHandle_);
      return var5 < 0 ? GetStatus.fromStatusCode(Status.Code.NotFound, 0) : GetStatus.fromStatusCode(Status.Code.Ok, var5);
   }

   public GetStatus get(ReadOptions var1, ColumnFamilyHandle var2, ByteBuffer var3, ByteBuffer var4) throws RocksDBException {
      int var5;
      if (var3.isDirect() && var4.isDirect()) {
         var5 = getDirect(
            this.nativeHandle_, var1.nativeHandle_, var3, var3.position(), var3.remaining(), var4, var4.position(), var4.remaining(), var2.nativeHandle_
         );
      } else {
         if (var3.isDirect() || var4.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var3.hasArray();
         assert var4.hasArray();
         var5 = get(
            this.nativeHandle_,
            var1.nativeHandle_,
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var4.array(),
            var4.arrayOffset() + var4.position(),
            var4.remaining(),
            var2.nativeHandle_
         );
      }

      ((Buffer)var3).position(var3.limit());
      if (var5 < 0) {
         return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
      }

      ((Buffer)var4).position(Math.min(var4.limit(), var4.position() + var5));
      return GetStatus.fromStatusCode(Status.Code.Ok, var5);
   }

   public GetStatus get(ReadOptions var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      return this.get(var1, this.defaultColumnFamilyHandle, var2, var3);
   }

   @Deprecated
   public byte[][] multiGet(ReadOptions var1, List<ColumnFamilyHandle> var2, byte[][] var3) throws RocksDBException {
      assert this.isOwningHandle();
      if (var3.length != var2.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      if (var3.length == 0) {
         return new byte[0][0];
      }

      long[] var4 = new long[var2.size()];

      for (int var5 = 0; var5 < var2.size(); var5++) {
         var4[var5] = ((ColumnFamilyHandle)var2.get(var5)).nativeHandle_;
      }

      return multiGet(this.nativeHandle_, var1.nativeHandle_, var3, var4);
   }

   public List<byte[]> multiGetAsList(ReadOptions var1, List<ColumnFamilyHandle> var2, List<byte[]> var3) throws RocksDBException {
      assert this.isOwningHandle();
      if (var3.size() != var2.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      if (var3.isEmpty()) {
         return new ArrayList<>(0);
      }

      byte[][] var4 = var3.toArray(new byte[var3.size()][]);
      long[] var5 = new long[var2.size()];

      for (int var6 = 0; var6 < var2.size(); var6++) {
         var5[var6] = ((ColumnFamilyHandle)var2.get(var6)).nativeHandle_;
      }

      return Arrays.asList(multiGet(this.nativeHandle_, var1.nativeHandle_, var4, var5));
   }

   @Deprecated
   public byte[][] multiGet(ReadOptions var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      return var2.length == 0 ? new byte[0][0] : multiGet(this.nativeHandle_, var1.nativeHandle_, var2);
   }

   public List<byte[]> multiGetAsList(ReadOptions var1, List<byte[]> var2) throws RocksDBException {
      if (var2.isEmpty()) {
         return new ArrayList<>(0);
      }

      byte[][] var3 = var2.toArray(new byte[var2.size()][]);
      return Arrays.asList(multiGet(this.nativeHandle_, var1.nativeHandle_, var3));
   }

   public List<byte[]> multiGetAsList(ReadOptions var1, ColumnFamilyHandle var2, List<byte[]> var3) throws RocksDBException {
      if (var3.isEmpty()) {
         return new ArrayList<>(0);
      }

      byte[][] var4 = var3.toArray(new byte[var3.size()][]);
      return Arrays.asList(multiGet(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_, var4));
   }

   public byte[] getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3, boolean var4, boolean var5) throws RocksDBException {
      assert this.isOwningHandle();
      return getForUpdate(this.nativeHandle_, var1.nativeHandle_, var3, 0, var3.length, var2.nativeHandle_, var4, var5);
   }

   public byte[] getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      return getForUpdate(this.nativeHandle_, var1.nativeHandle_, var3, 0, var3.length, var2.nativeHandle_, var4, true);
   }

   public byte[] getForUpdate(ReadOptions var1, byte[] var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      return getForUpdate(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, this.defaultColumnFamilyHandle.nativeHandle_, var3, true);
   }

   public GetStatus getForUpdate(ReadOptions var1, byte[] var2, byte[] var3, boolean var4) throws RocksDBException {
      int var5 = getForUpdate(
         this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, this.defaultColumnFamilyHandle.nativeHandle_, var4, true
      );
      return var5 < 0 ? GetStatus.fromStatusCode(Status.Code.NotFound, 0) : GetStatus.fromStatusCode(Status.Code.Ok, var5);
   }

   public GetStatus getForUpdate(ReadOptions var1, ByteBuffer var2, ByteBuffer var3, boolean var4) throws RocksDBException {
      return this.getForUpdate(var1, this.defaultColumnFamilyHandle, var2, var3, var4, true);
   }

   public GetStatus getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3, byte[] var4, boolean var5) throws RocksDBException {
      return this.getForUpdate(var1, var2, var3, var4, var5, true);
   }

   public GetStatus getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, byte[] var3, byte[] var4, boolean var5, boolean var6) throws RocksDBException {
      int var7 = getForUpdate(this.nativeHandle_, var1.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var2.nativeHandle_, var5, var6);
      return var7 < 0 ? GetStatus.fromStatusCode(Status.Code.NotFound, 0) : GetStatus.fromStatusCode(Status.Code.Ok, var7);
   }

   public GetStatus getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, ByteBuffer var3, ByteBuffer var4, boolean var5) throws RocksDBException {
      return this.getForUpdate(var1, var2, var3, var4, var5, true);
   }

   public GetStatus getForUpdate(ReadOptions var1, ColumnFamilyHandle var2, ByteBuffer var3, ByteBuffer var4, boolean var5, boolean var6) throws RocksDBException {
      int var7;
      if (var3.isDirect() && var4.isDirect()) {
         var7 = getDirectForUpdate(
            this.nativeHandle_,
            var1.nativeHandle_,
            var3,
            var3.position(),
            var3.remaining(),
            var4,
            var4.position(),
            var4.remaining(),
            var2.nativeHandle_,
            var5,
            var6
         );
      } else {
         if (var3.isDirect() || var4.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var3.hasArray();
         assert var4.hasArray();
         var7 = getForUpdate(
            this.nativeHandle_,
            var1.nativeHandle_,
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var4.array(),
            var4.arrayOffset() + var4.position(),
            var4.remaining(),
            var2.nativeHandle_,
            var5,
            var6
         );
      }

      ((Buffer)var3).position(var3.limit());
      if (var7 < 0) {
         return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
      }

      ((Buffer)var4).position(Math.min(var4.limit(), var4.position() + var7));
      return GetStatus.fromStatusCode(Status.Code.Ok, var7);
   }

   @Deprecated
   public byte[][] multiGetForUpdate(ReadOptions var1, List<ColumnFamilyHandle> var2, byte[][] var3) throws RocksDBException {
      assert this.isOwningHandle();
      if (var3.length != var2.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      if (var3.length == 0) {
         return new byte[0][0];
      }

      long[] var4 = new long[var2.size()];

      for (int var5 = 0; var5 < var2.size(); var5++) {
         var4[var5] = ((ColumnFamilyHandle)var2.get(var5)).nativeHandle_;
      }

      return multiGetForUpdate(this.nativeHandle_, var1.nativeHandle_, var3, var4);
   }

   public List<byte[]> multiGetForUpdateAsList(ReadOptions var1, List<ColumnFamilyHandle> var2, List<byte[]> var3) throws RocksDBException {
      assert this.isOwningHandle();
      if (var3.size() != var2.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      if (var3.isEmpty()) {
         return new ArrayList<>();
      }

      byte[][] var4 = var3.toArray(new byte[var3.size()][]);
      long[] var5 = new long[var2.size()];

      for (int var6 = 0; var6 < var2.size(); var6++) {
         var5[var6] = ((ColumnFamilyHandle)var2.get(var6)).nativeHandle_;
      }

      return Arrays.asList(multiGetForUpdate(this.nativeHandle_, var1.nativeHandle_, var4, var5));
   }

   @Deprecated
   public byte[][] multiGetForUpdate(ReadOptions var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      return var2.length == 0 ? new byte[0][0] : multiGetForUpdate(this.nativeHandle_, var1.nativeHandle_, var2);
   }

   public List<byte[]> multiGetForUpdateAsList(ReadOptions var1, List<byte[]> var2) throws RocksDBException {
      assert this.isOwningHandle();
      if (var2.isEmpty()) {
         return new ArrayList<>(0);
      }

      byte[][] var3 = var2.toArray(new byte[var2.size()][]);
      return Arrays.asList(multiGetForUpdate(this.nativeHandle_, var1.nativeHandle_, var3));
   }

   public RocksIterator getIterator() {
      assert this.isOwningHandle();

      try (ReadOptions var1 = new ReadOptions()) {
         return new RocksIterator(this.parent, getIterator(this.nativeHandle_, var1.nativeHandle_, this.defaultColumnFamilyHandle.nativeHandle_));
      }
   }

   public RocksIterator getIterator(ReadOptions var1) {
      assert this.isOwningHandle();
      return new RocksIterator(this.parent, getIterator(this.nativeHandle_, var1.nativeHandle_, this.defaultColumnFamilyHandle.nativeHandle_));
   }

   public RocksIterator getIterator(ReadOptions var1, ColumnFamilyHandle var2) {
      assert this.isOwningHandle();
      return new RocksIterator(this.parent, getIterator(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_));
   }

   public RocksIterator getIterator(ColumnFamilyHandle var1) {
      assert this.isOwningHandle();

      try (ReadOptions var2 = new ReadOptions()) {
         return new RocksIterator(this.parent, getIterator(this.nativeHandle_, var2.nativeHandle_, var1.nativeHandle_));
      }
   }

   public void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_, var4);
   }

   public void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_, false);
   }

   public void put(byte[] var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public void put(ColumnFamilyHandle var1, byte[][] var2, byte[][] var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_, var4);
   }

   public void put(ColumnFamilyHandle var1, byte[][] var2, byte[][] var3) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_, false);
   }

   public void put(ByteBuffer var1, ByteBuffer var2) throws RocksDBException {
      assert this.isOwningHandle();
      if (var1.isDirect() && var2.isDirect()) {
         putDirect(this.nativeHandle_, var1, var1.position(), var1.remaining(), var2, var2.position(), var2.remaining());
      } else {
         if (var1.isDirect() || var2.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var1.hasArray();
         assert var2.hasArray();
         put(
            this.nativeHandle_,
            var1.array(),
            var1.arrayOffset() + var1.position(),
            var1.remaining(),
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining()
         );
      }

      ((Buffer)var1).position(var1.limit());
      ((Buffer)var2).position(var2.limit());
   }

   public void put(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      if (var2.isDirect() && var3.isDirect()) {
         putDirect(this.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), var1.nativeHandle_, var4);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var2.hasArray();
         assert var3.hasArray();
         put(
            this.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var1.nativeHandle_,
            var4
         );
      }

      ((Buffer)var2).position(var2.limit());
      ((Buffer)var3).position(var3.limit());
   }

   public void put(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      this.put(var1, var2, var3, false);
   }

   public void put(byte[][] var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      put(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   public void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      merge(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_, var4);
   }

   public void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      merge(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_, false);
   }

   public void merge(byte[] var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      merge(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public void merge(ByteBuffer var1, ByteBuffer var2) throws RocksDBException {
      assert this.isOwningHandle();
      if (var1.isDirect() && var2.isDirect()) {
         mergeDirect(this.nativeHandle_, var1, var1.position(), var1.remaining(), var2, var2.position(), var2.remaining());
      } else {
         if (var1.isDirect() || var2.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var1.hasArray();
         assert var2.hasArray();
         merge(
            this.nativeHandle_,
            var1.array(),
            var1.arrayOffset() + var1.position(),
            var1.remaining(),
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining()
         );
      }
   }

   public void merge(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3, boolean var4) throws RocksDBException {
      assert this.isOwningHandle();
      if (var2.isDirect() && var3.isDirect()) {
         mergeDirect(this.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), var1.nativeHandle_, var4);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var2.hasArray();
         assert var3.hasArray();
         merge(
            this.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var1.nativeHandle_,
            var4
         );
      }

      ((Buffer)var2).position(var2.limit());
      ((Buffer)var3).position(var3.limit());
   }

   public void merge(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      this.merge(var1, var2, var3, false);
   }

   public void delete(ColumnFamilyHandle var1, byte[] var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, var3);
   }

   public void delete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, false);
   }

   public void delete(byte[] var1) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var1, var1.length);
   }

   public void delete(ColumnFamilyHandle var1, byte[][] var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, var3);
   }

   public void delete(ColumnFamilyHandle var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, false);
   }

   public void delete(byte[][] var1) throws RocksDBException {
      assert this.isOwningHandle();
      delete(this.nativeHandle_, var1, var1.length);
   }

   public void singleDelete(ColumnFamilyHandle var1, byte[] var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, var3);
   }

   public void singleDelete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, false);
   }

   public void singleDelete(byte[] var1) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var1, var1.length);
   }

   public void singleDelete(ColumnFamilyHandle var1, byte[][] var2, boolean var3) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, var3);
   }

   public void singleDelete(ColumnFamilyHandle var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_, false);
   }

   public void singleDelete(byte[][] var1) throws RocksDBException {
      assert this.isOwningHandle();
      singleDelete(this.nativeHandle_, var1, var1.length);
   }

   public void putUntracked(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      putUntracked(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_);
   }

   public void putUntracked(byte[] var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      putUntracked(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   public void putUntracked(ColumnFamilyHandle var1, byte[][] var2, byte[][] var3) throws RocksDBException {
      assert this.isOwningHandle();
      putUntracked(this.nativeHandle_, var2, var2.length, var3, var3.length, var1.nativeHandle_);
   }

   public void putUntracked(byte[][] var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      putUntracked(this.nativeHandle_, var1, var1.length, var2, var2.length);
   }

   public void mergeUntracked(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      assert this.isOwningHandle();
      mergeUntracked(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_);
   }

   public void mergeUntracked(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      assert this.isOwningHandle();
      if (var2.isDirect() && var3.isDirect()) {
         mergeUntrackedDirect(this.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), var1.nativeHandle_);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var2.hasArray();
         assert var3.hasArray();
         mergeUntracked(
            this.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var1.nativeHandle_
         );
      }

      ((Buffer)var2).position(var2.limit());
      ((Buffer)var3).position(var3.limit());
   }

   public void mergeUntracked(byte[] var1, byte[] var2) throws RocksDBException {
      this.mergeUntracked(this.defaultColumnFamilyHandle, var1, var2);
   }

   public void mergeUntracked(ByteBuffer var1, ByteBuffer var2) throws RocksDBException {
      this.mergeUntracked(this.defaultColumnFamilyHandle, var1, var2);
   }

   public void deleteUntracked(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      assert this.isOwningHandle();
      deleteUntracked(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   public void deleteUntracked(byte[] var1) throws RocksDBException {
      assert this.isOwningHandle();
      deleteUntracked(this.nativeHandle_, var1, var1.length);
   }

   public void deleteUntracked(ColumnFamilyHandle var1, byte[][] var2) throws RocksDBException {
      assert this.isOwningHandle();
      deleteUntracked(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   public void deleteUntracked(byte[][] var1) throws RocksDBException {
      assert this.isOwningHandle();
      deleteUntracked(this.nativeHandle_, var1, var1.length);
   }

   public void putLogData(byte[] var1) {
      assert this.isOwningHandle();
      putLogData(this.nativeHandle_, var1, var1.length);
   }

   public void disableIndexing() {
      assert this.isOwningHandle();
      disableIndexing(this.nativeHandle_);
   }

   public void enableIndexing() {
      assert this.isOwningHandle();
      enableIndexing(this.nativeHandle_);
   }

   public long getNumKeys() {
      assert this.isOwningHandle();
      return getNumKeys(this.nativeHandle_);
   }

   public long getNumPuts() {
      assert this.isOwningHandle();
      return getNumPuts(this.nativeHandle_);
   }

   public long getNumDeletes() {
      assert this.isOwningHandle();
      return getNumDeletes(this.nativeHandle_);
   }

   public long getNumMerges() {
      assert this.isOwningHandle();
      return getNumMerges(this.nativeHandle_);
   }

   public long getElapsedTime() {
      assert this.isOwningHandle();
      return getElapsedTime(this.nativeHandle_);
   }

   public WriteBatchWithIndex getWriteBatch() {
      assert this.isOwningHandle();
      return new WriteBatchWithIndex(getWriteBatch(this.nativeHandle_));
   }

   public void setLockTimeout(long var1) {
      assert this.isOwningHandle();
      setLockTimeout(this.nativeHandle_, var1);
   }

   public WriteOptions getWriteOptions() {
      assert this.isOwningHandle();
      return new WriteOptions(getWriteOptions(this.nativeHandle_));
   }

   public void setWriteOptions(WriteOptions var1) {
      assert this.isOwningHandle();
      setWriteOptions(this.nativeHandle_, var1.nativeHandle_);
   }

   public void undoGetForUpdate(ColumnFamilyHandle var1, byte[] var2) {
      assert this.isOwningHandle();
      undoGetForUpdate(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   public void undoGetForUpdate(byte[] var1) {
      assert this.isOwningHandle();
      undoGetForUpdate(this.nativeHandle_, var1, var1.length);
   }

   public void rebuildFromWriteBatch(WriteBatch var1) throws RocksDBException {
      assert this.isOwningHandle();
      rebuildFromWriteBatch(this.nativeHandle_, var1.nativeHandle_);
   }

   public WriteBatch getCommitTimeWriteBatch() {
      assert this.isOwningHandle();
      return new WriteBatch(getCommitTimeWriteBatch(this.nativeHandle_));
   }

   public void setLogNumber(long var1) {
      assert this.isOwningHandle();
      setLogNumber(this.nativeHandle_, var1);
   }

   public long getLogNumber() {
      assert this.isOwningHandle();
      return getLogNumber(this.nativeHandle_);
   }

   public void setName(String var1) throws RocksDBException {
      assert this.isOwningHandle();
      setName(this.nativeHandle_, var1);
   }

   public String getName() {
      assert this.isOwningHandle();
      return getName(this.nativeHandle_);
   }

   public long getID() {
      assert this.isOwningHandle();
      return getID(this.nativeHandle_);
   }

   public boolean isDeadlockDetect() {
      assert this.isOwningHandle();
      return isDeadlockDetect(this.nativeHandle_);
   }

   public Transaction.WaitingTransactions getWaitingTxns() {
      assert this.isOwningHandle();
      return this.getWaitingTxns(this.nativeHandle_);
   }

   public Transaction.TransactionState getState() {
      assert this.isOwningHandle();
      return Transaction.TransactionState.getTransactionState(getState(this.nativeHandle_));
   }

   public long getId() {
      assert this.isOwningHandle();
      return getId(this.nativeHandle_);
   }

   private Transaction.WaitingTransactions newWaitingTransactions(long var1, String var3, long[] var4) {
      return new Transaction.WaitingTransactions(var1, var3, var4);
   }

   private static native void setSnapshot(long var0);

   private static native void setSnapshotOnNextOperation(long var0);

   private static native void setSnapshotOnNextOperation(long var0, long var2);

   private static native long getSnapshot(long var0);

   private static native void clearSnapshot(long var0);

   private static native void prepare(long var0) throws RocksDBException;

   private static native void commit(long var0) throws RocksDBException;

   private static native void rollback(long var0) throws RocksDBException;

   private static native void setSavePoint(long var0) throws RocksDBException;

   private static native void rollbackToSavePoint(long var0) throws RocksDBException;

   private static native byte[] get(long var0, long var2, byte[] var4, int var5, int var6, long var7) throws RocksDBException;

   private static native int get(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10) throws RocksDBException;

   private static native int getDirect(long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10) throws RocksDBException;

   private static native byte[][] multiGet(long var0, long var2, byte[][] var4, long[] var5) throws RocksDBException;

   private static native byte[][] multiGet(long var0, long var2, byte[][] var4) throws RocksDBException;

   private static native byte[][] multiGet(long var0, long var2, long var4, byte[][] var6) throws RocksDBException;

   private static native byte[] getForUpdate(long var0, long var2, byte[] var4, int var5, int var6, long var7, boolean var9, boolean var10) throws RocksDBException;

   private static native int getForUpdate(
      long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10, boolean var12, boolean var13
   ) throws RocksDBException;

   private static native int getDirectForUpdate(
      long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10, boolean var12, boolean var13
   ) throws RocksDBException;

   private static native byte[][] multiGetForUpdate(long var0, long var2, byte[][] var4, long[] var5) throws RocksDBException;

   private static native byte[][] multiGetForUpdate(long var0, long var2, byte[][] var4) throws RocksDBException;

   private static native long getIterator(long var0, long var2, long var4);

   private static native void put(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native void put(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8, boolean var10) throws RocksDBException;

   private static native void put(long var0, byte[][] var2, int var3, byte[][] var4, int var5, long var6, boolean var8) throws RocksDBException;

   private static native void put(long var0, byte[][] var2, int var3, byte[][] var4, int var5) throws RocksDBException;

   private static native void putDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8, boolean var10) throws RocksDBException;

   private static native void putDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7) throws RocksDBException;

   private static native void merge(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8, boolean var10) throws RocksDBException;

   private static native void mergeDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8, boolean var10) throws RocksDBException;

   private static native void mergeDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7) throws RocksDBException;

   private static native void merge(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native void delete(long var0, byte[] var2, int var3, long var4, boolean var6) throws RocksDBException;

   private static native void delete(long var0, byte[] var2, int var3) throws RocksDBException;

   private static native void delete(long var0, byte[][] var2, int var3, long var4, boolean var6) throws RocksDBException;

   private static native void delete(long var0, byte[][] var2, int var3) throws RocksDBException;

   private static native void singleDelete(long var0, byte[] var2, int var3, long var4, boolean var6) throws RocksDBException;

   private static native void singleDelete(long var0, byte[] var2, int var3) throws RocksDBException;

   private static native void singleDelete(long var0, byte[][] var2, int var3, long var4, boolean var6) throws RocksDBException;

   private static native void singleDelete(long var0, byte[][] var2, int var3) throws RocksDBException;

   private static native void putUntracked(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6) throws RocksDBException;

   private static native void putUntracked(long var0, byte[] var2, int var3, byte[] var4, int var5) throws RocksDBException;

   private static native void putUntracked(long var0, byte[][] var2, int var3, byte[][] var4, int var5, long var6) throws RocksDBException;

   private static native void putUntracked(long var0, byte[][] var2, int var3, byte[][] var4, int var5) throws RocksDBException;

   private static native void mergeUntracked(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8) throws RocksDBException;

   private static native void mergeUntrackedDirect(long var0, ByteBuffer var2, int var3, int var4, ByteBuffer var5, int var6, int var7, long var8) throws RocksDBException;

   private static native void deleteUntracked(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   private static native void deleteUntracked(long var0, byte[] var2, int var3) throws RocksDBException;

   private static native void deleteUntracked(long var0, byte[][] var2, int var3, long var4) throws RocksDBException;

   private static native void deleteUntracked(long var0, byte[][] var2, int var3) throws RocksDBException;

   private static native void putLogData(long var0, byte[] var2, int var3);

   private static native void disableIndexing(long var0);

   private static native void enableIndexing(long var0);

   private static native long getNumKeys(long var0);

   private static native long getNumPuts(long var0);

   private static native long getNumDeletes(long var0);

   private static native long getNumMerges(long var0);

   private static native long getElapsedTime(long var0);

   private static native long getWriteBatch(long var0);

   private static native void setLockTimeout(long var0, long var2);

   private static native long getWriteOptions(long var0);

   private static native void setWriteOptions(long var0, long var2);

   private static native void undoGetForUpdate(long var0, byte[] var2, int var3, long var4);

   private static native void undoGetForUpdate(long var0, byte[] var2, int var3);

   private static native void rebuildFromWriteBatch(long var0, long var2) throws RocksDBException;

   private static native long getCommitTimeWriteBatch(long var0);

   private static native void setLogNumber(long var0, long var2);

   private static native long getLogNumber(long var0);

   private static native void setName(long var0, String var2) throws RocksDBException;

   private static native String getName(long var0);

   private static native long getID(long var0);

   private static native boolean isDeadlockDetect(long var0);

   private native Transaction.WaitingTransactions getWaitingTxns(long var1);

   private static native byte getState(long var0);

   private static native long getId(long var0);

   @Override
   protected final native void disposeInternal(long var1);

   public enum TransactionState {
      STARTED((byte)0),
      AWAITING_PREPARE((byte)1),
      PREPARED((byte)2),
      AWAITING_COMMIT((byte)3),
      COMMITTED((byte)4),
      AWAITING_ROLLBACK((byte)5),
      ROLLEDBACK((byte)6),
      LOCKS_STOLEN((byte)7);

      public static final Transaction.TransactionState COMMITED = COMMITTED;
      private final byte value;

      TransactionState(byte var3) {
         this.value = var3;
      }

      public static Transaction.TransactionState getTransactionState(byte var0) {
         for (Transaction.TransactionState var4 : values()) {
            if (var4.value == var0) {
               return var4;
            }
         }

         throw new IllegalArgumentException("Illegal value provided for TransactionState.");
      }
   }

   public static class WaitingTransactions {
      private final long columnFamilyId;
      private final String key;
      private final long[] transactionIds;

      private WaitingTransactions(long var1, String var3, long[] var4) {
         this.columnFamilyId = var1;
         this.key = var3;
         this.transactionIds = var4;
      }

      public long getColumnFamilyId() {
         return this.columnFamilyId;
      }

      public String getKey() {
         return this.key;
      }

      public long[] getTransactionIds() {
         return this.transactionIds;
      }
   }
}
