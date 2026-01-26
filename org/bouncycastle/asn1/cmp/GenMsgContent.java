/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.InfoTypeAndValue;

public class GenMsgContent
extends ASN1Object {
    private final ASN1Sequence content;

    private GenMsgContent(ASN1Sequence aSN1Sequence) {
        this.content = aSN1Sequence;
    }

    public GenMsgContent(InfoTypeAndValue infoTypeAndValue) {
        this.content = new DERSequence(infoTypeAndValue);
    }

    public GenMsgContent(InfoTypeAndValue[] infoTypeAndValueArray) {
        this.content = new DERSequence(infoTypeAndValueArray);
    }

    public static GenMsgContent getInstance(Object object) {
        if (object instanceof GenMsgContent) {
            return (GenMsgContent)object;
        }
        if (object != null) {
            return new GenMsgContent(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public InfoTypeAndValue[] toInfoTypeAndValueArray() {
        InfoTypeAndValue[] infoTypeAndValueArray = new InfoTypeAndValue[this.content.size()];
        for (int i = 0; i != infoTypeAndValueArray.length; ++i) {
            infoTypeAndValueArray[i] = InfoTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return infoTypeAndValueArray;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}

