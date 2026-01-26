/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.util.Arrays;

public class HashedData
extends ASN1Object
implements ASN1Choice {
    public static final int sha256HashedData = 0;
    public static final int sha384HashedData = 1;
    public static final int reserved = 2;
    private final int choice;
    private final ASN1Encodable hashedData;

    public HashedData(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.hashedData = aSN1Encodable;
    }

    private HashedData(ASN1TaggedObject aSN1TaggedObject) {
        switch (aSN1TaggedObject.getTagNo()) {
            case 0: 
            case 1: 
            case 2: {
                this.choice = aSN1TaggedObject.getTagNo();
                this.hashedData = DEROctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + aSN1TaggedObject.getTagNo());
            }
        }
    }

    public static HashedData sha256HashedData(ASN1OctetString aSN1OctetString) {
        return new HashedData(0, aSN1OctetString);
    }

    public static HashedData sha256HashedData(byte[] byArray) {
        return new HashedData(0, new DEROctetString(Arrays.clone(byArray)));
    }

    public static HashedData sha384HashedData(ASN1OctetString aSN1OctetString) {
        return new HashedData(1, aSN1OctetString);
    }

    public static HashedData sha384HashedData(byte[] byArray) {
        return new HashedData(1, new DEROctetString(Arrays.clone(byArray)));
    }

    public static HashedData reserved(ASN1OctetString aSN1OctetString) {
        return new HashedData(2, aSN1OctetString);
    }

    public static HashedData reserved(byte[] byArray) {
        return new HashedData(2, new DEROctetString(Arrays.clone(byArray)));
    }

    public static HashedData getInstance(Object object) {
        if (object instanceof HashedData) {
            return (HashedData)object;
        }
        if (object != null) {
            return new HashedData(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getHashedData() {
        return this.hashedData;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.hashedData);
    }
}

