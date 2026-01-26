/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.agreement.ecjpake.ECSchnorrZKP;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class ECJPAKEUtil {
    static final BigInteger ZERO = BigInteger.valueOf(0L);
    static final BigInteger ONE = BigInteger.valueOf(1L);

    public static BigInteger generateX1(BigInteger bigInteger, SecureRandom secureRandom) {
        BigInteger bigInteger2 = ONE;
        BigInteger bigInteger3 = bigInteger.subtract(ONE);
        return BigIntegers.createRandomInRange(bigInteger2, bigInteger3, secureRandom);
    }

    public static BigInteger calculateS(BigInteger bigInteger, byte[] byArray) throws CryptoException {
        BigInteger bigInteger2 = new BigInteger(1, byArray).mod(bigInteger);
        if (bigInteger2.signum() == 0) {
            throw new CryptoException("MUST ensure s is not equal to 0 modulo n");
        }
        return bigInteger2;
    }

    public static BigInteger calculateS(BigInteger bigInteger, char[] cArray) throws CryptoException {
        return ECJPAKEUtil.calculateS(bigInteger, Strings.toUTF8ByteArray(cArray));
    }

    public static ECPoint calculateGx(ECPoint eCPoint, BigInteger bigInteger) {
        return eCPoint.multiply(bigInteger);
    }

    public static ECPoint calculateGA(ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3) {
        return eCPoint.add(eCPoint2).add(eCPoint3);
    }

    public static BigInteger calculateX2s(BigInteger bigInteger, BigInteger bigInteger2, BigInteger bigInteger3) {
        return bigInteger2.multiply(bigInteger3).mod(bigInteger);
    }

    public static ECPoint calculateA(ECPoint eCPoint, BigInteger bigInteger) {
        return eCPoint.multiply(bigInteger);
    }

    public static ECSchnorrZKP calculateZeroKnowledgeProof(ECPoint eCPoint, BigInteger bigInteger, BigInteger bigInteger2, ECPoint eCPoint2, Digest digest, String string, SecureRandom secureRandom) {
        BigInteger bigInteger3 = BigIntegers.createRandomInRange(BigInteger.ONE, bigInteger.subtract(BigInteger.ONE), secureRandom);
        ECPoint eCPoint3 = eCPoint.multiply(bigInteger3);
        BigInteger bigInteger4 = ECJPAKEUtil.calculateHashForZeroKnowledgeProof(eCPoint, eCPoint3, eCPoint2, string, digest);
        return new ECSchnorrZKP(eCPoint3, bigInteger3.subtract(bigInteger2.multiply(bigInteger4)).mod(bigInteger));
    }

    private static BigInteger calculateHashForZeroKnowledgeProof(ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, String string, Digest digest) {
        digest.reset();
        ECJPAKEUtil.updateDigestIncludingSize(digest, eCPoint);
        ECJPAKEUtil.updateDigestIncludingSize(digest, eCPoint2);
        ECJPAKEUtil.updateDigestIncludingSize(digest, eCPoint3);
        ECJPAKEUtil.updateDigestIncludingSize(digest, string);
        byte[] byArray = new byte[digest.getDigestSize()];
        digest.doFinal(byArray, 0);
        return new BigInteger(byArray);
    }

    private static void updateDigestIncludingSize(Digest digest, ECPoint eCPoint) {
        byte[] byArray = eCPoint.getEncoded(true);
        digest.update(ECJPAKEUtil.intToByteArray(byArray.length), 0, 4);
        digest.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static void updateDigestIncludingSize(Digest digest, String string) {
        byte[] byArray = Strings.toUTF8ByteArray(string);
        digest.update(ECJPAKEUtil.intToByteArray(byArray.length), 0, 4);
        digest.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    public static void validateZeroKnowledgeProof(ECPoint eCPoint, ECPoint eCPoint2, ECSchnorrZKP eCSchnorrZKP, BigInteger bigInteger, BigInteger bigInteger2, ECCurve eCCurve, BigInteger bigInteger3, String string, Digest digest) throws CryptoException {
        ECPoint eCPoint3 = eCSchnorrZKP.getV();
        BigInteger bigInteger4 = eCSchnorrZKP.getr();
        BigInteger bigInteger5 = ECJPAKEUtil.calculateHashForZeroKnowledgeProof(eCPoint, eCPoint3, eCPoint2, string, digest);
        if (eCPoint2.isInfinity()) {
            throw new CryptoException("Zero-knowledge proof validation failed: X cannot equal infinity");
        }
        ECPoint eCPoint4 = eCPoint2.normalize();
        if (eCPoint4.getAffineXCoord().toBigInteger().signum() < 0 || eCPoint4.getAffineXCoord().toBigInteger().compareTo(bigInteger) >= 0 || eCPoint4.getAffineYCoord().toBigInteger().signum() < 0 || eCPoint4.getAffineYCoord().toBigInteger().compareTo(bigInteger) >= 0) {
            throw new CryptoException("Zero-knowledge proof validation failed: x and y are not in the field");
        }
        try {
            eCCurve.decodePoint(eCPoint2.getEncoded(true));
        }
        catch (Exception exception) {
            throw new CryptoException("Zero-knowledge proof validation failed: x does not lie on the curve", exception);
        }
        if (eCPoint2.multiply(bigInteger3).isInfinity()) {
            throw new CryptoException("Zero-knowledge proof validation failed: Nx cannot be infinity");
        }
        if (!eCPoint3.equals(eCPoint.multiply(bigInteger4).add(eCPoint2.multiply(bigInteger5.mod(bigInteger2))))) {
            throw new CryptoException("Zero-knowledge proof validation failed: V must be a point on the curve");
        }
    }

    public static void validateParticipantIdsDiffer(String string, String string2) throws CryptoException {
        if (string.equals(string2)) {
            throw new CryptoException("Both participants are using the same participantId (" + string + "). This is not allowed. Each participant must use a unique participantId.");
        }
    }

    public static void validateParticipantIdsEqual(String string, String string2) throws CryptoException {
        if (!string.equals(string2)) {
            throw new CryptoException("Received payload from incorrect partner (" + string2 + "). Expected to receive payload from " + string + ".");
        }
    }

    public static void validateNotNull(Object object, String string) {
        if (object == null) {
            throw new NullPointerException(string + " must not be null");
        }
    }

    public static BigInteger calculateKeyingMaterial(BigInteger bigInteger, ECPoint eCPoint, BigInteger bigInteger2, BigInteger bigInteger3, ECPoint eCPoint2) {
        ECPoint eCPoint3 = eCPoint2.subtract(eCPoint.multiply(bigInteger2.multiply(bigInteger3).mod(bigInteger))).multiply(bigInteger2);
        eCPoint3 = eCPoint3.normalize();
        return eCPoint3.getAffineXCoord().toBigInteger();
    }

    public static BigInteger calculateMacTag(String string, String string2, ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, ECPoint eCPoint4, BigInteger bigInteger, Digest digest) {
        byte[] byArray = ECJPAKEUtil.calculateMacKey(bigInteger, digest);
        HMac hMac = new HMac(digest);
        byte[] byArray2 = new byte[hMac.getMacSize()];
        hMac.init(new KeyParameter(byArray));
        ECJPAKEUtil.updateMac((Mac)hMac, "KC_1_U");
        ECJPAKEUtil.updateMac((Mac)hMac, string);
        ECJPAKEUtil.updateMac((Mac)hMac, string2);
        ECJPAKEUtil.updateMac((Mac)hMac, eCPoint);
        ECJPAKEUtil.updateMac((Mac)hMac, eCPoint2);
        ECJPAKEUtil.updateMac((Mac)hMac, eCPoint3);
        ECJPAKEUtil.updateMac((Mac)hMac, eCPoint4);
        hMac.doFinal(byArray2, 0);
        Arrays.fill(byArray, (byte)0);
        return new BigInteger(byArray2);
    }

    private static byte[] calculateMacKey(BigInteger bigInteger, Digest digest) {
        digest.reset();
        ECJPAKEUtil.updateDigest(digest, bigInteger);
        ECJPAKEUtil.updateDigest(digest, "ECJPAKE_KC");
        byte[] byArray = new byte[digest.getDigestSize()];
        digest.doFinal(byArray, 0);
        return byArray;
    }

    public static void validateMacTag(String string, String string2, ECPoint eCPoint, ECPoint eCPoint2, ECPoint eCPoint3, ECPoint eCPoint4, BigInteger bigInteger, Digest digest, BigInteger bigInteger2) throws CryptoException {
        BigInteger bigInteger3 = ECJPAKEUtil.calculateMacTag(string2, string, eCPoint3, eCPoint4, eCPoint, eCPoint2, bigInteger, digest);
        if (!bigInteger3.equals(bigInteger2)) {
            throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
        }
    }

    private static void updateMac(Mac mac, ECPoint eCPoint) {
        byte[] byArray = eCPoint.getEncoded(true);
        mac.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static void updateMac(Mac mac, String string) {
        byte[] byArray = Strings.toUTF8ByteArray(string);
        mac.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static void updateDigest(Digest digest, ECPoint eCPoint) {
        byte[] byArray = eCPoint.getEncoded(true);
        digest.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static void updateDigest(Digest digest, String string) {
        byte[] byArray = Strings.toUTF8ByteArray(string);
        digest.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static void updateDigest(Digest digest, BigInteger bigInteger) {
        byte[] byArray = BigIntegers.asUnsignedByteArray(bigInteger);
        digest.update(byArray, 0, byArray.length);
        Arrays.fill(byArray, (byte)0);
    }

    private static byte[] intToByteArray(int n) {
        return new byte[]{(byte)(n >>> 24), (byte)(n >>> 16), (byte)(n >>> 8), (byte)n};
    }
}

