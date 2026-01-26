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
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERExternal;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLSet;

public class DERSequence
extends ASN1Sequence {
    private int contentsLength = -1;

    public static DERSequence convert(ASN1Sequence aSN1Sequence) {
        return (DERSequence)aSN1Sequence.toDERObject();
    }

    public DERSequence() {
    }

    public DERSequence(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
    }

    public DERSequence(ASN1Encodable aSN1Encodable, ASN1Encodable aSN1Encodable2) {
        super(aSN1Encodable, aSN1Encodable2);
    }

    public DERSequence(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector);
    }

    public DERSequence(ASN1Encodable[] aSN1EncodableArray) {
        super(aSN1EncodableArray);
    }

    DERSequence(ASN1Encodable[] aSN1EncodableArray, boolean bl) {
        super(aSN1EncodableArray, bl);
    }

    private int getContentsLength() throws IOException {
        if (this.contentsLength < 0) {
            int n = this.elements.length;
            int n2 = 0;
            for (int i = 0; i < n; ++i) {
                ASN1Primitive aSN1Primitive = this.elements[i].toASN1Primitive().toDERObject();
                n2 += aSN1Primitive.encodedLength(true);
            }
            this.contentsLength = n2;
        }
        return this.contentsLength;
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.getContentsLength());
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeIdentifier(bl, 48);
        DEROutputStream dEROutputStream = aSN1OutputStream.getDERSubStream();
        int n = this.elements.length;
        if (this.contentsLength >= 0 || n > 16) {
            aSN1OutputStream.writeDL(this.getContentsLength());
            for (int i = 0; i < n; ++i) {
                ASN1Primitive aSN1Primitive = this.elements[i].toASN1Primitive().toDERObject();
                aSN1Primitive.encode(dEROutputStream, true);
            }
        } else {
            int n2;
            int n3 = 0;
            ASN1Primitive[] aSN1PrimitiveArray = new ASN1Primitive[n];
            for (n2 = 0; n2 < n; ++n2) {
                ASN1Primitive aSN1Primitive;
                aSN1PrimitiveArray[n2] = aSN1Primitive = this.elements[n2].toASN1Primitive().toDERObject();
                n3 += aSN1Primitive.encodedLength(true);
            }
            this.contentsLength = n3;
            aSN1OutputStream.writeDL(n3);
            for (n2 = 0; n2 < n; ++n2) {
                aSN1PrimitiveArray[n2].encode(dEROutputStream, true);
            }
        }
    }

    @Override
    ASN1BitString toASN1BitString() {
        return new DERBitString(BERBitString.flattenBitStrings(this.getConstructedBitStrings()), false);
    }

    @Override
    ASN1External toASN1External() {
        return new DERExternal(this);
    }

    @Override
    ASN1OctetString toASN1OctetString() {
        return new DEROctetString(BEROctetString.flattenOctetStrings(this.getConstructedOctetStrings()));
    }

    @Override
    ASN1Set toASN1Set() {
        return new DLSet(false, this.toArrayInternal());
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }
}

