/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.its.ITSCertificate;

public interface ITSContentSigner {
    public OutputStream getOutputStream();

    public byte[] getSignature();

    public ITSCertificate getAssociatedCertificate();

    public byte[] getAssociatedCertificateDigest();

    public AlgorithmIdentifier getDigestAlgorithm();

    public ASN1ObjectIdentifier getCurveID();

    public boolean isForSelfSigning();
}

