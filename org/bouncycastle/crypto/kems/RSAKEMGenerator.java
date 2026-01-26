/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.SecretWithEncapsulationImpl;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSAKEMGenerator
implements EncapsulatedSecretGenerator {
    private static final BigInteger ZERO = BigInteger.valueOf(0L);
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private final int keyLen;
    private DerivationFunction kdf;
    private SecureRandom rnd;

    public RSAKEMGenerator(int n, DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.keyLen = n;
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        RSAKeyParameters rSAKeyParameters = (RSAKeyParameters)asymmetricKeyParameter;
        if (rSAKeyParameters.isPrivate()) {
            throw new IllegalArgumentException("public key required for encryption");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(rSAKeyParameters.getModulus()), rSAKeyParameters, CryptoServicePurpose.ENCRYPTION));
        BigInteger bigInteger = rSAKeyParameters.getModulus();
        BigInteger bigInteger2 = rSAKeyParameters.getExponent();
        BigInteger bigInteger3 = BigIntegers.createRandomInRange(ZERO, bigInteger.subtract(ONE), this.rnd);
        BigInteger bigInteger4 = bigInteger3.modPow(bigInteger2, bigInteger);
        byte[] byArray = BigIntegers.asUnsignedByteArray((bigInteger.bitLength() + 7) / 8, bigInteger4);
        return new SecretWithEncapsulationImpl(RSAKEMGenerator.generateKey(this.kdf, bigInteger, bigInteger3, this.keyLen), byArray);
    }

    static byte[] generateKey(DerivationFunction derivationFunction, BigInteger bigInteger, BigInteger bigInteger2, int n) {
        byte[] byArray = BigIntegers.asUnsignedByteArray((bigInteger.bitLength() + 7) / 8, bigInteger2);
        derivationFunction.init(new KDFParameters(byArray, null));
        byte[] byArray2 = new byte[n];
        derivationFunction.generateBytes(byArray2, 0, byArray2.length);
        return byArray2;
    }
}

