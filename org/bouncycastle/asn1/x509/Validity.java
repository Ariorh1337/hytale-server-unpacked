/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Time;

public class Validity
extends ASN1Object {
    private final Time notBefore;
    private final Time notAfter;

    public static Validity getInstance(Object object) {
        if (object instanceof Validity) {
            return (Validity)object;
        }
        if (object != null) {
            return new Validity(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public static Validity getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return new Validity(ASN1Sequence.getInstance(aSN1TaggedObject, bl));
    }

    private Validity(ASN1Sequence aSN1Sequence) {
        int n = aSN1Sequence.size();
        if (n != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + n);
        }
        this.notBefore = Time.getInstance(aSN1Sequence.getObjectAt(0));
        this.notAfter = Time.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public Validity(Time time, Time time2) {
        if (time == null) {
            throw new NullPointerException("'notBefore' cannot be null");
        }
        if (time2 == null) {
            throw new NullPointerException("'notAfter' cannot be null");
        }
        this.notBefore = time;
        this.notAfter = time2;
    }

    public Time getNotBefore() {
        return this.notBefore;
    }

    public Time getNotAfter() {
        return this.notAfter;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.notBefore, this.notAfter);
    }
}

