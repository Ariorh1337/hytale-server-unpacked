/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.AbstractImmutableNativeReference;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIteratorInterface;
import org.rocksdb.RocksObject;
import org.rocksdb.Snapshot;

public abstract class AbstractRocksIterator<P extends RocksObject>
extends RocksObject
implements RocksIteratorInterface {
    final P parent_;

    protected AbstractRocksIterator(P p, long l) {
        super(l);
        assert (p != null);
        this.parent_ = p;
    }

    @Override
    public boolean isValid() {
        assert (this.isOwningHandle());
        return this.isValid0(this.nativeHandle_);
    }

    @Override
    public void seekToFirst() {
        assert (this.isOwningHandle());
        this.seekToFirst0(this.nativeHandle_);
    }

    @Override
    public void seekToLast() {
        assert (this.isOwningHandle());
        this.seekToLast0(this.nativeHandle_);
    }

    @Override
    public void seek(byte[] byArray) {
        assert (this.isOwningHandle());
        this.seek0(this.nativeHandle_, byArray, byArray.length);
    }

    @Override
    public void seekForPrev(byte[] byArray) {
        assert (this.isOwningHandle());
        this.seekForPrev0(this.nativeHandle_, byArray, byArray.length);
    }

    @Override
    public void seek(ByteBuffer byteBuffer) {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect()) {
            this.seekDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining());
        } else {
            this.seekByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        }
        byteBuffer.position(byteBuffer.limit());
    }

    @Override
    public void seekForPrev(ByteBuffer byteBuffer) {
        assert (this.isOwningHandle());
        if (byteBuffer.isDirect()) {
            this.seekForPrevDirect0(this.nativeHandle_, byteBuffer, byteBuffer.position(), byteBuffer.remaining());
        } else {
            this.seekForPrevByteArray0(this.nativeHandle_, byteBuffer.array(), byteBuffer.arrayOffset() + byteBuffer.position(), byteBuffer.remaining());
        }
        byteBuffer.position(byteBuffer.limit());
    }

    @Override
    public void next() {
        assert (this.isOwningHandle());
        this.next0(this.nativeHandle_);
    }

    @Override
    public void prev() {
        assert (this.isOwningHandle());
        this.prev0(this.nativeHandle_);
    }

    @Override
    public void refresh() throws RocksDBException {
        assert (this.isOwningHandle());
        this.refresh0(this.nativeHandle_);
    }

    @Override
    public void refresh(Snapshot snapshot) throws RocksDBException {
        assert (this.isOwningHandle());
        this.refresh1(this.nativeHandle_, snapshot.getNativeHandle());
    }

    @Override
    public void status() throws RocksDBException {
        assert (this.isOwningHandle());
        this.status0(this.nativeHandle_);
    }

    @Override
    protected void disposeInternal() {
        if (((AbstractImmutableNativeReference)this.parent_).isOwningHandle()) {
            this.disposeInternal(this.nativeHandle_);
        }
    }

    abstract boolean isValid0(long var1);

    abstract void seekToFirst0(long var1);

    abstract void seekToLast0(long var1);

    abstract void next0(long var1);

    abstract void prev0(long var1);

    abstract void refresh0(long var1) throws RocksDBException;

    abstract void refresh1(long var1, long var3) throws RocksDBException;

    abstract void seek0(long var1, byte[] var3, int var4);

    abstract void seekForPrev0(long var1, byte[] var3, int var4);

    abstract void seekDirect0(long var1, ByteBuffer var3, int var4, int var5);

    abstract void seekForPrevDirect0(long var1, ByteBuffer var3, int var4, int var5);

    abstract void seekByteArray0(long var1, byte[] var3, int var4, int var5);

    abstract void seekForPrevByteArray0(long var1, byte[] var3, int var4, int var5);

    abstract void status0(long var1) throws RocksDBException;
}

