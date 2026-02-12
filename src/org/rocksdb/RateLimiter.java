package org.rocksdb;

public class RateLimiter extends RocksObject {
   public static final long DEFAULT_REFILL_PERIOD_MICROS = 100000L;
   public static final int DEFAULT_FAIRNESS = 10;
   public static final RateLimiterMode DEFAULT_MODE = RateLimiterMode.WRITES_ONLY;
   public static final boolean DEFAULT_AUTOTUNE = false;

   public RateLimiter(long var1) {
      this(var1, 100000L, 10, DEFAULT_MODE, false);
   }

   public RateLimiter(long var1, long var3) {
      this(var1, var3, 10, DEFAULT_MODE, false);
   }

   public RateLimiter(long var1, long var3, int var5) {
      this(var1, var3, var5, DEFAULT_MODE, false);
   }

   public RateLimiter(long var1, long var3, int var5, RateLimiterMode var6) {
      this(var1, var3, var5, var6, false);
   }

   public RateLimiter(long var1, long var3, int var5, RateLimiterMode var6, boolean var7) {
      super(newRateLimiterHandle(var1, var3, var5, var6.getValue(), var7));
   }

   public void setBytesPerSecond(long var1) {
      assert this.isOwningHandle();
      setBytesPerSecond(this.nativeHandle_, var1);
   }

   public long getBytesPerSecond() {
      assert this.isOwningHandle();
      return getBytesPerSecond(this.nativeHandle_);
   }

   public void request(long var1) {
      assert this.isOwningHandle();
      request(this.nativeHandle_, var1);
   }

   public long getSingleBurstBytes() {
      assert this.isOwningHandle();
      return getSingleBurstBytes(this.nativeHandle_);
   }

   public long getTotalBytesThrough() {
      assert this.isOwningHandle();
      return getTotalBytesThrough(this.nativeHandle_);
   }

   public long getTotalRequests() {
      assert this.isOwningHandle();
      return getTotalRequests(this.nativeHandle_);
   }

   private static native long newRateLimiterHandle(long var0, long var2, int var4, byte var5, boolean var6);

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native void setBytesPerSecond(long var0, long var2);

   private static native long getBytesPerSecond(long var0);

   private static native void request(long var0, long var2);

   private static native long getSingleBurstBytes(long var0);

   private static native long getTotalBytesThrough(long var0);

   private static native long getTotalRequests(long var0);
}
