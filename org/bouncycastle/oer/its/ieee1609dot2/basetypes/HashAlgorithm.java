/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class HashAlgorithm
extends ASN1Enumerated {
    public static final HashAlgorithm sha256 = new HashAlgorithm(BigInteger.ZERO);
    public static final HashAlgorithm sha384 = new HashAlgorithm(BigIntegers.ONE);

    public HashAlgorithm(BigInteger bigInteger) {
        super(bigInteger);
        this.assertValues();
    }

    private HashAlgorithm(ASN1Enumerated aSN1Enumerated) {
        this(aSN1Enumerated.getValue());
    }

    public static HashAlgorithm getInstance(Object object) {
        if (object instanceof HashAlgorithm) {
            return (HashAlgorithm)object;
        }
        if (object != null) {
            return new HashAlgorithm(ASN1Enumerated.getInstance(object));
        }
        return null;
    }

    protected void assertValues() {
        switch (BigIntegers.intValueExact(this.getValue())) {
            case 0: 
            case 1: {
                return;
            }
        }
        throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
    }
}

