/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.RSAKEMGenerator;
import org.bouncycastle.crypto.params.RSAKeyParameters;

public class RSAKEMExtractor
implements EncapsulatedSecretExtractor {
    private final RSAKeyParameters privKey;
    private final int keyLen;
    private DerivationFunction kdf;

    public RSAKEMExtractor(RSAKeyParameters rSAKeyParameters, int n, DerivationFunction derivationFunction) {
        if (!rSAKeyParameters.isPrivate()) {
            throw new IllegalArgumentException("private key required for encryption");
        }
        this.privKey = rSAKeyParameters;
        this.keyLen = n;
        this.kdf = derivationFunction;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(this.privKey.getModulus()), rSAKeyParameters, CryptoServicePurpose.DECRYPTION));
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        BigInteger bigInteger = this.privKey.getModulus();
        BigInteger bigInteger2 = this.privKey.getExponent();
        BigInteger bigInteger3 = new BigInteger(1, byArray);
        BigInteger bigInteger4 = bigInteger3.modPow(bigInteger2, bigInteger);
        return RSAKEMGenerator.generateKey(this.kdf, bigInteger, bigInteger4, this.keyLen);
    }

    @Override
    public int getEncapsulationLength() {
        return (this.privKey.getModulus().bitLength() + 7) / 8;
    }
}

