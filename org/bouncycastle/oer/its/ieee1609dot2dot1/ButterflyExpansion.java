/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class ButterflyExpansion
extends ASN1Object
implements ASN1Choice {
    public static final int aes128 = 0;
    protected final int choice;
    protected final ASN1Encodable butterflyExpansion;

    ButterflyExpansion(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.butterflyExpansion = aSN1Encodable;
    }

    private ButterflyExpansion(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.butterflyExpansion = DEROctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static ButterflyExpansion getInstance(Object object) {
        if (object instanceof ButterflyExpansion) {
            return (ButterflyExpansion)object;
        }
        if (object != null) {
            return new ButterflyExpansion(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public static ButterflyExpansion aes128(byte[] byArray) {
        if (byArray.length != 16) {
            throw new IllegalArgumentException("length must be 16");
        }
        return new ButterflyExpansion(0, new DEROctetString(byArray));
    }

    public static ButterflyExpansion aes128(ASN1OctetString aSN1OctetString) {
        return ButterflyExpansion.aes128(aSN1OctetString.getOctets());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.butterflyExpansion);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getButterflyExpansion() {
        return this.butterflyExpansion;
    }
}

