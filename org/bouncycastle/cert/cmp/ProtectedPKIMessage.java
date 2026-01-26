/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CMPUtil;
import org.bouncycastle.cert.cmp.GeneralPKIMessage;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.util.Arrays;

public class ProtectedPKIMessage {
    private PKIMessage pkiMessage;

    public ProtectedPKIMessage(GeneralPKIMessage generalPKIMessage) {
        if (!generalPKIMessage.hasProtection()) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = generalPKIMessage.toASN1Structure();
    }

    ProtectedPKIMessage(PKIMessage pKIMessage) {
        if (pKIMessage.getHeader().getProtectionAlg() == null) {
            throw new IllegalArgumentException("PKIMessage not protected");
        }
        this.pkiMessage = pKIMessage;
    }

    public PKIHeader getHeader() {
        return this.pkiMessage.getHeader();
    }

    public PKIBody getBody() {
        return this.pkiMessage.getBody();
    }

    public PKIMessage toASN1Structure() {
        return this.pkiMessage;
    }

    public boolean hasPasswordBasedMacProtection() {
        return CMPObjectIdentifiers.passwordBasedMac.equals(this.getProtectionAlgorithm().getAlgorithm());
    }

    public AlgorithmIdentifier getProtectionAlgorithm() {
        return this.pkiMessage.getHeader().getProtectionAlg();
    }

    public X509CertificateHolder[] getCertificates() {
        CMPCertificate[] cMPCertificateArray = this.pkiMessage.getExtraCerts();
        if (cMPCertificateArray == null) {
            return new X509CertificateHolder[0];
        }
        X509CertificateHolder[] x509CertificateHolderArray = new X509CertificateHolder[cMPCertificateArray.length];
        for (int i = 0; i != cMPCertificateArray.length; ++i) {
            x509CertificateHolderArray[i] = new X509CertificateHolder(cMPCertificateArray[i].getX509v3PKCert());
        }
        return x509CertificateHolderArray;
    }

    public boolean verify(ContentVerifierProvider contentVerifierProvider) throws CMPException {
        try {
            ContentVerifier contentVerifier = contentVerifierProvider.get(this.getProtectionAlgorithm());
            return this.verifySignature(this.pkiMessage.getProtection().getOctets(), contentVerifier);
        }
        catch (Exception exception) {
            throw new CMPException("unable to verify signature: " + exception.getMessage(), exception);
        }
    }

    public boolean verify(PBEMacCalculatorProvider pBEMacCalculatorProvider, char[] cArray) throws CMPException {
        try {
            MacCalculator macCalculator = pBEMacCalculatorProvider.get(this.getProtectionAlgorithm(), cArray);
            CMPUtil.derEncodeToStream(this.createProtected(), macCalculator.getOutputStream());
            return Arrays.constantTimeAreEqual(macCalculator.getMac(), this.pkiMessage.getProtection().getOctets());
        }
        catch (Exception exception) {
            throw new CMPException("unable to verify MAC: " + exception.getMessage(), exception);
        }
    }

    private boolean verifySignature(byte[] byArray, ContentVerifier contentVerifier) {
        CMPUtil.derEncodeToStream(this.createProtected(), contentVerifier.getOutputStream());
        return contentVerifier.verify(byArray);
    }

    private DERSequence createProtected() {
        return new DERSequence(this.pkiMessage.getHeader(), this.pkiMessage.getBody());
    }
}

