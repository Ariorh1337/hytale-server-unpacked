/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DeltaCertificateDescriptor;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class DeltaCertificateRequestAttributeValue
implements ASN1Encodable {
    private final X500Name subject;
    private final SubjectPublicKeyInfo subjectPKInfo;
    private final Extensions extensions;
    private final AlgorithmIdentifier signatureAlgorithm;
    private final ASN1Sequence attrSeq;

    public DeltaCertificateRequestAttributeValue(Attribute attribute) {
        this(ASN1Sequence.getInstance(attribute.getAttributeValues()[0]));
    }

    public static DeltaCertificateRequestAttributeValue getInstance(Object object) {
        if (object instanceof DeltaCertificateDescriptor) {
            return (DeltaCertificateRequestAttributeValue)object;
        }
        if (object != null) {
            new DeltaCertificateRequestAttributeValue(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    DeltaCertificateRequestAttributeValue(ASN1Sequence aSN1Sequence) {
        this.attrSeq = aSN1Sequence;
        int n = 0;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.subject = X500Name.getInstance(ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(0)), true);
            ++n;
        } else {
            this.subject = null;
        }
        this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(n));
        Extensions extensions = null;
        AlgorithmIdentifier algorithmIdentifier = null;
        if (++n != aSN1Sequence.size()) {
            while (n < aSN1Sequence.size()) {
                ASN1TaggedObject aSN1TaggedObject = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(n));
                if (aSN1TaggedObject.getTagNo() == 1) {
                    extensions = Extensions.getInstance(aSN1TaggedObject, true);
                } else if (aSN1TaggedObject.getTagNo() == 2) {
                    algorithmIdentifier = AlgorithmIdentifier.getInstance(aSN1TaggedObject, true);
                } else {
                    throw new IllegalArgumentException("unknown tag");
                }
                ++n;
            }
        }
        this.extensions = extensions;
        this.signatureAlgorithm = algorithmIdentifier;
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPKInfo() {
        return this.subjectPKInfo;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.attrSeq;
    }
}

