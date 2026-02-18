/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractSlice;
import org.rocksdb.AbstractTableFilter;
import org.rocksdb.ReadTier;
import org.rocksdb.RocksObject;
import org.rocksdb.Slice;
import org.rocksdb.Snapshot;

public class ReadOptions
extends RocksObject {
    private AbstractSlice<?> iterateLowerBoundSlice_;
    private AbstractSlice<?> iterateUpperBoundSlice_;
    private AbstractSlice<?> timestampSlice_;
    private AbstractSlice<?> iterStartTs_;

    public ReadOptions() {
        super(ReadOptions.newReadOptions());
    }

    public ReadOptions(boolean bl, boolean bl2) {
        super(ReadOptions.newReadOptions(bl, bl2));
    }

    public ReadOptions(ReadOptions readOptions) {
        super(ReadOptions.copyReadOptions(readOptions.nativeHandle_));
        this.iterateLowerBoundSlice_ = readOptions.iterateLowerBoundSlice_;
        this.iterateUpperBoundSlice_ = readOptions.iterateUpperBoundSlice_;
        this.timestampSlice_ = readOptions.timestampSlice_;
        this.iterStartTs_ = readOptions.iterStartTs_;
    }

    public boolean verifyChecksums() {
        assert (this.isOwningHandle());
        return ReadOptions.verifyChecksums(this.nativeHandle_);
    }

    public ReadOptions setVerifyChecksums(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setVerifyChecksums(this.nativeHandle_, bl);
        return this;
    }

    public boolean fillCache() {
        assert (this.isOwningHandle());
        return ReadOptions.fillCache(this.nativeHandle_);
    }

    public ReadOptions setFillCache(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setFillCache(this.nativeHandle_, bl);
        return this;
    }

    public Snapshot snapshot() {
        assert (this.isOwningHandle());
        long l = ReadOptions.snapshot(this.nativeHandle_);
        if (l != 0L) {
            return new Snapshot(l);
        }
        return null;
    }

    public ReadOptions setSnapshot(Snapshot snapshot) {
        assert (this.isOwningHandle());
        if (snapshot != null) {
            ReadOptions.setSnapshot(this.nativeHandle_, snapshot.nativeHandle_);
        } else {
            ReadOptions.setSnapshot(this.nativeHandle_, 0L);
        }
        return this;
    }

    public ReadTier readTier() {
        assert (this.isOwningHandle());
        return ReadTier.getReadTier(ReadOptions.readTier(this.nativeHandle_));
    }

    public ReadOptions setReadTier(ReadTier readTier) {
        assert (this.isOwningHandle());
        ReadOptions.setReadTier(this.nativeHandle_, readTier.getValue());
        return this;
    }

    public boolean tailing() {
        assert (this.isOwningHandle());
        return ReadOptions.tailing(this.nativeHandle_);
    }

    public ReadOptions setTailing(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setTailing(this.nativeHandle_, bl);
        return this;
    }

    @Deprecated
    public boolean managed() {
        assert (this.isOwningHandle());
        return ReadOptions.managed(this.nativeHandle_);
    }

    @Deprecated
    public ReadOptions setManaged(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setManaged(this.nativeHandle_, bl);
        return this;
    }

    public boolean totalOrderSeek() {
        assert (this.isOwningHandle());
        return ReadOptions.totalOrderSeek(this.nativeHandle_);
    }

    public ReadOptions setTotalOrderSeek(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setTotalOrderSeek(this.nativeHandle_, bl);
        return this;
    }

    public boolean prefixSameAsStart() {
        assert (this.isOwningHandle());
        return ReadOptions.prefixSameAsStart(this.nativeHandle_);
    }

    public ReadOptions setPrefixSameAsStart(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setPrefixSameAsStart(this.nativeHandle_, bl);
        return this;
    }

    public boolean pinData() {
        assert (this.isOwningHandle());
        return ReadOptions.pinData(this.nativeHandle_);
    }

    public ReadOptions setPinData(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setPinData(this.nativeHandle_, bl);
        return this;
    }

    public boolean backgroundPurgeOnIteratorCleanup() {
        assert (this.isOwningHandle());
        return ReadOptions.backgroundPurgeOnIteratorCleanup(this.nativeHandle_);
    }

    public ReadOptions setBackgroundPurgeOnIteratorCleanup(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setBackgroundPurgeOnIteratorCleanup(this.nativeHandle_, bl);
        return this;
    }

    public long readaheadSize() {
        assert (this.isOwningHandle());
        return ReadOptions.readaheadSize(this.nativeHandle_);
    }

    public ReadOptions setReadaheadSize(long l) {
        assert (this.isOwningHandle());
        ReadOptions.setReadaheadSize(this.nativeHandle_, l);
        return this;
    }

    public long maxSkippableInternalKeys() {
        assert (this.isOwningHandle());
        return ReadOptions.maxSkippableInternalKeys(this.nativeHandle_);
    }

    public ReadOptions setMaxSkippableInternalKeys(long l) {
        assert (this.isOwningHandle());
        ReadOptions.setMaxSkippableInternalKeys(this.nativeHandle_, l);
        return this;
    }

    @Deprecated
    public boolean ignoreRangeDeletions() {
        assert (this.isOwningHandle());
        return ReadOptions.ignoreRangeDeletions(this.nativeHandle_);
    }

    @Deprecated
    public ReadOptions setIgnoreRangeDeletions(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setIgnoreRangeDeletions(this.nativeHandle_, bl);
        return this;
    }

    public ReadOptions setIterateLowerBound(AbstractSlice<?> abstractSlice) {
        assert (this.isOwningHandle());
        ReadOptions.setIterateLowerBound(this.nativeHandle_, abstractSlice == null ? 0L : abstractSlice.getNativeHandle());
        this.iterateLowerBoundSlice_ = abstractSlice;
        return this;
    }

    public Slice iterateLowerBound() {
        assert (this.isOwningHandle());
        long l = ReadOptions.iterateLowerBound(this.nativeHandle_);
        if (l != 0L) {
            return new Slice(l, false);
        }
        return null;
    }

    public ReadOptions setIterateUpperBound(AbstractSlice<?> abstractSlice) {
        assert (this.isOwningHandle());
        ReadOptions.setIterateUpperBound(this.nativeHandle_, abstractSlice == null ? 0L : abstractSlice.getNativeHandle());
        this.iterateUpperBoundSlice_ = abstractSlice;
        return this;
    }

    public Slice iterateUpperBound() {
        assert (this.isOwningHandle());
        long l = ReadOptions.iterateUpperBound(this.nativeHandle_);
        if (l != 0L) {
            return new Slice(l, false);
        }
        return null;
    }

    public ReadOptions setTableFilter(AbstractTableFilter abstractTableFilter) {
        assert (this.isOwningHandle());
        ReadOptions.setTableFilter(this.nativeHandle_, abstractTableFilter.nativeHandle_);
        return this;
    }

    public boolean autoPrefixMode() {
        assert (this.isOwningHandle());
        return ReadOptions.autoPrefixMode(this.nativeHandle_);
    }

    public ReadOptions setAutoPrefixMode(boolean bl) {
        assert (this.isOwningHandle());
        ReadOptions.setAutoPrefixMode(this.nativeHandle_, bl);
        return this;
    }

    public Slice timestamp() {
        assert (this.isOwningHandle());
        long l = ReadOptions.timestamp(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new Slice(l);
    }

    public ReadOptions setTimestamp(AbstractSlice<?> abstractSlice) {
        assert (this.isOwningHandle());
        ReadOptions.setTimestamp(this.nativeHandle_, abstractSlice == null ? 0L : abstractSlice.getNativeHandle());
        this.timestampSlice_ = abstractSlice;
        return this;
    }

    public Slice iterStartTs() {
        assert (this.isOwningHandle());
        long l = ReadOptions.iterStartTs(this.nativeHandle_);
        if (l == 0L) {
            return null;
        }
        return new Slice(l);
    }

    public ReadOptions setIterStartTs(AbstractSlice<?> abstractSlice) {
        assert (this.isOwningHandle());
        ReadOptions.setIterStartTs(this.nativeHandle_, abstractSlice == null ? 0L : abstractSlice.getNativeHandle());
        this.iterStartTs_ = abstractSlice;
        return this;
    }

    public long deadline() {
        assert (this.isOwningHandle());
        return ReadOptions.deadline(this.nativeHandle_);
    }

    public ReadOptions setDeadline(long l) {
        assert (this.isOwningHandle());
        ReadOptions.setDeadline(this.nativeHandle_, l);
        return this;
    }

    public long ioTimeout() {
        assert (this.isOwningHandle());
        return ReadOptions.ioTimeout(this.nativeHandle_);
    }

    public ReadOptions setIoTimeout(long l) {
        assert (this.isOwningHandle());
        ReadOptions.setIoTimeout(this.nativeHandle_, l);
        return this;
    }

    public long valueSizeSoftLimit() {
        assert (this.isOwningHandle());
        return ReadOptions.valueSizeSoftLimit(this.nativeHandle_);
    }

    public ReadOptions setValueSizeSoftLimit(long l) {
        assert (this.isOwningHandle());
        ReadOptions.setValueSizeSoftLimit(this.nativeHandle_, l);
        return this;
    }

    public boolean asyncIo() {
        assert (this.isOwningHandle());
        return this.asyncIo(this.nativeHandle_);
    }

    public ReadOptions setAsyncIo(boolean bl) {
        assert (this.isOwningHandle());
        this.setAsyncIo(this.nativeHandle_, bl);
        return this;
    }

    private static native long newReadOptions();

    private static native long newReadOptions(boolean var0, boolean var1);

    private static native long copyReadOptions(long var0);

    @Override
    protected final void disposeInternal(long l) {
        ReadOptions.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private native boolean asyncIo(long var1);

    private native void setAsyncIo(long var1, boolean var3);

    private static native boolean verifyChecksums(long var0);

    private static native void setVerifyChecksums(long var0, boolean var2);

    private static native boolean fillCache(long var0);

    private static native void setFillCache(long var0, boolean var2);

    private static native long snapshot(long var0);

    private static native void setSnapshot(long var0, long var2);

    private static native byte readTier(long var0);

    private static native void setReadTier(long var0, byte var2);

    private static native boolean tailing(long var0);

    private static native void setTailing(long var0, boolean var2);

    private static native boolean managed(long var0);

    private static native void setManaged(long var0, boolean var2);

    private static native boolean totalOrderSeek(long var0);

    private static native void setTotalOrderSeek(long var0, boolean var2);

    private static native boolean prefixSameAsStart(long var0);

    private static native void setPrefixSameAsStart(long var0, boolean var2);

    private static native boolean pinData(long var0);

    private static native void setPinData(long var0, boolean var2);

    private static native boolean backgroundPurgeOnIteratorCleanup(long var0);

    private static native void setBackgroundPurgeOnIteratorCleanup(long var0, boolean var2);

    private static native long readaheadSize(long var0);

    private static native void setReadaheadSize(long var0, long var2);

    private static native long maxSkippableInternalKeys(long var0);

    private static native void setMaxSkippableInternalKeys(long var0, long var2);

    private static native boolean ignoreRangeDeletions(long var0);

    private static native void setIgnoreRangeDeletions(long var0, boolean var2);

    private static native void setIterateUpperBound(long var0, long var2);

    private static native long iterateUpperBound(long var0);

    private static native void setIterateLowerBound(long var0, long var2);

    private static native long iterateLowerBound(long var0);

    private static native void setTableFilter(long var0, long var2);

    private static native boolean autoPrefixMode(long var0);

    private static native void setAutoPrefixMode(long var0, boolean var2);

    private static native long timestamp(long var0);

    private static native void setTimestamp(long var0, long var2);

    private static native long iterStartTs(long var0);

    private static native void setIterStartTs(long var0, long var2);

    private static native long deadline(long var0);

    private static native void setDeadline(long var0, long var2);

    private static native long ioTimeout(long var0);

    private static native void setIoTimeout(long var0, long var2);

    private static native long valueSizeSoftLimit(long var0);

    private static native void setValueSizeSoftLimit(long var0, long var2);
}

