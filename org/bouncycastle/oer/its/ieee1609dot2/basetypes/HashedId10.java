/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class HashedId10
extends HashedId {
    public HashedId10(byte[] byArray) {
        super(byArray);
        if (byArray.length != 10) {
            throw new IllegalArgumentException("hash id not 10 bytes");
        }
    }

    public static HashedId10 getInstance(Object object) {
        if (object instanceof HashedId10) {
            return (HashedId10)object;
        }
        if (object != null) {
            byte[] byArray = ASN1OctetString.getInstance(object).getOctets();
            return new HashedId10(byArray);
        }
        return null;
    }
}

