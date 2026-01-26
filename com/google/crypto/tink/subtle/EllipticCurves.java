/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.subtle;

import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.EllipticCurvesUtil;
import com.google.crypto.tink.subtle.EngineFactory;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import javax.crypto.KeyAgreement;

public final class EllipticCurves {
    public static ECParameterSpec getNistP256Params() {
        return EllipticCurvesUtil.NIST_P256_PARAMS;
    }

    public static ECParameterSpec getNistP384Params() {
        return EllipticCurvesUtil.NIST_P384_PARAMS;
    }

    public static ECParameterSpec getNistP521Params() {
        return EllipticCurvesUtil.NIST_P521_PARAMS;
    }

    static void checkPublicKey(ECPublicKey key) throws GeneralSecurityException {
        EllipticCurvesUtil.checkPointOnCurve(key.getW(), key.getParams().getCurve());
    }

    public static boolean isNistEcParameterSpec(ECParameterSpec spec) {
        return EllipticCurvesUtil.isNistEcParameterSpec(spec);
    }

    public static boolean isSameEcParameterSpec(ECParameterSpec one, ECParameterSpec two) {
        return EllipticCurvesUtil.isSameEcParameterSpec(one, two);
    }

    public static void validatePublicKey(ECPublicKey publicKey, ECPrivateKey privateKey) throws GeneralSecurityException {
        EllipticCurves.validatePublicKeySpec(publicKey, privateKey);
        EllipticCurvesUtil.checkPointOnCurve(publicKey.getW(), privateKey.getParams().getCurve());
    }

    static void validatePublicKeySpec(ECPublicKey publicKey, ECPrivateKey privateKey) throws GeneralSecurityException {
        try {
            ECParameterSpec publicKeySpec = publicKey.getParams();
            ECParameterSpec privateKeySpec = privateKey.getParams();
            if (!EllipticCurves.isSameEcParameterSpec(publicKeySpec, privateKeySpec)) {
                throw new GeneralSecurityException("invalid public key spec");
            }
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    public static BigInteger getModulus(EllipticCurve curve) throws GeneralSecurityException {
        return EllipticCurvesUtil.getModulus(curve);
    }

    public static int fieldSizeInBits(EllipticCurve curve) throws GeneralSecurityException {
        return EllipticCurves.getModulus(curve).subtract(BigInteger.ONE).bitLength();
    }

    public static int fieldSizeInBytes(EllipticCurve curve) throws GeneralSecurityException {
        return (EllipticCurves.fieldSizeInBits(curve) + 7) / 8;
    }

    private static BigInteger modSqrt(BigInteger x, BigInteger p) throws GeneralSecurityException {
        if (p.signum() != 1) {
            throw new InvalidAlgorithmParameterException("p must be positive");
        }
        x = x.mod(p);
        BigInteger squareRoot = null;
        if (x.equals(BigInteger.ZERO)) {
            return BigInteger.ZERO;
        }
        if (p.testBit(0) && p.testBit(1)) {
            BigInteger q = p.add(BigInteger.ONE).shiftRight(2);
            squareRoot = x.modPow(q, p);
        } else if (p.testBit(0) && !p.testBit(1)) {
            BigInteger d;
            BigInteger a;
            block10: {
                a = BigInteger.ONE;
                d = null;
                BigInteger q1 = p.subtract(BigInteger.ONE).shiftRight(1);
                int tries = 0;
                do {
                    if ((d = a.multiply(a).subtract(x).mod(p)).equals(BigInteger.ZERO)) {
                        return a;
                    }
                    BigInteger t = d.modPow(q1, p);
                    if (t.add(BigInteger.ONE).equals(p)) break block10;
                    if (!t.equals(BigInteger.ONE)) {
                        throw new InvalidAlgorithmParameterException("p is not prime");
                    }
                    a = a.add(BigInteger.ONE);
                } while (++tries != 128 || p.isProbablePrime(80));
                throw new InvalidAlgorithmParameterException("p is not prime");
            }
            BigInteger q = p.add(BigInteger.ONE).shiftRight(1);
            BigInteger u = a;
            BigInteger v = BigInteger.ONE;
            for (int bit = q.bitLength() - 2; bit >= 0; --bit) {
                BigInteger tmp = u.multiply(v);
                u = u.multiply(u).add(v.multiply(v).mod(p).multiply(d)).mod(p);
                v = tmp.add(tmp).mod(p);
                if (!q.testBit(bit)) continue;
                tmp = u.multiply(a).add(v.multiply(d)).mod(p);
                v = a.multiply(v).add(u).mod(p);
                u = tmp;
            }
            squareRoot = u;
        }
        if (squareRoot != null && squareRoot.multiply(squareRoot).mod(p).compareTo(x) != 0) {
            throw new GeneralSecurityException("Could not find a modular square root");
        }
        return squareRoot;
    }

    private static BigInteger computeY(BigInteger x, boolean lsb, EllipticCurve curve) throws GeneralSecurityException {
        BigInteger p = EllipticCurves.getModulus(curve);
        BigInteger a = curve.getA();
        BigInteger b = curve.getB();
        BigInteger rhs = x.multiply(x).add(a).multiply(x).add(b).mod(p);
        BigInteger y = EllipticCurves.modSqrt(rhs, p);
        if (lsb != y.testBit(0)) {
            y = p.subtract(y).mod(p);
        }
        return y;
    }

    @Deprecated
    public static BigInteger getY(BigInteger x, boolean lsb, EllipticCurve curve) throws GeneralSecurityException {
        return EllipticCurves.computeY(x, lsb, curve);
    }

    private static byte[] toMinimalSignedNumber(byte[] bs) {
        int start;
        for (start = 0; start < bs.length && bs[start] == 0; ++start) {
        }
        if (start == bs.length) {
            start = bs.length - 1;
        }
        int extraZero = 0;
        if ((bs[start] & 0x80) == 128) {
            extraZero = 1;
        }
        byte[] res = new byte[bs.length - start + extraZero];
        System.arraycopy(bs, start, res, extraZero, bs.length - start);
        return res;
    }

    public static byte[] ecdsaIeee2Der(byte[] ieee) throws GeneralSecurityException {
        byte[] der;
        if (ieee.length % 2 != 0 || ieee.length == 0 || ieee.length > 132) {
            throw new GeneralSecurityException("Invalid IEEE_P1363 encoding");
        }
        byte[] r = EllipticCurves.toMinimalSignedNumber(Arrays.copyOf(ieee, ieee.length / 2));
        byte[] s = EllipticCurves.toMinimalSignedNumber(Arrays.copyOfRange(ieee, ieee.length / 2, ieee.length));
        int offset = 0;
        int length = 2 + r.length + 1 + 1 + s.length;
        if (length >= 128) {
            der = new byte[length + 3];
            der[offset++] = 48;
            der[offset++] = -127;
            der[offset++] = (byte)length;
        } else {
            der = new byte[length + 2];
            der[offset++] = 48;
            der[offset++] = (byte)length;
        }
        der[offset++] = 2;
        der[offset++] = (byte)r.length;
        System.arraycopy(r, 0, der, offset, r.length);
        offset += r.length;
        der[offset++] = 2;
        der[offset++] = (byte)s.length;
        System.arraycopy(s, 0, der, offset, s.length);
        return der;
    }

    public static byte[] ecdsaDer2Ieee(byte[] der, int ieeeLength) throws GeneralSecurityException {
        if (!EllipticCurves.isValidDerEncoding(der)) {
            throw new GeneralSecurityException("Invalid DER encoding");
        }
        byte[] ieee = new byte[ieeeLength];
        int length = der[1] & 0xFF;
        int offset = 2;
        if (length >= 128) {
            ++offset;
        }
        int n = ++offset;
        byte rLength = der[n];
        int extraZero = 0;
        if (der[++offset] == 0) {
            extraZero = 1;
        }
        System.arraycopy(der, offset + extraZero, ieee, ieeeLength / 2 - rLength + extraZero, rLength - extraZero);
        offset += rLength + 1;
        byte sLength = der[offset++];
        extraZero = 0;
        if (der[offset] == 0) {
            extraZero = 1;
        }
        System.arraycopy(der, offset + extraZero, ieee, ieeeLength - sLength + extraZero, sLength - extraZero);
        return ieee;
    }

    public static boolean isValidDerEncoding(byte[] sig) {
        if (sig.length < 8) {
            return false;
        }
        if (sig[0] != 48) {
            return false;
        }
        int totalLen = sig[1] & 0xFF;
        int totalLenLen = 1;
        if (totalLen == 129) {
            totalLenLen = 2;
            totalLen = sig[2] & 0xFF;
            if (totalLen < 128) {
                return false;
            }
        } else if (totalLen == 128 || totalLen > 129) {
            return false;
        }
        if (totalLen != sig.length - 1 - totalLenLen) {
            return false;
        }
        if (sig[1 + totalLenLen] != 2) {
            return false;
        }
        int rLen = sig[1 + totalLenLen + 1] & 0xFF;
        if (1 + totalLenLen + 1 + 1 + rLen + 1 >= sig.length) {
            return false;
        }
        if (rLen == 0) {
            return false;
        }
        if ((sig[3 + totalLenLen] & 0xFF) >= 128) {
            return false;
        }
        if (rLen > 1 && sig[3 + totalLenLen] == 0 && (sig[4 + totalLenLen] & 0xFF) < 128) {
            return false;
        }
        if (sig[3 + totalLenLen + rLen] != 2) {
            return false;
        }
        int sLen = sig[1 + totalLenLen + 1 + 1 + rLen + 1] & 0xFF;
        if (1 + totalLenLen + 1 + 1 + rLen + 1 + 1 + sLen != sig.length) {
            return false;
        }
        if (sLen == 0) {
            return false;
        }
        if ((sig[5 + totalLenLen + rLen] & 0xFF) >= 128) {
            return false;
        }
        return sLen <= 1 || sig[5 + totalLenLen + rLen] != 0 || (sig[6 + totalLenLen + rLen] & 0xFF) >= 128;
    }

    public static int encodingSizeInBytes(EllipticCurve curve, PointFormatType format) throws GeneralSecurityException {
        int coordinateSize = EllipticCurves.fieldSizeInBytes(curve);
        switch (format.ordinal()) {
            case 0: {
                return 2 * coordinateSize + 1;
            }
            case 2: {
                return 2 * coordinateSize;
            }
            case 1: {
                return coordinateSize + 1;
            }
        }
        throw new GeneralSecurityException("unknown EC point format");
    }

    public static ECPoint ecPointDecode(EllipticCurve curve, PointFormatType format, byte[] encoded) throws GeneralSecurityException {
        return EllipticCurves.pointDecode(curve, format, encoded);
    }

    public static ECPoint pointDecode(CurveType curveType, PointFormatType format, byte[] encoded) throws GeneralSecurityException {
        return EllipticCurves.pointDecode(EllipticCurves.getCurveSpec(curveType).getCurve(), format, encoded);
    }

    public static ECPoint pointDecode(EllipticCurve curve, PointFormatType format, byte[] encoded) throws GeneralSecurityException {
        int coordinateSize = EllipticCurves.fieldSizeInBytes(curve);
        switch (format.ordinal()) {
            case 0: {
                if (encoded.length != 2 * coordinateSize + 1) {
                    throw new GeneralSecurityException("invalid point size");
                }
                if (encoded[0] != 4) {
                    throw new GeneralSecurityException("invalid point format");
                }
                BigInteger x = new BigInteger(1, Arrays.copyOfRange(encoded, 1, coordinateSize + 1));
                BigInteger y = new BigInteger(1, Arrays.copyOfRange(encoded, coordinateSize + 1, encoded.length));
                ECPoint point = new ECPoint(x, y);
                EllipticCurvesUtil.checkPointOnCurve(point, curve);
                return point;
            }
            case 2: {
                if (encoded.length != 2 * coordinateSize) {
                    throw new GeneralSecurityException("invalid point size");
                }
                BigInteger x = new BigInteger(1, Arrays.copyOf(encoded, coordinateSize));
                BigInteger y = new BigInteger(1, Arrays.copyOfRange(encoded, coordinateSize, encoded.length));
                ECPoint point = new ECPoint(x, y);
                EllipticCurvesUtil.checkPointOnCurve(point, curve);
                return point;
            }
            case 1: {
                boolean lsb;
                BigInteger p = EllipticCurves.getModulus(curve);
                if (encoded.length != coordinateSize + 1) {
                    throw new GeneralSecurityException("compressed point has wrong length");
                }
                if (encoded[0] == 2) {
                    lsb = false;
                } else if (encoded[0] == 3) {
                    lsb = true;
                } else {
                    throw new GeneralSecurityException("invalid format");
                }
                BigInteger x = new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length));
                if (x.signum() == -1 || x.compareTo(p) >= 0) {
                    throw new GeneralSecurityException("x is out of range");
                }
                BigInteger y = EllipticCurves.computeY(x, lsb, curve);
                return new ECPoint(x, y);
            }
        }
        throw new GeneralSecurityException("invalid format:" + (Object)((Object)format));
    }

    public static byte[] pointEncode(CurveType curveType, PointFormatType format, ECPoint point) throws GeneralSecurityException {
        return EllipticCurves.pointEncode(EllipticCurves.getCurveSpec(curveType).getCurve(), format, point);
    }

    public static byte[] pointEncode(EllipticCurve curve, PointFormatType format, ECPoint point) throws GeneralSecurityException {
        EllipticCurvesUtil.checkPointOnCurve(point, curve);
        int coordinateSize = EllipticCurves.fieldSizeInBytes(curve);
        switch (format.ordinal()) {
            case 0: {
                byte[] encoded = new byte[2 * coordinateSize + 1];
                byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
                byte[] y = BigIntegerEncoding.toBigEndianBytes(point.getAffineY());
                System.arraycopy(y, 0, encoded, 1 + 2 * coordinateSize - y.length, y.length);
                System.arraycopy(x, 0, encoded, 1 + coordinateSize - x.length, x.length);
                encoded[0] = 4;
                return encoded;
            }
            case 2: {
                byte[] y;
                byte[] encoded = new byte[2 * coordinateSize];
                byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
                if (x.length > coordinateSize) {
                    x = Arrays.copyOfRange(x, x.length - coordinateSize, x.length);
                }
                if ((y = BigIntegerEncoding.toBigEndianBytes(point.getAffineY())).length > coordinateSize) {
                    y = Arrays.copyOfRange(y, y.length - coordinateSize, y.length);
                }
                System.arraycopy(y, 0, encoded, 2 * coordinateSize - y.length, y.length);
                System.arraycopy(x, 0, encoded, coordinateSize - x.length, x.length);
                return encoded;
            }
            case 1: {
                byte[] encoded = new byte[coordinateSize + 1];
                byte[] x = BigIntegerEncoding.toBigEndianBytes(point.getAffineX());
                System.arraycopy(x, 0, encoded, 1 + coordinateSize - x.length, x.length);
                encoded[0] = (byte)(point.getAffineY().testBit(0) ? 3 : 2);
                return encoded;
            }
        }
        throw new GeneralSecurityException("invalid format:" + (Object)((Object)format));
    }

    public static ECParameterSpec getCurveSpec(CurveType curve) throws NoSuchAlgorithmException {
        switch (curve.ordinal()) {
            case 0: {
                return EllipticCurves.getNistP256Params();
            }
            case 1: {
                return EllipticCurves.getNistP384Params();
            }
            case 2: {
                return EllipticCurves.getNistP521Params();
            }
        }
        throw new NoSuchAlgorithmException("curve not implemented:" + (Object)((Object)curve));
    }

    public static ECPublicKey getEcPublicKey(byte[] x509PublicKey) throws GeneralSecurityException {
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        return (ECPublicKey)kf.generatePublic(new X509EncodedKeySpec(x509PublicKey));
    }

    public static ECPublicKey getEcPublicKey(CurveType curve, PointFormatType pointFormat, byte[] publicKey) throws GeneralSecurityException {
        return EllipticCurves.getEcPublicKey(EllipticCurves.getCurveSpec(curve), pointFormat, publicKey);
    }

    public static ECPublicKey getEcPublicKey(ECParameterSpec spec, PointFormatType pointFormat, byte[] publicKey) throws GeneralSecurityException {
        ECPoint point = EllipticCurves.pointDecode(spec.getCurve(), pointFormat, publicKey);
        ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, spec);
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        return (ECPublicKey)kf.generatePublic(pubSpec);
    }

    public static ECPublicKey getEcPublicKey(CurveType curve, byte[] x, byte[] y) throws GeneralSecurityException {
        ECParameterSpec ecParams = EllipticCurves.getCurveSpec(curve);
        BigInteger pubX = new BigInteger(1, x);
        BigInteger pubY = new BigInteger(1, y);
        ECPoint w = new ECPoint(pubX, pubY);
        EllipticCurvesUtil.checkPointOnCurve(w, ecParams.getCurve());
        ECPublicKeySpec spec = new ECPublicKeySpec(w, ecParams);
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        return (ECPublicKey)kf.generatePublic(spec);
    }

    public static ECPrivateKey getEcPrivateKey(byte[] pkcs8PrivateKey) throws GeneralSecurityException {
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        return (ECPrivateKey)kf.generatePrivate(new PKCS8EncodedKeySpec(pkcs8PrivateKey));
    }

    public static ECPrivateKey getEcPrivateKey(CurveType curve, byte[] keyValue) throws GeneralSecurityException {
        ECParameterSpec ecParams = EllipticCurves.getCurveSpec(curve);
        BigInteger privValue = BigIntegerEncoding.fromUnsignedBigEndianBytes(keyValue);
        ECPrivateKeySpec spec = new ECPrivateKeySpec(privValue, ecParams);
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        return (ECPrivateKey)kf.generatePrivate(spec);
    }

    public static KeyPair generateKeyPair(CurveType curve) throws GeneralSecurityException {
        return EllipticCurves.generateKeyPair(EllipticCurves.getCurveSpec(curve));
    }

    public static KeyPair generateKeyPair(ECParameterSpec spec) throws GeneralSecurityException {
        KeyPairGenerator keyGen = EngineFactory.KEY_PAIR_GENERATOR.getInstance("EC");
        keyGen.initialize(spec);
        return keyGen.generateKeyPair();
    }

    static void validateSharedSecret(byte[] secret, ECPrivateKey privateKey) throws GeneralSecurityException {
        EllipticCurve privateKeyCurve = privateKey.getParams().getCurve();
        BigInteger x = new BigInteger(1, secret);
        if (x.signum() == -1 || x.compareTo(EllipticCurves.getModulus(privateKeyCurve)) >= 0) {
            throw new GeneralSecurityException("shared secret is out of range");
        }
        BigInteger unused = EllipticCurves.computeY(x, true, privateKeyCurve);
    }

    public static byte[] computeSharedSecret(ECPrivateKey myPrivateKey, ECPublicKey peerPublicKey) throws GeneralSecurityException {
        EllipticCurves.validatePublicKeySpec(peerPublicKey, myPrivateKey);
        return EllipticCurves.computeSharedSecret(myPrivateKey, peerPublicKey.getW());
    }

    public static byte[] computeSharedSecret(ECPrivateKey myPrivateKey, ECPoint publicPoint) throws GeneralSecurityException {
        EllipticCurvesUtil.checkPointOnCurve(publicPoint, myPrivateKey.getParams().getCurve());
        ECParameterSpec privSpec = myPrivateKey.getParams();
        ECPublicKeySpec publicKeySpec = new ECPublicKeySpec(publicPoint, privSpec);
        KeyFactory kf = EngineFactory.KEY_FACTORY.getInstance("EC");
        PublicKey publicKey = kf.generatePublic(publicKeySpec);
        KeyAgreement ka = EngineFactory.KEY_AGREEMENT.getInstance("ECDH");
        ka.init(myPrivateKey);
        try {
            ka.doPhase(publicKey, true);
            byte[] secret = ka.generateSecret();
            EllipticCurves.validateSharedSecret(secret, myPrivateKey);
            return secret;
        }
        catch (IllegalStateException ex) {
            throw new GeneralSecurityException(ex);
        }
    }

    private EllipticCurves() {
    }

    public static enum PointFormatType {
        UNCOMPRESSED,
        COMPRESSED,
        DO_NOT_USE_CRUNCHY_UNCOMPRESSED;

    }

    public static enum CurveType {
        NIST_P256,
        NIST_P384,
        NIST_P521;

    }

    public static enum EcdsaEncoding {
        IEEE_P1363,
        DER;

    }
}

