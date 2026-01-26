/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

public abstract class TlsRsaKeyExchange {
    public static final int PRE_MASTER_SECRET_LENGTH = 48;
    private static final BigInteger ONE = BigInteger.valueOf(1L);

    private TlsRsaKeyExchange() {
    }

    public static byte[] decryptPreMasterSecret(byte[] byArray, int n, int n2, RSAKeyParameters rSAKeyParameters, int n3, SecureRandom secureRandom) {
        if (byArray == null || n2 < 1 || n2 > TlsRsaKeyExchange.getInputLimit(rSAKeyParameters) || n < 0 || n > byArray.length - n2) {
            throw new IllegalArgumentException("input not a valid EncryptedPreMasterSecret");
        }
        if (!rSAKeyParameters.isPrivate()) {
            throw new IllegalArgumentException("'privateKey' must be an RSA private key");
        }
        BigInteger bigInteger = rSAKeyParameters.getModulus();
        int n4 = bigInteger.bitLength();
        if (n4 < 512) {
            throw new IllegalArgumentException("'privateKey' must be at least 512 bits");
        }
        int n5 = ConstraintUtils.bitsOfSecurityFor(bigInteger);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", n5, rSAKeyParameters, CryptoServicePurpose.DECRYPTION));
        if ((n3 & 0xFFFF) != n3) {
            throw new IllegalArgumentException("'protocolVersion' must be a 16 bit value");
        }
        secureRandom = CryptoServicesRegistrar.getSecureRandom(secureRandom);
        byte[] byArray2 = new byte[48];
        secureRandom.nextBytes(byArray2);
        try {
            BigInteger bigInteger2 = TlsRsaKeyExchange.convertInput(bigInteger, byArray, n, n2);
            byte[] byArray3 = TlsRsaKeyExchange.rsaBlinded(rSAKeyParameters, bigInteger2, secureRandom);
            int n6 = (n4 - 1) / 8;
            int n7 = byArray3.length - 48;
            int n8 = TlsRsaKeyExchange.checkPkcs1Encoding2(byArray3, n6, 48);
            int n9 = -((Pack.bigEndianToShort(byArray3, n7) ^ n3) & 0xFFFF) >> 31;
            int n10 = n8 | n9;
            for (int i = 0; i < 48; ++i) {
                byArray2[i] = (byte)(byArray2[i] & n10 | byArray3[n7 + i] & ~n10);
            }
            Arrays.fill(byArray3, (byte)0);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return byArray2;
    }

    public static int getInputLimit(RSAKeyParameters rSAKeyParameters) {
        return (rSAKeyParameters.getModulus().bitLength() + 7) / 8;
    }

    private static int caddTo(int n, int n2, byte[] byArray, byte[] byArray2) {
        int n3 = n2 & 0xFF;
        int n4 = 0;
        for (int i = n - 1; i >= 0; --i) {
            byArray2[i] = (byte)(n4 += (byArray2[i] & 0xFF) + (byArray[i] & n3));
            n4 >>>= 8;
        }
        return n4;
    }

    private static int checkPkcs1Encoding2(byte[] byArray, int n, int n2) {
        int n3;
        int n4 = n - n2 - 10;
        int n5 = byArray.length - n;
        int n6 = byArray.length - 1 - n2;
        for (n3 = 0; n3 < n5; ++n3) {
            n4 |= -(byArray[n3] & 0xFF);
        }
        n4 |= -(byArray[n5] & 0xFF ^ 2);
        for (n3 = n5 + 1; n3 < n6; ++n3) {
            n4 |= (byArray[n3] & 0xFF) - 1;
        }
        return (n4 |= -(byArray[n6] & 0xFF)) >> 31;
    }

    private static BigInteger convertInput(BigInteger bigInteger, byte[] byArray, int n, int n2) {
        BigInteger bigInteger2 = BigIntegers.fromUnsignedByteArray(byArray, n, n2);
        if (bigInteger2.compareTo(bigInteger) < 0) {
            return bigInteger2;
        }
        throw new DataLengthException("input too large for RSA cipher.");
    }

    private static BigInteger rsa(RSAKeyParameters rSAKeyParameters, BigInteger bigInteger) {
        return bigInteger.modPow(rSAKeyParameters.getExponent(), rSAKeyParameters.getModulus());
    }

    private static byte[] rsaBlinded(RSAKeyParameters rSAKeyParameters, BigInteger bigInteger, SecureRandom secureRandom) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger bigInteger2;
        BigInteger bigInteger3 = rSAKeyParameters.getModulus();
        int n = bigInteger3.bitLength() / 8 + 1;
        if (rSAKeyParameters instanceof RSAPrivateCrtKeyParameters && (bigInteger2 = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)rSAKeyParameters).getPublicExponent()) != null) {
            BigInteger bigInteger4 = BigIntegers.createRandomInRange(ONE, bigInteger3.subtract(ONE), secureRandom);
            BigInteger bigInteger5 = bigInteger4.modPow(bigInteger2, bigInteger3);
            BigInteger bigInteger6 = BigIntegers.modOddInverse(bigInteger3, bigInteger4);
            BigInteger bigInteger7 = bigInteger5.multiply(bigInteger).mod(bigInteger3);
            BigInteger bigInteger8 = TlsRsaKeyExchange.rsaCrt(rSAPrivateCrtKeyParameters, bigInteger7);
            BigInteger bigInteger9 = bigInteger6.add(ONE).multiply(bigInteger8).mod(bigInteger3);
            byte[] byArray = TlsRsaKeyExchange.toBytes(bigInteger8, n);
            byte[] byArray2 = TlsRsaKeyExchange.toBytes(bigInteger3, n);
            byte[] byArray3 = TlsRsaKeyExchange.toBytes(bigInteger9, n);
            int n2 = TlsRsaKeyExchange.subFrom(n, byArray, byArray3);
            TlsRsaKeyExchange.caddTo(n, n2, byArray2, byArray3);
            return byArray3;
        }
        return TlsRsaKeyExchange.toBytes(TlsRsaKeyExchange.rsa(rSAKeyParameters, bigInteger), n);
    }

    private static BigInteger rsaCrt(RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters, BigInteger bigInteger) {
        BigInteger bigInteger2 = rSAPrivateCrtKeyParameters.getPublicExponent();
        BigInteger bigInteger3 = rSAPrivateCrtKeyParameters.getP();
        BigInteger bigInteger4 = rSAPrivateCrtKeyParameters.getQ();
        BigInteger bigInteger5 = rSAPrivateCrtKeyParameters.getDP();
        BigInteger bigInteger6 = rSAPrivateCrtKeyParameters.getDQ();
        BigInteger bigInteger7 = rSAPrivateCrtKeyParameters.getQInv();
        BigInteger bigInteger8 = bigInteger.remainder(bigInteger3).modPow(bigInteger5, bigInteger3);
        BigInteger bigInteger9 = bigInteger.remainder(bigInteger4).modPow(bigInteger6, bigInteger4);
        BigInteger bigInteger10 = bigInteger8.subtract(bigInteger9);
        bigInteger10 = bigInteger10.multiply(bigInteger7);
        BigInteger bigInteger11 = (bigInteger10 = bigInteger10.mod(bigInteger3)).multiply(bigInteger4).add(bigInteger9);
        BigInteger bigInteger12 = bigInteger11.modPow(bigInteger2, rSAPrivateCrtKeyParameters.getModulus());
        if (!bigInteger12.equals(bigInteger)) {
            throw new IllegalStateException("RSA engine faulty decryption/signing detected");
        }
        return bigInteger11;
    }

    private static int subFrom(int n, byte[] byArray, byte[] byArray2) {
        int n2 = 0;
        for (int i = n - 1; i >= 0; --i) {
            byArray2[i] = (byte)(n2 += (byArray2[i] & 0xFF) - (byArray[i] & 0xFF));
            n2 >>= 8;
        }
        return n2;
    }

    private static byte[] toBytes(BigInteger bigInteger, int n) {
        byte[] byArray = bigInteger.toByteArray();
        byte[] byArray2 = new byte[n];
        System.arraycopy(byArray, 0, byArray2, byArray2.length - byArray.length, byArray.length);
        return byArray2;
    }
}

