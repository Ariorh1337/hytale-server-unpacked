/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.agreement.Utils;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;

public class ECVKOAgreement {
    private final Digest digest;
    private ECPrivateKeyParameters key;
    private BigInteger ukm;

    public ECVKOAgreement(Digest digest) {
        this.digest = digest;
    }

    public void init(CipherParameters cipherParameters) {
        ParametersWithUKM parametersWithUKM = (ParametersWithUKM)cipherParameters;
        this.key = (ECPrivateKeyParameters)parametersWithUKM.getParameters();
        this.ukm = new BigInteger(1, Arrays.reverse(parametersWithUKM.getUKM()));
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties("ECVKO", this.key));
    }

    public int getAgreementSize() {
        return this.digest.getDigestSize();
    }

    public int getFieldSize() {
        return this.key.getParameters().getCurve().getFieldElementEncodingLength();
    }

    public byte[] calculateAgreement(CipherParameters cipherParameters) {
        ECPublicKeyParameters eCPublicKeyParameters = (ECPublicKeyParameters)cipherParameters;
        ECDomainParameters eCDomainParameters = this.key.getParameters();
        if (!eCDomainParameters.equals(eCPublicKeyParameters.getParameters())) {
            throw new IllegalStateException("ECVKO public key has wrong domain parameters");
        }
        BigInteger bigInteger = eCDomainParameters.getH().multiply(this.ukm).multiply(this.key.getD()).mod(eCDomainParameters.getN());
        ECPoint eCPoint = ECAlgorithms.cleanPoint(eCDomainParameters.getCurve(), eCPublicKeyParameters.getQ());
        if (eCPoint.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECVKO");
        }
        ECPoint eCPoint2 = eCPoint.multiply(bigInteger).normalize();
        if (eCPoint2.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO");
        }
        byte[] byArray = eCPoint2.getEncoded(false);
        int n = byArray.length;
        int n2 = n / 2;
        Arrays.reverseInPlace(byArray, n - n2 * 2, n2);
        Arrays.reverseInPlace(byArray, n - n2, n2);
        byte[] byArray2 = new byte[this.digest.getDigestSize()];
        this.digest.update(byArray, n - n2 * 2, n2 * 2);
        this.digest.doFinal(byArray2, 0);
        return byArray2;
    }
}

