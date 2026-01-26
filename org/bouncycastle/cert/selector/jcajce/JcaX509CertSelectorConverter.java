/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.selector.jcajce;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.X509CertSelector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.selector.X509CertificateHolderSelector;
import org.bouncycastle.util.Exceptions;

public class JcaX509CertSelectorConverter {
    protected X509CertSelector doConversion(X500Name x500Name, BigInteger bigInteger, byte[] byArray) {
        X509CertSelector x509CertSelector = new X509CertSelector();
        if (x500Name != null) {
            try {
                x509CertSelector.setIssuer(x500Name.getEncoded());
            }
            catch (IOException iOException) {
                throw Exceptions.illegalArgumentException("unable to convert issuer: " + iOException.getMessage(), iOException);
            }
        }
        if (bigInteger != null) {
            x509CertSelector.setSerialNumber(bigInteger);
        }
        if (byArray != null) {
            try {
                x509CertSelector.setSubjectKeyIdentifier(new DEROctetString(byArray).getEncoded());
            }
            catch (IOException iOException) {
                throw Exceptions.illegalArgumentException("unable to convert subjectKeyIdentifier: " + iOException.getMessage(), iOException);
            }
        }
        return x509CertSelector;
    }

    public X509CertSelector getCertSelector(X509CertificateHolderSelector x509CertificateHolderSelector) {
        return this.doConversion(x509CertificateHolderSelector.getIssuer(), x509CertificateHolderSelector.getSerialNumber(), x509CertificateHolderSelector.getSubjectKeyIdentifier());
    }
}

