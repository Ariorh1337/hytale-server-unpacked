/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Validity;

public class DeltaCertificateDescriptor
extends ASN1Object {
    private final ASN1Integer serialNumber;
    private final AlgorithmIdentifier signature;
    private final X500Name issuer;
    private final Validity validity;
    private final X500Name subject;
    private final SubjectPublicKeyInfo subjectPublicKeyInfo;
    private final Extensions extensions;
    private final ASN1BitString signatureValue;

    public static DeltaCertificateDescriptor getInstance(Object object) {
        if (object instanceof DeltaCertificateDescriptor) {
            return (DeltaCertificateDescriptor)object;
        }
        if (object != null) {
            return new DeltaCertificateDescriptor(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static DeltaCertificateDescriptor fromExtensions(Extensions extensions) {
        return DeltaCertificateDescriptor.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.deltaCertificateDescriptor));
    }

    private DeltaCertificateDescriptor(ASN1Sequence aSN1Sequence) {
        ASN1Primitive aSN1Primitive;
        ASN1Object aSN1Object;
        ASN1Integer aSN1Integer = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(0));
        int n = 1;
        ASN1Encodable aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        AlgorithmIdentifier algorithmIdentifier = null;
        X500Name x500Name = null;
        Validity validity = null;
        X500Name x500Name2 = null;
        while (aSN1Encodable instanceof ASN1TaggedObject) {
            aSN1Object = ASN1TaggedObject.getInstance(aSN1Encodable);
            switch (((ASN1TaggedObject)aSN1Object).getTagNo()) {
                case 0: {
                    algorithmIdentifier = AlgorithmIdentifier.getInstance((ASN1TaggedObject)aSN1Object, true);
                    break;
                }
                case 1: {
                    x500Name = X500Name.getInstance((ASN1TaggedObject)aSN1Object, true);
                    break;
                }
                case 2: {
                    validity = Validity.getInstance((ASN1TaggedObject)aSN1Object, true);
                    break;
                }
                case 3: {
                    x500Name2 = X500Name.getInstance((ASN1TaggedObject)aSN1Object, true);
                }
            }
            aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        }
        aSN1Object = SubjectPublicKeyInfo.getInstance(aSN1Encodable);
        aSN1Encodable = aSN1Sequence.getObjectAt(n);
        Extensions extensions = null;
        while (aSN1Encodable instanceof ASN1TaggedObject) {
            aSN1Primitive = ASN1TaggedObject.getInstance(aSN1Encodable);
            switch (((ASN1TaggedObject)aSN1Primitive).getTagNo()) {
                case 4: {
                    extensions = Extensions.getInstance((ASN1TaggedObject)aSN1Primitive, true);
                }
            }
            aSN1Encodable = aSN1Sequence.getObjectAt(n++);
        }
        aSN1Primitive = ASN1BitString.getInstance(aSN1Encodable);
        this.serialNumber = aSN1Integer;
        this.signature = algorithmIdentifier;
        this.issuer = x500Name;
        this.validity = validity;
        this.subject = x500Name2;
        this.subjectPublicKeyInfo = aSN1Object;
        this.extensions = extensions;
        this.signatureValue = aSN1Primitive;
    }

    public DeltaCertificateDescriptor(ASN1Integer aSN1Integer, AlgorithmIdentifier algorithmIdentifier, X500Name x500Name, Validity validity, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo, Extensions extensions, ASN1BitString aSN1BitString) {
        if (aSN1Integer == null) {
            throw new NullPointerException("'serialNumber' cannot be null");
        }
        if (subjectPublicKeyInfo == null) {
            throw new NullPointerException("'subjectPublicKeyInfo' cannot be null");
        }
        if (aSN1BitString == null) {
            throw new NullPointerException("'signatureValue' cannot be null");
        }
        this.serialNumber = aSN1Integer;
        this.signature = algorithmIdentifier;
        this.issuer = x500Name;
        this.validity = validity;
        this.subject = x500Name2;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.extensions = extensions;
        this.signatureValue = aSN1BitString;
    }

    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }

    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }

    public X500Name getIssuer() {
        return this.issuer;
    }

    public ASN1Sequence getValidity() {
        return (ASN1Sequence)this.validity.toASN1Primitive();
    }

    public Validity getValidityObject() {
        return this.validity;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public ASN1BitString getSignatureValue() {
        return this.signatureValue;
    }

    public DeltaCertificateDescriptor trimTo(TBSCertificate tBSCertificate, Extensions extensions) {
        return DeltaCertificateDescriptor.trimDeltaCertificateDescriptor(this, tBSCertificate, extensions);
    }

    private static DeltaCertificateDescriptor trimDeltaCertificateDescriptor(DeltaCertificateDescriptor deltaCertificateDescriptor, TBSCertificate tBSCertificate, Extensions extensions) {
        Object object;
        X500Name x500Name;
        Validity validity;
        X500Name x500Name2;
        ASN1Integer aSN1Integer = deltaCertificateDescriptor.getSerialNumber();
        AlgorithmIdentifier algorithmIdentifier = deltaCertificateDescriptor.getSignature();
        if (algorithmIdentifier != null && algorithmIdentifier.equals(tBSCertificate.getSignature())) {
            algorithmIdentifier = null;
        }
        if ((x500Name2 = deltaCertificateDescriptor.getIssuer()) != null && x500Name2.equals(tBSCertificate.getIssuer())) {
            x500Name2 = null;
        }
        if ((validity = deltaCertificateDescriptor.getValidityObject()) != null && validity.equals(tBSCertificate.getValidity())) {
            validity = null;
        }
        if ((x500Name = deltaCertificateDescriptor.getSubject()) != null && x500Name.equals(tBSCertificate.getSubject())) {
            x500Name = null;
        }
        SubjectPublicKeyInfo subjectPublicKeyInfo = deltaCertificateDescriptor.getSubjectPublicKeyInfo();
        Extensions extensions2 = deltaCertificateDescriptor.getExtensions();
        if (extensions2 != null) {
            object = new ExtensionsGenerator();
            Enumeration enumeration = extensions.oids();
            while (enumeration.hasMoreElements()) {
                Extension extension;
                ASN1ObjectIdentifier aSN1ObjectIdentifier = (ASN1ObjectIdentifier)enumeration.nextElement();
                if (Extension.deltaCertificateDescriptor.equals(aSN1ObjectIdentifier) || (extension = extensions2.getExtension(aSN1ObjectIdentifier)) == null || extension.equals(extensions.getExtension(aSN1ObjectIdentifier))) continue;
                ((ExtensionsGenerator)object).addExtension(extension);
            }
            extensions2 = ((ExtensionsGenerator)object).isEmpty() ? null : ((ExtensionsGenerator)object).generate();
        }
        object = deltaCertificateDescriptor.getSignatureValue();
        return new DeltaCertificateDescriptor(aSN1Integer, algorithmIdentifier, x500Name2, validity, x500Name, subjectPublicKeyInfo, extensions2, (ASN1BitString)object);
    }

    private void addOptional(ASN1EncodableVector aSN1EncodableVector, int n, boolean bl, ASN1Object aSN1Object) {
        if (aSN1Object != null) {
            aSN1EncodableVector.add(new DERTaggedObject(bl, n, (ASN1Encodable)aSN1Object));
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(8);
        aSN1EncodableVector.add(this.serialNumber);
        this.addOptional(aSN1EncodableVector, 0, true, this.signature);
        this.addOptional(aSN1EncodableVector, 1, true, this.issuer);
        this.addOptional(aSN1EncodableVector, 2, true, this.validity);
        this.addOptional(aSN1EncodableVector, 3, true, this.subject);
        aSN1EncodableVector.add(this.subjectPublicKeyInfo);
        this.addOptional(aSN1EncodableVector, 4, true, this.extensions);
        aSN1EncodableVector.add(this.signatureValue);
        return new DERSequence(aSN1EncodableVector);
    }
}

