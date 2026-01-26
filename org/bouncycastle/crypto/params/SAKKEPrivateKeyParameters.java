/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.SAKKEPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;

public class SAKKEPrivateKeyParameters
extends AsymmetricKeyParameter {
    private static final BigInteger qMinOne = SAKKEPublicKeyParameters.q.subtract(BigInteger.ONE);
    private final SAKKEPublicKeyParameters publicParams;
    private final BigInteger z;

    public SAKKEPrivateKeyParameters(BigInteger bigInteger, SAKKEPublicKeyParameters sAKKEPublicKeyParameters) {
        super(true);
        this.z = bigInteger;
        this.publicParams = sAKKEPublicKeyParameters;
        ECPoint eCPoint = sAKKEPublicKeyParameters.getPoint().multiply(bigInteger).normalize();
        if (!eCPoint.equals(sAKKEPublicKeyParameters.getZ())) {
            throw new IllegalStateException("public key and private key of SAKKE do not match");
        }
    }

    public SAKKEPrivateKeyParameters(SecureRandom secureRandom) {
        super(true);
        this.z = BigIntegers.createRandomInRange(BigIntegers.TWO, qMinOne, secureRandom);
        BigInteger bigInteger = BigIntegers.createRandomInRange(BigIntegers.TWO, qMinOne, secureRandom);
        this.publicParams = new SAKKEPublicKeyParameters(bigInteger, SAKKEPublicKeyParameters.P.multiply(this.z).normalize());
    }

    public SAKKEPublicKeyParameters getPublicParams() {
        return this.publicParams;
    }

    public BigInteger getMasterSecret() {
        return this.z;
    }
}

