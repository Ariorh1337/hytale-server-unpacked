/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.util.Iterator;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.util.Arrays;

public class BitmapSspRange
extends ASN1Object {
    private final ASN1OctetString sspValue;
    private final ASN1OctetString sspBitMask;

    public BitmapSspRange(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        this.sspValue = aSN1OctetString;
        this.sspBitMask = aSN1OctetString2;
    }

    private BitmapSspRange(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        Iterator<ASN1Encodable> iterator = aSN1Sequence.iterator();
        this.sspValue = ASN1OctetString.getInstance(iterator.next());
        this.sspBitMask = ASN1OctetString.getInstance(iterator.next());
    }

    public static BitmapSspRange getInstance(Object object) {
        if (object instanceof BitmapSspRange) {
            return (BitmapSspRange)object;
        }
        if (object != null) {
            return new BitmapSspRange(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getSspValue() {
        return this.sspValue;
    }

    public ASN1OctetString getSspBitMask() {
        return this.sspBitMask;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.sspValue, this.sspBitMask);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString sspValue;
        private ASN1OctetString sspBitMask;

        public Builder setSspValue(ASN1OctetString aSN1OctetString) {
            this.sspValue = aSN1OctetString;
            return this;
        }

        public Builder setSspBitMask(ASN1OctetString aSN1OctetString) {
            this.sspBitMask = aSN1OctetString;
            return this;
        }

        public Builder setSspValue(byte[] byArray) {
            this.sspValue = new DEROctetString(Arrays.clone(byArray));
            return this;
        }

        public Builder setSspBitMask(byte[] byArray) {
            this.sspBitMask = new DEROctetString(Arrays.clone(byArray));
            return this;
        }

        public BitmapSspRange createBitmapSspRange() {
            return new BitmapSspRange(this.sspValue, this.sspBitMask);
        }
    }
}

