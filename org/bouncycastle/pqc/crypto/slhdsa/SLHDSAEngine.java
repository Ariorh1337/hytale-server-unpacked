/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.slhdsa;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.generators.MGF1BytesGenerator;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.MGFParameters;
import org.bouncycastle.pqc.crypto.slhdsa.ADRS;
import org.bouncycastle.pqc.crypto.slhdsa.IndexedDigest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

abstract class SLHDSAEngine {
    final int N;
    final int WOTS_W;
    final int WOTS_LOGW;
    final int WOTS_LEN;
    final int WOTS_LEN1;
    final int WOTS_LEN2;
    final int D;
    final int A;
    final int K;
    final int H;
    final int H_PRIME;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public SLHDSAEngine(int n, int n2, int n3, int n4, int n5, int n6) {
        this.N = n;
        if (n2 == 16) {
            this.WOTS_LOGW = 4;
            this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
            if (this.N <= 8) {
                this.WOTS_LEN2 = 2;
            } else if (this.N <= 136) {
                this.WOTS_LEN2 = 3;
            } else {
                if (this.N > 256) throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
                this.WOTS_LEN2 = 4;
            }
        } else {
            if (n2 != 256) throw new IllegalArgumentException("wots_w assumed 16 or 256");
            this.WOTS_LOGW = 8;
            this.WOTS_LEN1 = 8 * this.N / this.WOTS_LOGW;
            if (this.N <= 1) {
                this.WOTS_LEN2 = 1;
            } else {
                if (this.N > 256) throw new IllegalArgumentException("cannot precompute SPX_WOTS_LEN2 for n outside {2, .., 256}");
                this.WOTS_LEN2 = 2;
            }
        }
        this.WOTS_W = n2;
        this.WOTS_LEN = this.WOTS_LEN1 + this.WOTS_LEN2;
        this.D = n3;
        this.A = n4;
        this.K = n5;
        this.H = n6;
        this.H_PRIME = n6 / n3;
    }

    abstract void init(byte[] var1);

    abstract byte[] F(byte[] var1, ADRS var2, byte[] var3);

    abstract byte[] H(byte[] var1, ADRS var2, byte[] var3, byte[] var4);

    abstract IndexedDigest H_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4, byte[] var5);

    abstract byte[] T_l(byte[] var1, ADRS var2, byte[] var3);

    abstract byte[] PRF(byte[] var1, byte[] var2, ADRS var3);

    abstract byte[] PRF_msg(byte[] var1, byte[] var2, byte[] var3, byte[] var4);

    static class Sha2Engine
    extends SLHDSAEngine {
        private final HMac treeHMac;
        private final MGF1BytesGenerator mgf1;
        private final byte[] hmacBuf;
        private final Digest msgDigest;
        private final byte[] msgDigestBuf;
        private final int bl;
        private final Digest sha256 = new SHA256Digest();
        private final byte[] sha256Buf = new byte[this.sha256.getDigestSize()];
        private Memoable msgMemo;
        private Memoable sha256Memo;

        public Sha2Engine(int n, int n2, int n3, int n4, int n5, int n6) {
            super(n, n2, n3, n4, n5, n6);
            if (n == 16) {
                this.msgDigest = new SHA256Digest();
                this.treeHMac = new HMac(new SHA256Digest());
                this.mgf1 = new MGF1BytesGenerator(new SHA256Digest());
                this.bl = 64;
            } else {
                this.msgDigest = new SHA512Digest();
                this.treeHMac = new HMac(new SHA512Digest());
                this.mgf1 = new MGF1BytesGenerator(new SHA512Digest());
                this.bl = 128;
            }
            this.hmacBuf = new byte[this.treeHMac.getMacSize()];
            this.msgDigestBuf = new byte[this.msgDigest.getDigestSize()];
        }

        @Override
        void init(byte[] byArray) {
            byte[] byArray2 = new byte[this.bl];
            this.msgDigest.update(byArray, 0, byArray.length);
            this.msgDigest.update(byArray2, 0, this.bl - this.N);
            this.msgMemo = ((Memoable)((Object)this.msgDigest)).copy();
            this.msgDigest.reset();
            this.sha256.update(byArray, 0, byArray.length);
            this.sha256.update(byArray2, 0, 64 - byArray.length);
            this.sha256Memo = ((Memoable)((Object)this.sha256)).copy();
            this.sha256.reset();
        }

        @Override
        public byte[] F(byte[] byArray, ADRS aDRS, byte[] byArray2) {
            byte[] byArray3 = this.compressedADRS(aDRS);
            ((Memoable)((Object)this.sha256)).reset(this.sha256Memo);
            this.sha256.update(byArray3, 0, byArray3.length);
            this.sha256.update(byArray2, 0, byArray2.length);
            this.sha256.doFinal(this.sha256Buf, 0);
            return Arrays.copyOfRange(this.sha256Buf, 0, this.N);
        }

        @Override
        public byte[] H(byte[] byArray, ADRS aDRS, byte[] byArray2, byte[] byArray3) {
            byte[] byArray4 = this.compressedADRS(aDRS);
            ((Memoable)((Object)this.msgDigest)).reset(this.msgMemo);
            this.msgDigest.update(byArray4, 0, byArray4.length);
            this.msgDigest.update(byArray2, 0, byArray2.length);
            this.msgDigest.update(byArray3, 0, byArray3.length);
            this.msgDigest.doFinal(this.msgDigestBuf, 0);
            return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
        }

        @Override
        IndexedDigest H_msg(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
            int n = (this.A * this.K + 7) / 8;
            int n2 = this.H / this.D;
            int n3 = this.H - n2;
            int n4 = (n2 + 7) / 8;
            int n5 = (n3 + 7) / 8;
            int n6 = n + n4 + n5;
            byte[] byArray6 = new byte[n6];
            byte[] byArray7 = new byte[this.msgDigest.getDigestSize()];
            this.msgDigest.update(byArray, 0, byArray.length);
            this.msgDigest.update(byArray2, 0, byArray2.length);
            this.msgDigest.update(byArray3, 0, byArray3.length);
            if (byArray4 != null) {
                this.msgDigest.update(byArray4, 0, byArray4.length);
            }
            this.msgDigest.update(byArray5, 0, byArray5.length);
            this.msgDigest.doFinal(byArray7, 0);
            byArray6 = this.bitmask(Arrays.concatenate(byArray, byArray2, byArray7), byArray6);
            byte[] byArray8 = new byte[8];
            System.arraycopy(byArray6, n, byArray8, 8 - n5, n5);
            long l = Pack.bigEndianToLong(byArray8, 0);
            byte[] byArray9 = new byte[4];
            System.arraycopy(byArray6, n + n5, byArray9, 4 - n4, n4);
            int n7 = Pack.bigEndianToInt(byArray9, 0);
            return new IndexedDigest(l &= -1L >>> 64 - n3, n7 &= -1 >>> 32 - n2, Arrays.copyOfRange(byArray6, 0, n));
        }

        @Override
        public byte[] T_l(byte[] byArray, ADRS aDRS, byte[] byArray2) {
            byte[] byArray3 = this.compressedADRS(aDRS);
            ((Memoable)((Object)this.msgDigest)).reset(this.msgMemo);
            this.msgDigest.update(byArray3, 0, byArray3.length);
            this.msgDigest.update(byArray2, 0, byArray2.length);
            this.msgDigest.doFinal(this.msgDigestBuf, 0);
            return Arrays.copyOfRange(this.msgDigestBuf, 0, this.N);
        }

        @Override
        byte[] PRF(byte[] byArray, byte[] byArray2, ADRS aDRS) {
            int n = byArray2.length;
            ((Memoable)((Object)this.sha256)).reset(this.sha256Memo);
            byte[] byArray3 = this.compressedADRS(aDRS);
            this.sha256.update(byArray3, 0, byArray3.length);
            this.sha256.update(byArray2, 0, byArray2.length);
            this.sha256.doFinal(this.sha256Buf, 0);
            return Arrays.copyOfRange(this.sha256Buf, 0, n);
        }

        @Override
        public byte[] PRF_msg(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
            this.treeHMac.init(new KeyParameter(byArray));
            this.treeHMac.update(byArray2, 0, byArray2.length);
            if (byArray3 != null) {
                this.treeHMac.update(byArray3, 0, byArray3.length);
            }
            this.treeHMac.update(byArray4, 0, byArray4.length);
            this.treeHMac.doFinal(this.hmacBuf, 0);
            return Arrays.copyOfRange(this.hmacBuf, 0, this.N);
        }

        private byte[] compressedADRS(ADRS aDRS) {
            byte[] byArray = new byte[22];
            System.arraycopy(aDRS.value, 3, byArray, 0, 1);
            System.arraycopy(aDRS.value, 8, byArray, 1, 8);
            System.arraycopy(aDRS.value, 19, byArray, 9, 1);
            System.arraycopy(aDRS.value, 20, byArray, 10, 12);
            return byArray;
        }

        protected byte[] bitmask(byte[] byArray, byte[] byArray2) {
            byte[] byArray3 = new byte[byArray2.length];
            this.mgf1.init(new MGFParameters(byArray));
            this.mgf1.generateBytes(byArray3, 0, byArray3.length);
            Bytes.xorTo(byArray2.length, byArray2, byArray3);
            return byArray3;
        }

        protected byte[] bitmask(byte[] byArray, byte[] byArray2, byte[] byArray3) {
            byte[] byArray4 = new byte[byArray2.length + byArray3.length];
            this.mgf1.init(new MGFParameters(byArray));
            this.mgf1.generateBytes(byArray4, 0, byArray4.length);
            Bytes.xorTo(byArray2.length, byArray2, byArray4);
            Bytes.xorTo(byArray3.length, byArray3, 0, byArray4, byArray2.length);
            return byArray4;
        }

        protected byte[] bitmask256(byte[] byArray, byte[] byArray2) {
            byte[] byArray3 = new byte[byArray2.length];
            MGF1BytesGenerator mGF1BytesGenerator = new MGF1BytesGenerator(new SHA256Digest());
            mGF1BytesGenerator.init(new MGFParameters(byArray));
            mGF1BytesGenerator.generateBytes(byArray3, 0, byArray3.length);
            Bytes.xorTo(byArray2.length, byArray2, byArray3);
            return byArray3;
        }
    }

    static class Shake256Engine
    extends SLHDSAEngine {
        private final Xof treeDigest = new SHAKEDigest(256);
        private final Xof maskDigest = new SHAKEDigest(256);

        public Shake256Engine(int n, int n2, int n3, int n4, int n5, int n6) {
            super(n, n2, n3, n4, n5, n6);
        }

        @Override
        void init(byte[] byArray) {
        }

        @Override
        byte[] F(byte[] byArray, ADRS aDRS, byte[] byArray2) {
            byte[] byArray3 = byArray2;
            byte[] byArray4 = new byte[this.N];
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(aDRS.value, 0, aDRS.value.length);
            this.treeDigest.update(byArray3, 0, byArray3.length);
            this.treeDigest.doFinal(byArray4, 0, byArray4.length);
            return byArray4;
        }

        @Override
        byte[] H(byte[] byArray, ADRS aDRS, byte[] byArray2, byte[] byArray3) {
            byte[] byArray4 = new byte[this.N];
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(aDRS.value, 0, aDRS.value.length);
            this.treeDigest.update(byArray2, 0, byArray2.length);
            this.treeDigest.update(byArray3, 0, byArray3.length);
            this.treeDigest.doFinal(byArray4, 0, byArray4.length);
            return byArray4;
        }

        @Override
        IndexedDigest H_msg(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5) {
            int n = (this.A * this.K + 7) / 8;
            int n2 = this.H / this.D;
            int n3 = this.H - n2;
            int n4 = (n2 + 7) / 8;
            int n5 = (n3 + 7) / 8;
            int n6 = n + n4 + n5;
            byte[] byArray6 = new byte[n6];
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(byArray2, 0, byArray2.length);
            this.treeDigest.update(byArray3, 0, byArray3.length);
            if (byArray4 != null) {
                this.treeDigest.update(byArray4, 0, byArray4.length);
            }
            this.treeDigest.update(byArray5, 0, byArray5.length);
            this.treeDigest.doFinal(byArray6, 0, byArray6.length);
            byte[] byArray7 = new byte[8];
            System.arraycopy(byArray6, n, byArray7, 8 - n5, n5);
            long l = Pack.bigEndianToLong(byArray7, 0);
            byte[] byArray8 = new byte[4];
            System.arraycopy(byArray6, n + n5, byArray8, 4 - n4, n4);
            int n7 = Pack.bigEndianToInt(byArray8, 0);
            return new IndexedDigest(l &= -1L >>> 64 - n3, n7 &= -1 >>> 32 - n2, Arrays.copyOfRange(byArray6, 0, n));
        }

        @Override
        byte[] T_l(byte[] byArray, ADRS aDRS, byte[] byArray2) {
            byte[] byArray3 = byArray2;
            byte[] byArray4 = new byte[this.N];
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(aDRS.value, 0, aDRS.value.length);
            this.treeDigest.update(byArray3, 0, byArray3.length);
            this.treeDigest.doFinal(byArray4, 0, byArray4.length);
            return byArray4;
        }

        @Override
        byte[] PRF(byte[] byArray, byte[] byArray2, ADRS aDRS) {
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(aDRS.value, 0, aDRS.value.length);
            this.treeDigest.update(byArray2, 0, byArray2.length);
            byte[] byArray3 = new byte[this.N];
            this.treeDigest.doFinal(byArray3, 0, this.N);
            return byArray3;
        }

        @Override
        public byte[] PRF_msg(byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4) {
            this.treeDigest.update(byArray, 0, byArray.length);
            this.treeDigest.update(byArray2, 0, byArray2.length);
            if (byArray3 != null) {
                this.treeDigest.update(byArray3, 0, byArray3.length);
            }
            this.treeDigest.update(byArray4, 0, byArray4.length);
            byte[] byArray5 = new byte[this.N];
            this.treeDigest.doFinal(byArray5, 0, byArray5.length);
            return byArray5;
        }

        protected byte[] bitmask(byte[] byArray, ADRS aDRS, byte[] byArray2) {
            byte[] byArray3 = new byte[byArray2.length];
            this.maskDigest.update(byArray, 0, byArray.length);
            this.maskDigest.update(aDRS.value, 0, aDRS.value.length);
            this.maskDigest.doFinal(byArray3, 0, byArray3.length);
            Bytes.xorTo(byArray2.length, byArray2, byArray3);
            return byArray3;
        }

        protected byte[] bitmask(byte[] byArray, ADRS aDRS, byte[] byArray2, byte[] byArray3) {
            byte[] byArray4 = new byte[byArray2.length + byArray3.length];
            this.maskDigest.update(byArray, 0, byArray.length);
            this.maskDigest.update(aDRS.value, 0, aDRS.value.length);
            this.maskDigest.doFinal(byArray4, 0, byArray4.length);
            Bytes.xorTo(byArray2.length, byArray2, byArray4);
            Bytes.xorTo(byArray3.length, byArray3, 0, byArray4, byArray2.length);
            return byArray4;
        }
    }
}

