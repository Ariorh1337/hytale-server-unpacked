/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;

public interface X509ContentVerifierProviderBuilder {
    public ContentVerifierProvider build(SubjectPublicKeyInfo var1) throws OperatorCreationException;

    public ContentVerifierProvider build(X509CertificateHolder var1) throws OperatorCreationException;
}

