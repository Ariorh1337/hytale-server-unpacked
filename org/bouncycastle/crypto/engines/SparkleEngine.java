/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.SparkleDigest;
import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SparkleEngine
extends AEADBaseEngine {
    private static final int[] RCON = new int[]{-1209970334, -1083090816, 951376470, 844003128, -1156479509, 1333558103, -809524792, -1028445891};
    private final int[] state;
    private final int[] k;
    private final int[] npub;
    private boolean encrypted;
    private final int SPARKLE_STEPS_SLIM;
    private final int SPARKLE_STEPS_BIG;
    private final int KEY_WORDS;
    private final int TAG_WORDS;
    private final int STATE_WORDS;
    private final int RATE_WORDS;
    private final int CAP_MASK;
    private final int _A0;
    private final int _A1;
    private final int _M2;
    private final int _M3;

    public SparkleEngine(SparkleParameters sparkleParameters) {
        int n;
        int n2;
        int n3;
        int n4;
        int n5;
        switch (sparkleParameters.ordinal()) {
            case 0: {
                n5 = 128;
                n4 = 128;
                n3 = 128;
                n2 = 256;
                n = 128;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 10;
                this.algorithmName = "SCHWAEMM128-128";
                break;
            }
            case 1: {
                n5 = 128;
                n4 = 256;
                n3 = 128;
                n2 = 384;
                n = 128;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.algorithmName = "SCHWAEMM256-128";
                break;
            }
            case 2: {
                n5 = 192;
                n4 = 192;
                n3 = 192;
                n2 = 384;
                n = 192;
                this.SPARKLE_STEPS_SLIM = 7;
                this.SPARKLE_STEPS_BIG = 11;
                this.algorithmName = "SCHWAEMM192-192";
                break;
            }
            case 3: {
                n5 = 256;
                n4 = 256;
                n3 = 256;
                n2 = 512;
                n = 256;
                this.SPARKLE_STEPS_SLIM = 8;
                this.SPARKLE_STEPS_BIG = 12;
                this.algorithmName = "SCHWAEMM256-256";
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid definition of SCHWAEMM instance");
            }
        }
        this.KEY_WORDS = n5 >>> 5;
        this.KEY_SIZE = n5 >>> 3;
        this.TAG_WORDS = n3 >>> 5;
        this.MAC_SIZE = n3 >>> 3;
        this.STATE_WORDS = n2 >>> 5;
        this.RATE_WORDS = n4 >>> 5;
        this.IV_SIZE = n4 >>> 3;
        int n6 = n >>> 6;
        int n7 = n >>> 5;
        this.CAP_MASK = this.RATE_WORDS > n7 ? n7 - 1 : -1;
        this._A0 = 1 << n6 << 24;
        this._A1 = (1 ^ 1 << n6) << 24;
        this._M2 = (2 ^ 1 << n6) << 24;
        this._M3 = (3 ^ 1 << n6) << 24;
        this.state = new int[this.STATE_WORDS];
        this.k = new int[this.KEY_WORDS];
        this.npub = new int[this.RATE_WORDS];
        this.AADBufferSize = this.BlockSize = this.IV_SIZE;
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Default);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        Pack.littleEndianToInt(byArray, 0, this.k);
        Pack.littleEndianToInt(byArray2, 0, this.npub);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD2(state);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        if (this.encrypted || this.m_bufPos > 0) {
            int n2;
            int n3 = this.STATE_WORDS - 1;
            this.state[n3] = this.state[n3] ^ (this.m_bufPos < this.IV_SIZE ? this._M2 : this._M3);
            int[] nArray = new int[this.RATE_WORDS];
            for (n2 = 0; n2 < this.m_bufPos; ++n2) {
                int n4 = n2 >>> 2;
                nArray[n4] = nArray[n4] | (this.m_buf[n2] & 0xFF) << ((n2 & 3) << 3);
            }
            if (this.m_bufPos < this.IV_SIZE) {
                if (!this.forEncryption) {
                    n2 = (this.m_bufPos & 3) << 3;
                    int n5 = this.m_bufPos >>> 2;
                    nArray[n5] = nArray[n5] | this.state[this.m_bufPos >>> 2] >>> n2 << n2;
                    n2 = (this.m_bufPos >>> 2) + 1;
                    System.arraycopy(this.state, n2, nArray, n2, this.RATE_WORDS - n2);
                }
                int n6 = this.m_bufPos >>> 2;
                nArray[n6] = nArray[n6] ^ 128 << ((this.m_bufPos & 3) << 3);
            }
            n2 = 0;
            while (n2 < this.RATE_WORDS / 2) {
                int n7 = n2 + this.RATE_WORDS / 2;
                int n8 = this.state[n2];
                int n9 = this.state[n7];
                if (this.forEncryption) {
                    this.state[n2] = n9 ^ nArray[n2] ^ this.state[this.RATE_WORDS + n2];
                    this.state[n7] = n8 ^ n9 ^ nArray[n7] ^ this.state[this.RATE_WORDS + (n7 & this.CAP_MASK)];
                } else {
                    this.state[n2] = n8 ^ n9 ^ nArray[n2] ^ this.state[this.RATE_WORDS + n2];
                    this.state[n7] = n8 ^ nArray[n7] ^ this.state[this.RATE_WORDS + (n7 & this.CAP_MASK)];
                }
                int n10 = n2++;
                nArray[n10] = nArray[n10] ^ n8;
                int n11 = n7;
                nArray[n11] = nArray[n11] ^ n9;
            }
            for (n2 = 0; n2 < this.m_bufPos; ++n2) {
                byArray[n++] = (byte)(nArray[n2 >>> 2] >>> ((n2 & 3) << 3));
            }
            SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
        }
        for (int i = 0; i < this.KEY_WORDS; ++i) {
            int n12 = this.RATE_WORDS + i;
            this.state[n12] = this.state[n12] ^ this.k[i];
        }
        Pack.intToLittleEndian(this.state, this.RATE_WORDS, this.TAG_WORDS, this.mac, 0);
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        for (int i = 0; i < this.RATE_WORDS / 2; ++i) {
            int n2 = i + this.RATE_WORDS / 2;
            int n3 = this.state[i];
            int n4 = this.state[n2];
            int n5 = Pack.littleEndianToInt(byArray, n + i * 4);
            int n6 = Pack.littleEndianToInt(byArray, n + n2 * 4);
            this.state[i] = n4 ^ n5 ^ this.state[this.RATE_WORDS + i];
            this.state[n2] = n3 ^ n4 ^ n6 ^ this.state[this.RATE_WORDS + (n2 & this.CAP_MASK)];
        }
        SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        for (int i = 0; i < this.RATE_WORDS / 2; ++i) {
            int n3 = i + this.RATE_WORDS / 2;
            int n4 = this.state[i];
            int n5 = this.state[n3];
            int n6 = Pack.littleEndianToInt(byArray, n + i * 4);
            int n7 = Pack.littleEndianToInt(byArray, n + n3 * 4);
            this.state[i] = n4 ^ n5 ^ n6 ^ this.state[this.RATE_WORDS + i];
            this.state[n3] = n4 ^ n7 ^ this.state[this.RATE_WORDS + (n3 & this.CAP_MASK)];
            Pack.intToLittleEndian(n6 ^ n4, byArray2, n2 + i * 4);
            Pack.intToLittleEndian(n7 ^ n5, byArray2, n2 + n3 * 4);
        }
        SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
        this.encrypted = true;
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        for (int i = 0; i < this.RATE_WORDS / 2; ++i) {
            int n3 = i + this.RATE_WORDS / 2;
            int n4 = this.state[i];
            int n5 = this.state[n3];
            int n6 = Pack.littleEndianToInt(byArray, n + i * 4);
            int n7 = Pack.littleEndianToInt(byArray, n + n3 * 4);
            this.state[i] = n5 ^ n6 ^ this.state[this.RATE_WORDS + i];
            this.state[n3] = n4 ^ n5 ^ n7 ^ this.state[this.RATE_WORDS + (n3 & this.CAP_MASK)];
            Pack.intToLittleEndian(n6 ^ n4, byArray2, n2 + i * 4);
            Pack.intToLittleEndian(n7 ^ n5, byArray2, n2 + n3 * 4);
        }
        SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_SLIM);
        this.encrypted = true;
    }

    @Override
    protected void processFinalAAD() {
        if (this.m_aadPos < this.BlockSize) {
            int n = this.STATE_WORDS - 1;
            this.state[n] = this.state[n] ^ this._A0;
            this.m_aad[this.m_aadPos++] = -128;
            Arrays.fill(this.m_aad, this.m_aadPos, this.BlockSize, (byte)0);
        } else {
            int n = this.STATE_WORDS - 1;
            this.state[n] = this.state[n] ^ this._A1;
        }
        for (int i = 0; i < this.RATE_WORDS / 2; ++i) {
            int n = i + this.RATE_WORDS / 2;
            int n2 = this.state[i];
            int n3 = this.state[n];
            int n4 = Pack.littleEndianToInt(this.m_aad, i * 4);
            int n5 = Pack.littleEndianToInt(this.m_aad, n * 4);
            this.state[i] = n3 ^ n4 ^ this.state[this.RATE_WORDS + i];
            this.state[n] = n2 ^ n3 ^ n5 ^ this.state[this.RATE_WORDS + (n & this.CAP_MASK)];
        }
        SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
    }

    @Override
    protected void reset(boolean bl) {
        this.encrypted = false;
        System.arraycopy(this.npub, 0, this.state, 0, this.RATE_WORDS);
        System.arraycopy(this.k, 0, this.state, this.RATE_WORDS, this.KEY_WORDS);
        SparkleEngine.sparkle_opt(this.state, this.SPARKLE_STEPS_BIG);
        super.reset(bl);
    }

    private static int ELL(int n) {
        return Integers.rotateRight(n, 16) ^ n & 0xFFFF;
    }

    private static void sparkle_opt(int[] nArray, int n) {
        switch (nArray.length) {
            case 8: {
                SparkleEngine.sparkle_opt8(nArray, n);
                break;
            }
            case 12: {
                SparkleEngine.sparkle_opt12(nArray, n);
                break;
            }
            case 16: {
                SparkleEngine.sparkle_opt16(nArray, n);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }

    static void sparkle_opt8(int[] nArray, int n) {
        int n2 = nArray[0];
        int n3 = nArray[1];
        int n4 = nArray[2];
        int n5 = nArray[3];
        int n6 = nArray[4];
        int n7 = nArray[5];
        int n8 = nArray[6];
        int n9 = nArray[7];
        for (int i = 0; i < n; ++i) {
            n5 ^= i;
            int n10 = RCON[0];
            n2 += Integers.rotateRight(n3 ^= RCON[i & 7], 31);
            n3 ^= Integers.rotateRight(n2, 24);
            n2 ^= n10;
            n2 += Integers.rotateRight(n3, 17);
            n3 ^= Integers.rotateRight(n2, 17);
            n2 ^= n10;
            n2 += n3;
            n3 ^= Integers.rotateRight(n2, 31);
            n2 ^= n10;
            n2 += Integers.rotateRight(n3, 24);
            n3 ^= Integers.rotateRight(n2, 16);
            n2 ^= n10;
            n10 = RCON[1];
            n4 += Integers.rotateRight(n5, 31);
            n5 ^= Integers.rotateRight(n4, 24);
            n4 ^= n10;
            n4 += Integers.rotateRight(n5, 17);
            n5 ^= Integers.rotateRight(n4, 17);
            n4 ^= n10;
            n4 += n5;
            n5 ^= Integers.rotateRight(n4, 31);
            n4 ^= n10;
            n4 += Integers.rotateRight(n5, 24);
            n5 ^= Integers.rotateRight(n4, 16);
            n4 ^= n10;
            n10 = RCON[2];
            n6 += Integers.rotateRight(n7, 31);
            n7 ^= Integers.rotateRight(n6, 24);
            n6 ^= n10;
            n6 += Integers.rotateRight(n7, 17);
            n7 ^= Integers.rotateRight(n6, 17);
            n6 ^= n10;
            n6 += n7;
            n7 ^= Integers.rotateRight(n6, 31);
            n6 ^= n10;
            n6 += Integers.rotateRight(n7, 24);
            n7 ^= Integers.rotateRight(n6, 16);
            n6 ^= n10;
            n10 = RCON[3];
            n8 += Integers.rotateRight(n9, 31);
            n9 ^= Integers.rotateRight(n8, 24);
            n8 ^= n10;
            n8 += Integers.rotateRight(n9, 17);
            n9 ^= Integers.rotateRight(n8, 17);
            n8 ^= n10;
            n8 += n9;
            n9 ^= Integers.rotateRight(n8, 31);
            n8 ^= n10;
            n8 += Integers.rotateRight(n9, 24);
            n9 ^= Integers.rotateRight(n8, 16);
            n8 ^= n10;
            n10 = SparkleEngine.ELL(n2 ^ n4);
            int n11 = SparkleEngine.ELL(n3 ^ n5);
            int n12 = n2 ^ n6;
            int n13 = n3 ^ n7;
            int n14 = n4 ^ n8;
            int n15 = n5 ^ n9;
            n6 = n2;
            n7 = n3;
            n8 = n4;
            n9 = n5;
            n2 = n14 ^ n11;
            n3 = n15 ^ n10;
            n4 = n12 ^ n11;
            n5 = n13 ^ n10;
        }
        nArray[0] = n2;
        nArray[1] = n3;
        nArray[2] = n4;
        nArray[3] = n5;
        nArray[4] = n6;
        nArray[5] = n7;
        nArray[6] = n8;
        nArray[7] = n9;
    }

    static void sparkle_opt12(int[] nArray, int n) {
        int n2 = nArray[0];
        int n3 = nArray[1];
        int n4 = nArray[2];
        int n5 = nArray[3];
        int n6 = nArray[4];
        int n7 = nArray[5];
        int n8 = nArray[6];
        int n9 = nArray[7];
        int n10 = nArray[8];
        int n11 = nArray[9];
        int n12 = nArray[10];
        int n13 = nArray[11];
        for (int i = 0; i < n; ++i) {
            n5 ^= i;
            int n14 = RCON[0];
            n2 += Integers.rotateRight(n3 ^= RCON[i & 7], 31);
            n3 ^= Integers.rotateRight(n2, 24);
            n2 ^= n14;
            n2 += Integers.rotateRight(n3, 17);
            n3 ^= Integers.rotateRight(n2, 17);
            n2 ^= n14;
            n2 += n3;
            n3 ^= Integers.rotateRight(n2, 31);
            n2 ^= n14;
            n2 += Integers.rotateRight(n3, 24);
            n3 ^= Integers.rotateRight(n2, 16);
            n2 ^= n14;
            n14 = RCON[1];
            n4 += Integers.rotateRight(n5, 31);
            n5 ^= Integers.rotateRight(n4, 24);
            n4 ^= n14;
            n4 += Integers.rotateRight(n5, 17);
            n5 ^= Integers.rotateRight(n4, 17);
            n4 ^= n14;
            n4 += n5;
            n5 ^= Integers.rotateRight(n4, 31);
            n4 ^= n14;
            n4 += Integers.rotateRight(n5, 24);
            n5 ^= Integers.rotateRight(n4, 16);
            n4 ^= n14;
            n14 = RCON[2];
            n6 += Integers.rotateRight(n7, 31);
            n7 ^= Integers.rotateRight(n6, 24);
            n6 ^= n14;
            n6 += Integers.rotateRight(n7, 17);
            n7 ^= Integers.rotateRight(n6, 17);
            n6 ^= n14;
            n6 += n7;
            n7 ^= Integers.rotateRight(n6, 31);
            n6 ^= n14;
            n6 += Integers.rotateRight(n7, 24);
            n7 ^= Integers.rotateRight(n6, 16);
            n6 ^= n14;
            n14 = RCON[3];
            n8 += Integers.rotateRight(n9, 31);
            n9 ^= Integers.rotateRight(n8, 24);
            n8 ^= n14;
            n8 += Integers.rotateRight(n9, 17);
            n9 ^= Integers.rotateRight(n8, 17);
            n8 ^= n14;
            n8 += n9;
            n9 ^= Integers.rotateRight(n8, 31);
            n8 ^= n14;
            n8 += Integers.rotateRight(n9, 24);
            n9 ^= Integers.rotateRight(n8, 16);
            n8 ^= n14;
            n14 = RCON[4];
            n10 += Integers.rotateRight(n11, 31);
            n11 ^= Integers.rotateRight(n10, 24);
            n10 ^= n14;
            n10 += Integers.rotateRight(n11, 17);
            n11 ^= Integers.rotateRight(n10, 17);
            n10 ^= n14;
            n10 += n11;
            n11 ^= Integers.rotateRight(n10, 31);
            n10 ^= n14;
            n10 += Integers.rotateRight(n11, 24);
            n11 ^= Integers.rotateRight(n10, 16);
            n10 ^= n14;
            n14 = RCON[5];
            n12 += Integers.rotateRight(n13, 31);
            n13 ^= Integers.rotateRight(n12, 24);
            n12 ^= n14;
            n12 += Integers.rotateRight(n13, 17);
            n13 ^= Integers.rotateRight(n12, 17);
            n12 ^= n14;
            n12 += n13;
            n13 ^= Integers.rotateRight(n12, 31);
            n12 ^= n14;
            n12 += Integers.rotateRight(n13, 24);
            n13 ^= Integers.rotateRight(n12, 16);
            n12 ^= n14;
            n14 = SparkleEngine.ELL(n2 ^ n4 ^ n6);
            int n15 = SparkleEngine.ELL(n3 ^ n5 ^ n7);
            int n16 = n2 ^ n8;
            int n17 = n3 ^ n9;
            int n18 = n4 ^ n10;
            int n19 = n5 ^ n11;
            int n20 = n6 ^ n12;
            int n21 = n7 ^ n13;
            n8 = n2;
            n9 = n3;
            n10 = n4;
            n11 = n5;
            n12 = n6;
            n13 = n7;
            n2 = n18 ^ n15;
            n3 = n19 ^ n14;
            n4 = n20 ^ n15;
            n5 = n21 ^ n14;
            n6 = n16 ^ n15;
            n7 = n17 ^ n14;
        }
        nArray[0] = n2;
        nArray[1] = n3;
        nArray[2] = n4;
        nArray[3] = n5;
        nArray[4] = n6;
        nArray[5] = n7;
        nArray[6] = n8;
        nArray[7] = n9;
        nArray[8] = n10;
        nArray[9] = n11;
        nArray[10] = n12;
        nArray[11] = n13;
    }

    public static void sparkle_opt12(SparkleDigest.Friend friend, int[] nArray, int n) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by SparkleDigest");
        }
        SparkleEngine.sparkle_opt12(nArray, n);
    }

    static void sparkle_opt16(int[] nArray, int n) {
        int n2 = nArray[0];
        int n3 = nArray[1];
        int n4 = nArray[2];
        int n5 = nArray[3];
        int n6 = nArray[4];
        int n7 = nArray[5];
        int n8 = nArray[6];
        int n9 = nArray[7];
        int n10 = nArray[8];
        int n11 = nArray[9];
        int n12 = nArray[10];
        int n13 = nArray[11];
        int n14 = nArray[12];
        int n15 = nArray[13];
        int n16 = nArray[14];
        int n17 = nArray[15];
        for (int i = 0; i < n; ++i) {
            n5 ^= i;
            int n18 = RCON[0];
            n2 += Integers.rotateRight(n3 ^= RCON[i & 7], 31);
            n3 ^= Integers.rotateRight(n2, 24);
            n2 ^= n18;
            n2 += Integers.rotateRight(n3, 17);
            n3 ^= Integers.rotateRight(n2, 17);
            n2 ^= n18;
            n2 += n3;
            n3 ^= Integers.rotateRight(n2, 31);
            n2 ^= n18;
            n2 += Integers.rotateRight(n3, 24);
            n3 ^= Integers.rotateRight(n2, 16);
            n2 ^= n18;
            n18 = RCON[1];
            n4 += Integers.rotateRight(n5, 31);
            n5 ^= Integers.rotateRight(n4, 24);
            n4 ^= n18;
            n4 += Integers.rotateRight(n5, 17);
            n5 ^= Integers.rotateRight(n4, 17);
            n4 ^= n18;
            n4 += n5;
            n5 ^= Integers.rotateRight(n4, 31);
            n4 ^= n18;
            n4 += Integers.rotateRight(n5, 24);
            n5 ^= Integers.rotateRight(n4, 16);
            n4 ^= n18;
            n18 = RCON[2];
            n6 += Integers.rotateRight(n7, 31);
            n7 ^= Integers.rotateRight(n6, 24);
            n6 ^= n18;
            n6 += Integers.rotateRight(n7, 17);
            n7 ^= Integers.rotateRight(n6, 17);
            n6 ^= n18;
            n6 += n7;
            n7 ^= Integers.rotateRight(n6, 31);
            n6 ^= n18;
            n6 += Integers.rotateRight(n7, 24);
            n7 ^= Integers.rotateRight(n6, 16);
            n6 ^= n18;
            n18 = RCON[3];
            n8 += Integers.rotateRight(n9, 31);
            n9 ^= Integers.rotateRight(n8, 24);
            n8 ^= n18;
            n8 += Integers.rotateRight(n9, 17);
            n9 ^= Integers.rotateRight(n8, 17);
            n8 ^= n18;
            n8 += n9;
            n9 ^= Integers.rotateRight(n8, 31);
            n8 ^= n18;
            n8 += Integers.rotateRight(n9, 24);
            n9 ^= Integers.rotateRight(n8, 16);
            n8 ^= n18;
            n18 = RCON[4];
            n10 += Integers.rotateRight(n11, 31);
            n11 ^= Integers.rotateRight(n10, 24);
            n10 ^= n18;
            n10 += Integers.rotateRight(n11, 17);
            n11 ^= Integers.rotateRight(n10, 17);
            n10 ^= n18;
            n10 += n11;
            n11 ^= Integers.rotateRight(n10, 31);
            n10 ^= n18;
            n10 += Integers.rotateRight(n11, 24);
            n11 ^= Integers.rotateRight(n10, 16);
            n10 ^= n18;
            n18 = RCON[5];
            n12 += Integers.rotateRight(n13, 31);
            n13 ^= Integers.rotateRight(n12, 24);
            n12 ^= n18;
            n12 += Integers.rotateRight(n13, 17);
            n13 ^= Integers.rotateRight(n12, 17);
            n12 ^= n18;
            n12 += n13;
            n13 ^= Integers.rotateRight(n12, 31);
            n12 ^= n18;
            n12 += Integers.rotateRight(n13, 24);
            n13 ^= Integers.rotateRight(n12, 16);
            n12 ^= n18;
            n18 = RCON[6];
            n14 += Integers.rotateRight(n15, 31);
            n15 ^= Integers.rotateRight(n14, 24);
            n14 ^= n18;
            n14 += Integers.rotateRight(n15, 17);
            n15 ^= Integers.rotateRight(n14, 17);
            n14 ^= n18;
            n14 += n15;
            n15 ^= Integers.rotateRight(n14, 31);
            n14 ^= n18;
            n14 += Integers.rotateRight(n15, 24);
            n15 ^= Integers.rotateRight(n14, 16);
            n14 ^= n18;
            n18 = RCON[7];
            n16 += Integers.rotateRight(n17, 31);
            n17 ^= Integers.rotateRight(n16, 24);
            n16 ^= n18;
            n16 += Integers.rotateRight(n17, 17);
            n17 ^= Integers.rotateRight(n16, 17);
            n16 ^= n18;
            n16 += n17;
            n17 ^= Integers.rotateRight(n16, 31);
            n16 ^= n18;
            n16 += Integers.rotateRight(n17, 24);
            n17 ^= Integers.rotateRight(n16, 16);
            n16 ^= n18;
            n18 = SparkleEngine.ELL(n2 ^ n4 ^ n6 ^ n8);
            int n19 = SparkleEngine.ELL(n3 ^ n5 ^ n7 ^ n9);
            int n20 = n2 ^ n10;
            int n21 = n3 ^ n11;
            int n22 = n4 ^ n12;
            int n23 = n5 ^ n13;
            int n24 = n6 ^ n14;
            int n25 = n7 ^ n15;
            int n26 = n8 ^ n16;
            int n27 = n9 ^ n17;
            n10 = n2;
            n11 = n3;
            n12 = n4;
            n13 = n5;
            n14 = n6;
            n15 = n7;
            n16 = n8;
            n17 = n9;
            n2 = n22 ^ n19;
            n3 = n23 ^ n18;
            n4 = n24 ^ n19;
            n5 = n25 ^ n18;
            n6 = n26 ^ n19;
            n7 = n27 ^ n18;
            n8 = n20 ^ n19;
            n9 = n21 ^ n18;
        }
        nArray[0] = n2;
        nArray[1] = n3;
        nArray[2] = n4;
        nArray[3] = n5;
        nArray[4] = n6;
        nArray[5] = n7;
        nArray[6] = n8;
        nArray[7] = n9;
        nArray[8] = n10;
        nArray[9] = n11;
        nArray[10] = n12;
        nArray[11] = n13;
        nArray[12] = n14;
        nArray[13] = n15;
        nArray[14] = n16;
        nArray[15] = n17;
    }

    public static void sparkle_opt16(SparkleDigest.Friend friend, int[] nArray, int n) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by SparkleDigest");
        }
        SparkleEngine.sparkle_opt16(nArray, n);
    }

    public static enum SparkleParameters {
        SCHWAEMM128_128,
        SCHWAEMM256_128,
        SCHWAEMM192_192,
        SCHWAEMM256_256;

    }
}

