/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmp.PKIMessage;
import org.bouncycastle.asn1.cmp.PKIMessages;

public class NestedMessageContent
extends PKIMessages {
    public NestedMessageContent(PKIMessage pKIMessage) {
        super(pKIMessage);
    }

    public NestedMessageContent(PKIMessage[] pKIMessageArray) {
        super(pKIMessageArray);
    }

    public NestedMessageContent(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static NestedMessageContent getInstance(Object object) {
        if (object instanceof NestedMessageContent) {
            return (NestedMessageContent)object;
        }
        if (object != null) {
            return new NestedMessageContent(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

