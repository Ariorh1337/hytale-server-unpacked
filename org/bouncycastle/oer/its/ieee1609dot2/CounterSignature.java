/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CounterSignature
extends Ieee1609Dot2Data {
    public CounterSignature(UINT8 uINT8, Ieee1609Dot2Content ieee1609Dot2Content) {
        super(uINT8, ieee1609Dot2Content);
    }

    protected CounterSignature(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static Ieee1609Dot2Data getInstance(Object object) {
        if (object instanceof Ieee1609Dot2Data) {
            return (Ieee1609Dot2Data)object;
        }
        if (object != null) {
            return new CounterSignature(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

