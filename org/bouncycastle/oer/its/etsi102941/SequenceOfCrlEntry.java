/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

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
import org.bouncycastle.oer.its.etsi102941.CrlEntry;

public class SequenceOfCrlEntry
extends ASN1Object {
    private final List<CrlEntry> crlEntries;

    public SequenceOfCrlEntry(List<CrlEntry> list) {
        this.crlEntries = Collections.unmodifiableList(list);
    }

    private SequenceOfCrlEntry(ASN1Sequence aSN1Sequence) {
        ArrayList<CrlEntry> arrayList = new ArrayList<CrlEntry>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(CrlEntry.getInstance(iterator.next()));
        }
        this.crlEntries = Collections.unmodifiableList(arrayList);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SequenceOfCrlEntry getInstance(Object object) {
        if (object instanceof SequenceOfCrlEntry) {
            return (SequenceOfCrlEntry)object;
        }
        if (object != null) {
            return new SequenceOfCrlEntry(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<CrlEntry> getCrlEntries() {
        return this.crlEntries;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.crlEntries.toArray(new ASN1Encodable[0]));
    }

    public static class Builder {
        private final List<CrlEntry> items = new ArrayList<CrlEntry>();

        public Builder addCrlEntry(CrlEntry ... crlEntryArray) {
            this.items.addAll(Arrays.asList(crlEntryArray));
            return this;
        }

        public SequenceOfCrlEntry build() {
            return new SequenceOfCrlEntry(this.items);
        }
    }
}

