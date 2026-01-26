/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROutputStream;

public class DERSet
extends ASN1Set {
    private int contentsLength = -1;

    public static DERSet convert(ASN1Set aSN1Set) {
        return (DERSet)aSN1Set.toDERObject();
    }

    public DERSet() {
    }

    public DERSet(ASN1Encodable aSN1Encodable) {
        super(aSN1Encodable);
    }

    public DERSet(ASN1EncodableVector aSN1EncodableVector) {
        super(aSN1EncodableVector, true);
    }

    public DERSet(ASN1Encodable[] aSN1EncodableArray) {
        super(aSN1EncodableArray, true);
    }

    DERSet(boolean bl, ASN1Encodable[] aSN1EncodableArray) {
        super(DERSet.checkSorted(bl), aSN1EncodableArray);
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
        aSN1OutputStream.writeIdentifier(bl, 49);
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
    ASN1Primitive toDERObject() {
        return this.sortedElements != null ? this : super.toDERObject();
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    private static boolean checkSorted(boolean bl) {
        if (!bl) {
            throw new IllegalStateException("DERSet elements should always be in sorted order");
        }
        return bl;
    }
}

