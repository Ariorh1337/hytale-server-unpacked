/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase;

public class UINT16
extends UintBase {
    private static final BigInteger MAX = BigInteger.valueOf(65535L);

    public UINT16(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT16(int n) {
        super(n);
    }

    public UINT16(long l) {
        super(l);
    }

    protected UINT16(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT16 getInstance(Object object) {
        if (object instanceof UINT16) {
            return (UINT16)object;
        }
        if (object != null) {
            return new UINT16(ASN1Integer.getInstance(object));
        }
        return null;
    }

    public static UINT16 valueOf(int n) {
        return new UINT16(n);
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

