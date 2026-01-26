/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.oer.its.ieee1609dot2.basetypes;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.its.ItsUtils;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.EccP384CurvePoint;
import org.bouncycastle.util.Arrays;

public class EcdsaP384Signature
extends ASN1Object {
    private final EccP384CurvePoint rSig;
    private final ASN1OctetString sSig;

    public EcdsaP384Signature(EccP384CurvePoint eccP384CurvePoint, ASN1OctetString aSN1OctetString) {
        this.rSig = eccP384CurvePoint;
        this.sSig = aSN1OctetString;
    }

    private EcdsaP384Signature(ASN1Sequence aSN1Sequence) {
        if (aSN1Sequence.size() != 2) {
            throw new IllegalArgumentException("expected sequence size of 2");
        }
        this.rSig = EccP384CurvePoint.getInstance(aSN1Sequence.getObjectAt(0));
        this.sSig = ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1));
    }

    public static EcdsaP384Signature getInstance(Object object) {
        if (object instanceof EcdsaP384Signature) {
            return (EcdsaP384Signature)object;
        }
        if (object != null) {
            return new EcdsaP384Signature(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public EccP384CurvePoint getRSig() {
        return this.rSig;
    }

    public ASN1OctetString getSSig() {
        return this.sSig;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return ItsUtils.toSequence(this.rSig, this.sSig);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private EccP384CurvePoint rSig;
        private ASN1OctetString sSig;

        public Builder setRSig(EccP384CurvePoint eccP384CurvePoint) {
            this.rSig = eccP384CurvePoint;
            return this;
        }

        public Builder setSSig(ASN1OctetString aSN1OctetString) {
            this.sSig = aSN1OctetString;
            return this;
        }

        public Builder setSSig(byte[] byArray) {
            return this.setSSig(new DEROctetString(Arrays.clone(byArray)));
        }

        public EcdsaP384Signature createEcdsaP384Signature() {
            return new EcdsaP384Signature(this.rSig, this.sSig);
        }
    }
}

