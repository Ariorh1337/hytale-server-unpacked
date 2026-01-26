/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.BitmapSspRange;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfOctetString;

public class SspRange
extends ASN1Object
implements ASN1Choice {
    public static final int opaque = 0;
    public static final int all = 1;
    public static final int bitmapSspRange = 2;
    private final int choice;
    private final ASN1Encodable sspRange;

    public static SspRange opaque(SequenceOfOctetString sequenceOfOctetString) {
        return new SspRange(0, sequenceOfOctetString);
    }

    public static SspRange all() {
        return new SspRange(1, DERNull.INSTANCE);
    }

    public static SspRange bitmapSspRange(BitmapSspRange bitmapSspRange) {
        return new SspRange(2, bitmapSspRange);
    }

    public SspRange(int n, ASN1Encodable aSN1Encodable) {
        switch (n) {
            case 0: 
            case 1: 
            case 2: {
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + n);
            }
        }
        this.choice = n;
        this.sspRange = aSN1Encodable;
    }

    private SspRange(ASN1TaggedObject aSN1TaggedObject) {
        this(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }

    public static SspRange getInstance(Object object) {
        if (object instanceof SspRange) {
            return (SspRange)object;
        }
        if (object != null) {
            return new SspRange(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSspRange() {
        return this.sspRange;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.sspRange);
    }
}

