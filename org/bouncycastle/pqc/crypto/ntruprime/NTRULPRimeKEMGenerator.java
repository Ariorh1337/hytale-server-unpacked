/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.Utils;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.util.Arrays;

public class NTRULPRimeKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom random;

    public NTRULPRimeKEMGenerator(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters = (NTRULPRimePublicKeyParameters)asymmetricKeyParameter;
        NTRULPRimeParameters nTRULPRimeParameters = nTRULPRimePublicKeyParameters.getParameters();
        int n = nTRULPRimeParameters.getP();
        int n2 = nTRULPRimeParameters.getQ();
        int n3 = nTRULPRimeParameters.getW();
        int n4 = nTRULPRimeParameters.getRoundedPolynomialBytes();
        int n5 = nTRULPRimeParameters.getTau0();
        int n6 = nTRULPRimeParameters.getTau1();
        byte[] byArray = new byte[]{4};
        byte[] byArray2 = Utils.getHashWithPrefix(byArray, nTRULPRimePublicKeyParameters.getEncoded());
        byte[] byArray3 = new byte[256];
        Utils.getRandomInputs(this.random, byArray3);
        byte[] byArray4 = new byte[32];
        Utils.getEncodedInputs(byArray4, byArray3);
        short[] sArray = new short[n];
        Utils.getRoundedDecodedPolynomial(sArray, nTRULPRimePublicKeyParameters.getRoundEncA(), n, n2);
        short[] sArray2 = new short[n];
        Utils.generatePolynomialInRQFromSeed(sArray2, nTRULPRimePublicKeyParameters.getSeed(), n, n2);
        byte[] byArray5 = new byte[]{5};
        byte[] byArray6 = Utils.getHashWithPrefix(byArray5, byArray4);
        byte[] byArray7 = Arrays.copyOfRange(byArray6, 0, byArray6.length / 2);
        int[] nArray = new int[n];
        Utils.expand(nArray, byArray7);
        byte[] byArray8 = new byte[n];
        Utils.sortGenerateShortPolynomial(byArray8, nArray, n, n3);
        short[] sArray3 = new short[n];
        Utils.multiplicationInRQ(sArray3, sArray2, byArray8, n, n2);
        short[] sArray4 = new short[n];
        Utils.roundPolynomial(sArray4, sArray3);
        byte[] byArray9 = new byte[n4];
        Utils.getRoundedEncodedPolynomial(byArray9, sArray4, n, n2);
        short[] sArray5 = new short[n];
        Utils.multiplicationInRQ(sArray5, sArray, byArray8, n, n2);
        byte[] byArray10 = new byte[256];
        Utils.top(byArray10, sArray5, byArray3, n2, n5, n6);
        byte[] byArray11 = new byte[128];
        Utils.getTopEncodedPolynomial(byArray11, byArray10);
        byte[] byArray12 = new byte[byArray4.length + byArray2.length / 2];
        System.arraycopy(byArray4, 0, byArray12, 0, byArray4.length);
        System.arraycopy(byArray2, 0, byArray12, byArray4.length, byArray2.length / 2);
        byte[] byArray13 = new byte[]{2};
        byte[] byArray14 = Utils.getHashWithPrefix(byArray13, byArray12);
        byte[] byArray15 = new byte[byArray9.length + byArray11.length + byArray14.length / 2];
        System.arraycopy(byArray9, 0, byArray15, 0, byArray9.length);
        System.arraycopy(byArray11, 0, byArray15, byArray9.length, byArray11.length);
        System.arraycopy(byArray14, 0, byArray15, byArray9.length + byArray11.length, byArray14.length / 2);
        byte[] byArray16 = new byte[byArray4.length + byArray15.length];
        System.arraycopy(byArray4, 0, byArray16, 0, byArray4.length);
        System.arraycopy(byArray15, 0, byArray16, byArray4.length, byArray15.length);
        byte[] byArray17 = new byte[]{1};
        byte[] byArray18 = Utils.getHashWithPrefix(byArray17, byArray16);
        byte[] byArray19 = Arrays.copyOfRange(byArray18, 0, nTRULPRimeParameters.getSessionKeySize() / 8);
        return new SecretWithEncapsulationImpl(byArray19, byArray15);
    }
}

