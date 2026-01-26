/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificateRca;
import org.bouncycastle.oer.its.etsi102941.ToBeSignedLinkCertificateTlm;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class ToBeSignedLinkCertificate
extends ASN1Object {
    private final Time32 expiryTime;
    private final HashedData certificateHash;

    public ToBeSignedLinkCertificate(Time32 time32, HashedData hashedData) {
        this.expiryTime = time32;
        this.certificateHash = hashedData;
    }

    protected ToBeSignedLinkCertificate(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.expiryTime = Time32.getInstance(aSN1Sequence.getObjectAt(0));
        this.certificateHash = HashedData.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static ToBeSignedLinkCertificate getInstance(Object object) {
        if (object instanceof ToBeSignedLinkCertificate) {
            return (ToBeSignedLinkCertificate)object;
        }
        if (object != null) {
            return new ToBeSignedLinkCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Time32 getExpiryTime() {
        return this.expiryTime;
    }

    public HashedData getCertificateHash() {
        return this.certificateHash;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.expiryTime, this.certificateHash});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Time32 expiryTime;
        private HashedData certificateHash;

        public Builder setExpiryTime(Time32 time32) {
            this.expiryTime = time32;
            return this;
        }

        public Builder setCertificateHash(HashedData hashedData) {
            this.certificateHash = hashedData;
            return this;
        }

        public ToBeSignedLinkCertificate createToBeSignedLinkCertificate() {
            return new ToBeSignedLinkCertificate(this.expiryTime, this.certificateHash);
        }

        public ToBeSignedLinkCertificateTlm createToBeSignedLinkCertificateTlm() {
            return new ToBeSignedLinkCertificateTlm(this.expiryTime, this.certificateHash);
        }

        public ToBeSignedLinkCertificateRca createToBeSignedLinkCertificateRca() {
            return new ToBeSignedLinkCertificateRca(this.expiryTime, this.certificateHash);
        }
    }
}

