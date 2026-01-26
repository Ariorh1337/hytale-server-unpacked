/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.HashedData;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Data;

public class SignedDataPayload
extends ASN1Object {
    private final Ieee1609Dot2Data data;
    private final HashedData extDataHash;

    public SignedDataPayload(Ieee1609Dot2Data ieee1609Dot2Data, HashedData hashedData) {
        this.data = ieee1609Dot2Data;
        this.extDataHash = hashedData;
    }

    private SignedDataPayload(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.data = OEROptional.getValue(Ieee1609Dot2Data.class, aSN1Sequence.getObjectAt(0));
        this.extDataHash = OEROptional.getValue(HashedData.class, aSN1Sequence.getObjectAt(1));
    }

    public static SignedDataPayload getInstance(Object object) {
        if (object instanceof SignedDataPayload) {
            return (SignedDataPayload)object;
        }
        if (object != null) {
            return new SignedDataPayload(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{OEROptional.getInstance(this.data), OEROptional.getInstance(this.extDataHash)});
    }

    public Ieee1609Dot2Data getData() {
        return this.data;
    }

    public HashedData getExtDataHash() {
        return this.extDataHash;
    }

    public static class Builder {
        private Ieee1609Dot2Data data;
        private HashedData extDataHash;

        public Builder setData(Ieee1609Dot2Data ieee1609Dot2Data) {
            this.data = ieee1609Dot2Data;
            return this;
        }

        public Builder setExtDataHash(HashedData hashedData) {
            this.extDataHash = hashedData;
            return this;
        }

        public SignedDataPayload createSignedDataPayload() {
            return new SignedDataPayload(this.data, this.extDataHash);
        }
    }
}

