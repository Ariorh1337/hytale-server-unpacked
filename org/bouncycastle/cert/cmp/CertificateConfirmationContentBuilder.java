/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertConfirmContent;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPUtil;
import org.bouncycastle.cert.cmp.CertificateConfirmationContent;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;

public class CertificateConfirmationContentBuilder {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private List<CMPCertificate> acceptedCerts = new ArrayList<CMPCertificate>();
    private List<AlgorithmIdentifier> acceptedSignatureAlgorithms = new ArrayList<AlgorithmIdentifier>();
    private List<ASN1Integer> acceptedReqIds = new ArrayList<ASN1Integer>();

    public CertificateConfirmationContentBuilder() {
        this(new DefaultDigestAlgorithmIdentifierFinder());
    }

    public CertificateConfirmationContentBuilder(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        this.digestAlgFinder = digestAlgorithmIdentifierFinder;
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder x509CertificateHolder, BigInteger bigInteger) {
        return this.addAcceptedCertificate(x509CertificateHolder, new ASN1Integer(bigInteger));
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(X509CertificateHolder x509CertificateHolder, ASN1Integer aSN1Integer) {
        return this.addAcceptedCertificate(new CMPCertificate(x509CertificateHolder.toASN1Structure()), x509CertificateHolder.getSignatureAlgorithm(), aSN1Integer);
    }

    public CertificateConfirmationContentBuilder addAcceptedCertificate(CMPCertificate cMPCertificate, AlgorithmIdentifier algorithmIdentifier, ASN1Integer aSN1Integer) {
        this.acceptedCerts.add(cMPCertificate);
        this.acceptedSignatureAlgorithms.add(algorithmIdentifier);
        this.acceptedReqIds.add(aSN1Integer);
        return this;
    }

    public CertificateConfirmationContent build(DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(this.acceptedCerts.size());
        for (int i = 0; i != this.acceptedCerts.size(); ++i) {
            byte[] byArray = CMPUtil.calculateCertHash(this.acceptedCerts.get(i), this.acceptedSignatureAlgorithms.get(i), digestCalculatorProvider, this.digestAlgFinder);
            ASN1Integer aSN1Integer = this.acceptedReqIds.get(i);
            aSN1EncodableVector.add(new CertStatus(byArray, aSN1Integer));
        }
        CertConfirmContent certConfirmContent = CertConfirmContent.getInstance(new DERSequence(aSN1EncodableVector));
        return new CertificateConfirmationContent(certConfirmContent, this.digestAlgFinder);
    }
}

