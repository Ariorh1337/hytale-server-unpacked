/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

public class SequenceOfOctetString
extends ASN1Object {
    private final List<ASN1OctetString> octetStrings;

    public SequenceOfOctetString(List<ASN1OctetString> list) {
        this.octetStrings = Collections.unmodifiableList(list);
    }

    private SequenceOfOctetString(ASN1Sequence aSN1Sequence) {
        ArrayList<ASN1OctetString> arrayList = new ArrayList<ASN1OctetString>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(DEROctetString.getInstance(iterator.next()));
        }
        this.octetStrings = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfOctetString getInstance(Object object) {
        if (object instanceof SequenceOfOctetString) {
            return (SequenceOfOctetString)object;
        }
        if (object != null) {
            return new SequenceOfOctetString(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public List<ASN1OctetString> getOctetStrings() {
        return this.octetStrings;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.octetStrings.size(); ++i) {
            aSN1EncodableVector.add(this.octetStrings.get(i));
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

