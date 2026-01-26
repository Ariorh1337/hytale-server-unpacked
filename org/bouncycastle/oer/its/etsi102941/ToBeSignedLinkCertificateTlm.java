/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificateTlm
extends ToBeSignedLinkCertificate {
    public ToBeSignedLinkCertificateTlm(Time32 time32, HashedData hashedData) {
        super(time32, hashedData);
    }

    protected ToBeSignedLinkCertificateTlm(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    private ToBeSignedLinkCertificateTlm(ToBeSignedLinkCertificate toBeSignedLinkCertificate) {
        super(toBeSignedLinkCertificate.getExpiryTime(), toBeSignedLinkCertificate.getCertificateHash());
    }

    public static ToBeSignedLinkCertificateTlm getInstance(Object object) {
        if (object instanceof ToBeSignedLinkCertificateTlm) {
            return (ToBeSignedLinkCertificateTlm)object;
        }
        if (object instanceof ToBeSignedLinkCertificate) {
            return new ToBeSignedLinkCertificateTlm((ToBeSignedLinkCertificate)object);
        }
        if (object != null) {
            return new ToBeSignedLinkCertificateTlm(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

