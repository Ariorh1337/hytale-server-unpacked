/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class Psid
extends ASN1Object {
    private final BigInteger psid;

    public Psid(long l) {
        this(BigInteger.valueOf(l));
    }

    public Psid(BigInteger bigInteger) {
        if (bigInteger.signum() < 0) {
            throw new IllegalStateException("psid must be greater than zero");
        }
        this.psid = bigInteger;
    }

    public BigInteger getPsid() {
        return this.psid;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.psid);
    }

    public static Psid getInstance(Object object) {
        if (object instanceof Psid) {
            return (Psid)object;
        }
        if (object != null) {
            return new Psid(ASN1Integer.getInstance(object).getValue());
        }
        return null;
    }
}

