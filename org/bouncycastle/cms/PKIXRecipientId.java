/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.RecipientId;

public class PKIXRecipientId
extends RecipientId {
    protected final X509CertificateHolderSelector baseSelector;

    protected PKIXRecipientId(int n, X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(n);
        this.baseSelector = x509CertificateHolderSelector;
    }

    protected PKIXRecipientId(int n, X500Name x500Name, BigInteger bigInteger, byte[] byArray) {
        this(n, new X509CertificateHolderSelector(x500Name, bigInteger, byArray));
    }

    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }

    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    @Override
    public Object clone() {
        return new PKIXRecipientId(this.getType(), this.baseSelector);
    }

    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    public boolean equals(Object object) {
        if (!(object instanceof PKIXRecipientId)) {
            return false;
        }
        PKIXRecipientId pKIXRecipientId = (PKIXRecipientId)object;
        return this.baseSelector.equals(pKIXRecipientId.baseSelector);
    }

    public boolean match(Object object) {
        return this.baseSelector.match(object);
    }
}

