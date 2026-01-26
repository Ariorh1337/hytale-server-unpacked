/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import java.util.Date;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT32;

public class Time32
extends UINT32 {
    public static long etsiEpochMillis = 1072915200000L;

    public Time32(long l) {
        super(l);
    }

    public Time32(BigInteger bigInteger) {
        super(bigInteger);
    }

    public Time32(UINT32 uINT32) {
        this(uINT32.getValue());
    }

    public static Time32 now() {
        return Time32.ofUnixMillis(System.currentTimeMillis());
    }

    public static Time32 ofUnixMillis(long l) {
        return new Time32((l - etsiEpochMillis) / 1000L);
    }

    public static Time32 getInstance(Object object) {
        if (object instanceof UINT32) {
            return new Time32((UINT32)object);
        }
        if (object != null) {
            return new Time32(ASN1Integer.getInstance(object).getValue());
        }
        return null;
    }

    public long toUnixMillis() {
        return this.getValue().longValue() * 1000L + etsiEpochMillis;
    }

    public String toString() {
        return new Date(this.toUnixMillis()).toString();
    }
}

