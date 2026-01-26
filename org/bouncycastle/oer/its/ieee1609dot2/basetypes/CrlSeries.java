/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class CrlSeries
extends UINT16 {
    public CrlSeries(int n) {
        super(n);
    }

    public CrlSeries(BigInteger bigInteger) {
        super(bigInteger);
    }

    public static CrlSeries getInstance(Object object) {
        if (object instanceof CrlSeries) {
            return (CrlSeries)object;
        }
        if (object != null) {
            return new CrlSeries(ASN1Integer.getInstance(object).getValue());
        }
        return null;
    }
}

