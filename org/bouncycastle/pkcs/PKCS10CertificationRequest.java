/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.CertificationRequest;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.util.Exceptions;

public class PKCS10CertificationRequest {
    private static Attribute[] EMPTY_ARRAY = new Attribute[0];
    private final CertificationRequest certificationRequest;
    private final boolean isAltRequest;
    private final AlgorithmIdentifier altSignature;
    private final SubjectPublicKeyInfo altPublicKey;
    private final ASN1BitString altSignatureValue;

    private static CertificationRequest parseBytes(byte[] byArray) throws IOException {
        try {
            CertificationRequest certificationRequest = CertificationRequest.getInstance(ASN1Primitive.fromByteArray(byArray));
            if (certificationRequest == null) {
                throw new PKCSIOException("empty data passed to constructor");
            }
            return certificationRequest;
        }
        catch (ClassCastException classCastException) {
            throw new PKCSIOException("malformed data: " + classCastException.getMessage(), classCastException);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new PKCSIOException("malformed data: " + illegalArgumentException.getMessage(), illegalArgumentException);
        }
    }

    private static ASN1Encodable getSingleValue(Attribute attribute) {
        ASN1Encodable[] aSN1EncodableArray = attribute.getAttributeValues();
        if (aSN1EncodableArray.length != 1) {
            throw new IllegalArgumentException("single value attribute value not size of 1");
        }
        return aSN1EncodableArray[0];
    }

    public PKCS10CertificationRequest(CertificationRequest certificationRequest) {
        if (certificationRequest == null) {
            throw new NullPointerException("certificationRequest cannot be null");
        }
        this.certificationRequest = certificationRequest;
        ASN1Set aSN1Set = certificationRequest.getCertificationRequestInfo().getAttributes();
        AlgorithmIdentifier algorithmIdentifier = null;
        SubjectPublicKeyInfo subjectPublicKeyInfo = null;
        ASN1BitString aSN1BitString = null;
        if (aSN1Set != null) {
            Enumeration enumeration = aSN1Set.getObjects();
            while (enumeration.hasMoreElements()) {
                Attribute attribute = Attribute.getInstance(enumeration.nextElement());
                if (Extension.altSignatureAlgorithm.equals(attribute.getAttrType())) {
                    algorithmIdentifier = AlgorithmIdentifier.getInstance(PKCS10CertificationRequest.getSingleValue(attribute));
                }
                if (Extension.subjectAltPublicKeyInfo.equals(attribute.getAttrType())) {
                    subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(PKCS10CertificationRequest.getSingleValue(attribute));
                }
                if (!Extension.altSignatureValue.equals(attribute.getAttrType())) continue;
                aSN1BitString = ASN1BitString.getInstance(PKCS10CertificationRequest.getSingleValue(attribute));
            }
        }
        this.isAltRequest = algorithmIdentifier != null | subjectPublicKeyInfo != null | aSN1BitString != null;
        if (this.isAltRequest && !(algorithmIdentifier != null & subjectPublicKeyInfo != null & aSN1BitString != null)) {
            throw new IllegalArgumentException("invalid alternate public key details found");
        }
        this.altSignature = algorithmIdentifier;
        this.altPublicKey = subjectPublicKeyInfo;
        this.altSignatureValue = aSN1BitString;
    }

    public PKCS10CertificationRequest(byte[] byArray) throws IOException {
        this(PKCS10CertificationRequest.parseBytes(byArray));
    }

    public CertificationRequest toASN1Structure() {
        return this.certificationRequest;
    }

    public X500Name getSubject() {
        return X500Name.getInstance(this.certificationRequest.getCertificationRequestInfo().getSubject());
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.certificationRequest.getSignatureAlgorithm();
    }

    public byte[] getSignature() {
        return this.certificationRequest.getSignature().getOctets();
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.certificationRequest.getCertificationRequestInfo().getSubjectPublicKeyInfo();
    }

    public Attribute[] getAttributes() {
        ASN1Set aSN1Set = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (aSN1Set == null) {
            return EMPTY_ARRAY;
        }
        Attribute[] attributeArray = new Attribute[aSN1Set.size()];
        for (int i = 0; i != aSN1Set.size(); ++i) {
            attributeArray[i] = Attribute.getInstance(aSN1Set.getObjectAt(i));
        }
        return attributeArray;
    }

    public Attribute[] getAttributes(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        ASN1Set aSN1Set = this.certificationRequest.getCertificationRequestInfo().getAttributes();
        if (aSN1Set == null) {
            return EMPTY_ARRAY;
        }
        ArrayList<Attribute> arrayList = new ArrayList<Attribute>();
        for (int i = 0; i != aSN1Set.size(); ++i) {
            Attribute attribute = Attribute.getInstance(aSN1Set.getObjectAt(i));
            if (!attribute.getAttrType().equals(aSN1ObjectIdentifier)) continue;
            arrayList.add(attribute);
        }
        if (arrayList.size() == 0) {
            return EMPTY_ARRAY;
        }
        return arrayList.toArray(new Attribute[arrayList.size()]);
    }

    public byte[] getEncoded() throws IOException {
        return this.certificationRequest.getEncoded();
    }

    public boolean isSignatureValid(ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        ContentVerifier contentVerifier;
        CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
        try {
            contentVerifier = contentVerifierProvider.get(this.certificationRequest.getSignatureAlgorithm());
            OutputStream outputStream = contentVerifier.getOutputStream();
            outputStream.write(certificationRequestInfo.getEncoded("DER"));
            outputStream.close();
        }
        catch (Exception exception) {
            throw new PKCSException("unable to process signature: " + exception.getMessage(), exception);
        }
        return contentVerifier.verify(this.getSignature());
    }

    public boolean hasAltPublicKey() {
        return this.isAltRequest;
    }

    public boolean isAltSignatureValid(ContentVerifierProvider contentVerifierProvider) throws PKCSException {
        Object object;
        if (!this.isAltRequest) {
            throw new IllegalStateException("no alternate public key present");
        }
        CertificationRequestInfo certificationRequestInfo = this.certificationRequest.getCertificationRequestInfo();
        ASN1Set aSN1Set = certificationRequestInfo.getAttributes();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Object object2 = aSN1Set.getObjects();
        while (object2.hasMoreElements()) {
            object = Attribute.getInstance(object2.nextElement());
            if (Extension.altSignatureValue.equals(((Attribute)object).getAttrType())) continue;
            aSN1EncodableVector.add((ASN1Encodable)object);
        }
        certificationRequestInfo = new CertificationRequestInfo(certificationRequestInfo.getSubject(), certificationRequestInfo.getSubjectPublicKeyInfo(), (ASN1Set)new DERSet(aSN1EncodableVector));
        try {
            object2 = contentVerifierProvider.get(this.altSignature);
            object = object2.getOutputStream();
            ((OutputStream)object).write(certificationRequestInfo.getEncoded("DER"));
            ((OutputStream)object).close();
        }
        catch (Exception exception) {
            throw new PKCSException("unable to process signature: " + exception.getMessage(), exception);
        }
        return object2.verify(this.altSignatureValue.getOctets());
    }

    public Extensions getRequestedExtensions() {
        Attribute[] attributeArray = this.getAttributes();
        for (int i = 0; i != attributeArray.length; ++i) {
            Attribute attribute = attributeArray[i];
            if (!PKCSObjectIdentifiers.pkcs_9_at_extensionRequest.equals(attribute.getAttrType())) continue;
            ExtensionsGenerator extensionsGenerator = new ExtensionsGenerator();
            ASN1Set aSN1Set = attribute.getAttrValues();
            if (aSN1Set == null || aSN1Set.size() == 0) {
                throw new IllegalStateException("pkcs_9_at_extensionRequest present but has no value");
            }
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1Set.getObjectAt(0));
            try {
                Enumeration enumeration = aSN1Sequence.getObjects();
                while (enumeration.hasMoreElements()) {
                    boolean bl;
                    ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(enumeration.nextElement());
                    boolean bl2 = bl = aSN1Sequence2.size() == 3 && ASN1Boolean.getInstance(aSN1Sequence2.getObjectAt(1)).isTrue();
                    if (aSN1Sequence2.size() == 2) {
                        extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(0)), false, ASN1OctetString.getInstance(aSN1Sequence2.getObjectAt(1)).getOctets());
                        continue;
                    }
                    if (aSN1Sequence2.size() == 3) {
                        extensionsGenerator.addExtension(ASN1ObjectIdentifier.getInstance(aSN1Sequence2.getObjectAt(0)), bl, ASN1OctetString.getInstance(aSN1Sequence2.getObjectAt(2)).getOctets());
                        continue;
                    }
                    throw new IllegalStateException("incorrect sequence size of Extension get " + aSN1Sequence2.size() + " expected 2 or three");
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw Exceptions.illegalStateException("asn1 processing issue: " + illegalArgumentException.getMessage(), illegalArgumentException);
            }
            return extensionsGenerator.generate();
        }
        return null;
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof PKCS10CertificationRequest)) {
            return false;
        }
        PKCS10CertificationRequest pKCS10CertificationRequest = (PKCS10CertificationRequest)object;
        return this.toASN1Structure().equals(pKCS10CertificationRequest.toASN1Structure());
    }

    public int hashCode() {
        return this.toASN1Structure().hashCode();
    }
}

