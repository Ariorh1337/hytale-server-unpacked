/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.PKIFreeText;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cmp.PKIHeaderBuilder;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.CertificateConfirmationContent;
import org.bouncycastle.cert.cmp.POPODecryptionKeyChallengeContent;
import org.bouncycastle.cert.cmp.POPODecryptionKeyResponseContent;
import org.bouncycastle.cert.cmp.ProtectedPKIMessage;
import org.bouncycastle.cert.crmf.CertificateRepMessage;
import org.bouncycastle.cert.crmf.CertificateReqMessages;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.MacCalculator;

public class ProtectedPKIMessageBuilder {
    private PKIHeaderBuilder hdrBuilder;
    private PKIBody body;
    private List generalInfos = new ArrayList();
    private List extraCerts = new ArrayList();

    public ProtectedPKIMessageBuilder(GeneralName generalName, GeneralName generalName2) {
        this(2, generalName, generalName2);
    }

    public ProtectedPKIMessageBuilder(int n, GeneralName generalName, GeneralName generalName2) {
        this.hdrBuilder = new PKIHeaderBuilder(n, generalName, generalName2);
    }

    public ProtectedPKIMessageBuilder setTransactionID(byte[] byArray) {
        this.hdrBuilder.setTransactionID(byArray);
        return this;
    }

    public ProtectedPKIMessageBuilder setFreeText(PKIFreeText pKIFreeText) {
        this.hdrBuilder.setFreeText(pKIFreeText);
        return this;
    }

    public ProtectedPKIMessageBuilder addGeneralInfo(InfoTypeAndValue infoTypeAndValue) {
        this.generalInfos.add(infoTypeAndValue);
        return this;
    }

    public ProtectedPKIMessageBuilder setMessageTime(Date date) {
        this.hdrBuilder.setMessageTime(new ASN1GeneralizedTime(date));
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipKID(byte[] byArray) {
        this.hdrBuilder.setRecipKID(byArray);
        return this;
    }

    public ProtectedPKIMessageBuilder setRecipNonce(byte[] byArray) {
        this.hdrBuilder.setRecipNonce(byArray);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderKID(byte[] byArray) {
        this.hdrBuilder.setSenderKID(byArray);
        return this;
    }

    public ProtectedPKIMessageBuilder setSenderNonce(byte[] byArray) {
        this.hdrBuilder.setSenderNonce(byArray);
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(PKIBody pKIBody) {
        this.body = pKIBody;
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int n, CertificateReqMessages certificateReqMessages) {
        if (!CertificateReqMessages.isCertificateRequestMessages(n)) {
            throw new IllegalArgumentException("body type " + n + " does not match CMP type CertReqMessages");
        }
        this.body = new PKIBody(n, certificateReqMessages.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int n, CertificateRepMessage certificateRepMessage) {
        if (!CertificateRepMessage.isCertificateRepMessage(n)) {
            throw new IllegalArgumentException("body type " + n + " does not match CMP type CertRepMessage");
        }
        this.body = new PKIBody(n, certificateRepMessage.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(int n, CertificateConfirmationContent certificateConfirmationContent) {
        if (!CertificateConfirmationContent.isCertificateConfirmationContent(n)) {
            throw new IllegalArgumentException("body type " + n + " does not match CMP type CertConfirmContent");
        }
        this.body = new PKIBody(n, certificateConfirmationContent.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(POPODecryptionKeyChallengeContent pOPODecryptionKeyChallengeContent) {
        this.body = new PKIBody(5, pOPODecryptionKeyChallengeContent.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder setBody(POPODecryptionKeyResponseContent pOPODecryptionKeyResponseContent) {
        this.body = new PKIBody(6, pOPODecryptionKeyResponseContent.toASN1Structure());
        return this;
    }

    public ProtectedPKIMessageBuilder addCMPCertificate(X509CertificateHolder x509CertificateHolder) {
        this.extraCerts.add(x509CertificateHolder);
        return this;
    }

    public ProtectedPKIMessage build(MacCalculator macCalculator) throws CMPException {
        if (null == this.body) {
            throw new IllegalStateException("body must be set before building");
        }
        this.finaliseHeader(macCalculator.getAlgorithmIdentifier());
        PKIHeader pKIHeader = this.hdrBuilder.build();
        try {
            DERBitString dERBitString = new DERBitString(this.calculateMac(macCalculator, pKIHeader, this.body));
            return this.finaliseMessage(pKIHeader, dERBitString);
        }
        catch (IOException iOException) {
            throw new CMPException("unable to encode MAC input: " + iOException.getMessage(), iOException);
        }
    }

    public ProtectedPKIMessage build(ContentSigner contentSigner) throws CMPException {
        if (null == this.body) {
            throw new IllegalStateException("body must be set before building");
        }
        this.finaliseHeader(contentSigner.getAlgorithmIdentifier());
        PKIHeader pKIHeader = this.hdrBuilder.build();
        try {
            DERBitString dERBitString = new DERBitString(this.calculateSignature(contentSigner, pKIHeader, this.body));
            return this.finaliseMessage(pKIHeader, dERBitString);
        }
        catch (IOException iOException) {
            throw new CMPException("unable to encode signature input: " + iOException.getMessage(), iOException);
        }
    }

    private void finaliseHeader(AlgorithmIdentifier algorithmIdentifier) {
        this.hdrBuilder.setProtectionAlg(algorithmIdentifier);
        if (!this.generalInfos.isEmpty()) {
            InfoTypeAndValue[] infoTypeAndValueArray = new InfoTypeAndValue[this.generalInfos.size()];
            this.hdrBuilder.setGeneralInfo(this.generalInfos.toArray(infoTypeAndValueArray));
        }
    }

    private ProtectedPKIMessage finaliseMessage(PKIHeader pKIHeader, DERBitString dERBitString) {
        if (!this.extraCerts.isEmpty()) {
            CMPCertificate[] cMPCertificateArray = new CMPCertificate[this.extraCerts.size()];
            for (int i = 0; i != cMPCertificateArray.length; ++i) {
                cMPCertificateArray[i] = new CMPCertificate(((X509CertificateHolder)this.extraCerts.get(i)).toASN1Structure());
            }
            return new ProtectedPKIMessage(new PKIMessage(pKIHeader, this.body, dERBitString, cMPCertificateArray));
        }
        return new ProtectedPKIMessage(new PKIMessage(pKIHeader, this.body, dERBitString));
    }

    private byte[] calculateSignature(ContentSigner contentSigner, PKIHeader pKIHeader, PKIBody pKIBody) throws IOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(pKIHeader);
        aSN1EncodableVector.add(pKIBody);
        OutputStream outputStream = contentSigner.getOutputStream();
        outputStream.write(new DERSequence(aSN1EncodableVector).getEncoded("DER"));
        outputStream.close();
        return contentSigner.getSignature();
    }

    private byte[] calculateMac(MacCalculator macCalculator, PKIHeader pKIHeader, PKIBody pKIBody) throws IOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(pKIHeader);
        aSN1EncodableVector.add(pKIBody);
        OutputStream outputStream = macCalculator.getOutputStream();
        outputStream.write(new DERSequence(aSN1EncodableVector).getEncoded("DER"));
        outputStream.close();
        return macCalculator.getMac();
    }
}

