/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import org.bouncycastle.cert.jcajce.CertHelper;

class DefaultCertHelper
extends CertHelper {
    DefaultCertHelper() {
    }

    @Override
    protected CertificateFactory createCertificateFactory(String string) throws CertificateException {
        return CertificateFactory.getInstance(string);
    }
}

