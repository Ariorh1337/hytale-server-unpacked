/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1External;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BERBitString;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;

public class BERSequence
extends ASN1Sequence {
    public BERSequence() {
    }

    public BERSequence(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
    }

    public BERSequence(ASN1Encodable aSN1Encodable, ASN1Encodable aSN1Encodable2) {
        super(aSN1Encodable, aSN1Encodable2);
    }

    public BERSequence(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector);
    }

    public BERSequence(ASN1Encodable[] aSN1EncodableArray) {
        super(aSN1EncodableArray);
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        int n = bl ? 4 : 3;
        int n2 = this.elements.length;
        for (int i = 0; i < n2; ++i) {
            ASN1Primitive aSN1Primitive = this.elements[i].toASN1Primitive();
            n += aSN1Primitive.encodedLength(true);
        }
        return n;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingIL(bl, 48, this.elements);
    }

    @Override
    ASN1BitString toASN1BitString() {
        return new BERBitString(this.getConstructedBitStrings());
    }

    @Override
    ASN1External toASN1External() {
        return ((ASN1Sequence)this.toDLObject()).toASN1External();
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return new BEROctetString(this.getConstructedOctetStrings());
    }

    @Override
    ASN1Set toASN1Set() {
        return new BERSet(false, this.toArrayInternal());
    }
}

