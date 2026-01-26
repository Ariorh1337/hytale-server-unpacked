/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class Version
extends ASN1Object {
    private final BigInteger version;

    public Version(BigInteger bigInteger) {
        this.version = bigInteger;
    }

    public Version(int n) {
        this(BigInteger.valueOf(n));
    }

    public Version(long l) {
        this(BigInteger.valueOf(l));
    }

    protected Version(ASN1Integer aSN1Integer) {
        this.version = aSN1Integer.getValue();
    }

    public BigInteger getVersion() {
        return this.version;
    }

    public static Version getInstance(Object object) {
        if (object instanceof UINT8) {
            return (Version)object;
        }
        if (object != null) {
            return new Version(ASN1Integer.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.version);
    }
}

