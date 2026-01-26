/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.asn1;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class SABERPublicKey
extends ASN1Object {
    private byte[] seed_A;
    private byte[] b;

    public SABERPublicKey(byte[] byArray, byte[] byArray2) {
        this.seed_A = byArray;
        this.b = byArray2;
    }

    private SABERPublicKey(ASN1Sequence aSN1Sequence) {
        this.seed_A = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
        this.b = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets());
    }

    public byte[] getSeed_A() {
        return this.seed_A;
    }

    public byte[] getB() {
        return this.b;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new DEROctetString(this.seed_A));
        aSN1EncodableVector.add(new DEROctetString(this.b));
        return new DERSequence(aSN1EncodableVector);
    }

    public static SABERPublicKey getInstance(Object object) {
        if (object instanceof SABERPublicKey) {
            return (SABERPublicKey)object;
        }
        if (object != null) {
            return new SABERPublicKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

