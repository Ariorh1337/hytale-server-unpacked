/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.math.ec.ECFieldElement;

public class X9FieldElement
extends ASN1Object {
    protected ECFieldElement f;
    private static X9IntegerConverter converter = new X9IntegerConverter();

    public X9FieldElement(ECFieldElement eCFieldElement) {
        this.f = eCFieldElement;
    }

    public ECFieldElement getValue() {
        return this.f;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        int n = converter.getByteLength(this.f);
        byte[] byArray = converter.integerToBytes(this.f.toBigInteger(), n);
        return new DEROctetString(byArray);
    }
}

