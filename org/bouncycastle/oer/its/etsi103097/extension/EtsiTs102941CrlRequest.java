/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Time32;

public class EtsiTs102941CrlRequest
extends ASN1Object {
    private final HashedId8 issuerId;
    private final Time32 lastKnownUpdate;

    public EtsiTs102941CrlRequest(HashedId8 hashedId8, Time32 time32) {
        this.issuerId = hashedId8;
        this.lastKnownUpdate = time32;
    }

    private EtsiTs102941CrlRequest(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.issuerId = HashedId8.getInstance(aSN1Sequence.getObjectAt(0));
        this.lastKnownUpdate = aSN1Sequence.size() > 1 ? OEROptional.getValue(Time32.class, aSN1Sequence.getObjectAt(1)) : null;
    }

    public static EtsiTs102941CrlRequest getInstance(Object object) {
        if (object instanceof EtsiTs102941CrlRequest) {
            return (EtsiTs102941CrlRequest)object;
        }
        if (object != null) {
            return new EtsiTs102941CrlRequest(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    public HashedId8 getIssuerId() {
        return this.issuerId;
    }

    public Time32 getLastKnownUpdate() {
        return this.lastKnownUpdate;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.issuerId, OEROptional.getInstance(this.lastKnownUpdate)});
    }

    public static class Builder {
        private HashedId8 issuerId;
        private Time32 lastKnownUpdate;

        public Builder setIssuerId(HashedId8 hashedId8) {
            this.issuerId = hashedId8;
            return this;
        }

        public Builder setLastKnownUpdate(Time32 time32) {
            this.lastKnownUpdate = time32;
            return this;
        }

        public EtsiTs102941CrlRequest createEtsiTs102941CrlRequest() {
            return new EtsiTs102941CrlRequest(this.issuerId, this.lastKnownUpdate);
        }
    }
}

