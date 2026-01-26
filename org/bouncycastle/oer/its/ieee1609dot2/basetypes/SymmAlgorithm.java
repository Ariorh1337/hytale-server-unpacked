/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.util.BigIntegers;

public class SymmAlgorithm
extends ASN1Enumerated {
    public static final SymmAlgorithm aes128Ccm = new SymmAlgorithm(BigInteger.ZERO);

    public SymmAlgorithm(BigInteger bigInteger) {
        super(bigInteger);
        this.assertValues();
    }

    private SymmAlgorithm(ASN1Enumerated aSN1Enumerated) {
        super(aSN1Enumerated.getValue());
        this.assertValues();
    }

    protected void assertValues() {
        switch (BigIntegers.intValueExact(this.getValue())) {
            case 0: {
                return;
            }
        }
        throw new IllegalArgumentException("invalid enumeration value " + this.getValue());
    }

    public static SymmAlgorithm getInstance(Object object) {
        if (object instanceof SymmAlgorithm) {
            return (SymmAlgorithm)object;
        }
        if (object != null) {
            return new SymmAlgorithm(ASN1Enumerated.getInstance(object));
        }
        return null;
    }
}

