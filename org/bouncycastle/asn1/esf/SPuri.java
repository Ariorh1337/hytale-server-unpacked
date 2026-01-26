/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Primitive;

public class SPuri {
    private ASN1IA5String uri;

    public static SPuri getInstance(Object object) {
        if (object instanceof SPuri) {
            return (SPuri)object;
        }
        if (object instanceof ASN1IA5String) {
            return new SPuri(ASN1IA5String.getInstance(object));
        }
        return null;
    }

    public SPuri(ASN1IA5String aSN1IA5String) {
        this.uri = aSN1IA5String;
    }

    public ASN1IA5String getUriIA5() {
        return this.uri;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.uri.toASN1Primitive();
    }
}

