/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncryptedUnicast;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class AuthorizationRequestMessageWithPop
extends EtsiTs103097DataEncryptedUnicast {
    public AuthorizationRequestMessageWithPop(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected AuthorizationRequestMessageWithPop(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static AuthorizationRequestMessageWithPop getInstance(Object object) {
        if (object instanceof AuthorizationRequestMessageWithPop) {
            return (AuthorizationRequestMessageWithPop)object;
        }
        if (object != null) {
            return new AuthorizationRequestMessageWithPop(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

