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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;

public class SequenceOfPsid
extends ASN1Object {
    private final List<Psid> psids;

    public SequenceOfPsid(List<Psid> list) {
        this.psids = Collections.unmodifiableList(list);
    }

    private SequenceOfPsid(ASN1Sequence aSN1Sequence) {
        ArrayList<Psid> arrayList = new ArrayList<Psid>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(Psid.getInstance(iterator.next()));
        }
        this.psids = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfPsid getInstance(Object object) {
        if (object instanceof SequenceOfPsid) {
            return (SequenceOfPsid)object;
        }
        if (object != null) {
            return new SequenceOfPsid(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Psid> getPsids() {
        return this.psids;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.psids);
    }

    public static class Builder {
        private List<Psid> items = new ArrayList<Psid>();

        public Builder setItems(List<Psid> list) {
            this.items = list;
            return this;
        }

        public Builder setItem(Psid ... psidArray) {
            for (int i = 0; i != psidArray.length; ++i) {
                Psid psid = psidArray[i];
                this.items.add(psid);
            }
            return this;
        }

        public SequenceOfPsid createSequenceOfPsidSsp() {
            return new SequenceOfPsid(this.items);
        }
    }
}

