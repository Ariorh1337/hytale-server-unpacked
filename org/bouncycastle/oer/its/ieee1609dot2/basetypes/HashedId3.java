/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId3
extends HashedId {
    public HashedId3(byte[] byArray) {
        super(byArray);
        if (byArray.length != 3) {
            throw new IllegalArgumentException("hash id not 3 bytes");
        }
    }

    public static HashedId3 getInstance(Object object) {
        if (object instanceof HashedId3) {
            return (HashedId3)object;
        }
        if (object != null) {
            byte[] byArray = ASN1OctetString.getInstance(object).getOctets();
            return new HashedId3(byArray);
        }
        return null;
    }
}

