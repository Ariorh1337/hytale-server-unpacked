/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.PKIXRecipientId;

public class KeyTransRecipientId
extends PKIXRecipientId {
    private KeyTransRecipientId(X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(0, x509CertificateHolderSelector);
    }

    public KeyTransRecipientId(byte[] byArray) {
        super(0, null, null, byArray);
    }

    public KeyTransRecipientId(X500Name x500Name, BigInteger bigInteger) {
        super(0, x500Name, bigInteger, null);
    }

    public KeyTransRecipientId(X500Name x500Name, BigInteger bigInteger, byte[] byArray) {
        super(0, x500Name, bigInteger, byArray);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof KeyTransRecipientId)) {
            return false;
        }
        KeyTransRecipientId keyTransRecipientId = (KeyTransRecipientId)object;
        return this.baseSelector.equals(keyTransRecipientId.baseSelector);
    }

    @Override
    public Object clone() {
        return new KeyTransRecipientId(this.baseSelector);
    }

    @Override
    public boolean match(Object object) {
        if (object instanceof KeyTransRecipientInformation) {
            return ((KeyTransRecipientInformation)object).getRID().equals(this);
        }
        return super.match(object);
    }
}

