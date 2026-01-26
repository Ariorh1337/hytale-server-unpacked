/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Data;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EtsiTs103097DataSignedAndEncrypted
extends EtsiTs103097Data {
    public EtsiTs103097DataSignedAndEncrypted(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected EtsiTs103097DataSignedAndEncrypted(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097DataSignedAndEncrypted getInstance(Object object) {
        if (object instanceof EtsiTs103097DataSignedAndEncrypted) {
            return (EtsiTs103097DataSignedAndEncrypted)object;
        }
        if (object != null) {
            return new EtsiTs103097DataSignedAndEncrypted(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

