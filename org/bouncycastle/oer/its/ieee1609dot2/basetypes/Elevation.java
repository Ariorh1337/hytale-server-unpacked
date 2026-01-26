/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class Elevation
extends UINT16 {
    public Elevation(UINT16 uINT16) {
        super(uINT16.getValue());
    }

    public Elevation(BigInteger bigInteger) {
        super(bigInteger);
    }

    public Elevation(int n) {
        super(n);
    }

    public Elevation(long l) {
        super(l);
    }

    protected Elevation(ASN1Integer aSN1Integer) {
        super(aSN1Integer);
    }

    public static Elevation getInstance(Object object) {
        if (object instanceof Elevation) {
            return (Elevation)object;
        }
        if (object != null) {
            return new Elevation(UINT16.getInstance(object));
        }
        return null;
    }
}

