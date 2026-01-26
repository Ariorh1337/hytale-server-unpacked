/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class BitBuilder {
    private static final byte[] bits = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};
    byte[] buf = new byte[1];
    int pos = 0;

    public BitBuilder writeBit(int n) {
        if (this.pos / 8 >= this.buf.length) {
            byte[] byArray = new byte[this.buf.length + 4];
            System.arraycopy(this.buf, 0, byArray, 0, this.pos / 8);
            Arrays.clear(this.buf);
            this.buf = byArray;
        }
        if (n == 0) {
            int n2 = this.pos / 8;
            this.buf[n2] = (byte)(this.buf[n2] & ~bits[this.pos % 8]);
        } else {
            int n3 = this.pos / 8;
            this.buf[n3] = (byte)(this.buf[n3] | bits[this.pos % 8]);
        }
        ++this.pos;
        return this;
    }

    public BitBuilder writeBits(long l, int n) {
        for (int i = n - 1; i >= 0; --i) {
            int n2 = (l & 1L << i) > 0L ? 1 : 0;
            this.writeBit(n2);
        }
        return this;
    }

    public BitBuilder writeBits(long l, int n, int n2) {
        for (int i = n - 1; i >= n - n2; --i) {
            int n3 = (l & 1L << i) != 0L ? 1 : 0;
            this.writeBit(n3);
        }
        return this;
    }

    public int write(OutputStream outputStream) throws IOException {
        int n = (this.pos + this.pos % 8) / 8;
        outputStream.write(this.buf, 0, n);
        outputStream.flush();
        return n;
    }

    public int writeAndClear(OutputStream outputStream) throws IOException {
        int n = (this.pos + this.pos % 8) / 8;
        outputStream.write(this.buf, 0, n);
        outputStream.flush();
        this.zero();
        return n;
    }

    public void pad() {
        this.pos += this.pos % 8;
    }

    public void write7BitBytes(int n) {
        boolean bl = false;
        for (int i = 4; i >= 0; --i) {
            if (!bl && (n & 0xFE000000) != 0) {
                bl = true;
            }
            if (bl) {
                this.writeBit(i).writeBits(n, 32, 7);
            }
            n <<= 7;
        }
    }

    public void write7BitBytes(BigInteger bigInteger) {
        int n = (bigInteger.bitLength() + bigInteger.bitLength() % 8) / 8;
        BigInteger bigInteger2 = BigInteger.valueOf(254L).shiftLeft(n * 8);
        boolean bl = false;
        for (int i = n; i >= 0; --i) {
            if (!bl && bigInteger.and(bigInteger2).compareTo(BigInteger.ZERO) != 0) {
                bl = true;
            }
            if (bl) {
                BigInteger bigInteger3 = bigInteger.and(bigInteger2).shiftRight(8 * n - 8);
                this.writeBit(i).writeBits(bigInteger3.intValue(), 8, 7);
            }
            bigInteger = bigInteger.shiftLeft(7);
        }
    }

    public void zero() {
        Arrays.clear(this.buf);
        this.pos = 0;
    }
}

