/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.engines.RSACoreEngine;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.BigIntegers;

public class RSABlindedEngine
implements AsymmetricBlockCipher {
    private static final BigInteger ONE = BigInteger.valueOf(1L);
    private RSACoreEngine core = new RSACoreEngine();
    private RSAKeyParameters key;
    private SecureRandom random;

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        SecureRandom secureRandom = null;
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            secureRandom = parametersWithRandom.getRandom();
            cipherParameters = parametersWithRandom.getParameters();
        }
        this.core.init(bl, cipherParameters);
        this.key = (RSAKeyParameters)cipherParameters;
        this.random = this.initSecureRandom(this.key instanceof RSAPrivateCrtKeyParameters, secureRandom);
    }

    @Override
    public int getInputBlockSize() {
        return this.core.getInputBlockSize();
    }

    @Override
    public int getOutputBlockSize() {
        return this.core.getOutputBlockSize();
    }

    @Override
    public byte[] processBlock(byte[] byArray, int n, int n2) {
        if (this.key == null) {
            throw new IllegalStateException("RSA engine not initialised");
        }
        BigInteger bigInteger = this.core.convertInput(byArray, n, n2);
        BigInteger bigInteger2 = this.processInput(bigInteger);
        return this.core.convertOutput(bigInteger2);
    }

    protected SecureRandom initSecureRandom(boolean bl, SecureRandom secureRandom) {
        return bl ? CryptoServicesRegistrar.getSecureRandom(secureRandom) : null;
    }

    private BigInteger processInput(BigInteger bigInteger) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger bigInteger2;
        if (this.key instanceof RSAPrivateCrtKeyParameters && (bigInteger2 = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)this.key).getPublicExponent()) != null) {
            BigInteger bigInteger3 = rSAPrivateCrtKeyParameters.getModulus();
            BigInteger bigInteger4 = BigIntegers.createRandomInRange(ONE, bigInteger3.subtract(ONE), this.random);
            BigInteger bigInteger5 = bigInteger4.modPow(bigInteger2, bigInteger3);
            BigInteger bigInteger6 = BigIntegers.modOddInverse(bigInteger3, bigInteger4);
            BigInteger bigInteger7 = bigInteger5.multiply(bigInteger).mod(bigInteger3);
            BigInteger bigInteger8 = this.core.processBlock(bigInteger7);
            return bigInteger6.multiply(bigInteger8).mod(bigInteger3);
        }
        return this.core.processBlock(bigInteger);
    }
}

