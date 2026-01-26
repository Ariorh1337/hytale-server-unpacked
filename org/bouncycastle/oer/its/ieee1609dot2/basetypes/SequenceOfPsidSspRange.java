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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSspRange;

public class SequenceOfPsidSspRange
extends ASN1Object {
    private final List<PsidSspRange> psidSspRanges;

    public SequenceOfPsidSspRange(List<PsidSspRange> list) {
        this.psidSspRanges = Collections.unmodifiableList(list);
    }

    private SequenceOfPsidSspRange(ASN1Sequence aSN1Sequence) {
        ArrayList<PsidSspRange> arrayList = new ArrayList<PsidSspRange>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(PsidSspRange.getInstance(iterator.next()));
        }
        this.psidSspRanges = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfPsidSspRange getInstance(Object object) {
        if (object instanceof SequenceOfPsidSspRange) {
            return (SequenceOfPsidSspRange)object;
        }
        if (object != null) {
            return new SequenceOfPsidSspRange(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<PsidSspRange> getPsidSspRanges() {
        return this.psidSspRanges;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        Iterator<PsidSspRange> iterator = this.psidSspRanges.iterator();
        while (iterator.hasNext()) {
            aSN1EncodableVector.add(iterator.next());
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public static class Builder {
        private final ArrayList<PsidSspRange> psidSspRanges = new ArrayList();

        public Builder add(PsidSspRange ... psidSspRangeArray) {
            this.psidSspRanges.addAll(Arrays.asList(psidSspRangeArray));
            return this;
        }

        public SequenceOfPsidSspRange build() {
            return new SequenceOfPsidSspRange(this.psidSspRanges);
        }
    }
}

