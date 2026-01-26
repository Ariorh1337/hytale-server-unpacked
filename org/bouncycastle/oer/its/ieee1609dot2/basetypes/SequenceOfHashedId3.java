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
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;

public class SequenceOfHashedId3
extends ASN1Object {
    private final List<HashedId3> hashedId3s;

    public SequenceOfHashedId3(List<HashedId3> list) {
        this.hashedId3s = Collections.unmodifiableList(list);
    }

    private SequenceOfHashedId3(ASN1Sequence aSN1Sequence) {
        ArrayList<HashedId3> arrayList = new ArrayList<HashedId3>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(HashedId3.getInstance(iterator.next()));
        }
        this.hashedId3s = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfHashedId3 getInstance(Object object) {
        if (object instanceof SequenceOfHashedId3) {
            return (SequenceOfHashedId3)object;
        }
        if (object != null) {
            return new SequenceOfHashedId3(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<HashedId3> getHashedId3s() {
        return this.hashedId3s;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.hashedId3s.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<HashedId3> items = new ArrayList<HashedId3>();

        public Builder addHashId3(HashedId3 ... hashedId3Array) {
            this.items.addAll(Arrays.asList(hashedId3Array));
            return this;
        }

        public SequenceOfHashedId3 build() {
            return new SequenceOfHashedId3(this.items);
        }
    }
}

