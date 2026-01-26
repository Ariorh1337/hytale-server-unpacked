/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId;

public class CrlEntry
extends HashedId {
    public CrlEntry(byte[] byArray) {
        super(byArray);
        if (byArray.length != 8) {
            throw new IllegalArgumentException("expected 8 bytes");
        }
    }

    private CrlEntry(ASN1OctetString aSN1OctetString) {
        super(aSN1OctetString.getOctets());
    }

    public static CrlEntry getInstance(Object object) {
        if (object instanceof CrlEntry) {
            return (CrlEntry)object;
        }
        if (object != null) {
            return new CrlEntry(ASN1OctetString.getInstance(object));
        }
        return null;
    }
}

