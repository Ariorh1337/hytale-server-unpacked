/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class CountryOnly
extends UINT16
implements RegionInterface {
    public CountryOnly(int n) {
        super(n);
    }

    public CountryOnly(BigInteger bigInteger) {
        super(bigInteger);
    }

    public static CountryOnly getInstance(Object object) {
        if (object instanceof CountryOnly) {
            return (CountryOnly)object;
        }
        if (object != null) {
            return new CountryOnly(ASN1Integer.getInstance(object).getValue());
        }
        return null;
    }
}

