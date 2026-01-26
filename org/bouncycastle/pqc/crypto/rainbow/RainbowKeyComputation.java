/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.pqc.crypto.rainbow.ComputeInField;
import org.bouncycastle.pqc.crypto.rainbow.RainbowDRBG;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowUtil;
import org.bouncycastle.pqc.crypto.rainbow.Version;
import org.bouncycastle.util.Arrays;

class RainbowKeyComputation {
    private SecureRandom random;
    private Version version;
    private RainbowParameters rainbowParams;
    ComputeInField cf = new ComputeInField();
    private int v1;
    private int o1;
    private int o2;
    private byte[] sk_seed;
    private byte[] pk_seed;
    private short[][] s1;
    private short[][] t1;
    private short[][] t2;
    private short[][] t3;
    private short[][] t4;
    private short[][][] l1_F1;
    private short[][][] l1_F2;
    private short[][][] l2_F1;
    private short[][][] l2_F2;
    private short[][][] l2_F3;
    private short[][][] l2_F5;
    private short[][][] l2_F6;
    private short[][][] l1_Q1;
    private short[][][] l1_Q2;
    private short[][][] l1_Q3;
    private short[][][] l1_Q5;
    private short[][][] l1_Q6;
    private short[][][] l1_Q9;
    private short[][][] l2_Q1;
    private short[][][] l2_Q2;
    private short[][][] l2_Q3;
    private short[][][] l2_Q5;
    private short[][][] l2_Q6;
    private short[][][] l2_Q9;

    public RainbowKeyComputation(RainbowParameters rainbowParameters, SecureRandom secureRandom) {
        this.rainbowParams = rainbowParameters;
        this.random = secureRandom;
        this.version = this.rainbowParams.getVersion();
        this.v1 = this.rainbowParams.getV1();
        this.o1 = this.rainbowParams.getO1();
        this.o2 = this.rainbowParams.getO2();
    }

    public RainbowKeyComputation(RainbowParameters rainbowParameters, byte[] byArray, byte[] byArray2) {
        this.rainbowParams = rainbowParameters;
        this.random = null;
        this.version = this.rainbowParams.getVersion();
        this.pk_seed = byArray;
        this.sk_seed = byArray2;
        this.v1 = this.rainbowParams.getV1();
        this.o1 = this.rainbowParams.getO1();
        this.o2 = this.rainbowParams.getO2();
    }

    private void generate_S_and_T(SecureRandom secureRandom) {
        this.s1 = RainbowUtil.generate_random_2d(secureRandom, this.o1, this.o2);
        this.t1 = RainbowUtil.generate_random_2d(secureRandom, this.v1, this.o1);
        this.t2 = RainbowUtil.generate_random_2d(secureRandom, this.v1, this.o2);
        this.t3 = RainbowUtil.generate_random_2d(secureRandom, this.o1, this.o2);
    }

    private void generate_B1_and_B2(SecureRandom secureRandom) {
        this.l1_Q1 = RainbowUtil.generate_random(secureRandom, this.o1, this.v1, this.v1, true);
        this.l1_Q2 = RainbowUtil.generate_random(secureRandom, this.o1, this.v1, this.o1, false);
        this.l2_Q1 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.v1, true);
        this.l2_Q2 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.o1, false);
        this.l2_Q3 = RainbowUtil.generate_random(secureRandom, this.o2, this.v1, this.o2, false);
        this.l2_Q5 = RainbowUtil.generate_random(secureRandom, this.o2, this.o1, this.o1, true);
        this.l2_Q6 = RainbowUtil.generate_random(secureRandom, this.o2, this.o1, this.o2, false);
    }

    private void calculate_t4() {
        short[][] sArray = this.cf.multiplyMatrix(this.t1, this.t3);
        this.t4 = this.cf.addMatrix(sArray, this.t2);
    }

    private void calculate_F_from_Q() {
        int n;
        this.l1_F1 = RainbowUtil.cloneArray(this.l1_Q1);
        this.l1_F2 = new short[this.o1][][];
        for (n = 0; n < this.o1; ++n) {
            this.l1_F2[n] = this.cf.addMatrixTranspose(this.l1_Q1[n]);
            this.l1_F2[n] = this.cf.multiplyMatrix(this.l1_F2[n], this.t1);
            this.l1_F2[n] = this.cf.addMatrix(this.l1_F2[n], this.l1_Q2[n]);
        }
        this.l2_F2 = new short[this.o2][][];
        this.l2_F3 = new short[this.o2][][];
        this.l2_F5 = new short[this.o2][][];
        this.l2_F6 = new short[this.o2][][];
        this.l2_F1 = RainbowUtil.cloneArray(this.l2_Q1);
        for (n = 0; n < this.o2; ++n) {
            short[][] sArray = this.cf.addMatrixTranspose(this.l2_Q1[n]);
            this.l2_F2[n] = this.cf.multiplyMatrix(sArray, this.t1);
            this.l2_F2[n] = this.cf.addMatrix(this.l2_F2[n], this.l2_Q2[n]);
            this.l2_F3[n] = this.cf.multiplyMatrix(sArray, this.t4);
            short[][] sArray2 = this.cf.multiplyMatrix(this.l2_Q2[n], this.t3);
            this.l2_F3[n] = this.cf.addMatrix(this.l2_F3[n], sArray2);
            this.l2_F3[n] = this.cf.addMatrix(this.l2_F3[n], this.l2_Q3[n]);
            sArray2 = this.cf.multiplyMatrix(this.l2_Q1[n], this.t1);
            sArray2 = this.cf.addMatrix(sArray2, this.l2_Q2[n]);
            short[][] sArray3 = this.cf.transpose(this.t1);
            this.l2_F5[n] = this.cf.multiplyMatrix(sArray3, sArray2);
            this.l2_F5[n] = this.cf.addMatrix(this.l2_F5[n], this.l2_Q5[n]);
            this.l2_F5[n] = this.cf.to_UT(this.l2_F5[n]);
            this.l2_F6[n] = this.cf.multiplyMatrix(sArray3, this.l2_F3[n]);
            sArray2 = this.cf.multiplyMatrix(this.cf.transpose(this.l2_Q2[n]), this.t4);
            this.l2_F6[n] = this.cf.addMatrix(this.l2_F6[n], sArray2);
            sArray2 = this.cf.addMatrixTranspose(this.l2_Q5[n]);
            sArray2 = this.cf.multiplyMatrix(sArray2, this.t3);
            this.l2_F6[n] = this.cf.addMatrix(this.l2_F6[n], sArray2);
            this.l2_F6[n] = this.cf.addMatrix(this.l2_F6[n], this.l2_Q6[n]);
        }
    }

    private void calculate_Q_from_F() {
        short[][] sArray = this.cf.transpose(this.t1);
        short[][] sArray2 = this.cf.transpose(this.t2);
        this.l1_Q1 = RainbowUtil.cloneArray(this.l1_F1);
        this.l1_Q2 = new short[this.o1][][];
        for (int i = 0; i < this.o1; ++i) {
            this.l1_Q2[i] = this.cf.addMatrixTranspose(this.l1_F1[i]);
            this.l1_Q2[i] = this.cf.multiplyMatrix(this.l1_Q2[i], this.t1);
            this.l1_Q2[i] = this.cf.addMatrix(this.l1_Q2[i], this.l1_F2[i]);
        }
        this.calculate_l1_Q3569(sArray, sArray2);
        this.l2_Q2 = new short[this.o2][][];
        this.l2_Q3 = new short[this.o2][][];
        this.l2_Q5 = new short[this.o2][][];
        this.l2_Q6 = new short[this.o2][][];
        this.l2_Q1 = RainbowUtil.cloneArray(this.l2_F1);
        for (int i = 0; i < this.o2; ++i) {
            short[][] sArray3 = this.cf.addMatrixTranspose(this.l2_F1[i]);
            this.l2_Q2[i] = this.cf.multiplyMatrix(sArray3, this.t1);
            this.l2_Q2[i] = this.cf.addMatrix(this.l2_Q2[i], this.l2_F2[i]);
            this.l2_Q3[i] = this.cf.multiplyMatrix(sArray3, this.t2);
            short[][] sArray4 = this.cf.multiplyMatrix(this.l2_F2[i], this.t3);
            this.l2_Q3[i] = this.cf.addMatrix(this.l2_Q3[i], sArray4);
            this.l2_Q3[i] = this.cf.addMatrix(this.l2_Q3[i], this.l2_F3[i]);
            sArray4 = this.cf.multiplyMatrix(this.l2_F1[i], this.t1);
            sArray4 = this.cf.addMatrix(sArray4, this.l2_F2[i]);
            this.l2_Q5[i] = this.cf.multiplyMatrix(sArray, sArray4);
            this.l2_Q5[i] = this.cf.addMatrix(this.l2_Q5[i], this.l2_F5[i]);
            this.l2_Q5[i] = this.cf.to_UT(this.l2_Q5[i]);
            this.l2_Q6[i] = this.cf.multiplyMatrix(sArray, this.l2_Q3[i]);
            sArray4 = this.cf.multiplyMatrix(this.cf.transpose(this.l2_F2[i]), this.t2);
            this.l2_Q6[i] = this.cf.addMatrix(this.l2_Q6[i], sArray4);
            sArray4 = this.cf.addMatrixTranspose(this.l2_F5[i]);
            sArray4 = this.cf.multiplyMatrix(sArray4, this.t3);
            this.l2_Q6[i] = this.cf.addMatrix(this.l2_Q6[i], sArray4);
            this.l2_Q6[i] = this.cf.addMatrix(this.l2_Q6[i], this.l2_F6[i]);
        }
        this.calculate_l2_Q9(sArray2);
    }

    private void calculate_Q_from_F_cyclic() {
        short[][] sArray = this.cf.transpose(this.t1);
        short[][] sArray2 = this.cf.transpose(this.t2);
        this.calculate_l1_Q3569(sArray, sArray2);
        this.calculate_l2_Q9(sArray2);
    }

    private void calculate_l1_Q3569(short[][] sArray, short[][] sArray2) {
        this.l1_Q3 = new short[this.o1][][];
        this.l1_Q5 = new short[this.o1][][];
        this.l1_Q6 = new short[this.o1][][];
        this.l1_Q9 = new short[this.o1][][];
        for (int i = 0; i < this.o1; ++i) {
            short[][] sArray3 = this.cf.multiplyMatrix(this.l1_F2[i], this.t3);
            this.l1_Q3[i] = this.cf.addMatrixTranspose(this.l1_F1[i]);
            this.l1_Q3[i] = this.cf.multiplyMatrix(this.l1_Q3[i], this.t2);
            this.l1_Q3[i] = this.cf.addMatrix(this.l1_Q3[i], sArray3);
            this.l1_Q5[i] = this.cf.multiplyMatrix(this.l1_F1[i], this.t1);
            this.l1_Q5[i] = this.cf.addMatrix(this.l1_Q5[i], this.l1_F2[i]);
            this.l1_Q5[i] = this.cf.multiplyMatrix(sArray, this.l1_Q5[i]);
            this.l1_Q5[i] = this.cf.to_UT(this.l1_Q5[i]);
            short[][] sArray4 = this.cf.multiplyMatrix(this.cf.transpose(this.l1_F2[i]), this.t2);
            this.l1_Q6[i] = this.cf.multiplyMatrix(sArray, this.l1_Q3[i]);
            this.l1_Q6[i] = this.cf.addMatrix(this.l1_Q6[i], sArray4);
            sArray4 = this.cf.multiplyMatrix(this.l1_F1[i], this.t2);
            this.l1_Q9[i] = this.cf.addMatrix(sArray4, sArray3);
            this.l1_Q9[i] = this.cf.multiplyMatrix(sArray2, this.l1_Q9[i]);
            this.l1_Q9[i] = this.cf.to_UT(this.l1_Q9[i]);
        }
    }

    private void calculate_l2_Q9(short[][] sArray) {
        this.l2_Q9 = new short[this.o2][][];
        for (int i = 0; i < this.o2; ++i) {
            this.l2_Q9[i] = this.cf.multiplyMatrix(this.l2_F1[i], this.t2);
            short[][] sArray2 = this.cf.multiplyMatrix(this.l2_F2[i], this.t3);
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], sArray2);
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], this.l2_F3[i]);
            this.l2_Q9[i] = this.cf.multiplyMatrix(sArray, this.l2_Q9[i]);
            sArray2 = this.cf.multiplyMatrix(this.l2_F5[i], this.t3);
            sArray2 = this.cf.addMatrix(sArray2, this.l2_F6[i]);
            sArray2 = this.cf.multiplyMatrix(this.cf.transpose(this.t3), sArray2);
            this.l2_Q9[i] = this.cf.addMatrix(this.l2_Q9[i], sArray2);
            this.l2_Q9[i] = this.cf.to_UT(this.l2_Q9[i]);
        }
    }

    private void genKeyMaterial() {
        this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
        this.random.nextBytes(this.sk_seed);
        RainbowDRBG rainbowDRBG = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
        this.generate_S_and_T(rainbowDRBG);
        this.l1_F1 = RainbowUtil.generate_random(rainbowDRBG, this.o1, this.v1, this.v1, true);
        this.l1_F2 = RainbowUtil.generate_random(rainbowDRBG, this.o1, this.v1, this.o1, false);
        this.l2_F1 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.v1, true);
        this.l2_F2 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.o1, false);
        this.l2_F3 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.v1, this.o2, false);
        this.l2_F5 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.o1, this.o1, true);
        this.l2_F6 = RainbowUtil.generate_random(rainbowDRBG, this.o2, this.o1, this.o2, false);
        this.calculate_Q_from_F();
        this.calculate_t4();
        this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
        this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
        this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
        this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
        this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
        this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
    }

    private void genPrivateKeyMaterial_cyclic() {
        RainbowDRBG rainbowDRBG = new RainbowDRBG(this.sk_seed, this.rainbowParams.getHash_algo());
        RainbowDRBG rainbowDRBG2 = new RainbowDRBG(this.pk_seed, this.rainbowParams.getHash_algo());
        this.generate_S_and_T(rainbowDRBG);
        this.calculate_t4();
        this.generate_B1_and_B2(rainbowDRBG2);
        this.l1_Q1 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q1, this.l1_Q1);
        this.l1_Q2 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q2, this.l1_Q2);
        this.calculate_F_from_Q();
    }

    private void genKeyMaterial_cyclic() {
        this.sk_seed = new byte[this.rainbowParams.getLen_skseed()];
        this.random.nextBytes(this.sk_seed);
        this.pk_seed = new byte[this.rainbowParams.getLen_pkseed()];
        this.random.nextBytes(this.pk_seed);
        this.genPrivateKeyMaterial_cyclic();
        this.calculate_Q_from_F_cyclic();
        this.l1_Q3 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q3, this.l1_Q3);
        this.l1_Q5 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q5, this.l1_Q5);
        this.l1_Q6 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q6, this.l1_Q6);
        this.l1_Q9 = this.cf.obfuscate_l1_polys(this.s1, this.l2_Q9, this.l1_Q9);
    }

    public AsymmetricCipherKeyPair genKeyPairClassical() {
        this.genKeyMaterial();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.l1_Q1, this.l1_Q2, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q1, this.l2_Q2, this.l2_Q3, this.l2_Q5, this.l2_Q6, this.l2_Q9);
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, rainbowPublicKeyParameters.getEncoded());
        return new AsymmetricCipherKeyPair(rainbowPublicKeyParameters, rainbowPrivateKeyParameters);
    }

    public AsymmetricCipherKeyPair genKeyPairCircumzenithal() {
        this.genKeyMaterial_cyclic();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9);
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, rainbowPublicKeyParameters.getEncoded());
        return new AsymmetricCipherKeyPair(rainbowPublicKeyParameters, rainbowPrivateKeyParameters);
    }

    public AsymmetricCipherKeyPair genKeyPairCompressed() {
        this.genKeyMaterial_cyclic();
        RainbowPublicKeyParameters rainbowPublicKeyParameters = new RainbowPublicKeyParameters(this.rainbowParams, this.pk_seed, this.l1_Q3, this.l1_Q5, this.l1_Q6, this.l1_Q9, this.l2_Q9);
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowPrivateKeyParameters(this.rainbowParams, this.pk_seed, this.sk_seed, rainbowPublicKeyParameters.getEncoded());
        return new AsymmetricCipherKeyPair(rainbowPublicKeyParameters, rainbowPrivateKeyParameters);
    }

    RainbowPrivateKeyParameters generatePrivateKey() {
        this.sk_seed = Arrays.clone(this.sk_seed);
        this.pk_seed = Arrays.clone(this.pk_seed);
        this.genPrivateKeyMaterial_cyclic();
        return new RainbowPrivateKeyParameters(this.rainbowParams, this.sk_seed, this.s1, this.t1, this.t3, this.t4, this.l1_F1, this.l1_F2, this.l2_F1, this.l2_F2, this.l2_F3, this.l2_F5, this.l2_F6, null);
    }
}

