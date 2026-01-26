/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

public class RadixConverter {
    private static final double LOG_LONG_MAX_VALUE = Math.log(9.223372036854776E18);
    private static final int DEFAULT_POWERS_TO_CACHE = 10;
    private final int digitsGroupLength;
    private final BigInteger digitsGroupSpaceSize;
    private final int radix;
    private final BigInteger[] digitsGroupSpacePowers;

    public RadixConverter(int n, int n2) {
        this.radix = n;
        this.digitsGroupLength = (int)Math.floor(LOG_LONG_MAX_VALUE / Math.log(n));
        this.digitsGroupSpaceSize = BigInteger.valueOf(n).pow(this.digitsGroupLength);
        this.digitsGroupSpacePowers = this.precomputeDigitsGroupPowers(n2, this.digitsGroupSpaceSize);
    }

    public RadixConverter(int n) {
        this(n, 10);
    }

    public int getRadix() {
        return this.radix;
    }

    public void toEncoding(BigInteger bigInteger, int n, short[] sArray) {
        if (bigInteger.signum() < 0) {
            throw new IllegalArgumentException();
        }
        int n2 = n - 1;
        do {
            if (bigInteger.equals(BigInteger.ZERO)) {
                sArray[n2--] = 0;
                continue;
            }
            BigInteger[] bigIntegerArray = bigInteger.divideAndRemainder(this.digitsGroupSpaceSize);
            bigInteger = bigIntegerArray[0];
            n2 = this.toEncoding(bigIntegerArray[1].longValue(), n2, sArray);
        } while (n2 >= 0);
        if (bigInteger.signum() != 0) {
            throw new IllegalArgumentException();
        }
    }

    private int toEncoding(long l, int n, short[] sArray) {
        for (int i = 0; i < this.digitsGroupLength && n >= 0; ++i) {
            if (l == 0L) {
                sArray[n--] = 0;
                continue;
            }
            sArray[n--] = (short)(l % (long)this.radix);
            l /= (long)this.radix;
        }
        if (l != 0L) {
            throw new IllegalStateException("Failed to convert decimal number");
        }
        return n;
    }

    public BigInteger fromEncoding(short[] sArray) {
        BigInteger bigInteger = BigIntegers.ONE;
        BigInteger bigInteger2 = null;
        int n = 0;
        int n2 = sArray.length;
        for (int i = n2 - this.digitsGroupLength; i > -this.digitsGroupLength; i -= this.digitsGroupLength) {
            int n3 = this.digitsGroupLength;
            if (i < 0) {
                n3 = this.digitsGroupLength + i;
                i = 0;
            }
            int n4 = Math.min(i + n3, n2);
            long l = this.fromEncoding(i, n4, sArray);
            BigInteger bigInteger3 = BigInteger.valueOf(l);
            if (n == 0) {
                bigInteger2 = bigInteger3;
            } else {
                bigInteger = n <= this.digitsGroupSpacePowers.length ? this.digitsGroupSpacePowers[n - 1] : bigInteger.multiply(this.digitsGroupSpaceSize);
                bigInteger2 = bigInteger2.add(bigInteger3.multiply(bigInteger));
            }
            ++n;
        }
        return bigInteger2;
    }

    public int getDigitsGroupLength() {
        return this.digitsGroupLength;
    }

    private long fromEncoding(int n, int n2, short[] sArray) {
        long l = 0L;
        for (int i = n; i < n2; ++i) {
            l = l * (long)this.radix + (long)(sArray[i] & 0xFFFF);
        }
        return l;
    }

    private BigInteger[] precomputeDigitsGroupPowers(int n, BigInteger bigInteger) {
        BigInteger[] bigIntegerArray = new BigInteger[n];
        BigInteger bigInteger2 = bigInteger;
        for (int i = 0; i < n; ++i) {
            bigIntegerArray[i] = bigInteger2;
            bigInteger2 = bigInteger2.multiply(bigInteger);
        }
        return bigIntegerArray;
    }
}

