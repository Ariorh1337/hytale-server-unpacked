/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;

public final class AdaptiveCalculator {
    private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    private final int minIndex;
    private final int maxIndex;
    private final int minCapacity;
    private final int maxCapacity;
    private int index;
    private int nextSize;
    private boolean decreaseNow;

    private static int getSizeTableIndex(int size) {
        int a;
        int mid;
        int low = 0;
        int high = SIZE_TABLE.length - 1;
        while (true) {
            if (high < low) {
                return low;
            }
            if (high == low) {
                return high;
            }
            mid = low + high >>> 1;
            a = SIZE_TABLE[mid];
            int b = SIZE_TABLE[mid + 1];
            if (size > b) {
                low = mid + 1;
                continue;
            }
            if (size >= a) break;
            high = mid - 1;
        }
        if (size == a) {
            return mid;
        }
        return mid + 1;
    }

    public AdaptiveCalculator(int minimum, int initial, int maximum) {
        ObjectUtil.checkPositive(minimum, "minimum");
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }
        int minIndex = AdaptiveCalculator.getSizeTableIndex(minimum);
        this.minIndex = SIZE_TABLE[minIndex] < minimum ? minIndex + 1 : minIndex;
        int maxIndex = AdaptiveCalculator.getSizeTableIndex(maximum);
        this.maxIndex = SIZE_TABLE[maxIndex] > maximum ? maxIndex - 1 : maxIndex;
        int initialIndex = AdaptiveCalculator.getSizeTableIndex(initial);
        this.index = SIZE_TABLE[initialIndex] > initial ? initialIndex - 1 : initialIndex;
        this.minCapacity = minimum;
        this.maxCapacity = maximum;
        this.nextSize = Math.max(SIZE_TABLE[this.index], this.minCapacity);
    }

    public void record(int size) {
        if (size <= SIZE_TABLE[Math.max(0, this.index - 1)]) {
            if (this.decreaseNow) {
                this.index = Math.max(this.index - 1, this.minIndex);
                this.nextSize = Math.max(SIZE_TABLE[this.index], this.minCapacity);
                this.decreaseNow = false;
            } else {
                this.decreaseNow = true;
            }
        } else if (size >= this.nextSize) {
            this.index = Math.min(this.index + 4, this.maxIndex);
            this.nextSize = Math.min(SIZE_TABLE[this.index], this.maxCapacity);
            this.decreaseNow = false;
        }
    }

    public int nextSize() {
        return this.nextSize;
    }

    static {
        int i;
        ArrayList<Integer> sizeTable = new ArrayList<Integer>();
        for (i = 16; i < 512; i += 16) {
            sizeTable.add(i);
        }
        for (i = 512; i > 0; i <<= 1) {
            sizeTable.add(i);
        }
        SIZE_TABLE = new int[sizeTable.size()];
        for (i = 0; i < SIZE_TABLE.length; ++i) {
            AdaptiveCalculator.SIZE_TABLE[i] = (Integer)sizeTable.get(i);
        }
    }
}

