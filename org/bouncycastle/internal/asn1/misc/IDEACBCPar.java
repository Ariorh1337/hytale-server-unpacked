/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.internal.asn1.misc;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class IDEACBCPar
extends ASN1Object {
    ASN1OctetString iv;

    public static IDEACBCPar getInstance(Object object) {
        if (object instanceof IDEACBCPar) {
            return (IDEACBCPar)object;
        }
        if (object != null) {
            return new IDEACBCPar(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public IDEACBCPar(byte[] byArray) {
        this.iv = new DEROctetString(Arrays.clone(byArray));
    }

    private IDEACBCPar(ASN1Sequence aSN1Sequence) {
        this.iv = aSN1Sequence.size() == 1 ? (ASN1OctetString)aSN1Sequence.getObjectAt(0) : null;
    }

    public byte[] getIV() {
        if (this.iv != null) {
            return Arrays.clone(this.iv.getOctets());
        }
        return null;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(1);
        if (this.iv != null) {
            aSN1EncodableVector.add(this.iv);
        }
        return new DERSequence(aSN1EncodableVector);
    }
}

