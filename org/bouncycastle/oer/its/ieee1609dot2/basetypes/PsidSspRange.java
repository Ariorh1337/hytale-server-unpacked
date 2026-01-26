/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.OEROptional;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Psid;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SspRange;

public class PsidSspRange
extends ASN1Object {
    private final Psid psid;
    private final SspRange sspRange;

    public PsidSspRange(Psid psid, SspRange sspRange) {
        this.psid = psid;
        this.sspRange = sspRange;
    }

    private PsidSspRange(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.psid = Psid.getInstance(aSN1Sequence.getObjectAt(0));
        this.sspRange = OEROptional.getValue(SspRange.class, aSN1Sequence.getObjectAt(1));
    }

    public static PsidSspRange getInstance(Object object) {
        if (object instanceof PsidSspRange) {
            return (PsidSspRange)object;
        }
        if (object != null) {
            return new PsidSspRange(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Psid getPsid() {
        return this.psid;
    }

    public SspRange getSspRange() {
        return this.sspRange;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.psid, OEROptional.getInstance(this.sspRange)});
    }

    public static class Builder {
        private Psid psid;
        private SspRange sspRange;

        public Builder setPsid(Psid psid) {
            this.psid = psid;
            return this;
        }

        public Builder setPsid(long l) {
            this.psid = new Psid(l);
            return this;
        }

        public Builder setSspRange(SspRange sspRange) {
            this.sspRange = sspRange;
            return this;
        }

        public PsidSspRange createPsidSspRange() {
            return new PsidSspRange(this.psid, this.sspRange);
        }
    }
}

