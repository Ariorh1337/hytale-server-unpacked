/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Bytes;

public class GiftCofbEngine
extends AEADBaseEngine {
    private byte[] npub;
    private byte[] k;
    private byte[] Y;
    private byte[] input;
    private byte[] offset;
    private static final byte[] GIFT_RC = new byte[]{1, 3, 7, 15, 31, 62, 61, 59, 55, 47, 30, 60, 57, 51, 39, 14, 29, 58, 53, 43, 22, 44, 24, 48, 33, 2, 5, 11, 23, 46, 28, 56, 49, 35, 6, 13, 27, 54, 45, 26};

    public GiftCofbEngine() {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 16;
        this.MAC_SIZE = 16;
        this.BlockSize = 16;
        this.AADBufferSize = 16;
        this.algorithmName = "GIFT-COFB AEAD";
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Default, AEADBaseEngine.DataOperatorType.Counter);
    }

    private int rowperm(int n, int n2, int n3, int n4, int n5) {
        int n6 = 0;
        for (int i = 0; i < 8; ++i) {
            n6 |= (n >>> 4 * i & 1) << i + 8 * n2;
            n6 |= (n >>> 4 * i + 1 & 1) << i + 8 * n3;
            n6 |= (n >>> 4 * i + 2 & 1) << i + 8 * n4;
            n6 |= (n >>> 4 * i + 3 & 1) << i + 8 * n5;
        }
        return n6;
    }

    private void giftb128(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int[] nArray = new int[4];
        short[] sArray = new short[8];
        nArray[0] = (byArray[0] & 0xFF) << 24 | (byArray[1] & 0xFF) << 16 | (byArray[2] & 0xFF) << 8 | byArray[3] & 0xFF;
        nArray[1] = (byArray[4] & 0xFF) << 24 | (byArray[5] & 0xFF) << 16 | (byArray[6] & 0xFF) << 8 | byArray[7] & 0xFF;
        nArray[2] = (byArray[8] & 0xFF) << 24 | (byArray[9] & 0xFF) << 16 | (byArray[10] & 0xFF) << 8 | byArray[11] & 0xFF;
        nArray[3] = (byArray[12] & 0xFF) << 24 | (byArray[13] & 0xFF) << 16 | (byArray[14] & 0xFF) << 8 | byArray[15] & 0xFF;
        sArray[0] = (short)((byArray2[0] & 0xFF) << 8 | byArray2[1] & 0xFF);
        sArray[1] = (short)((byArray2[2] & 0xFF) << 8 | byArray2[3] & 0xFF);
        sArray[2] = (short)((byArray2[4] & 0xFF) << 8 | byArray2[5] & 0xFF);
        sArray[3] = (short)((byArray2[6] & 0xFF) << 8 | byArray2[7] & 0xFF);
        sArray[4] = (short)((byArray2[8] & 0xFF) << 8 | byArray2[9] & 0xFF);
        sArray[5] = (short)((byArray2[10] & 0xFF) << 8 | byArray2[11] & 0xFF);
        sArray[6] = (short)((byArray2[12] & 0xFF) << 8 | byArray2[13] & 0xFF);
        sArray[7] = (short)((byArray2[14] & 0xFF) << 8 | byArray2[15] & 0xFF);
        for (int i = 0; i < 40; ++i) {
            nArray[1] = nArray[1] ^ nArray[0] & nArray[2];
            nArray[0] = nArray[0] ^ nArray[1] & nArray[3];
            nArray[2] = nArray[2] ^ (nArray[0] | nArray[1]);
            nArray[3] = nArray[3] ^ nArray[2];
            nArray[1] = nArray[1] ^ nArray[3];
            nArray[3] = ~nArray[3];
            nArray[2] = nArray[2] ^ nArray[0] & nArray[1];
            int n = nArray[0];
            nArray[0] = nArray[3];
            nArray[3] = n;
            nArray[0] = this.rowperm(nArray[0], 0, 3, 2, 1);
            nArray[1] = this.rowperm(nArray[1], 1, 0, 3, 2);
            nArray[2] = this.rowperm(nArray[2], 2, 1, 0, 3);
            nArray[3] = this.rowperm(nArray[3], 3, 2, 1, 0);
            nArray[2] = nArray[2] ^ ((sArray[2] & 0xFFFF) << 16 | sArray[3] & 0xFFFF);
            nArray[1] = nArray[1] ^ ((sArray[6] & 0xFFFF) << 16 | sArray[7] & 0xFFFF);
            nArray[3] = nArray[3] ^ (Integer.MIN_VALUE ^ GIFT_RC[i] & 0xFF);
            short s = (short)((sArray[6] & 0xFFFF) >>> 2 | (sArray[6] & 0xFFFF) << 14);
            short s2 = (short)((sArray[7] & 0xFFFF) >>> 12 | (sArray[7] & 0xFFFF) << 4);
            sArray[7] = sArray[5];
            sArray[6] = sArray[4];
            sArray[5] = sArray[3];
            sArray[4] = sArray[2];
            sArray[3] = sArray[1];
            sArray[2] = sArray[0];
            sArray[1] = s2;
            sArray[0] = s;
        }
        byArray3[0] = (byte)(nArray[0] >>> 24);
        byArray3[1] = (byte)(nArray[0] >>> 16);
        byArray3[2] = (byte)(nArray[0] >>> 8);
        byArray3[3] = (byte)nArray[0];
        byArray3[4] = (byte)(nArray[1] >>> 24);
        byArray3[5] = (byte)(nArray[1] >>> 16);
        byArray3[6] = (byte)(nArray[1] >>> 8);
        byArray3[7] = (byte)nArray[1];
        byArray3[8] = (byte)(nArray[2] >>> 24);
        byArray3[9] = (byte)(nArray[2] >>> 16);
        byArray3[10] = (byte)(nArray[2] >>> 8);
        byArray3[11] = (byte)nArray[2];
        byArray3[12] = (byte)(nArray[3] >>> 24);
        byArray3[13] = (byte)(nArray[3] >>> 16);
        byArray3[14] = (byte)(nArray[3] >>> 8);
        byArray3[15] = (byte)nArray[3];
    }

    private void double_half_block(byte[] byArray) {
        int n = ((byArray[0] & 0xFF) >>> 7) * 27;
        for (int i = 0; i < 7; ++i) {
            byArray[i] = (byte)((byArray[i] & 0xFF) << 1 | (byArray[i + 1] & 0xFF) >>> 7);
        }
        byArray[7] = (byte)((byArray[7] & 0xFF) << 1 ^ n);
    }

    private void triple_half_block(byte[] byArray) {
        byte[] byArray2 = new byte[8];
        for (int i = 0; i < 7; ++i) {
            byArray2[i] = (byte)((byArray[i] & 0xFF) << 1 | (byArray[i + 1] & 0xFF) >>> 7);
        }
        byArray2[7] = (byte)((byArray[7] & 0xFF) << 1 ^ ((byArray[0] & 0xFF) >>> 7) * 27);
        Bytes.xorTo(8, byArray2, byArray);
    }

    private void pho1(byte[] byArray, byte[] byArray2, byte[] byArray3, int n, int n2) {
        byte[] byArray4 = new byte[16];
        byte[] byArray5 = new byte[16];
        if (n2 == 0) {
            byArray4[0] = -128;
        } else if (n2 < 16) {
            System.arraycopy(byArray3, n, byArray4, 0, n2);
            byArray4[n2] = -128;
        } else {
            System.arraycopy(byArray3, n, byArray4, 0, n2);
        }
        System.arraycopy(byArray2, 8, byArray5, 0, 8);
        for (int i = 0; i < 7; ++i) {
            byArray5[i + 8] = (byte)((byArray2[i] & 0xFF) << 1 | (byArray2[i + 1] & 0xFF) >>> 7);
        }
        byArray5[15] = (byte)((byArray2[7] & 0xFF) << 1 | (byArray2[0] & 0xFF) >>> 7);
        System.arraycopy(byArray5, 0, byArray2, 0, 16);
        Bytes.xor(16, byArray2, byArray4, byArray);
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        this.pho1(this.input, this.Y, byArray, n, 16);
        this.double_half_block(this.offset);
        Bytes.xorTo(8, this.offset, this.input);
        this.giftb128(this.input, this.k, this.Y);
    }

    @Override
    protected void processFinalAAD() {
        int n = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
        this.triple_half_block(this.offset);
        if ((this.m_aadPos & 0xF) != 0 || this.m_state == AEADBaseEngine.State.DecInit || this.m_state == AEADBaseEngine.State.EncInit) {
            this.triple_half_block(this.offset);
        }
        if (n == 0) {
            this.triple_half_block(this.offset);
            this.triple_half_block(this.offset);
        }
        this.pho1(this.input, this.Y, this.m_aad, 0, this.m_aadPos);
        Bytes.xorTo(8, this.offset, this.input);
        this.giftb128(this.input, this.k, this.Y);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD3(state, bl);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) {
        this.npub = byArray2;
        this.k = byArray;
        this.Y = new byte[this.BlockSize];
        this.input = new byte[16];
        this.offset = new byte[8];
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        int n2 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
        if (n2 != 0) {
            this.triple_half_block(this.offset);
            if ((n2 & 0xF) != 0) {
                this.triple_half_block(this.offset);
            }
            Bytes.xor(this.m_bufPos, this.Y, this.m_buf, 0, byArray, n);
            if (this.forEncryption) {
                this.pho1(this.input, this.Y, this.m_buf, 0, this.m_bufPos);
            } else {
                this.pho1(this.input, this.Y, byArray, n, this.m_bufPos);
            }
            Bytes.xorTo(8, this.offset, this.input);
            this.giftb128(this.input, this.k, this.Y);
        }
        System.arraycopy(this.Y, 0, this.mac, 0, this.BlockSize);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.double_half_block(this.offset);
        Bytes.xor(this.BlockSize, this.Y, byArray, n, byArray2, n2);
        this.pho1(this.input, this.Y, byArray, n, this.BlockSize);
        Bytes.xorTo(8, this.offset, this.input);
        this.giftb128(this.input, this.k, this.Y);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.double_half_block(this.offset);
        Bytes.xor(this.BlockSize, this.Y, byArray, n, byArray2, n2);
        this.pho1(this.input, this.Y, byArray2, n2, this.BlockSize);
        Bytes.xorTo(8, this.offset, this.input);
        this.giftb128(this.input, this.k, this.Y);
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        System.arraycopy(this.npub, 0, this.input, 0, this.IV_SIZE);
        this.giftb128(this.input, this.k, this.Y);
        System.arraycopy(this.Y, 0, this.offset, 0, 8);
    }
}

