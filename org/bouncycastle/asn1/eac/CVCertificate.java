/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.CertificateBody;
import org.bouncycastle.asn1.eac.CertificateHolderAuthorization;
import org.bouncycastle.asn1.eac.CertificateHolderReference;
import org.bouncycastle.asn1.eac.CertificationAuthorityReference;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.asn1.eac.Flags;
import org.bouncycastle.asn1.eac.PackedDate;
import org.bouncycastle.util.Arrays;

public class CVCertificate
extends ASN1Object {
    private CertificateBody certificateBody;
    private byte[] signature;
    private int valid;
    private static int bodyValid = 1;
    private static int signValid = 2;

    private void setPrivateData(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.valid = 0;
        if (aSN1TaggedObject.hasTag(64, 33)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(aSN1TaggedObject.getBaseUniversal(false, 16));
            Enumeration enumeration = aSN1Sequence.getObjects();
            while (enumeration.hasMoreElements()) {
                Object e = enumeration.nextElement();
                if (e instanceof ASN1TaggedObject) {
                    ASN1TaggedObject aSN1TaggedObject2 = ASN1TaggedObject.getInstance(e, 64);
                    switch (aSN1TaggedObject2.getTagNo()) {
                        case 78: {
                            this.certificateBody = CertificateBody.getInstance(aSN1TaggedObject2);
                            this.valid |= bodyValid;
                            break;
                        }
                        case 55: {
                            this.signature = ASN1OctetString.getInstance(aSN1TaggedObject2.getBaseUniversal(false, 4)).getOctets();
                            this.valid |= signValid;
                            break;
                        }
                        default: {
                            throw new IOException("Invalid tag, not an Iso7816CertificateStructure :" + aSN1TaggedObject2.getTagNo());
                        }
                    }
                    continue;
                }
                throw new IOException("Invalid Object, not an Iso7816CertificateStructure");
            }
        } else {
            throw new IOException("not a CARDHOLDER_CERTIFICATE :" + aSN1TaggedObject.getTagNo());
        }
        if (this.valid != (signValid | bodyValid)) {
            throw new IOException("invalid CARDHOLDER_CERTIFICATE :" + aSN1TaggedObject.getTagNo());
        }
    }

    public CVCertificate(ASN1InputStream aSN1InputStream) throws IOException {
        this.initFrom(aSN1InputStream);
    }

    private void initFrom(ASN1InputStream aSN1InputStream) throws IOException {
        ASN1Primitive aSN1Primitive;
        while ((aSN1Primitive = aSN1InputStream.readObject()) != null) {
            if (aSN1Primitive instanceof ASN1TaggedObject) {
                this.setPrivateData((ASN1TaggedObject)aSN1Primitive);
                continue;
            }
            throw new IOException("Invalid Input Stream for creating an Iso7816CertificateStructure");
        }
    }

    private CVCertificate(ASN1TaggedObject aSN1TaggedObject) throws IOException {
        this.setPrivateData(aSN1TaggedObject);
    }

    public CVCertificate(CertificateBody certificateBody, byte[] byArray) throws IOException {
        this.certificateBody = certificateBody;
        this.signature = Arrays.clone(byArray);
        this.valid |= bodyValid;
        this.valid |= signValid;
    }

    public static CVCertificate getInstance(Object object) {
        if (object instanceof CVCertificate) {
            return (CVCertificate)object;
        }
        if (object != null) {
            try {
                return new CVCertificate(ASN1TaggedObject.getInstance(object, 64));
            }
            catch (IOException iOException) {
                throw new ASN1ParsingException("unable to parse data: " + iOException.getMessage(), iOException);
            }
        }
        return null;
    }

    public byte[] getSignature() {
        return Arrays.clone(this.signature);
    }

    public CertificateBody getBody() {
        return this.certificateBody;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        DERSequence dERSequence = new DERSequence(this.certificateBody, EACTagged.create(55, this.signature));
        return EACTagged.create(33, dERSequence);
    }

    public ASN1ObjectIdentifier getHolderAuthorization() throws IOException {
        CertificateHolderAuthorization certificateHolderAuthorization = this.certificateBody.getCertificateHolderAuthorization();
        return certificateHolderAuthorization.getOid();
    }

    public PackedDate getEffectiveDate() throws IOException {
        return this.certificateBody.getCertificateEffectiveDate();
    }

    public int getCertificateType() {
        return this.certificateBody.getCertificateType();
    }

    public PackedDate getExpirationDate() throws IOException {
        return this.certificateBody.getCertificateExpirationDate();
    }

    public int getRole() throws IOException {
        CertificateHolderAuthorization certificateHolderAuthorization = this.certificateBody.getCertificateHolderAuthorization();
        return certificateHolderAuthorization.getAccessRights();
    }

    public CertificationAuthorityReference getAuthorityReference() throws IOException {
        return this.certificateBody.getCertificationAuthorityReference();
    }

    public CertificateHolderReference getHolderReference() throws IOException {
        return this.certificateBody.getCertificateHolderReference();
    }

    public int getHolderAuthorizationRole() throws IOException {
        int n = this.certificateBody.getCertificateHolderAuthorization().getAccessRights();
        return n & 0xC0;
    }

    public Flags getHolderAuthorizationRights() throws IOException {
        return new Flags(this.certificateBody.getCertificateHolderAuthorization().getAccessRights() & 0x1F);
    }
}

