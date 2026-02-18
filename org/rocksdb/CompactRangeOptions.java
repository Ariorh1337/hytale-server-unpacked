/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.RocksObject;

public class CompactRangeOptions
extends RocksObject {
    private static final byte VALUE_kSkip = 0;
    private static final byte VALUE_kIfHaveCompactionFilter = 1;
    private static final byte VALUE_kForce = 2;
    private static final byte VALUE_kForceOptimized = 3;

    public CompactRangeOptions() {
        super(CompactRangeOptions.newCompactRangeOptions());
    }

    public boolean exclusiveManualCompaction() {
        return CompactRangeOptions.exclusiveManualCompaction(this.nativeHandle_);
    }

    public CompactRangeOptions setExclusiveManualCompaction(boolean bl) {
        CompactRangeOptions.setExclusiveManualCompaction(this.nativeHandle_, bl);
        return this;
    }

    public boolean changeLevel() {
        return CompactRangeOptions.changeLevel(this.nativeHandle_);
    }

    public CompactRangeOptions setChangeLevel(boolean bl) {
        CompactRangeOptions.setChangeLevel(this.nativeHandle_, bl);
        return this;
    }

    public int targetLevel() {
        return CompactRangeOptions.targetLevel(this.nativeHandle_);
    }

    public CompactRangeOptions setTargetLevel(int n) {
        CompactRangeOptions.setTargetLevel(this.nativeHandle_, n);
        return this;
    }

    public int targetPathId() {
        return CompactRangeOptions.targetPathId(this.nativeHandle_);
    }

    public CompactRangeOptions setTargetPathId(int n) {
        CompactRangeOptions.setTargetPathId(this.nativeHandle_, n);
        return this;
    }

    public BottommostLevelCompaction bottommostLevelCompaction() {
        return BottommostLevelCompaction.fromRocksId(CompactRangeOptions.bottommostLevelCompaction(this.nativeHandle_));
    }

    public CompactRangeOptions setBottommostLevelCompaction(BottommostLevelCompaction bottommostLevelCompaction) {
        CompactRangeOptions.setBottommostLevelCompaction(this.nativeHandle_, bottommostLevelCompaction.getValue());
        return this;
    }

    public boolean allowWriteStall() {
        return CompactRangeOptions.allowWriteStall(this.nativeHandle_);
    }

    public CompactRangeOptions setAllowWriteStall(boolean bl) {
        CompactRangeOptions.setAllowWriteStall(this.nativeHandle_, bl);
        return this;
    }

    public int maxSubcompactions() {
        return CompactRangeOptions.maxSubcompactions(this.nativeHandle_);
    }

    public CompactRangeOptions setMaxSubcompactions(int n) {
        CompactRangeOptions.setMaxSubcompactions(this.nativeHandle_, n);
        return this;
    }

    public CompactRangeOptions setFullHistoryTSLow(Timestamp timestamp) {
        CompactRangeOptions.setFullHistoryTSLow(this.nativeHandle_, timestamp.start, timestamp.range);
        return this;
    }

    public Timestamp fullHistoryTSLow() {
        return CompactRangeOptions.fullHistoryTSLow(this.nativeHandle_);
    }

    public CompactRangeOptions setCanceled(boolean bl) {
        CompactRangeOptions.setCanceled(this.nativeHandle_, bl);
        return this;
    }

    public boolean canceled() {
        return CompactRangeOptions.canceled(this.nativeHandle_);
    }

    private static native long newCompactRangeOptions();

    @Override
    protected final void disposeInternal(long l) {
        CompactRangeOptions.disposeInternalJni(l);
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

    private static native Timestamp fullHistoryTSLow(long var0);

    private static native void setCanceled(long var0, boolean var2);

    private static native boolean canceled(long var0);

    public static class Timestamp {
        public final long start;
        public final long range;

        public Timestamp(long l, long l2) {
            this.start = l;
            this.range = l2;
        }

        public Timestamp() {
            this.start = 0L;
            this.range = 0L;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || this.getClass() != object.getClass()) {
                return false;
            }
            Timestamp timestamp = (Timestamp)object;
            return this.start == timestamp.start && this.range == timestamp.range;
        }

        public int hashCode() {
            return Objects.hash(this.start, this.range);
        }
    }

    public static enum BottommostLevelCompaction {
        kSkip(0),
        kIfHaveCompactionFilter(1),
        kForce(2),
        kForceOptimized(3);

        private final byte value;

        private BottommostLevelCompaction(byte by) {
            this.value = by;
        }

        public byte getValue() {
            return this.value;
        }

        public static BottommostLevelCompaction fromRocksId(int n) {
            switch (n) {
                case 0: {
                    return kSkip;
                }
                case 1: {
                    return kIfHaveCompactionFilter;
                }
                case 2: {
                    return kForce;
                }
                case 3: {
                    return kForceOptimized;
                }
            }
            return null;
        }
    }
}

