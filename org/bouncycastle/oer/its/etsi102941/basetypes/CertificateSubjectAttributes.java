/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.CertificateId;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.GeographicRegion;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SubjectAssurance;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.ValidityPeriod;

public class CertificateSubjectAttributes
extends ASN1Object {
    private final CertificateId id;
    private final ValidityPeriod validityPeriod;
    private final GeographicRegion region;
    private final SubjectAssurance assuranceLevel;
    private final SequenceOfPsidSsp appPermissions;
    private final SequenceOfPsidGroupPermissions certIssuePermissions;

    public CertificateSubjectAttributes(CertificateId certificateId, ValidityPeriod validityPeriod, GeographicRegion geographicRegion, SubjectAssurance subjectAssurance, SequenceOfPsidSsp sequenceOfPsidSsp, SequenceOfPsidGroupPermissions sequenceOfPsidGroupPermissions) {
        this.id = certificateId;
        this.validityPeriod = validityPeriod;
        this.region = geographicRegion;
        this.assuranceLevel = subjectAssurance;
        this.appPermissions = sequenceOfPsidSsp;
        this.certIssuePermissions = sequenceOfPsidGroupPermissions;
    }

    private CertificateSubjectAttributes(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 6) {
            throw new IllegalArgumentException("expected sequence size of 6");
        }
        this.id = OEROptional.getValue(CertificateId.class, aSN1Sequence.getObjectAt(0));
        this.validityPeriod = OEROptional.getValue(ValidityPeriod.class, aSN1Sequence.getObjectAt(1));
        this.region = OEROptional.getValue(GeographicRegion.class, aSN1Sequence.getObjectAt(2));
        this.assuranceLevel = OEROptional.getValue(SubjectAssurance.class, aSN1Sequence.getObjectAt(3));
        this.appPermissions = OEROptional.getValue(SequenceOfPsidSsp.class, aSN1Sequence.getObjectAt(4));
        this.certIssuePermissions = OEROptional.getValue(SequenceOfPsidGroupPermissions.class, aSN1Sequence.getObjectAt(5));
    }

    public static CertificateSubjectAttributes getInstance(Object object) {
        if (object instanceof CertificateSubjectAttributes) {
            return (CertificateSubjectAttributes)object;
        }
        if (object != null) {
            return new CertificateSubjectAttributes(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CertificateId getId() {
        return this.id;
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

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{OEROptional.getInstance(this.id), OEROptional.getInstance(this.validityPeriod), OEROptional.getInstance(this.region), OEROptional.getInstance(this.assuranceLevel), OEROptional.getInstance(this.appPermissions), OEROptional.getInstance(this.certIssuePermissions)});
    }
}

