/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.math.range;

import com.hypixel.hytale.math.codec.IntRangeArrayCodec;
import com.hypixel.hytale.math.util.MathUtil;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IntRange {
    public static final IntRangeArrayCodec CODEC = new IntRangeArrayCodec();
    private int inclusiveMin;
    private int inclusiveMax;
    private int range;

    public IntRange() {
        this(0, 0);
    }

    public IntRange(int inclusiveMin, int inclusiveMax) {
        this.inclusiveMin = inclusiveMin;
        this.inclusiveMax = inclusiveMax;
        this.range = inclusiveMax - inclusiveMin + 1;
    }

    public int getInclusiveMin() {
        return this.inclusiveMin;
    }

    public int getInclusiveMax() {
        return this.inclusiveMax;
    }

    public void setInclusiveMin(int inclusiveMin) {
        this.inclusiveMin = inclusiveMin;
        this.range = this.inclusiveMax - inclusiveMin + 1;
    }

    public void setInclusiveMax(int inclusiveMax) {
        this.inclusiveMax = inclusiveMax;
        this.range = inclusiveMax - this.inclusiveMin + 1;
    }

    public int getInt(float factor) {
        int value = this.inclusiveMin + MathUtil.fastFloor((float)this.range * factor);
        return Integer.min(this.inclusiveMax, value);
    }

    public int getInt(double factor) {
        int value = this.inclusiveMin + MathUtil.floor((double)this.range * factor);
        return Integer.min(this.inclusiveMax, value);
    }

    public boolean includes(int value) {
        return value >= this.inclusiveMin && value <= this.inclusiveMax;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IntRange intRange = (IntRange)o;
        if (this.inclusiveMin != intRange.inclusiveMin) {
            return false;
        }
        return this.inclusiveMax == intRange.inclusiveMax;
    }

    public int hashCode() {
        int result = this.inclusiveMin;
        result = 31 * result + this.inclusiveMax;
        return result;
    }

    @Nonnull
    public String toString() {
        return "IntRange{inclusiveMin=" + this.inclusiveMin + ", inclusiveMax=" + this.inclusiveMax + "}";
    }
}

