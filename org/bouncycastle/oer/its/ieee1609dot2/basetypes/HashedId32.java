/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId32
extends HashedId {
    public HashedId32(byte[] byArray) {
        super(byArray);
        if (byArray.length != 32) {
            throw new IllegalArgumentException("hash id not 32 bytes");
        }
    }

    public static HashedId32 getInstance(Object object) {
        if (object instanceof HashedId32) {
            return (HashedId32)object;
        }
        if (object != null) {
            byte[] byArray = ASN1OctetString.getInstance(object).getOctets();
            return new HashedId32(byArray);
        }
        return null;
    }
}

