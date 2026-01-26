/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.saber;

import org.bouncycastle.pqc.crypto.saber.SABEREngine;

class Utils {
    private final int SABER_N;
    private final int SABER_L;
    private final int SABER_ET;
    private final int SABER_POLYBYTES;
    private final int SABER_EP;
    private final int SABER_KEYBYTES;
    private final boolean usingEffectiveMasking;

    public Utils(SABEREngine sABEREngine) {
        this.SABER_N = sABEREngine.getSABER_N();
        this.SABER_L = sABEREngine.getSABER_L();
        this.SABER_ET = sABEREngine.getSABER_ET();
        this.SABER_POLYBYTES = sABEREngine.getSABER_POLYBYTES();
        this.SABER_EP = sABEREngine.getSABER_EP();
        this.SABER_KEYBYTES = sABEREngine.getSABER_KEYBYTES();
        this.usingEffectiveMasking = sABEREngine.usingEffectiveMasking;
    }

    public void POLT2BS(byte[] byArray, int n, short[] sArray) {
        block4: {
            block5: {
                block3: {
                    if (this.SABER_ET != 3) break block3;
                    for (int n2 = 0; n2 < this.SABER_N / 8; n2 = (int)((short)(n2 + 1))) {
                        short s = (short)(3 * n2);
                        short s2 = (short)(8 * n2);
                        byArray[n + s + 0] = (byte)(sArray[s2 + 0] & 7 | (sArray[s2 + 1] & 7) << 3 | (sArray[s2 + 2] & 3) << 6);
                        byArray[n + s + 1] = (byte)(sArray[s2 + 2] >> 2 & 1 | (sArray[s2 + 3] & 7) << 1 | (sArray[s2 + 4] & 7) << 4 | (sArray[s2 + 5] & 1) << 7);
                        byArray[n + s + 2] = (byte)(sArray[s2 + 5] >> 1 & 3 | (sArray[s2 + 6] & 7) << 2 | (sArray[s2 + 7] & 7) << 5);
                    }
                    break block4;
                }
                if (this.SABER_ET != 4) break block5;
                for (int n3 = 0; n3 < this.SABER_N / 2; n3 = (int)((short)(n3 + 1))) {
                    int n4 = n3;
                    short s = (short)(2 * n3);
                    byArray[n + n4] = (byte)(sArray[s] & 0xF | (sArray[s + 1] & 0xF) << 4);
                }
                break block4;
            }
            if (this.SABER_ET != 6) break block4;
            for (int n5 = 0; n5 < this.SABER_N / 4; n5 = (int)((short)(n5 + 1))) {
                short s = (short)(3 * n5);
                short s3 = (short)(4 * n5);
                byArray[n + s + 0] = (byte)(sArray[s3 + 0] & 0x3F | (sArray[s3 + 1] & 3) << 6);
                byArray[n + s + 1] = (byte)(sArray[s3 + 1] >> 2 & 0xF | (sArray[s3 + 2] & 0xF) << 4);
                byArray[n + s + 2] = (byte)(sArray[s3 + 2] >> 4 & 3 | (sArray[s3 + 3] & 0x3F) << 2);
            }
        }
    }

    public void BS2POLT(byte[] byArray, int n, short[] sArray) {
        block4: {
            block5: {
                block3: {
                    if (this.SABER_ET != 3) break block3;
                    for (int n2 = 0; n2 < this.SABER_N / 8; n2 = (int)((short)(n2 + 1))) {
                        short s = (short)(3 * n2);
                        short s2 = (short)(8 * n2);
                        sArray[s2 + 0] = (short)(byArray[n + s + 0] & 7);
                        sArray[s2 + 1] = (short)(byArray[n + s + 0] >> 3 & 7);
                        sArray[s2 + 2] = (short)(byArray[n + s + 0] >> 6 & 3 | (byArray[n + s + 1] & 1) << 2);
                        sArray[s2 + 3] = (short)(byArray[n + s + 1] >> 1 & 7);
                        sArray[s2 + 4] = (short)(byArray[n + s + 1] >> 4 & 7);
                        sArray[s2 + 5] = (short)(byArray[n + s + 1] >> 7 & 1 | (byArray[n + s + 2] & 3) << 1);
                        sArray[s2 + 6] = (short)(byArray[n + s + 2] >> 2 & 7);
                        sArray[s2 + 7] = (short)(byArray[n + s + 2] >> 5 & 7);
                    }
                    break block4;
                }
                if (this.SABER_ET != 4) break block5;
                for (int n3 = 0; n3 < this.SABER_N / 2; n3 = (int)((short)(n3 + 1))) {
                    int n4 = n3;
                    short s = (short)(2 * n3);
                    sArray[s] = (short)(byArray[n + n4] & 0xF);
                    sArray[s + 1] = (short)(byArray[n + n4] >> 4 & 0xF);
                }
                break block4;
            }
            if (this.SABER_ET != 6) break block4;
            for (int n5 = 0; n5 < this.SABER_N / 4; n5 = (int)((short)(n5 + 1))) {
                short s = (short)(3 * n5);
                short s3 = (short)(4 * n5);
                sArray[s3 + 0] = (short)(byArray[n + s + 0] & 0x3F);
                sArray[s3 + 1] = (short)(byArray[n + s + 0] >> 6 & 3 | (byArray[n + s + 1] & 0xF) << 2);
                sArray[s3 + 2] = (short)((byArray[n + s + 1] & 0xFF) >> 4 | (byArray[n + s + 2] & 3) << 4);
                sArray[s3 + 3] = (short)((byArray[n + s + 2] & 0xFF) >> 2);
            }
        }
    }

    private void POLq2BS(byte[] byArray, int n, short[] sArray) {
        if (!this.usingEffectiveMasking) {
            for (int n2 = 0; n2 < this.SABER_N / 8; n2 = (int)((short)(n2 + 1))) {
                short s = (short)(13 * n2);
                short s2 = (short)(8 * n2);
                byArray[n + s + 0] = (byte)(sArray[s2 + 0] & 0xFF);
                byArray[n + s + 1] = (byte)(sArray[s2 + 0] >> 8 & 0x1F | (sArray[s2 + 1] & 7) << 5);
                byArray[n + s + 2] = (byte)(sArray[s2 + 1] >> 3 & 0xFF);
                byArray[n + s + 3] = (byte)(sArray[s2 + 1] >> 11 & 3 | (sArray[s2 + 2] & 0x3F) << 2);
                byArray[n + s + 4] = (byte)(sArray[s2 + 2] >> 6 & 0x7F | (sArray[s2 + 3] & 1) << 7);
                byArray[n + s + 5] = (byte)(sArray[s2 + 3] >> 1 & 0xFF);
                byArray[n + s + 6] = (byte)(sArray[s2 + 3] >> 9 & 0xF | (sArray[s2 + 4] & 0xF) << 4);
                byArray[n + s + 7] = (byte)(sArray[s2 + 4] >> 4 & 0xFF);
                byArray[n + s + 8] = (byte)(sArray[s2 + 4] >> 12 & 1 | (sArray[s2 + 5] & 0x7F) << 1);
                byArray[n + s + 9] = (byte)(sArray[s2 + 5] >> 7 & 0x3F | (sArray[s2 + 6] & 3) << 6);
                byArray[n + s + 10] = (byte)(sArray[s2 + 6] >> 2 & 0xFF);
                byArray[n + s + 11] = (byte)(sArray[s2 + 6] >> 10 & 7 | (sArray[s2 + 7] & 0x1F) << 3);
                byArray[n + s + 12] = (byte)(sArray[s2 + 7] >> 5 & 0xFF);
            }
        } else {
            for (int n3 = 0; n3 < this.SABER_N / 2; n3 = (int)((short)(n3 + 1))) {
                short s = (short)(3 * n3);
                short s3 = (short)(2 * n3);
                byArray[n + s + 0] = (byte)(sArray[s3 + 0] & 0xFF);
                byArray[n + s + 1] = (byte)(sArray[s3 + 0] >> 8 & 0xF | (sArray[s3 + 1] & 0xF) << 4);
                byArray[n + s + 2] = (byte)(sArray[s3 + 1] >> 4 & 0xFF);
            }
        }
    }

    private void BS2POLq(byte[] byArray, int n, short[] sArray) {
        if (!this.usingEffectiveMasking) {
            for (int n2 = 0; n2 < this.SABER_N / 8; n2 = (int)((short)(n2 + 1))) {
                short s = (short)(13 * n2);
                short s2 = (short)(8 * n2);
                sArray[s2 + 0] = (short)(byArray[n + s + 0] & 0xFF | (byArray[n + s + 1] & 0x1F) << 8);
                sArray[s2 + 1] = (short)(byArray[n + s + 1] >> 5 & 7 | (byArray[n + s + 2] & 0xFF) << 3 | (byArray[n + s + 3] & 3) << 11);
                sArray[s2 + 2] = (short)(byArray[n + s + 3] >> 2 & 0x3F | (byArray[n + s + 4] & 0x7F) << 6);
                sArray[s2 + 3] = (short)(byArray[n + s + 4] >> 7 & 1 | (byArray[n + s + 5] & 0xFF) << 1 | (byArray[n + s + 6] & 0xF) << 9);
                sArray[s2 + 4] = (short)(byArray[n + s + 6] >> 4 & 0xF | (byArray[n + s + 7] & 0xFF) << 4 | (byArray[n + s + 8] & 1) << 12);
                sArray[s2 + 5] = (short)(byArray[n + s + 8] >> 1 & 0x7F | (byArray[n + s + 9] & 0x3F) << 7);
                sArray[s2 + 6] = (short)(byArray[n + s + 9] >> 6 & 3 | (byArray[n + s + 10] & 0xFF) << 2 | (byArray[n + s + 11] & 7) << 10);
                sArray[s2 + 7] = (short)(byArray[n + s + 11] >> 3 & 0x1F | (byArray[n + s + 12] & 0xFF) << 5);
            }
        } else {
            for (int n3 = 0; n3 < this.SABER_N / 2; n3 = (int)((short)(n3 + 1))) {
                short s = (short)(3 * n3);
                short s3 = (short)(2 * n3);
                sArray[s3 + 0] = (short)(byArray[n + s + 0] & 0xFF | (byArray[n + s + 1] & 0xF) << 8);
                sArray[s3 + 1] = (short)(byArray[n + s + 1] >> 4 & 0xF | (byArray[n + s + 2] & 0xFF) << 4);
            }
        }
    }

    private void POLp2BS(byte[] byArray, int n, short[] sArray) {
        for (int n2 = 0; n2 < this.SABER_N / 4; n2 = (int)((short)(n2 + 1))) {
            short s = (short)(5 * n2);
            short s2 = (short)(4 * n2);
            byArray[n + s + 0] = (byte)(sArray[s2 + 0] & 0xFF);
            byArray[n + s + 1] = (byte)(sArray[s2 + 0] >> 8 & 3 | (sArray[s2 + 1] & 0x3F) << 2);
            byArray[n + s + 2] = (byte)(sArray[s2 + 1] >> 6 & 0xF | (sArray[s2 + 2] & 0xF) << 4);
            byArray[n + s + 3] = (byte)(sArray[s2 + 2] >> 4 & 0x3F | (sArray[s2 + 3] & 3) << 6);
            byArray[n + s + 4] = (byte)(sArray[s2 + 3] >> 2 & 0xFF);
        }
    }

    public void BS2POLp(byte[] byArray, int n, short[] sArray) {
        for (int n2 = 0; n2 < this.SABER_N / 4; n2 = (int)((short)(n2 + 1))) {
            short s = (short)(5 * n2);
            short s2 = (short)(4 * n2);
            sArray[s2 + 0] = (short)(byArray[n + s + 0] & 0xFF | (byArray[n + s + 1] & 3) << 8);
            sArray[s2 + 1] = (short)(byArray[n + s + 1] >> 2 & 0x3F | (byArray[n + s + 2] & 0xF) << 6);
            sArray[s2 + 2] = (short)(byArray[n + s + 2] >> 4 & 0xF | (byArray[n + s + 3] & 0x3F) << 4);
            sArray[s2 + 3] = (short)(byArray[n + s + 3] >> 6 & 3 | (byArray[n + s + 4] & 0xFF) << 2);
        }
    }

    public void POLVECq2BS(byte[] byArray, short[][] sArray) {
        for (int n = 0; n < this.SABER_L; n = (int)((byte)(n + 1))) {
            this.POLq2BS(byArray, n * this.SABER_POLYBYTES, sArray[n]);
        }
    }

    public void BS2POLVECq(byte[] byArray, int n, short[][] sArray) {
        for (int n2 = 0; n2 < this.SABER_L; n2 = (int)((byte)(n2 + 1))) {
            this.BS2POLq(byArray, n + n2 * this.SABER_POLYBYTES, sArray[n2]);
        }
    }

    public void POLVECp2BS(byte[] byArray, short[][] sArray) {
        for (int n = 0; n < this.SABER_L; n = (int)((byte)(n + 1))) {
            this.POLp2BS(byArray, n * (this.SABER_EP * this.SABER_N / 8), sArray[n]);
        }
    }

    public void BS2POLVECp(byte[] byArray, short[][] sArray) {
        for (int n = 0; n < this.SABER_L; n = (int)((byte)(n + 1))) {
            this.BS2POLp(byArray, n * (this.SABER_EP * this.SABER_N / 8), sArray[n]);
        }
    }

    public void BS2POLmsg(byte[] byArray, short[] sArray) {
        for (int n = 0; n < this.SABER_KEYBYTES; n = (int)((byte)(n + 1))) {
            for (int n2 = 0; n2 < 8; n2 = (int)((byte)(n2 + 1))) {
                sArray[n * 8 + n2] = (short)(byArray[n] >> n2 & 1);
            }
        }
    }

    public void POLmsg2BS(byte[] byArray, short[] sArray) {
        for (int n = 0; n < this.SABER_KEYBYTES; n = (int)((byte)(n + 1))) {
            for (int n2 = 0; n2 < 8; n2 = (int)((byte)(n2 + 1))) {
                byArray[n] = (byte)(byArray[n] | (sArray[n * 8 + n2] & 1) << n2);
            }
        }
    }
}

