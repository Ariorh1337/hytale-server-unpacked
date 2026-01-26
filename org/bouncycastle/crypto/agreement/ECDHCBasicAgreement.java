/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;

public class ECDHCBasicAgreement
implements BasicAgreement {
    ECPrivateKeyParameters key;

    @Override
    public void init(CipherParameters cipherParameters) {
        this.key = (ECPrivateKeyParameters)cipherParameters;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECCDH", this.key));
    }

    @Override
    public int getFieldSize() {
        return this.key.getParameters().getCurve().getFieldElementEncodingLength();
    }

    @Override
    public BigInteger calculateAgreement(CipherParameters cipherParameters) {
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)cipherParameters;
        ECDomainParameters eCDomainParameters = this.key.getParameters();
        if (!eCDomainParameters.equals(eCPublicKeyParameters.getParameters())) {
            throw new IllegalStateException("ECDHC public key has wrong domain parameters");
        }
        BigInteger bigInteger = eCDomainParameters.getH().multiply(this.key.getD()).mod(eCDomainParameters.getN());
        ECPoint eCPoint = ECAlgorithms.cleanPoint(eCDomainParameters.getCurve(), eCPublicKeyParameters.getQ());
        if (eCPoint.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDHC");
        }
        ECPoint eCPoint2 = eCPoint.multiply(bigInteger).normalize();
        if (eCPoint2.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDHC");
        }
        return eCPoint2.getAffineXCoord().toBigInteger();
    }
}

