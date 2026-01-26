/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.math.BigInteger;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CertStatus;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPUtil;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.util.Arrays;

public class CertificateStatus {
    private DigestAlgorithmIdentifierFinder digestAlgFinder;
    private CertStatus certStatus;

    CertificateStatus(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder, CertStatus certStatus) {
        this.digestAlgFinder = digestAlgorithmIdentifierFinder;
        this.certStatus = certStatus;
    }

    public PKIStatusInfo getStatusInfo() {
        return this.certStatus.getStatusInfo();
    }

    public BigInteger getCertRequestID() {
        return this.certStatus.getCertReqId().getValue();
    }

    public boolean isVerified(X509CertificateHolder x509CertificateHolder, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        return this.isVerified(new CMPCertificate(x509CertificateHolder.toASN1Structure()), x509CertificateHolder.getSignatureAlgorithm(), digestCalculatorProvider);
    }

    public boolean isVerified(CMPCertificate cMPCertificate, AlgorithmIdentifier algorithmIdentifier, DigestCalculatorProvider digestCalculatorProvider) throws CMPException {
        byte[] byArray = CMPUtil.calculateCertHash(cMPCertificate, algorithmIdentifier, digestCalculatorProvider, this.digestAlgFinder);
        return Arrays.constantTimeAreEqual(this.certStatus.getCertHash().getOctets(), byArray);
    }
}

