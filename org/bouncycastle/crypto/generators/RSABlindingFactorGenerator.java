/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindingFactorGenerator {
    private static BigInteger TWO = BigInteger.valueOf(2L);
    private RSAKeyParameters key;
    private SecureRandom random;

    public void init(CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.key = (RSAKeyParameters)parametersWithRandom.getParameters();
            this.random = parametersWithRandom.getRandom();
        } else {
            this.key = (RSAKeyParameters)cipherParameters;
            this.random = CryptoServicesRegistrar.getSecureRandom();
        }
        if (this.key instanceof RSAPrivateCrtKeyParameters) {
            throw new IllegalArgumentException("generator requires RSA public key");
        }
    }

    public BigInteger generateBlindingFactor() {
        BigInteger bigInteger;
        if (this.key == null) {
            throw new IllegalStateException("generator not initialised");
        }
        BigInteger bigInteger2 = this.key.getModulus();
        int n = bigInteger2.bitLength() - 1;
        while ((bigInteger = BigIntegers.createRandomBigInteger(n, this.random)).compareTo(TWO) < 0 || !BigIntegers.modOddIsCoprime(bigInteger2, bigInteger)) {
        }
        return bigInteger;
    }
}

