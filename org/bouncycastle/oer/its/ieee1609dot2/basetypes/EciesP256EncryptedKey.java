/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP256CurvePoint;
import org.bouncycastle.util.Arrays;

public class EciesP256EncryptedKey
extends ASN1Object {
    private final EccP256CurvePoint v;
    private final ASN1OctetString c;
    private final ASN1OctetString t;

    public EciesP256EncryptedKey(EccP256CurvePoint eccP256CurvePoint, ASN1OctetString aSN1OctetString, ASN1OctetString aSN1OctetString2) {
        this.v = eccP256CurvePoint;
        this.c = aSN1OctetString;
        this.t = aSN1OctetString2;
    }

    public static EciesP256EncryptedKey getInstance(Object object) {
        if (object instanceof EciesP256EncryptedKey) {
            return (EciesP256EncryptedKey)object;
        }
        if (object != null) {
            return new EciesP256EncryptedKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    private EciesP256EncryptedKey(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 3) {
            throw new IllegalArgumentException("expected sequence size of 3");
        }
        this.v = EccP256CurvePoint.getInstance(aSN1Sequence.getObjectAt(0));
        this.c = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
        this.t = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(2));
    }

    public EccP256CurvePoint getV() {
        return this.v;
    }

    public ASN1OctetString getC() {
        return this.c;
    }

    public ASN1OctetString getT() {
        return this.t;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(new ASN1Encodable[]{this.v, this.c, this.t});
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EccP256CurvePoint v;
        private ASN1OctetString c;
        private ASN1OctetString t;

        public Builder setV(EccP256CurvePoint eccP256CurvePoint) {
            this.v = eccP256CurvePoint;
            return this;
        }

        public Builder setC(ASN1OctetString aSN1OctetString) {
            this.c = aSN1OctetString;
            return this;
        }

        public Builder setC(byte[] byArray) {
            this.c = new DEROctetString(Arrays.clone(byArray));
            return this;
        }

        public Builder setT(ASN1OctetString aSN1OctetString) {
            this.t = aSN1OctetString;
            return this;
        }

        public Builder setT(byte[] byArray) {
            this.t = new DEROctetString(Arrays.clone(byArray));
            return this;
        }

        public EciesP256EncryptedKey createEciesP256EncryptedKey() {
            return new EciesP256EncryptedKey(this.v, this.c, this.t);
        }
    }
}

