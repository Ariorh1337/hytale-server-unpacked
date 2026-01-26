/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Latitude;

public class UnknownLatitude
extends Latitude {
    public static UnknownLatitude INSTANCE = new UnknownLatitude();

    private UnknownLatitude() {
        super(900000001L);
    }

    public static UnknownLatitude getInstance(Object object) {
        if (object instanceof UnknownLatitude) {
            return (UnknownLatitude)object;
        }
        if (object != null) {
            ASN1Integer aSN1Integer = ASN1Integer.getInstance(object);
            if (aSN1Integer.getValue().intValue() != 900000001) {
                throw new IllegalArgumentException("value " + aSN1Integer.getValue() + " is not unknown value of 900000001");
            }
            return INSTANCE;
        }
        return null;
    }
}

