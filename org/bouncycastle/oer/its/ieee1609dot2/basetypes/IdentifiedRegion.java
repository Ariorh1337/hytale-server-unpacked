/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryAndRegions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryAndSubregions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CountryOnly;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;

public class IdentifiedRegion
extends ASN1Object
implements ASN1Choice,
RegionInterface {
    public static final int countryOnly = 0;
    public static final int countryAndRegions = 1;
    public static final int countryAndSubregions = 2;
    private final int choice;
    private final ASN1Encodable identifiedRegion;

    public IdentifiedRegion(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.identifiedRegion = aSN1Encodable;
    }

    private IdentifiedRegion(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.identifiedRegion = CountryOnly.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.identifiedRegion = CountryAndRegions.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.identifiedRegion = CountryAndSubregions.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static IdentifiedRegion countryOnly(CountryOnly countryOnly) {
        return new IdentifiedRegion(0, countryOnly);
    }

    public static IdentifiedRegion countryAndRegions(CountryAndRegions countryAndRegions) {
        return new IdentifiedRegion(1, countryAndRegions);
    }

    public static IdentifiedRegion countryAndSubregions(CountryAndSubregions countryAndSubregions) {
        return new IdentifiedRegion(2, countryAndSubregions);
    }

    public static IdentifiedRegion getInstance(Object object) {
        if (object instanceof IdentifiedRegion) {
            return (IdentifiedRegion)object;
        }
        if (object != null) {
            return new IdentifiedRegion(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getIdentifiedRegion() {
        return this.identifiedRegion;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.identifiedRegion);
    }
}

