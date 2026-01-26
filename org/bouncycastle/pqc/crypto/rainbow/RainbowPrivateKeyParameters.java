/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.rainbow;

import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyComputation;
import org.bouncycastle.pqc.crypto.rainbow.RainbowKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowUtil;
import org.bouncycastle.pqc.crypto.rainbow.Version;
import org.bouncycastle.util.Arrays;

public class RainbowPrivateKeyParameters
extends RainbowKeyParameters {
    final byte[] sk_seed;
    final short[][] s1;
    final short[][] t1;
    final short[][] t3;
    final short[][] t4;
    final short[][][] l1_F1;
    final short[][][] l1_F2;
    final short[][][] l2_F1;
    final short[][][] l2_F2;
    final short[][][] l2_F3;
    final short[][][] l2_F5;
    final short[][][] l2_F6;
    private final byte[] pk_seed;
    private byte[] pk_encoded;

    RainbowPrivateKeyParameters(RainbowParameters rainbowParameters, byte[] byArray, short[][] sArray, short[][] sArray2, short[][] sArray3, short[][] sArray4, short[][][] sArray5, short[][][] sArray6, short[][][] sArray7, short[][][] sArray8, short[][][] sArray9, short[][][] sArray10, short[][][] sArray11, byte[] byArray2) {
        super(true, rainbowParameters);
        this.pk_seed = null;
        this.pk_encoded = byArray2;
        this.sk_seed = (byte[])byArray.clone();
        this.s1 = RainbowUtil.cloneArray(sArray);
        this.t1 = RainbowUtil.cloneArray(sArray2);
        this.t3 = RainbowUtil.cloneArray(sArray3);
        this.t4 = RainbowUtil.cloneArray(sArray4);
        this.l1_F1 = RainbowUtil.cloneArray(sArray5);
        this.l1_F2 = RainbowUtil.cloneArray(sArray6);
        this.l2_F1 = RainbowUtil.cloneArray(sArray7);
        this.l2_F2 = RainbowUtil.cloneArray(sArray8);
        this.l2_F3 = RainbowUtil.cloneArray(sArray9);
        this.l2_F5 = RainbowUtil.cloneArray(sArray10);
        this.l2_F6 = RainbowUtil.cloneArray(sArray11);
    }

    RainbowPrivateKeyParameters(RainbowParameters rainbowParameters, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        super(true, rainbowParameters);
        RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowKeyComputation(rainbowParameters, byArray, byArray2).generatePrivateKey();
        this.pk_seed = byArray;
        this.pk_encoded = byArray3;
        this.sk_seed = byArray2;
        this.s1 = rainbowPrivateKeyParameters.s1;
        this.t1 = rainbowPrivateKeyParameters.t1;
        this.t3 = rainbowPrivateKeyParameters.t3;
        this.t4 = rainbowPrivateKeyParameters.t4;
        this.l1_F1 = rainbowPrivateKeyParameters.l1_F1;
        this.l1_F2 = rainbowPrivateKeyParameters.l1_F2;
        this.l2_F1 = rainbowPrivateKeyParameters.l2_F1;
        this.l2_F2 = rainbowPrivateKeyParameters.l2_F2;
        this.l2_F3 = rainbowPrivateKeyParameters.l2_F3;
        this.l2_F5 = rainbowPrivateKeyParameters.l2_F5;
        this.l2_F6 = rainbowPrivateKeyParameters.l2_F6;
    }

    public RainbowPrivateKeyParameters(RainbowParameters rainbowParameters, byte[] byArray) {
        super(true, rainbowParameters);
        if (rainbowParameters.getVersion() == Version.COMPRESSED) {
            this.pk_seed = Arrays.copyOfRange(byArray, 0, rainbowParameters.getLen_pkseed());
            this.sk_seed = Arrays.copyOfRange(byArray, rainbowParameters.getLen_pkseed(), rainbowParameters.getLen_pkseed() + rainbowParameters.getLen_skseed());
            RainbowPrivateKeyParameters rainbowPrivateKeyParameters = new RainbowKeyComputation(rainbowParameters, this.pk_seed, this.sk_seed).generatePrivateKey();
            this.pk_encoded = rainbowPrivateKeyParameters.pk_encoded;
            this.s1 = rainbowPrivateKeyParameters.s1;
            this.t1 = rainbowPrivateKeyParameters.t1;
            this.t3 = rainbowPrivateKeyParameters.t3;
            this.t4 = rainbowPrivateKeyParameters.t4;
            this.l1_F1 = rainbowPrivateKeyParameters.l1_F1;
            this.l1_F2 = rainbowPrivateKeyParameters.l1_F2;
            this.l2_F1 = rainbowPrivateKeyParameters.l2_F1;
            this.l2_F2 = rainbowPrivateKeyParameters.l2_F2;
            this.l2_F3 = rainbowPrivateKeyParameters.l2_F3;
            this.l2_F5 = rainbowPrivateKeyParameters.l2_F5;
            this.l2_F6 = rainbowPrivateKeyParameters.l2_F6;
        } else {
            int n = rainbowParameters.getV1();
            int n2 = rainbowParameters.getO1();
            int n3 = rainbowParameters.getO2();
            this.s1 = new short[n2][n3];
            this.t1 = new short[n][n2];
            this.t4 = new short[n][n3];
            this.t3 = new short[n2][n3];
            this.l1_F1 = new short[n2][n][n];
            this.l1_F2 = new short[n2][n][n2];
            this.l2_F1 = new short[n3][n][n];
            this.l2_F2 = new short[n3][n][n2];
            this.l2_F3 = new short[n3][n][n3];
            this.l2_F5 = new short[n3][n2][n2];
            this.l2_F6 = new short[n3][n2][n3];
            int n4 = 0;
            this.pk_seed = null;
            this.sk_seed = Arrays.copyOfRange(byArray, n4, rainbowParameters.getLen_skseed());
            n4 += this.sk_seed.length;
            n4 += RainbowUtil.loadEncoded(this.s1, byArray, n4);
            n4 += RainbowUtil.loadEncoded(this.t1, byArray, n4);
            n4 += RainbowUtil.loadEncoded(this.t4, byArray, n4);
            n4 += RainbowUtil.loadEncoded(this.t3, byArray, n4);
            n4 += RainbowUtil.loadEncoded(this.l1_F1, byArray, n4, true);
            n4 += RainbowUtil.loadEncoded(this.l1_F2, byArray, n4, false);
            n4 += RainbowUtil.loadEncoded(this.l2_F1, byArray, n4, true);
            n4 += RainbowUtil.loadEncoded(this.l2_F2, byArray, n4, false);
            n4 += RainbowUtil.loadEncoded(this.l2_F3, byArray, n4, false);
            n4 += RainbowUtil.loadEncoded(this.l2_F5, byArray, n4, true);
            n4 += RainbowUtil.loadEncoded(this.l2_F6, byArray, n4, false);
            this.pk_encoded = Arrays.copyOfRange(byArray, n4, byArray.length);
        }
    }

    byte[] getSk_seed() {
        return Arrays.clone(this.sk_seed);
    }

    short[][] getS1() {
        return RainbowUtil.cloneArray(this.s1);
    }

    short[][] getT1() {
        return RainbowUtil.cloneArray(this.t1);
    }

    short[][] getT4() {
        return RainbowUtil.cloneArray(this.t4);
    }

    short[][] getT3() {
        return RainbowUtil.cloneArray(this.t3);
    }

    short[][][] getL1_F1() {
        return RainbowUtil.cloneArray(this.l1_F1);
    }

    short[][][] getL1_F2() {
        return RainbowUtil.cloneArray(this.l1_F2);
    }

    short[][][] getL2_F1() {
        return RainbowUtil.cloneArray(this.l2_F1);
    }

    short[][][] getL2_F2() {
        return RainbowUtil.cloneArray(this.l2_F2);
    }

    short[][][] getL2_F3() {
        return RainbowUtil.cloneArray(this.l2_F3);
    }

    short[][][] getL2_F5() {
        return RainbowUtil.cloneArray(this.l2_F5);
    }

    short[][][] getL2_F6() {
        return RainbowUtil.cloneArray(this.l2_F6);
    }

    public byte[] getPrivateKey() {
        if (this.getParameters().getVersion() == Version.COMPRESSED) {
            return Arrays.concatenate(this.pk_seed, this.sk_seed);
        }
        byte[] byArray = this.sk_seed;
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.s1));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.t1));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.t4));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.t3));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_F1, true));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l1_F2, false));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_F1, true));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_F2, false));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_F3, false));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_F5, true));
        byArray = Arrays.concatenate(byArray, RainbowUtil.getEncoded(this.l2_F6, false));
        return byArray;
    }

    public byte[] getEncoded() {
        if (this.getParameters().getVersion() == Version.COMPRESSED) {
            return Arrays.concatenate(this.pk_seed, this.sk_seed);
        }
        return Arrays.concatenate(this.getPrivateKey(), this.pk_encoded);
    }

    public byte[] getPublicKey() {
        return this.pk_encoded;
    }
}

