/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public class HashedId
extends ASN1Object {
    private final byte[] id;

    protected HashedId(byte[] byArray) {
        this.id = Arrays.clone(byArray);
    }

    public byte[] getHashBytes() {
        return Arrays.clone(this.id);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        if (!super.equals(object)) {
            return false;
        }
        HashedId hashedId = (HashedId)object;
        return java.util.Arrays.equals(this.id, hashedId.id);
    }

    @Override
    public int hashCode() {
        int n = super.hashCode();
        n = 31 * n + java.util.Arrays.hashCode(this.id);
        return n;
    }
}

