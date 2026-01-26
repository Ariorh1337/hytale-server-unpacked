/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RegionAndSubregions;

public class SequenceOfRegionAndSubregions
extends ASN1Object {
    private final List<RegionAndSubregions> regionAndSubregions;

    public SequenceOfRegionAndSubregions(List<RegionAndSubregions> list) {
        this.regionAndSubregions = Collections.unmodifiableList(list);
    }

    private SequenceOfRegionAndSubregions(ASN1Sequence aSN1Sequence) {
        ArrayList<RegionAndSubregions> arrayList = new ArrayList<RegionAndSubregions>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(RegionAndSubregions.getInstance(iterator.next()));
        }
        this.regionAndSubregions = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfRegionAndSubregions getInstance(Object object) {
        if (object instanceof SequenceOfRegionAndSubregions) {
            return (SequenceOfRegionAndSubregions)object;
        }
        if (object != null) {
            return new SequenceOfRegionAndSubregions(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<RegionAndSubregions> getRegionAndSubregions() {
        return this.regionAndSubregions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.regionAndSubregions);
    }
}

