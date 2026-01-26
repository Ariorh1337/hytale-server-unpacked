/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.internal.asn1.misc;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.util.Arrays;

public class CAST5CBCParameters
extends ASN1Object {
    ASN1Integer keyLength;
    ASN1OctetString iv;

    public static CAST5CBCParameters getInstance(Object object) {
        if (object instanceof CAST5CBCParameters) {
            return (CAST5CBCParameters)object;
        }
        if (object != null) {
            return new CAST5CBCParameters(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public CAST5CBCParameters(byte[] byArray, int n) {
        this.iv = new DEROctetString(Arrays.clone(byArray));
        this.keyLength = new ASN1Integer(n);
    }

    private CAST5CBCParameters(ASN1Sequence aSN1Sequence) {
        this.iv = (ASN1OctetString)aSN1Sequence.getObjectAt(0);
        this.keyLength = (ASN1Integer)aSN1Sequence.getObjectAt(1);
    }

    public byte[] getIV() {
        return Arrays.clone(this.iv.getOctets());
    }

    public int getKeyLength() {
        return this.keyLength.intValueExact();
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.iv, this.keyLength);
    }
}

