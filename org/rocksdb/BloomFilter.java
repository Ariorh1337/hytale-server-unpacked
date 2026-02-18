/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.Filter;

public class BloomFilter
extends Filter {
    private static final double DEFAULT_BITS_PER_KEY = 10.0;
    private final double bitsPerKey;

    public BloomFilter() {
        this(10.0);
    }

    public BloomFilter(double d) {
        this(BloomFilter.createNewBloomFilter(d), d);
    }

    BloomFilter(long l, double d) {
        super(l);
        this.bitsPerKey = d;
    }

    public BloomFilter(double d, boolean bl) {
        this(d);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        return this.bitsPerKey == ((BloomFilter)object).bitsPerKey;
    }

    public int hashCode() {
        return Objects.hash(this.bitsPerKey);
    }

    private static native long createNewBloomFilter(double var0);
}

