/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.VerificationKeyIndicator;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PublicEncryptionKey;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class ToBeSignedCertificate
extends ASN1Object {
    private final CertificateId id;
    private final HashedId3 cracaId;
    private final CrlSeries crlSeries;
    private final ValidityPeriod validityPeriod;
    private final GeographicRegion region;
    private final SubjectAssurance assuranceLevel;
    private final SequenceOfPsidSsp appPermissions;
    private final SequenceOfPsidGroupPermissions certIssuePermissions;
    private final SequenceOfPsidGroupPermissions certRequestPermissions;
    private final ASN1Null canRequestRollover;
    private final PublicEncryptionKey encryptionKey;
    private final VerificationKeyIndicator verifyKeyIndicator;

    public ToBeSignedCertificate(CertificateId certificateId, HashedId3 hashedId3, CrlSeries crlSeries, ValidityPeriod validityPeriod, GeographicRegion geographicRegion, SubjectAssurance subjectAssurance, SequenceOfPsidSsp sequenceOfPsidSsp, SequenceOfPsidGroupPermissions sequenceOfPsidGroupPermissions, SequenceOfPsidGroupPermissions sequenceOfPsidGroupPermissions2, ASN1Null aSN1Null, PublicEncryptionKey publicEncryptionKey, VerificationKeyIndicator verificationKeyIndicator) {
        this.id = certificateId;
        this.cracaId = hashedId3;
        this.crlSeries = crlSeries;
        this.validityPeriod = validityPeriod;
        this.region = geographicRegion;
        this.assuranceLevel = subjectAssurance;
        this.appPermissions = sequenceOfPsidSsp;
        this.certIssuePermissions = sequenceOfPsidGroupPermissions;
        this.certRequestPermissions = sequenceOfPsidGroupPermissions2;
        this.canRequestRollover = aSN1Null;
        this.encryptionKey = publicEncryptionKey;
        this.verifyKeyIndicator = verificationKeyIndicator;
    }

    private ToBeSignedCertificate(ASN1Sequence aSN1Sequence) {
        Iterator<ASN1Encodable> iterator = ASN1Sequence.getInstance(aSN1Sequence).iterator();
        if (aSN1Sequence.size() != 12) {
            throw new IllegalArgumentException("expected sequence size of 12");
        }
        this.id = CertificateId.getInstance(iterator.next());
        this.cracaId = HashedId3.getInstance(iterator.next());
        this.crlSeries = CrlSeries.getInstance(iterator.next());
        this.validityPeriod = ValidityPeriod.getInstance(iterator.next());
        this.region = OEROptional.getValue(GeographicRegion.class, iterator.next());
        this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, iterator.next());
        this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, iterator.next());
        this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, iterator.next());
        this.certRequestPermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, iterator.next());
        this.canRequestRollover = OEROptional.getValue(ASN1Null.class, iterator.next());
        this.encryptionKey = OEROptional.getValue(PublicEncryptionKey.class, iterator.next());
        this.verifyKeyIndicator = VerificationKeyIndicator.getInstance(iterator.next());
    }

    public static ToBeSignedCertificate getInstance(Object object) {
        if (object instanceof ToBeSignedCertificate) {
            return (ToBeSignedCertificate)object;
        }
        if (object != null) {
            return new ToBeSignedCertificate(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CertificateId getId() {
        return this.id;
    }

    public HashedId3 getCracaId() {
        return this.cracaId;
    }

    public CrlSeries getCrlSeries() {
        return this.crlSeries;
    }

    public ValidityPeriod getValidityPeriod() {
        return this.validityPeriod;
    }

    public GeographicRegion getRegion() {
        return this.region;
    }

    public SubjectAssurance getAssuranceLevel() {
        return this.assuranceLevel;
    }

    public SequenceOfPsidSsp getAppPermissions() {
        return this.appPermissions;
    }

    public SequenceOfPsidGroupPermissions getCertIssuePermissions() {
        return this.certIssuePermissions;
    }

    public SequenceOfPsidGroupPermissions getCertRequestPermissions() {
        return this.certRequestPermissions;
    }

    public ASN1Null getCanRequestRollover() {
        return this.canRequestRollover;
    }

    public PublicEncryptionKey getEncryptionKey() {
        return this.encryptionKey;
    }

    public VerificationKeyIndicator getVerifyKeyIndicator() {
        return this.verifyKeyIndicator;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.id, this.cracaId, this.crlSeries, this.validityPeriod, OEROptional.getInstance(this.region), OEROptional.getInstance(this.assuranceLevel), OEROptional.getInstance(this.appPermissions), OEROptional.getInstance(this.certIssuePermissions), OEROptional.getInstance(this.certRequestPermissions), OEROptional.getInstance(this.canRequestRollover), OEROptional.getInstance(this.encryptionKey), this.verifyKeyIndicator);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private CertificateId id;
        private HashedId3 cracaId;
        private CrlSeries crlSeries;
        private ValidityPeriod validityPeriod;
        private GeographicRegion region;
        private SubjectAssurance assuranceLevel;
        private SequenceOfPsidSsp appPermissions;
        private SequenceOfPsidGroupPermissions certIssuePermissions;
        private SequenceOfPsidGroupPermissions certRequestPermissions;
        private ASN1Null canRequestRollover;
        private PublicEncryptionKey encryptionKey;
        private VerificationKeyIndicator verifyKeyIndicator;

        public Builder() {
        }

        public Builder(Builder builder) {
            this.id = builder.id;
            this.cracaId = builder.cracaId;
            this.crlSeries = builder.crlSeries;
            this.validityPeriod = builder.validityPeriod;
            this.region = builder.region;
            this.assuranceLevel = builder.assuranceLevel;
            this.appPermissions = builder.appPermissions;
            this.certIssuePermissions = builder.certIssuePermissions;
            this.certRequestPermissions = builder.certRequestPermissions;
            this.canRequestRollover = builder.canRequestRollover;
            this.encryptionKey = builder.encryptionKey;
            this.verifyKeyIndicator = builder.verifyKeyIndicator;
        }

        public Builder(ToBeSignedCertificate toBeSignedCertificate) {
            this.id = toBeSignedCertificate.id;
            this.cracaId = toBeSignedCertificate.cracaId;
            this.crlSeries = toBeSignedCertificate.crlSeries;
            this.validityPeriod = toBeSignedCertificate.validityPeriod;
            this.region = toBeSignedCertificate.region;
            this.assuranceLevel = toBeSignedCertificate.assuranceLevel;
            this.appPermissions = toBeSignedCertificate.appPermissions;
            this.certIssuePermissions = toBeSignedCertificate.certIssuePermissions;
            this.certRequestPermissions = toBeSignedCertificate.certRequestPermissions;
            this.canRequestRollover = toBeSignedCertificate.canRequestRollover;
            this.encryptionKey = toBeSignedCertificate.encryptionKey;
            this.verifyKeyIndicator = toBeSignedCertificate.verifyKeyIndicator;
        }

        public Builder setId(CertificateId certificateId) {
            this.id = certificateId;
            return this;
        }

        public Builder setCracaId(HashedId3 hashedId3) {
            this.cracaId = hashedId3;
            return this;
        }

        public Builder setCrlSeries(CrlSeries crlSeries) {
            this.crlSeries = crlSeries;
            return this;
        }

        public Builder setValidityPeriod(ValidityPeriod validityPeriod) {
            this.validityPeriod = validityPeriod;
            return this;
        }

        public Builder setRegion(GeographicRegion geographicRegion) {
            this.region = geographicRegion;
            return this;
        }

        public Builder setAssuranceLevel(SubjectAssurance subjectAssurance) {
            this.assuranceLevel = subjectAssurance;
            return this;
        }

        public Builder setAppPermissions(SequenceOfPsidSsp sequenceOfPsidSsp) {
            this.appPermissions = sequenceOfPsidSsp;
            return this;
        }

        public Builder setCertIssuePermissions(SequenceOfPsidGroupPermissions sequenceOfPsidGroupPermissions) {
            this.certIssuePermissions = sequenceOfPsidGroupPermissions;
            return this;
        }

        public Builder setCertRequestPermissions(SequenceOfPsidGroupPermissions sequenceOfPsidGroupPermissions) {
            this.certRequestPermissions = sequenceOfPsidGroupPermissions;
            return this;
        }

        public Builder setCanRequestRollover() {
            this.canRequestRollover = DERNull.INSTANCE;
            return this;
        }

        public Builder setEncryptionKey(PublicEncryptionKey publicEncryptionKey) {
            this.encryptionKey = publicEncryptionKey;
            return this;
        }

        public Builder setVerifyKeyIndicator(VerificationKeyIndicator verificationKeyIndicator) {
            this.verifyKeyIndicator = verificationKeyIndicator;
            return this;
        }

        public ToBeSignedCertificate createToBeSignedCertificate() {
            return new ToBeSignedCertificate(this.id, this.cracaId, this.crlSeries, this.validityPeriod, this.region, this.assuranceLevel, this.appPermissions, this.certIssuePermissions, this.certRequestPermissions, this.canRequestRollover, this.encryptionKey, this.verifyKeyIndicator);
        }
    }
}

