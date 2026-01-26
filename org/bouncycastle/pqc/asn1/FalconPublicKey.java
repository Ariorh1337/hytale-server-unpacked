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

public class FalconPublicKey
extends ASN1Object {
    private byte[] h;

    public FalconPublicKey(byte[] byArray) {
        this.h = byArray;
    }

    public byte[] getH() {
        return this.h;
    }

    @Deprecated
    public FalconPublicKey(ASN1Sequence aSN1Sequence) {
        this.h = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new DEROctetString(this.h));
        return new DERSequence(aSN1EncodableVector);
    }

    public static FalconPublicKey getInstance(Object object) {
        if (object instanceof FalconPublicKey) {
            return (FalconPublicKey)object;
        }
        if (object != null) {
            return new FalconPublicKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

