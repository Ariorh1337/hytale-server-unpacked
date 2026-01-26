/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UintBase;

public class UINT64
extends UintBase {
    private static final BigInteger MAX = new BigInteger("18446744073709551615");

    public UINT64(BigInteger bigInteger) {
        super(bigInteger);
    }

    public UINT64(int n) {
        super(n);
    }

    public UINT64(long l) {
        super(l);
    }

    protected UINT64(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static UINT64 getInstance(Object object) {
        if (object instanceof UINT64) {
            return (UINT64)object;
        }
        if (object != null) {
            return new UINT64(ASN1Integer.getInstance(object));
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

