/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase;

public class UINT8
extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(255L);

    public UINT8(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT8(int n) {
        super(n);
    }

    public UINT8(long l) {
        super(l);
    }

    protected UINT8(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT8 getInstance(Object object) {
        if (object instanceof UINT8) {
            return (UINT8)object;
        }
        if (object != null) {
            return new UINT8(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    protected void assertLimit() {
        if (this.value.signum() < 0) {
            throw new IllegalArgumentException("value must not be negative");
        }
        if (this.value.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("value 0x" + this.value.toString(16) + "  must not exceed 0x" + MAX.toString(16));
        }
    }
}

