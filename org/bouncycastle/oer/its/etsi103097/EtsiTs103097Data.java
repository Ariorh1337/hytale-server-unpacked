/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class EtsiTs103097Data
extends Ieee1609Dot2Data {
    public EtsiTs103097Data(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(new UINT8(3), ieee1609Dot2Content);
    }

    public EtsiTs103097Data(UINT8 uINT8, Ieee1609Dot2Content ieee1609Dot2Content) {
        super(uINT8, ieee1609Dot2Content);
    }

    protected EtsiTs103097Data(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097Data getInstance(Object object) {
        if (object instanceof EtsiTs103097Data) {
            return (EtsiTs103097Data)object;
        }
        if (object != null) {
            return new EtsiTs103097Data(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

