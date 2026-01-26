/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryOnly;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfRegionAndSubregions;

public class CountryAndSubregions
extends ASN1Object {
    private final CountryOnly country;
    private final SequenceOfRegionAndSubregions regionAndSubregions;

    public CountryAndSubregions(CountryOnly countryOnly, SequenceOfRegionAndSubregions sequenceOfRegionAndSubregions) {
        this.country = countryOnly;
        this.regionAndSubregions = sequenceOfRegionAndSubregions;
    }

    private CountryAndSubregions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.country = CountryOnly.getInstance(aSN1Sequence.getObjectAt(0));
        this.regionAndSubregions = SequenceOfRegionAndSubregions.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static CountryAndSubregions getInstance(Object object) {
        if (object instanceof CountryAndSubregions) {
            return (CountryAndSubregions)object;
        }
        if (object != null) {
            return new CountryAndSubregions(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CountryOnly getCountry() {
        return this.country;
    }

    public SequenceOfRegionAndSubregions getRegionAndSubregions() {
        return this.regionAndSubregions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.country, this.regionAndSubregions});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CountryOnly country;
        private SequenceOfRegionAndSubregions regionAndSubregions;

        public Builder setCountry(CountryOnly countryOnly) {
            this.country = countryOnly;
            return this;
        }

        public Builder setRegionAndSubregions(SequenceOfRegionAndSubregions sequenceOfRegionAndSubregions) {
            this.regionAndSubregions = sequenceOfRegionAndSubregions;
            return this;
        }

        public CountryAndSubregions createCountryAndSubregions() {
            return new CountryAndSubregions(this.country, this.regionAndSubregions);
        }
    }
}

