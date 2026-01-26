/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.XoodyakDigest;
import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class XoodyakEngine
extends AEADBaseEngine {
    private final byte[] state;
    private int phase;
    private int mode;
    private static final int f_bPrime_1 = 47;
    private byte[] K;
    private byte[] iv;
    private static final int PhaseUp = 2;
    private static final int PhaseDown = 1;
    private static final int[] RC = new int[]{88, 56, 960, 208, 288, 20, 96, 44, 896, 240, 416, 18};
    private boolean encrypted;
    private byte aadcd;
    private static final int ModeKeyed = 0;
    private static final int ModeHash = 1;

    public XoodyakEngine() {
        this.algorithmName = "Xoodyak AEAD";
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        this.KEY_SIZE = 16;
        this.BlockSize = 24;
        this.AADBufferSize = 44;
        this.state = new byte[48];
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.K = byArray;
        this.iv = byArray2;
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        this.AbsorbAny(byArray, n, this.AADBufferSize, this.aadcd);
        this.aadcd = 0;
    }

    @Override
    protected void processFinalAAD() {
        this.AbsorbAny(this.m_aad, 0, this.m_aadPos, this.aadcd);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD3(state, bl);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        XoodyakEngine.up(this.mode, this.state, this.encrypted ? 0 : 128);
        Bytes.xor(this.BlockSize, this.state, byArray, n, byArray2, n2);
        XoodyakEngine.down(this.mode, this.state, byArray, n, this.BlockSize, 0);
        this.phase = 1;
        this.encrypted = true;
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        XoodyakEngine.up(this.mode, this.state, this.encrypted ? 0 : 128);
        Bytes.xor(this.BlockSize, this.state, byArray, n, byArray2, n2);
        XoodyakEngine.down(this.mode, this.state, byArray2, n2, this.BlockSize, 0);
        this.phase = 1;
        this.encrypted = true;
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        if (this.m_bufPos != 0 || !this.encrypted) {
            XoodyakEngine.up(this.mode, this.state, this.encrypted ? 0 : 128);
            Bytes.xor(this.m_bufPos, this.state, this.m_buf, 0, byArray, n);
            if (this.forEncryption) {
                XoodyakEngine.down(this.mode, this.state, this.m_buf, 0, this.m_bufPos, 0);
            } else {
                XoodyakEngine.down(this.mode, this.state, byArray, n, this.m_bufPos, 0);
            }
            this.phase = 1;
        }
        XoodyakEngine.up(this.mode, this.state, 64);
        System.arraycopy(this.state, 0, this.mac, 0, this.MAC_SIZE);
        this.phase = 2;
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        Arrays.fill(this.state, (byte)0);
        this.encrypted = false;
        this.phase = 2;
        this.aadcd = (byte)3;
        int n = this.K.length;
        int n2 = this.iv.length;
        byte[] byArray = new byte[this.AADBufferSize];
        this.mode = 0;
        System.arraycopy(this.K, 0, byArray, 0, n);
        System.arraycopy(this.iv, 0, byArray, n, n2);
        byArray[n + n2] = (byte)n2;
        this.AbsorbAny(byArray, 0, n + n2 + 1, 2);
    }

    private void AbsorbAny(byte[] byArray, int n, int n2, int n3) {
        int n4;
        if (this.phase != 2) {
            XoodyakEngine.up(this.mode, this.state, 0);
        }
        do {
            n4 = Math.min(n2, this.AADBufferSize);
            XoodyakEngine.down(this.mode, this.state, byArray, n, n4, n3);
            this.phase = 1;
            n3 = 0;
            n += n4;
        } while ((n2 -= n4) != 0);
    }

    public static void up(XoodyakDigest.Friend friend, int n, byte[] byArray, int n2) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by XoodyakDigest");
        }
        XoodyakEngine.up(n, byArray, n2);
    }

    private static void up(int n, byte[] byArray, int n2) {
        if (n != 1) {
            byArray[47] = (byte)(byArray[47] ^ n2);
        }
        int n3 = Pack.littleEndianToInt(byArray, 0);
        int n4 = Pack.littleEndianToInt(byArray, 4);
        int n5 = Pack.littleEndianToInt(byArray, 8);
        int n6 = Pack.littleEndianToInt(byArray, 12);
        int n7 = Pack.littleEndianToInt(byArray, 16);
        int n8 = Pack.littleEndianToInt(byArray, 20);
        int n9 = Pack.littleEndianToInt(byArray, 24);
        int n10 = Pack.littleEndianToInt(byArray, 28);
        int n11 = Pack.littleEndianToInt(byArray, 32);
        int n12 = Pack.littleEndianToInt(byArray, 36);
        int n13 = Pack.littleEndianToInt(byArray, 40);
        int n14 = Pack.littleEndianToInt(byArray, 44);
        for (int i = 0; i < 12; ++i) {
            int n15 = n3 ^ n7 ^ n11;
            int n16 = n4 ^ n8 ^ n12;
            int n17 = n5 ^ n9 ^ n13;
            int n18 = n6 ^ n10 ^ n14;
            int n19 = Integers.rotateLeft(n18, 5) ^ Integers.rotateLeft(n18, 14);
            int n20 = Integers.rotateLeft(n15, 5) ^ Integers.rotateLeft(n15, 14);
            int n21 = Integers.rotateLeft(n16, 5) ^ Integers.rotateLeft(n16, 14);
            int n22 = Integers.rotateLeft(n17, 5) ^ Integers.rotateLeft(n17, 14);
            n3 ^= n19;
            n7 ^= n19;
            n11 ^= n19;
            n4 ^= n20;
            n8 ^= n20;
            n12 ^= n20;
            n5 ^= n21;
            n9 ^= n21;
            n13 ^= n21;
            n6 ^= n22;
            n10 ^= n22;
            n14 ^= n22;
            int n23 = n3;
            int n24 = n4;
            int n25 = n5;
            int n26 = n6;
            int n27 = n10;
            int n28 = n7;
            int n29 = n8;
            int n30 = n9;
            int n31 = Integers.rotateLeft(n11, 11);
            int n32 = Integers.rotateLeft(n12, 11);
            int n33 = Integers.rotateLeft(n13, 11);
            int n34 = Integers.rotateLeft(n14, 11);
            n3 = (n23 ^= RC[i]) ^ ~n27 & n31;
            n4 = n24 ^ ~n28 & n32;
            n5 = n25 ^ ~n29 & n33;
            n6 = n26 ^ ~n30 & n34;
            n7 = n27 ^ ~n31 & n23;
            n8 = n28 ^ ~n32 & n24;
            n9 = n29 ^ ~n33 & n25;
            n10 = n30 ^ ~n34 & n26;
            n31 ^= ~n23 & n27;
            n32 ^= ~n24 & n28;
            n33 ^= ~n25 & n29;
            n34 ^= ~n26 & n30;
            n7 = Integers.rotateLeft(n7, 1);
            n8 = Integers.rotateLeft(n8, 1);
            n9 = Integers.rotateLeft(n9, 1);
            n10 = Integers.rotateLeft(n10, 1);
            n11 = Integers.rotateLeft(n33, 8);
            n12 = Integers.rotateLeft(n34, 8);
            n13 = Integers.rotateLeft(n31, 8);
            n14 = Integers.rotateLeft(n32, 8);
        }
        Pack.intToLittleEndian(n3, byArray, 0);
        Pack.intToLittleEndian(n4, byArray, 4);
        Pack.intToLittleEndian(n5, byArray, 8);
        Pack.intToLittleEndian(n6, byArray, 12);
        Pack.intToLittleEndian(n7, byArray, 16);
        Pack.intToLittleEndian(n8, byArray, 20);
        Pack.intToLittleEndian(n9, byArray, 24);
        Pack.intToLittleEndian(n10, byArray, 28);
        Pack.intToLittleEndian(n11, byArray, 32);
        Pack.intToLittleEndian(n12, byArray, 36);
        Pack.intToLittleEndian(n13, byArray, 40);
        Pack.intToLittleEndian(n14, byArray, 44);
    }

    public static void down(XoodyakDigest.Friend friend, int n, byte[] byArray, byte[] byArray2, int n2, int n3, int n4) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by XoodyakDigest");
        }
        XoodyakEngine.down(n, byArray, byArray2, n2, n3, n4);
    }

    private static void down(int n, byte[] byArray, byte[] byArray2, int n2, int n3, int n4) {
        Bytes.xorTo(n3, byArray2, n2, byArray);
        int n5 = n3;
        byArray[n5] = (byte)(byArray[n5] ^ 1);
        byArray[47] = (byte)(byArray[47] ^ (n == 1 ? n4 & 1 : n4));
    }
}

