/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT64;
import org.bouncycastle.util.BigIntegers;

public class Time64
extends UINT64 {
    public static long etsiEpochMicros = Time32.etsiEpochMillis * 1000L;

    public Time64(long l) {
        this(BigInteger.valueOf(l));
    }

    public Time64(BigInteger bigInteger) {
        super(bigInteger);
    }

    public Time64(UINT64 uINT64) {
        this(uINT64.getValue());
    }

    public static Time64 now() {
        return new Time64(1000L * System.currentTimeMillis() - etsiEpochMicros);
    }

    public static Time64 ofUnixMillis(long l) {
        return new Time64(l * 1000L - etsiEpochMicros);
    }

    public static Time64 getInstance(Object object) {
        if (object instanceof UINT64) {
            return new Time64((UINT64)object);
        }
        if (object != null) {
            return new Time64(ASN1Integer.getInstance(object).getValue());
        }
        return null;
    }

    public long toUnixMillis() {
        return (BigIntegers.longValueExact(this.getValue()) + etsiEpochMicros) / 1000L;
    }
}

