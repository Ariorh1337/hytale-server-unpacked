/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.PhotonBeetleDigest;
import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Bytes;

public class PhotonBeetleEngine
extends AEADBaseEngine {
    private boolean input_empty;
    private byte[] K;
    private byte[] N;
    private byte[] state;
    private final int RATE_INBYTES_HALF;
    private final int STATE_INBYTES;
    private final int LAST_THREE_BITS_OFFSET;
    private static final int D = 8;
    private static final byte[][] RC = new byte[][]{{1, 3, 7, 14, 13, 11, 6, 12, 9, 2, 5, 10}, {0, 2, 6, 15, 12, 10, 7, 13, 8, 3, 4, 11}, {2, 0, 4, 13, 14, 8, 5, 15, 10, 1, 6, 9}, {6, 4, 0, 9, 10, 12, 1, 11, 14, 5, 2, 13}, {14, 12, 8, 1, 2, 4, 9, 3, 6, 13, 10, 5}, {15, 13, 9, 0, 3, 5, 8, 2, 7, 12, 11, 4}, {13, 15, 11, 2, 1, 7, 10, 0, 5, 14, 9, 6}, {9, 11, 15, 6, 5, 3, 14, 4, 1, 10, 13, 2}};
    private static final byte[][] MixColMatrix = new byte[][]{{2, 4, 2, 11, 2, 8, 5, 6}, {12, 9, 8, 13, 7, 7, 5, 2}, {4, 4, 13, 13, 9, 4, 13, 9}, {1, 6, 5, 1, 12, 13, 15, 14}, {15, 12, 9, 13, 14, 5, 14, 13}, {9, 14, 5, 15, 4, 12, 9, 6}, {12, 2, 2, 10, 3, 1, 1, 14}, {15, 1, 13, 10, 5, 10, 2, 3}};
    private static final byte[] sbox = new byte[]{12, 5, 6, 11, 9, 0, 10, 13, 3, 14, 15, 8, 4, 7, 1, 2};

    public PhotonBeetleEngine(PhotonBeetleParameters photonBeetleParameters) {
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        this.KEY_SIZE = 16;
        int n = 0;
        int n2 = 0;
        switch (photonBeetleParameters.ordinal()) {
            case 0: {
                n2 = 32;
                n = 224;
                break;
            }
            case 1: {
                n2 = 128;
                n = 128;
            }
        }
        this.AADBufferSize = this.BlockSize = n2 + 7 >>> 3;
        this.RATE_INBYTES_HALF = this.BlockSize >>> 1;
        int n3 = n2 + n;
        this.STATE_INBYTES = n3 + 7 >>> 3;
        this.LAST_THREE_BITS_OFFSET = n3 - (this.STATE_INBYTES - 1 << 3) - 3;
        this.algorithmName = "Photon-Beetle AEAD";
        this.state = new byte[this.STATE_INBYTES];
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Buffered, AEADBaseEngine.AADOperatorType.Counter, AEADBaseEngine.DataOperatorType.Counter);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.K = byArray;
        this.N = byArray2;
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        PhotonBeetleEngine.photonPermutation(this.state);
        Bytes.xorTo(this.BlockSize, byArray, n, this.state);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD3(state, bl);
    }

    @Override
    protected void processFinalAAD() {
        int n = this.aadOperator.getLen();
        if (n != 0) {
            if (this.m_aadPos != 0) {
                PhotonBeetleEngine.photonPermutation(this.state);
                Bytes.xorTo(this.m_aadPos, this.m_aad, this.state);
                if (this.m_aadPos < this.BlockSize) {
                    int n2 = this.m_aadPos;
                    this.state[n2] = (byte)(this.state[n2] ^ 1);
                }
            }
            int n3 = this.STATE_INBYTES - 1;
            this.state[n3] = (byte)(this.state[n3] ^ this.select(this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE) > 0, n % this.BlockSize == 0, (byte)3, (byte)4) << this.LAST_THREE_BITS_OFFSET);
        }
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.rhoohr(byArray2, n2, byArray, n, this.BlockSize);
        Bytes.xorTo(this.BlockSize, byArray, n, this.state);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.rhoohr(byArray2, n2, byArray, n, this.BlockSize);
        Bytes.xorTo(this.BlockSize, byArray2, n2, this.state);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        int n2 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
        int n3 = this.m_bufPos;
        int n4 = this.aadOperator.getLen();
        if (n4 != 0 || n2 != 0) {
            this.input_empty = false;
        }
        byte by = this.select(n4 != 0, n2 % this.BlockSize == 0, (byte)5, (byte)6);
        if (n2 != 0) {
            if (n3 != 0) {
                this.rhoohr(byArray, n, this.m_buf, 0, n3);
                if (this.forEncryption) {
                    Bytes.xorTo(n3, this.m_buf, this.state);
                } else {
                    Bytes.xorTo(n3, byArray, n, this.state);
                }
                if (n3 < this.BlockSize) {
                    int n5 = n3;
                    this.state[n5] = (byte)(this.state[n5] ^ 1);
                }
            }
            int n6 = this.STATE_INBYTES - 1;
            this.state[n6] = (byte)(this.state[n6] ^ by << this.LAST_THREE_BITS_OFFSET);
        } else if (this.input_empty) {
            int n7 = this.STATE_INBYTES - 1;
            this.state[n7] = (byte)(this.state[n7] ^ 1 << this.LAST_THREE_BITS_OFFSET);
        }
        PhotonBeetleEngine.photonPermutation(this.state);
        System.arraycopy(this.state, 0, this.mac, 0, this.MAC_SIZE);
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        this.input_empty = true;
        System.arraycopy(this.K, 0, this.state, 0, this.K.length);
        System.arraycopy(this.N, 0, this.state, this.K.length, this.N.length);
    }

    private static void photonPermutation(byte[] byArray) {
        int n;
        int n2 = 3;
        int n3 = 7;
        int n4 = 64;
        byte[][] byArray2 = new byte[8][8];
        for (n = 0; n < n4; ++n) {
            byArray2[n >>> n2][n & n3] = (byte)((byArray[n >> 1] & 0xFF) >>> 4 * (n & 1) & 0xF);
        }
        int n5 = 12;
        for (int i = 0; i < n5; ++i) {
            int n6;
            for (n = 0; n < 8; ++n) {
                byte[] byArray3 = byArray2[n];
                byArray3[0] = (byte)(byArray3[0] ^ RC[n][i]);
            }
            for (n = 0; n < 8; ++n) {
                for (n6 = 0; n6 < 8; ++n6) {
                    byArray2[n][n6] = sbox[byArray2[n][n6]];
                }
            }
            for (n = 1; n < 8; ++n) {
                System.arraycopy(byArray2[n], 0, byArray, 0, 8);
                System.arraycopy(byArray, n, byArray2[n], 0, 8 - n);
                System.arraycopy(byArray, 0, byArray2[n], 8 - n, n);
            }
            for (n6 = 0; n6 < 8; ++n6) {
                for (n = 0; n < 8; ++n) {
                    int n7;
                    int n8;
                    int n9 = 0;
                    for (int j = 0; j < 8; ++j) {
                        n8 = MixColMatrix[n][j];
                        n7 = byArray2[j][n6];
                        n9 ^= n8 * (n7 & 1);
                        n9 ^= n8 * (n7 & 2);
                        n9 ^= n8 * (n7 & 4);
                        n9 ^= n8 * (n7 & 8);
                    }
                    n8 = n9 >>> 4;
                    n9 = n9 & 0xF ^ n8 ^ n8 << 1;
                    n7 = n9 >>> 4;
                    n9 = n9 & 0xF ^ n7 ^ n7 << 1;
                    byArray[n] = (byte)n9;
                }
                for (n = 0; n < 8; ++n) {
                    byArray2[n][n6] = byArray[n];
                }
            }
        }
        for (n = 0; n < n4; n += 2) {
            byArray[n >>> 1] = (byte)(byArray2[n >>> n2][n & n3] & 0xF | (byArray2[n >>> n2][n + 1 & n3] & 0xF) << 4);
        }
    }

    private byte select(boolean bl, boolean bl2, byte by, byte by2) {
        if (bl && bl2) {
            return 1;
        }
        if (bl) {
            return 2;
        }
        if (bl2) {
            return by;
        }
        return by2;
    }

    private void rhoohr(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        int n4;
        PhotonBeetleEngine.photonPermutation(this.state);
        byte[] byArray3 = new byte[8];
        int n5 = Math.min(n3, this.RATE_INBYTES_HALF);
        for (n4 = 0; n4 < this.RATE_INBYTES_HALF - 1; ++n4) {
            byArray3[n4] = (byte)((this.state[n4] & 0xFF) >>> 1 | (this.state[n4 + 1] & 1) << 7);
        }
        byArray3[this.RATE_INBYTES_HALF - 1] = (byte)((this.state[n4] & 0xFF) >>> 1 | (this.state[0] & 1) << 7);
        Bytes.xor(n5, this.state, this.RATE_INBYTES_HALF, byArray2, n2, byArray, n);
        Bytes.xor(n3 - n5, byArray3, n5 - this.RATE_INBYTES_HALF, byArray2, n2 + n5, byArray, n + n5);
    }

    public static void photonPermutation(PhotonBeetleDigest.Friend friend, byte[] byArray) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by PhotonBeetleDigest");
        }
        PhotonBeetleEngine.photonPermutation(byArray);
    }

    public static enum PhotonBeetleParameters {
        pb32,
        pb128;

    }
}

