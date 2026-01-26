/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.agreement;

import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.RawAgreement;
import org.bouncycastle.crypto.agreement.X25519Agreement;
import org.bouncycastle.crypto.agreement.X448Agreement;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;

public class XDHBasicAgreement
implements BasicAgreement {
    private AsymmetricKeyParameter key;
    private RawAgreement agreement;
    private int fieldSize = 0;

    @Override
    public void init(CipherParameters cipherParameters) {
        if (cipherParameters instanceof X25519PrivateKeyParameters) {
            this.fieldSize = 32;
            this.agreement = new X25519Agreement();
        } else if (cipherParameters instanceof X448PrivateKeyParameters) {
            this.fieldSize = 56;
            this.agreement = new X448Agreement();
        } else {
            throw new IllegalArgumentException("key is neither X25519 nor X448");
        }
        this.key = (AsymmetricKeyParameter)cipherParameters;
        this.agreement.init(cipherParameters);
    }

    @Override
    public int getFieldSize() {
        return this.fieldSize;
    }

    @Override
    public BigInteger calculateAgreement(CipherParameters cipherParameters) {
        byte[] byArray = new byte[this.fieldSize];
        this.agreement.calculateAgreement(cipherParameters, byArray, 0);
        return new BigInteger(1, byArray);
    }
}

