/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.BigIntegers;

public class Point256
extends ASN1Object {
    private final ASN1OctetString x;
    private final ASN1OctetString y;

    public Point256(ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        if (aSN1OctetString == null || aSN1OctetString.getOctets().length != 32) {
            throw new IllegalArgumentException("x must be 32 bytes long");
        }
        if (aSN1OctetString2 == null || aSN1OctetString2.getOctets().length != 32) {
            throw new IllegalArgumentException("y must be 32 bytes long");
        }
        this.x = aSN1OctetString;
        this.y = aSN1OctetString2;
    }

    private Point256(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.x = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0));
        this.y = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        if (this.x.getOctets().length != 32) {
            throw new IllegalArgumentException("x must be 32 bytes long");
        }
        if (this.y.getOctets().length != 32) {
            throw new IllegalArgumentException("y must be 32 bytes long");
        }
    }

    public static Point256 getInstance(Object object) {
        if (object instanceof Point256) {
            return (Point256)object;
        }
        if (object != null) {
            return new Point256(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public ASN1OctetString getX() {
        return this.x;
    }

    public ASN1OctetString getY() {
        return this.y;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.x, this.y});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ASN1OctetString x;
        private ASN1OctetString y;

        public Builder setX(ASN1OctetString aSN1OctetString) {
            this.x = aSN1OctetString;
            return this;
        }

        public Builder setY(ASN1OctetString aSN1OctetString) {
            this.y = aSN1OctetString;
            return this;
        }

        public Builder setX(byte[] byArray) {
            this.x = new DEROctetString(byArray);
            return this;
        }

        public Builder setY(byte[] byArray) {
            this.y = new DEROctetString(byArray);
            return this;
        }

        public Builder setX(BigInteger bigInteger) {
            return this.setX(BigIntegers.asUnsignedByteArray(32, bigInteger));
        }

        public Builder setY(BigInteger bigInteger) {
            return this.setY(BigIntegers.asUnsignedByteArray(32, bigInteger));
        }

        public Point256 createPoint256() {
            return new Point256(this.x, this.y);
        }
    }
}

