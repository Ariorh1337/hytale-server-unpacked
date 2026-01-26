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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;

public class SequenceOfPsidSsp
extends ASN1Object {
    private final List<PsidSsp> psidSsps;

    public SequenceOfPsidSsp(List<PsidSsp> list) {
        this.psidSsps = Collections.unmodifiableList(list);
    }

    private SequenceOfPsidSsp(ASN1Sequence aSN1Sequence) {
        ArrayList<PsidSsp> arrayList = new ArrayList<PsidSsp>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(PsidSsp.getInstance(iterator.next()));
        }
        this.psidSsps = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfPsidSsp getInstance(Object object) {
        if (object instanceof SequenceOfPsidSsp) {
            return (SequenceOfPsidSsp)object;
        }
        if (object != null) {
            return new SequenceOfPsidSsp(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<PsidSsp> getPsidSsps() {
        return this.psidSsps;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psidSsps);
    }

    public static class Builder {
        private List<PsidSsp> items = new ArrayList<PsidSsp>();

        public Builder setItems(List<PsidSsp> list) {
            this.items = list;
            return this;
        }

        public Builder setItem(PsidSsp ... psidSspArray) {
            for (int i = 0; i != psidSspArray.length; ++i) {
                PsidSsp psidSsp = psidSspArray[i];
                this.items.add(psidSsp);
            }
            return this;
        }

        public SequenceOfPsidSsp createSequenceOfPsidSsp() {
            return new SequenceOfPsidSsp(this.items);
        }
    }
}

