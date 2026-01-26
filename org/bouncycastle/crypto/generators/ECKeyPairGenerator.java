/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.WNafUtil;
import org.bouncycastle.util.BigIntegers;

public class ECKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator,
ECConstants {
    private final String name;
    ECDomainParameters params;
    SecureRandom random;

    public ECKeyPairGenerator() {
        this("ECKeyGen");
    }

    protected ECKeyPairGenerator(String string) {
        this.name = string;
    }

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        ECKeyGenerationParameters eCKeyGenerationParameters = (ECKeyGenerationParameters)keyGenerationParameters;
        this.random = eCKeyGenerationParameters.getRandom();
        this.params = eCKeyGenerationParameters.getDomainParameters();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.name, ConstraintUtils.bitsOfSecurityFor(this.params.getCurve()), eCKeyGenerationParameters.getDomainParameters(), CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        BigInteger bigInteger;
        BigInteger bigInteger2 = this.params.getN();
        int n = bigInteger2.bitLength();
        int n2 = n >>> 2;
        while (this.isOutOfRangeD(bigInteger = BigIntegers.createRandomBigInteger(n, this.random), bigInteger2) || WNafUtil.getNafWeight(bigInteger) < n2) {
        }
        ECPoint eCPoint = this.createBasePointMultiplier().multiply(this.params.getG(), bigInteger);
        return new AsymmetricCipherKeyPair(new ECPublicKeyParameters(eCPoint, this.params), new ECPrivateKeyParameters(bigInteger, this.params));
    }

    protected boolean isOutOfRangeD(BigInteger bigInteger, BigInteger bigInteger2) {
        return bigInteger.compareTo(ONE) < 0 || bigInteger.compareTo(bigInteger2) >= 0;
    }

    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
}

