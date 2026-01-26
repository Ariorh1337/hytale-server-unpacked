/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class BitmapSsp
extends ASN1Object {
    private final DEROctetString string;

    public BitmapSsp(byte[] byArray) {
        this.string = new DEROctetString(Arrays.clone(byArray));
    }

    public BitmapSsp(DEROctetString dEROctetString) {
        this.string = dEROctetString;
    }

    public static BitmapSsp getInstance(Object object) {
        if (object instanceof BitmapSsp) {
            return (BitmapSsp)object;
        }
        if (object != null) {
            return new BitmapSsp(DEROctetString.getInstance(object).getOctets());
        }
        return null;
    }

    public DEROctetString getString() {
        return this.string;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.string;
    }
}

