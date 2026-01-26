/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.NinetyDegreeInt;

public class Latitude
extends NinetyDegreeInt {
    public Latitude(long l) {
        super(l);
    }

    public Latitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private Latitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static Latitude getInstance(Object object) {
        if (object instanceof Latitude) {
            return (Latitude)object;
        }
        if (object != null) {
            return new Latitude(ASN1Integer.getInstance(object));
        }
        return null;
    }
}

