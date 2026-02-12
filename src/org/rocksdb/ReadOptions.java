package org.rocksdb;

public class ReadOptions extends RocksObject {
   private AbstractSlice<?> iterateLowerBoundSlice_;
   private AbstractSlice<?> iterateUpperBoundSlice_;
   private AbstractSlice<?> timestampSlice_;
   private AbstractSlice<?> iterStartTs_;

   public ReadOptions() {
      super(newReadOptions());
   }

   public ReadOptions(boolean var1, boolean var2) {
      super(newReadOptions(var1, var2));
   }

   public ReadOptions(ReadOptions var1) {
      super(copyReadOptions(var1.nativeHandle_));
      this.iterateLowerBoundSlice_ = var1.iterateLowerBoundSlice_;
      this.iterateUpperBoundSlice_ = var1.iterateUpperBoundSlice_;
      this.timestampSlice_ = var1.timestampSlice_;
      this.iterStartTs_ = var1.iterStartTs_;
   }

   public boolean verifyChecksums() {
      assert this.isOwningHandle();
      return verifyChecksums(this.nativeHandle_);
   }

   public ReadOptions setVerifyChecksums(boolean var1) {
      assert this.isOwningHandle();
      setVerifyChecksums(this.nativeHandle_, var1);
      return this;
   }

   public boolean fillCache() {
      assert this.isOwningHandle();
      return fillCache(this.nativeHandle_);
   }

   public ReadOptions setFillCache(boolean var1) {
      assert this.isOwningHandle();
      setFillCache(this.nativeHandle_, var1);
      return this;
   }

   public Snapshot snapshot() {
      assert this.isOwningHandle();
      long var1 = snapshot(this.nativeHandle_);
      return var1 != 0L ? new Snapshot(var1) : null;
   }

   public ReadOptions setSnapshot(Snapshot var1) {
      assert this.isOwningHandle();
      if (var1 != null) {
         setSnapshot(this.nativeHandle_, var1.nativeHandle_);
      } else {
         setSnapshot(this.nativeHandle_, 0L);
      }

      return this;
   }

   public ReadTier readTier() {
      assert this.isOwningHandle();
      return ReadTier.getReadTier(readTier(this.nativeHandle_));
   }

   public ReadOptions setReadTier(ReadTier var1) {
      assert this.isOwningHandle();
      setReadTier(this.nativeHandle_, var1.getValue());
      return this;
   }

   public boolean tailing() {
      assert this.isOwningHandle();
      return tailing(this.nativeHandle_);
   }

   public ReadOptions setTailing(boolean var1) {
      assert this.isOwningHandle();
      setTailing(this.nativeHandle_, var1);
      return this;
   }

   @Deprecated
   public boolean managed() {
      assert this.isOwningHandle();
      return managed(this.nativeHandle_);
   }

   @Deprecated
   public ReadOptions setManaged(boolean var1) {
      assert this.isOwningHandle();
      setManaged(this.nativeHandle_, var1);
      return this;
   }

   public boolean totalOrderSeek() {
      assert this.isOwningHandle();
      return totalOrderSeek(this.nativeHandle_);
   }

   public ReadOptions setTotalOrderSeek(boolean var1) {
      assert this.isOwningHandle();
      setTotalOrderSeek(this.nativeHandle_, var1);
      return this;
   }

   public boolean prefixSameAsStart() {
      assert this.isOwningHandle();
      return prefixSameAsStart(this.nativeHandle_);
   }

   public ReadOptions setPrefixSameAsStart(boolean var1) {
      assert this.isOwningHandle();
      setPrefixSameAsStart(this.nativeHandle_, var1);
      return this;
   }

   public boolean pinData() {
      assert this.isOwningHandle();
      return pinData(this.nativeHandle_);
   }

   public ReadOptions setPinData(boolean var1) {
      assert this.isOwningHandle();
      setPinData(this.nativeHandle_, var1);
      return this;
   }

   public boolean backgroundPurgeOnIteratorCleanup() {
      assert this.isOwningHandle();
      return backgroundPurgeOnIteratorCleanup(this.nativeHandle_);
   }

   public ReadOptions setBackgroundPurgeOnIteratorCleanup(boolean var1) {
      assert this.isOwningHandle();
      setBackgroundPurgeOnIteratorCleanup(this.nativeHandle_, var1);
      return this;
   }

   public long readaheadSize() {
      assert this.isOwningHandle();
      return readaheadSize(this.nativeHandle_);
   }

   public ReadOptions setReadaheadSize(long var1) {
      assert this.isOwningHandle();
      setReadaheadSize(this.nativeHandle_, var1);
      return this;
   }

   public long maxSkippableInternalKeys() {
      assert this.isOwningHandle();
      return maxSkippableInternalKeys(this.nativeHandle_);
   }

   public ReadOptions setMaxSkippableInternalKeys(long var1) {
      assert this.isOwningHandle();
      setMaxSkippableInternalKeys(this.nativeHandle_, var1);
      return this;
   }

   @Deprecated
   public boolean ignoreRangeDeletions() {
      assert this.isOwningHandle();
      return ignoreRangeDeletions(this.nativeHandle_);
   }

   @Deprecated
   public ReadOptions setIgnoreRangeDeletions(boolean var1) {
      assert this.isOwningHandle();
      setIgnoreRangeDeletions(this.nativeHandle_, var1);
      return this;
   }

   public ReadOptions setIterateLowerBound(AbstractSlice<?> var1) {
      assert this.isOwningHandle();
      setIterateLowerBound(this.nativeHandle_, var1 == null ? 0L : var1.getNativeHandle());
      this.iterateLowerBoundSlice_ = var1;
      return this;
   }

   public Slice iterateLowerBound() {
      assert this.isOwningHandle();
      long var1 = iterateLowerBound(this.nativeHandle_);
      return var1 != 0L ? new Slice(var1, false) : null;
   }

   public ReadOptions setIterateUpperBound(AbstractSlice<?> var1) {
      assert this.isOwningHandle();
      setIterateUpperBound(this.nativeHandle_, var1 == null ? 0L : var1.getNativeHandle());
      this.iterateUpperBoundSlice_ = var1;
      return this;
   }

   public Slice iterateUpperBound() {
      assert this.isOwningHandle();
      long var1 = iterateUpperBound(this.nativeHandle_);
      return var1 != 0L ? new Slice(var1, false) : null;
   }

   public ReadOptions setTableFilter(AbstractTableFilter var1) {
      assert this.isOwningHandle();
      setTableFilter(this.nativeHandle_, var1.nativeHandle_);
      return this;
   }

   public boolean autoPrefixMode() {
      assert this.isOwningHandle();
      return autoPrefixMode(this.nativeHandle_);
   }

   public ReadOptions setAutoPrefixMode(boolean var1) {
      assert this.isOwningHandle();
      setAutoPrefixMode(this.nativeHandle_, var1);
      return this;
   }

   public Slice timestamp() {
      assert this.isOwningHandle();
      long var1 = timestamp(this.nativeHandle_);
      return var1 == 0L ? null : new Slice(var1);
   }

   public ReadOptions setTimestamp(AbstractSlice<?> var1) {
      assert this.isOwningHandle();
      setTimestamp(this.nativeHandle_, var1 == null ? 0L : var1.getNativeHandle());
      this.timestampSlice_ = var1;
      return this;
   }

   public Slice iterStartTs() {
      assert this.isOwningHandle();
      long var1 = iterStartTs(this.nativeHandle_);
      return var1 == 0L ? null : new Slice(var1);
   }

   public ReadOptions setIterStartTs(AbstractSlice<?> var1) {
      assert this.isOwningHandle();
      setIterStartTs(this.nativeHandle_, var1 == null ? 0L : var1.getNativeHandle());
      this.iterStartTs_ = var1;
      return this;
   }

   public long deadline() {
      assert this.isOwningHandle();
      return deadline(this.nativeHandle_);
   }

   public ReadOptions setDeadline(long var1) {
      assert this.isOwningHandle();
      setDeadline(this.nativeHandle_, var1);
      return this;
   }

   public long ioTimeout() {
      assert this.isOwningHandle();
      return ioTimeout(this.nativeHandle_);
   }

   public ReadOptions setIoTimeout(long var1) {
      assert this.isOwningHandle();
      setIoTimeout(this.nativeHandle_, var1);
      return this;
   }

   public long valueSizeSoftLimit() {
      assert this.isOwningHandle();
      return valueSizeSoftLimit(this.nativeHandle_);
   }

   public ReadOptions setValueSizeSoftLimit(long var1) {
      assert this.isOwningHandle();
      setValueSizeSoftLimit(this.nativeHandle_, var1);
      return this;
   }

   public boolean asyncIo() {
      assert this.isOwningHandle();
      return this.asyncIo(this.nativeHandle_);
   }

   public ReadOptions setAsyncIo(boolean var1) {
      assert this.isOwningHandle();
      this.setAsyncIo(this.nativeHandle_, var1);
      return this;
   }

   private static native long newReadOptions();

   private static native long newReadOptions(boolean var0, boolean var1);

   private static native long copyReadOptions(long var0);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
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
