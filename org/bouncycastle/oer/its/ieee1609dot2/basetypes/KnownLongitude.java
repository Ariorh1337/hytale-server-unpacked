/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class KnownLongitude
extends Longitude {
    public KnownLongitude(long l) {
        super(l);
    }

    public KnownLongitude(BigInteger bigInteger) {
        super(bigInteger);
    }

    private KnownLongitude(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static KnownLongitude getInstance(Object object) {
        if (object instanceof KnownLongitude) {
            return (KnownLongitude)object;
        }
        if (object != null) {
            return new KnownLongitude(ASN1Integer.getInstance(object));
        }
        return null;
    }
}

