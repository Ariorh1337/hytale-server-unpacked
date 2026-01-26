/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.TwoDLocation;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class CircularRegion
extends ASN1Object
implements RegionInterface {
    private final TwoDLocation center;
    private final UINT16 radius;

    public CircularRegion(TwoDLocation twoDLocation, UINT16 uINT16) {
        this.center = twoDLocation;
        this.radius = uINT16;
    }

    private CircularRegion(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.center = TwoDLocation.getInstance(aSN1Sequence.getObjectAt(0));
        this.radius = UINT16.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CircularRegion getInstance(Object object) {
        if (object instanceof CircularRegion) {
            return (CircularRegion)object;
        }
        if (object != null) {
            return new CircularRegion(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public TwoDLocation getCenter() {
        return this.center;
    }

    public UINT16 getRadius() {
        return this.radius;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.center, this.radius);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private TwoDLocation center;
        private UINT16 radius;

        public Builder setCenter(TwoDLocation twoDLocation) {
            this.center = twoDLocation;
            return this;
        }

        public Builder setRadius(UINT16 uINT16) {
            this.radius = uINT16;
            return this;
        }

        public CircularRegion createCircularRegion() {
            return new CircularRegion(this.center, this.radius);
        }
    }
}

