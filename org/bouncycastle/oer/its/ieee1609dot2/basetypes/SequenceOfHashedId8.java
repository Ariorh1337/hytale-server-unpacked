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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class SequenceOfHashedId8
extends ASN1Object {
    private final List<HashedId8> hashedId8s;

    public SequenceOfHashedId8(List<HashedId8> list) {
        this.hashedId8s = Collections.unmodifiableList(list);
    }

    private SequenceOfHashedId8(ASN1Sequence aSN1Sequence) {
        ArrayList<HashedId8> arrayList = new ArrayList<HashedId8>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(HashedId8.getInstance(iterator.next()));
        }
        this.hashedId8s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfHashedId8 getInstance(Object object) {
        if (object instanceof SequenceOfHashedId8) {
            return (SequenceOfHashedId8)object;
        }
        if (object != null) {
            return new SequenceOfHashedId8(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<HashedId8> getHashedId8s() {
        return this.hashedId8s;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.hashedId8s.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<HashedId8> items = new ArrayList<HashedId8>();

        public Builder addHashId8(HashedId8 ... hashedId8Array) {
            this.items.addAll(Arrays.asList(hashedId8Array));
            return this;
        }

        public SequenceOfHashedId8 build() {
            return new SequenceOfHashedId8(this.items);
        }
    }
}

