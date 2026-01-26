/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry.util;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class Random
implements Serializable {
    private static final long serialVersionUID = -4257915988930727506L;
    static final AtomicLong UNIQUE_SEED = new AtomicLong(System.nanoTime());
    private static final long MULT_64 = 6364136223846793005L;
    private static final double DOUBLE_MASK = 9.007199254740992E15;
    private static final float FLOAT_UNIT = 1.6777216E7f;
    private static final long INTEGER_MASK = 0xFFFFFFFFL;
    private long state;
    private long inc;
    private boolean gausAvailable;
    private double nextGaus;

    public Random() {
        this(Random.getRandomSeed(), Random.getRandomSeed());
    }

    public Random(long seed, long streamNumber) {
        this.setSeed(seed, streamNumber);
    }

    private Random(long initialState, long increment, boolean dummy) {
        this.setState(initialState);
        this.setInc(increment);
    }

    public void setSeed(long seed, long streamNumber) {
        this.state = 0L;
        this.inc = streamNumber << 1 | 1L;
        this.state = this.state * 6364136223846793005L + this.inc;
        this.state += seed;
    }

    public byte nextByte() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return (byte)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) >>> 24);
    }

    public void nextBytes(byte[] b) {
        for (int i = 0; i < b.length; ++i) {
            this.state = this.state * 6364136223846793005L + this.inc;
            b[i] = (byte)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) >>> 24);
        }
    }

    public char nextChar() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return (char)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) >>> 16);
    }

    public short nextShort() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return (short)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) >>> 16);
    }

    public int nextInt() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return (int)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L));
    }

    public int nextInt(int n) {
        this.state = this.state * 6364136223846793005L + this.inc;
        int r = (int)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L)) >>> 1;
        int m = n - 1;
        if ((n & m) == 0) {
            r = (int)((long)n * (long)r >> 31);
        } else {
            int u = r;
            while (u - (r = u % n) + m < 0) {
                this.state = this.state * 6364136223846793005L + this.inc;
                u = (int)((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L)) >>> 1;
            }
        }
        return r;
    }

    public boolean nextBoolean() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return ((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 31 != 0L;
    }

    public boolean nextBoolean(double probability) {
        if (probability < 0.0 || probability > 1.0) {
            throw new IllegalArgumentException("probability must be between 0.0 and 1.0 inclusive.");
        }
        if (probability == 0.0) {
            return false;
        }
        if (probability == 1.0) {
            return true;
        }
        this.state = this.state * 6364136223846793005L + this.inc;
        long l = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL;
        this.state = this.state * 6364136223846793005L + this.inc;
        return (double)((l >>> 6 << 27) + (((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 5)) / 9.007199254740992E15 < probability;
    }

    public long nextLong() {
        this.state = this.state * 6364136223846793005L + this.inc;
        long l = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L);
        this.state = this.state * 6364136223846793005L + this.inc;
        long j = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L);
        return (l << 32) + (long)((int)j);
    }

    public long nextLong(long n) {
        long val;
        long j;
        long l;
        long bits;
        if (n == 0L) {
            throw new IllegalArgumentException("n has to be greater than 0");
        }
        do {
            this.state = this.state * 6364136223846793005L + this.inc;
            l = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L);
            this.state = this.state * 6364136223846793005L + this.inc;
        } while ((bits = (l << 32) + (long)((int)(j = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L))) >>> 1) - (val = bits % n) + (n - 1L) < 0L);
        return val;
    }

    public double nextDouble() {
        this.state = this.state * 6364136223846793005L + this.inc;
        long l = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL;
        this.state = this.state * 6364136223846793005L + this.inc;
        return (double)((l >>> 6 << 27) + (((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 5)) / 9.007199254740992E15;
    }

    public double nextDouble(boolean includeZero, boolean includeOne) {
        double d = 0.0;
        do {
            this.state = this.state * 6364136223846793005L + this.inc;
            long l = (this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL;
            this.state = this.state * 6364136223846793005L + this.inc;
            d = (double)((l >>> 6 << 27) + (((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 5)) / 9.007199254740992E15;
            if (!includeOne) continue;
            this.state = this.state * 6364136223846793005L + this.inc;
            if (((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 31 == 0L) continue;
            d += 1.0;
        } while (d > 1.0 || !includeZero && d == 0.0);
        return d;
    }

    public float nextFloat() {
        this.state = this.state * 6364136223846793005L + this.inc;
        return (float)(((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 8) / 1.6777216E7f;
    }

    public float nextFloat(boolean includeZero, boolean includeOne) {
        float d = 0.0f;
        do {
            this.state = this.state * 6364136223846793005L + this.inc;
            d = (float)(((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 8) / 1.6777216E7f;
            if (!includeOne) continue;
            this.state = this.state * 6364136223846793005L + this.inc;
            if (((this.state >>> 22 ^ this.state) >>> (int)((this.state >>> 61) + 22L) & 0xFFFFFFFFL) >>> 31 == 0L) continue;
            d += 1.0f;
        } while (d > 1.0f || !includeZero && d == 0.0f);
        return d;
    }

    private void setInc(long increment) {
        if (increment == 0L || increment % 2L == 0L) {
            throw new IllegalArgumentException("Increment may not be 0 or even. Value: " + increment);
        }
        this.inc = increment;
    }

    private void setState(long state) {
        this.state = state;
    }

    private static long getRandomSeed() {
        long next;
        long current;
        do {
            next = current = UNIQUE_SEED.get();
            next ^= next >> 12;
            next ^= next << 25;
            next ^= next >> 27;
        } while (!UNIQUE_SEED.compareAndSet(current, next *= 2685821657736338717L));
        return next;
    }
}

