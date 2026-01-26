/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionInterface;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.TwoDLocation;

public class PolygonalRegion
extends ASN1Object
implements RegionInterface {
    private final List<TwoDLocation> twoDLocations;

    public PolygonalRegion(List<TwoDLocation> list) {
        this.twoDLocations = Collections.unmodifiableList(list);
    }

    private PolygonalRegion(ASN1Sequence aSN1Sequence) {
        ArrayList<TwoDLocation> arrayList = new ArrayList<TwoDLocation>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(TwoDLocation.getInstance(iterator.next()));
        }
        this.twoDLocations = Collections.unmodifiableList(arrayList);
    }

    public static PolygonalRegion getInstance(Object object) {
        if (object instanceof PolygonalRegion) {
            return (PolygonalRegion)object;
        }
        if (object != null) {
            return new PolygonalRegion(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<TwoDLocation> getTwoDLocations() {
        return this.twoDLocations;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.twoDLocations);
    }

    public static class Builder {
        private List<TwoDLocation> locations = new ArrayList<TwoDLocation>();

        public Builder setLocations(List<TwoDLocation> list) {
            this.locations = list;
            return this;
        }

        public Builder setLocations(TwoDLocation ... twoDLocationArray) {
            this.locations.addAll(Arrays.asList(twoDLocationArray));
            return this;
        }

        public PolygonalRegion createPolygonalRegion() {
            return new PolygonalRegion(this.locations);
        }
    }
}

