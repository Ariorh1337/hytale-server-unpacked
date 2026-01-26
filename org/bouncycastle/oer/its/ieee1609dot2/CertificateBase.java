/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.Certificate;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateType;
import org.bouncycastle.oer.its.ieee1609dot2.ExplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.ImplicitCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.IssuerIdentifier;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Signature;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class CertificateBase
extends ASN1Object {
    private final UINT8 version;
    private final CertificateType type;
    private final IssuerIdentifier issuer;
    private final ToBeSignedCertificate toBeSigned;
    private final Signature signature;

    public CertificateBase(UINT8 uINT8, CertificateType certificateType, IssuerIdentifier issuerIdentifier, ToBeSignedCertificate toBeSignedCertificate, Signature signature) {
        this.version = uINT8;
        this.type = certificateType;
        this.issuer = issuerIdentifier;
        this.toBeSigned = toBeSignedCertificate;
        this.signature = signature;
    }

    protected CertificateBase(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 5) {
            throw new IllegalArgumentException("expected sequence size of 5");
        }
        this.version = UINT8.getInstance(aSN1Sequence.getObjectAt(0));
        this.type = CertificateType.getInstance(aSN1Sequence.getObjectAt(1));
        this.issuer = IssuerIdentifier.getInstance(aSN1Sequence.getObjectAt(2));
        this.toBeSigned = ToBeSignedCertificate.getInstance(aSN1Sequence.getObjectAt(3));
        this.signature = OEROptional.getValue(Signature.class, aSN1Sequence.getObjectAt(4));
    }

    public static CertificateBase getInstance(Object object) {
        if (object instanceof CertificateBase) {
            return (CertificateBase)object;
        }
        if (object != null) {
            return new CertificateBase(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public UINT8 getVersion() {
        return this.version;
    }

    public CertificateType getType() {
        return this.type;
    }

    public IssuerIdentifier getIssuer() {
        return this.issuer;
    }

    public ToBeSignedCertificate getToBeSigned() {
        return this.toBeSigned;
    }

    public Signature getSignature() {
        return this.signature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.version, this.type, this.issuer, this.toBeSigned, OEROptional.getInstance(this.signature));
    }

    public static class Builder {
        private UINT8 version;
        private CertificateType type;
        private IssuerIdentifier issuer;
        private ToBeSignedCertificate toBeSigned;
        private Signature signature;

        public Builder setVersion(UINT8 uINT8) {
            this.version = uINT8;
            return this;
        }

        public Builder setType(CertificateType certificateType) {
            this.type = certificateType;
            return this;
        }

        public Builder setIssuer(IssuerIdentifier issuerIdentifier) {
            this.issuer = issuerIdentifier;
            return this;
        }

        public Builder setToBeSigned(ToBeSignedCertificate toBeSignedCertificate) {
            this.toBeSigned = toBeSignedCertificate;
            return this;
        }

        public Builder setSignature(Signature signature) {
            this.signature = signature;
            return this;
        }

        public Certificate createCertificate() {
            return new Certificate(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
        }

        public ExplicitCertificate createExplicitCertificate() {
            return new ExplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }

        public ImplicitCertificate createImplicitCertificate() {
            return new ImplicitCertificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }

        public CertificateBase createCertificateBase() {
            return new CertificateBase(this.version, this.type, this.issuer, this.toBeSigned, this.signature);
        }

        public CertificateBase createEtsiTs103097Certificate() {
            return new EtsiTs103097Certificate(this.version, this.issuer, this.toBeSigned, this.signature);
        }
    }
}

