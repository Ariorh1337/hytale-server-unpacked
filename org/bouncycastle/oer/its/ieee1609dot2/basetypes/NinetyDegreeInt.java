/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class NinetyDegreeInt
extends ASN1Object {
    private static final BigInteger loweBound = new BigInteger("-900000000");
    private static final BigInteger upperBound = new BigInteger("900000000");
    private static final BigInteger unknown = new BigInteger("900000001");
    private final BigInteger value;

    public NinetyDegreeInt(long l) {
        this(BigInteger.valueOf(l));
    }

    public NinetyDegreeInt(BigInteger bigInteger) {
        if (!bigInteger.equals(unknown)) {
            if (bigInteger.compareTo(loweBound) < 0) {
                throw new IllegalStateException("ninety degree int cannot be less than -900000000");
            }
            if (bigInteger.compareTo(upperBound) > 0) {
                throw new IllegalStateException("ninety degree int cannot be greater than 900000000");
            }
        }
        this.value = bigInteger;
    }

    private NinetyDegreeInt(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public BigInteger getValue() {
        return this.value;
    }

    public static NinetyDegreeInt getInstance(Object object) {
        if (object instanceof NinetyDegreeInt) {
            return (NinetyDegreeInt)object;
        }
        if (object != null) {
            return new NinetyDegreeInt(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.value);
    }
}

