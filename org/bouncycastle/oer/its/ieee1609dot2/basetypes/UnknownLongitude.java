/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class UnknownLongitude
extends Longitude {
    public static final UnknownLongitude INSTANCE = new UnknownLongitude();

    public UnknownLongitude() {
        super(1800000001L);
    }

    public static UnknownLongitude getInstance(Object object) {
        if (object instanceof UnknownLongitude) {
            return (UnknownLongitude)object;
        }
        if (object != null) {
            ASN1Integer aSN1Integer = ASN1Integer.getInstance(object);
            if (aSN1Integer.getValue().intValue() != 1800000001) {
                throw new IllegalArgumentException("value " + aSN1Integer.getValue() + " is not 1800000001");
            }
            return INSTANCE;
        }
        return null;
    }
}

