/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.crypto.engines.AsconPermutationFriend;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Pack;

public class ISAPEngine
extends AEADBaseEngine {
    private static final int ISAP_STATE_SZ = 40;
    private byte[] k;
    private byte[] npub;
    private int ISAP_rH;
    private final ISAP_AEAD ISAPAEAD;

    public ISAPEngine(IsapType isapType) {
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        this.KEY_SIZE = 16;
        switch (isapType.ordinal()) {
            case 0: {
                this.ISAPAEAD = new ISAPAEAD_A_128A();
                this.algorithmName = "ISAP-A-128A AEAD";
                break;
            }
            case 1: {
                this.ISAPAEAD = new ISAPAEAD_K_128A();
                this.algorithmName = "ISAP-K-128A AEAD";
                break;
            }
            case 2: {
                this.ISAPAEAD = new ISAPAEAD_A_128();
                this.algorithmName = "ISAP-A-128 AEAD";
                break;
            }
            case 3: {
                this.ISAPAEAD = new ISAPAEAD_K_128();
                this.algorithmName = "ISAP-K-128 AEAD";
                break;
            }
            default: {
                throw new IllegalArgumentException("Incorrect ISAP parameter");
            }
        }
        this.AADBufferSize = this.BlockSize;
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.npub = byArray2;
        this.k = byArray;
        this.ISAPAEAD.init();
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        this.ISAPAEAD.absorbMacBlock(byArray, n);
    }

    @Override
    protected void processFinalAAD() {
        this.ISAPAEAD.absorbFinalAADBlock();
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD3(state, bl);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.ISAPAEAD.processEncBlock(byArray, n, byArray2, n2);
        this.ISAPAEAD.absorbMacBlock(byArray2, n2);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.ISAPAEAD.processEncBlock(byArray, n, byArray2, n2);
        this.ISAPAEAD.absorbMacBlock(byArray, n);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        this.ISAPAEAD.processEncFinalBlock(byArray, n);
        if (this.forEncryption) {
            this.ISAPAEAD.processMACFinal(byArray, n, this.m_bufPos, this.mac);
        } else {
            this.ISAPAEAD.processMACFinal(this.m_buf, 0, this.m_bufPos, this.mac);
        }
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        this.ISAPAEAD.reset();
    }

    private abstract class ISAPAEAD_A
    implements ISAP_AEAD {
        protected long[] k64;
        protected long[] npub64;
        protected long ISAP_IV1_64;
        protected long ISAP_IV2_64;
        protected long ISAP_IV3_64;
        AsconPermutationFriend.AsconPermutation p;
        AsconPermutationFriend.AsconPermutation mac;

        public ISAPAEAD_A() {
            ISAPEngine.this.ISAP_rH = 64;
            ISAPEngine.this.BlockSize = ISAPEngine.this.ISAP_rH + 7 >> 3;
            this.p = new AsconPermutationFriend.AsconPermutation();
            this.mac = new AsconPermutationFriend.AsconPermutation();
        }

        @Override
        public void init() {
            this.npub64 = new long[this.getLongSize(ISAPEngine.this.npub.length)];
            this.k64 = new long[this.getLongSize(ISAPEngine.this.k.length)];
            Pack.bigEndianToLong(ISAPEngine.this.npub, 0, this.npub64);
            Pack.bigEndianToLong(ISAPEngine.this.k, 0, this.k64);
        }

        protected abstract void PX1(AsconPermutationFriend.AsconPermutation var1);

        protected abstract void PX2(AsconPermutationFriend.AsconPermutation var1);

        @Override
        public void absorbMacBlock(byte[] byArray, int n) {
            this.mac.x0 ^= Pack.bigEndianToLong(byArray, n);
            this.mac.p(12);
        }

        @Override
        public void absorbFinalAADBlock() {
            for (int i = 0; i < ISAPEngine.this.m_aadPos; ++i) {
                this.mac.x0 ^= ((long)ISAPEngine.this.m_aad[i] & 0xFFL) << (7 - i << 3);
            }
            this.mac.x0 ^= 128L << (7 - ISAPEngine.this.m_aadPos << 3);
            this.mac.p(12);
            this.mac.x4 ^= 1L;
        }

        @Override
        public void processMACFinal(byte[] byArray, int n, int n2, byte[] byArray2) {
            for (int i = 0; i < n2; ++i) {
                this.mac.x0 ^= ((long)byArray[n++] & 0xFFL) << (7 - i << 3);
            }
            this.mac.x0 ^= 128L << (7 - n2 << 3);
            this.mac.p(12);
            Pack.longToBigEndian(this.mac.x0, byArray2, 0);
            Pack.longToBigEndian(this.mac.x1, byArray2, 8);
            long l = this.mac.x2;
            long l2 = this.mac.x3;
            long l3 = this.mac.x4;
            this.isap_rk(this.mac, this.ISAP_IV2_64, byArray2, ISAPEngine.this.KEY_SIZE);
            this.mac.x2 = l;
            this.mac.x3 = l2;
            this.mac.x4 = l3;
            this.mac.p(12);
            Pack.longToBigEndian(this.mac.x0, byArray2, 0);
            Pack.longToBigEndian(this.mac.x1, byArray2, 8);
        }

        private void isap_rk(AsconPermutationFriend.AsconPermutation asconPermutation, long l, byte[] byArray, int n) {
            asconPermutation.set(this.k64[0], this.k64[1], l, 0L, 0L);
            asconPermutation.p(12);
            for (int i = 0; i < (n << 3) - 1; ++i) {
                asconPermutation.x0 ^= ((long)((byArray[i >>> 3] >>> 7 - (i & 7) & 1) << 7) & 0xFFL) << 56;
                this.PX2(asconPermutation);
            }
            asconPermutation.x0 ^= ((long)byArray[n - 1] & 1L) << 7 << 56;
            asconPermutation.p(12);
        }

        @Override
        public void processEncBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
            Pack.longToBigEndian(Pack.bigEndianToLong(byArray, n) ^ this.p.x0, byArray2, n2);
            this.PX1(this.p);
        }

        @Override
        public void processEncFinalBlock(byte[] byArray, int n) {
            byte[] byArray2 = Pack.longToLittleEndian(this.p.x0);
            Bytes.xor(ISAPEngine.this.m_bufPos, byArray2, ISAPEngine.this.BlockSize - ISAPEngine.this.m_bufPos, ISAPEngine.this.m_buf, 0, byArray, n);
        }

        @Override
        public void reset() {
            this.isap_rk(this.p, this.ISAP_IV3_64, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE);
            this.p.x3 = this.npub64[0];
            this.p.x4 = this.npub64[1];
            this.PX1(this.p);
            this.mac.set(this.npub64[0], this.npub64[1], this.ISAP_IV1_64, 0L, 0L);
            this.mac.p(12);
        }

        private int getLongSize(int n) {
            return n + 7 >>> 3;
        }
    }

    private class ISAPAEAD_A_128
    extends ISAPAEAD_A {
        public ISAPAEAD_A_128() {
            this.ISAP_IV1_64 = 108156764298152972L;
            this.ISAP_IV2_64 = 180214358336080908L;
            this.ISAP_IV3_64 = 252271952374008844L;
        }

        @Override
        protected void PX1(AsconPermutationFriend.AsconPermutation asconPermutation) {
            asconPermutation.p(12);
        }

        @Override
        protected void PX2(AsconPermutationFriend.AsconPermutation asconPermutation) {
            asconPermutation.p(12);
        }
    }

    private class ISAPAEAD_A_128A
    extends ISAPAEAD_A {
        public ISAPAEAD_A_128A() {
            this.ISAP_IV1_64 = 108156764297430540L;
            this.ISAP_IV2_64 = 180214358335358476L;
            this.ISAP_IV3_64 = 252271952373286412L;
        }

        @Override
        protected void PX1(AsconPermutationFriend.AsconPermutation asconPermutation) {
            asconPermutation.p(6);
        }

        @Override
        protected void PX2(AsconPermutationFriend.AsconPermutation asconPermutation) {
            asconPermutation.round(75L);
        }
    }

    private abstract class ISAPAEAD_K
    implements ISAP_AEAD {
        protected final int ISAP_STATE_SZ_CRYPTO_NPUBBYTES;
        protected short[] ISAP_IV1_16;
        protected short[] ISAP_IV2_16;
        protected short[] ISAP_IV3_16;
        protected short[] k16;
        protected short[] iv16;
        private final int[] KeccakF400RoundConstants;
        protected short[] SX;
        protected short[] macSX;
        protected short[] E;
        protected short[] C;
        protected short[] macE;
        protected short[] macC;

        public ISAPAEAD_K() {
            this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES = 40 - ISAPEngine.this.IV_SIZE;
            this.KeccakF400RoundConstants = new int[]{1, 32898, 32906, 32768, 32907, 1, 32897, 32777, 138, 136, 32777, 10, 32907, 139, 32905, 32771, 32770, 128, 32778, 10};
            this.SX = new short[25];
            this.macSX = new short[25];
            this.E = new short[25];
            this.C = new short[5];
            this.macE = new short[25];
            this.macC = new short[5];
            ISAPEngine.this.ISAP_rH = 144;
            ISAPEngine.this.BlockSize = ISAPEngine.this.ISAP_rH + 7 >> 3;
        }

        @Override
        public void init() {
            this.k16 = new short[ISAPEngine.this.k.length >> 1];
            Pack.littleEndianToShort(ISAPEngine.this.k, 0, this.k16, 0, this.k16.length);
            this.iv16 = new short[ISAPEngine.this.npub.length >> 1];
            Pack.littleEndianToShort(ISAPEngine.this.npub, 0, this.iv16, 0, this.iv16.length);
        }

        @Override
        public void reset() {
            Arrays.fill(this.SX, (short)0);
            this.isap_rk(this.ISAP_IV3_16, ISAPEngine.this.npub, ISAPEngine.this.IV_SIZE, this.SX, this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES, this.C);
            System.arraycopy(this.iv16, 0, this.SX, 17, 8);
            this.PermuteRoundsKX(this.SX, this.E, this.C);
            Arrays.fill(this.macSX, 12, 25, (short)0);
            System.arraycopy(this.iv16, 0, this.macSX, 0, 8);
            System.arraycopy(this.ISAP_IV1_16, 0, this.macSX, 8, 4);
            this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
        }

        protected abstract void PermuteRoundsHX(short[] var1, short[] var2, short[] var3);

        protected abstract void PermuteRoundsKX(short[] var1, short[] var2, short[] var3);

        protected abstract void PermuteRoundsBX(short[] var1, short[] var2, short[] var3);

        @Override
        public void absorbMacBlock(byte[] byArray, int n) {
            this.byteToShortXor(byArray, n, this.macSX, ISAPEngine.this.BlockSize >> 1);
            this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
        }

        @Override
        public void absorbFinalAADBlock() {
            for (int i = 0; i < ISAPEngine.this.m_aadPos; ++i) {
                int n = i >> 1;
                this.macSX[n] = (short)(this.macSX[n] ^ (ISAPEngine.this.m_aad[i] & 0xFF) << ((i & 1) << 3));
            }
            int n = ISAPEngine.this.m_aadPos >> 1;
            this.macSX[n] = (short)(this.macSX[n] ^ 128 << ((ISAPEngine.this.m_aadPos & 1) << 3));
            this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
            this.macSX[24] = (short)(this.macSX[24] ^ 0x100);
        }

        public void isap_rk(short[] sArray, byte[] byArray, int n, short[] sArray2, int n2, short[] sArray3) {
            short[] sArray4 = new short[25];
            short[] sArray5 = new short[25];
            System.arraycopy(this.k16, 0, sArray4, 0, 8);
            System.arraycopy(sArray, 0, sArray4, 8, 4);
            this.PermuteRoundsKX(sArray4, sArray5, sArray3);
            for (int i = 0; i < (n << 3) - 1; ++i) {
                sArray4[0] = (short)(sArray4[0] ^ (byArray[i >> 3] >>> 7 - (i & 7) & 1) << 7);
                this.PermuteRoundsBX(sArray4, sArray5, sArray3);
            }
            sArray4[0] = (short)(sArray4[0] ^ (byArray[n - 1] & 1) << 7);
            this.PermuteRoundsKX(sArray4, sArray5, sArray3);
            System.arraycopy(sArray4, 0, sArray2, 0, n2 == this.ISAP_STATE_SZ_CRYPTO_NPUBBYTES ? 17 : 8);
        }

        @Override
        public void processMACFinal(byte[] byArray, int n, int n2, byte[] byArray2) {
            for (int i = 0; i < n2; ++i) {
                int n3 = i >> 1;
                this.macSX[n3] = (short)(this.macSX[n3] ^ (byArray[n++] & 0xFF) << ((i & 1) << 3));
            }
            int n4 = n2 >> 1;
            this.macSX[n4] = (short)(this.macSX[n4] ^ 128 << ((n2 & 1) << 3));
            this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
            Pack.shortToLittleEndian(this.macSX, 0, 8, byArray2, 0);
            this.isap_rk(this.ISAP_IV2_16, byArray2, ISAPEngine.this.KEY_SIZE, this.macSX, ISAPEngine.this.KEY_SIZE, this.macC);
            this.PermuteRoundsHX(this.macSX, this.macE, this.macC);
            Pack.shortToLittleEndian(this.macSX, 0, 8, byArray2, 0);
        }

        @Override
        public void processEncBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
            for (int i = 0; i < ISAPEngine.this.BlockSize; ++i) {
                byArray2[n2++] = (byte)(this.SX[i >> 1] >>> ((i & 1) << 3) ^ byArray[n++]);
            }
            this.PermuteRoundsKX(this.SX, this.E, this.C);
        }

        @Override
        public void processEncFinalBlock(byte[] byArray, int n) {
            for (int i = 0; i < ISAPEngine.this.m_bufPos; ++i) {
                byArray[n++] = (byte)(this.SX[i >> 1] >>> ((i & 1) << 3) ^ ISAPEngine.this.m_buf[i]);
            }
        }

        private void byteToShortXor(byte[] byArray, int n, short[] sArray, int n2) {
            for (int i = 0; i < n2; ++i) {
                int n3 = i;
                sArray[n3] = (short)(sArray[n3] ^ Pack.littleEndianToShort(byArray, n + (i << 1)));
            }
        }

        protected void rounds12X(short[] sArray, short[] sArray2, short[] sArray3) {
            this.prepareThetaX(sArray, sArray3);
            this.rounds_8_18(sArray, sArray2, sArray3);
        }

        protected void rounds_4_18(short[] sArray, short[] sArray2, short[] sArray3) {
            this.thetaRhoPiChiIotaPrepareTheta(4, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(5, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(6, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(7, sArray2, sArray, sArray3);
            this.rounds_8_18(sArray, sArray2, sArray3);
        }

        protected void rounds_8_18(short[] sArray, short[] sArray2, short[] sArray3) {
            this.thetaRhoPiChiIotaPrepareTheta(8, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(9, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(10, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(11, sArray2, sArray, sArray3);
            this.rounds_12_18(sArray, sArray2, sArray3);
        }

        protected void rounds_12_18(short[] sArray, short[] sArray2, short[] sArray3) {
            this.thetaRhoPiChiIotaPrepareTheta(12, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(13, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(14, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(15, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(16, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(17, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(18, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIota(sArray2, sArray, sArray3);
        }

        protected void prepareThetaX(short[] sArray, short[] sArray2) {
            sArray2[0] = (short)(sArray[0] ^ sArray[5] ^ sArray[10] ^ sArray[15] ^ sArray[20]);
            sArray2[1] = (short)(sArray[1] ^ sArray[6] ^ sArray[11] ^ sArray[16] ^ sArray[21]);
            sArray2[2] = (short)(sArray[2] ^ sArray[7] ^ sArray[12] ^ sArray[17] ^ sArray[22]);
            sArray2[3] = (short)(sArray[3] ^ sArray[8] ^ sArray[13] ^ sArray[18] ^ sArray[23]);
            sArray2[4] = (short)(sArray[4] ^ sArray[9] ^ sArray[14] ^ sArray[19] ^ sArray[24]);
        }

        private short ROL16(short s, int n) {
            return (short)((s & 0xFFFF) << n ^ (s & 0xFFFF) >>> 16 - n);
        }

        protected void thetaRhoPiChiIotaPrepareTheta(int n, short[] sArray, short[] sArray2, short[] sArray3) {
            short s = (short)(sArray3[4] ^ this.ROL16(sArray3[1], 1));
            short s2 = (short)(sArray3[0] ^ this.ROL16(sArray3[2], 1));
            short s3 = (short)(sArray3[1] ^ this.ROL16(sArray3[3], 1));
            short s4 = (short)(sArray3[2] ^ this.ROL16(sArray3[4], 1));
            short s5 = (short)(sArray3[3] ^ this.ROL16(sArray3[0], 1));
            short s6 = sArray[0] = (short)(sArray[0] ^ s);
            sArray[6] = (short)(sArray[6] ^ s2);
            short s7 = this.ROL16(sArray[6], 12);
            sArray[12] = (short)(sArray[12] ^ s3);
            short s8 = this.ROL16(sArray[12], 11);
            sArray[18] = (short)(sArray[18] ^ s4);
            short s9 = this.ROL16(sArray[18], 5);
            sArray[24] = (short)(sArray[24] ^ s5);
            short s10 = this.ROL16(sArray[24], 14);
            sArray3[0] = sArray2[0] = (short)(s6 ^ ~s7 & s8 ^ this.KeccakF400RoundConstants[n]);
            sArray3[1] = sArray2[1] = (short)(s7 ^ ~s8 & s9);
            sArray3[2] = sArray2[2] = (short)(s8 ^ ~s9 & s10);
            sArray3[3] = sArray2[3] = (short)(s9 ^ ~s10 & s6);
            sArray3[4] = sArray2[4] = (short)(s10 ^ ~s6 & s7);
            sArray[3] = (short)(sArray[3] ^ s4);
            s6 = this.ROL16(sArray[3], 12);
            sArray[9] = (short)(sArray[9] ^ s5);
            s7 = this.ROL16(sArray[9], 4);
            sArray[10] = (short)(sArray[10] ^ s);
            s8 = this.ROL16(sArray[10], 3);
            sArray[16] = (short)(sArray[16] ^ s2);
            s9 = this.ROL16(sArray[16], 13);
            sArray[22] = (short)(sArray[22] ^ s3);
            s10 = this.ROL16(sArray[22], 13);
            sArray2[5] = (short)(s6 ^ ~s7 & s8);
            sArray3[0] = (short)(sArray3[0] ^ sArray2[5]);
            sArray2[6] = (short)(s7 ^ ~s8 & s9);
            sArray3[1] = (short)(sArray3[1] ^ sArray2[6]);
            sArray2[7] = (short)(s8 ^ ~s9 & s10);
            sArray3[2] = (short)(sArray3[2] ^ sArray2[7]);
            sArray2[8] = (short)(s9 ^ ~s10 & s6);
            sArray3[3] = (short)(sArray3[3] ^ sArray2[8]);
            sArray2[9] = (short)(s10 ^ ~s6 & s7);
            sArray3[4] = (short)(sArray3[4] ^ sArray2[9]);
            sArray[1] = (short)(sArray[1] ^ s2);
            s6 = this.ROL16(sArray[1], 1);
            sArray[7] = (short)(sArray[7] ^ s3);
            s7 = this.ROL16(sArray[7], 6);
            sArray[13] = (short)(sArray[13] ^ s4);
            s8 = this.ROL16(sArray[13], 9);
            sArray[19] = (short)(sArray[19] ^ s5);
            s9 = this.ROL16(sArray[19], 8);
            sArray[20] = (short)(sArray[20] ^ s);
            s10 = this.ROL16(sArray[20], 2);
            sArray2[10] = (short)(s6 ^ ~s7 & s8);
            sArray3[0] = (short)(sArray3[0] ^ sArray2[10]);
            sArray2[11] = (short)(s7 ^ ~s8 & s9);
            sArray3[1] = (short)(sArray3[1] ^ sArray2[11]);
            sArray2[12] = (short)(s8 ^ ~s9 & s10);
            sArray3[2] = (short)(sArray3[2] ^ sArray2[12]);
            sArray2[13] = (short)(s9 ^ ~s10 & s6);
            sArray3[3] = (short)(sArray3[3] ^ sArray2[13]);
            sArray2[14] = (short)(s10 ^ ~s6 & s7);
            sArray3[4] = (short)(sArray3[4] ^ sArray2[14]);
            sArray[4] = (short)(sArray[4] ^ s5);
            s6 = this.ROL16(sArray[4], 11);
            sArray[5] = (short)(sArray[5] ^ s);
            s7 = this.ROL16(sArray[5], 4);
            sArray[11] = (short)(sArray[11] ^ s2);
            s8 = this.ROL16(sArray[11], 10);
            sArray[17] = (short)(sArray[17] ^ s3);
            s9 = this.ROL16(sArray[17], 15);
            sArray[23] = (short)(sArray[23] ^ s4);
            s10 = this.ROL16(sArray[23], 8);
            sArray2[15] = (short)(s6 ^ ~s7 & s8);
            sArray3[0] = (short)(sArray3[0] ^ sArray2[15]);
            sArray2[16] = (short)(s7 ^ ~s8 & s9);
            sArray3[1] = (short)(sArray3[1] ^ sArray2[16]);
            sArray2[17] = (short)(s8 ^ ~s9 & s10);
            sArray3[2] = (short)(sArray3[2] ^ sArray2[17]);
            sArray2[18] = (short)(s9 ^ ~s10 & s6);
            sArray3[3] = (short)(sArray3[3] ^ sArray2[18]);
            sArray2[19] = (short)(s10 ^ ~s6 & s7);
            sArray3[4] = (short)(sArray3[4] ^ sArray2[19]);
            sArray[2] = (short)(sArray[2] ^ s3);
            s6 = this.ROL16(sArray[2], 14);
            sArray[8] = (short)(sArray[8] ^ s4);
            s7 = this.ROL16(sArray[8], 7);
            sArray[14] = (short)(sArray[14] ^ s5);
            s8 = this.ROL16(sArray[14], 7);
            sArray[15] = (short)(sArray[15] ^ s);
            s9 = this.ROL16(sArray[15], 9);
            sArray[21] = (short)(sArray[21] ^ s2);
            s10 = this.ROL16(sArray[21], 2);
            sArray2[20] = (short)(s6 ^ ~s7 & s8);
            sArray3[0] = (short)(sArray3[0] ^ sArray2[20]);
            sArray2[21] = (short)(s7 ^ ~s8 & s9);
            sArray3[1] = (short)(sArray3[1] ^ sArray2[21]);
            sArray2[22] = (short)(s8 ^ ~s9 & s10);
            sArray3[2] = (short)(sArray3[2] ^ sArray2[22]);
            sArray2[23] = (short)(s9 ^ ~s10 & s6);
            sArray3[3] = (short)(sArray3[3] ^ sArray2[23]);
            sArray2[24] = (short)(s10 ^ ~s6 & s7);
            sArray3[4] = (short)(sArray3[4] ^ sArray2[24]);
        }

        protected void thetaRhoPiChiIota(short[] sArray, short[] sArray2, short[] sArray3) {
            short s = (short)(sArray3[4] ^ this.ROL16(sArray3[1], 1));
            short s2 = (short)(sArray3[0] ^ this.ROL16(sArray3[2], 1));
            short s3 = (short)(sArray3[1] ^ this.ROL16(sArray3[3], 1));
            short s4 = (short)(sArray3[2] ^ this.ROL16(sArray3[4], 1));
            short s5 = (short)(sArray3[3] ^ this.ROL16(sArray3[0], 1));
            short s6 = sArray[0] = (short)(sArray[0] ^ s);
            sArray[6] = (short)(sArray[6] ^ s2);
            short s7 = this.ROL16(sArray[6], 12);
            sArray[12] = (short)(sArray[12] ^ s3);
            short s8 = this.ROL16(sArray[12], 11);
            sArray[18] = (short)(sArray[18] ^ s4);
            short s9 = this.ROL16(sArray[18], 5);
            sArray[24] = (short)(sArray[24] ^ s5);
            short s10 = this.ROL16(sArray[24], 14);
            sArray2[0] = (short)(s6 ^ ~s7 & s8 ^ this.KeccakF400RoundConstants[19]);
            sArray2[1] = (short)(s7 ^ ~s8 & s9);
            sArray2[2] = (short)(s8 ^ ~s9 & s10);
            sArray2[3] = (short)(s9 ^ ~s10 & s6);
            sArray2[4] = (short)(s10 ^ ~s6 & s7);
            sArray[3] = (short)(sArray[3] ^ s4);
            s6 = this.ROL16(sArray[3], 12);
            sArray[9] = (short)(sArray[9] ^ s5);
            s7 = this.ROL16(sArray[9], 4);
            sArray[10] = (short)(sArray[10] ^ s);
            s8 = this.ROL16(sArray[10], 3);
            sArray[16] = (short)(sArray[16] ^ s2);
            s9 = this.ROL16(sArray[16], 13);
            sArray[22] = (short)(sArray[22] ^ s3);
            s10 = this.ROL16(sArray[22], 13);
            sArray2[5] = (short)(s6 ^ ~s7 & s8);
            sArray2[6] = (short)(s7 ^ ~s8 & s9);
            sArray2[7] = (short)(s8 ^ ~s9 & s10);
            sArray2[8] = (short)(s9 ^ ~s10 & s6);
            sArray2[9] = (short)(s10 ^ ~s6 & s7);
            sArray[1] = (short)(sArray[1] ^ s2);
            s6 = this.ROL16(sArray[1], 1);
            sArray[7] = (short)(sArray[7] ^ s3);
            s7 = this.ROL16(sArray[7], 6);
            sArray[13] = (short)(sArray[13] ^ s4);
            s8 = this.ROL16(sArray[13], 9);
            sArray[19] = (short)(sArray[19] ^ s5);
            s9 = this.ROL16(sArray[19], 8);
            sArray[20] = (short)(sArray[20] ^ s);
            s10 = this.ROL16(sArray[20], 2);
            sArray2[10] = (short)(s6 ^ ~s7 & s8);
            sArray2[11] = (short)(s7 ^ ~s8 & s9);
            sArray2[12] = (short)(s8 ^ ~s9 & s10);
            sArray2[13] = (short)(s9 ^ ~s10 & s6);
            sArray2[14] = (short)(s10 ^ ~s6 & s7);
            sArray[4] = (short)(sArray[4] ^ s5);
            s6 = this.ROL16(sArray[4], 11);
            sArray[5] = (short)(sArray[5] ^ s);
            s7 = this.ROL16(sArray[5], 4);
            sArray[11] = (short)(sArray[11] ^ s2);
            s8 = this.ROL16(sArray[11], 10);
            sArray[17] = (short)(sArray[17] ^ s3);
            s9 = this.ROL16(sArray[17], 15);
            sArray[23] = (short)(sArray[23] ^ s4);
            s10 = this.ROL16(sArray[23], 8);
            sArray2[15] = (short)(s6 ^ ~s7 & s8);
            sArray2[16] = (short)(s7 ^ ~s8 & s9);
            sArray2[17] = (short)(s8 ^ ~s9 & s10);
            sArray2[18] = (short)(s9 ^ ~s10 & s6);
            sArray2[19] = (short)(s10 ^ ~s6 & s7);
            sArray[2] = (short)(sArray[2] ^ s3);
            s6 = this.ROL16(sArray[2], 14);
            sArray[8] = (short)(sArray[8] ^ s4);
            s7 = this.ROL16(sArray[8], 7);
            sArray[14] = (short)(sArray[14] ^ s5);
            s8 = this.ROL16(sArray[14], 7);
            sArray[15] = (short)(sArray[15] ^ s);
            s9 = this.ROL16(sArray[15], 9);
            sArray[21] = (short)(sArray[21] ^ s2);
            s10 = this.ROL16(sArray[21], 2);
            sArray2[20] = (short)(s6 ^ ~s7 & s8);
            sArray2[21] = (short)(s7 ^ ~s8 & s9);
            sArray2[22] = (short)(s8 ^ ~s9 & s10);
            sArray2[23] = (short)(s9 ^ ~s10 & s6);
            sArray2[24] = (short)(s10 ^ ~s6 & s7);
        }
    }

    private class ISAPAEAD_K_128
    extends ISAPAEAD_K {
        public ISAPAEAD_K_128() {
            this.ISAP_IV1_16 = new short[]{-32767, 400, 3092, 3084};
            this.ISAP_IV2_16 = new short[]{-32766, 400, 3092, 3084};
            this.ISAP_IV3_16 = new short[]{-32765, 400, 3092, 3084};
        }

        @Override
        protected void PermuteRoundsHX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.prepareThetaX(sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(0, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(1, sArray2, sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(2, sArray, sArray2, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(3, sArray2, sArray, sArray3);
            this.rounds_4_18(sArray, sArray2, sArray3);
        }

        @Override
        protected void PermuteRoundsKX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.rounds12X(sArray, sArray2, sArray3);
        }

        @Override
        protected void PermuteRoundsBX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.rounds12X(sArray, sArray2, sArray3);
        }
    }

    private class ISAPAEAD_K_128A
    extends ISAPAEAD_K {
        public ISAPAEAD_K_128A() {
            this.ISAP_IV1_16 = new short[]{-32767, 400, 272, 2056};
            this.ISAP_IV2_16 = new short[]{-32766, 400, 272, 2056};
            this.ISAP_IV3_16 = new short[]{-32765, 400, 272, 2056};
        }

        @Override
        protected void PermuteRoundsHX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.prepareThetaX(sArray, sArray3);
            this.rounds_4_18(sArray, sArray2, sArray3);
        }

        @Override
        protected void PermuteRoundsKX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.prepareThetaX(sArray, sArray3);
            this.rounds_12_18(sArray, sArray2, sArray3);
        }

        @Override
        protected void PermuteRoundsBX(short[] sArray, short[] sArray2, short[] sArray3) {
            this.prepareThetaX(sArray, sArray3);
            this.thetaRhoPiChiIotaPrepareTheta(19, sArray, sArray2, sArray3);
            System.arraycopy(sArray2, 0, sArray, 0, sArray2.length);
        }
    }

    private static interface ISAP_AEAD {
        public void init();

        public void reset();

        public void absorbMacBlock(byte[] var1, int var2);

        public void absorbFinalAADBlock();

        public void processEncBlock(byte[] var1, int var2, byte[] var3, int var4);

        public void processEncFinalBlock(byte[] var1, int var2);

        public void processMACFinal(byte[] var1, int var2, int var3, byte[] var4);
    }

    public static enum IsapType {
        ISAP_A_128A,
        ISAP_K_128A,
        ISAP_A_128,
        ISAP_K_128;

    }
}

