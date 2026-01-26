/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;

public class DEROctetString
extends ASN1OctetString {
    public DEROctetString(byte[] byArray) {
        super(byArray);
    }

    public DEROctetString(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable.toASN1Primitive().getEncoded("DER"));
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.string.length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 4, this.string);
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    static void encode(ASN1OutputStream aSN1OutputStream, boolean bl, byte[] byArray, int n, int n2) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 4, byArray, n, n2);
    }

    static int encodedLength(boolean bl, int n) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, n);
    }
}

