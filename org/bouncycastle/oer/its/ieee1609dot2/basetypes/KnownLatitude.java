/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.NinetyDegreeInt;

public class KnownLatitude
extends NinetyDegreeInt {
    public KnownLatitude(long l) {
        super(l);
    }

    public KnownLatitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private KnownLatitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static KnownLatitude getInstance(Object object) {
        if (object instanceof KnownLatitude) {
            return (KnownLatitude)object;
        }
        if (object != null) {
            return new KnownLatitude(ASN1Integer.getInstance(object));
        }
        return null;
    }
}

