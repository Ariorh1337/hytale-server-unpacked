/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Data;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EtsiTs103097DataSignedAndEncryptedUnicast
extends EtsiTs103097Data {
    public EtsiTs103097DataSignedAndEncryptedUnicast(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected EtsiTs103097DataSignedAndEncryptedUnicast(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097DataSignedAndEncryptedUnicast getInstance(Object object) {
        if (object instanceof EtsiTs103097DataSignedAndEncryptedUnicast) {
            return (EtsiTs103097DataSignedAndEncryptedUnicast)object;
        }
        if (object != null) {
            return new EtsiTs103097DataSignedAndEncryptedUnicast(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

