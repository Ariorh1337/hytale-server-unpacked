/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowUtil;
import org.bouncycastle.pqc.crypto.rainbow.Version;
import org.bouncycastle.util.Arrays;

public class RainbowPublicKeyParameters
extends RainbowKeyParameters {
    short[][][] pk;
    byte[] pk_seed;
    short[][][] l1_Q3;
    short[][][] l1_Q5;
    short[][][] l1_Q6;
    short[][][] l1_Q9;
    short[][][] l2_Q9;

    RainbowPublicKeyParameters(RainbowParameters rainbowParameters, short[][][] sArray, short[][][] sArray2, short[][][] sArray3, short[][][] sArray4, short[][][] sArray5, short[][][] sArray6, short[][][] sArray7, short[][][] sArray8, short[][][] sArray9, short[][][] sArray10, short[][][] sArray11, short[][][] sArray12) {
        super(false, rainbowParameters);
        int n;
        int n2;
        int n3 = rainbowParameters.getV1();
        int n4 = rainbowParameters.getO1();
        int n5 = rainbowParameters.getO2();
        this.pk = new short[rainbowParameters.getM()][rainbowParameters.getN()][rainbowParameters.getN()];
        for (n2 = 0; n2 < n4; ++n2) {
            for (n = 0; n < n3; ++n) {
                System.arraycopy(sArray[n2][n], 0, this.pk[n2][n], 0, n3);
                System.arraycopy(sArray2[n2][n], 0, this.pk[n2][n], n3, n4);
                System.arraycopy(sArray3[n2][n], 0, this.pk[n2][n], n3 + n4, n5);
            }
            for (n = 0; n < n4; ++n) {
                System.arraycopy(sArray4[n2][n], 0, this.pk[n2][n + n3], n3, n4);
                System.arraycopy(sArray5[n2][n], 0, this.pk[n2][n + n3], n3 + n4, n5);
            }
            for (n = 0; n < n5; ++n) {
                System.arraycopy(sArray6[n2][n], 0, this.pk[n2][n + n3 + n4], n3 + n4, n5);
            }
        }
        for (n2 = 0; n2 < n5; ++n2) {
            for (n = 0; n < n3; ++n) {
                System.arraycopy(sArray7[n2][n], 0, this.pk[n2 + n4][n], 0, n3);
                System.arraycopy(sArray8[n2][n], 0, this.pk[n2 + n4][n], n3, n4);
                System.arraycopy(sArray9[n2][n], 0, this.pk[n2 + n4][n], n3 + n4, n5);
            }
            for (n = 0; n < n4; ++n) {
                System.arraycopy(sArray10[n2][n], 0, this.pk[n2 + n4][n + n3], n3, n4);
                System.arraycopy(sArray11[n2][n], 0, this.pk[n2 + n4][n + n3], n3 + n4, n5);
            }
            for (n = 0; n < n5; ++n) {
                System.arraycopy(sArray12[n2][n], 0, this.pk[n2 + n4][n + n3 + n4], n3 + n4, n5);
            }
        }
    }

    RainbowPublicKeyParameters(RainbowParameters rainbowParameters, byte[] byArray, short[][][] sArray, short[][][] sArray2, short[][][] sArray3, short[][][] sArray4, short[][][] sArray5) {
        super(false, rainbowParameters);
        this.pk_seed = (byte[])byArray.clone();
        this.l1_Q3 = RainbowUtil.cloneArray(sArray);
        this.l1_Q5 = RainbowUtil.cloneArray(sArray2);
        this.l1_Q6 = RainbowUtil.cloneArray(sArray3);
        this.l1_Q9 = RainbowUtil.cloneArray(sArray4);
        this.l2_Q9 = RainbowUtil.cloneArray(sArray5);
    }

    public RainbowPublicKeyParameters(RainbowParameters rainbowParameters, byte[] byArray) {
        super(false, rainbowParameters);
        int n = rainbowParameters.getM();
        int n2 = rainbowParameters.getN();
        if (this.getParameters().getVersion() == Version.CLASSIC) {
            this.pk = new short[n][n2][n2];
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                for (int j = 0; j < n2; ++j) {
                    for (int k = 0; k < n; ++k) {
                        if (i > j) {
                            this.pk[k][i][j] = 0;
                            continue;
                        }
                        this.pk[k][i][j] = (short)(byArray[n3] & 0xFF);
                        ++n3;
                    }
                }
            }
        } else {
            this.pk_seed = Arrays.copyOfRange(byArray, 0, rainbowParameters.getLen_pkseed());
            this.l1_Q3 = new short[rainbowParameters.getO1()][rainbowParameters.getV1()][rainbowParameters.getO2()];
            this.l1_Q5 = new short[rainbowParameters.getO1()][rainbowParameters.getO1()][rainbowParameters.getO1()];
            this.l1_Q6 = new short[rainbowParameters.getO1()][rainbowParameters.getO1()][rainbowParameters.getO2()];
            this.l1_Q9 = new short[rainbowParameters.getO1()][rainbowParameters.getO2()][rainbowParameters.getO2()];
            this.l2_Q9 = new short[rainbowParameters.getO2()][rainbowParameters.getO2()][rainbowParameters.getO2()];
            int n4 = rainbowParameters.getLen_pkseed();
            n4 += RainbowUtil.loadEncoded(this.l1_Q3, byArray, n4, false);
            n4 += RainbowUtil.loadEncoded(this.l1_Q5, byArray, n4, true);
            n4 += RainbowUtil.loadEncoded(this.l1_Q6, byArray, n4, false);
            n4 += RainbowUtil.loadEncoded(this.l1_Q9, byArray, n4, true);
            if ((n4 += RainbowUtil.loadEncoded(this.l2_Q9, byArray, n4, true)) != byArray.length) {
                throw new IllegalArgumentException("unparsed data in key encoding");
            }
        }
    }

    public short[][][] getPk() {
        return RainbowUtil.cloneArray(this.pk);
    }

    public byte[] getEncoded() {
        if (this.getParameters().getVersion() != Version.CLASSIC) {
            byte[] byArray = this.pk_seed;
            byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_Q3, false));
            byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_Q5, true));
            byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_Q6, false));
            byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_Q9, true));
            byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_Q9, true));
            return byArray;
        }
        return RainbowUtil.getEncoded(this.pk, true);
    }
}

