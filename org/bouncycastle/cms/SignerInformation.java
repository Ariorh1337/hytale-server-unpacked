/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.SignerIdentifier;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedHelper;
import org.bouncycastle.cms.CMSSignerDigestMismatchException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.CMSVerifierCertificateNotValidException;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RawContentVerifier;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.TeeOutputStream;

public class SignerInformation {
    private final SignerId sid;
    private final CMSProcessable content;
    private final byte[] signature;
    private final ASN1ObjectIdentifier contentType;
    private final boolean isCounterSignature;
    private AttributeTable signedAttributeValues;
    private AttributeTable unsignedAttributeValues;
    private byte[] resultDigest;
    protected final SignerInfo info;
    protected final AlgorithmIdentifier digestAlgorithm;
    protected final AlgorithmIdentifier encryptionAlgorithm;
    protected final ASN1Set signedAttributeSet;
    protected final ASN1Set unsignedAttributeSet;

    SignerInformation(SignerInfo signerInfo, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSProcessable cMSProcessable, byte[] byArray) {
        this.info = signerInfo;
        this.contentType = aSN1ObjectIdentifier;
        this.isCounterSignature = aSN1ObjectIdentifier == null;
        SignerIdentifier signerIdentifier = signerInfo.getSID();
        if (signerIdentifier.isTagged()) {
            ASN1OctetString aSN1OctetString = ASN1OctetString.getInstance(signerIdentifier.getId());
            this.sid = new SignerId(aSN1OctetString.getOctets());
        } else {
            IssuerAndSerialNumber issuerAndSerialNumber = IssuerAndSerialNumber.getInstance(signerIdentifier.getId());
            this.sid = new SignerId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
        }
        this.digestAlgorithm = signerInfo.getDigestAlgorithm();
        this.signedAttributeSet = signerInfo.getAuthenticatedAttributes();
        this.unsignedAttributeSet = signerInfo.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = signerInfo.getDigestEncryptionAlgorithm();
        this.signature = signerInfo.getEncryptedDigest().getOctets();
        this.content = cMSProcessable;
        this.resultDigest = byArray;
    }

    protected SignerInformation(SignerInformation signerInformation) {
        this(signerInformation, signerInformation.info);
    }

    protected SignerInformation(SignerInformation signerInformation, SignerInfo signerInfo) {
        this.info = signerInfo;
        this.contentType = signerInformation.contentType;
        this.isCounterSignature = signerInformation.isCounterSignature();
        this.sid = signerInformation.getSID();
        this.digestAlgorithm = signerInfo.getDigestAlgorithm();
        this.signedAttributeSet = signerInfo.getAuthenticatedAttributes();
        this.unsignedAttributeSet = signerInfo.getUnauthenticatedAttributes();
        this.encryptionAlgorithm = signerInfo.getDigestEncryptionAlgorithm();
        this.signature = signerInfo.getEncryptedDigest().getOctets();
        this.content = signerInformation.content;
        this.resultDigest = signerInformation.resultDigest;
        this.signedAttributeValues = this.getSignedAttributes();
        this.unsignedAttributeValues = this.getUnsignedAttributes();
    }

    public boolean isCounterSignature() {
        return this.isCounterSignature;
    }

    public ASN1ObjectIdentifier getContentType() {
        return this.contentType;
    }

    public SignerId getSID() {
        return this.sid;
    }

    public int getVersion() {
        return this.info.getVersion().intValueExact();
    }

    public AlgorithmIdentifier getDigestAlgorithmID() {
        return this.digestAlgorithm;
    }

    public String getDigestAlgOID() {
        return this.digestAlgorithm.getAlgorithm().getId();
    }

    public byte[] getDigestAlgParams() {
        try {
            return CMSUtils.encodeObj(this.digestAlgorithm.getParameters());
        }
        catch (Exception exception) {
            throw new RuntimeException("exception getting digest parameters " + exception);
        }
    }

    public byte[] getContentDigest() {
        if (this.resultDigest == null) {
            throw new IllegalStateException("method can only be called after verify.");
        }
        return Arrays.clone(this.resultDigest);
    }

    public String getEncryptionAlgOID() {
        return this.encryptionAlgorithm.getAlgorithm().getId();
    }

    public byte[] getEncryptionAlgParams() {
        try {
            return CMSUtils.encodeObj(this.encryptionAlgorithm.getParameters());
        }
        catch (Exception exception) {
            throw new RuntimeException("exception getting encryption parameters " + exception);
        }
    }

    public AttributeTable getSignedAttributes() {
        if (this.signedAttributeSet != null && this.signedAttributeValues == null) {
            this.signedAttributeValues = new AttributeTable(this.signedAttributeSet);
        }
        return this.signedAttributeValues;
    }

    public AttributeTable getUnsignedAttributes() {
        if (this.unsignedAttributeSet != null && this.unsignedAttributeValues == null) {
            this.unsignedAttributeValues = new AttributeTable(this.unsignedAttributeSet);
        }
        return this.unsignedAttributeValues;
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public SignerInformationStore getCounterSignatures() {
        AttributeTable attributeTable = this.getUnsignedAttributes();
        if (attributeTable == null) {
            return new SignerInformationStore(new ArrayList<SignerInformation>(0));
        }
        ArrayList<SignerInformation> arrayList = new ArrayList<SignerInformation>();
        ASN1EncodableVector aSN1EncodableVector = attributeTable.getAll(CMSAttributes.counterSignature);
        for (int i = 0; i < aSN1EncodableVector.size(); ++i) {
            Attribute attribute = (Attribute)aSN1EncodableVector.get(i);
            ASN1Set aSN1Set = attribute.getAttrValues();
            if (aSN1Set.size() < 1) {
                // empty if block
            }
            Enumeration enumeration = aSN1Set.getObjects();
            while (enumeration.hasMoreElements()) {
                SignerInfo signerInfo = SignerInfo.getInstance(enumeration.nextElement());
                arrayList.add(new SignerInformation(signerInfo, null, new CMSProcessableByteArray(this.getSignature()), null));
            }
        }
        return new SignerInformationStore(arrayList);
    }

    public byte[] getEncodedSignedAttributes() throws IOException {
        if (this.signedAttributeSet != null) {
            return this.signedAttributeSet.getEncoded("DER");
        }
        return null;
    }

    private boolean doVerify(SignerInformationVerifier signerInformationVerifier) throws CMSException {
        Object object;
        Object object2;
        Object object3;
        ContentVerifier contentVerifier;
        String string = CMSSignedHelper.INSTANCE.getEncryptionAlgName(this.getEncryptionAlgOID());
        AlgorithmIdentifier algorithmIdentifier = this.signedAttributeSet != null ? this.info.getDigestAlgorithm() : SignerInformation.translateBrokenRSAPkcs7(this.encryptionAlgorithm, this.info.getDigestAlgorithm());
        try {
            contentVerifier = signerInformationVerifier.getContentVerifier(this.encryptionAlgorithm, algorithmIdentifier);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new CMSException("can't create content verifier: " + operatorCreationException.getMessage(), operatorCreationException);
        }
        try {
            object3 = contentVerifier.getOutputStream();
            if (this.resultDigest == null) {
                object2 = signerInformationVerifier.getDigestCalculator(algorithmIdentifier);
                if (this.content != null) {
                    object = object2.getOutputStream();
                    if (this.signedAttributeSet == null) {
                        if (contentVerifier instanceof RawContentVerifier) {
                            this.content.write((OutputStream)object);
                        } else {
                            TeeOutputStream teeOutputStream = new TeeOutputStream((OutputStream)object, (OutputStream)object3);
                            this.content.write(teeOutputStream);
                            ((OutputStream)teeOutputStream).close();
                        }
                    } else {
                        this.content.write((OutputStream)object);
                        ((OutputStream)object3).write(this.getEncodedSignedAttributes());
                    }
                    ((OutputStream)object).close();
                } else if (this.signedAttributeSet != null) {
                    ((OutputStream)object3).write(this.getEncodedSignedAttributes());
                } else {
                    throw new CMSException("data not encapsulated in signature - use detached constructor.");
                }
                this.resultDigest = object2.getDigest();
            } else if (this.signedAttributeSet == null) {
                if (this.content != null) {
                    this.content.write((OutputStream)object3);
                }
            } else {
                ((OutputStream)object3).write(this.getEncodedSignedAttributes());
            }
            ((OutputStream)object3).close();
        }
        catch (IOException iOException) {
            throw new CMSException("can't process mime object to create signature.", iOException);
        }
        catch (OperatorCreationException operatorCreationException) {
            throw new CMSException("can't create digest calculator: " + operatorCreationException.getMessage(), operatorCreationException);
        }
        this.verifyContentTypeAttributeValue();
        object3 = this.getSignedAttributes();
        this.verifyAlgorithmIdentifierProtectionAttribute((AttributeTable)object3);
        this.verifyMessageDigestAttribute();
        this.verifyCounterSignatureAttribute((AttributeTable)object3);
        try {
            if (this.signedAttributeSet == null && this.resultDigest != null && contentVerifier instanceof RawContentVerifier) {
                object2 = (RawContentVerifier)((Object)contentVerifier);
                if (string.equals("RSA")) {
                    object = new DigestInfo(new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), DERNull.INSTANCE), this.resultDigest);
                    return object2.verify(((ASN1Object)object).getEncoded("DER"), this.getSignature());
                }
                return object2.verify(this.resultDigest, this.getSignature());
            }
            return contentVerifier.verify(this.getSignature());
        }
        catch (IOException iOException) {
            throw new CMSException("can't process mime object to create signature.", iOException);
        }
    }

    private void verifyContentTypeAttributeValue() throws CMSException {
        ASN1Primitive aSN1Primitive = this.getSingleValuedSignedAttribute(CMSAttributes.contentType, "content-type");
        if (aSN1Primitive == null) {
            if (!this.isCounterSignature && this.signedAttributeSet != null) {
                throw new CMSException("The content-type attribute type MUST be present whenever signed attributes are present in signed-data");
            }
        } else {
            if (this.isCounterSignature) {
                throw new CMSException("[For counter signatures,] the signedAttributes field MUST NOT contain a content-type attribute");
            }
            if (!(aSN1Primitive instanceof ASN1ObjectIdentifier)) {
                throw new CMSException("content-type attribute value not of ASN.1 type 'OBJECT IDENTIFIER'");
            }
            ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)aSN1Primitive;
            if (!aSN1ObjectIdentifier.equals(this.contentType)) {
                throw new CMSException("content-type attribute value does not match eContentType");
            }
        }
    }

    private void verifyMessageDigestAttribute() throws CMSException {
        ASN1Primitive aSN1Primitive = this.getSingleValuedSignedAttribute(CMSAttributes.messageDigest, "message-digest");
        if (aSN1Primitive == null) {
            if (this.signedAttributeSet != null) {
                throw new CMSException("the message-digest signed attribute type MUST be present when there are any signed attributes present");
            }
        } else {
            if (!(aSN1Primitive instanceof ASN1OctetString)) {
                throw new CMSException("message-digest attribute value not of ASN.1 type 'OCTET STRING'");
            }
            ASN1OctetString aSN1OctetString = (ASN1OctetString)aSN1Primitive;
            if (!Arrays.constantTimeAreEqual(this.resultDigest, aSN1OctetString.getOctets())) {
                throw new CMSSignerDigestMismatchException("message-digest attribute value does not match calculated value");
            }
        }
    }

    private void verifyAlgorithmIdentifierProtectionAttribute(AttributeTable attributeTable) throws CMSException {
        AttributeTable attributeTable2 = this.getUnsignedAttributes();
        if (attributeTable2 != null && attributeTable2.getAll(CMSAttributes.cmsAlgorithmProtect).size() > 0) {
            throw new CMSException("A cmsAlgorithmProtect attribute MUST be a signed attribute");
        }
        if (attributeTable != null) {
            ASN1EncodableVector aSN1EncodableVector = attributeTable.getAll(CMSAttributes.cmsAlgorithmProtect);
            if (aSN1EncodableVector.size() > 1) {
                throw new CMSException("Only one instance of a cmsAlgorithmProtect attribute can be present");
            }
            if (aSN1EncodableVector.size() > 0) {
                Attribute attribute = Attribute.getInstance(aSN1EncodableVector.get(0));
                if (attribute.getAttrValues().size() != 1) {
                    throw new CMSException("A cmsAlgorithmProtect attribute MUST contain exactly one value");
                }
                CMSAlgorithmProtection cMSAlgorithmProtection = CMSAlgorithmProtection.getInstance(attribute.getAttributeValues()[0]);
                if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getDigestAlgorithm(), this.info.getDigestAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for digestAlgorithm");
                }
                if (!CMSUtils.isEquivalent(cMSAlgorithmProtection.getSignatureAlgorithm(), this.info.getDigestEncryptionAlgorithm())) {
                    throw new CMSException("CMS Algorithm Identifier Protection check failed for signatureAlgorithm");
                }
            }
        }
    }

    private void verifyCounterSignatureAttribute(AttributeTable attributeTable) throws CMSException {
        if (attributeTable != null && attributeTable.getAll(CMSAttributes.counterSignature).size() > 0) {
            throw new CMSException("A countersignature attribute MUST NOT be a signed attribute");
        }
        AttributeTable attributeTable2 = this.getUnsignedAttributes();
        if (attributeTable2 != null) {
            ASN1EncodableVector aSN1EncodableVector = attributeTable2.getAll(CMSAttributes.counterSignature);
            for (int i = 0; i < aSN1EncodableVector.size(); ++i) {
                Attribute attribute = Attribute.getInstance(aSN1EncodableVector.get(i));
                if (attribute.getAttrValues().size() >= 1) continue;
                throw new CMSException("A countersignature attribute MUST contain at least one AttributeValue");
            }
        }
    }

    public boolean verify(SignerInformationVerifier signerInformationVerifier) throws CMSException {
        X509CertificateHolder x509CertificateHolder;
        Time time = this.getSigningTime();
        if (signerInformationVerifier.hasAssociatedCertificate() && time != null && !(x509CertificateHolder = signerInformationVerifier.getAssociatedCertificate()).isValidOn(time.getDate())) {
            throw new CMSVerifierCertificateNotValidException("verifier not valid at signingTime");
        }
        return this.doVerify(signerInformationVerifier);
    }

    public SignerInfo toASN1Structure() {
        return this.info;
    }

    private ASN1Primitive getSingleValuedSignedAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) throws CMSException {
        AttributeTable attributeTable = this.getUnsignedAttributes();
        if (attributeTable != null && attributeTable.getAll(aSN1ObjectIdentifier).size() > 0) {
            throw new CMSException("The " + string + " attribute MUST NOT be an unsigned attribute");
        }
        AttributeTable attributeTable2 = this.getSignedAttributes();
        if (attributeTable2 == null) {
            return null;
        }
        ASN1EncodableVector aSN1EncodableVector = attributeTable2.getAll(aSN1ObjectIdentifier);
        switch (aSN1EncodableVector.size()) {
            case 0: {
                return null;
            }
            case 1: {
                Attribute attribute = (Attribute)aSN1EncodableVector.get(0);
                ASN1Set aSN1Set = attribute.getAttrValues();
                if (aSN1Set.size() != 1) {
                    throw new CMSException("A " + string + " attribute MUST have a single attribute value");
                }
                return aSN1Set.getObjectAt(0).toASN1Primitive();
            }
        }
        throw new CMSException("The SignedAttributes in a signerInfo MUST NOT include multiple instances of the " + string + " attribute");
    }

    private Time getSigningTime() throws CMSException {
        ASN1Primitive aSN1Primitive = this.getSingleValuedSignedAttribute(CMSAttributes.signingTime, "signing-time");
        if (aSN1Primitive == null) {
            return null;
        }
        try {
            return Time.getInstance(aSN1Primitive);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new CMSException("signing-time attribute value not a valid 'Time' structure");
        }
    }

    public static SignerInformation replaceUnsignedAttributes(SignerInformation signerInformation, AttributeTable attributeTable) {
        SignerInfo signerInfo = signerInformation.info;
        DERSet dERSet = null;
        if (attributeTable != null) {
            dERSet = new DERSet(attributeTable.toASN1EncodableVector());
        }
        return new SignerInformation(new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), dERSet), signerInformation.contentType, signerInformation.content, null);
    }

    public static SignerInformation addCounterSigners(SignerInformation signerInformation, SignerInformationStore signerInformationStore) {
        SignerInfo signerInfo = signerInformation.info;
        AttributeTable attributeTable = signerInformation.getUnsignedAttributes();
        ASN1EncodableVector aSN1EncodableVector = attributeTable != null ? attributeTable.toASN1EncodableVector() : new ASN1EncodableVector();
        ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
        Iterator<SignerInformation> iterator = signerInformationStore.getSigners().iterator();
        while (iterator.hasNext()) {
            aSN1EncodableVector2.add(iterator.next().toASN1Structure());
        }
        aSN1EncodableVector.add(new Attribute(CMSAttributes.counterSignature, new DERSet(aSN1EncodableVector2)));
        return new SignerInformation(new SignerInfo(signerInfo.getSID(), signerInfo.getDigestAlgorithm(), signerInfo.getAuthenticatedAttributes(), signerInfo.getDigestEncryptionAlgorithm(), signerInfo.getEncryptedDigest(), new DERSet(aSN1EncodableVector)), signerInformation.contentType, signerInformation.content, null);
    }

    private static AlgorithmIdentifier translateBrokenRSAPkcs7(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (PKCSObjectIdentifiers.rsaEncryption.equals(algorithmIdentifier.getAlgorithm()) && (OIWObjectIdentifiers.sha1WithRSA.equals(algorithmIdentifier2.getAlgorithm()) || PKCSObjectIdentifiers.sha1WithRSAEncryption.equals(algorithmIdentifier2.getAlgorithm()))) {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE);
        }
        return algorithmIdentifier2;
    }
}

