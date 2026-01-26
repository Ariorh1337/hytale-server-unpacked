/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.OneEightyDegreeInt;

public class Longitude
extends OneEightyDegreeInt {
    public Longitude(long l) {
        super(l);
    }

    public Longitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private Longitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static Longitude getInstance(Object object) {
        if (object instanceof Longitude) {
            return (Longitude)object;
        }
        if (object != null) {
            return new Longitude(ASN1Integer.getInstance(object));
        }
        return null;
    }
}

