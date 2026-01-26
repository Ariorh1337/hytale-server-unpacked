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
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT16;

public class SequenceOfUint16
extends ASN1Object {
    private final List<UINT16> uint16s;

    public SequenceOfUint16(List<UINT16> list) {
        this.uint16s = Collections.unmodifiableList(list);
    }

    private SequenceOfUint16(ASN1Sequence aSN1Sequence) {
        ArrayList<UINT16> arrayList = new ArrayList<UINT16>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(UINT16.getInstance(iterator.next()));
        }
        this.uint16s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfUint16 getInstance(Object object) {
        if (object instanceof SequenceOfUint16) {
            return (SequenceOfUint16)object;
        }
        if (object != null) {
            return new SequenceOfUint16(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<UINT16> getUint16s() {
        return this.uint16s;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.uint16s.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<UINT16> items = new ArrayList<UINT16>();

        public Builder addHashId3(UINT16 ... uINT16Array) {
            this.items.addAll(Arrays.asList(uINT16Array));
            return this;
        }

        public SequenceOfUint16 build() {
            return new SequenceOfUint16(this.items);
        }
    }
}

