/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class DERTaggedObject
extends ASN1TaggedObject {
    public DERTaggedObject(int n, ASN1Encodable aSN1Encodable) {
        super(true, n, aSN1Encodable);
    }

    public DERTaggedObject(int n, int n2, ASN1Encodable aSN1Encodable) {
        super(true, n, n2, aSN1Encodable);
    }

    public DERTaggedObject(boolean bl, int n, ASN1Encodable aSN1Encodable) {
        super(bl, n, aSN1Encodable);
    }

    public DERTaggedObject(boolean bl, int n, int n2, ASN1Encodable aSN1Encodable) {
        super(bl, n, n2, aSN1Encodable);
    }

    DERTaggedObject(int n, int n2, int n3, ASN1Encodable aSN1Encodable) {
        super(n, n2, n3, aSN1Encodable);
    }

    @Override
    boolean encodeConstructed() {
        return this.isExplicit() || this.obj.toASN1Primitive().toDERObject().encodeConstructed();
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
        boolean bl2 = this.isExplicit();
        int n = aSN1Primitive.encodedLength(bl2);
        if (bl2) {
            n += ASN1OutputStream.getLengthOfDL(n);
        }
        return n += bl ? ASN1OutputStream.getLengthOfIdentifier(this.tagNo) : 0;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        ASN1Primitive aSN1Primitive = this.obj.toASN1Primitive().toDERObject();
        boolean bl2 = this.isExplicit();
        if (bl) {
            int n = this.tagClass;
            if (bl2 || aSN1Primitive.encodeConstructed()) {
                n |= 0x20;
            }
            aSN1OutputStream.writeIdentifier(true, n, this.tagNo);
        }
        if (bl2) {
            aSN1OutputStream.writeDL(aSN1Primitive.encodedLength(true));
        }
        aSN1Primitive.encode(aSN1OutputStream.getDERSubStream(), bl2);
    }

    @Override
    ASN1Sequence rebuildConstructed(ASN1Primitive aSN1Primitive) {
        return new DERSequence(aSN1Primitive);
    }

    @Override
    ASN1TaggedObject replaceTag(int n, int n2) {
        return new DERTaggedObject(this.explicitness, n, n2, this.obj);
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

