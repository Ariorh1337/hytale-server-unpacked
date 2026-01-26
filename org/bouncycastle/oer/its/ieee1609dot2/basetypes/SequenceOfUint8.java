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
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class SequenceOfUint8
extends ASN1Object {
    private final List<UINT8> uint8s;

    public SequenceOfUint8(List<UINT8> list) {
        this.uint8s = Collections.unmodifiableList(list);
    }

    private SequenceOfUint8(ASN1Sequence aSN1Sequence) {
        ArrayList<UINT8> arrayList = new ArrayList<UINT8>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(UINT8.getInstance(iterator.next()));
        }
        this.uint8s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfUint8 getInstance(Object object) {
        if (object instanceof SequenceOfUint8) {
            return (SequenceOfUint8)object;
        }
        if (object != null) {
            return new SequenceOfUint8(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<UINT8> getUint8s() {
        return this.uint8s;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (UINT8 uINT8 : this.uint8s) {
            aSN1EncodableVector.add(uINT8.toASN1Primitive());
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static class Builder {
        private final List<UINT8> items = new ArrayList<UINT8>();

        public Builder addHashId3(UINT8 ... uINT8Array) {
            this.items.addAll(Arrays.asList(uINT8Array));
            return this;
        }

        public SequenceOfUint8 build() {
            return new SequenceOfUint8(this.items);
        }
    }
}

