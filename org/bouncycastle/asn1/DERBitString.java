/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;

public class DERBitString
extends ASN1BitString {
    public static DERBitString convert(ASN1BitString aSN1BitString) {
        return (DERBitString)aSN1BitString.toDERObject();
    }

    public DERBitString(byte[] byArray) {
        this(byArray, 0);
    }

    public DERBitString(byte by, int n) {
        super(by, n);
    }

    public DERBitString(byte[] byArray, int n) {
        super(byArray, n);
    }

    public DERBitString(int n) {
        super(DERBitString.getBytes(n), DERBitString.getPadBits(n));
    }

    public DERBitString(ASN1Encodable aSN1Encodable) throws IOException {
        super(aSN1Encodable.toASN1Primitive().getEncoded("DER"), 0);
    }

    DERBitString(byte[] byArray, boolean bl) {
        super(byArray, bl);
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.contents.length);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        int n = this.contents.length;
        int n2 = n - 1;
        byte by = this.contents[n2];
        int n3 = this.contents[0] & 0xFF;
        byte by2 = (byte)(this.contents[n2] & 255 << n3);
        if (by == by2) {
            aSN1OutputStream.writeEncodingDL(bl, 3, this.contents);
        } else {
            aSN1OutputStream.writeEncodingDL(bl, 3, this.contents, 0, n2, by2);
        }
    }

    @Override
    ASN1Primitive toDERObject() {
        return this;
    }

    @Override
    ASN1Primitive toDLObject() {
        return this;
    }

    static DERBitString fromOctetString(ASN1OctetString aSN1OctetString) {
        return new DERBitString(aSN1OctetString.getOctets(), true);
    }
}

