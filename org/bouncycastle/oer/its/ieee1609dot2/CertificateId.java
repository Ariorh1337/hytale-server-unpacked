/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.LinkageData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Hostname;

public class CertificateId
extends ASN1Object
implements ASN1Choice {
    public static final int linkageData = 0;
    public static final int name = 1;
    public static final int binaryId = 2;
    public static final int none = 3;
    private final int choice;
    private final ASN1Encodable certificateId;

    public CertificateId(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.certificateId = aSN1Encodable;
    }

    private CertificateId(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.certificateId = LinkageData.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.certificateId = Hostname.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.certificateId = DEROctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 3: {
                this.certificateId = ASN1Null.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static CertificateId linkageData(LinkageData linkageData) {
        return new CertificateId(0, linkageData);
    }

    public static CertificateId name(Hostname hostname) {
        return new CertificateId(1, hostname);
    }

    public static CertificateId binaryId(ASN1OctetString aSN1OctetString) {
        return new CertificateId(2, aSN1OctetString);
    }

    public static CertificateId binaryId(byte[] byArray) {
        return new CertificateId(2, new DEROctetString(byArray));
    }

    public static CertificateId none() {
        return new CertificateId(3, DERNull.INSTANCE);
    }

    public static CertificateId getInstance(Object object) {
        if (object instanceof CertificateId) {
            return (CertificateId)object;
        }
        if (object != null) {
            return new CertificateId(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.certificateId).toASN1Primitive();
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getCertificateId() {
        return this.certificateId;
    }
}

