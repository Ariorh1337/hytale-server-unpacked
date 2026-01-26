/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.etsi102941;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.etsi102941.Url;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfHashedId8;

public class DcEntry
extends ASN1Object {
    private final Url url;
    private final SequenceOfHashedId8 cert;

    public DcEntry(Url url, SequenceOfHashedId8 sequenceOfHashedId8) {
        this.url = url;
        this.cert = sequenceOfHashedId8;
    }

    private DcEntry(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.url = Url.getInstance(aSN1Sequence.getObjectAt(0));
        this.cert = SequenceOfHashedId8.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static DcEntry getInstance(Object object) {
        if (object instanceof DcEntry) {
            return (DcEntry)object;
        }
        if (object != null) {
            return new DcEntry(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public Url getUrl() {
        return this.url;
    }

    public SequenceOfHashedId8 getCert() {
        return this.cert;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.url, this.cert});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Url url;
        private SequenceOfHashedId8 cert;

        public Builder setUrl(Url url) {
            this.url = url;
            return this;
        }

        public Builder setCert(SequenceOfHashedId8 sequenceOfHashedId8) {
            this.cert = sequenceOfHashedId8;
            return this;
        }

        public DcEntry createDcEntry() {
            return new DcEntry(this.url, this.cert);
        }
    }
}

