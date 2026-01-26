/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase;

public class UINT3
extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(7L);

    public UINT3(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT3(int n) {
        super(n);
    }

    public UINT3(long l) {
        super(l);
    }

    protected UINT3(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT3 getInstance(Object object) {
        if (object instanceof UINT3) {
            return (UINT3)object;
        }
        if (object != null) {
            return new UINT3(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    protected void assertLimit() {
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        if (this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value must not exceed " + MAX.toString(16));
        }
    }
}

