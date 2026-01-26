/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.cms.KeyAgreeRecipientInformation;
import org.bouncycastle.cms.PKIXRecipientId;

public class KeyAgreeRecipientId
extends PKIXRecipientId {
    private KeyAgreeRecipientId(X509CertificateHolderSelector x509CertificateHolderSelector) {
        super(2, x509CertificateHolderSelector);
    }

    public KeyAgreeRecipientId(byte[] byArray) {
        super(2, null, null, byArray);
    }

    public KeyAgreeRecipientId(X500Name x500Name, BigInteger bigInteger) {
        super(2, x500Name, bigInteger, null);
    }

    public KeyAgreeRecipientId(X500Name x500Name, BigInteger bigInteger, byte[] byArray) {
        super(2, x500Name, bigInteger, byArray);
    }

    @Override
    public X500Name getIssuer() {
        return this.baseSelector.getIssuer();
    }

    @Override
    public BigInteger getSerialNumber() {
        return this.baseSelector.getSerialNumber();
    }

    @Override
    public byte[] getSubjectKeyIdentifier() {
        return this.baseSelector.getSubjectKeyIdentifier();
    }

    @Override
    public int hashCode() {
        return this.baseSelector.hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof KeyAgreeRecipientId)) {
            return false;
        }
        KeyAgreeRecipientId keyAgreeRecipientId = (KeyAgreeRecipientId)object;
        return this.baseSelector.equals(keyAgreeRecipientId.baseSelector);
    }

    @Override
    public Object clone() {
        return new KeyAgreeRecipientId(this.baseSelector);
    }

    @Override
    public boolean match(Object object) {
        if (object instanceof KeyAgreeRecipientInformation) {
            return ((KeyAgreeRecipientInformation)object).getRID().equals(this);
        }
        return this.baseSelector.match(object);
    }
}

