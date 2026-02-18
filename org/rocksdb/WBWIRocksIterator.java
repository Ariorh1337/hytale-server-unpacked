/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractRocksIterator;
import org.rocksdb.DirectSlice;
import org.rocksdb.RocksDBException;
import org.rocksdb.WriteBatchWithIndex;

public class WBWIRocksIterator
extends AbstractRocksIterator<WriteBatchWithIndex> {
    private final WriteEntry entry = new WriteEntry();

    protected WBWIRocksIterator(WriteBatchWithIndex writeBatchWithIndex, long l) {
        super(writeBatchWithIndex, l);
    }

    public WriteEntry entry() {
        assert (this.isOwningHandle());
        long[] lArray = WBWIRocksIterator.entry1(this.nativeHandle_);
        this.entry.type = WriteType.fromId((byte)lArray[0]);
        this.entry.key.resetNativeHandle(lArray[1], lArray[1] != 0L);
        this.entry.value.resetNativeHandle(lArray[2], lArray[2] != 0L);
        return this.entry;
    }

    @Override
    final native void refresh1(long var1, long var3);

    @Override
    protected final void disposeInternal(long l) {
        WBWIRocksIterator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    @Override
    final boolean isValid0(long l) {
        return WBWIRocksIterator.isValid0Jni(l);
    }

    private static native boolean isValid0Jni(long var0);

    @Override
    final void seekToFirst0(long l) {
        WBWIRocksIterator.seekToFirst0Jni(l);
    }

    private static native void seekToFirst0Jni(long var0);

    @Override
    final void seekToLast0(long l) {
        WBWIRocksIterator.seekToLast0Jni(l);
    }

    private static native void seekToLast0Jni(long var0);

    @Override
    final void next0(long l) {
        WBWIRocksIterator.next0Jni(l);
    }

    private static native void next0Jni(long var0);

    @Override
    final void prev0(long l) {
        WBWIRocksIterator.prev0Jni(l);
    }

    private static native void prev0Jni(long var0);

    @Override
    final void refresh0(long l) throws RocksDBException {
        WBWIRocksIterator.refresh0Jni(l);
    }

    private static native void refresh0Jni(long var0) throws RocksDBException;

    @Override
    final void seek0(long l, byte[] byArray, int n) {
        WBWIRocksIterator.seek0Jni(l, byArray, n);
    }

    private static native void seek0Jni(long var0, byte[] var2, int var3);

    @Override
    final void seekForPrev0(long l, byte[] byArray, int n) {
        WBWIRocksIterator.seekForPrev0Jni(l, byArray, n);
    }

    private static native void seekForPrev0Jni(long var0, byte[] var2, int var3);

    @Override
    final void status0(long l) throws RocksDBException {
        WBWIRocksIterator.status0Jni(l);
    }

    private static native void status0Jni(long var0) throws RocksDBException;

    @Override
    final void seekDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        WBWIRocksIterator.seekDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekForPrevDirect0(long l, ByteBuffer byteBuffer, int n, int n2) {
        WBWIRocksIterator.seekForPrevDirect0Jni(l, byteBuffer, n, n2);
    }

    private static native void seekForPrevDirect0Jni(long var0, ByteBuffer var2, int var3, int var4);

    @Override
    final void seekByteArray0(long l, byte[] byArray, int n, int n2) {
        WBWIRocksIterator.seekByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    @Override
    final void seekForPrevByteArray0(long l, byte[] byArray, int n, int n2) {
        WBWIRocksIterator.seekForPrevByteArray0Jni(l, byArray, n, n2);
    }

    private static native void seekForPrevByteArray0Jni(long var0, byte[] var2, int var3, int var4);

    private static native long[] entry1(long var0);

    @Override
    public void close() {
        this.entry.close();
        super.close();
    }

    public static class WriteEntry
    implements AutoCloseable {
        WriteType type = null;
        final DirectSlice key;
        final DirectSlice value;

        private WriteEntry() {
            this.key = new DirectSlice();
            this.value = new DirectSlice();
        }

        public WriteEntry(WriteType writeType, DirectSlice directSlice, DirectSlice directSlice2) {
            this.type = writeType;
            this.key = directSlice;
            this.value = directSlice2;
        }

        public WriteType getType() {
            return this.type;
        }

        public DirectSlice getKey() {
            return this.key;
        }

        public DirectSlice getValue() {
            if (this.value.isOwningHandle()) {
                return this.value;
            }
            return null;
        }

        public int hashCode() {
            return this.key == null ? 0 : this.key.hashCode();
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (this == object) {
                return true;
            }
            if (object instanceof WriteEntry) {
                WriteEntry writeEntry = (WriteEntry)object;
                return this.type.equals((Object)writeEntry.type) && this.key.equals(writeEntry.key) && this.value.equals(writeEntry.value);
            }
            return false;
        }

        @Override
        public void close() {
            this.value.close();
            this.key.close();
        }
    }

    public static enum WriteType {
        PUT(0),
        MERGE(1),
        DELETE(2),
        SINGLE_DELETE(3),
        DELETE_RANGE(4),
        LOG(5),
        XID(6);

        final byte id;

        private WriteType(byte by) {
            this.id = by;
        }

        public static WriteType fromId(byte by) {
            for (WriteType writeType : WriteType.values()) {
                if (by != writeType.id) continue;
                return writeType;
            }
            throw new IllegalArgumentException("No WriteType with id=" + by);
        }
    }
}

