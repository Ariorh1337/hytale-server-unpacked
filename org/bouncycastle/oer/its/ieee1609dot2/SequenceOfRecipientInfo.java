/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

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
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;

public class SequenceOfRecipientInfo
extends ASN1Object {
    private final List<RecipientInfo> recipientInfos;

    public SequenceOfRecipientInfo(List<RecipientInfo> list) {
        this.recipientInfos = Collections.unmodifiableList(list);
    }

    private SequenceOfRecipientInfo(ASN1Sequence aSN1Sequence) {
        ArrayList<RecipientInfo> arrayList = new ArrayList<RecipientInfo>();
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        while (iterator.hasNext()) {
            arrayList.add(RecipientInfo.getInstance(iterator.next()));
        }
        this.recipientInfos = Collections.unmodifiableList(arrayList);
    }

    public static SequenceOfRecipientInfo getInstance(Object object) {
        if (object instanceof SequenceOfRecipientInfo) {
            return (SequenceOfRecipientInfo)object;
        }
        if (object != null) {
            return new SequenceOfRecipientInfo(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (RecipientInfo recipientInfo : this.recipientInfos) {
            aSN1EncodableVector.add(recipientInfo);
        }
        return new DERSequence(aSN1EncodableVector);
    }

    public List<RecipientInfo> getRecipientInfos() {
        return this.recipientInfos;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<RecipientInfo> recipientInfos;

        public Builder setRecipientInfos(List<RecipientInfo> list) {
            this.recipientInfos = list;
            return this;
        }

        public Builder addRecipients(RecipientInfo ... recipientInfoArray) {
            if (this.recipientInfos == null) {
                this.recipientInfos = new ArrayList<RecipientInfo>();
            }
            this.recipientInfos.addAll(Arrays.asList(recipientInfoArray));
            return this;
        }

        public SequenceOfRecipientInfo createSequenceOfRecipientInfo() {
            return new SequenceOfRecipientInfo(this.recipientInfos);
        }
    }
}

