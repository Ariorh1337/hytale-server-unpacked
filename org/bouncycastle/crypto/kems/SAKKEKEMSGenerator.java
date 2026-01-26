/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.kems.SAKKEKEMExtractor;
import org.bouncycastle.crypto.kems.SecretWithEncapsulationImpl;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SAKKEPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SAKKEKEMSGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom random;

    public SAKKEKEMSGenerator(SecureRandom secureRandom) {
        this.random = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        Object object;
        BigInteger bigInteger;
        ECPoint eCPoint;
        SAKKEPublicKeyParameters sAKKEPublicKeyParameters = (SAKKEPublicKeyParameters)asymmetricKeyParameter;
        ECPoint eCPoint2 = sAKKEPublicKeyParameters.getZ();
        BigInteger bigInteger2 = sAKKEPublicKeyParameters.getIdentifier();
        BigInteger bigInteger3 = sAKKEPublicKeyParameters.getPrime();
        BigInteger bigInteger4 = sAKKEPublicKeyParameters.getQ();
        BigInteger bigInteger5 = sAKKEPublicKeyParameters.getG();
        int n = sAKKEPublicKeyParameters.getN();
        ECCurve eCCurve = sAKKEPublicKeyParameters.getCurve();
        ECPoint eCPoint3 = sAKKEPublicKeyParameters.getPoint();
        Digest digest = sAKKEPublicKeyParameters.getDigest();
        BigInteger bigInteger6 = BigIntegers.createRandomBigInteger(n, this.random);
        BigInteger bigInteger7 = SAKKEKEMSGenerator.hashToIntegerRange(Arrays.concatenate(bigInteger6.toByteArray(), bigInteger2.toByteArray()), bigInteger4, digest);
        BigInteger bigInteger8 = eCCurve.getOrder();
        if (bigInteger8 == null) {
            eCPoint = eCPoint3.multiply(bigInteger2).add(eCPoint2).multiply(bigInteger7).normalize();
        } else {
            bigInteger = bigInteger2.multiply(bigInteger7).mod(bigInteger8);
            eCPoint = ECAlgorithms.sumOfTwoMultiplies(eCPoint3, bigInteger, eCPoint2, bigInteger7).normalize();
        }
        bigInteger = BigInteger.ONE;
        BigInteger bigInteger9 = bigInteger5;
        Object object2 = BigInteger.ONE;
        Object object3 = bigInteger5;
        ECPoint eCPoint4 = eCCurve.createPoint((BigInteger)object2, (BigInteger)object3);
        for (int i = bigInteger7.bitLength() - 2; i >= 0; --i) {
            object = SAKKEKEMExtractor.fp2PointSquare((BigInteger)object2, (BigInteger)object3, bigInteger3);
            eCPoint4 = eCPoint4.timesPow2(2);
            object2 = object[0];
            object3 = object[1];
            if (!bigInteger7.testBit(i)) continue;
            object = SAKKEKEMExtractor.fp2Multiply((BigInteger)object2, (BigInteger)object3, bigInteger, bigInteger9, bigInteger3);
            object2 = object[0];
            object3 = object[1];
        }
        BigInteger bigInteger10 = BigIntegers.modOddInverse(bigInteger3, (BigInteger)object2);
        object = ((BigInteger)object3).multiply(bigInteger10).mod(bigInteger3);
        BigInteger bigInteger11 = SAKKEKEMSGenerator.hashToIntegerRange(((BigInteger)object).toByteArray(), BigInteger.ONE.shiftLeft(n), digest);
        BigInteger bigInteger12 = bigInteger6.xor(bigInteger11);
        byte[] byArray = Arrays.concatenate(eCPoint.getEncoded(false), BigIntegers.asUnsignedByteArray(16, bigInteger12));
        return new SecretWithEncapsulationImpl(BigIntegers.asUnsignedByteArray(n / 8, bigInteger6), byArray);
    }

    static BigInteger hashToIntegerRange(byte[] byArray, BigInteger bigInteger, Digest digest) {
        byte[] byArray2 = new byte[digest.getDigestSize()];
        digest.update(byArray, 0, byArray.length);
        digest.doFinal(byArray2, 0);
        byte[] byArray3 = new byte[digest.getDigestSize()];
        int n = bigInteger.bitLength() >> 8;
        BigInteger bigInteger2 = BigInteger.ZERO;
        byte[] byArray4 = new byte[digest.getDigestSize()];
        for (int i = 0; i <= n; ++i) {
            digest.update(byArray3, 0, byArray3.length);
            digest.doFinal(byArray3, 0);
            digest.update(byArray3, 0, byArray3.length);
            digest.update(byArray2, 0, byArray2.length);
            digest.doFinal(byArray4, 0);
            bigInteger2 = bigInteger2.shiftLeft(byArray4.length * 8).add(new BigInteger(1, byArray4));
        }
        return bigInteger2.mod(bigInteger);
    }
}

