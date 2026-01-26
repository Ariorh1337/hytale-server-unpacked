/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.ComputeInField;
import org.bouncycastle.pqc.crypto.rainbow.GF2Field;
import org.bouncycastle.pqc.crypto.rainbow.RainbowDRBG;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowUtil;
import org.bouncycastle.util.Arrays;

class RainbowPublicMap {
    private ComputeInField cf = new ComputeInField();
    private RainbowParameters params;
    private final int num_gf_elements = 256;

    public RainbowPublicMap(RainbowParameters rainbowParameters) {
        this.params = rainbowParameters;
    }

    private short[][] compute_accumulator(short[] sArray, short[] sArray2, short[][][] sArray3, int n) {
        short[][] sArray4 = new short[256][n];
        if (sArray2.length != sArray3[0].length || sArray.length != sArray3[0][0].length || sArray3.length != n) {
            throw new RuntimeException("Accumulator calculation not possible!");
        }
        for (int i = 0; i < sArray2.length; ++i) {
            short[] sArray5 = this.cf.multVect(sArray2[i], sArray);
            for (int j = 0; j < sArray.length; ++j) {
                for (int k = 0; k < sArray3.length; ++k) {
                    short s = sArray5[j];
                    if (s == 0) continue;
                    sArray4[s][k] = GF2Field.addElem(sArray4[s][k], sArray3[k][i][j]);
                }
            }
        }
        return sArray4;
    }

    private short[] add_and_reduce(short[][] sArray) {
        int n = this.params.getM();
        short[] sArray2 = new short[n];
        for (int i = 0; i < 8; ++i) {
            int n2 = (int)Math.pow(2.0, i);
            short[] sArray3 = new short[n];
            for (int j = n2; j < 256; j += n2 * 2) {
                for (int k = 0; k < n2; ++k) {
                    sArray3 = this.cf.addVect(sArray3, sArray[j + k]);
                }
            }
            sArray2 = this.cf.addVect(sArray2, this.cf.multVect((short)n2, sArray3));
        }
        return sArray2;
    }

    public short[] publicMap(RainbowPublicKeyParameters rainbowPublicKeyParameters, short[] sArray) {
        short[][] sArray2 = this.compute_accumulator(sArray, sArray, rainbowPublicKeyParameters.pk, this.params.getM());
        return this.add_and_reduce(sArray2);
    }

    public short[] publicMap_cyclic(RainbowPublicKeyParameters rainbowPublicKeyParameters, short[] sArray) {
        int n = this.params.getV1();
        int n2 = this.params.getO1();
        int n3 = this.params.getO2();
        short[][] sArray2 = new short[256][n2 + n3];
        short[] sArray3 = Arrays.copyOfRange(sArray, 0, n);
        short[] sArray4 = Arrays.copyOfRange(sArray, n, n + n2);
        short[] sArray5 = Arrays.copyOfRange(sArray, n + n2, sArray.length);
        RainbowDRBG rainbowDRBG = new RainbowDRBG(rainbowPublicKeyParameters.pk_seed, rainbowPublicKeyParameters.getParameters().getHash_algo());
        short[][][] sArray6 = RainbowUtil.generate_random(rainbowDRBG, n2, n, n, true);
        short[][] sArray7 = this.compute_accumulator(sArray3, sArray3, sArray6, n2);
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n2, n, n2, false);
        sArray7 = this.cf.addMatrix(sArray7, this.compute_accumulator(sArray4, sArray3, sArray6, n2));
        sArray7 = this.cf.addMatrix(sArray7, this.compute_accumulator(sArray5, sArray3, rainbowPublicKeyParameters.l1_Q3, n2));
        sArray7 = this.cf.addMatrix(sArray7, this.compute_accumulator(sArray4, sArray4, rainbowPublicKeyParameters.l1_Q5, n2));
        sArray7 = this.cf.addMatrix(sArray7, this.compute_accumulator(sArray5, sArray4, rainbowPublicKeyParameters.l1_Q6, n2));
        sArray7 = this.cf.addMatrix(sArray7, this.compute_accumulator(sArray5, sArray5, rainbowPublicKeyParameters.l1_Q9, n2));
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n3, n, n, true);
        short[][] sArray8 = this.compute_accumulator(sArray3, sArray3, sArray6, n3);
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n3, n, n2, false);
        sArray8 = this.cf.addMatrix(sArray8, this.compute_accumulator(sArray4, sArray3, sArray6, n3));
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n3, n, n3, false);
        sArray8 = this.cf.addMatrix(sArray8, this.compute_accumulator(sArray5, sArray3, sArray6, n3));
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n3, n2, n2, true);
        sArray8 = this.cf.addMatrix(sArray8, this.compute_accumulator(sArray4, sArray4, sArray6, n3));
        sArray6 = RainbowUtil.generate_random(rainbowDRBG, n3, n2, n3, false);
        sArray8 = this.cf.addMatrix(sArray8, this.compute_accumulator(sArray5, sArray4, sArray6, n3));
        sArray8 = this.cf.addMatrix(sArray8, this.compute_accumulator(sArray5, sArray5, rainbowPublicKeyParameters.l2_Q9, n3));
        for (int i = 0; i < 256; ++i) {
            sArray2[i] = Arrays.concatenate(sArray7[i], sArray8[i]);
        }
        return this.add_and_reduce(sArray2);
    }
}

