/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERBitString;

public class EndEntityType
extends ASN1Object {
    public static final int app = 128;
    public static final int enrol = 64;
    private final ASN1BitString type;

    public EndEntityType(int n) {
        this(new DERBitString(n));
    }

    private EndEntityType(ASN1BitString aSN1BitString) {
        this.type = aSN1BitString;
    }

    public static EndEntityType getInstance(Object object) {
        if (object instanceof EndEntityType) {
            return (EndEntityType)object;
        }
        if (object != null) {
            return new EndEntityType(ASN1BitString.getInstance(object));
        }
        return null;
    }

    public ASN1BitString getType() {
        return this.type;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.type;
    }
}

