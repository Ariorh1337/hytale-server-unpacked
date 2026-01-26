/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.kems.SAKKEKEMSGenerator;
import org.bouncycastle.crypto.params.SAKKEPrivateKeyParameters;
import org.bouncycastle.crypto.params.SAKKEPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public class SAKKEKEMExtractor
implements EncapsulatedSecretExtractor {
    private final ECCurve curve;
    private final BigInteger p;
    private final BigInteger q;
    private final ECPoint P;
    private final ECPoint Z_S;
    private final ECPoint K_bs;
    private final int n;
    private final BigInteger identifier;
    private final Digest digest;

    public SAKKEKEMExtractor(SAKKEPrivateKeyParameters sAKKEPrivateKeyParameters) {
        SAKKEPublicKeyParameters sAKKEPublicKeyParameters = sAKKEPrivateKeyParameters.getPublicParams();
        this.curve = sAKKEPublicKeyParameters.getCurve();
        this.q = sAKKEPublicKeyParameters.getQ();
        this.P = sAKKEPublicKeyParameters.getPoint();
        this.p = sAKKEPublicKeyParameters.getPrime();
        this.Z_S = sAKKEPublicKeyParameters.getZ();
        this.identifier = sAKKEPublicKeyParameters.getIdentifier();
        this.K_bs = this.P.multiply(this.identifier.add(sAKKEPrivateKeyParameters.getMasterSecret()).modInverse(this.q)).normalize();
        this.n = sAKKEPublicKeyParameters.getN();
        this.digest = sAKKEPublicKeyParameters.getDigest();
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        ECPoint eCPoint;
        ECPoint eCPoint2 = this.curve.decodePoint(Arrays.copyOfRange(byArray, 0, 257));
        BigInteger bigInteger = BigIntegers.fromUnsignedByteArray(byArray, 257, 16);
        BigInteger bigInteger2 = SAKKEKEMExtractor.computePairing(eCPoint2, this.K_bs, this.p, this.q);
        BigInteger bigInteger3 = BigInteger.ONE.shiftLeft(this.n);
        BigInteger bigInteger4 = SAKKEKEMSGenerator.hashToIntegerRange(bigInteger2.toByteArray(), bigInteger3, this.digest);
        BigInteger bigInteger5 = bigInteger.xor(bigInteger4).mod(this.p);
        BigInteger bigInteger6 = this.identifier;
        BigInteger bigInteger7 = SAKKEKEMSGenerator.hashToIntegerRange(Arrays.concatenate(bigInteger5.toByteArray(), bigInteger6.toByteArray()), this.q, this.digest);
        BigInteger bigInteger8 = this.curve.getOrder();
        if (bigInteger8 == null) {
            eCPoint = this.P.multiply(bigInteger6).add(this.Z_S).multiply(bigInteger7);
        } else {
            BigInteger bigInteger9 = bigInteger6.multiply(bigInteger7).mod(bigInteger8);
            eCPoint = ECAlgorithms.sumOfTwoMultiplies(this.P, bigInteger9, this.Z_S, bigInteger7);
        }
        eCPoint = eCPoint.subtract(eCPoint2);
        if (!eCPoint.isInfinity()) {
            throw new IllegalStateException("Validation of R_bS failed");
        }
        return BigIntegers.asUnsignedByteArray(this.n / 8, bigInteger5);
    }

    @Override
    public int getEncapsulationLength() {
        return 273;
    }

    static BigInteger computePairing(ECPoint eCPoint, ECPoint eCPoint2, BigInteger bigInteger, BigInteger bigInteger2) {
        BigInteger[] bigIntegerArray = new BigInteger[]{BigInteger.ONE, BigInteger.ZERO};
        ECPoint eCPoint3 = eCPoint;
        BigInteger bigInteger3 = bigInteger2.subtract(BigInteger.ONE);
        int n = bigInteger3.bitLength();
        BigInteger bigInteger4 = eCPoint2.getAffineXCoord().toBigInteger();
        BigInteger bigInteger5 = eCPoint2.getAffineYCoord().toBigInteger();
        BigInteger bigInteger6 = eCPoint.getAffineXCoord().toBigInteger();
        BigInteger bigInteger7 = eCPoint.getAffineYCoord().toBigInteger();
        BigInteger bigInteger8 = BigInteger.valueOf(3L);
        for (int i = n - 2; i >= 0; --i) {
            BigInteger bigInteger9 = eCPoint3.getAffineXCoord().toBigInteger();
            BigInteger bigInteger10 = eCPoint3.getAffineYCoord().toBigInteger();
            BigInteger bigInteger11 = bigInteger9.multiply(bigInteger9).mod(bigInteger).subtract(BigInteger.ONE).multiply(bigInteger8).multiply(BigIntegers.modOddInverse(bigInteger, bigInteger10.shiftLeft(1))).mod(bigInteger);
            bigIntegerArray = SAKKEKEMExtractor.fp2PointSquare(bigIntegerArray[0], bigIntegerArray[1], bigInteger);
            bigIntegerArray = SAKKEKEMExtractor.fp2Multiply(bigIntegerArray[0], bigIntegerArray[1], bigInteger11.multiply(bigInteger4.add(bigInteger9)).subtract(bigInteger10).mod(bigInteger), bigInteger5, bigInteger);
            eCPoint3 = eCPoint3.twice().normalize();
            if (!bigInteger3.testBit(i)) continue;
            bigInteger9 = eCPoint3.getAffineXCoord().toBigInteger();
            bigInteger10 = eCPoint3.getAffineYCoord().toBigInteger();
            bigInteger11 = bigInteger10.subtract(bigInteger7).multiply(BigIntegers.modOddInverse(bigInteger, bigInteger9.subtract(bigInteger6))).mod(bigInteger);
            bigIntegerArray = SAKKEKEMExtractor.fp2Multiply(bigIntegerArray[0], bigIntegerArray[1], bigInteger11.multiply(bigInteger4.add(bigInteger9)).subtract(bigInteger10).mod(bigInteger), bigInteger5, bigInteger);
            if (i <= 0) continue;
            eCPoint3 = eCPoint3.add(eCPoint).normalize();
        }
        bigIntegerArray = SAKKEKEMExtractor.fp2PointSquare(bigIntegerArray[0], bigIntegerArray[1], bigInteger);
        bigIntegerArray = SAKKEKEMExtractor.fp2PointSquare(bigIntegerArray[0], bigIntegerArray[1], bigInteger);
        BigInteger bigInteger12 = BigIntegers.modOddInverse(bigInteger, bigIntegerArray[0]);
        return bigIntegerArray[1].multiply(bigInteger12).mod(bigInteger);
    }

    static BigInteger[] fp2Multiply(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3, BigInteger bigInteger4, BigInteger bigInteger5) {
        return new BigInteger[]{bigInteger.multiply(bigInteger3).subtract(bigInteger2.multiply(bigInteger4)).mod(bigInteger5), bigInteger.multiply(bigInteger4).add(bigInteger2.multiply(bigInteger3)).mod(bigInteger5)};
    }

    static BigInteger[] fp2PointSquare(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        return new BigInteger[]{bigInteger.add(bigInteger2).multiply(bigInteger.subtract(bigInteger2)).mod(bigInteger3), bigInteger.multiply(bigInteger2).shiftLeft(1).mod(bigInteger3)};
    }
}

