/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

public class SymmetricEncryptionKey
extends ASN1Object
implements ASN1Choice {
    public static final int aes128ccm = 0;
    private final int choice;
    private final ASN1Encodable symmetricEncryptionKey;

    public SymmetricEncryptionKey(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.symmetricEncryptionKey = aSN1Encodable;
    }

    private SymmetricEncryptionKey(ASN1TaggedObject aSN1TaggedObject) {
        ASN1OctetString aSN1OctetString;
        this.choice = aSN1TaggedObject.getTagNo();
        if (this.choice == 0) {
            aSN1OctetString = DEROctetString.getInstance(aSN1TaggedObject.getExplicitBaseObject());
            if (aSN1OctetString.getOctets().length != 16) {
                throw new IllegalArgumentException("aes128ccm string not 16 bytes");
            }
        } else {
            throw new IllegalArgumentException("invalid choice value " + this.choice);
        }
        this.symmetricEncryptionKey = aSN1OctetString;
    }

    public static SymmetricEncryptionKey getInstance(Object object) {
        if (object instanceof SymmetricEncryptionKey) {
            return (SymmetricEncryptionKey)object;
        }
        if (object != null) {
            return new SymmetricEncryptionKey(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public static SymmetricEncryptionKey aes128ccm(byte[] byArray) {
        return new SymmetricEncryptionKey(0, new DEROctetString(byArray));
    }

    public static SymmetricEncryptionKey aes128ccm(ASN1OctetString aSN1OctetString) {
        return new SymmetricEncryptionKey(0, aSN1OctetString);
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getSymmetricEncryptionKey() {
        return this.symmetricEncryptionKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.symmetricEncryptionKey);
    }
}

