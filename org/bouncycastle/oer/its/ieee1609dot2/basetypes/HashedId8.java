/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId8
extends HashedId {
    public HashedId8(byte[] byArray) {
        super(byArray);
        if (byArray.length != 8) {
            throw new IllegalArgumentException("hash id not 8 bytes");
        }
    }

    public static HashedId8 getInstance(Object object) {
        if (object instanceof HashedId8) {
            return (HashedId8)object;
        }
        if (object != null) {
            byte[] byArray = ASN1OctetString.getInstance(object).getOctets();
            return new HashedId8(byArray);
        }
        return null;
    }
}

