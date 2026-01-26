/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificateRca
extends ToBeSignedLinkCertificate {
    public ToBeSignedLinkCertificateRca(Time32 time32, HashedData hashedData) {
        super(time32, hashedData);
    }

    protected ToBeSignedLinkCertificateRca(ASN1Sequence aSN1Sequence) {
        super(aSN1Sequence);
    }

    private ToBeSignedLinkCertificateRca(ToBeSignedLinkCertificate toBeSignedLinkCertificate) {
        super(toBeSignedLinkCertificate.getExpiryTime(), toBeSignedLinkCertificate.getCertificateHash());
    }

    public static ToBeSignedLinkCertificateRca getInstance(Object object) {
        if (object instanceof ToBeSignedLinkCertificateRca) {
            return (ToBeSignedLinkCertificateRca)object;
        }
        if (object instanceof ToBeSignedLinkCertificate) {
            return new ToBeSignedLinkCertificateRca((ToBeSignedLinkCertificate)object);
        }
        if (object != null) {
            return new ToBeSignedLinkCertificateRca(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

