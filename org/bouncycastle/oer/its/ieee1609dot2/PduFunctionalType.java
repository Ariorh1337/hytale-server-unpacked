/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class PduFunctionalType
extends ASN1Object {
    private static final BigInteger MAX = BigInteger.valueOf(255L);
    public static final PduFunctionalType tlsHandshake = new PduFunctionalType(1L);
    public static final PduFunctionalType iso21177ExtendedAuth = new PduFunctionalType(2L);
    private final BigInteger functionalType;

    public PduFunctionalType(long l) {
        this(BigInteger.valueOf(l));
    }

    public PduFunctionalType(BigInteger bigInteger) {
        this.functionalType = PduFunctionalType.assertValue(bigInteger);
    }

    public PduFunctionalType(byte[] byArray) {
        this(new BigInteger(byArray));
    }

    private PduFunctionalType(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static PduFunctionalType getInstance(Object object) {
        if (object instanceof PduFunctionalType) {
            return (PduFunctionalType)object;
        }
        if (object != null) {
            return new PduFunctionalType(ASN1Integer.getInstance(object));
        }
        return null;
    }

    public BigInteger getFunctionalType() {
        return this.functionalType;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.functionalType);
    }

    private static BigInteger assertValue(BigInteger bigInteger) {
        if (bigInteger.signum() < 0) {
            throw new IllegalArgumentException("value less than 0");
        }
        if (bigInteger.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value exceeds " + MAX);
        }
        return bigInteger;
    }
}

