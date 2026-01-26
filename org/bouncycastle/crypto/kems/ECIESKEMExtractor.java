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
import org.bouncycastle.crypto.kems.ECIESKEMGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class ECIESKEMExtractor
implements EncapsulatedSecretExtractor {
    private final ECPrivateKeyParameters decKey;
    private int keyLen;
    private DerivationFunction kdf;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKEMExtractor(ECPrivateKeyParameters eCPrivateKeyParameters, int n, DerivationFunction derivationFunction) {
        this.decKey = eCPrivateKeyParameters;
        this.keyLen = n;
        this.kdf = derivationFunction;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKEMExtractor(ECPrivateKeyParameters eCPrivateKeyParameters, int n, DerivationFunction derivationFunction, boolean bl, boolean bl2, boolean bl3) {
        this.decKey = eCPrivateKeyParameters;
        this.keyLen = n;
        this.kdf = derivationFunction;
        this.CofactorMode = bl;
        this.OldCofactorMode = bl ? false : bl2;
        this.SingleHashMode = bl3;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.decKey.getParameters().getCurve()), eCPrivateKeyParameters, CryptoServicePurpose.DECRYPTION));
    }

    @Override
    public byte[] extractSecret(byte[] byArray) {
        ECPrivateKeyParameters eCPrivateKeyParameters = this.decKey;
        ECDomainParameters eCDomainParameters = eCPrivateKeyParameters.getParameters();
        ECCurve eCCurve = eCDomainParameters.getCurve();
        BigInteger bigInteger = eCDomainParameters.getN();
        BigInteger bigInteger2 = eCDomainParameters.getH();
        ECPoint eCPoint = eCCurve.decodePoint(byArray);
        if (this.CofactorMode || this.OldCofactorMode) {
            eCPoint = eCPoint.multiply(bigInteger2);
        }
        BigInteger bigInteger3 = eCPrivateKeyParameters.getD();
        if (this.CofactorMode) {
            bigInteger3 = bigInteger3.multiply(eCDomainParameters.getHInv()).mod(bigInteger);
        }
        ECPoint eCPoint2 = eCPoint.multiply(bigInteger3).normalize();
        byte[] byArray2 = eCPoint2.getAffineXCoord().getEncoded();
        return ECIESKEMGenerator.deriveKey(this.SingleHashMode, this.kdf, this.keyLen, byArray, byArray2);
    }

    @Override
    public int getEncapsulationLength() {
        return this.decKey.getParameters().getCurve().getAffinePointEncodingLength(false);
    }
}

