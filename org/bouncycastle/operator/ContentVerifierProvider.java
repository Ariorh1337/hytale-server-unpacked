/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;

public interface ContentVerifierProvider {
    public boolean hasAssociatedCertificate();

    public X509CertificateHolder getAssociatedCertificate();

    public ContentVerifier get(AlgorithmIdentifier var1) throws OperatorCreationException;
}

