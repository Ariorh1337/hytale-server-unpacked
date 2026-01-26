/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfUint16;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class RegionAndSubregions
extends ASN1Object
implements RegionInterface {
    private final UINT8 region;
    private final SequenceOfUint16 subregions;

    public RegionAndSubregions(UINT8 uINT8, SequenceOfUint16 sequenceOfUint16) {
        this.region = uINT8;
        this.subregions = sequenceOfUint16;
    }

    private RegionAndSubregions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.region = UINT8.getInstance(aSN1Sequence.getObjectAt(0));
        this.subregions = SequenceOfUint16.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public UINT8 getRegion() {
        return this.region;
    }

    public SequenceOfUint16 getSubregions() {
        return this.subregions;
    }

    public static RegionAndSubregions getInstance(Object object) {
        if (object instanceof RegionAndSubregions) {
            return (RegionAndSubregions)object;
        }
        if (object != null) {
            return new RegionAndSubregions(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.region, this.subregions);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UINT8 region;
        private SequenceOfUint16 subRegions;

        public Builder setRegion(UINT8 uINT8) {
            this.region = uINT8;
            return this;
        }

        public Builder setSubregions(SequenceOfUint16 sequenceOfUint16) {
            this.subRegions = sequenceOfUint16;
            return this;
        }

        public RegionAndSubregions createRegionAndSubregions() {
            return new RegionAndSubregions(this.region, this.subRegions);
        }
    }
}

