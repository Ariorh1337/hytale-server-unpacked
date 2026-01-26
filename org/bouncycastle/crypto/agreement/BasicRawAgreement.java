/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.util.BigIntegers;

public final class BasicRawAgreement
implements RawAgreement {
    public final BasicAgreement basicAgreement;

    public BasicRawAgreement(BasicAgreement basicAgreement) {
        if (basicAgreement == null) {
            throw new NullPointerException("'basicAgreement' cannot be null");
        }
        this.basicAgreement = basicAgreement;
    }

    @Override
    public void init(CipherParameters cipherParameters) {
        this.basicAgreement.init(cipherParameters);
    }

    @Override
    public int getAgreementSize() {
        return this.basicAgreement.getFieldSize();
    }

    @Override
    public void calculateAgreement(CipherParameters cipherParameters, byte[] byArray, int n) {
        BigInteger bigInteger = this.basicAgreement.calculateAgreement(cipherParameters);
        BigIntegers.asUnsignedByteArray(bigInteger, byArray, n, this.getAgreementSize());
    }
}

