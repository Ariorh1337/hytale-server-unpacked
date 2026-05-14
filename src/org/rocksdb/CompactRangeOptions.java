package org.rocksdb;

import java.util.Objects;

public class CompactRangeOptions extends RocksObject {
   private static final byte VALUE_kSkip = 0;
   private static final byte VALUE_kIfHaveCompactionFilter = 1;
   private static final byte VALUE_kForce = 2;
   private static final byte VALUE_kForceOptimized = 3;

   public CompactRangeOptions() {
      super(newCompactRangeOptions());
   }

   public boolean exclusiveManualCompaction() {
      return exclusiveManualCompaction(this.nativeHandle_);
   }

   public CompactRangeOptions setExclusiveManualCompaction(boolean var1) {
      setExclusiveManualCompaction(this.nativeHandle_, var1);
      return this;
   }

   public boolean changeLevel() {
      return changeLevel(this.nativeHandle_);
   }

   public CompactRangeOptions setChangeLevel(boolean var1) {
      setChangeLevel(this.nativeHandle_, var1);
      return this;
   }

   public int targetLevel() {
      return targetLevel(this.nativeHandle_);
   }

   public CompactRangeOptions setTargetLevel(int var1) {
      setTargetLevel(this.nativeHandle_, var1);
      return this;
   }

   public int targetPathId() {
      return targetPathId(this.nativeHandle_);
   }

   public CompactRangeOptions setTargetPathId(int var1) {
      setTargetPathId(this.nativeHandle_, var1);
      return this;
   }

   public CompactRangeOptions.BottommostLevelCompaction bottommostLevelCompaction() {
      return CompactRangeOptions.BottommostLevelCompaction.fromRocksId(bottommostLevelCompaction(this.nativeHandle_));
   }

   public CompactRangeOptions setBottommostLevelCompaction(CompactRangeOptions.BottommostLevelCompaction var1) {
      setBottommostLevelCompaction(this.nativeHandle_, var1.getValue());
      return this;
   }

   public boolean allowWriteStall() {
      return allowWriteStall(this.nativeHandle_);
   }

   public CompactRangeOptions setAllowWriteStall(boolean var1) {
      setAllowWriteStall(this.nativeHandle_, var1);
      return this;
   }

   public int maxSubcompactions() {
      return maxSubcompactions(this.nativeHandle_);
   }

   public CompactRangeOptions setMaxSubcompactions(int var1) {
      setMaxSubcompactions(this.nativeHandle_, var1);
      return this;
   }

   public CompactRangeOptions setFullHistoryTSLow(CompactRangeOptions.Timestamp var1) {
      setFullHistoryTSLow(this.nativeHandle_, var1.start, var1.range);
      return this;
   }

   public CompactRangeOptions.Timestamp fullHistoryTSLow() {
      return fullHistoryTSLow(this.nativeHandle_);
   }

   public CompactRangeOptions setCanceled(boolean var1) {
      setCanceled(this.nativeHandle_, var1);
      return this;
   }

   public boolean canceled() {
      return canceled(this.nativeHandle_);
   }

   private static native long newCompactRangeOptions();

   @Override
   protected final void disposeInternal(long var1) {
      disposeInternalJni(var1);
   }

   private static native void disposeInternalJni(long var0);

   private static native boolean exclusiveManualCompaction(long var0);

   private static native void setExclusiveManualCompaction(long var0, boolean var2);

   private static native boolean changeLevel(long var0);

   private static native void setChangeLevel(long var0, boolean var2);

   private static native int targetLevel(long var0);

   private static native void setTargetLevel(long var0, int var2);

   private static native int targetPathId(long var0);

   private static native void setTargetPathId(long var0, int var2);

   private static native int bottommostLevelCompaction(long var0);

   private static native void setBottommostLevelCompaction(long var0, int var2);

   private static native boolean allowWriteStall(long var0);

   private static native void setAllowWriteStall(long var0, boolean var2);

   private static native void setMaxSubcompactions(long var0, int var2);

   private static native int maxSubcompactions(long var0);

   private static native void setFullHistoryTSLow(long var0, long var2, long var4);

   private static native CompactRangeOptions.Timestamp fullHistoryTSLow(long var0);

   private static native void setCanceled(long var0, boolean var2);

   private static native boolean canceled(long var0);

   public enum BottommostLevelCompaction {
      kSkip((byte)0),
      kIfHaveCompactionFilter((byte)1),
      kForce((byte)2),
      kForceOptimized((byte)3);

      private final byte value;

      BottommostLevelCompaction(final byte nullxx) {
         this.value = nullxx;
      }

      public byte getValue() {
         return this.value;
      }

      public static CompactRangeOptions.BottommostLevelCompaction fromRocksId(int var0) {
         switch (var0) {
            case 0:
               return kSkip;
            case 1:
               return kIfHaveCompactionFilter;
            case 2:
               return kForce;
            case 3:
               return kForceOptimized;
            default:
               return null;
         }
      }
   }

   public static class Timestamp {
      public final long start;
      public final long range;

      public Timestamp(long var1, long var3) {
         this.start = var1;
         this.range = var3;
      }

      public Timestamp() {
         this.start = 0L;
         this.range = 0L;
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            CompactRangeOptions.Timestamp var2 = (CompactRangeOptions.Timestamp)var1;
            return this.start == var2.start && this.range == var2.range;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.start, this.range);
      }
   }
}
