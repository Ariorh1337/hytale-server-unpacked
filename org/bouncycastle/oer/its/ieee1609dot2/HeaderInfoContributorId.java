/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;

public class HeaderInfoContributorId
extends ASN1Object {
    private final BigInteger contributorId;
    private static final BigInteger MAX = BigInteger.valueOf(255L);

    public HeaderInfoContributorId(long l) {
        this(BigInteger.valueOf(l));
    }

    public HeaderInfoContributorId(BigInteger bigInteger) {
        if (bigInteger.signum() < 0 && bigInteger.compareTo(MAX) > 0) {
            throw new IllegalArgumentException("contributor id " + bigInteger + " is out of range 0..255");
        }
        this.contributorId = bigInteger;
    }

    private HeaderInfoContributorId(ASN1Integer aSN1Integer) {
        this(aSN1Integer.getValue());
    }

    public static HeaderInfoContributorId getInstance(Object object) {
        if (object instanceof HeaderInfoContributorId) {
            return (HeaderInfoContributorId)object;
        }
        if (object != null) {
            return new HeaderInfoContributorId(ASN1Integer.getInstance(object));
        }
        return null;
    }

    public BigInteger getContributorId() {
        return this.contributorId;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new ASN1Integer(this.contributorId);
    }
}

