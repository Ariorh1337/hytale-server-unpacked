/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.digests.RomulusDigest;
import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;

public class RomulusEngine
extends AEADBaseEngine {
    private byte[] k;
    private byte[] npub;
    private static final int AD_BLK_LEN_HALF = 16;
    private Instance instance;
    private final byte[] CNT;
    private static final byte[] sbox_8 = new byte[]{101, 76, 106, 66, 75, 99, 67, 107, 85, 117, 90, 122, 83, 115, 91, 123, 53, -116, 58, -127, -119, 51, -128, 59, -107, 37, -104, 42, -112, 35, -103, 43, -27, -52, -24, -63, -55, -32, -64, -23, -43, -11, -40, -8, -48, -16, -39, -7, -91, 28, -88, 18, 27, -96, 19, -87, 5, -75, 10, -72, 3, -80, 11, -71, 50, -120, 60, -123, -115, 52, -124, 61, -111, 34, -100, 44, -108, 36, -99, 45, 98, 74, 108, 69, 77, 100, 68, 109, 82, 114, 92, 124, 84, 116, 93, 125, -95, 26, -84, 21, 29, -92, 20, -83, 2, -79, 12, -68, 4, -76, 13, -67, -31, -56, -20, -59, -51, -28, -60, -19, -47, -15, -36, -4, -44, -12, -35, -3, 54, -114, 56, -126, -117, 48, -125, 57, -106, 38, -102, 40, -109, 32, -101, 41, 102, 78, 104, 65, 73, 96, 64, 105, 86, 118, 88, 120, 80, 112, 89, 121, -90, 30, -86, 17, 25, -93, 16, -85, 6, -74, 8, -70, 0, -77, 9, -69, -26, -50, -22, -62, -53, -29, -61, -21, -42, -10, -38, -6, -45, -13, -37, -5, 49, -118, 62, -122, -113, 55, -121, 63, -110, 33, -98, 46, -105, 39, -97, 47, 97, 72, 110, 70, 79, 103, 71, 111, 81, 113, 94, 126, 87, 119, 95, 127, -94, 24, -82, 22, 31, -89, 23, -81, 1, -78, 14, -66, 7, -73, 15, -65, -30, -54, -18, -58, -49, -25, -57, -17, -46, -14, -34, -2, -41, -9, -33, -1};
    private static final byte[] TWEAKEY_P = new byte[]{9, 15, 8, 13, 10, 14, 12, 11, 0, 1, 2, 3, 4, 5, 6, 7};
    private static final byte[] RC = new byte[]{1, 3, 7, 15, 31, 62, 61, 59, 55, 47, 30, 60, 57, 51, 39, 14, 29, 58, 53, 43, 22, 44, 24, 48, 33, 2, 5, 11, 23, 46, 28, 56, 49, 35, 6, 13, 27, 54, 45, 26};

    public RomulusEngine(RomulusParameters romulusParameters) {
        this.AADBufferSize = 16;
        this.BlockSize = 16;
        this.MAC_SIZE = 16;
        this.IV_SIZE = 16;
        this.KEY_SIZE = 16;
        this.CNT = new byte[7];
        switch (romulusParameters.ord) {
            case 0: {
                this.algorithmName = "Romulus-M";
                this.instance = new RomulusM();
                break;
            }
            case 1: {
                this.algorithmName = "Romulus-N";
                this.instance = new RomulusN();
                break;
            }
            case 2: {
                this.algorithmName = "Romulus-T";
                this.AADBufferSize = 32;
                this.instance = new RomulusT();
            }
        }
        this.setInnerMembers(romulusParameters == RomulusParameters.RomulusN ? AEADBaseEngine.ProcessingBufferType.Buffered : AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Counter, romulusParameters == RomulusParameters.RomulusM ? AEADBaseEngine.DataOperatorType.Stream : AEADBaseEngine.DataOperatorType.Counter);
    }

    private static void skinny_128_384_plus_enc(byte[] byArray, byte[] byArray2) {
        int n;
        int n2;
        byte[][] byArray3 = new byte[4][4];
        byte[][][] byArray4 = new byte[3][4][4];
        byte[][][] byArray5 = new byte[3][4][4];
        for (n2 = 0; n2 < 4; ++n2) {
            n = n2 << 2;
            System.arraycopy(byArray, n, byArray3[n2], 0, 4);
            System.arraycopy(byArray2, n, byArray4[0][n2], 0, 4);
            System.arraycopy(byArray2, n + 16, byArray4[1][n2], 0, 4);
            System.arraycopy(byArray2, n + 32, byArray4[2][n2], 0, 4);
        }
        for (int i = 0; i < 40; ++i) {
            byte by;
            int n3;
            for (n2 = 0; n2 < 4; ++n2) {
                for (n3 = 0; n3 < 4; ++n3) {
                    byArray3[n2][n3] = sbox_8[byArray3[n2][n3] & 0xFF];
                }
            }
            byte[] byArray6 = byArray3[0];
            byArray6[0] = (byte)(byArray6[0] ^ RC[i] & 0xF);
            byte[] byArray7 = byArray3[1];
            byArray7[0] = (byte)(byArray7[0] ^ RC[i] >>> 4 & 3);
            byte[] byArray8 = byArray3[2];
            byArray8[0] = (byte)(byArray8[0] ^ 2);
            for (n2 = 0; n2 <= 1; ++n2) {
                for (n3 = 0; n3 < 4; ++n3) {
                    byte[] byArray9 = byArray3[n2];
                    int n4 = n3;
                    byArray9[n4] = (byte)(byArray9[n4] ^ (byArray4[0][n2][n3] ^ byArray4[1][n2][n3] ^ byArray4[2][n2][n3]));
                }
            }
            for (n2 = 0; n2 < 4; ++n2) {
                for (n3 = 0; n3 < 4; ++n3) {
                    byte by2 = TWEAKEY_P[n3 + (n2 << 2)];
                    n = by2 >>> 2;
                    int n5 = by2 & 3;
                    byArray5[0][n2][n3] = byArray4[0][n][n5];
                    byArray5[1][n2][n3] = byArray4[1][n][n5];
                    byArray5[2][n2][n3] = byArray4[2][n][n5];
                }
            }
            for (n2 = 0; n2 <= 1; ++n2) {
                for (n3 = 0; n3 < 4; ++n3) {
                    byArray4[0][n2][n3] = byArray5[0][n2][n3];
                    by = byArray5[1][n2][n3];
                    byArray4[1][n2][n3] = (byte)(by << 1 & 0xFE ^ by >>> 7 & 1 ^ by >>> 5 & 1);
                    by = byArray5[2][n2][n3];
                    byArray4[2][n2][n3] = (byte)(by >>> 1 & 0x7F ^ by << 7 & 0x80 ^ by << 1 & 0x80);
                }
            }
            while (n2 < 4) {
                for (n3 = 0; n3 < 4; ++n3) {
                    byArray4[0][n2][n3] = byArray5[0][n2][n3];
                    byArray4[1][n2][n3] = byArray5[1][n2][n3];
                    byArray4[2][n2][n3] = byArray5[2][n2][n3];
                }
                ++n2;
            }
            by = byArray3[1][3];
            byArray3[1][3] = byArray3[1][2];
            byArray3[1][2] = byArray3[1][1];
            byArray3[1][1] = byArray3[1][0];
            byArray3[1][0] = by;
            by = byArray3[2][0];
            byArray3[2][0] = byArray3[2][2];
            byArray3[2][2] = by;
            by = byArray3[2][1];
            byArray3[2][1] = byArray3[2][3];
            byArray3[2][3] = by;
            by = byArray3[3][0];
            byArray3[3][0] = byArray3[3][1];
            byArray3[3][1] = byArray3[3][2];
            byArray3[3][2] = byArray3[3][3];
            byArray3[3][3] = by;
            for (n3 = 0; n3 < 4; ++n3) {
                byte[] byArray10 = byArray3[1];
                int n6 = n3;
                byArray10[n6] = (byte)(byArray10[n6] ^ byArray3[2][n3]);
                byte[] byArray11 = byArray3[2];
                int n7 = n3;
                byArray11[n7] = (byte)(byArray11[n7] ^ byArray3[0][n3]);
                byte[] byArray12 = byArray3[3];
                int n8 = n3;
                byArray12[n8] = (byte)(byArray12[n8] ^ byArray3[2][n3]);
                by = byArray3[3][n3];
                byArray3[3][n3] = byArray3[2][n3];
                byArray3[2][n3] = byArray3[1][n3];
                byArray3[1][n3] = byArray3[0][n3];
                byArray3[0][n3] = by;
            }
        }
        for (n2 = 0; n2 < 16; ++n2) {
            byArray[n2] = (byte)(byArray3[n2 >>> 2][n2 & 3] & 0xFF);
        }
    }

    void pad(byte[] byArray, int n, byte[] byArray2, int n2, int n3) {
        byArray2[n2 - 1] = (byte)(n3 & 0xF);
        System.arraycopy(byArray, n, byArray2, 0, n3);
    }

    void g8A(byte[] byArray, byte[] byArray2, int n) {
        int n2 = Math.min(byArray2.length - n, 16);
        for (int i = 0; i < n2; ++i) {
            byArray2[i + n] = (byte)((byArray[i] & 0xFF) >>> 1 ^ byArray[i] & 0x80 ^ (byArray[i] & 1) << 7);
        }
    }

    void rho(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3) {
        byte[] byArray4 = new byte[16];
        this.pad(byArray, n, byArray4, 16, n3);
        this.g8A(byArray3, byArray2, n2);
        if (this.forEncryption) {
            for (int i = 0; i < 16; ++i) {
                int n4 = i;
                byArray3[n4] = (byte)(byArray3[n4] ^ byArray4[i]);
                if (i < n3) {
                    int n5 = i + n2;
                    byArray2[n5] = (byte)(byArray2[n5] ^ byArray4[i]);
                    continue;
                }
                byArray2[i + n2] = 0;
            }
        } else {
            for (int i = 0; i < 16; ++i) {
                int n6 = i;
                byArray3[n6] = (byte)(byArray3[n6] ^ byArray4[i]);
                if (i >= n3 || i + n2 >= byArray2.length) continue;
                int n7 = i;
                byArray3[n7] = (byte)(byArray3[n7] ^ byArray2[i + n2]);
                int n8 = i + n2;
                byArray2[n8] = (byte)(byArray2[n8] ^ byArray4[i]);
            }
        }
    }

    void lfsr_gf56(byte[] byArray) {
        byte by = (byte)((byArray[6] & 0xFF) >>> 7);
        byArray[6] = (byte)((byArray[6] & 0xFF) << 1 | (byArray[5] & 0xFF) >>> 7);
        byArray[5] = (byte)((byArray[5] & 0xFF) << 1 | (byArray[4] & 0xFF) >>> 7);
        byArray[4] = (byte)((byArray[4] & 0xFF) << 1 | (byArray[3] & 0xFF) >>> 7);
        byArray[3] = (byte)((byArray[3] & 0xFF) << 1 | (byArray[2] & 0xFF) >>> 7);
        byArray[2] = (byte)((byArray[2] & 0xFF) << 1 | (byArray[1] & 0xFF) >>> 7);
        byArray[1] = (byte)((byArray[1] & 0xFF) << 1 | (byArray[0] & 0xFF) >>> 7);
        byArray[0] = by == 1 ? (byte)((byArray[0] & 0xFF) << 1 ^ 0x95) : (byte)((byArray[0] & 0xFF) << 1);
    }

    void block_cipher(byte[] byArray, byte[] byArray2, byte[] byArray3, int n, byte[] byArray4, byte by) {
        byte[] byArray5 = new byte[48];
        System.arraycopy(byArray4, 0, byArray5, 0, 7);
        byArray5[7] = by;
        System.arraycopy(byArray3, n, byArray5, 16, 16);
        System.arraycopy(byArray2, 0, byArray5, 32, 16);
        RomulusEngine.skinny_128_384_plus_enc(byArray, byArray5);
    }

    private void reset_lfsr_gf56(byte[] byArray) {
        byArray[0] = 1;
        Arrays.fill(byArray, 1, 7, (byte)0);
    }

    public static void hirose_128_128_256(RomulusDigest.Friend friend, byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by RomulusDigest");
        }
        RomulusEngine.hirose_128_128_256(byArray, byArray2, byArray3, n);
    }

    static void hirose_128_128_256(byte[] byArray, byte[] byArray2, byte[] byArray3, int n) {
        byte[] byArray4 = new byte[48];
        byte[] byArray5 = new byte[16];
        System.arraycopy(byArray2, 0, byArray4, 0, 16);
        System.arraycopy(byArray, 0, byArray2, 0, 16);
        System.arraycopy(byArray, 0, byArray5, 0, 16);
        byArray2[0] = (byte)(byArray2[0] ^ 1);
        System.arraycopy(byArray3, n, byArray4, 16, 32);
        RomulusEngine.skinny_128_384_plus_enc(byArray, byArray4);
        RomulusEngine.skinny_128_384_plus_enc(byArray2, byArray4);
        for (int i = 0; i < 16; ++i) {
            int n2 = i;
            byArray[n2] = (byte)(byArray[n2] ^ byArray5[i]);
            int n3 = i;
            byArray2[n3] = (byte)(byArray2[n3] ^ byArray5[i]);
        }
        byArray2[0] = (byte)(byArray2[0] ^ 1);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.npub = byArray2;
        this.k = byArray;
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD1(state);
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
        this.instance.processBufferAAD(byArray, n);
    }

    @Override
    protected void processFinalAAD() {
        this.instance.processFinalAAD();
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        this.instance.processFinalBlock(byArray, n);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.instance.processBufferEncrypt(byArray, n, byArray2, n2);
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.instance.processBufferDecrypt(byArray, n, byArray2, n2);
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        this.instance.reset();
    }

    private static interface Instance {
        public void processFinalBlock(byte[] var1, int var2);

        public void processBufferAAD(byte[] var1, int var2);

        public void processFinalAAD();

        public void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4);

        public void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4);

        public void reset();
    }

    private class RomulusM
    implements Instance {
        private final byte[] mac_s = new byte[16];
        private final byte[] mac_CNT = new byte[7];
        private final byte[] s = new byte[16];
        private int offset;
        private boolean twist = true;

        @Override
        public void processFinalBlock(byte[] byArray, int n) {
            int n2;
            byte[] byArray2;
            byte by = 48;
            int n3 = RomulusEngine.this.aadOperator.getLen();
            int n4 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
            byte[] byArray3 = ((AEADBaseEngine.StreamDataOperator)RomulusEngine.this.dataOperator).getBytes();
            int n5 = 0;
            int n6 = n;
            int n7 = n4;
            if ((n3 & 0x1F) == 0 && n3 != 0) {
                by = (byte)(by ^ 8);
            } else if ((n3 & 0x1F) < 16) {
                by = (byte)(by ^ 2);
            } else if ((n3 & 0x1F) != 16) {
                by = (byte)(by ^ 0xA);
            }
            if ((n4 & 0x1F) == 0 && n4 != 0) {
                by = (byte)(by ^ 4);
            } else if ((n4 & 0x1F) < 16) {
                by = (byte)(by ^ 1);
            } else if ((n4 & 0x1F) != 16) {
                by = (byte)(by ^ 5);
            }
            if (RomulusEngine.this.forEncryption) {
                if ((by & 8) == 0) {
                    byArray2 = new byte[16];
                    n2 = Math.min(n7, 16);
                    n7 -= n2;
                    RomulusEngine.this.pad(byArray3, n5, byArray2, 16, n2);
                    RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, byArray2, 0, this.mac_CNT, (byte)44);
                    RomulusEngine.this.lfsr_gf56(this.mac_CNT);
                    n5 += n2;
                } else if (n4 == 0) {
                    RomulusEngine.this.lfsr_gf56(this.mac_CNT);
                }
                while (n7 > 0) {
                    this.offset = n5;
                    n7 = this.ad_encryption(byArray3, n5, this.mac_s, RomulusEngine.this.k, n7, this.mac_CNT);
                    n5 = this.offset;
                }
                RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, this.mac_CNT, by);
                RomulusEngine.this.g8A(this.mac_s, RomulusEngine.this.mac, 0);
                n5 -= n4;
            } else {
                System.arraycopy(byArray3, n4, RomulusEngine.this.mac, 0, RomulusEngine.this.MAC_SIZE);
            }
            RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
            System.arraycopy(RomulusEngine.this.mac, 0, this.s, 0, 16);
            if (n4 > 0) {
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)36);
                while (n4 > 16) {
                    n4 -= 16;
                    RomulusEngine.this.rho(byArray3, n5, byArray, n, this.s, 16);
                    n += 16;
                    n5 += 16;
                    RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
                    RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)36);
                }
                RomulusEngine.this.rho(byArray3, n5, byArray, n, this.s, n4);
            }
            if (!RomulusEngine.this.forEncryption) {
                if ((by & 8) == 0) {
                    byArray2 = new byte[16];
                    n2 = Math.min(n7, 16);
                    n7 -= n2;
                    RomulusEngine.this.pad(byArray, n6, byArray2, 16, n2);
                    RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, byArray2, 0, this.mac_CNT, (byte)44);
                    RomulusEngine.this.lfsr_gf56(this.mac_CNT);
                    n6 += n2;
                } else if (n4 == 0) {
                    RomulusEngine.this.lfsr_gf56(this.mac_CNT);
                }
                while (n7 > 0) {
                    this.offset = n6;
                    n7 = this.ad_encryption(byArray, n6, this.mac_s, RomulusEngine.this.k, n7, this.mac_CNT);
                    n6 = this.offset;
                }
                RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, this.mac_CNT, by);
                RomulusEngine.this.g8A(this.mac_s, RomulusEngine.this.mac, 0);
                System.arraycopy(byArray3, RomulusEngine.this.dataOperator.getLen() - RomulusEngine.this.MAC_SIZE, RomulusEngine.this.m_buf, 0, RomulusEngine.this.MAC_SIZE);
                RomulusEngine.this.m_bufPos = 0;
            }
        }

        int ad_encryption(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, byte[] byArray4) {
            byte[] byArray5 = new byte[16];
            byte[] byArray6 = new byte[16];
            int n3 = 16;
            int n4 = Math.min(n2, n3);
            RomulusEngine.this.pad(byArray, n, byArray6, n3, n4);
            Bytes.xorTo(n3, byArray6, byArray2);
            this.offset = n += n4;
            RomulusEngine.this.lfsr_gf56(byArray4);
            if ((n2 -= n4) != 0) {
                n4 = Math.min(n2, n3);
                n2 -= n4;
                RomulusEngine.this.pad(byArray, n, byArray5, n3, n4);
                this.offset = n + n4;
                RomulusEngine.this.block_cipher(byArray2, byArray3, byArray5, 0, byArray4, (byte)44);
                RomulusEngine.this.lfsr_gf56(byArray4);
            }
            return n2;
        }

        @Override
        public void processBufferAAD(byte[] byArray, int n) {
            if (this.twist) {
                Bytes.xorTo(RomulusEngine.this.MAC_SIZE, byArray, n, this.mac_s);
            } else {
                RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, byArray, n, this.mac_CNT, (byte)40);
            }
            this.twist = !this.twist;
            RomulusEngine.this.lfsr_gf56(this.mac_CNT);
        }

        @Override
        public void processFinalAAD() {
            if (RomulusEngine.this.aadOperator.getLen() == 0) {
                RomulusEngine.this.lfsr_gf56(this.mac_CNT);
            } else if (RomulusEngine.this.m_aadPos != 0) {
                Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize - 1, (byte)0);
                RomulusEngine.this.m_aad[RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 0xF);
                if (this.twist) {
                    Bytes.xorTo(RomulusEngine.this.BlockSize, RomulusEngine.this.m_aad, this.mac_s);
                } else {
                    RomulusEngine.this.block_cipher(this.mac_s, RomulusEngine.this.k, RomulusEngine.this.m_aad, 0, this.mac_CNT, (byte)40);
                }
                RomulusEngine.this.lfsr_gf56(this.mac_CNT);
            }
            RomulusEngine.this.m_aadPos = 0;
            RomulusEngine.this.m_bufPos = RomulusEngine.this.dataOperator.getLen();
        }

        @Override
        public void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        }

        @Override
        public void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        }

        @Override
        public void reset() {
            Arrays.clear(this.s);
            Arrays.clear(this.mac_s);
            RomulusEngine.this.reset_lfsr_gf56(this.mac_CNT);
            RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
            this.twist = true;
        }
    }

    private class RomulusN
    implements Instance {
        private final byte[] s = new byte[16];
        boolean twist;

        @Override
        public void processFinalBlock(byte[] byArray, int n) {
            int n2 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
            if (n2 == 0) {
                RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)21);
            } else if (RomulusEngine.this.m_bufPos != 0) {
                int n3 = Math.min(RomulusEngine.this.m_bufPos, 16);
                RomulusEngine.this.rho(RomulusEngine.this.m_buf, 0, byArray, n, this.s, n3);
                RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, RomulusEngine.this.m_bufPos == 16 ? (byte)20 : 21);
            }
            RomulusEngine.this.g8A(this.s, RomulusEngine.this.mac, 0);
        }

        @Override
        public void processBufferAAD(byte[] byArray, int n) {
            if (this.twist) {
                Bytes.xorTo(16, byArray, n, this.s);
            } else {
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, byArray, n, RomulusEngine.this.CNT, (byte)8);
            }
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            this.twist = !this.twist;
        }

        @Override
        public void processFinalAAD() {
            if (RomulusEngine.this.m_aadPos != 0) {
                byte[] byArray = new byte[16];
                int n = Math.min(RomulusEngine.this.m_aadPos, 16);
                RomulusEngine.this.pad(RomulusEngine.this.m_aad, 0, byArray, 16, n);
                if (this.twist) {
                    Bytes.xorTo(16, byArray, this.s);
                } else {
                    RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, byArray, 0, RomulusEngine.this.CNT, (byte)8);
                }
                RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            }
            if (RomulusEngine.this.aadOperator.getLen() == 0) {
                RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)26);
            } else if ((RomulusEngine.this.m_aadPos & 0xF) != 0) {
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)26);
            } else {
                RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)24);
            }
            RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
        }

        @Override
        public void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
            RomulusEngine.this.g8A(this.s, byArray2, n2);
            for (int i = 0; i < 16; ++i) {
                int n3 = i;
                this.s[n3] = (byte)(this.s[n3] ^ byArray[i + n]);
                int n4 = i + n2;
                byArray2[n4] = (byte)(byArray2[n4] ^ byArray[i + n]);
            }
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)4);
        }

        @Override
        public void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
            RomulusEngine.this.g8A(this.s, byArray2, n2);
            for (int i = 0; i < 16; ++i) {
                int n3 = i + n2;
                byArray2[n3] = (byte)(byArray2[n3] ^ byArray[i + n]);
                int n4 = i;
                this.s[n4] = (byte)(this.s[n4] ^ byArray2[i + n2]);
            }
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
            RomulusEngine.this.block_cipher(this.s, RomulusEngine.this.k, RomulusEngine.this.npub, 0, RomulusEngine.this.CNT, (byte)4);
        }

        @Override
        public void reset() {
            Arrays.clear(this.s);
            RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
            this.twist = true;
        }
    }

    public static class RomulusParameters {
        public static final int ROMULUS_M = 0;
        public static final int ROMULUS_N = 1;
        public static final int ROMULUS_T = 2;
        public static final RomulusParameters RomulusM = new RomulusParameters(0);
        public static final RomulusParameters RomulusN = new RomulusParameters(1);
        public static final RomulusParameters RomulusT = new RomulusParameters(2);
        private final int ord;

        RomulusParameters(int n) {
            this.ord = n;
        }
    }

    private class RomulusT
    implements Instance {
        private final byte[] h = new byte[16];
        private final byte[] g = new byte[16];
        byte[] Z = new byte[16];
        byte[] CNT_Z = new byte[7];
        byte[] LR = new byte[32];
        byte[] T = new byte[16];
        byte[] S = new byte[16];

        private RomulusT() {
        }

        @Override
        public void processFinalBlock(byte[] byArray, int n) {
            int n2 = 16;
            int n3 = RomulusEngine.this.dataOperator.getLen() - (RomulusEngine.this.forEncryption ? 0 : RomulusEngine.this.MAC_SIZE);
            if (RomulusEngine.this.m_bufPos != 0) {
                int n4;
                byte[] byArray2;
                int n5 = Math.min(RomulusEngine.this.m_bufPos, 16);
                System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
                RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)64);
                Bytes.xor(n5, RomulusEngine.this.m_buf, this.S, byArray, n);
                System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
                RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
                if (RomulusEngine.this.forEncryption) {
                    byArray2 = byArray;
                    n4 = n;
                } else {
                    byArray2 = RomulusEngine.this.m_buf;
                    n4 = 0;
                }
                System.arraycopy(byArray2, n4, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.m_bufPos);
                Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos + RomulusEngine.this.m_bufPos, RomulusEngine.this.AADBufferSize - 1, (byte)0);
                RomulusEngine.this.m_aad[RomulusEngine.this.m_aadPos + RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_bufPos & 0xF);
                if (RomulusEngine.this.m_aadPos == 0) {
                    System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.BlockSize);
                    n2 = 0;
                }
                RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
                RomulusEngine.this.lfsr_gf56(this.CNT_Z);
            } else if (RomulusEngine.this.m_aadPos != 0) {
                if (n3 > 0) {
                    Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.AADBufferSize, (byte)0);
                } else if (RomulusEngine.this.aadOperator.getLen() != 0) {
                    System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, 16);
                    n2 = 0;
                    RomulusEngine.this.m_aadPos = 0;
                }
                RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            } else if (n3 > 0) {
                Arrays.fill(RomulusEngine.this.m_aad, 0, RomulusEngine.this.BlockSize, (byte)0);
                System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, RomulusEngine.this.BlockSize, RomulusEngine.this.BlockSize);
                n2 = 0;
                RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            }
            if (n2 == 16) {
                System.arraycopy(RomulusEngine.this.npub, 0, RomulusEngine.this.m_aad, 0, 16);
                System.arraycopy(RomulusEngine.this.CNT, 0, RomulusEngine.this.m_aad, 16, 7);
                Arrays.fill(RomulusEngine.this.m_aad, 23, 31, (byte)0);
                RomulusEngine.this.m_aad[31] = 23;
            } else {
                System.arraycopy(this.CNT_Z, 0, RomulusEngine.this.m_aad, 0, 7);
                Arrays.fill(RomulusEngine.this.m_aad, 7, 31, (byte)0);
                RomulusEngine.this.m_aad[31] = 7;
            }
            this.h[0] = (byte)(this.h[0] ^ 2);
            RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
            System.arraycopy(this.h, 0, this.LR, 0, 16);
            System.arraycopy(this.g, 0, this.LR, 16, 16);
            Arrays.clear(this.CNT_Z);
            RomulusEngine.this.block_cipher(this.LR, RomulusEngine.this.k, this.LR, 16, this.CNT_Z, (byte)68);
            System.arraycopy(this.LR, 0, RomulusEngine.this.mac, 0, RomulusEngine.this.MAC_SIZE);
        }

        @Override
        public void processBufferAAD(byte[] byArray, int n) {
            RomulusEngine.hirose_128_128_256(this.h, this.g, byArray, n);
        }

        @Override
        public void processFinalAAD() {
            Arrays.fill(RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.AADBufferSize - 1, (byte)0);
            if (RomulusEngine.this.m_aadPos >= 16) {
                RomulusEngine.this.m_aad[RomulusEngine.this.AADBufferSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 0xF);
                RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
                RomulusEngine.this.m_aadPos = 0;
            } else if (RomulusEngine.this.m_aadPos >= 0 && RomulusEngine.this.aadOperator.getLen() != 0) {
                RomulusEngine.this.m_aad[RomulusEngine.this.BlockSize - 1] = (byte)(RomulusEngine.this.m_aadPos & 0xF);
                RomulusEngine.this.m_aadPos = RomulusEngine.this.BlockSize;
            }
        }

        private void processBuffer(byte[] byArray, int n, byte[] byArray2, int n2) {
            System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
            RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)64);
            Bytes.xor(16, this.S, byArray, n, byArray2, n2);
            System.arraycopy(RomulusEngine.this.npub, 0, this.S, 0, 16);
            RomulusEngine.this.block_cipher(this.S, this.Z, this.T, 0, RomulusEngine.this.CNT, (byte)65);
            System.arraycopy(this.S, 0, this.Z, 0, 16);
            RomulusEngine.this.lfsr_gf56(RomulusEngine.this.CNT);
        }

        private void processAfterAbsorbCiphertext() {
            if (RomulusEngine.this.m_aadPos == RomulusEngine.this.BlockSize) {
                RomulusEngine.hirose_128_128_256(this.h, this.g, RomulusEngine.this.m_aad, 0);
                RomulusEngine.this.m_aadPos = 0;
            } else {
                RomulusEngine.this.m_aadPos = RomulusEngine.this.BlockSize;
            }
            RomulusEngine.this.lfsr_gf56(this.CNT_Z);
        }

        @Override
        public void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
            this.processBuffer(byArray, n, byArray2, n2);
            System.arraycopy(byArray2, n2, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize);
            this.processAfterAbsorbCiphertext();
        }

        @Override
        public void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
            this.processBuffer(byArray, n, byArray2, n2);
            System.arraycopy(byArray, n, RomulusEngine.this.m_aad, RomulusEngine.this.m_aadPos, RomulusEngine.this.BlockSize);
            this.processAfterAbsorbCiphertext();
        }

        @Override
        public void reset() {
            Arrays.clear(this.h);
            Arrays.clear(this.g);
            Arrays.clear(this.LR);
            Arrays.clear(this.T);
            Arrays.clear(this.S);
            Arrays.clear(this.CNT_Z);
            RomulusEngine.this.reset_lfsr_gf56(RomulusEngine.this.CNT);
            System.arraycopy(RomulusEngine.this.npub, 0, this.Z, 0, RomulusEngine.this.IV_SIZE);
            RomulusEngine.this.block_cipher(this.Z, RomulusEngine.this.k, this.T, 0, this.CNT_Z, (byte)66);
            RomulusEngine.this.reset_lfsr_gf56(this.CNT_Z);
        }
    }
}

