/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class OneEightyDegreeInt
extends ASN1Object {
    private static final BigInteger loweBound = new BigInteger("-1799999999");
    private static final BigInteger upperBound = new BigInteger("1800000000");
    private static final BigInteger unknown = new BigInteger("1800000001");
    private final BigInteger value;

    public OneEightyDegreeInt(long l) {
        this(BigInteger.valueOf(l));
    }

    public OneEightyDegreeInt(BigInteger bigInteger) {
        if (!bigInteger.equals(unknown)) {
            if (bigInteger.compareTo(loweBound) < 0) {
                throw new IllegalStateException("one eighty degree int cannot be less than -1799999999");
            }
            if (bigInteger.compareTo(upperBound) > 0) {
                throw new IllegalStateException("one eighty degree int cannot be greater than 1800000000");
            }
        }
        this.value = bigInteger;
    }

    private OneEightyDegreeInt(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }

    public BigInteger getValue() {
        return this.value;
    }

    public static OneEightyDegreeInt getInstance(Object object) {
        if (object instanceof OneEightyDegreeInt) {
            return (OneEightyDegreeInt)object;
        }
        if (object != null) {
            return new OneEightyDegreeInt(ASN1Integer.getInstance(object));
        }
        return null;
    }

    private static BigInteger assertValue(BigInteger bigInteger) {
        return bigInteger;
    }
}

