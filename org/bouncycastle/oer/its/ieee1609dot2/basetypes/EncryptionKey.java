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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SymmetricEncryptionKey;

public class EncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int publicOption = 0;
    public static final int symmetric = 1;
    private final int choice;
    private final ASN1Encodable encryptionKey;

    public static EncryptionKey getInstance(Object object) {
        if (object instanceof EncryptionKey) {
            return (EncryptionKey)object;
        }
        if (object != null) {
            return new EncryptionKey(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public EncryptionKey(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        switch (n) {
            case 0: 
            case 1: {
                this.encryptionKey = aSN1Encodable;
                return;
            }
        }
        throw new IllegalArgumentException("invalid choice value " + n);
    }

    public static EncryptionKey publicOption(PublicEncryptionKey publicEncryptionKey) {
        return new EncryptionKey(0, publicEncryptionKey);
    }

    public static EncryptionKey symmetric(SymmetricEncryptionKey symmetricEncryptionKey) {
        return new EncryptionKey(1, symmetricEncryptionKey);
    }

    private EncryptionKey(ASN1TaggedObject aSN1TaggedObject) {
        this(aSN1TaggedObject.getTagNo(), aSN1TaggedObject.getExplicitBaseObject());
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getEncryptionKey() {
        return this.encryptionKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.encryptionKey);
    }
}

