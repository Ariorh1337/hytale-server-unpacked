/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2dot1.ButterflyExpansion;
import org.bouncycastle.oer.its.ieee1609dot2dot1.ButterflyParamsOriginal;

public class AdditionalParams
extends ASN1Object
implements ASN1Choice {
    public static final int original = 0;
    public static final int unified = 1;
    public static final int compactUnified = 2;
    public static final int encryptionKey = 3;
    protected final int choice;
    protected final ASN1Encodable additionalParams;

    private AdditionalParams(int n, ASN1Encodable aSN1Encodable) {
        switch (n) {
            case 0: {
                this.additionalParams = ButterflyParamsOriginal.getInstance(aSN1Encodable);
                break;
            }
            case 1: 
            case 2: {
                this.additionalParams = ButterflyExpansion.getInstance(aSN1Encodable);
                break;
            }
            case 3: {
                this.additionalParams = PublicEncryptionKey.getInstance(aSN1Encodable);
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + n);
            }
        }
        this.choice = n;
    }

    private AdditionalParams(ASN1TaggedObject aSN1TaggedObject) {
        this(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }

    public static AdditionalParams getInstance(Object object) {
        if (object instanceof AdditionalParams) {
            return (AdditionalParams)object;
        }
        if (object != null) {
            return new AdditionalParams(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public static AdditionalParams original(ButterflyParamsOriginal butterflyParamsOriginal) {
        return new AdditionalParams(0, butterflyParamsOriginal);
    }

    public static AdditionalParams unified(ButterflyExpansion butterflyExpansion) {
        return new AdditionalParams(1, butterflyExpansion);
    }

    public static AdditionalParams compactUnified(ButterflyExpansion butterflyExpansion) {
        return new AdditionalParams(2, butterflyExpansion);
    }

    public static AdditionalParams encryptionKey(PublicEncryptionKey publicEncryptionKey) {
        return new AdditionalParams(3, publicEncryptionKey);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getAdditionalParams() {
        return this.additionalParams;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.additionalParams);
    }
}

