/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;

class DLOutputStream
extends ASN1OutputStream {
    DLOutputStream(OutputStream outputStream) {
        super(outputStream);
    }

    @Override
    DLOutputStream getDLSubStream() {
        return this;
    }

    @Override
    void writeElements(ASN1Encodable[] aSN1EncodableArray) throws IOException {
        int n = aSN1EncodableArray.length;
        for (int i = 0; i < n; ++i) {
            aSN1EncodableArray[i].toASN1Primitive().toDLObject().encode(this, true);
        }
    }

    @Override
    void writePrimitive(ASN1Primitive aSN1Primitive, boolean bl) throws IOException {
        aSN1Primitive.toDLObject().encode(this, bl);
    }

    @Override
    void writePrimitives(ASN1Primitive[] aSN1PrimitiveArray) throws IOException {
        int n = aSN1PrimitiveArray.length;
        for (int i = 0; i < n; ++i) {
            aSN1PrimitiveArray[i].toDLObject().encode(this, true);
        }
    }
}

