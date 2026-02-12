package org.rocksdb;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.rocksdb.util.BufferUtil;
import org.rocksdb.util.Environment;

public class RocksDB extends RocksObject {
   public static final byte[] DEFAULT_COLUMN_FAMILY = "default".getBytes(StandardCharsets.UTF_8);
   public static final int NOT_FOUND = -1;
   private static final AtomicReference<RocksDB.LibraryState> libraryLoaded = new AtomicReference<>(RocksDB.LibraryState.NOT_LOADED);
   static final String PERFORMANCE_OPTIMIZATION_FOR_A_VERY_SPECIFIC_WORKLOAD = "Performance optimization for a very specific workload";
   private static final String BB_ALL_DIRECT_OR_INDIRECT = "ByteBuffer parameters must all be direct, or must all be indirect";
   private ColumnFamilyHandle defaultColumnFamilyHandle_;
   private final ReadOptions defaultReadOptions_ = new ReadOptions();
   final List<ColumnFamilyHandle> ownedColumnFamilyHandles = new ArrayList<>();
   protected DBOptionsInterface<?> options_;
   private static RocksDB.Version version;

   public static void loadLibrary() {
      if (libraryLoaded.get() != RocksDB.LibraryState.LOADED) {
         if (!libraryLoaded.compareAndSet(RocksDB.LibraryState.NOT_LOADED, RocksDB.LibraryState.LOADING)) {
            waitForLibraryToBeLoaded();
         } else {
            String var0 = System.getenv("ROCKSDB_SHAREDLIB_DIR");

            for (CompressionType var4 : CompressionType.values()) {
               try {
                  if (var4.getLibraryName() != null) {
                     System.loadLibrary(var4.getLibraryName());
                  }
               } catch (UnsatisfiedLinkError var7) {
               }
            }

            try {
               NativeLibraryLoader.getInstance().loadLibrary(var0);
            } catch (IOException var6) {
               libraryLoaded.set(RocksDB.LibraryState.NOT_LOADED);
               throw new RuntimeException("Unable to load the RocksDB shared library", var6);
            }

            int var8 = version();
            version = RocksDB.Version.fromEncodedVersion(var8);
            libraryLoaded.set(RocksDB.LibraryState.LOADED);
         }
      }
   }

   public static void loadLibrary(List<String> var0) {
      if (libraryLoaded.get() != RocksDB.LibraryState.LOADED) {
         if (!libraryLoaded.compareAndSet(RocksDB.LibraryState.NOT_LOADED, RocksDB.LibraryState.LOADING)) {
            waitForLibraryToBeLoaded();
         } else {
            for (CompressionType var4 : CompressionType.values()) {
               if (!var4.equals(CompressionType.NO_COMPRESSION)) {
                  for (String var6 : var0) {
                     try {
                        System.load(var6 + "/" + Environment.getSharedLibraryFileName(var4.getLibraryName()));
                        break;
                     } catch (UnsatisfiedLinkError var9) {
                     }
                  }
               }
            }

            boolean var10 = false;
            UnsatisfiedLinkError var11 = null;

            for (String var14 : var0) {
               try {
                  System.load(var14 + "/" + Environment.getJniLibraryFileName("rocksdbjni"));
                  var10 = true;
                  break;
               } catch (UnsatisfiedLinkError var8) {
                  var11 = var8;
               }
            }

            if (!var10) {
               libraryLoaded.set(RocksDB.LibraryState.NOT_LOADED);
               throw var11;
            }

            int var13 = version();
            version = RocksDB.Version.fromEncodedVersion(var13);
            libraryLoaded.set(RocksDB.LibraryState.LOADED);
         }
      }
   }

   private static void waitForLibraryToBeLoaded() {
      long var4 = 0L;

      try {
         while (libraryLoaded.get() == RocksDB.LibraryState.LOADING) {
            Thread.sleep(10L);
            var4 += 10L;
            if (var4 >= 10000L) {
               throw new RuntimeException("Exceeded timeout whilst trying to load the RocksDB shared library");
            }
         }
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
         throw new RuntimeException("Interrupted whilst trying to load the RocksDB shared library", var7);
      }
   }

   public static RocksDB.Version rocksdbVersion() {
      return version;
   }

   public boolean isClosed() {
      return !this.owningHandle_.get();
   }

   protected RocksDB(long var1) {
      super(var1);
   }

   public static RocksDB open(String var0) throws RocksDBException {
      loadLibrary();

      try (Options var1 = new Options()) {
         var1.setCreateIfMissing(true);
         return open(var1, var0);
      }
   }

   public static RocksDB open(String var0, List<ColumnFamilyDescriptor> var1, List<ColumnFamilyHandle> var2) throws RocksDBException {
      try (DBOptions var3 = new DBOptions()) {
         return open(var3, var0, var1, var2);
      }
   }

   public static RocksDB open(Options var0, String var1) throws RocksDBException {
      RocksDB var2 = new RocksDB(open(var0.nativeHandle_, var1));
      var2.storeOptionsInstance(var0);
      var2.storeDefaultColumnFamilyHandle(var2.makeDefaultColumnFamilyHandle());
      return var2;
   }

   public static RocksDB open(DBOptions var0, String var1, List<ColumnFamilyDescriptor> var2, List<ColumnFamilyHandle> var3) throws RocksDBException {
      byte[][] var4 = new byte[var2.size()][];
      long[] var5 = new long[var2.size()];
      int var6 = -1;

      for (int var7 = 0; var7 < var2.size(); var7++) {
         ColumnFamilyDescriptor var8 = (ColumnFamilyDescriptor)var2.get(var7);
         var4[var7] = var8.getName();
         var5[var7] = var8.getOptions().nativeHandle_;
         if (Arrays.equals(var8.getName(), DEFAULT_COLUMN_FAMILY)) {
            var6 = var7;
         }
      }

      if (var6 < 0) {
         throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
      }

      long[] var11 = open(var0.nativeHandle_, var1, var4, var5);
      RocksDB var12 = new RocksDB(var11[0]);
      var12.storeOptionsInstance(var0);

      for (int var9 = 1; var9 < var11.length; var9++) {
         ColumnFamilyHandle var10 = new ColumnFamilyHandle(var12, var11[var9]);
         var3.add(var10);
      }

      var12.ownedColumnFamilyHandles.addAll(var3);
      var12.storeDefaultColumnFamilyHandle((ColumnFamilyHandle)var3.get(var6));
      return var12;
   }

   public static RocksDB openReadOnly(String var0) throws RocksDBException {
      loadLibrary();

      try (Options var1 = new Options()) {
         return openReadOnly(var1, var0);
      }
   }

   public static RocksDB openReadOnly(Options var0, String var1) throws RocksDBException {
      return openReadOnly(var0, var1, false);
   }

   public static RocksDB openReadOnly(Options var0, String var1, boolean var2) throws RocksDBException {
      RocksDB var3 = new RocksDB(openROnly(var0.nativeHandle_, var1, var2));
      var3.storeOptionsInstance(var0);
      var3.storeDefaultColumnFamilyHandle(var3.makeDefaultColumnFamilyHandle());
      return var3;
   }

   public static RocksDB openReadOnly(String var0, List<ColumnFamilyDescriptor> var1, List<ColumnFamilyHandle> var2) throws RocksDBException {
      try (DBOptions var3 = new DBOptions()) {
         return openReadOnly(var3, var0, var1, var2, false);
      }
   }

   public static RocksDB openReadOnly(DBOptions var0, String var1, List<ColumnFamilyDescriptor> var2, List<ColumnFamilyHandle> var3) throws RocksDBException {
      return openReadOnly(var0, var1, var2, var3, false);
   }

   public static RocksDB openReadOnly(DBOptions var0, String var1, List<ColumnFamilyDescriptor> var2, List<ColumnFamilyHandle> var3, boolean var4) throws RocksDBException {
      byte[][] var5 = new byte[var2.size()][];
      long[] var6 = new long[var2.size()];
      int var7 = -1;

      for (int var8 = 0; var8 < var2.size(); var8++) {
         ColumnFamilyDescriptor var9 = (ColumnFamilyDescriptor)var2.get(var8);
         var5[var8] = var9.getName();
         var6[var8] = var9.getOptions().nativeHandle_;
         if (Arrays.equals(var9.getName(), DEFAULT_COLUMN_FAMILY)) {
            var7 = var8;
         }
      }

      if (var7 < 0) {
         throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
      }

      long[] var12 = openROnly(var0.nativeHandle_, var1, var5, var6, var4);
      RocksDB var13 = new RocksDB(var12[0]);
      var13.storeOptionsInstance(var0);

      for (int var10 = 1; var10 < var12.length; var10++) {
         ColumnFamilyHandle var11 = new ColumnFamilyHandle(var13, var12[var10]);
         var3.add(var11);
      }

      var13.ownedColumnFamilyHandles.addAll(var3);
      var13.storeDefaultColumnFamilyHandle((ColumnFamilyHandle)var3.get(var7));
      return var13;
   }

   public static RocksDB openAsSecondary(Options var0, String var1, String var2) throws RocksDBException {
      RocksDB var3 = new RocksDB(openAsSecondary(var0.nativeHandle_, var1, var2));
      var3.storeOptionsInstance(var0);
      var3.storeDefaultColumnFamilyHandle(var3.makeDefaultColumnFamilyHandle());
      return var3;
   }

   public static RocksDB openAsSecondary(DBOptions var0, String var1, String var2, List<ColumnFamilyDescriptor> var3, List<ColumnFamilyHandle> var4) throws RocksDBException {
      byte[][] var5 = new byte[var3.size()][];
      long[] var6 = new long[var3.size()];

      for (int var7 = 0; var7 < var3.size(); var7++) {
         ColumnFamilyDescriptor var8 = (ColumnFamilyDescriptor)var3.get(var7);
         var5[var7] = var8.getName();
         var6[var7] = var8.getOptions().nativeHandle_;
      }

      long[] var11 = openAsSecondary(var0.nativeHandle_, var1, var2, var5, var6);
      RocksDB var12 = new RocksDB(var11[0]);
      var12.storeOptionsInstance(var0);

      for (int var9 = 1; var9 < var11.length; var9++) {
         ColumnFamilyHandle var10 = new ColumnFamilyHandle(var12, var11[var9]);
         var4.add(var10);
      }

      var12.ownedColumnFamilyHandles.addAll(var4);
      var12.storeDefaultColumnFamilyHandle(var12.makeDefaultColumnFamilyHandle());
      return var12;
   }

   public void closeE() throws RocksDBException {
      for (ColumnFamilyHandle var2 : this.ownedColumnFamilyHandles) {
         var2.close();
      }

      this.ownedColumnFamilyHandles.clear();
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

   public static List<byte[]> listColumnFamilies(Options var0, String var1) throws RocksDBException {
      return Arrays.asList(listColumnFamilies(var0.nativeHandle_, var1));
   }

   public ColumnFamilyHandle createColumnFamily(ColumnFamilyDescriptor var1) throws RocksDBException {
      ColumnFamilyHandle var2 = new ColumnFamilyHandle(
         this, createColumnFamily(this.nativeHandle_, var1.getName(), var1.getName().length, var1.getOptions().nativeHandle_)
      );
      this.ownedColumnFamilyHandles.add(var2);
      return var2;
   }

   public List<ColumnFamilyHandle> createColumnFamilies(ColumnFamilyOptions var1, List<byte[]> var2) throws RocksDBException {
      byte[][] var3 = var2.toArray(new byte[0][]);
      long[] var4 = createColumnFamilies(this.nativeHandle_, var1.nativeHandle_, var3);
      ArrayList var5 = new ArrayList(var4.length);

      for (long var9 : var4) {
         ColumnFamilyHandle var11 = new ColumnFamilyHandle(this, var9);
         var5.add(var11);
      }

      this.ownedColumnFamilyHandles.addAll(var5);
      return var5;
   }

   public List<ColumnFamilyHandle> createColumnFamilies(List<ColumnFamilyDescriptor> var1) throws RocksDBException {
      long[] var2 = new long[var1.size()];
      byte[][] var3 = new byte[var1.size()][];

      for (int var4 = 0; var4 < var1.size(); var4++) {
         ColumnFamilyDescriptor var5 = (ColumnFamilyDescriptor)var1.get(var4);
         var2[var4] = var5.getOptions().nativeHandle_;
         var3[var4] = var5.getName();
      }

      long[] var12 = createColumnFamilies(this.nativeHandle_, var2, var3);
      ArrayList var13 = new ArrayList(var12.length);

      for (long var9 : var12) {
         ColumnFamilyHandle var11 = new ColumnFamilyHandle(this, var9);
         var13.add(var11);
      }

      this.ownedColumnFamilyHandles.addAll(var13);
      return var13;
   }

   public ColumnFamilyHandle createColumnFamilyWithImport(ColumnFamilyDescriptor var1, ImportColumnFamilyOptions var2, ExportImportFilesMetaData var3) throws RocksDBException {
      ArrayList var4 = new ArrayList();
      var4.add(var3);
      return this.createColumnFamilyWithImport(var1, var2, var4);
   }

   public ColumnFamilyHandle createColumnFamilyWithImport(ColumnFamilyDescriptor var1, ImportColumnFamilyOptions var2, List<ExportImportFilesMetaData> var3) throws RocksDBException {
      int var4 = var3.size();
      long[] var5 = new long[var4];

      for (int var6 = 0; var6 < var4; var6++) {
         var5[var6] = ((ExportImportFilesMetaData)var3.get(var6)).getNativeHandle();
      }

      ColumnFamilyHandle var7 = new ColumnFamilyHandle(
         this,
         createColumnFamilyWithImport(this.nativeHandle_, var1.getName(), var1.getName().length, var1.getOptions().nativeHandle_, var2.nativeHandle_, var5)
      );
      this.ownedColumnFamilyHandles.add(var7);
      return var7;
   }

   public void dropColumnFamily(ColumnFamilyHandle var1) throws RocksDBException {
      dropColumnFamily(this.nativeHandle_, var1.nativeHandle_);
   }

   public void dropColumnFamilies(List<ColumnFamilyHandle> var1) throws RocksDBException {
      long[] var2 = new long[var1.size()];

      for (int var3 = 0; var3 < var1.size(); var3++) {
         var2[var3] = ((ColumnFamilyHandle)var1.get(var3)).nativeHandle_;
      }

      dropColumnFamilies(this.nativeHandle_, var2);
   }

   public void destroyColumnFamilyHandle(ColumnFamilyHandle var1) {
      for (int var2 = 0; var2 < this.ownedColumnFamilyHandles.size(); var2++) {
         ColumnFamilyHandle var3 = this.ownedColumnFamilyHandles.get(var2);
         if (var3.equals(var1)) {
            var1.close();
            this.ownedColumnFamilyHandles.remove(var2);
            return;
         }
      }
   }

   public void put(byte[] var1, byte[] var2) throws RocksDBException {
      put(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public void put(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) throws RocksDBException {
      BufferUtil.CheckBounds(var2, var3, var1.length);
      BufferUtil.CheckBounds(var5, var6, var4.length);
      put(this.nativeHandle_, var1, var2, var3, var4, var5, var6);
   }

   public void put(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      put(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_);
   }

   public void put(ColumnFamilyHandle var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      put(this.nativeHandle_, var2, var3, var4, var5, var6, var7, var1.nativeHandle_);
   }

   public void put(WriteOptions var1, byte[] var2, byte[] var3) throws RocksDBException {
      put(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length);
   }

   public void put(WriteOptions var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      put(this.nativeHandle_, var1.nativeHandle_, var2, var3, var4, var5, var6, var7);
   }

   public void put(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, byte[] var4) throws RocksDBException {
      put(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var1.nativeHandle_);
   }

   public void put(ColumnFamilyHandle var1, WriteOptions var2, ByteBuffer var3, ByteBuffer var4) throws RocksDBException {
      if (var3.isDirect() && var4.isDirect()) {
         putDirect(this.nativeHandle_, var2.nativeHandle_, var3, var3.position(), var3.remaining(), var4, var4.position(), var4.remaining(), var1.nativeHandle_);
      } else {
         if (var3.isDirect() || var4.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var3.hasArray();
         assert var4.hasArray();
         put(
            this.nativeHandle_,
            var2.nativeHandle_,
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var4.array(),
            var4.arrayOffset() + var4.position(),
            var4.remaining(),
            var1.nativeHandle_
         );
      }

      ((Buffer)var3).position(var3.limit());
      ((Buffer)var4).position(var4.limit());
   }

   public void put(WriteOptions var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      if (var2.isDirect() && var3.isDirect()) {
         putDirect(this.nativeHandle_, var1.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), 0L);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var2.hasArray();
         assert var3.hasArray();
         put(
            this.nativeHandle_,
            var1.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining()
         );
      }

      ((Buffer)var2).position(var2.limit());
      ((Buffer)var3).position(var3.limit());
   }

   public void put(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8) throws RocksDBException {
      BufferUtil.CheckBounds(var4, var5, var3.length);
      BufferUtil.CheckBounds(var7, var8, var6.length);
      put(this.nativeHandle_, var2.nativeHandle_, var3, var4, var5, var6, var7, var8, var1.nativeHandle_);
   }

   public void delete(byte[] var1) throws RocksDBException {
      delete(this.nativeHandle_, var1, 0, var1.length);
   }

   public void delete(byte[] var1, int var2, int var3) throws RocksDBException {
      delete(this.nativeHandle_, var1, var2, var3);
   }

   public void delete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      delete(this.nativeHandle_, var2, 0, var2.length, var1.nativeHandle_);
   }

   public void delete(ColumnFamilyHandle var1, byte[] var2, int var3, int var4) throws RocksDBException {
      delete(this.nativeHandle_, var2, var3, var4, var1.nativeHandle_);
   }

   public void delete(WriteOptions var1, byte[] var2) throws RocksDBException {
      delete(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length);
   }

   public void delete(WriteOptions var1, byte[] var2, int var3, int var4) throws RocksDBException {
      delete(this.nativeHandle_, var1.nativeHandle_, var2, var3, var4);
   }

   public void delete(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3) throws RocksDBException {
      delete(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var1.nativeHandle_);
   }

   public void delete(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, int var4, int var5) throws RocksDBException {
      delete(this.nativeHandle_, var2.nativeHandle_, var3, var4, var5, var1.nativeHandle_);
   }

   public int get(ReadOptions var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      int var4;
      if (var2.isDirect() && var3.isDirect()) {
         var4 = getDirect(this.nativeHandle_, var1.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), 0L);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         var4 = get(
            this.nativeHandle_,
            var1.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            this.defaultColumnFamilyHandle_.nativeHandle_
         );
      }

      if (var4 != -1) {
         ((Buffer)var3).limit(Math.min(var3.limit(), var3.position() + var4));
      }

      ((Buffer)var2).position(var2.limit());
      return var4;
   }

   public int get(ColumnFamilyHandle var1, ReadOptions var2, ByteBuffer var3, ByteBuffer var4) throws RocksDBException {
      assert var3.isDirect() && var4.isDirect();
      int var5 = getDirect(
         this.nativeHandle_, var2.nativeHandle_, var3, var3.position(), var3.remaining(), var4, var4.position(), var4.remaining(), var1.nativeHandle_
      );
      if (var5 != -1) {
         ((Buffer)var4).limit(Math.min(var4.limit(), var4.position() + var5));
      }

      ((Buffer)var3).position(var3.limit());
      return var5;
   }

   public void singleDelete(byte[] var1) throws RocksDBException {
      singleDelete(this.nativeHandle_, var1, var1.length);
   }

   public void singleDelete(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      singleDelete(this.nativeHandle_, var2, var2.length, var1.nativeHandle_);
   }

   public void singleDelete(WriteOptions var1, byte[] var2) throws RocksDBException {
      singleDelete(this.nativeHandle_, var1.nativeHandle_, var2, var2.length);
   }

   public void singleDelete(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3) throws RocksDBException {
      singleDelete(this.nativeHandle_, var2.nativeHandle_, var3, var3.length, var1.nativeHandle_);
   }

   public void deleteRange(byte[] var1, byte[] var2) throws RocksDBException {
      deleteRange(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public void deleteRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      deleteRange(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_);
   }

   public void deleteRange(WriteOptions var1, byte[] var2, byte[] var3) throws RocksDBException {
      deleteRange(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length);
   }

   public void deleteRange(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, byte[] var4) throws RocksDBException {
      deleteRange(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var1.nativeHandle_);
   }

   public void merge(byte[] var1, byte[] var2) throws RocksDBException {
      merge(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public void merge(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) throws RocksDBException {
      BufferUtil.CheckBounds(var2, var3, var1.length);
      BufferUtil.CheckBounds(var5, var6, var4.length);
      merge(this.nativeHandle_, var1, var2, var3, var4, var5, var6);
   }

   public void merge(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      merge(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_);
   }

   public void merge(ColumnFamilyHandle var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      merge(this.nativeHandle_, var2, var3, var4, var5, var6, var7, var1.nativeHandle_);
   }

   public void merge(WriteOptions var1, byte[] var2, byte[] var3) throws RocksDBException {
      merge(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length);
   }

   public void merge(WriteOptions var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      merge(this.nativeHandle_, var1.nativeHandle_, var2, var3, var4, var5, var6, var7);
   }

   public void merge(WriteOptions var1, ByteBuffer var2, ByteBuffer var3) throws RocksDBException {
      if (var2.isDirect() && var3.isDirect()) {
         mergeDirect(this.nativeHandle_, var1.nativeHandle_, var2, var2.position(), var2.remaining(), var3, var3.position(), var3.remaining(), 0L);
      } else {
         if (var2.isDirect() || var3.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var2.hasArray();
         assert var3.hasArray();
         merge(
            this.nativeHandle_,
            var1.nativeHandle_,
            var2.array(),
            var2.arrayOffset() + var2.position(),
            var2.remaining(),
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining()
         );
      }

      ((Buffer)var2).position(var2.limit());
      ((Buffer)var3).position(var3.limit());
   }

   public void merge(ColumnFamilyHandle var1, WriteOptions var2, ByteBuffer var3, ByteBuffer var4) throws RocksDBException {
      if (var3.isDirect() && var4.isDirect()) {
         mergeDirect(
            this.nativeHandle_, var2.nativeHandle_, var3, var3.position(), var3.remaining(), var4, var4.position(), var4.remaining(), var1.nativeHandle_
         );
      } else {
         if (var3.isDirect() || var4.isDirect()) {
            throw new RocksDBException("ByteBuffer parameters must all be direct, or must all be indirect");
         }

         assert var3.hasArray();
         assert var4.hasArray();
         merge(
            this.nativeHandle_,
            var2.nativeHandle_,
            var3.array(),
            var3.arrayOffset() + var3.position(),
            var3.remaining(),
            var4.array(),
            var4.arrayOffset() + var4.position(),
            var4.remaining(),
            var1.nativeHandle_
         );
      }

      ((Buffer)var3).position(var3.limit());
      ((Buffer)var4).position(var4.limit());
   }

   public void delete(WriteOptions var1, ByteBuffer var2) throws RocksDBException {
      assert var2.isDirect();
      deleteDirect(this.nativeHandle_, var1.nativeHandle_, var2, var2.position(), var2.remaining(), 0L);
      ((Buffer)var2).position(var2.limit());
   }

   public void delete(ColumnFamilyHandle var1, WriteOptions var2, ByteBuffer var3) throws RocksDBException {
      assert var3.isDirect();
      deleteDirect(this.nativeHandle_, var2.nativeHandle_, var3, var3.position(), var3.remaining(), var1.nativeHandle_);
      ((Buffer)var3).position(var3.limit());
   }

   public void merge(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, byte[] var4) throws RocksDBException {
      merge(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var1.nativeHandle_);
   }

   public void merge(ColumnFamilyHandle var1, WriteOptions var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8) throws RocksDBException {
      BufferUtil.CheckBounds(var4, var5, var3.length);
      BufferUtil.CheckBounds(var7, var8, var6.length);
      merge(this.nativeHandle_, var2.nativeHandle_, var3, var4, var5, var6, var7, var8, var1.nativeHandle_);
   }

   public void write(WriteOptions var1, WriteBatch var2) throws RocksDBException {
      write0(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_);
   }

   public void write(WriteOptions var1, WriteBatchWithIndex var2) throws RocksDBException {
      write1(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_);
   }

   public int get(byte[] var1, byte[] var2) throws RocksDBException {
      return get(this.nativeHandle_, var1, 0, var1.length, var2, 0, var2.length);
   }

   public int get(byte[] var1, int var2, int var3, byte[] var4, int var5, int var6) throws RocksDBException {
      BufferUtil.CheckBounds(var2, var3, var1.length);
      BufferUtil.CheckBounds(var5, var6, var4.length);
      return get(this.nativeHandle_, var1, var2, var3, var4, var5, var6);
   }

   public int get(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException, IllegalArgumentException {
      return get(this.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length, var1.nativeHandle_);
   }

   public int get(ColumnFamilyHandle var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException, IllegalArgumentException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      return get(this.nativeHandle_, var2, var3, var4, var5, var6, var7, var1.nativeHandle_);
   }

   public int get(ReadOptions var1, byte[] var2, byte[] var3) throws RocksDBException {
      return get(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length);
   }

   public int get(ReadOptions var1, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      BufferUtil.CheckBounds(var6, var7, var5.length);
      return get(this.nativeHandle_, var1.nativeHandle_, var2, var3, var4, var5, var6, var7);
   }

   public int get(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, byte[] var4) throws RocksDBException {
      return get(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var4, 0, var4.length, var1.nativeHandle_);
   }

   public int get(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, int var4, int var5, byte[] var6, int var7, int var8) throws RocksDBException {
      BufferUtil.CheckBounds(var4, var5, var3.length);
      BufferUtil.CheckBounds(var7, var8, var6.length);
      return get(this.nativeHandle_, var2.nativeHandle_, var3, var4, var5, var6, var7, var8, var1.nativeHandle_);
   }

   public byte[] get(byte[] var1) throws RocksDBException {
      return get(this.nativeHandle_, var1, 0, var1.length);
   }

   public byte[] get(byte[] var1, int var2, int var3) throws RocksDBException {
      BufferUtil.CheckBounds(var2, var3, var1.length);
      return get(this.nativeHandle_, var1, var2, var3);
   }

   public byte[] get(ColumnFamilyHandle var1, byte[] var2) throws RocksDBException {
      return get(this.nativeHandle_, var2, 0, var2.length, var1.nativeHandle_);
   }

   public byte[] get(ColumnFamilyHandle var1, byte[] var2, int var3, int var4) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      return get(this.nativeHandle_, var2, var3, var4, var1.nativeHandle_);
   }

   public byte[] get(ReadOptions var1, byte[] var2) throws RocksDBException {
      return get(this.nativeHandle_, var1.nativeHandle_, var2, 0, var2.length);
   }

   public byte[] get(ReadOptions var1, byte[] var2, int var3, int var4) throws RocksDBException {
      BufferUtil.CheckBounds(var3, var4, var2.length);
      return get(this.nativeHandle_, var1.nativeHandle_, var2, var3, var4);
   }

   public byte[] get(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3) throws RocksDBException {
      return get(this.nativeHandle_, var2.nativeHandle_, var3, 0, var3.length, var1.nativeHandle_);
   }

   public byte[] get(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, int var4, int var5) throws RocksDBException {
      BufferUtil.CheckBounds(var4, var5, var3.length);
      return get(this.nativeHandle_, var2.nativeHandle_, var3, var4, var5, var1.nativeHandle_);
   }

   public List<byte[]> multiGetAsList(List<byte[]> var1) throws RocksDBException {
      assert !var1.isEmpty();
      byte[][] var2 = var1.toArray(new byte[var1.size()][]);
      int[] var3 = new int[var2.length];
      int[] var4 = new int[var2.length];

      for (int var5 = 0; var5 < var4.length; var5++) {
         var4[var5] = var2[var5].length;
      }

      return Arrays.asList(multiGet(this.nativeHandle_, var2, var3, var4));
   }

   public List<byte[]> multiGetAsList(List<ColumnFamilyHandle> var1, List<byte[]> var2) throws RocksDBException, IllegalArgumentException {
      assert !var2.isEmpty();
      if (var2.size() != var1.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      long[] var3 = new long[var1.size()];

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3[var4] = ((ColumnFamilyHandle)var1.get(var4)).nativeHandle_;
      }

      byte[][] var8 = var2.toArray(new byte[var2.size()][]);
      int[] var5 = new int[var8.length];
      int[] var6 = new int[var8.length];

      for (int var7 = 0; var7 < var6.length; var7++) {
         var6[var7] = var8[var7].length;
      }

      return Arrays.asList(multiGet(this.nativeHandle_, var8, var5, var6, var3));
   }

   public List<byte[]> multiGetAsList(ReadOptions var1, List<byte[]> var2) throws RocksDBException {
      assert !var2.isEmpty();
      byte[][] var3 = var2.toArray(new byte[var2.size()][]);
      int[] var4 = new int[var3.length];
      int[] var5 = new int[var3.length];

      for (int var6 = 0; var6 < var5.length; var6++) {
         var5[var6] = var3[var6].length;
      }

      return Arrays.asList(multiGet(this.nativeHandle_, var1.nativeHandle_, var3, var4, var5));
   }

   public List<byte[]> multiGetAsList(ReadOptions var1, List<ColumnFamilyHandle> var2, List<byte[]> var3) throws RocksDBException {
      assert !var3.isEmpty();
      if (var3.size() != var2.size()) {
         throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
      }

      long[] var4 = new long[var2.size()];

      for (int var5 = 0; var5 < var2.size(); var5++) {
         var4[var5] = ((ColumnFamilyHandle)var2.get(var5)).nativeHandle_;
      }

      byte[][] var9 = var3.toArray(new byte[var3.size()][]);
      int[] var6 = new int[var9.length];
      int[] var7 = new int[var9.length];

      for (int var8 = 0; var8 < var7.length; var8++) {
         var7[var8] = var9[var8].length;
      }

      return Arrays.asList(multiGet(this.nativeHandle_, var1.nativeHandle_, var9, var6, var7, var4));
   }

   public List<ByteBufferGetStatus> multiGetByteBuffers(List<ByteBuffer> var1, List<ByteBuffer> var2) throws RocksDBException {
      try (ReadOptions var3 = new ReadOptions()) {
         ArrayList var5 = new ArrayList(1);
         var5.add(this.getDefaultColumnFamily());
         return this.multiGetByteBuffers(var3, var5, var1, var2);
      }
   }

   public List<ByteBufferGetStatus> multiGetByteBuffers(ReadOptions var1, List<ByteBuffer> var2, List<ByteBuffer> var3) throws RocksDBException {
      ArrayList var4 = new ArrayList(1);
      var4.add(this.getDefaultColumnFamily());
      return this.multiGetByteBuffers(var1, var4, var2, var3);
   }

   public List<ByteBufferGetStatus> multiGetByteBuffers(List<ColumnFamilyHandle> var1, List<ByteBuffer> var2, List<ByteBuffer> var3) throws RocksDBException {
      try (ReadOptions var4 = new ReadOptions()) {
         return this.multiGetByteBuffers(var4, var1, var2, var3);
      }
   }

   public List<ByteBufferGetStatus> multiGetByteBuffers(ReadOptions var1, List<ColumnFamilyHandle> var2, List<ByteBuffer> var3, List<ByteBuffer> var4) throws RocksDBException {
      assert !var3.isEmpty();
      if (var3.size() != var2.size() && var2.size() > 1) {
         throw new IllegalArgumentException("Wrong number of ColumnFamilyHandle(s) supplied. Provide 0, 1, or as many as there are key/value(s)");
      }

      if (var4.size() != var3.size()) {
         throw new IllegalArgumentException(
            "For each key there must be a corresponding value. " + var3.size() + " keys were supplied, but " + var4.size() + " values were supplied."
         );
      }

      for (ByteBuffer var6 : var3) {
         if (!var6.isDirect()) {
            throw new IllegalArgumentException("All key buffers must be direct byte buffers");
         }
      }

      for (ByteBuffer var20 : var4) {
         if (!var20.isDirect()) {
            throw new IllegalArgumentException("All value buffers must be direct byte buffers");
         }
      }

      int var19 = var2.size();
      long[] var21 = new long[var19];

      for (int var7 = 0; var7 < var19; var7++) {
         var21[var7] = ((ColumnFamilyHandle)var2.get(var7)).nativeHandle_;
      }

      int var22 = var3.size();
      ByteBuffer[] var8 = var3.toArray(new ByteBuffer[0]);
      int[] var9 = new int[var22];
      int[] var10 = new int[var22];

      for (int var11 = 0; var11 < var22; var11++) {
         var9[var11] = var8[var11].position();
         var10[var11] = var8[var11].limit();
      }

      ByteBuffer[] var23 = var4.toArray(new ByteBuffer[0]);
      int[] var12 = new int[var22];
      Status[] var13 = new Status[var22];
      multiGet(this.nativeHandle_, var1.nativeHandle_, var21, var8, var9, var10, var23, var12, var13);
      ArrayList var14 = new ArrayList();

      for (int var15 = 0; var15 < var22; var15++) {
         Status var16 = var13[var15];
         if (var16.getCode() == Status.Code.Ok) {
            ByteBuffer var17 = var23[var15];
            ((Buffer)var17).position(Math.min(var12[var15], var17.capacity()));
            ((Buffer)var17).flip();
            var14.add(new ByteBufferGetStatus(var16, var12[var15], var17));
         } else if (var16.getCode() == Status.Code.Incomplete) {
            assert var12[var15] == -1;
            ByteBuffer var24 = var23[var15];
            ((Buffer)var24).position(var24.capacity());
            ((Buffer)var24).flip();
            var14.add(new ByteBufferGetStatus(var16, var24.capacity(), var24));
         } else {
            var14.add(new ByteBufferGetStatus(var16));
         }
      }

      return var14;
   }

   public boolean keyExists(byte[] var1) {
      return this.keyExists(var1, 0, var1.length);
   }

   public boolean keyExists(byte[] var1, int var2, int var3) {
      return this.keyExists(null, null, var1, var2, var3);
   }

   public boolean keyExists(ColumnFamilyHandle var1, byte[] var2) {
      return this.keyExists(var1, var2, 0, var2.length);
   }

   public boolean keyExists(ColumnFamilyHandle var1, byte[] var2, int var3, int var4) {
      return this.keyExists(var1, null, var2, var3, var4);
   }

   public boolean keyExists(ReadOptions var1, byte[] var2) {
      return this.keyExists(var1, var2, 0, var2.length);
   }

   public boolean keyExists(ReadOptions var1, byte[] var2, int var3, int var4) {
      return this.keyExists(null, var1, var2, var3, var4);
   }

   public boolean keyExists(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3) {
      return this.keyExists(var1, var2, var3, 0, var3.length);
   }

   public boolean keyExists(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, int var4, int var5) {
      checkBounds(var4, var5, var3.length);
      return keyExists(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2 == null ? 0L : var2.nativeHandle_, var3, var4, var5);
   }

   public boolean keyExists(ByteBuffer var1) {
      return this.keyExists(null, null, var1);
   }

   public boolean keyExists(ColumnFamilyHandle var1, ByteBuffer var2) {
      return this.keyExists(var1, null, var2);
   }

   public boolean keyExists(ReadOptions var1, ByteBuffer var2) {
      return this.keyExists(null, var1, var2);
   }

   public boolean keyExists(ColumnFamilyHandle var1, ReadOptions var2, ByteBuffer var3) {
      assert var3 != null : "key ByteBuffer parameter cannot be null";
      assert var3.isDirect() : "key parameter must be a direct ByteBuffer";
      return keyExistsDirect(
         this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2 == null ? 0L : var2.nativeHandle_, var3, var3.position(), var3.limit()
      );
   }

   public boolean keyMayExist(byte[] var1, Holder<byte[]> var2) {
      return this.keyMayExist(var1, 0, var1.length, var2);
   }

   public boolean keyMayExist(byte[] var1, int var2, int var3, Holder<byte[]> var4) {
      return this.keyMayExist((ColumnFamilyHandle)null, var1, var2, var3, var4);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, byte[] var2, Holder<byte[]> var3) {
      return this.keyMayExist(var1, var2, 0, var2.length, var3);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, byte[] var2, int var3, int var4, Holder<byte[]> var5) {
      return this.keyMayExist(var1, null, var2, var3, var4, var5);
   }

   public boolean keyMayExist(ReadOptions var1, byte[] var2, Holder<byte[]> var3) {
      return this.keyMayExist(var1, var2, 0, var2.length, var3);
   }

   public boolean keyMayExist(ReadOptions var1, byte[] var2, int var3, int var4, Holder<byte[]> var5) {
      return this.keyMayExist(null, var1, var2, var3, var4, var5);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, Holder<byte[]> var4) {
      return this.keyMayExist(var1, var2, var3, 0, var3.length, var4);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, ReadOptions var2, byte[] var3, int var4, int var5, Holder<byte[]> var6) {
      BufferUtil.CheckBounds(var4, var5, var3.length);
      if (var6 == null) {
         return keyMayExist(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2 == null ? 0L : var2.nativeHandle_, var3, var4, var5);
      } else {
         byte[][] var7 = keyMayExistFoundValue(
            this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2 == null ? 0L : var2.nativeHandle_, var3, var4, var5
         );
         if (var7[0][0] == 0) {
            var6.setValue(null);
            return false;
         } else if (var7[0][0] == 1) {
            var6.setValue(null);
            return true;
         } else {
            var6.setValue(var7[1]);
            return true;
         }
      }
   }

   public boolean keyMayExist(ByteBuffer var1) {
      return this.keyMayExist(null, (ReadOptions)null, var1);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, ByteBuffer var2) {
      return this.keyMayExist(var1, (ReadOptions)null, var2);
   }

   public boolean keyMayExist(ReadOptions var1, ByteBuffer var2) {
      return this.keyMayExist(null, var1, var2);
   }

   public KeyMayExist keyMayExist(ByteBuffer var1, ByteBuffer var2) {
      return this.keyMayExist(null, null, var1, var2);
   }

   public KeyMayExist keyMayExist(ColumnFamilyHandle var1, ByteBuffer var2, ByteBuffer var3) {
      return this.keyMayExist(var1, null, var2, var3);
   }

   public KeyMayExist keyMayExist(ReadOptions var1, ByteBuffer var2, ByteBuffer var3) {
      return this.keyMayExist(null, var1, var2, var3);
   }

   public boolean keyMayExist(ColumnFamilyHandle var1, ReadOptions var2, ByteBuffer var3) {
      assert var3 != null : "key ByteBuffer parameter cannot be null";
      assert var3.isDirect() : "key parameter must be a direct ByteBuffer";
      boolean var4 = keyMayExistDirect(
         this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2 == null ? 0L : var2.nativeHandle_, var3, var3.position(), var3.limit()
      );
      ((Buffer)var3).position(var3.limit());
      return var4;
   }

   public KeyMayExist keyMayExist(ColumnFamilyHandle var1, ReadOptions var2, ByteBuffer var3, ByteBuffer var4) {
      assert var3 != null : "key ByteBuffer parameter cannot be null";
      assert var3.isDirect() : "key parameter must be a direct ByteBuffer";
      assert var4 != null : "value ByteBuffer parameter cannot be null. If you do not need the value, use a different version of the method";
      assert var4.isDirect() : "value parameter must be a direct ByteBuffer";
      int[] var5 = keyMayExistDirectFoundValue(
         this.nativeHandle_,
         var1 == null ? 0L : var1.nativeHandle_,
         var2 == null ? 0L : var2.nativeHandle_,
         var3,
         var3.position(),
         var3.remaining(),
         var4,
         var4.position(),
         var4.remaining()
      );
      int var6 = var5[1];
      ((Buffer)var4).limit(var4.position() + Math.min(var6, var4.remaining()));
      ((Buffer)var3).position(var3.limit());
      return new KeyMayExist(KeyMayExist.KeyMayExistEnum.values()[var5[0]], var6);
   }

   public RocksIterator newIterator() {
      return new RocksIterator(this, iterator(this.nativeHandle_, this.defaultColumnFamilyHandle_.nativeHandle_, this.defaultReadOptions_.nativeHandle_));
   }

   public RocksIterator newIterator(ReadOptions var1) {
      return new RocksIterator(this, iterator(this.nativeHandle_, this.defaultColumnFamilyHandle_.nativeHandle_, var1.nativeHandle_));
   }

   public RocksIterator newIterator(ColumnFamilyHandle var1) {
      return new RocksIterator(this, iterator(this.nativeHandle_, var1.nativeHandle_, this.defaultReadOptions_.nativeHandle_));
   }

   public RocksIterator newIterator(ColumnFamilyHandle var1, ReadOptions var2) {
      return new RocksIterator(this, iterator(this.nativeHandle_, var1.nativeHandle_, var2.nativeHandle_));
   }

   public List<RocksIterator> newIterators(List<ColumnFamilyHandle> var1) throws RocksDBException {
      return this.newIterators(var1, new ReadOptions());
   }

   public List<RocksIterator> newIterators(List<ColumnFamilyHandle> var1, ReadOptions var2) throws RocksDBException {
      long[] var3 = new long[var1.size()];

      for (int var4 = 0; var4 < var1.size(); var4++) {
         var3[var4] = ((ColumnFamilyHandle)var1.get(var4)).nativeHandle_;
      }

      long[] var7 = iterators(this.nativeHandle_, var3, var2.nativeHandle_);
      ArrayList var5 = new ArrayList(var1.size());

      for (int var6 = 0; var6 < var1.size(); var6++) {
         var5.add(new RocksIterator(this, var7[var6]));
      }

      return var5;
   }

   public Snapshot getSnapshot() {
      long var1 = getSnapshot(this.nativeHandle_);
      return var1 != 0L ? new Snapshot(var1) : null;
   }

   public void releaseSnapshot(Snapshot var1) {
      if (var1 != null) {
         releaseSnapshot(this.nativeHandle_, var1.nativeHandle_);
      }
   }

   public String getProperty(ColumnFamilyHandle var1, String var2) throws RocksDBException {
      return getProperty(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2, var2.length());
   }

   public String getProperty(String var1) throws RocksDBException {
      return this.getProperty(null, var1);
   }

   public Map<String, String> getMapProperty(String var1) throws RocksDBException {
      return this.getMapProperty(null, var1);
   }

   public Map<String, String> getMapProperty(ColumnFamilyHandle var1, String var2) throws RocksDBException {
      return getMapProperty(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2, var2.length());
   }

   public long getLongProperty(String var1) throws RocksDBException {
      return this.getLongProperty(null, var1);
   }

   public long getLongProperty(ColumnFamilyHandle var1, String var2) throws RocksDBException {
      return getLongProperty(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2, var2.length());
   }

   public void resetStats() throws RocksDBException {
      resetStats(this.nativeHandle_);
   }

   public long getAggregatedLongProperty(String var1) throws RocksDBException {
      return getAggregatedLongProperty(this.nativeHandle_, var1, var1.length());
   }

   public long[] getApproximateSizes(ColumnFamilyHandle var1, List<Range> var2, SizeApproximationFlag... var3) {
      byte var4 = 0;

      for (SizeApproximationFlag var8 : var3) {
         var4 |= var8.getValue();
      }

      return getApproximateSizes(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, toRangeSliceHandles(var2), var4);
   }

   public long[] getApproximateSizes(List<Range> var1, SizeApproximationFlag... var2) {
      return this.getApproximateSizes(null, var1, var2);
   }

   public RocksDB.CountAndSize getApproximateMemTableStats(ColumnFamilyHandle var1, Range var2) {
      long[] var3 = getApproximateMemTableStats(
         this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2.start.getNativeHandle(), var2.limit.getNativeHandle()
      );
      return new RocksDB.CountAndSize(var3[0], var3[1]);
   }

   public RocksDB.CountAndSize getApproximateMemTableStats(Range var1) {
      return this.getApproximateMemTableStats(null, var1);
   }

   public void compactRange() throws RocksDBException {
      this.compactRange(null);
   }

   public void compactRange(ColumnFamilyHandle var1) throws RocksDBException {
      compactRange(this.nativeHandle_, null, -1, null, -1, 0L, var1 == null ? 0L : var1.nativeHandle_);
   }

   public void compactRange(byte[] var1, byte[] var2) throws RocksDBException {
      this.compactRange(null, var1, var2);
   }

   public void compactRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      compactRange(this.nativeHandle_, var2, var2 == null ? -1 : var2.length, var3, var3 == null ? -1 : var3.length, 0L, var1 == null ? 0L : var1.nativeHandle_);
   }

   public void compactRange(ColumnFamilyHandle var1, byte[] var2, byte[] var3, CompactRangeOptions var4) throws RocksDBException {
      compactRange(
         this.nativeHandle_,
         var2,
         var2 == null ? -1 : var2.length,
         var3,
         var3 == null ? -1 : var3.length,
         var4.nativeHandle_,
         var1 == null ? 0L : var1.nativeHandle_
      );
   }

   public void clipColumnFamily(ColumnFamilyHandle var1, byte[] var2, byte[] var3) throws RocksDBException {
      clipColumnFamily(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2, 0, var2.length, var3, 0, var3.length);
   }

   public void setOptions(ColumnFamilyHandle var1, MutableColumnFamilyOptions var2) throws RocksDBException {
      setOptions(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2.getKeys(), var2.getValues());
   }

   public void setPerfLevel(PerfLevel var1) {
      if (var1 == PerfLevel.UNINITIALIZED) {
         throw new IllegalArgumentException("Unable to set UNINITIALIZED level");
      }

      if (var1 == PerfLevel.OUT_OF_BOUNDS) {
         throw new IllegalArgumentException("Unable to set OUT_OF_BOUNDS level");
      }

      setPerfLevel(var1.getValue());
   }

   public PerfLevel getPerfLevel() {
      byte var1 = getPerfLevelNative();
      return PerfLevel.getPerfLevel(var1);
   }

   public PerfContext getPerfContext() {
      long var1 = getPerfContextNative();
      return new PerfContext(var1);
   }

   public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder getOptions(ColumnFamilyHandle var1) throws RocksDBException {
      String var2 = getOptions(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
      return MutableColumnFamilyOptions.parse(var2, true);
   }

   public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder getOptions() throws RocksDBException {
      return this.getOptions(null);
   }

   public MutableDBOptions.MutableDBOptionsBuilder getDBOptions() throws RocksDBException {
      String var1 = getDBOptions(this.nativeHandle_);
      return MutableDBOptions.parse(var1, true);
   }

   public void setOptions(MutableColumnFamilyOptions var1) throws RocksDBException {
      this.setOptions(null, var1);
   }

   public void setDBOptions(MutableDBOptions var1) throws RocksDBException {
      setDBOptions(this.nativeHandle_, var1.getKeys(), var1.getValues());
   }

   public List<String> compactFiles(CompactionOptions var1, List<String> var2, int var3, int var4, CompactionJobInfo var5) throws RocksDBException {
      return this.compactFiles(var1, null, var2, var3, var4, var5);
   }

   public List<String> compactFiles(CompactionOptions var1, ColumnFamilyHandle var2, List<String> var3, int var4, int var5, CompactionJobInfo var6) throws RocksDBException {
      return Arrays.asList(
         compactFiles(
            this.nativeHandle_,
            var1.nativeHandle_,
            var2 == null ? 0L : var2.nativeHandle_,
            var3.toArray(new String[0]),
            var4,
            var5,
            var6 == null ? 0L : var6.nativeHandle_
         )
      );
   }

   public void cancelAllBackgroundWork(boolean var1) {
      cancelAllBackgroundWork(this.nativeHandle_, var1);
   }

   public void pauseBackgroundWork() throws RocksDBException {
      pauseBackgroundWork(this.nativeHandle_);
   }

   public void continueBackgroundWork() throws RocksDBException {
      continueBackgroundWork(this.nativeHandle_);
   }

   public void enableAutoCompaction(List<ColumnFamilyHandle> var1) throws RocksDBException {
      enableAutoCompaction(this.nativeHandle_, this.toNativeHandleList(var1));
   }

   public int numberLevels() {
      return this.numberLevels(null);
   }

   public int numberLevels(ColumnFamilyHandle var1) {
      return numberLevels(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
   }

   @Deprecated
   public int maxMemCompactionLevel() {
      return this.maxMemCompactionLevel(null);
   }

   public int maxMemCompactionLevel(ColumnFamilyHandle var1) {
      return maxMemCompactionLevel(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
   }

   public int level0StopWriteTrigger() {
      return this.level0StopWriteTrigger(null);
   }

   public int level0StopWriteTrigger(ColumnFamilyHandle var1) {
      return level0StopWriteTrigger(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
   }

   public String getName() {
      return getName(this.nativeHandle_);
   }

   public Env getEnv() {
      long var1 = getEnv(this.nativeHandle_);
      if (var1 == Env.getDefault().nativeHandle_) {
         return Env.getDefault();
      }

      RocksEnv var3 = new RocksEnv(var1);
      var3.disOwnNativeHandle();
      return var3;
   }

   public void flush(FlushOptions var1) throws RocksDBException {
      this.flush(var1, Collections.singletonList(this.getDefaultColumnFamily()));
   }

   public void flush(FlushOptions var1, ColumnFamilyHandle var2) throws RocksDBException {
      this.flush(var1, var2 == null ? null : Collections.singletonList(var2));
   }

   public void flush(FlushOptions var1, List<ColumnFamilyHandle> var2) throws RocksDBException {
      flush(this.nativeHandle_, var1.nativeHandle_, this.toNativeHandleList(var2));
   }

   public void flushWal(boolean var1) throws RocksDBException {
      flushWal(this.nativeHandle_, var1);
   }

   public void syncWal() throws RocksDBException {
      syncWal(this.nativeHandle_);
   }

   public long getLatestSequenceNumber() {
      return getLatestSequenceNumber(this.nativeHandle_);
   }

   public void disableFileDeletions() throws RocksDBException {
      disableFileDeletions(this.nativeHandle_);
   }

   public void enableFileDeletions() throws RocksDBException {
      enableFileDeletions(this.nativeHandle_);
   }

   public RocksDB.LiveFiles getLiveFiles() throws RocksDBException {
      return this.getLiveFiles(true);
   }

   public RocksDB.LiveFiles getLiveFiles(boolean var1) throws RocksDBException {
      String[] var2 = getLiveFiles(this.nativeHandle_, var1);
      if (var2 == null) {
         return null;
      }

      String[] var3 = Arrays.copyOf(var2, var2.length - 1);
      long var4 = Long.parseLong(var2[var2.length - 1]);
      return new RocksDB.LiveFiles(var4, Arrays.asList(var3));
   }

   public List<LogFile> getSortedWalFiles() throws RocksDBException {
      LogFile[] var1 = getSortedWalFiles(this.nativeHandle_);
      return Arrays.asList(var1);
   }

   public TransactionLogIterator getUpdatesSince(long var1) throws RocksDBException {
      return new TransactionLogIterator(getUpdatesSince(this.nativeHandle_, var1));
   }

   public List<LiveFileMetaData> getLiveFilesMetaData() {
      return Arrays.asList(getLiveFilesMetaData(this.nativeHandle_));
   }

   public ColumnFamilyMetaData getColumnFamilyMetaData(ColumnFamilyHandle var1) {
      return getColumnFamilyMetaData(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
   }

   public ColumnFamilyMetaData getColumnFamilyMetaData() {
      return this.getColumnFamilyMetaData(null);
   }

   public void ingestExternalFile(List<String> var1, IngestExternalFileOptions var2) throws RocksDBException {
      ingestExternalFile(this.nativeHandle_, this.getDefaultColumnFamily().nativeHandle_, var1.toArray(new String[0]), var1.size(), var2.nativeHandle_);
   }

   public void ingestExternalFile(ColumnFamilyHandle var1, List<String> var2, IngestExternalFileOptions var3) throws RocksDBException {
      ingestExternalFile(this.nativeHandle_, var1.nativeHandle_, var2.toArray(new String[0]), var2.size(), var3.nativeHandle_);
   }

   public void verifyChecksum() throws RocksDBException {
      verifyChecksum(this.nativeHandle_);
   }

   public ColumnFamilyHandle getDefaultColumnFamily() {
      return this.defaultColumnFamilyHandle_;
   }

   protected ColumnFamilyHandle makeDefaultColumnFamilyHandle() {
      ColumnFamilyHandle var1 = new ColumnFamilyHandle(this, getDefaultColumnFamily(this.nativeHandle_));
      var1.disOwnNativeHandle();
      return var1;
   }

   public Map<String, TableProperties> getPropertiesOfAllTables(ColumnFamilyHandle var1) throws RocksDBException {
      return getPropertiesOfAllTables(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
   }

   public Map<String, TableProperties> getPropertiesOfAllTables() throws RocksDBException {
      return this.getPropertiesOfAllTables(null);
   }

   public Map<String, TableProperties> getPropertiesOfTablesInRange(ColumnFamilyHandle var1, List<Range> var2) throws RocksDBException {
      return getPropertiesOfTablesInRange(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, toRangeSliceHandles(var2));
   }

   public Map<String, TableProperties> getPropertiesOfTablesInRange(List<Range> var1) throws RocksDBException {
      return this.getPropertiesOfTablesInRange(null, var1);
   }

   public Range suggestCompactRange(ColumnFamilyHandle var1) throws RocksDBException {
      long[] var2 = suggestCompactRange(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_);
      return new Range(new Slice(var2[0]), new Slice(var2[1]));
   }

   public Range suggestCompactRange() throws RocksDBException {
      return this.suggestCompactRange(null);
   }

   @Deprecated
   public void promoteL0(ColumnFamilyHandle var1, int var2) throws RocksDBException {
      promoteL0(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var2);
   }

   @Deprecated
   public void promoteL0(int var1) throws RocksDBException {
      this.promoteL0(null, var1);
   }

   public void startTrace(TraceOptions var1, AbstractTraceWriter var2) throws RocksDBException {
      startTrace(this.nativeHandle_, var1.getMaxTraceFileSize(), var2.nativeHandle_);
      var2.disOwnNativeHandle();
   }

   public void endTrace() throws RocksDBException {
      endTrace(this.nativeHandle_);
   }

   public void tryCatchUpWithPrimary() throws RocksDBException {
      tryCatchUpWithPrimary(this.nativeHandle_);
   }

   public void deleteFilesInRanges(ColumnFamilyHandle var1, List<byte[]> var2, boolean var3) throws RocksDBException {
      if (!var2.isEmpty()) {
         if (var2.size() % 2 != 0) {
            throw new IllegalArgumentException("Ranges size needs to be multiple of 2 (from1, to1, from2, to2, ...), but is " + var2.size());
         }

         byte[][] var4 = var2.toArray(new byte[var2.size()][]);
         deleteFilesInRanges(this.nativeHandle_, var1 == null ? 0L : var1.nativeHandle_, var4, var3);
      }
   }

   public static void destroyDB(String var0, Options var1) throws RocksDBException {
      destroyDB(var0, var1.nativeHandle_);
   }

   private long[] toNativeHandleList(List<? extends RocksObject> var1) {
      if (var1 == null) {
         return new long[0];
      }

      int var2 = var1.size();
      long[] var3 = new long[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = ((RocksObject)var1.get(var4)).nativeHandle_;
      }

      return var3;
   }

   private static long[] toRangeSliceHandles(List<Range> var0) {
      long[] var1 = new long[var0.size() * 2];
      int var2 = 0;
      int var3 = 0;

      while (var2 < var0.size()) {
         Range var4 = (Range)var0.get(var2);
         var1[var3++] = var4.start.getNativeHandle();
         var1[var3++] = var4.limit.getNativeHandle();
         var2++;
      }

      return var1;
   }

   protected void storeOptionsInstance(DBOptionsInterface<?> var1) {
      this.options_ = var1;
   }

   protected void storeDefaultColumnFamilyHandle(ColumnFamilyHandle var1) {
      this.defaultColumnFamilyHandle_ = var1;
   }

   private static void checkBounds(int var0, int var1, int var2) {
      if ((var0 | var1 | var0 + var1 | var2 - (var0 + var1)) < 0) {
         throw new IndexOutOfBoundsException(String.format("offset(%d), len(%d), size(%d)", var0, var1, var2));
      }
   }

   private static native long open(long var0, String var2) throws RocksDBException;

   private static native long[] open(long var0, String var2, byte[][] var3, long[] var4) throws RocksDBException;

   private static native long openROnly(long var0, String var2, boolean var3) throws RocksDBException;

   private static native long[] openROnly(long var0, String var2, byte[][] var3, long[] var4, boolean var5) throws RocksDBException;

   private static native long openAsSecondary(long var0, String var2, String var3) throws RocksDBException;

   private static native long[] openAsSecondary(long var0, String var2, String var3, byte[][] var4, long[] var5) throws RocksDBException;

   @Override
   protected void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void closeDatabase(long var0) throws RocksDBException;

   private static native byte[][] listColumnFamilies(long var0, String var2) throws RocksDBException;

   private static native long createColumnFamily(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   private static native long[] createColumnFamilies(long var0, long var2, byte[][] var4) throws RocksDBException;

   private static native long[] createColumnFamilies(long var0, long[] var2, byte[][] var3) throws RocksDBException;

   private static native long createColumnFamilyWithImport(long var0, byte[] var2, int var3, long var4, long var6, long[] var8) throws RocksDBException;

   private static native void dropColumnFamily(long var0, long var2) throws RocksDBException;

   private static native void dropColumnFamilies(long var0, long[] var2) throws RocksDBException;

   private static native void put(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native void put(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8) throws RocksDBException;

   private static native void put(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws RocksDBException;

   private static native void put(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10) throws RocksDBException;

   private static native void delete(long var0, byte[] var2, int var3, int var4) throws RocksDBException;

   private static native void delete(long var0, byte[] var2, int var3, int var4, long var5) throws RocksDBException;

   private static native void delete(long var0, long var2, byte[] var4, int var5, int var6) throws RocksDBException;

   private static native void delete(long var0, long var2, byte[] var4, int var5, int var6, long var7) throws RocksDBException;

   private static native void singleDelete(long var0, byte[] var2, int var3) throws RocksDBException;

   private static native void singleDelete(long var0, byte[] var2, int var3, long var4) throws RocksDBException;

   private static native void singleDelete(long var0, long var2, byte[] var4, int var5) throws RocksDBException;

   private static native void singleDelete(long var0, long var2, byte[] var4, int var5, long var6) throws RocksDBException;

   private static native void deleteRange(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native void deleteRange(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8) throws RocksDBException;

   private static native void deleteRange(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws RocksDBException;

   private static native void deleteRange(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10) throws RocksDBException;

   private static native void clipColumnFamily(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws RocksDBException;

   private static native void merge(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native void merge(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8) throws RocksDBException;

   private static native void merge(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws RocksDBException;

   private static native void merge(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10) throws RocksDBException;

   private static native void mergeDirect(long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10) throws RocksDBException;

   private static native void write0(long var0, long var2, long var4) throws RocksDBException;

   private static native void write1(long var0, long var2, long var4) throws RocksDBException;

   private static native int get(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7) throws RocksDBException;

   private static native int get(long var0, byte[] var2, int var3, int var4, byte[] var5, int var6, int var7, long var8) throws RocksDBException;

   private static native int get(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9) throws RocksDBException;

   private static native int get(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10) throws RocksDBException;

   private static native byte[] get(long var0, byte[] var2, int var3, int var4) throws RocksDBException;

   private static native byte[] get(long var0, byte[] var2, int var3, int var4, long var5) throws RocksDBException;

   private static native byte[] get(long var0, long var2, byte[] var4, int var5, int var6) throws RocksDBException;

   private static native byte[] get(long var0, long var2, byte[] var4, int var5, int var6, long var7) throws RocksDBException;

   private static native byte[][] multiGet(long var0, byte[][] var2, int[] var3, int[] var4);

   private static native byte[][] multiGet(long var0, byte[][] var2, int[] var3, int[] var4, long[] var5);

   private static native byte[][] multiGet(long var0, long var2, byte[][] var4, int[] var5, int[] var6);

   private static native byte[][] multiGet(long var0, long var2, byte[][] var4, int[] var5, int[] var6, long[] var7);

   private static native void multiGet(
      long var0, long var2, long[] var4, ByteBuffer[] var5, int[] var6, int[] var7, ByteBuffer[] var8, int[] var9, Status[] var10
   );

   private static native boolean keyExists(long var0, long var2, long var4, byte[] var6, int var7, int var8);

   private static native boolean keyExistsDirect(long var0, long var2, long var4, ByteBuffer var6, int var7, int var8);

   private static native boolean keyMayExist(long var0, long var2, long var4, byte[] var6, int var7, int var8);

   private static native byte[][] keyMayExistFoundValue(long var0, long var2, long var4, byte[] var6, int var7, int var8);

   private static native void putDirect(long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10) throws RocksDBException;

   private static native long iterator(long var0, long var2, long var4);

   private static native long[] iterators(long var0, long[] var2, long var3) throws RocksDBException;

   private static native long getSnapshot(long var0);

   private static native void releaseSnapshot(long var0, long var2);

   private static native String getProperty(long var0, long var2, String var4, int var5) throws RocksDBException;

   private static native Map<String, String> getMapProperty(long var0, long var2, String var4, int var5) throws RocksDBException;

   private static native int getDirect(long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10) throws RocksDBException;

   private static native boolean keyMayExistDirect(long var0, long var2, long var4, ByteBuffer var6, int var7, int var8);

   private static native int[] keyMayExistDirectFoundValue(
      long var0, long var2, long var4, ByteBuffer var6, int var7, int var8, ByteBuffer var9, int var10, int var11
   );

   private static native void deleteDirect(long var0, long var2, ByteBuffer var4, int var5, int var6, long var7) throws RocksDBException;

   private static native long getLongProperty(long var0, long var2, String var4, int var5) throws RocksDBException;

   private static native void resetStats(long var0) throws RocksDBException;

   private static native long getAggregatedLongProperty(long var0, String var2, int var3) throws RocksDBException;

   private static native long[] getApproximateSizes(long var0, long var2, long[] var4, byte var5);

   private static native long[] getApproximateMemTableStats(long var0, long var2, long var4, long var6);

   private static native void compactRange(long var0, byte[] var2, int var3, byte[] var4, int var5, long var6, long var8) throws RocksDBException;

   private static native void setOptions(long var0, long var2, String[] var4, String[] var5) throws RocksDBException;

   private static native String getOptions(long var0, long var2);

   private static native void setDBOptions(long var0, String[] var2, String[] var3) throws RocksDBException;

   private static native String getDBOptions(long var0);

   private static native void setPerfLevel(byte var0);

   private static native byte getPerfLevelNative();

   private static native long getPerfContextNative();

   private static native String[] compactFiles(long var0, long var2, long var4, String[] var6, int var7, int var8, long var9) throws RocksDBException;

   private static native void cancelAllBackgroundWork(long var0, boolean var2);

   private static native void pauseBackgroundWork(long var0) throws RocksDBException;

   private static native void continueBackgroundWork(long var0) throws RocksDBException;

   private static native void enableAutoCompaction(long var0, long[] var2) throws RocksDBException;

   private static native int numberLevels(long var0, long var2);

   private static native int maxMemCompactionLevel(long var0, long var2);

   private static native int level0StopWriteTrigger(long var0, long var2);

   private static native String getName(long var0);

   private static native long getEnv(long var0);

   private static native void flush(long var0, long var2, long[] var4) throws RocksDBException;

   private static native void flushWal(long var0, boolean var2) throws RocksDBException;

   private static native void syncWal(long var0) throws RocksDBException;

   private static native long getLatestSequenceNumber(long var0);

   private static native void disableFileDeletions(long var0) throws RocksDBException;

   private static native void enableFileDeletions(long var0) throws RocksDBException;

   private static native String[] getLiveFiles(long var0, boolean var2) throws RocksDBException;

   private static native LogFile[] getSortedWalFiles(long var0) throws RocksDBException;

   private static native long getUpdatesSince(long var0, long var2) throws RocksDBException;

   private static native LiveFileMetaData[] getLiveFilesMetaData(long var0);

   private static native ColumnFamilyMetaData getColumnFamilyMetaData(long var0, long var2);

   private static native void ingestExternalFile(long var0, long var2, String[] var4, int var5, long var6) throws RocksDBException;

   private static native void verifyChecksum(long var0) throws RocksDBException;

   private static native long getDefaultColumnFamily(long var0);

   private static native Map<String, TableProperties> getPropertiesOfAllTables(long var0, long var2) throws RocksDBException;

   private static native Map<String, TableProperties> getPropertiesOfTablesInRange(long var0, long var2, long[] var4);

   private static native long[] suggestCompactRange(long var0, long var2) throws RocksDBException;

   private static native void promoteL0(long var0, long var2, int var4) throws RocksDBException;

   private static native void startTrace(long var0, long var2, long var4) throws RocksDBException;

   private static native void endTrace(long var0) throws RocksDBException;

   private static native void tryCatchUpWithPrimary(long var0) throws RocksDBException;

   private static native void deleteFilesInRanges(long var0, long var2, byte[][] var4, boolean var5) throws RocksDBException;

   private static native void destroyDB(String var0, long var1) throws RocksDBException;

   private static native int version();

   public static class CountAndSize {
      public final long count;
      public final long size;

      public CountAndSize(long var1, long var3) {
         this.count = var1;
         this.size = var3;
      }
   }

   private enum LibraryState {
      NOT_LOADED,
      LOADING,
      LOADED;
   }

   public static class LiveFiles {
      public final long manifestFileSize;
      public final List<String> files;

      LiveFiles(long var1, List<String> var3) {
         this.manifestFileSize = var1;
         this.files = var3;
      }
   }

   public static class Version {
      private final byte major;
      private final byte minor;
      private final byte patch;

      public Version(byte var1, byte var2, byte var3) {
         this.major = var1;
         this.minor = var2;
         this.patch = var3;
      }

      public int getMajor() {
         return this.major;
      }

      public int getMinor() {
         return this.minor;
      }

      public int getPatch() {
         return this.patch;
      }

      @Override
      public String toString() {
         return this.getMajor() + "." + this.getMinor() + "." + this.getPatch();
      }

      private static RocksDB.Version fromEncodedVersion(int var0) {
         byte var1 = (byte)(var0 & 0xFF);
         byte var2 = (byte)(var0 >> 8 & 0xFF);
         byte var3 = (byte)(var0 >> 16 & 0xFF);
         return new RocksDB.Version(var3, var2, var1);
      }
   }
}
