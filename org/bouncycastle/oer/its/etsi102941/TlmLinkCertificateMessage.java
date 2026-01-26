/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataSigned;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;

public class TlmLinkCertificateMessage
extends EtsiTs103097DataSigned {
    public TlmLinkCertificateMessage(Ieee1609Dot2Content ieee1609Dot2Content) {
        super(ieee1609Dot2Content);
    }

    protected TlmLinkCertificateMessage(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    public static TlmLinkCertificateMessage getInstance(Object object) {
        if (object instanceof TlmLinkCertificateMessage) {
            return (TlmLinkCertificateMessage)object;
        }
        if (object != null) {
            return new TlmLinkCertificateMessage(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

