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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.IdentifiedRegion;

public class SequenceOfIdentifiedRegion
extends ASN1Object {
    private final List<IdentifiedRegion> identifiedRegions;

    public SequenceOfIdentifiedRegion(List<IdentifiedRegion> list) {
        this.identifiedRegions = Collections.unmodifiableList(list);
    }

    private SequenceOfIdentifiedRegion(ASN1Sequence aSN1Sequence) {
        ArrayList<IdentifiedRegion> arrayList = new ArrayList<IdentifiedRegion>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(IdentifiedRegion.getInstance(iterator.next()));
        }
        this.identifiedRegions = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfIdentifiedRegion getInstance(Object object) {
        if (object instanceof SequenceOfIdentifiedRegion) {
            return (SequenceOfIdentifiedRegion)object;
        }
        if (object != null) {
            return new SequenceOfIdentifiedRegion(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<IdentifiedRegion> getIdentifiedRegions() {
        return this.identifiedRegions;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.identifiedRegions);
    }
}

