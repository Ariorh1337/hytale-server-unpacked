/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Data;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class EtsiTs103097DataEncryptedUnicast
extends EtsiTs103097Data {
    public EtsiTs103097DataEncryptedUnicast(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected EtsiTs103097DataEncryptedUnicast(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static EtsiTs103097DataEncryptedUnicast getInstance(Object object) {
        if (object instanceof EtsiTs103097DataEncrypted) {
            return (EtsiTs103097DataEncryptedUnicast)object;
        }
        if (object != null) {
            return new EtsiTs103097DataEncryptedUnicast(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

