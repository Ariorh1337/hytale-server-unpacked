/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.KEMRecipientInformation;
import org.bouncycastle.cms.PKIXRecipientId;

public class KEMRecipientId
extends PKIXRecipientId {
    private KEMRecipientId(X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(4, x509CertificateHolderSelector);
    }

    public KEMRecipientId(byte[] byArray) {
        super(4, null, null, byArray);
    }

    public KEMRecipientId(X500Name x500Name, BigInteger bigInteger) {
        super(4, x500Name, bigInteger, null);
    }

    public KEMRecipientId(X500Name x500Name, BigInteger bigInteger, byte[] byArray) {
        super(4, x500Name, bigInteger, byArray);
    }

    @Override
    public Object clone() {
        return new KEMRecipientId(this.baseSelector);
    }

    @Override
    public boolean match(Object object) {
        if (object instanceof KEMRecipientInformation) {
            return ((KEMRecipientInformation)object).getRID().equals(this);
        }
        return super.match(object);
    }
}

