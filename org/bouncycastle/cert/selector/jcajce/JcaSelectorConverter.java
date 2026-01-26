/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;

public class JcaSelectorConverter {
    public X509CertificateHolderSelector getCertificateHolderSelector(X509CertSelector x509CertSelector) {
        try {
            X500Name x500Name = X500Name.getInstance(x509CertSelector.getIssuerAsBytes());
            BigInteger bigInteger = x509CertSelector.getSerialNumber();
            byte[] byArray = null;
            byte[] byArray2 = x509CertSelector.getSubjectKeyIdentifier();
            if (byArray2 != null) {
                byArray = ASN1OctetString.getInstance(byArray2).getOctets();
            }
            return new X509CertificateHolderSelector(x500Name, bigInteger, byArray);
        }
        catch (IOException iOException) {
            throw new IllegalArgumentException("unable to convert issuer: " + iOException.getMessage());
        }
    }
}

