/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class LaId
extends ASN1Object {
    private final byte[] laId;

    public LaId(byte[] byArray) {
        this.laId = byArray;
        this.assertLength();
    }

    private LaId(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static LaId getInstance(Object object) {
        if (object instanceof LaId) {
            return (LaId)object;
        }
        if (object != null) {
            return new LaId(DEROctetString.getInstance(object));
        }
        return null;
    }

    private void assertLength() {
        if (this.laId.length != 2) {
            throw new IllegalArgumentException("laId must be 2 octets");
        }
    }

    public byte[] getLaId() {
        return Arrays.clone(this.laId);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.laId);
    }
}

