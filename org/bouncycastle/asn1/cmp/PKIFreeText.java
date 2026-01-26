/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;

public class PKIFreeText
extends ASN1Object {
    ASN1Sequence strings;

    private PKIFreeText(ASN1Sequence aSN1Sequence) {
        Enumeration enumeration = aSN1Sequence.getObjects();
        while (enumeration.hasMoreElements()) {
            if (enumeration.nextElement() instanceof ASN1UTF8String) continue;
            throw new IllegalArgumentException("attempt to insert non UTF8 STRING into PKIFreeText");
        }
        this.strings = aSN1Sequence;
    }

    public PKIFreeText(ASN1UTF8String aSN1UTF8String) {
        this.strings = new DERSequence(aSN1UTF8String);
    }

    public PKIFreeText(String string) {
        this(new DERUTF8String(string));
    }

    public PKIFreeText(ASN1UTF8String[] aSN1UTF8StringArray) {
        this.strings = new DERSequence(aSN1UTF8StringArray);
    }

    public PKIFreeText(String[] stringArray) {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(stringArray.length);
        for (int i = 0; i < stringArray.length; ++i) {
            aSN1EncodableVector.add(new DERUTF8String(stringArray[i]));
        }
        this.strings = new DERSequence(aSN1EncodableVector);
    }

    public static PKIFreeText getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return PKIFreeText.getInstance(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    public static PKIFreeText getInstance(Object object) {
        if (object instanceof PKIFreeText) {
            return (PKIFreeText)object;
        }
        if (object != null) {
            return new PKIFreeText(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public int size() {
        return this.strings.size();
    }

    public DERUTF8String getStringAt(int n) {
        ASN1UTF8String aSN1UTF8String = this.getStringAtUTF8(n);
        return null == aSN1UTF8String || aSN1UTF8String instanceof DERUTF8String ? (DERUTF8String)aSN1UTF8String : new DERUTF8String(aSN1UTF8String.getString());
    }

    public ASN1UTF8String getStringAtUTF8(int n) {
        return (ASN1UTF8String)this.strings.getObjectAt(n);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.strings;
    }
}

