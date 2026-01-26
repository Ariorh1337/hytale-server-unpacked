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
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECCSIKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECCSIPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECCSIPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class ECCSIKeyPairGenerator
implements AsymmetricCipherKeyPairGenerator {
    private BigInteger q;
    private ECPoint G;
    private Digest digest;
    private ECCSIKeyGenerationParameters parameters;

    @Override
    public void init(KeyGenerationParameters keyGenerationParameters) {
        this.parameters = (ECCSIKeyGenerationParameters)keyGenerationParameters;
        this.q = this.parameters.getQ();
        this.G = this.parameters.getG();
        this.digest = this.parameters.getDigest();
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECCSI", this.parameters.getN(), null, CryptoServicePurpose.KEYGEN));
    }

    @Override
    public AsymmetricCipherKeyPair generateKeyPair() {
        SecureRandom secureRandom = this.parameters.getRandom();
        this.digest.reset();
        byte[] byArray = this.parameters.getId();
        ECPoint eCPoint = this.parameters.getKPAK();
        BigInteger bigInteger = BigIntegers.createRandomBigInteger(256, secureRandom).mod(this.q);
        ECPoint eCPoint2 = this.G.multiply(bigInteger).normalize();
        byte[] byArray2 = this.G.getEncoded(false);
        this.digest.update(byArray2, 0, byArray2.length);
        byArray2 = eCPoint.getEncoded(false);
        this.digest.update(byArray2, 0, byArray2.length);
        this.digest.update(byArray, 0, byArray.length);
        byArray2 = eCPoint2.getEncoded(false);
        this.digest.update(byArray2, 0, byArray2.length);
        byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(byArray2, 0);
        BigInteger bigInteger2 = new BigInteger(1, byArray2).mod(this.q);
        BigInteger bigInteger3 = this.parameters.computeSSK(bigInteger2.multiply(bigInteger));
        ECCSIPublicKeyParameters eCCSIPublicKeyParameters = new ECCSIPublicKeyParameters(eCPoint2);
        return new AsymmetricCipherKeyPair(new ECCSIPublicKeyParameters(eCPoint2), new ECCSIPrivateKeyParameters(bigInteger3, eCCSIPublicKeyParameters));
    }
}

