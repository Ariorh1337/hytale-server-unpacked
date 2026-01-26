/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;

public class CertificateBody
extends ASN1Object {
    ASN1InputStream seq;
    private ASN1TaggedObject certificateProfileIdentifier;
    private ASN1TaggedObject certificationAuthorityReference;
    private PublicKeyDataObject publicKey;
    private ASN1TaggedObject certificateHolderReference;
    private CertificateHolderAuthorization certificateHolderAuthorization;
    private ASN1TaggedObject certificateEffectiveDate;
    private ASN1TaggedObject certificateExpirationDate;
    private int certificateType = 0;
    private static final int CPI = 1;
    private static final int CAR = 2;
    private static final int PK = 4;
    private static final int CHR = 8;
    private static final int CHA = 16;
    private static final int CEfD = 32;
    private static final int CExD = 64;
    public static final int profileType = 127;
    private static final int profileType_m = 127;
    private static final int profileType_r = 0;
    public static final int requestType = 13;
    private static final int requestType_m = 13;
    private static final int requestType_r = 2;

    private void setIso7816CertificateBody(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        if (!aSN1TaggedObject.hasTag(64, 78)) {
            throw new IOException("Bad tag : not an iso7816 CERTIFICATE_CONTENT_TEMPLATE");
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16));
        int n = aSN1Sequence.size();
        block9: for (int i = 0; i < n; ++i) {
            ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(aSN1Sequence.getObjectAt(i), 64);
            switch (aSN1TaggedObject2.getTagNo()) {
                case 41: {
                    this.setCertificateProfileIdentifier(aSN1TaggedObject2);
                    continue block9;
                }
                case 2: {
                    this.setCertificationAuthorityReference(aSN1TaggedObject2);
                    continue block9;
                }
                case 73: {
                    this.setPublicKey(PublicKeyDataObject.getInstance(aSN1TaggedObject2.getBaseUniversal(false, 16)));
                    continue block9;
                }
                case 32: {
                    this.setCertificateHolderReference(aSN1TaggedObject2);
                    continue block9;
                }
                case 76: {
                    this.setCertificateHolderAuthorization(new CertificateHolderAuthorization(aSN1TaggedObject2));
                    continue block9;
                }
                case 37: {
                    this.setCertificateEffectiveDate(aSN1TaggedObject2);
                    continue block9;
                }
                case 36: {
                    this.setCertificateExpirationDate(aSN1TaggedObject2);
                    continue block9;
                }
                default: {
                    this.certificateType = 0;
                    throw new IOException("Not a valid iso7816 ASN1TaggedObject tag " + aSN1TaggedObject2.getTagNo());
                }
            }
        }
    }

    public CertificateBody(ASN1TaggedObject aSN1TaggedObject, CertificationAuthorityReference certificationAuthorityReference, PublicKeyDataObject publicKeyDataObject, CertificateHolderReference certificateHolderReference, CertificateHolderAuthorization certificateHolderAuthorization, PackedDate packedDate, PackedDate packedDate2) {
        this.setCertificateProfileIdentifier(aSN1TaggedObject);
        this.setCertificationAuthorityReference(EACTagged.create(2, certificationAuthorityReference.getEncoded()));
        this.setPublicKey(publicKeyDataObject);
        this.setCertificateHolderReference(EACTagged.create(32, certificateHolderReference.getEncoded()));
        this.setCertificateHolderAuthorization(certificateHolderAuthorization);
        this.setCertificateEffectiveDate(EACTagged.create(37, packedDate.getEncoding()));
        this.setCertificateExpirationDate(EACTagged.create(36, packedDate2.getEncoding()));
    }

    private CertificateBody(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.setIso7816CertificateBody(aSN1TaggedObject);
    }

    private ASN1Primitive profileToASN1Object() throws IOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(7);
        aSN1EncodableVector.add(this.certificateProfileIdentifier);
        aSN1EncodableVector.add(this.certificationAuthorityReference);
        aSN1EncodableVector.add(EACTagged.create(73, this.publicKey));
        aSN1EncodableVector.add(this.certificateHolderReference);
        aSN1EncodableVector.add(this.certificateHolderAuthorization);
        aSN1EncodableVector.add(this.certificateEffectiveDate);
        aSN1EncodableVector.add(this.certificateExpirationDate);
        return EACTagged.create(78, new DERSequence(aSN1EncodableVector));
    }

    private void setCertificateProfileIdentifier(ASN1TaggedObject aSN1TaggedObject) throws IllegalArgumentException {
        if (aSN1TaggedObject.hasTag(64, 41)) {
            this.certificateProfileIdentifier = aSN1TaggedObject;
            this.certificateType |= 1;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.INTERCHANGE_PROFILE tag :" + aSN1TaggedObject.getTagNo());
        }
    }

    private void setCertificateHolderReference(ASN1TaggedObject aSN1TaggedObject) throws IllegalArgumentException {
        if (aSN1TaggedObject.hasTag(64, 32)) {
            this.certificateHolderReference = aSN1TaggedObject;
            this.certificateType |= 8;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.CARDHOLDER_NAME tag");
        }
    }

    private void setCertificationAuthorityReference(ASN1TaggedObject aSN1TaggedObject) throws IllegalArgumentException {
        if (aSN1TaggedObject.hasTag(64, 2)) {
            this.certificationAuthorityReference = aSN1TaggedObject;
            this.certificateType |= 2;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.ISSUER_IDENTIFICATION_NUMBER tag");
        }
    }

    private void setPublicKey(PublicKeyDataObject publicKeyDataObject) {
        this.publicKey = PublicKeyDataObject.getInstance(publicKeyDataObject);
        this.certificateType |= 4;
    }

    private ASN1Primitive requestToASN1Object() throws IOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(3);
        aSN1EncodableVector.add(this.certificateProfileIdentifier);
        if (this.certificationAuthorityReference != null) {
            aSN1EncodableVector.add(this.certificationAuthorityReference);
        }
        aSN1EncodableVector.add(EACTagged.create(73, this.publicKey));
        aSN1EncodableVector.add(this.certificateHolderReference);
        return EACTagged.create(78, new DERSequence(aSN1EncodableVector));
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        try {
            if ((this.certificateType & 0xFFFFFFFF) == 127) {
                return this.profileToASN1Object();
            }
            if ((this.certificateType & 0xFFFFFFFD) == 13) {
                return this.requestToASN1Object();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    public int getCertificateType() {
        return this.certificateType;
    }

    public static CertificateBody getInstance(Object object) throws IOException {
        if (object instanceof CertificateBody) {
            return (CertificateBody)object;
        }
        if (object != null) {
            return new CertificateBody(ASN1TaggedObject.getInstance(object, 64));
        }
        return null;
    }

    public PackedDate getCertificateEffectiveDate() {
        if ((this.certificateType & 0x20) == 32) {
            return new PackedDate(ASN1OctetString.getInstance(this.certificateEffectiveDate.getBaseUniversal(false, 4)).getOctets());
        }
        return null;
    }

    private void setCertificateEffectiveDate(ASN1TaggedObject aSN1TaggedObject) throws IllegalArgumentException {
        if (aSN1TaggedObject.hasTag(64, 37)) {
            this.certificateEffectiveDate = aSN1TaggedObject;
            this.certificateType |= 0x20;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EFFECTIVE_DATE tag :" + aSN1TaggedObject.getTagNo());
        }
    }

    public PackedDate getCertificateExpirationDate() throws IOException {
        if ((this.certificateType & 0x40) == 64) {
            return new PackedDate(ASN1OctetString.getInstance(this.certificateExpirationDate.getBaseUniversal(false, 4)).getOctets());
        }
        throw new IOException("certificate Expiration Date not set");
    }

    private void setCertificateExpirationDate(ASN1TaggedObject aSN1TaggedObject) throws IllegalArgumentException {
        if (aSN1TaggedObject.hasTag(64, 36)) {
            this.certificateExpirationDate = aSN1TaggedObject;
            this.certificateType |= 0x40;
        } else {
            throw new IllegalArgumentException("Not an Iso7816Tags.APPLICATION_EXPIRATION_DATE tag");
        }
    }

    public CertificateHolderAuthorization getCertificateHolderAuthorization() throws IOException {
        if ((this.certificateType & 0x10) == 16) {
            return this.certificateHolderAuthorization;
        }
        throw new IOException("Certificate Holder Authorisation not set");
    }

    private void setCertificateHolderAuthorization(CertificateHolderAuthorization certificateHolderAuthorization) {
        this.certificateHolderAuthorization = certificateHolderAuthorization;
        this.certificateType |= 0x10;
    }

    public CertificateHolderReference getCertificateHolderReference() {
        return new CertificateHolderReference(ASN1OctetString.getInstance(this.certificateHolderReference.getBaseUniversal(false, 4)).getOctets());
    }

    public ASN1TaggedObject getCertificateProfileIdentifier() {
        return this.certificateProfileIdentifier;
    }

    public CertificationAuthorityReference getCertificationAuthorityReference() throws IOException {
        if ((this.certificateType & 2) == 2) {
            return new CertificationAuthorityReference(ASN1OctetString.getInstance(this.certificationAuthorityReference.getBaseUniversal(false, 4)).getOctets());
        }
        throw new IOException("Certification authority reference not set");
    }

    public PublicKeyDataObject getPublicKey() {
        return this.publicKey;
    }
}

