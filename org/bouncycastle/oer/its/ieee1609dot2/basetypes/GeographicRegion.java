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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CircularRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PolygonalRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfIdentifiedRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfRectangularRegion;

public class GeographicRegion
extends ASN1Object
implements ASN1Choice {
    public static final int circularRegion = 0;
    public static final int rectangularRegion = 1;
    public static final int polygonalRegion = 2;
    public static final int identifiedRegion = 3;
    private final int choice;
    private final ASN1Encodable geographicRegion;

    public GeographicRegion(int n, ASN1Encodable aSN1Encodable) {
        this.choice = n;
        this.geographicRegion = aSN1Encodable;
    }

    private GeographicRegion(ASN1TaggedObject aSN1TaggedObject) {
        this.choice = aSN1TaggedObject.getTagNo();
        switch (this.choice) {
            case 0: {
                this.geographicRegion = CircularRegion.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 1: {
                this.geographicRegion = SequenceOfRectangularRegion.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 2: {
                this.geographicRegion = PolygonalRegion.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            case 3: {
                this.geographicRegion = SequenceOfIdentifiedRegion.getInstance(aSN1TaggedObject.getExplicitBaseObject());
                break;
            }
            default: {
                throw new IllegalArgumentException("invalid choice value " + this.choice);
            }
        }
    }

    public static GeographicRegion circularRegion(CircularRegion circularRegion) {
        return new GeographicRegion(0, circularRegion);
    }

    public static GeographicRegion rectangularRegion(SequenceOfRectangularRegion sequenceOfRectangularRegion) {
        return new GeographicRegion(1, sequenceOfRectangularRegion);
    }

    public static GeographicRegion polygonalRegion(PolygonalRegion polygonalRegion) {
        return new GeographicRegion(2, polygonalRegion);
    }

    public static GeographicRegion identifiedRegion(SequenceOfIdentifiedRegion sequenceOfIdentifiedRegion) {
        return new GeographicRegion(3, sequenceOfIdentifiedRegion);
    }

    public static GeographicRegion getInstance(Object object) {
        if (object instanceof GeographicRegion) {
            return (GeographicRegion)object;
        }
        if (object != null) {
            return new GeographicRegion(ASN1TaggedObject.getInstance(object, 128));
        }
        return null;
    }

    public int getChoice() {
        return this.choice;
    }

    public ASN1Encodable getGeographicRegion() {
        return this.geographicRegion;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERTaggedObject(this.choice, this.geographicRegion);
    }
}

