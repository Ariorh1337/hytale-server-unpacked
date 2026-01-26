/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2dot1;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;
import org.bouncycastle.oer.its.ieee1609dot2dot1.AdditionalParams;

public class EeRaCertRequest
extends ASN1Object {
    private final UINT8 version;
    private final Time32 generationTime;
    private final CertificateType type;
    private final ToBeSignedCertificate tbsCert;
    private final AdditionalParams additionalParams;

    public EeRaCertRequest(UINT8 uINT8, Time32 time32, CertificateType certificateType, ToBeSignedCertificate toBeSignedCertificate, AdditionalParams additionalParams) {
        this.version = uINT8;
        this.generationTime = time32;
        this.type = certificateType;
        this.tbsCert = toBeSignedCertificate;
        this.additionalParams = additionalParams;
    }

    private EeRaCertRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = UINT8.getInstance(aSN1Sequence.getObjectAt(0));
        this.generationTime = Time32.getInstance(aSN1Sequence.getObjectAt(1));
        this.type = CertificateType.getInstance(aSN1Sequence.getObjectAt(2));
        this.tbsCert = ToBeSignedCertificate.getInstance(aSN1Sequence.getObjectAt(3));
        this.additionalParams = OEROptional.getInstance(aSN1Sequence.getObjectAt(4)).getObject(AdditionalParams.class);
    }

    public static EeRaCertRequest getInstance(Object object) {
        if (object instanceof EeRaCertRequest) {
            return (EeRaCertRequest)object;
        }
        if (object != null) {
            return new EeRaCertRequest(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UINT8 getVersion() {
        return this.version;
    }

    public Time32 getGenerationTime() {
        return this.generationTime;
    }

    public CertificateType getType() {
        return this.type;
    }

    public ToBeSignedCertificate getTbsCert() {
        return this.tbsCert;
    }

    public AdditionalParams getAdditionalParams() {
        return this.additionalParams;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.version, this.generationTime, this.type, this.tbsCert, OEROptional.getInstance(this.additionalParams));
    }

    public static class Builder {
        private UINT8 version;
        private Time32 generationTime;
        private CertificateType type;
        private ToBeSignedCertificate tbsCert;
        private AdditionalParams additionalParams;

        public Builder setVersion(UINT8 uINT8) {
            this.version = uINT8;
            return this;
        }

        public Builder setGenerationTime(Time32 time32) {
            this.generationTime = time32;
            return this;
        }

        public Builder setType(CertificateType certificateType) {
            this.type = certificateType;
            return this;
        }

        public Builder setTbsCert(ToBeSignedCertificate toBeSignedCertificate) {
            this.tbsCert = toBeSignedCertificate;
            return this;
        }

        public Builder setAdditionalParams(AdditionalParams additionalParams) {
            this.additionalParams = additionalParams;
            return this;
        }

        public EeRaCertRequest createEeRaCertRequest() {
            return new EeRaCertRequest(this.version, this.generationTime, this.type, this.tbsCert, this.additionalParams);
        }
    }
}

