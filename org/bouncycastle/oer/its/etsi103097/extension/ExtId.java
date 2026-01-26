/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097.extension;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class ExtId
extends ASN1Object {
    private final BigInteger extId;
    private static final BigInteger MAX = BigInteger.valueOf(255L);

    public ExtId(long l) {
        this(BigInteger.valueOf(l));
    }

    public ExtId(BigInteger bigInteger) {
        if (bigInteger.signum() < 0 || bigInteger.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value " + bigInteger + " outside of range 0...255");
        }
        this.extId = bigInteger;
    }

    public ExtId(byte[] byArray) {
        this(new BigInteger(byArray));
    }

    private ExtId(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public BigInteger getExtId() {
        return this.extId;
    }

    public static ExtId getInstance(Object object) {
        if (object instanceof ExtId) {
            return (ExtId)object;
        }
        if (object != null) {
            return new ExtId(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.extId);
    }
}

