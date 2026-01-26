/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP256Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EcdsaP384Signature;

public class Signature
extends ASN1Object
implements ASN1Choice {
    public static final int ecdsaNistP256Signature = 0;
    public static final int ecdsaBrainpoolP256r1Signature = 1;
    public static final int ecdsaBrainpoolP384r1Signature = 2;
    private final int choice;
    private final ASN1Encodable signature;

    public Signature(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.signature = aSN1Encodable;
    }

    public static Signature ecdsaNistP256Signature(EcdsaP256Signature ecdsaP256Signature) {
        return new Signature(0, ecdsaP256Signature);
    }

    public static Signature ecdsaBrainpoolP256r1Signature(EcdsaP256Signature ecdsaP256Signature) {
        return new Signature(1, ecdsaP256Signature);
    }

    public static Signature ecdsaBrainpoolP384r1Signature(EcdsaP384Signature ecdsaP384Signature) {
        return new Signature(2, ecdsaP384Signature);
    }

    private Signature(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: 
            case 1: {
                this.signature = EcdsaP256Signature.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.signature = EcdsaP384Signature.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public static Signature getInstance(Object object) {
        if (object instanceof Signature) {
            return (Signature)object;
        }
        if (object != null) {
            return new Signature(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSignature() {
        return this.signature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.signature);
    }
}

