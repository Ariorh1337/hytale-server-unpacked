/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf.jcajce;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.crmf.CertificateRepMessageBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class JcaCertificateRepMessageBuilder
extends CertificateRepMessageBuilder {
    public JcaCertificateRepMessageBuilder(X509Certificate ... x509CertificateArray) throws CertificateEncodingException {
        super(JcaCertificateRepMessageBuilder.convert(x509CertificateArray));
    }

    private static X509CertificateHolder[] convert(X509Certificate ... x509CertificateArray) throws CertificateEncodingException {
        X509CertificateHolder[] x509CertificateHolderArray = new X509CertificateHolder[x509CertificateArray.length];
        for (int i = 0; i != x509CertificateHolderArray.length; ++i) {
            x509CertificateHolderArray[i] = new JcaX509CertificateHolder(x509CertificateArray[i]);
        }
        return x509CertificateHolderArray;
    }
}

