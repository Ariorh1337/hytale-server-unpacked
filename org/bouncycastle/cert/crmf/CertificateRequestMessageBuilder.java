/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.crmf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.OptionalValidity;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.CRMFUtil;
import org.bouncycastle.cert.crmf.CertificateRequestMessage;
import org.bouncycastle.cert.crmf.Control;
import org.bouncycastle.cert.crmf.PKMACBuilder;
import org.bouncycastle.cert.crmf.ProofOfPossessionSigningKeyBuilder;
import org.bouncycastle.operator.ContentSigner;

public class CertificateRequestMessageBuilder {
    private final BigInteger certReqId;
    private ExtensionsGenerator extGenerator;
    private CertTemplateBuilder templateBuilder;
    private List controls;
    private ContentSigner popSigner;
    private PKMACBuilder pkmacBuilder;
    private char[] password;
    private GeneralName sender;
    private int popoType = 2;
    private POPOPrivKey popoPrivKey;
    private ASN1Null popRaVerified;
    private PKMACValue agreeMAC;
    private AttributeTypeAndValue[] regInfo;

    public CertificateRequestMessageBuilder(BigInteger bigInteger) {
        this.certReqId = bigInteger;
        this.extGenerator = new ExtensionsGenerator();
        this.templateBuilder = new CertTemplateBuilder();
        this.controls = new ArrayList();
        this.regInfo = null;
    }

    public CertificateRequestMessageBuilder setRegInfo(AttributeTypeAndValue[] attributeTypeAndValueArray) {
        this.regInfo = attributeTypeAndValueArray;
        return this;
    }

    public CertificateRequestMessageBuilder setPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        if (subjectPublicKeyInfo != null) {
            this.templateBuilder.setPublicKey(subjectPublicKeyInfo);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setIssuer(X500Name x500Name) {
        if (x500Name != null) {
            this.templateBuilder.setIssuer(x500Name);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSubject(X500Name x500Name) {
        if (x500Name != null) {
            this.templateBuilder.setSubject(x500Name);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(BigInteger bigInteger) {
        if (bigInteger != null) {
            this.templateBuilder.setSerialNumber(new ASN1Integer(bigInteger));
        }
        return this;
    }

    public CertificateRequestMessageBuilder setSerialNumber(ASN1Integer aSN1Integer) {
        if (aSN1Integer != null) {
            this.templateBuilder.setSerialNumber(aSN1Integer);
        }
        return this;
    }

    public CertificateRequestMessageBuilder setValidity(Date date, Date date2) {
        this.templateBuilder.setValidity(new OptionalValidity(this.createTime(date), this.createTime(date2)));
        return this;
    }

    private Time createTime(Date date) {
        if (date != null) {
            return new Time(date);
        }
        return null;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, ASN1Encodable aSN1Encodable) throws CertIOException {
        CRMFUtil.addExtension(this.extGenerator, aSN1ObjectIdentifier, bl, aSN1Encodable);
        return this;
    }

    public CertificateRequestMessageBuilder addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean bl, byte[] byArray) {
        this.extGenerator.addExtension(aSN1ObjectIdentifier, bl, byArray);
        return this;
    }

    public CertificateRequestMessageBuilder addControl(Control control) {
        this.controls.add(control);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSigningKeySigner(ContentSigner contentSigner) {
        if (this.popoPrivKey != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popSigner = contentSigner;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(SubsequentMessage subsequentMessage) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popoType = 2;
        this.popoPrivKey = new POPOPrivKey(subsequentMessage);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionSubsequentMessage(int n, SubsequentMessage subsequentMessage) {
        if (this.popSigner != null || this.popRaVerified != null || this.agreeMAC != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        if (n != 2 && n != 3) {
            throw new IllegalArgumentException("type must be ProofOfPossession.TYPE_KEY_ENCIPHERMENT or ProofOfPossession.TYPE_KEY_AGREEMENT");
        }
        this.popoType = n;
        this.popoPrivKey = new POPOPrivKey(subsequentMessage);
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionAgreeMAC(PKMACValue pKMACValue) {
        if (this.popSigner != null || this.popRaVerified != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.agreeMAC = pKMACValue;
        return this;
    }

    public CertificateRequestMessageBuilder setProofOfPossessionRaVerified() {
        if (this.popSigner != null || this.popoPrivKey != null) {
            throw new IllegalStateException("only one proof of possession allowed");
        }
        this.popRaVerified = DERNull.INSTANCE;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoPKMAC(PKMACBuilder pKMACBuilder, char[] cArray) {
        this.pkmacBuilder = pKMACBuilder;
        this.password = cArray;
        return this;
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(X500Name x500Name) {
        return this.setAuthInfoSender(new GeneralName(x500Name));
    }

    public CertificateRequestMessageBuilder setAuthInfoSender(GeneralName generalName) {
        this.sender = generalName;
        return this;
    }

    public CertificateRequestMessage build() throws CRMFException {
        Object object;
        Object object22;
        Object object3;
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(new ASN1Integer(this.certReqId));
        if (!this.extGenerator.isEmpty()) {
            this.templateBuilder.setExtensions(this.extGenerator.generate());
        }
        aSN1EncodableVector.add(this.templateBuilder.build());
        if (!this.controls.isEmpty()) {
            object3 = new ASN1EncodableVector();
            for (Object object22 : this.controls) {
                ((ASN1EncodableVector)object3).add(new AttributeTypeAndValue(object22.getType(), object22.getValue()));
            }
            aSN1EncodableVector.add(new DERSequence((ASN1EncodableVector)object3));
        }
        object3 = CertRequest.getInstance(new DERSequence(aSN1EncodableVector));
        if (this.popSigner != null) {
            ProofOfPossessionSigningKeyBuilder proofOfPossessionSigningKeyBuilder;
            object22 = ((CertRequest)object3).getCertTemplate();
            if (((CertTemplate)object22).getSubject() == null || ((CertTemplate)object22).getPublicKey() == null) {
                SubjectPublicKeyInfo subjectPublicKeyInfo = ((CertRequest)object3).getCertTemplate().getPublicKey();
                proofOfPossessionSigningKeyBuilder = new ProofOfPossessionSigningKeyBuilder(subjectPublicKeyInfo);
                if (this.sender != null) {
                    proofOfPossessionSigningKeyBuilder.setSender(this.sender);
                } else {
                    proofOfPossessionSigningKeyBuilder.setPublicKeyMac(this.pkmacBuilder, this.password);
                }
            } else {
                proofOfPossessionSigningKeyBuilder = new ProofOfPossessionSigningKeyBuilder((CertRequest)object3);
            }
            object = new ProofOfPossession(proofOfPossessionSigningKeyBuilder.build(this.popSigner));
        } else {
            object = this.popoPrivKey != null ? new ProofOfPossession(this.popoType, this.popoPrivKey) : (this.agreeMAC != null ? new ProofOfPossession(3, new POPOPrivKey(this.agreeMAC)) : (this.popRaVerified != null ? new ProofOfPossession() : new ProofOfPossession()));
        }
        object22 = new CertReqMsg((CertRequest)object3, (ProofOfPossession)object, this.regInfo);
        return new CertificateRequestMessage((CertReqMsg)object22);
    }
}

