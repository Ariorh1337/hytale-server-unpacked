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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicVerificationKey;

public class VerificationKeyIndicator
extends ASN1Object
implements ASN1Choice {
    public static final int verificationKey = 0;
    public static final int reconstructionValue = 1;
    private final int choice;
    private final ASN1Encodable verificationKeyIndicator;

    public VerificationKeyIndicator(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.verificationKeyIndicator = aSN1Encodable;
    }

    private VerificationKeyIndicator(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.verificationKeyIndicator = PublicVerificationKey.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.verificationKeyIndicator = EccP256CurvePoint.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static VerificationKeyIndicator verificationKey(PublicVerificationKey publicVerificationKey) {
        return new VerificationKeyIndicator(0, publicVerificationKey);
    }

    public static VerificationKeyIndicator reconstructionValue(EccP256CurvePoint eccP256CurvePoint) {
        return new VerificationKeyIndicator(1, eccP256CurvePoint);
    }

    public static VerificationKeyIndicator getInstance(Object object) {
        if (object instanceof VerificationKeyIndicator) {
            return (VerificationKeyIndicator)object;
        }
        if (object != null) {
            return new VerificationKeyIndicator(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getVerificationKeyIndicator() {
        return this.verificationKeyIndicator;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.verificationKeyIndicator);
    }
}

