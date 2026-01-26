/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashAlgorithm;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class IssuerIdentifier
extends ASN1Object
implements ASN1Choice {
    public static final int sha256AndDigest = 0;
    public static final int self = 1;
    public static final int sha384AndDigest = 2;
    private final int choice;
    private final ASN1Encodable issuerIdentifier;

    public static IssuerIdentifier sha256AndDigest(HashedId8 hashedId8) {
        return new IssuerIdentifier(0, hashedId8);
    }

    public static IssuerIdentifier self(HashAlgorithm hashAlgorithm) {
        return new IssuerIdentifier(1, hashAlgorithm);
    }

    public static IssuerIdentifier sha384AndDigest(HashedId8 hashedId8) {
        return new IssuerIdentifier(2, hashedId8);
    }

    public IssuerIdentifier(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.issuerIdentifier = aSN1Encodable;
    }

    private IssuerIdentifier(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        ASN1Object aSN1Object = aSN1TaggedObject.getExplicitBaseObject();
        switch (this.choice) {
            case 0: 
            case 2: {
                this.issuerIdentifier = HashedId8.getInstance(aSN1Object);
                break;
            }
            case 1: {
                this.issuerIdentifier = HashAlgorithm.getInstance(aSN1Object);
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static IssuerIdentifier getInstance(Object object) {
        if (object instanceof IssuerIdentifier) {
            return (IssuerIdentifier)object;
        }
        if (object != null) {
            return new IssuerIdentifier(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public boolean isSelf() {
        return this.choice == 1;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIssuerIdentifier() {
        return this.issuerIdentifier;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.issuerIdentifier);
    }
}

