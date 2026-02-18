/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.rocksdb.AbstractTraceWriter;
import org.rocksdb.ByteBufferGetStatus;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.ColumnFamilyMetaData;
import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.CompactRangeOptions;
import org.rocksdb.CompactionJobInfo;
import org.rocksdb.CompactionOptions;
import org.rocksdb.CompressionType;
import org.rocksdb.DBOptions;
import org.rocksdb.DBOptionsInterface;
import org.rocksdb.Env;
import org.rocksdb.ExportImportFilesMetaData;
import org.rocksdb.FlushOptions;
import org.rocksdb.Holder;
import org.rocksdb.ImportColumnFamilyOptions;
import org.rocksdb.IngestExternalFileOptions;
import org.rocksdb.KeyMayExist;
import org.rocksdb.LiveFileMetaData;
import org.rocksdb.LogFile;
import org.rocksdb.MutableColumnFamilyOptions;
import org.rocksdb.MutableDBOptions;
import org.rocksdb.NativeLibraryLoader;
import org.rocksdb.Options;
import org.rocksdb.PerfContext;
import org.rocksdb.PerfLevel;
import org.rocksdb.Range;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksEnv;
import org.rocksdb.RocksIterator;
import org.rocksdb.RocksObject;
import org.rocksdb.SizeApproximationFlag;
import org.rocksdb.Slice;
import org.rocksdb.Snapshot;
import org.rocksdb.Status;
import org.rocksdb.TableProperties;
import org.rocksdb.TraceOptions;
import org.rocksdb.TransactionLogIterator;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteBatchWithIndex;
import org.rocksdb.WriteOptions;
import org.rocksdb.util.BufferUtil;
import org.rocksdb.util.Environment;

public class RocksDB
extends RocksObject {
    public static final byte[] DEFAULT_COLUMN_FAMILY = "default".getBytes(StandardCharsets.UTF_8);
    public static final int NOT_FOUND = -1;
    private static final AtomicReference<LibraryState> libraryLoaded = new AtomicReference<LibraryState>(LibraryState.NOT_LOADED);
    static final String PERFORMANCE_OPTIMIZATION_FOR_A_VERY_SPECIFIC_WORKLOAD = "Performance optimization for a very specific workload";
    private static final String BB_ALL_DIRECT_OR_INDIRECT = "ByteBuffer parameters must all be direct, or must all be indirect";
    private ColumnFamilyHandle defaultColumnFamilyHandle_;
    private final ReadOptions defaultReadOptions_ = new ReadOptions();
    final List<ColumnFamilyHandle> ownedColumnFamilyHandles = new ArrayList<ColumnFamilyHandle>();
    protected DBOptionsInterface<?> options_;
    private static Version version;

    public static void loadLibrary() {
        if (libraryLoaded.get() == LibraryState.LOADED) {
            return;
        }
        if (libraryLoaded.compareAndSet(LibraryState.NOT_LOADED, LibraryState.LOADING)) {
            String string = System.getenv("ROCKSDB_SHAREDLIB_DIR");
            for (CompressionType compressionType : CompressionType.values()) {
                try {
                    if (compressionType.getLibraryName() == null) continue;
                    System.loadLibrary(compressionType.getLibraryName());
                }
                catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                    // empty catch block
                }
            }
            try {
                NativeLibraryLoader.getInstance().loadLibrary(string);
            }
            catch (IOException iOException) {
                libraryLoaded.set(LibraryState.NOT_LOADED);
                throw new RuntimeException("Unable to load the RocksDB shared library", iOException);
            }
            int n = RocksDB.version();
            version = Version.fromEncodedVersion(n);
            libraryLoaded.set(LibraryState.LOADED);
            return;
        }
        RocksDB.waitForLibraryToBeLoaded();
    }

    public static void loadLibrary(List<String> list) {
        if (libraryLoaded.get() == LibraryState.LOADED) {
            return;
        }
        if (libraryLoaded.compareAndSet(LibraryState.NOT_LOADED, LibraryState.LOADING)) {
            block4: for (CompressionType object : CompressionType.values()) {
                if (object.equals((Object)CompressionType.NO_COMPRESSION)) continue;
                for (String string : list) {
                    try {
                        System.load(string + "/" + Environment.getSharedLibraryFileName(object.getLibraryName()));
                        continue block4;
                    }
                    catch (UnsatisfiedLinkError unsatisfiedLinkError) {
                    }
                }
            }
            boolean bl = false;
            UnsatisfiedLinkError unsatisfiedLinkError = null;
            for (String string : list) {
                try {
                    System.load(string + "/" + Environment.getJniLibraryFileName("rocksdbjni"));
                    bl = true;
                    break;
                }
                catch (UnsatisfiedLinkError unsatisfiedLinkError2) {
                    unsatisfiedLinkError = unsatisfiedLinkError2;
                }
            }
            if (!bl) {
                libraryLoaded.set(LibraryState.NOT_LOADED);
                throw unsatisfiedLinkError;
            }
            int n = RocksDB.version();
            version = Version.fromEncodedVersion(n);
            libraryLoaded.set(LibraryState.LOADED);
            return;
        }
        RocksDB.waitForLibraryToBeLoaded();
    }

    private static void waitForLibraryToBeLoaded() {
        long l = 0L;
        try {
            while (libraryLoaded.get() == LibraryState.LOADING) {
                Thread.sleep(10L);
                if ((l += 10L) < 10000L) continue;
                throw new RuntimeException("Exceeded timeout whilst trying to load the RocksDB shared library");
            }
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted whilst trying to load the RocksDB shared library", interruptedException);
        }
    }

    public static Version rocksdbVersion() {
        return version;
    }

    public boolean isClosed() {
        return !this.owningHandle_.get();
    }

    protected RocksDB(long l) {
        super(l);
    }

    public static RocksDB open(String string) throws RocksDBException {
        RocksDB.loadLibrary();
        try (Options options = new Options();){
            options.setCreateIfMissing(true);
            RocksDB rocksDB = RocksDB.open(options, string);
            return rocksDB;
        }
    }

    public static RocksDB open(String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        try (DBOptions dBOptions = new DBOptions();){
            RocksDB rocksDB = RocksDB.open(dBOptions, string, list, list2);
            return rocksDB;
        }
    }

    public static RocksDB open(Options options, String string) throws RocksDBException {
        RocksDB rocksDB = new RocksDB(RocksDB.open(options.nativeHandle_, string));
        rocksDB.storeOptionsInstance(options);
        rocksDB.storeDefaultColumnFamilyHandle(rocksDB.makeDefaultColumnFamilyHandle());
        return rocksDB;
    }

    public static RocksDB open(DBOptions dBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        Object object;
        byte[][] byArrayArray = new byte[list.size()][];
        long[] lArray = new long[list.size()];
        int n = -1;
        for (int i = 0; i < list.size(); ++i) {
            object = list.get(i);
            byArrayArray[i] = ((ColumnFamilyDescriptor)object).getName();
            lArray[i] = ((ColumnFamilyDescriptor)object).getOptions().nativeHandle_;
            if (!Arrays.equals(((ColumnFamilyDescriptor)object).getName(), DEFAULT_COLUMN_FAMILY)) continue;
            n = i;
        }
        if (n < 0) {
            throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
        }
        long[] lArray2 = RocksDB.open(dBOptions.nativeHandle_, string, byArrayArray, lArray);
        object = new RocksDB(lArray2[0]);
        ((RocksDB)object).storeOptionsInstance(dBOptions);
        for (int i = 1; i < lArray2.length; ++i) {
            ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle((RocksDB)object, lArray2[i]);
            list2.add(columnFamilyHandle);
        }
        ((RocksDB)object).ownedColumnFamilyHandles.addAll(list2);
        ((RocksDB)object).storeDefaultColumnFamilyHandle(list2.get(n));
        return object;
    }

    public static RocksDB openReadOnly(String string) throws RocksDBException {
        RocksDB.loadLibrary();
        try (Options options = new Options();){
            RocksDB rocksDB = RocksDB.openReadOnly(options, string);
            return rocksDB;
        }
    }

    public static RocksDB openReadOnly(Options options, String string) throws RocksDBException {
        return RocksDB.openReadOnly(options, string, false);
    }

    public static RocksDB openReadOnly(Options options, String string, boolean bl) throws RocksDBException {
        RocksDB rocksDB = new RocksDB(RocksDB.openROnly(options.nativeHandle_, string, bl));
        rocksDB.storeOptionsInstance(options);
        rocksDB.storeDefaultColumnFamilyHandle(rocksDB.makeDefaultColumnFamilyHandle());
        return rocksDB;
    }

    public static RocksDB openReadOnly(String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        try (DBOptions dBOptions = new DBOptions();){
            RocksDB rocksDB = RocksDB.openReadOnly(dBOptions, string, list, list2, false);
            return rocksDB;
        }
    }

    public static RocksDB openReadOnly(DBOptions dBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        return RocksDB.openReadOnly(dBOptions, string, list, list2, false);
    }

    public static RocksDB openReadOnly(DBOptions dBOptions, String string, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2, boolean bl) throws RocksDBException {
        Object object;
        byte[][] byArrayArray = new byte[list.size()][];
        long[] lArray = new long[list.size()];
        int n = -1;
        for (int i = 0; i < list.size(); ++i) {
            object = list.get(i);
            byArrayArray[i] = ((ColumnFamilyDescriptor)object).getName();
            lArray[i] = ((ColumnFamilyDescriptor)object).getOptions().nativeHandle_;
            if (!Arrays.equals(((ColumnFamilyDescriptor)object).getName(), DEFAULT_COLUMN_FAMILY)) continue;
            n = i;
        }
        if (n < 0) {
            throw new IllegalArgumentException("You must provide the default column family in your columnFamilyDescriptors");
        }
        long[] lArray2 = RocksDB.openROnly(dBOptions.nativeHandle_, string, byArrayArray, lArray, bl);
        object = new RocksDB(lArray2[0]);
        ((RocksDB)object).storeOptionsInstance(dBOptions);
        for (int i = 1; i < lArray2.length; ++i) {
            ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle((RocksDB)object, lArray2[i]);
            list2.add(columnFamilyHandle);
        }
        ((RocksDB)object).ownedColumnFamilyHandles.addAll(list2);
        ((RocksDB)object).storeDefaultColumnFamilyHandle(list2.get(n));
        return object;
    }

    public static RocksDB openAsSecondary(Options options, String string, String string2) throws RocksDBException {
        RocksDB rocksDB = new RocksDB(RocksDB.openAsSecondary(options.nativeHandle_, string, string2));
        rocksDB.storeOptionsInstance(options);
        rocksDB.storeDefaultColumnFamilyHandle(rocksDB.makeDefaultColumnFamilyHandle());
        return rocksDB;
    }

    public static RocksDB openAsSecondary(DBOptions dBOptions, String string, String string2, List<ColumnFamilyDescriptor> list, List<ColumnFamilyHandle> list2) throws RocksDBException {
        Object object;
        byte[][] byArrayArray = new byte[list.size()][];
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            object = list.get(i);
            byArrayArray[i] = ((ColumnFamilyDescriptor)object).getName();
            lArray[i] = ((ColumnFamilyDescriptor)object).getOptions().nativeHandle_;
        }
        long[] lArray2 = RocksDB.openAsSecondary(dBOptions.nativeHandle_, string, string2, byArrayArray, lArray);
        object = new RocksDB(lArray2[0]);
        ((RocksDB)object).storeOptionsInstance(dBOptions);
        for (int i = 1; i < lArray2.length; ++i) {
            ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle((RocksDB)object, lArray2[i]);
            list2.add(columnFamilyHandle);
        }
        ((RocksDB)object).ownedColumnFamilyHandles.addAll(list2);
        ((RocksDB)object).storeDefaultColumnFamilyHandle(((RocksDB)object).makeDefaultColumnFamilyHandle());
        return object;
    }

    public void closeE() throws RocksDBException {
        for (ColumnFamilyHandle columnFamilyHandle : this.ownedColumnFamilyHandles) {
            columnFamilyHandle.close();
        }
        this.ownedColumnFamilyHandles.clear();
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                RocksDB.closeDatabase(this.nativeHandle_);
            }
            finally {
                this.disposeInternal();
            }
        }
    }

    @Override
    public void close() {
        for (ColumnFamilyHandle columnFamilyHandle : this.ownedColumnFamilyHandles) {
            columnFamilyHandle.close();
        }
        this.ownedColumnFamilyHandles.clear();
        if (this.owningHandle_.compareAndSet(true, false)) {
            try {
                RocksDB.closeDatabase(this.nativeHandle_);
            }
            catch (RocksDBException rocksDBException) {
            }
            finally {
                this.disposeInternal();
            }
        }
    }

    public static List<byte[]> listColumnFamilies(Options options, String string) throws RocksDBException {
        return Arrays.asList(RocksDB.listColumnFamilies(options.nativeHandle_, string));
    }

    public ColumnFamilyHandle createColumnFamily(ColumnFamilyDescriptor columnFamilyDescriptor) throws RocksDBException {
        ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle(this, RocksDB.createColumnFamily(this.nativeHandle_, columnFamilyDescriptor.getName(), columnFamilyDescriptor.getName().length, columnFamilyDescriptor.getOptions().nativeHandle_));
        this.ownedColumnFamilyHandles.add(columnFamilyHandle);
        return columnFamilyHandle;
    }

    public List<ColumnFamilyHandle> createColumnFamilies(ColumnFamilyOptions columnFamilyOptions, List<byte[]> list) throws RocksDBException {
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[0][]);
        long[] lArray = RocksDB.createColumnFamilies(this.nativeHandle_, columnFamilyOptions.nativeHandle_, byArray);
        ArrayList<ColumnFamilyHandle> arrayList = new ArrayList<ColumnFamilyHandle>(lArray.length);
        for (long l : lArray) {
            ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle(this, l);
            arrayList.add(columnFamilyHandle);
        }
        this.ownedColumnFamilyHandles.addAll(arrayList);
        return arrayList;
    }

    public List<ColumnFamilyHandle> createColumnFamilies(List<ColumnFamilyDescriptor> list) throws RocksDBException {
        Object object;
        long[] lArray = new long[list.size()];
        byte[][] byArrayArray = new byte[list.size()][];
        for (int i = 0; i < list.size(); ++i) {
            object = list.get(i);
            lArray[i] = ((ColumnFamilyDescriptor)object).getOptions().nativeHandle_;
            byArrayArray[i] = ((ColumnFamilyDescriptor)object).getName();
        }
        long[] lArray2 = RocksDB.createColumnFamilies(this.nativeHandle_, lArray, (byte[][])byArrayArray);
        object = new ArrayList(lArray2.length);
        for (long l : lArray2) {
            ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle(this, l);
            object.add(columnFamilyHandle);
        }
        this.ownedColumnFamilyHandles.addAll((Collection<ColumnFamilyHandle>)object);
        return object;
    }

    public ColumnFamilyHandle createColumnFamilyWithImport(ColumnFamilyDescriptor columnFamilyDescriptor, ImportColumnFamilyOptions importColumnFamilyOptions, ExportImportFilesMetaData exportImportFilesMetaData) throws RocksDBException {
        ArrayList<ExportImportFilesMetaData> arrayList = new ArrayList<ExportImportFilesMetaData>();
        arrayList.add(exportImportFilesMetaData);
        return this.createColumnFamilyWithImport(columnFamilyDescriptor, importColumnFamilyOptions, arrayList);
    }

    public ColumnFamilyHandle createColumnFamilyWithImport(ColumnFamilyDescriptor columnFamilyDescriptor, ImportColumnFamilyOptions importColumnFamilyOptions, List<ExportImportFilesMetaData> list) throws RocksDBException {
        int n = list.size();
        long[] lArray = new long[n];
        for (int i = 0; i < n; ++i) {
            lArray[i] = list.get(i).getNativeHandle();
        }
        ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle(this, RocksDB.createColumnFamilyWithImport(this.nativeHandle_, columnFamilyDescriptor.getName(), columnFamilyDescriptor.getName().length, columnFamilyDescriptor.getOptions().nativeHandle_, importColumnFamilyOptions.nativeHandle_, lArray));
        this.ownedColumnFamilyHandles.add(columnFamilyHandle);
        return columnFamilyHandle;
    }

    public void dropColumnFamily(ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        RocksDB.dropColumnFamily(this.nativeHandle_, columnFamilyHandle.nativeHandle_);
    }

    public void dropColumnFamilies(List<ColumnFamilyHandle> list) throws RocksDBException {
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        RocksDB.dropColumnFamilies(this.nativeHandle_, lArray);
    }

    public void destroyColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle) {
        for (int i = 0; i < this.ownedColumnFamilyHandles.size(); ++i) {
            ColumnFamilyHandle columnFamilyHandle2 = this.ownedColumnFamilyHandles.get(i);
            if (!columnFamilyHandle2.equals(columnFamilyHandle)) continue;
            columnFamilyHandle.close();
            this.ownedColumnFamilyHandles.remove(i);
            return;
        }
    }

    public void put(byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.put(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void put(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.put(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.put(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.put(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public void put(WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void put(WriteOptions writeOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            RocksDB.putDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void put(WriteOptions writeOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            RocksDB.putDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), 0L);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining());
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.put(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public void delete(byte[] byArray) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, byArray, 0, byArray.length);
    }

    public void delete(byte[] byArray, int n, int n2) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, byArray, n, n2);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, byArray, n, n2, columnFamilyHandle.nativeHandle_);
    }

    public void delete(WriteOptions writeOptions, byte[] byArray) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length);
    }

    public void delete(WriteOptions writeOptions, byte[] byArray, int n, int n2) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, int n, int n2) throws RocksDBException {
        RocksDB.delete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2, columnFamilyHandle.nativeHandle_);
    }

    public int get(ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        int n;
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            n = RocksDB.getDirect(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), 0L);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            n = RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), this.defaultColumnFamilyHandle_.nativeHandle_);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        if (n != -1) {
            byteBuffer2.limit(Math.min(byteBuffer2.limit(), byteBuffer2.position() + n));
        }
        byteBuffer.position(byteBuffer.limit());
        return n;
    }

    public int get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (byteBuffer.isDirect() && byteBuffer2.isDirect());
        int n = RocksDB.getDirect(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        if (n != -1) {
            byteBuffer2.limit(Math.min(byteBuffer2.limit(), byteBuffer2.position() + n));
        }
        byteBuffer.position(byteBuffer.limit());
        return n;
    }

    public void singleDelete(byte[] byArray) throws RocksDBException {
        RocksDB.singleDelete(this.nativeHandle_, byArray, byArray.length);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        RocksDB.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void singleDelete(WriteOptions writeOptions, byte[] byArray) throws RocksDBException {
        RocksDB.singleDelete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, byArray.length);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray) throws RocksDBException {
        RocksDB.singleDelete(this.nativeHandle_, writeOptions.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void deleteRange(byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.deleteRange(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void deleteRange(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.deleteRange(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void deleteRange(WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.deleteRange(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void deleteRange(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.deleteRange(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void merge(byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.merge(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void merge(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.merge(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.merge(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.merge(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public void merge(WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void merge(WriteOptions writeOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public void merge(WriteOptions writeOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            RocksDB.mergeDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), 0L);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining());
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            RocksDB.mergeDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void delete(WriteOptions writeOptions, ByteBuffer byteBuffer) throws RocksDBException {
        assert (byteBuffer.isDirect());
        RocksDB.deleteDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), 0L);
        byteBuffer.position(byteBuffer.limit());
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, ByteBuffer byteBuffer) throws RocksDBException {
        assert (byteBuffer.isDirect());
        RocksDB.deleteDirect(this.nativeHandle_, writeOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), columnFamilyHandle.nativeHandle_);
        byteBuffer.position(byteBuffer.limit());
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, WriteOptions writeOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        RocksDB.merge(this.nativeHandle_, writeOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public void write(WriteOptions writeOptions, WriteBatch writeBatch) throws RocksDBException {
        RocksDB.write0(this.nativeHandle_, writeOptions.nativeHandle_, writeBatch.nativeHandle_);
    }

    public void write(WriteOptions writeOptions, WriteBatchWithIndex writeBatchWithIndex) throws RocksDBException {
        RocksDB.write1(this.nativeHandle_, writeOptions.nativeHandle_, writeBatchWithIndex.nativeHandle_);
    }

    public int get(byte[] byArray, byte[] byArray2) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public int get(byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        return RocksDB.get(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public int get(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException, IllegalArgumentException {
        return RocksDB.get(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public int get(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException, IllegalArgumentException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        return RocksDB.get(this.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public int get(ReadOptions readOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public int get(ReadOptions readOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4);
    }

    public int get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public int get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, int n, int n2, byte[] byArray2, int n3, int n4) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        BufferUtil.CheckBounds(n3, n4, byArray2.length);
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, n, n2, byArray2, n3, n4, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(byte[] byArray) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, byArray, 0, byArray.length);
    }

    public byte[] get(byte[] byArray, int n, int n2) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksDB.get(this.nativeHandle_, byArray, n, n2);
    }

    public byte[] get(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksDB.get(this.nativeHandle_, byArray, n, n2, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length);
    }

    public byte[] get(ReadOptions readOptions, byte[] byArray, int n, int n2) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, n, n2);
    }

    public byte[] get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, int n, int n2) throws RocksDBException {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        return RocksDB.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, n, n2, columnFamilyHandle.nativeHandle_);
    }

    public List<byte[]> multiGetAsList(List<byte[]> list) throws RocksDBException {
        assert (!list.isEmpty());
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        int[] nArray = new int[byArray.length];
        int[] nArray2 = new int[byArray.length];
        for (int i = 0; i < nArray2.length; ++i) {
            nArray2[i] = byArray[i].length;
        }
        return Arrays.asList(RocksDB.multiGet(this.nativeHandle_, byArray, nArray, nArray2));
    }

    public List<byte[]> multiGetAsList(List<ColumnFamilyHandle> list, List<byte[]> list2) throws RocksDBException, IllegalArgumentException {
        assert (!list2.isEmpty());
        if (list2.size() != list.size()) {
            throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
        }
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        byte[][] byArray = (byte[][])list2.toArray((T[])new byte[list2.size()][]);
        int[] nArray = new int[byArray.length];
        int[] nArray2 = new int[byArray.length];
        for (int i = 0; i < nArray2.length; ++i) {
            nArray2[i] = byArray[i].length;
        }
        return Arrays.asList(RocksDB.multiGet(this.nativeHandle_, byArray, nArray, nArray2, lArray));
    }

    public List<byte[]> multiGetAsList(ReadOptions readOptions, List<byte[]> list) throws RocksDBException {
        assert (!list.isEmpty());
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        int[] nArray = new int[byArray.length];
        int[] nArray2 = new int[byArray.length];
        for (int i = 0; i < nArray2.length; ++i) {
            nArray2[i] = byArray[i].length;
        }
        return Arrays.asList(RocksDB.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray, nArray, nArray2));
    }

    public List<byte[]> multiGetAsList(ReadOptions readOptions, List<ColumnFamilyHandle> list, List<byte[]> list2) throws RocksDBException {
        assert (!list2.isEmpty());
        if (list2.size() != list.size()) {
            throw new IllegalArgumentException("For each key there must be a ColumnFamilyHandle.");
        }
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        byte[][] byArray = (byte[][])list2.toArray((T[])new byte[list2.size()][]);
        int[] nArray = new int[byArray.length];
        int[] nArray2 = new int[byArray.length];
        for (int i = 0; i < nArray2.length; ++i) {
            nArray2[i] = byArray[i].length;
        }
        return Arrays.asList(RocksDB.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray, nArray, nArray2, lArray));
    }

    public List<ByteBufferGetStatus> multiGetByteBuffers(List<ByteBuffer> list, List<ByteBuffer> list2) throws RocksDBException {
        try (ReadOptions readOptions = new ReadOptions();){
            ArrayList<ColumnFamilyHandle> arrayList = new ArrayList<ColumnFamilyHandle>(1);
            arrayList.add(this.getDefaultColumnFamily());
            List<ByteBufferGetStatus> list3 = this.multiGetByteBuffers(readOptions, arrayList, list, list2);
            return list3;
        }
    }

    public List<ByteBufferGetStatus> multiGetByteBuffers(ReadOptions readOptions, List<ByteBuffer> list, List<ByteBuffer> list2) throws RocksDBException {
        ArrayList<ColumnFamilyHandle> arrayList = new ArrayList<ColumnFamilyHandle>(1);
        arrayList.add(this.getDefaultColumnFamily());
        return this.multiGetByteBuffers(readOptions, arrayList, list, list2);
    }

    public List<ByteBufferGetStatus> multiGetByteBuffers(List<ColumnFamilyHandle> list, List<ByteBuffer> list2, List<ByteBuffer> list3) throws RocksDBException {
        try (ReadOptions readOptions = new ReadOptions();){
            List<ByteBufferGetStatus> list4 = this.multiGetByteBuffers(readOptions, list, list2, list3);
            return list4;
        }
    }

    public List<ByteBufferGetStatus> multiGetByteBuffers(ReadOptions readOptions, List<ColumnFamilyHandle> list, List<ByteBuffer> list2, List<ByteBuffer> list3) throws RocksDBException {
        int n;
        assert (!list2.isEmpty());
        if (list2.size() != list.size() && list.size() > 1) {
            throw new IllegalArgumentException("Wrong number of ColumnFamilyHandle(s) supplied. Provide 0, 1, or as many as there are key/value(s)");
        }
        if (list3.size() != list2.size()) {
            throw new IllegalArgumentException("For each key there must be a corresponding value. " + list2.size() + " keys were supplied, but " + list3.size() + " values were supplied.");
        }
        for (ByteBuffer object2 : list2) {
            if (object2.isDirect()) continue;
            throw new IllegalArgumentException("All key buffers must be direct byte buffers");
        }
        for (ByteBuffer byteBuffer : list3) {
            if (byteBuffer.isDirect()) continue;
            throw new IllegalArgumentException("All value buffers must be direct byte buffers");
        }
        int n2 = list.size();
        long[] lArray = new long[n2];
        for (n = 0; n < n2; ++n) {
            lArray[n] = list.get((int)n).nativeHandle_;
        }
        n = list2.size();
        ByteBuffer[] byteBufferArray = list2.toArray(new ByteBuffer[0]);
        int[] nArray = new int[n];
        int[] nArray2 = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = byteBufferArray[i].position();
            nArray2[i] = byteBufferArray[i].limit();
        }
        ByteBuffer[] byteBufferArray2 = list3.toArray(new ByteBuffer[0]);
        int[] nArray3 = new int[n];
        Status[] statusArray = new Status[n];
        RocksDB.multiGet(this.nativeHandle_, readOptions.nativeHandle_, lArray, byteBufferArray, nArray, nArray2, byteBufferArray2, nArray3, statusArray);
        ArrayList<ByteBufferGetStatus> arrayList = new ArrayList<ByteBufferGetStatus>();
        for (int i = 0; i < n; ++i) {
            ByteBuffer byteBuffer;
            Status status = statusArray[i];
            if (status.getCode() == Status.Code.Ok) {
                byteBuffer = byteBufferArray2[i];
                byteBuffer.position(Math.min(nArray3[i], byteBuffer.capacity()));
                byteBuffer.flip();
                arrayList.add(new ByteBufferGetStatus(status, nArray3[i], byteBuffer));
                continue;
            }
            if (status.getCode() == Status.Code.Incomplete) {
                assert (nArray3[i] == -1);
                byteBuffer = byteBufferArray2[i];
                byteBuffer.position(byteBuffer.capacity());
                byteBuffer.flip();
                arrayList.add(new ByteBufferGetStatus(status, byteBuffer.capacity(), byteBuffer));
                continue;
            }
            arrayList.add(new ByteBufferGetStatus(status));
        }
        return arrayList;
    }

    public boolean keyExists(byte[] byArray) {
        return this.keyExists(byArray, 0, byArray.length);
    }

    public boolean keyExists(byte[] byArray, int n, int n2) {
        return this.keyExists(null, null, byArray, n, n2);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) {
        return this.keyExists(columnFamilyHandle, byArray, 0, byArray.length);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2) {
        return this.keyExists(columnFamilyHandle, null, byArray, n, n2);
    }

    public boolean keyExists(ReadOptions readOptions, byte[] byArray) {
        return this.keyExists(readOptions, byArray, 0, byArray.length);
    }

    public boolean keyExists(ReadOptions readOptions, byte[] byArray, int n, int n2) {
        return this.keyExists(null, readOptions, byArray, n, n2);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray) {
        return this.keyExists(columnFamilyHandle, readOptions, byArray, 0, byArray.length);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, int n, int n2) {
        RocksDB.checkBounds(n, n2, byArray.length);
        return RocksDB.keyExists(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byArray, n, n2);
    }

    public boolean keyExists(ByteBuffer byteBuffer) {
        return this.keyExists(null, null, byteBuffer);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer) {
        return this.keyExists(columnFamilyHandle, null, byteBuffer);
    }

    public boolean keyExists(ReadOptions readOptions, ByteBuffer byteBuffer) {
        return this.keyExists(null, readOptions, byteBuffer);
    }

    public boolean keyExists(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, ByteBuffer byteBuffer) {
        assert (byteBuffer != null) : "key ByteBuffer parameter cannot be null";
        assert (byteBuffer.isDirect()) : "key parameter must be a direct ByteBuffer";
        return RocksDB.keyExistsDirect(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.limit());
    }

    public boolean keyMayExist(byte[] byArray, Holder<byte[]> holder) {
        return this.keyMayExist(byArray, 0, byArray.length, holder);
    }

    public boolean keyMayExist(byte[] byArray, int n, int n2, Holder<byte[]> holder) {
        return this.keyMayExist((ColumnFamilyHandle)null, byArray, n, n2, holder);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, Holder<byte[]> holder) {
        return this.keyMayExist(columnFamilyHandle, byArray, 0, byArray.length, holder);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, int n, int n2, Holder<byte[]> holder) {
        return this.keyMayExist(columnFamilyHandle, null, byArray, n, n2, holder);
    }

    public boolean keyMayExist(ReadOptions readOptions, byte[] byArray, Holder<byte[]> holder) {
        return this.keyMayExist(readOptions, byArray, 0, byArray.length, holder);
    }

    public boolean keyMayExist(ReadOptions readOptions, byte[] byArray, int n, int n2, Holder<byte[]> holder) {
        return this.keyMayExist(null, readOptions, byArray, n, n2, holder);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, Holder<byte[]> holder) {
        return this.keyMayExist(columnFamilyHandle, readOptions, byArray, 0, byArray.length, holder);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray, int n, int n2, Holder<byte[]> holder) {
        BufferUtil.CheckBounds(n, n2, byArray.length);
        if (holder == null) {
            return RocksDB.keyMayExist(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byArray, n, n2);
        }
        byte[][] byArray2 = RocksDB.keyMayExistFoundValue(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byArray, n, n2);
        if (byArray2[0][0] == 0) {
            holder.setValue(null);
            return false;
        }
        if (byArray2[0][0] == 1) {
            holder.setValue(null);
            return true;
        }
        holder.setValue(byArray2[1]);
        return true;
    }

    public boolean keyMayExist(ByteBuffer byteBuffer) {
        return this.keyMayExist(null, (ReadOptions)null, byteBuffer);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer) {
        return this.keyMayExist(columnFamilyHandle, (ReadOptions)null, byteBuffer);
    }

    public boolean keyMayExist(ReadOptions readOptions, ByteBuffer byteBuffer) {
        return this.keyMayExist(null, readOptions, byteBuffer);
    }

    public KeyMayExist keyMayExist(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return this.keyMayExist(null, null, byteBuffer, byteBuffer2);
    }

    public KeyMayExist keyMayExist(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return this.keyMayExist(columnFamilyHandle, null, byteBuffer, byteBuffer2);
    }

    public KeyMayExist keyMayExist(ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        return this.keyMayExist(null, readOptions, byteBuffer, byteBuffer2);
    }

    public boolean keyMayExist(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, ByteBuffer byteBuffer) {
        assert (byteBuffer != null) : "key ByteBuffer parameter cannot be null";
        assert (byteBuffer.isDirect()) : "key parameter must be a direct ByteBuffer";
        boolean bl = RocksDB.keyMayExistDirect(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.limit());
        byteBuffer.position(byteBuffer.limit());
        return bl;
    }

    public KeyMayExist keyMayExist(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) {
        assert (byteBuffer != null) : "key ByteBuffer parameter cannot be null";
        assert (byteBuffer.isDirect()) : "key parameter must be a direct ByteBuffer";
        assert (byteBuffer2 != null) : "value ByteBuffer parameter cannot be null. If you do not need the value, use a different version of the method";
        assert (byteBuffer2.isDirect()) : "value parameter must be a direct ByteBuffer";
        int[] nArray = RocksDB.keyMayExistDirectFoundValue(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, readOptions == null ? 0L : readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining());
        int n = nArray[1];
        byteBuffer2.limit(byteBuffer2.position() + Math.min(n, byteBuffer2.remaining()));
        byteBuffer.position(byteBuffer.limit());
        return new KeyMayExist(KeyMayExist.KeyMayExistEnum.values()[nArray[0]], n);
    }

    public RocksIterator newIterator() {
        return new RocksIterator(this, RocksDB.iterator(this.nativeHandle_, this.defaultColumnFamilyHandle_.nativeHandle_, this.defaultReadOptions_.nativeHandle_));
    }

    public RocksIterator newIterator(ReadOptions readOptions) {
        return new RocksIterator(this, RocksDB.iterator(this.nativeHandle_, this.defaultColumnFamilyHandle_.nativeHandle_, readOptions.nativeHandle_));
    }

    public RocksIterator newIterator(ColumnFamilyHandle columnFamilyHandle) {
        return new RocksIterator(this, RocksDB.iterator(this.nativeHandle_, columnFamilyHandle.nativeHandle_, this.defaultReadOptions_.nativeHandle_));
    }

    public RocksIterator newIterator(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions) {
        return new RocksIterator(this, RocksDB.iterator(this.nativeHandle_, columnFamilyHandle.nativeHandle_, readOptions.nativeHandle_));
    }

    public List<RocksIterator> newIterators(List<ColumnFamilyHandle> list) throws RocksDBException {
        return this.newIterators(list, new ReadOptions());
    }

    public List<RocksIterator> newIterators(List<ColumnFamilyHandle> list, ReadOptions readOptions) throws RocksDBException {
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        long[] lArray2 = RocksDB.iterators(this.nativeHandle_, lArray, readOptions.nativeHandle_);
        ArrayList<RocksIterator> arrayList = new ArrayList<RocksIterator>(list.size());
        for (int i = 0; i < list.size(); ++i) {
            arrayList.add(new RocksIterator(this, lArray2[i]));
        }
        return arrayList;
    }

    public Snapshot getSnapshot() {
        long l = RocksDB.getSnapshot(this.nativeHandle_);
        if (l != 0L) {
            return new Snapshot(l);
        }
        return null;
    }

    public void releaseSnapshot(Snapshot snapshot) {
        if (snapshot != null) {
            RocksDB.releaseSnapshot(this.nativeHandle_, snapshot.nativeHandle_);
        }
    }

    public String getProperty(ColumnFamilyHandle columnFamilyHandle, String string) throws RocksDBException {
        return RocksDB.getProperty(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, string, string.length());
    }

    public String getProperty(String string) throws RocksDBException {
        return this.getProperty(null, string);
    }

    public Map<String, String> getMapProperty(String string) throws RocksDBException {
        return this.getMapProperty(null, string);
    }

    public Map<String, String> getMapProperty(ColumnFamilyHandle columnFamilyHandle, String string) throws RocksDBException {
        return RocksDB.getMapProperty(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, string, string.length());
    }

    public long getLongProperty(String string) throws RocksDBException {
        return this.getLongProperty(null, string);
    }

    public long getLongProperty(ColumnFamilyHandle columnFamilyHandle, String string) throws RocksDBException {
        return RocksDB.getLongProperty(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, string, string.length());
    }

    public void resetStats() throws RocksDBException {
        RocksDB.resetStats(this.nativeHandle_);
    }

    public long getAggregatedLongProperty(String string) throws RocksDBException {
        return RocksDB.getAggregatedLongProperty(this.nativeHandle_, string, string.length());
    }

    public long[] getApproximateSizes(ColumnFamilyHandle columnFamilyHandle, List<Range> list, SizeApproximationFlag ... sizeApproximationFlagArray) {
        byte by = 0;
        for (SizeApproximationFlag sizeApproximationFlag : sizeApproximationFlagArray) {
            by = (byte)(by | sizeApproximationFlag.getValue());
        }
        return RocksDB.getApproximateSizes(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, RocksDB.toRangeSliceHandles(list), by);
    }

    public long[] getApproximateSizes(List<Range> list, SizeApproximationFlag ... sizeApproximationFlagArray) {
        return this.getApproximateSizes(null, list, sizeApproximationFlagArray);
    }

    public CountAndSize getApproximateMemTableStats(ColumnFamilyHandle columnFamilyHandle, Range range) {
        long[] lArray = RocksDB.getApproximateMemTableStats(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, range.start.getNativeHandle(), range.limit.getNativeHandle());
        return new CountAndSize(lArray[0], lArray[1]);
    }

    public CountAndSize getApproximateMemTableStats(Range range) {
        return this.getApproximateMemTableStats(null, range);
    }

    public void compactRange() throws RocksDBException {
        this.compactRange(null);
    }

    public void compactRange(ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        RocksDB.compactRange(this.nativeHandle_, null, -1, null, -1, 0L, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public void compactRange(byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.compactRange(null, byArray, byArray2);
    }

    public void compactRange(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.compactRange(this.nativeHandle_, byArray, byArray == null ? -1 : byArray.length, byArray2, byArray2 == null ? -1 : byArray2.length, 0L, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public void compactRange(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2, CompactRangeOptions compactRangeOptions) throws RocksDBException {
        RocksDB.compactRange(this.nativeHandle_, byArray, byArray == null ? -1 : byArray.length, byArray2, byArray2 == null ? -1 : byArray2.length, compactRangeOptions.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public void clipColumnFamily(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        RocksDB.clipColumnFamily(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void setOptions(ColumnFamilyHandle columnFamilyHandle, MutableColumnFamilyOptions mutableColumnFamilyOptions) throws RocksDBException {
        RocksDB.setOptions(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, mutableColumnFamilyOptions.getKeys(), mutableColumnFamilyOptions.getValues());
    }

    public void setPerfLevel(PerfLevel perfLevel) {
        if (perfLevel == PerfLevel.UNINITIALIZED) {
            throw new IllegalArgumentException("Unable to set UNINITIALIZED level");
        }
        if (perfLevel == PerfLevel.OUT_OF_BOUNDS) {
            throw new IllegalArgumentException("Unable to set OUT_OF_BOUNDS level");
        }
        RocksDB.setPerfLevel(perfLevel.getValue());
    }

    public PerfLevel getPerfLevel() {
        byte by = RocksDB.getPerfLevelNative();
        return PerfLevel.getPerfLevel(by);
    }

    public PerfContext getPerfContext() {
        long l = RocksDB.getPerfContextNative();
        return new PerfContext(l);
    }

    public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder getOptions(ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        String string = RocksDB.getOptions(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
        return MutableColumnFamilyOptions.parse(string, true);
    }

    public MutableColumnFamilyOptions.MutableColumnFamilyOptionsBuilder getOptions() throws RocksDBException {
        return this.getOptions(null);
    }

    public MutableDBOptions.MutableDBOptionsBuilder getDBOptions() throws RocksDBException {
        String string = RocksDB.getDBOptions(this.nativeHandle_);
        return MutableDBOptions.parse(string, true);
    }

    public void setOptions(MutableColumnFamilyOptions mutableColumnFamilyOptions) throws RocksDBException {
        this.setOptions(null, mutableColumnFamilyOptions);
    }

    public void setDBOptions(MutableDBOptions mutableDBOptions) throws RocksDBException {
        RocksDB.setDBOptions(this.nativeHandle_, mutableDBOptions.getKeys(), mutableDBOptions.getValues());
    }

    public List<String> compactFiles(CompactionOptions compactionOptions, List<String> list, int n, int n2, CompactionJobInfo compactionJobInfo) throws RocksDBException {
        return this.compactFiles(compactionOptions, null, list, n, n2, compactionJobInfo);
    }

    public List<String> compactFiles(CompactionOptions compactionOptions, ColumnFamilyHandle columnFamilyHandle, List<String> list, int n, int n2, CompactionJobInfo compactionJobInfo) throws RocksDBException {
        return Arrays.asList(RocksDB.compactFiles(this.nativeHandle_, compactionOptions.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, list.toArray(new String[0]), n, n2, compactionJobInfo == null ? 0L : compactionJobInfo.nativeHandle_));
    }

    public void cancelAllBackgroundWork(boolean bl) {
        RocksDB.cancelAllBackgroundWork(this.nativeHandle_, bl);
    }

    public void pauseBackgroundWork() throws RocksDBException {
        RocksDB.pauseBackgroundWork(this.nativeHandle_);
    }

    public void continueBackgroundWork() throws RocksDBException {
        RocksDB.continueBackgroundWork(this.nativeHandle_);
    }

    public void enableAutoCompaction(List<ColumnFamilyHandle> list) throws RocksDBException {
        RocksDB.enableAutoCompaction(this.nativeHandle_, this.toNativeHandleList(list));
    }

    public int numberLevels() {
        return this.numberLevels(null);
    }

    public int numberLevels(ColumnFamilyHandle columnFamilyHandle) {
        return RocksDB.numberLevels(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    @Deprecated
    public int maxMemCompactionLevel() {
        return this.maxMemCompactionLevel(null);
    }

    public int maxMemCompactionLevel(ColumnFamilyHandle columnFamilyHandle) {
        return RocksDB.maxMemCompactionLevel(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public int level0StopWriteTrigger() {
        return this.level0StopWriteTrigger(null);
    }

    public int level0StopWriteTrigger(ColumnFamilyHandle columnFamilyHandle) {
        return RocksDB.level0StopWriteTrigger(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public String getName() {
        return RocksDB.getName(this.nativeHandle_);
    }

    public Env getEnv() {
        long l = RocksDB.getEnv(this.nativeHandle_);
        if (l == Env.getDefault().nativeHandle_) {
            return Env.getDefault();
        }
        RocksEnv rocksEnv = new RocksEnv(l);
        rocksEnv.disOwnNativeHandle();
        return rocksEnv;
    }

    public void flush(FlushOptions flushOptions) throws RocksDBException {
        this.flush(flushOptions, Collections.singletonList(this.getDefaultColumnFamily()));
    }

    public void flush(FlushOptions flushOptions, ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        this.flush(flushOptions, columnFamilyHandle == null ? null : Collections.singletonList(columnFamilyHandle));
    }

    public void flush(FlushOptions flushOptions, List<ColumnFamilyHandle> list) throws RocksDBException {
        RocksDB.flush(this.nativeHandle_, flushOptions.nativeHandle_, this.toNativeHandleList(list));
    }

    public void flushWal(boolean bl) throws RocksDBException {
        RocksDB.flushWal(this.nativeHandle_, bl);
    }

    public void syncWal() throws RocksDBException {
        RocksDB.syncWal(this.nativeHandle_);
    }

    public long getLatestSequenceNumber() {
        return RocksDB.getLatestSequenceNumber(this.nativeHandle_);
    }

    public void disableFileDeletions() throws RocksDBException {
        RocksDB.disableFileDeletions(this.nativeHandle_);
    }

    public void enableFileDeletions() throws RocksDBException {
        RocksDB.enableFileDeletions(this.nativeHandle_);
    }

    public LiveFiles getLiveFiles() throws RocksDBException {
        return this.getLiveFiles(true);
    }

    public LiveFiles getLiveFiles(boolean bl) throws RocksDBException {
        String[] stringArray = RocksDB.getLiveFiles(this.nativeHandle_, bl);
        if (stringArray == null) {
            return null;
        }
        String[] stringArray2 = Arrays.copyOf(stringArray, stringArray.length - 1);
        long l = Long.parseLong(stringArray[stringArray.length - 1]);
        return new LiveFiles(l, Arrays.asList(stringArray2));
    }

    public List<LogFile> getSortedWalFiles() throws RocksDBException {
        LogFile[] logFileArray = RocksDB.getSortedWalFiles(this.nativeHandle_);
        return Arrays.asList(logFileArray);
    }

    public TransactionLogIterator getUpdatesSince(long l) throws RocksDBException {
        return new TransactionLogIterator(RocksDB.getUpdatesSince(this.nativeHandle_, l));
    }

    public List<LiveFileMetaData> getLiveFilesMetaData() {
        return Arrays.asList(RocksDB.getLiveFilesMetaData(this.nativeHandle_));
    }

    public ColumnFamilyMetaData getColumnFamilyMetaData(ColumnFamilyHandle columnFamilyHandle) {
        return RocksDB.getColumnFamilyMetaData(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public ColumnFamilyMetaData getColumnFamilyMetaData() {
        return this.getColumnFamilyMetaData(null);
    }

    public void ingestExternalFile(List<String> list, IngestExternalFileOptions ingestExternalFileOptions) throws RocksDBException {
        RocksDB.ingestExternalFile(this.nativeHandle_, this.getDefaultColumnFamily().nativeHandle_, list.toArray(new String[0]), list.size(), ingestExternalFileOptions.nativeHandle_);
    }

    public void ingestExternalFile(ColumnFamilyHandle columnFamilyHandle, List<String> list, IngestExternalFileOptions ingestExternalFileOptions) throws RocksDBException {
        RocksDB.ingestExternalFile(this.nativeHandle_, columnFamilyHandle.nativeHandle_, list.toArray(new String[0]), list.size(), ingestExternalFileOptions.nativeHandle_);
    }

    public void verifyChecksum() throws RocksDBException {
        RocksDB.verifyChecksum(this.nativeHandle_);
    }

    public ColumnFamilyHandle getDefaultColumnFamily() {
        return this.defaultColumnFamilyHandle_;
    }

    protected ColumnFamilyHandle makeDefaultColumnFamilyHandle() {
        ColumnFamilyHandle columnFamilyHandle = new ColumnFamilyHandle(this, RocksDB.getDefaultColumnFamily(this.nativeHandle_));
        columnFamilyHandle.disOwnNativeHandle();
        return columnFamilyHandle;
    }

    public Map<String, TableProperties> getPropertiesOfAllTables(ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        return RocksDB.getPropertiesOfAllTables(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
    }

    public Map<String, TableProperties> getPropertiesOfAllTables() throws RocksDBException {
        return this.getPropertiesOfAllTables(null);
    }

    public Map<String, TableProperties> getPropertiesOfTablesInRange(ColumnFamilyHandle columnFamilyHandle, List<Range> list) throws RocksDBException {
        return RocksDB.getPropertiesOfTablesInRange(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, RocksDB.toRangeSliceHandles(list));
    }

    public Map<String, TableProperties> getPropertiesOfTablesInRange(List<Range> list) throws RocksDBException {
        return this.getPropertiesOfTablesInRange(null, list);
    }

    public Range suggestCompactRange(ColumnFamilyHandle columnFamilyHandle) throws RocksDBException {
        long[] lArray = RocksDB.suggestCompactRange(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_);
        return new Range(new Slice(lArray[0]), new Slice(lArray[1]));
    }

    public Range suggestCompactRange() throws RocksDBException {
        return this.suggestCompactRange(null);
    }

    @Deprecated
    public void promoteL0(ColumnFamilyHandle columnFamilyHandle, int n) throws RocksDBException {
        RocksDB.promoteL0(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, n);
    }

    @Deprecated
    public void promoteL0(int n) throws RocksDBException {
        this.promoteL0(null, n);
    }

    public void startTrace(TraceOptions traceOptions, AbstractTraceWriter abstractTraceWriter) throws RocksDBException {
        RocksDB.startTrace(this.nativeHandle_, traceOptions.getMaxTraceFileSize(), abstractTraceWriter.nativeHandle_);
        abstractTraceWriter.disOwnNativeHandle();
    }

    public void endTrace() throws RocksDBException {
        RocksDB.endTrace(this.nativeHandle_);
    }

    public void tryCatchUpWithPrimary() throws RocksDBException {
        RocksDB.tryCatchUpWithPrimary(this.nativeHandle_);
    }

    public void deleteFilesInRanges(ColumnFamilyHandle columnFamilyHandle, List<byte[]> list, boolean bl) throws RocksDBException {
        if (list.isEmpty()) {
            return;
        }
        if (list.size() % 2 != 0) {
            throw new IllegalArgumentException("Ranges size needs to be multiple of 2 (from1, to1, from2, to2, ...), but is " + list.size());
        }
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        RocksDB.deleteFilesInRanges(this.nativeHandle_, columnFamilyHandle == null ? 0L : columnFamilyHandle.nativeHandle_, byArray, bl);
    }

    public static void destroyDB(String string, Options options) throws RocksDBException {
        RocksDB.destroyDB(string, options.nativeHandle_);
    }

    private long[] toNativeHandleList(List<? extends RocksObject> list) {
        if (list == null) {
            return new long[0];
        }
        int n = list.size();
        long[] lArray = new long[n];
        for (int i = 0; i < n; ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return lArray;
    }

    private static long[] toRangeSliceHandles(List<Range> list) {
        long[] lArray = new long[list.size() * 2];
        int n = 0;
        for (int i = 0; i < list.size(); ++i) {
            Range range = list.get(i);
            lArray[n++] = range.start.getNativeHandle();
            lArray[n++] = range.limit.getNativeHandle();
        }
        return lArray;
    }

    protected void storeOptionsInstance(DBOptionsInterface<?> dBOptionsInterface) {
        this.options_ = dBOptionsInterface;
    }

    protected void storeDefaultColumnFamilyHandle(ColumnFamilyHandle columnFamilyHandle) {
        this.defaultColumnFamilyHandle_ = columnFamilyHandle;
    }

    private static void checkBounds(int n, int n2, int n3) {
        if ((n | n2 | n + n2 | n3 - (n + n2)) < 0) {
            throw new IndexOutOfBoundsException(String.format("offset(%d), len(%d), size(%d)", n, n2, n3));
        }
    }

    private static native long open(long var0, String var2) throws RocksDBException;

    private static native long[] open(long var0, String var2, byte[][] var3, long[] var4) throws RocksDBException;

    private static native long openROnly(long var0, String var2, boolean var3) throws RocksDBException;

    private static native long[] openROnly(long var0, String var2, byte[][] var3, long[] var4, boolean var5) throws RocksDBException;

    private static native long openAsSecondary(long var0, String var2, String var3) throws RocksDBException;

    private static native long[] openAsSecondary(long var0, String var2, String var3, byte[][] var4, long[] var5) throws RocksDBException;

    @Override
    protected void disposeInternal(long l) {
        RocksDB.disposeInternalJni(l);
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

    private static native void multiGet(long var0, long var2, long[] var4, ByteBuffer[] var5, int[] var6, int[] var7, ByteBuffer[] var8, int[] var9, Status[] var10);

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

    private static native int[] keyMayExistDirectFoundValue(long var0, long var2, long var4, ByteBuffer var6, int var7, int var8, ByteBuffer var9, int var10, int var11);

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

    public static class Version {
        private final byte major;
        private final byte minor;
        private final byte patch;

        public Version(byte by, byte by2, byte by3) {
            this.major = by;
            this.minor = by2;
            this.patch = by3;
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

        public String toString() {
            return this.getMajor() + "." + this.getMinor() + "." + this.getPatch();
        }

        private static Version fromEncodedVersion(int n) {
            byte by = (byte)(n & 0xFF);
            byte by2 = (byte)(n >> 8 & 0xFF);
            byte by3 = (byte)(n >> 16 & 0xFF);
            return new Version(by3, by2, by);
        }
    }

    public static class LiveFiles {
        public final long manifestFileSize;
        public final List<String> files;

        LiveFiles(long l, List<String> list) {
            this.manifestFileSize = l;
            this.files = list;
        }
    }

    public static class CountAndSize {
        public final long count;
        public final long size;

        public CountAndSize(long l, long l2) {
            this.count = l;
            this.size = l2;
        }
    }

    private static enum LibraryState {
        NOT_LOADED,
        LOADING,
        LOADED;

    }
}

