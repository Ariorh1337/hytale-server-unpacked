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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.RectangularRegion;

public class SequenceOfRectangularRegion
extends ASN1Object {
    private final List<RectangularRegion> rectangularRegions;

    public SequenceOfRectangularRegion(List<RectangularRegion> list) {
        this.rectangularRegions = Collections.unmodifiableList(list);
    }

    private SequenceOfRectangularRegion(ASN1Sequence aSN1Sequence) {
        ArrayList<RectangularRegion> arrayList = new ArrayList<RectangularRegion>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(RectangularRegion.getInstance(iterator.next()));
        }
        this.rectangularRegions = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfRectangularRegion getInstance(Object object) {
        if (object instanceof SequenceOfRectangularRegion) {
            return (SequenceOfRectangularRegion)object;
        }
        if (object != null) {
            return new SequenceOfRectangularRegion(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<RectangularRegion> getRectangularRegions() {
        return this.rectangularRegions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.rectangularRegions);
    }
}

