/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.rocksdb.AbstractTransactionNotifier;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.GetStatus;
import org.rocksdb.ReadOptions;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;
import org.rocksdb.RocksObject;
import org.rocksdb.Snapshot;
import org.rocksdb.Status;
import org.rocksdb.WriteBatch;
import org.rocksdb.WriteBatchWithIndex;
import org.rocksdb.WriteOptions;

public class Transaction
extends RocksObject {
    private static final String FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE = "For each key there must be a ColumnFamilyHandle.";
    private static final String BB_ALL_DIRECT_OR_INDIRECT = "ByteBuffer parameters must all be direct, or must all be indirect";
    private final RocksDB parent;
    private final ColumnFamilyHandle defaultColumnFamilyHandle;

    Transaction(RocksDB rocksDB, long l) {
        super(l);
        this.parent = rocksDB;
        this.defaultColumnFamilyHandle = rocksDB.getDefaultColumnFamily();
    }

    public void setSnapshot() {
        assert (this.isOwningHandle());
        Transaction.setSnapshot(this.nativeHandle_);
    }

    public void setSnapshotOnNextOperation() {
        assert (this.isOwningHandle());
        Transaction.setSnapshotOnNextOperation(this.nativeHandle_);
    }

    public void setSnapshotOnNextOperation(AbstractTransactionNotifier abstractTransactionNotifier) {
        assert (this.isOwningHandle());
        Transaction.setSnapshotOnNextOperation(this.nativeHandle_, abstractTransactionNotifier.nativeHandle_);
    }

    public Snapshot getSnapshot() {
        assert (this.isOwningHandle());
        long l = Transaction.getSnapshot(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new Snapshot(l);
    }

    public void clearSnapshot() {
        assert (this.isOwningHandle());
        Transaction.clearSnapshot(this.nativeHandle_);
    }

    public void prepare() throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.prepare(this.nativeHandle_);
    }

    public void commit() throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.commit(this.nativeHandle_);
    }

    public void rollback() throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.rollback(this.nativeHandle_);
    }

    public void setSavePoint() throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.setSavePoint(this.nativeHandle_);
    }

    public void rollbackToSavePoint() throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.rollbackToSavePoint(this.nativeHandle_);
    }

    @Deprecated
    public byte[] get(ColumnFamilyHandle columnFamilyHandle, ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public byte[] get(ReadOptions readOptions, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, this.defaultColumnFamilyHandle.nativeHandle_);
    }

    public GetStatus get(ReadOptions readOptions, byte[] byArray, byte[] byArray2) throws RocksDBException {
        int n = Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, this.defaultColumnFamilyHandle.nativeHandle_);
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    public GetStatus get(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        int n = Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    public GetStatus get(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        int n;
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            n = Transaction.getDirect(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            n = Transaction.get(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        byteBuffer2.position(Math.min(byteBuffer2.limit(), byteBuffer2.position() + n));
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    public GetStatus get(ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        return this.get(readOptions, this.defaultColumnFamilyHandle, byteBuffer, byteBuffer2);
    }

    @Deprecated
    public byte[][] multiGet(ReadOptions readOptions, List<ColumnFamilyHandle> list, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byArray.length != list.size()) {
            throw new IllegalArgumentException(FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE);
        }
        if (byArray.length == 0) {
            return new byte[0][0];
        }
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return Transaction.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray, lArray);
    }

    public List<byte[]> multiGetAsList(ReadOptions readOptions, List<ColumnFamilyHandle> list, List<byte[]> list2) throws RocksDBException {
        assert (this.isOwningHandle());
        if (list2.size() != list.size()) {
            throw new IllegalArgumentException(FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE);
        }
        if (list2.isEmpty()) {
            return new ArrayList<byte[]>(0);
        }
        byte[][] byArray = (byte[][])list2.toArray((T[])new byte[list2.size()][]);
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return Arrays.asList(Transaction.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray, lArray));
    }

    @Deprecated
    public byte[][] multiGet(ReadOptions readOptions, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byArray.length == 0) {
            return new byte[0][0];
        }
        return Transaction.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray);
    }

    public List<byte[]> multiGetAsList(ReadOptions readOptions, List<byte[]> list) throws RocksDBException {
        if (list.isEmpty()) {
            return new ArrayList<byte[]>(0);
        }
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        return Arrays.asList(Transaction.multiGet(this.nativeHandle_, readOptions.nativeHandle_, byArray));
    }

    public List<byte[]> multiGetAsList(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, List<byte[]> list) throws RocksDBException {
        if (list.isEmpty()) {
            return new ArrayList<byte[]>(0);
        }
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        return Arrays.asList(Transaction.multiGet(this.nativeHandle_, readOptions.nativeHandle_, columnFamilyHandle.nativeHandle_, byArray));
    }

    public byte[] getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray, boolean bl, boolean bl2) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_, bl, bl2);
    }

    public byte[] getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, columnFamilyHandle.nativeHandle_, bl, true);
    }

    public byte[] getForUpdate(ReadOptions readOptions, byte[] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        return Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, this.defaultColumnFamilyHandle.nativeHandle_, bl, true);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, byte[] byArray, byte[] byArray2, boolean bl) throws RocksDBException {
        int n = Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, this.defaultColumnFamilyHandle.nativeHandle_, bl, true);
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, boolean bl) throws RocksDBException {
        return this.getForUpdate(readOptions, this.defaultColumnFamilyHandle, byteBuffer, byteBuffer2, bl, true);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2, boolean bl) throws RocksDBException {
        return this.getForUpdate(readOptions, columnFamilyHandle, byArray, byArray2, bl, true);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2, boolean bl, boolean bl2) throws RocksDBException {
        int n = Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_, bl, bl2);
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, boolean bl) throws RocksDBException {
        return this.getForUpdate(readOptions, columnFamilyHandle, byteBuffer, byteBuffer2, bl, true);
    }

    public GetStatus getForUpdate(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, boolean bl, boolean bl2) throws RocksDBException {
        int n;
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            n = Transaction.getDirectForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl, bl2);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            n = Transaction.getForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl, bl2);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        if (n < 0) {
            return GetStatus.fromStatusCode(Status.Code.NotFound, 0);
        }
        byteBuffer2.position(Math.min(byteBuffer2.limit(), byteBuffer2.position() + n));
        return GetStatus.fromStatusCode(Status.Code.Ok, n);
    }

    @Deprecated
    public byte[][] multiGetForUpdate(ReadOptions readOptions, List<ColumnFamilyHandle> list, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byArray.length != list.size()) {
            throw new IllegalArgumentException(FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE);
        }
        if (byArray.length == 0) {
            return new byte[0][0];
        }
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return Transaction.multiGetForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, lArray);
    }

    public List<byte[]> multiGetForUpdateAsList(ReadOptions readOptions, List<ColumnFamilyHandle> list, List<byte[]> list2) throws RocksDBException {
        assert (this.isOwningHandle());
        if (list2.size() != list.size()) {
            throw new IllegalArgumentException(FOR_EACH_KEY_THERE_MUST_BE_A_COLUMNFAMILYHANDLE);
        }
        if (list2.isEmpty()) {
            return new ArrayList<byte[]>();
        }
        byte[][] byArray = (byte[][])list2.toArray((T[])new byte[list2.size()][]);
        long[] lArray = new long[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            lArray[i] = list.get((int)i).nativeHandle_;
        }
        return Arrays.asList(Transaction.multiGetForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray, lArray));
    }

    @Deprecated
    public byte[][] multiGetForUpdate(ReadOptions readOptions, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byArray.length == 0) {
            return new byte[0][0];
        }
        return Transaction.multiGetForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray);
    }

    public List<byte[]> multiGetForUpdateAsList(ReadOptions readOptions, List<byte[]> list) throws RocksDBException {
        assert (this.isOwningHandle());
        if (list.isEmpty()) {
            return new ArrayList<byte[]>(0);
        }
        byte[][] byArray = (byte[][])list.toArray((T[])new byte[list.size()][]);
        return Arrays.asList(Transaction.multiGetForUpdate(this.nativeHandle_, readOptions.nativeHandle_, byArray));
    }

    public RocksIterator getIterator() {
        assert (this.isOwningHandle());
        try (ReadOptions readOptions = new ReadOptions();){
            RocksIterator rocksIterator = new RocksIterator(this.parent, Transaction.getIterator(this.nativeHandle_, readOptions.nativeHandle_, this.defaultColumnFamilyHandle.nativeHandle_));
            return rocksIterator;
        }
    }

    public RocksIterator getIterator(ReadOptions readOptions) {
        assert (this.isOwningHandle());
        return new RocksIterator(this.parent, Transaction.getIterator(this.nativeHandle_, readOptions.nativeHandle_, this.defaultColumnFamilyHandle.nativeHandle_));
    }

    public RocksIterator getIterator(ReadOptions readOptions, ColumnFamilyHandle columnFamilyHandle) {
        assert (this.isOwningHandle());
        return new RocksIterator(this.parent, Transaction.getIterator(this.nativeHandle_, readOptions.nativeHandle_, columnFamilyHandle.nativeHandle_));
    }

    public RocksIterator getIterator(ColumnFamilyHandle columnFamilyHandle) {
        assert (this.isOwningHandle());
        try (ReadOptions readOptions = new ReadOptions();){
            RocksIterator rocksIterator = new RocksIterator(this.parent, Transaction.getIterator(this.nativeHandle_, readOptions.nativeHandle_, columnFamilyHandle.nativeHandle_));
            return rocksIterator;
        }
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void put(byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray, byte[][] byArray2, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray, byte[][] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void put(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            Transaction.putDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining());
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            Transaction.put(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining());
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            Transaction.putDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            Transaction.put(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void put(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        this.put(columnFamilyHandle, byteBuffer, byteBuffer2, false);
    }

    public void put(byte[][] byArray, byte[][] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.put(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.merge(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.merge(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void merge(byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.merge(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length);
    }

    public void merge(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            Transaction.mergeDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining());
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            Transaction.merge(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining());
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            Transaction.mergeDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            Transaction.merge(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_, bl);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void merge(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        this.merge(columnFamilyHandle, byteBuffer, byteBuffer2, false);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void delete(byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void delete(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void delete(byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.delete(this.nativeHandle_, byArray, byArray.length);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void singleDelete(byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray, boolean bl) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, bl);
    }

    public void singleDelete(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_, false);
    }

    public void singleDelete(byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.singleDelete(this.nativeHandle_, byArray, byArray.length);
    }

    public void putUntracked(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.putUntracked(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void putUntracked(byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.putUntracked(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    public void putUntracked(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray, byte[][] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.putUntracked(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void putUntracked(byte[][] byArray, byte[][] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.putUntracked(this.nativeHandle_, byArray, byArray.length, byArray2, byArray2.length);
    }

    public void mergeUntracked(ColumnFamilyHandle columnFamilyHandle, byte[] byArray, byte[] byArray2) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.mergeUntracked(this.nativeHandle_, byArray, 0, byArray.length, byArray2, 0, byArray2.length, columnFamilyHandle.nativeHandle_);
    }

    public void mergeUntracked(ColumnFamilyHandle columnFamilyHandle, ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect() && byteBuffer2.isDirect()) {
            Transaction.mergeUntrackedDirect(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining(), byteBuffer2, byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else if (!byteBuffer.isDirect() && !byteBuffer2.isDirect()) {
            assert (byteBuffer.hasArray());
            assert (byteBuffer2.hasArray());
            Transaction.mergeUntracked(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining(), byteBuffer2.array(), byteBuffer2.arrayOffset() + byteBuffer2.position(), byteBuffer2.remaining(), columnFamilyHandle.nativeHandle_);
        } else {
            throw new RocksDBException(BB_ALL_DIRECT_OR_INDIRECT);
        }
        byteBuffer.position(byteBuffer.limit());
        byteBuffer2.position(byteBuffer2.limit());
    }

    public void mergeUntracked(byte[] byArray, byte[] byArray2) throws RocksDBException {
        this.mergeUntracked(this.defaultColumnFamilyHandle, byArray, byArray2);
    }

    public void mergeUntracked(ByteBuffer byteBuffer, ByteBuffer byteBuffer2) throws RocksDBException {
        this.mergeUntracked(this.defaultColumnFamilyHandle, byteBuffer, byteBuffer2);
    }

    public void deleteUntracked(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.deleteUntracked(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void deleteUntracked(byte[] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.deleteUntracked(this.nativeHandle_, byArray, byArray.length);
    }

    public void deleteUntracked(ColumnFamilyHandle columnFamilyHandle, byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.deleteUntracked(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void deleteUntracked(byte[][] byArray) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.deleteUntracked(this.nativeHandle_, byArray, byArray.length);
    }

    public void putLogData(byte[] byArray) {
        assert (this.isOwningHandle());
        Transaction.putLogData(this.nativeHandle_, byArray, byArray.length);
    }

    public void disableIndexing() {
        assert (this.isOwningHandle());
        Transaction.disableIndexing(this.nativeHandle_);
    }

    public void enableIndexing() {
        assert (this.isOwningHandle());
        Transaction.enableIndexing(this.nativeHandle_);
    }

    public long getNumKeys() {
        assert (this.isOwningHandle());
        return Transaction.getNumKeys(this.nativeHandle_);
    }

    public long getNumPuts() {
        assert (this.isOwningHandle());
        return Transaction.getNumPuts(this.nativeHandle_);
    }

    public long getNumDeletes() {
        assert (this.isOwningHandle());
        return Transaction.getNumDeletes(this.nativeHandle_);
    }

    public long getNumMerges() {
        assert (this.isOwningHandle());
        return Transaction.getNumMerges(this.nativeHandle_);
    }

    public long getElapsedTime() {
        assert (this.isOwningHandle());
        return Transaction.getElapsedTime(this.nativeHandle_);
    }

    public WriteBatchWithIndex getWriteBatch() {
        assert (this.isOwningHandle());
        return new WriteBatchWithIndex(Transaction.getWriteBatch(this.nativeHandle_));
    }

    public void setLockTimeout(long l) {
        assert (this.isOwningHandle());
        Transaction.setLockTimeout(this.nativeHandle_, l);
    }

    public WriteOptions getWriteOptions() {
        assert (this.isOwningHandle());
        return new WriteOptions(Transaction.getWriteOptions(this.nativeHandle_));
    }

    public void setWriteOptions(WriteOptions writeOptions) {
        assert (this.isOwningHandle());
        Transaction.setWriteOptions(this.nativeHandle_, writeOptions.nativeHandle_);
    }

    public void undoGetForUpdate(ColumnFamilyHandle columnFamilyHandle, byte[] byArray) {
        assert (this.isOwningHandle());
        Transaction.undoGetForUpdate(this.nativeHandle_, byArray, byArray.length, columnFamilyHandle.nativeHandle_);
    }

    public void undoGetForUpdate(byte[] byArray) {
        assert (this.isOwningHandle());
        Transaction.undoGetForUpdate(this.nativeHandle_, byArray, byArray.length);
    }

    public void rebuildFromWriteBatch(WriteBatch writeBatch) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.rebuildFromWriteBatch(this.nativeHandle_, writeBatch.nativeHandle_);
    }

    public WriteBatch getCommitTimeWriteBatch() {
        assert (this.isOwningHandle());
        return new WriteBatch(Transaction.getCommitTimeWriteBatch(this.nativeHandle_));
    }

    public void setLogNumber(long l) {
        assert (this.isOwningHandle());
        Transaction.setLogNumber(this.nativeHandle_, l);
    }

    public long getLogNumber() {
        assert (this.isOwningHandle());
        return Transaction.getLogNumber(this.nativeHandle_);
    }

    public void setName(String string) throws RocksDBException {
        assert (this.isOwningHandle());
        Transaction.setName(this.nativeHandle_, string);
    }

    public String getName() {
        assert (this.isOwningHandle());
        return Transaction.getName(this.nativeHandle_);
    }

    public long getID() {
        assert (this.isOwningHandle());
        return Transaction.getID(this.nativeHandle_);
    }

    public boolean isDeadlockDetect() {
        assert (this.isOwningHandle());
        return Transaction.isDeadlockDetect(this.nativeHandle_);
    }

    public WaitingTransactions getWaitingTxns() {
        assert (this.isOwningHandle());
        return this.getWaitingTxns(this.nativeHandle_);
    }

    public TransactionState getState() {
        assert (this.isOwningHandle());
        return TransactionState.getTransactionState(Transaction.getState(this.nativeHandle_));
    }

    public long getId() {
        assert (this.isOwningHandle());
        return Transaction.getId(this.nativeHandle_);
    }

    private WaitingTransactions newWaitingTransactions(long l, String string, long[] lArray) {
        return new WaitingTransactions(l, string, lArray);
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

    private static native int getForUpdate(long var0, long var2, byte[] var4, int var5, int var6, byte[] var7, int var8, int var9, long var10, boolean var12, boolean var13) throws RocksDBException;

    private static native int getDirectForUpdate(long var0, long var2, ByteBuffer var4, int var5, int var6, ByteBuffer var7, int var8, int var9, long var10, boolean var12, boolean var13) throws RocksDBException;

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

    private native WaitingTransactions getWaitingTxns(long var1);

    private static native byte getState(long var0);

    private static native long getId(long var0);

    @Override
    protected final native void disposeInternal(long var1);

    public static class WaitingTransactions {
        private final long columnFamilyId;
        private final String key;
        private final long[] transactionIds;

        private WaitingTransactions(long l, String string, long[] lArray) {
            this.columnFamilyId = l;
            this.key = string;
            this.transactionIds = lArray;
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

    public static enum TransactionState {
        STARTED(0),
        AWAITING_PREPARE(1),
        PREPARED(2),
        AWAITING_COMMIT(3),
        COMMITTED(4),
        AWAITING_ROLLBACK(5),
        ROLLEDBACK(6),
        LOCKS_STOLEN(7);

        public static final TransactionState COMMITED;
        private final byte value;

        private TransactionState(byte by) {
            this.value = by;
        }

        public static TransactionState getTransactionState(byte by) {
            for (TransactionState transactionState : TransactionState.values()) {
                if (transactionState.value != by) continue;
                return transactionState;
            }
            throw new IllegalArgumentException("Illegal value provided for TransactionState.");
        }

        static {
            COMMITED = COMMITTED;
        }
    }
}

