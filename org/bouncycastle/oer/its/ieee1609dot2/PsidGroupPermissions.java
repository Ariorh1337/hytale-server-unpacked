/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.EndEntityType;
import org.bouncycastle.oer.its.ieee1609dot2.SubjectPermissions;

public class PsidGroupPermissions
extends ASN1Object {
    private final SubjectPermissions subjectPermissions;
    private final ASN1Integer minChainLength;
    private final ASN1Integer chainLengthRange;
    private final EndEntityType eeType;

    private PsidGroupPermissions(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 4) {
            throw new IllegalArgumentException("expected sequence size of 4");
        }
        this.subjectPermissions = SubjectPermissions.getInstance(aSN1Sequence.getObjectAt(0));
        this.minChainLength = OEROptional.getInstance(aSN1Sequence.getObjectAt(1)).getObject(ASN1Integer.class);
        this.chainLengthRange = OEROptional.getInstance(aSN1Sequence.getObjectAt(2)).getObject(ASN1Integer.class);
        this.eeType = OEROptional.getInstance(aSN1Sequence.getObjectAt(3)).getObject(EndEntityType.class);
    }

    public PsidGroupPermissions(SubjectPermissions subjectPermissions, ASN1Integer aSN1Integer, ASN1Integer aSN1Integer2, EndEntityType endEntityType) {
        this.subjectPermissions = subjectPermissions;
        this.minChainLength = aSN1Integer;
        this.chainLengthRange = aSN1Integer2;
        this.eeType = endEntityType;
    }

    public static PsidGroupPermissions getInstance(Object object) {
        if (object instanceof PsidGroupPermissions) {
            return (PsidGroupPermissions)object;
        }
        if (object != null) {
            return new PsidGroupPermissions(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SubjectPermissions getSubjectPermissions() {
        return this.subjectPermissions;
    }

    public ASN1Integer getMinChainLength() {
        return this.minChainLength;
    }

    public EndEntityType getEeType() {
        return this.eeType;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.subjectPermissions, OEROptional.getInstance(this.minChainLength), OEROptional.getInstance(this.chainLengthRange), OEROptional.getInstance(this.eeType)});
    }

    public ASN1Integer getChainLengthRange() {
        return this.chainLengthRange;
    }

    public static class Builder {
        private SubjectPermissions subjectPermissions;
        private ASN1Integer minChainLength;
        private ASN1Integer chainLengthRange;
        private EndEntityType eeType;

        public Builder setSubjectPermissions(SubjectPermissions subjectPermissions) {
            this.subjectPermissions = subjectPermissions;
            return this;
        }

        public Builder setMinChainLength(BigInteger bigInteger) {
            this.minChainLength = new ASN1Integer(bigInteger);
            return this;
        }

        public Builder setMinChainLength(long l) {
            this.minChainLength = new ASN1Integer(l);
            return this;
        }

        public Builder setChainLengthRange(ASN1Integer aSN1Integer) {
            this.chainLengthRange = aSN1Integer;
            return this;
        }

        public Builder setMinChainLength(ASN1Integer aSN1Integer) {
            this.minChainLength = aSN1Integer;
            return this;
        }

        public Builder setChainLengthRange(BigInteger bigInteger) {
            this.chainLengthRange = new ASN1Integer(bigInteger);
            return this;
        }

        public Builder setChainLengthRange(long l) {
            this.chainLengthRange = new ASN1Integer(l);
            return this;
        }

        public Builder setEeType(EndEntityType endEntityType) {
            this.eeType = endEntityType;
            return this;
        }

        public PsidGroupPermissions createPsidGroupPermissions() {
            return new PsidGroupPermissions(this.subjectPermissions, this.minChainLength, this.chainLengthRange, this.eeType);
        }
    }
}

