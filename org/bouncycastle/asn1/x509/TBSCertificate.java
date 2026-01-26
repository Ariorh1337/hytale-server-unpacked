/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.Validity;
import org.bouncycastle.util.Properties;

public class TBSCertificate
extends ASN1Object {
    ASN1Sequence seq;
    ASN1Integer version;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Validity validity;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    ASN1BitString issuerUniqueId;
    ASN1BitString subjectUniqueId;
    Extensions extensions;

    public static TBSCertificate getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return TBSCertificate.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static TBSCertificate getInstance(Object object) {
        if (object instanceof TBSCertificate) {
            return (TBSCertificate)object;
        }
        if (object != null) {
            return new TBSCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private TBSCertificate(ASN1Sequence aSN1Sequence) {
        int n;
        int n2 = 0;
        this.seq = aSN1Sequence;
        if (aSN1Sequence.getObjectAt(0) instanceof ASN1TaggedObject) {
            this.version = ASN1Integer.getInstance((ASN1TaggedObject)aSN1Sequence.getObjectAt(0), true);
        } else {
            n2 = -1;
            this.version = new ASN1Integer(0L);
        }
        boolean bl = false;
        boolean bl2 = false;
        if (this.version.hasValue(0)) {
            bl = true;
        } else if (this.version.hasValue(1)) {
            bl2 = true;
        } else if (!this.version.hasValue(2)) {
            throw new IllegalArgumentException("version number not recognised");
        }
        this.serialNumber = ASN1Integer.getInstance(aSN1Sequence.getObjectAt(n2 + 1));
        this.signature = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(n2 + 2));
        this.issuer = X500Name.getInstance(aSN1Sequence.getObjectAt(n2 + 3));
        this.validity = Validity.getInstance(aSN1Sequence.getObjectAt(n2 + 4));
        this.subject = X500Name.getInstance(aSN1Sequence.getObjectAt(n2 + 5));
        this.subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(aSN1Sequence.getObjectAt(n2 + 6));
        if (n != 0 && bl) {
            throw new IllegalArgumentException("version 1 certificate contains extra data");
        }
        block5: for (n = aSN1Sequence.size() - (n2 + 6) - 1; n > 0; --n) {
            ASN1TaggedObject aSN1TaggedObject = (ASN1TaggedObject)aSN1Sequence.getObjectAt(n2 + 6 + n);
            switch (aSN1TaggedObject.getTagNo()) {
                case 1: {
                    this.issuerUniqueId = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    continue block5;
                }
                case 2: {
                    this.subjectUniqueId = ASN1BitString.getInstance(aSN1TaggedObject, false);
                    continue block5;
                }
                case 3: {
                    if (bl2) {
                        throw new IllegalArgumentException("version 2 certificate cannot contain extensions");
                    }
                    this.extensions = Extensions.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, true));
                    continue block5;
                }
                default: {
                    throw new IllegalArgumentException("Unknown tag encountered in structure: " + aSN1TaggedObject.getTagNo());
                }
            }
        }
    }

    public TBSCertificate(ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, AlgorithmIdentifier algorithmIdentifier, X500Name x500Name, Validity validity, X500Name x500Name2, SubjectPublicKeyInfo subjectPublicKeyInfo, ASN1BitString aSN1BitString, ASN1BitString aSN1BitString2, Extensions extensions) {
        if (aSN1Integer2 == null) {
            throw new NullPointerException("'serialNumber' cannot be null");
        }
        if (algorithmIdentifier == null) {
            throw new NullPointerException("'signature' cannot be null");
        }
        if (x500Name == null) {
            throw new NullPointerException("'issuer' cannot be null");
        }
        if (validity == null) {
            throw new NullPointerException("'validity' cannot be null");
        }
        if (x500Name2 == null) {
            throw new NullPointerException("'subject' cannot be null");
        }
        if (subjectPublicKeyInfo == null) {
            throw new NullPointerException("'subjectPublicKeyInfo' cannot be null");
        }
        this.version = aSN1Integer != null ? aSN1Integer : new ASN1Integer(0L);
        this.serialNumber = aSN1Integer2;
        this.signature = algorithmIdentifier;
        this.issuer = x500Name;
        this.validity = validity;
        this.subject = x500Name2;
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
        this.issuerUniqueId = aSN1BitString;
        this.subjectUniqueId = aSN1BitString2;
        this.extensions = extensions;
        this.seq = null;
    }

    public int getVersionNumber() {
        return this.version.intValueExact() + 1;
    }

    public ASN1Integer getVersion() {
        return this.version;
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

    public Validity getValidity() {
        return this.validity;
    }

    public Time getStartDate() {
        return this.validity.getNotBefore();
    }

    public Time getEndDate() {
        return this.validity.getNotAfter();
    }

    public X500Name getSubject() {
        return this.subject;
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPublicKeyInfo;
    }

    public ASN1BitString getIssuerUniqueId() {
        return this.issuerUniqueId;
    }

    public ASN1BitString getSubjectUniqueId() {
        return this.subjectUniqueId;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        if (this.seq != null) {
            if (Properties.getPropertyValue("org.bouncycastle.x509.allow_non-der_tbscert") != null) {
                if (Properties.isOverrideSet("org.bouncycastle.x509.allow_non-der_tbscert")) {
                    return this.seq;
                }
            } else {
                return this.seq;
            }
        }
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(10);
        if (!this.version.hasValue(0)) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 0, (ASN1Encodable)this.version));
        }
        aSN1EncodableVector.add(this.serialNumber);
        aSN1EncodableVector.add(this.signature);
        aSN1EncodableVector.add(this.issuer);
        aSN1EncodableVector.add(this.validity);
        aSN1EncodableVector.add(this.subject);
        aSN1EncodableVector.add(this.subjectPublicKeyInfo);
        if (this.issuerUniqueId != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 1, (ASN1Encodable)this.issuerUniqueId));
        }
        if (this.subjectUniqueId != null) {
            aSN1EncodableVector.add(new DERTaggedObject(false, 2, (ASN1Encodable)this.subjectUniqueId));
        }
        if (this.extensions != null) {
            aSN1EncodableVector.add(new DERTaggedObject(true, 3, (ASN1Encodable)this.extensions));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

