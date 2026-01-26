/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.ISAPDigest;
import org.bouncycastle.util.Longs;

public class AsconPermutationFriend {
    public static AsconPermutation getAsconPermutation(ISAPDigest.Friend friend) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by ISAPDigest or Ascon Digest");
        }
        return new AsconPermutation();
    }

    public static class AsconPermutation {
        public long x0;
        public long x1;
        public long x2;
        public long x3;
        public long x4;

        AsconPermutation() {
        }

        public void round(long l) {
            this.x2 ^= l;
            long l2 = this.x0 ^ this.x4;
            long l3 = this.x1 ^ this.x2;
            long l4 = this.x1 | this.x2;
            long l5 = this.x3 ^ l4 ^ this.x0 ^ this.x1 & l2;
            long l6 = l2 ^ (l4 | this.x3) ^ this.x1 & this.x2 & this.x3;
            long l7 = l3 ^ this.x4 & (this.x3 ^ 0xFFFFFFFFFFFFFFFFL);
            long l8 = (this.x0 | this.x3 ^ this.x4) ^ l3;
            long l9 = this.x3 ^ (this.x1 | this.x4) ^ this.x0 & this.x1;
            this.x0 = l5 ^ Longs.rotateRight(l5, 19) ^ Longs.rotateRight(l5, 28);
            this.x1 = l6 ^ Longs.rotateRight(l6, 39) ^ Longs.rotateRight(l6, 61);
            this.x2 = l7 ^ Longs.rotateRight(l7, 1) ^ Longs.rotateRight(l7, 6) ^ 0xFFFFFFFFFFFFFFFFL;
            this.x3 = l8 ^ Longs.rotateRight(l8, 10) ^ Longs.rotateRight(l8, 17);
            this.x4 = l9 ^ Longs.rotateRight(l9, 7) ^ Longs.rotateRight(l9, 41);
        }

        public void p(int n) {
            if (n == 12) {
                this.round(240L);
                this.round(225L);
                this.round(210L);
                this.round(195L);
            }
            if (n >= 8) {
                this.round(180L);
                this.round(165L);
            }
            this.round(150L);
            this.round(135L);
            this.round(120L);
            this.round(105L);
            this.round(90L);
            this.round(75L);
        }

        public void set(long l, long l2, long l3, long l4, long l5) {
            this.x0 = l;
            this.x1 = l2;
            this.x2 = l3;
            this.x3 = l4;
            this.x4 = l5;
        }
    }
}

