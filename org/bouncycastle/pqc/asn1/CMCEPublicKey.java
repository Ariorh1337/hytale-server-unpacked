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

public class CMCEPublicKey
extends ASN1Object {
    private byte[] T;

    public CMCEPublicKey(byte[] byArray) {
        this.T = byArray;
    }

    @Deprecated
    public CMCEPublicKey(ASN1Sequence aSN1Sequence) {
        this.T = Arrays.clone(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(0)).getOctets());
    }

    public byte[] getT() {
        return Arrays.clone(this.T);
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(new DEROctetString(this.T));
        return new DERSequence(aSN1EncodableVector);
    }

    public static CMCEPublicKey getInstance(Object object) {
        if (object instanceof CMCEPublicKey) {
            return (CMCEPublicKey)object;
        }
        if (object != null) {
            return new CMCEPublicKey(ASN1Sequence.getInstance(object));
        }
        return null;
    }
}

