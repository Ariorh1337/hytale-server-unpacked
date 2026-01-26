/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public abstract class UintBase
extends ASN1Object {
    protected final BigInteger value;

    public UintBase(BigInteger bigInteger) {
        this.value = bigInteger;
        this.assertLimit();
    }

    public UintBase(int n) {
        this(BigInteger.valueOf(n));
    }

    public UintBase(long l) {
        this(BigInteger.valueOf(l));
    }

    protected UintBase(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public BigInteger getValue() {
        return this.value;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }

    protected abstract void assertLimit();
}

