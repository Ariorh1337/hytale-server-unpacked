/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Latitude;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.Longitude;

public class TwoDLocation
extends ASN1Object {
    private final Latitude latitude;
    private final Longitude longitude;

    public TwoDLocation(Latitude latitude, Longitude longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private TwoDLocation(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.latitude = Latitude.getInstance(aSN1Sequence.getObjectAt(0));
        this.longitude = Longitude.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static TwoDLocation getInstance(Object object) {
        if (object instanceof TwoDLocation) {
            return (TwoDLocation)object;
        }
        if (object != null) {
            return new TwoDLocation(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.latitude, this.longitude});
    }

    public Latitude getLatitude() {
        return this.latitude;
    }

    public Longitude getLongitude() {
        return this.longitude;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Latitude latitude;
        private Longitude longitude;

        public Builder setLatitude(Latitude latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(Longitude longitude) {
            this.longitude = longitude;
            return this;
        }

        public TwoDLocation createTwoDLocation() {
            return new TwoDLocation(this.latitude, this.longitude);
        }
    }
}

