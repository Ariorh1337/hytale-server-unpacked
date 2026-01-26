/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.CRLSource;
import org.bouncycastle.asn1.x509.Time;

public class CRLStatus
extends ASN1Object {
    private final CRLSource source;
    private final Time thisUpdate;

    private CRLStatus(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() == 1 || aSN1Sequence.size() == 2) {
            this.source = CRLSource.getInstance(aSN1Sequence.getObjectAt(0));
            this.thisUpdate = aSN1Sequence.size() == 2 ? Time.getInstance(aSN1Sequence.getObjectAt(1)) : null;
        } else {
            throw new IllegalArgumentException("expected sequence size of 1 or 2, got " + aSN1Sequence.size());
        }
    }

    public CRLStatus(CRLSource cRLSource, Time time) {
        this.source = cRLSource;
        this.thisUpdate = time;
    }

    public static CRLStatus getInstance(Object object) {
        if (object instanceof CRLStatus) {
            return (CRLStatus)object;
        }
        if (object != null) {
            return new CRLStatus(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CRLSource getSource() {
        return this.source;
    }

    public Time getThisUpdate() {
        return this.thisUpdate;
    }

    public Time getTime() {
        return this.thisUpdate;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(2);
        aSN1EncodableVector.add(this.source);
        if (this.thisUpdate != null) {
            aSN1EncodableVector.add(this.thisUpdate);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

