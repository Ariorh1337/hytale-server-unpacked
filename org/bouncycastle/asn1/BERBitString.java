/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DLBitString;

public class BERBitString
extends ASN1BitString {
    private static final int DEFAULT_SEGMENT_LIMIT = 1000;
    private final int segmentLimit;
    private final ASN1BitString[] elements;

    static byte[] flattenBitStrings(ASN1BitString[] aSN1BitStringArray) {
        int n = aSN1BitStringArray.length;
        switch (n) {
            case 0: {
                return new byte[]{0};
            }
            case 1: {
                return aSN1BitStringArray[0].contents;
            }
        }
        int n2 = n - 1;
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            byte[] byArray = aSN1BitStringArray[i].contents;
            if (byArray[0] != 0) {
                throw new IllegalArgumentException("only the last nested bitstring can have padding");
            }
            n3 += byArray.length - 1;
        }
        byte[] byArray = aSN1BitStringArray[n2].contents;
        byte by = byArray[0];
        byte[] byArray2 = new byte[n3 += byArray.length];
        byArray2[0] = by;
        int n4 = 1;
        for (int i = 0; i < n; ++i) {
            byte[] byArray3 = aSN1BitStringArray[i].contents;
            int n5 = byArray3.length - 1;
            System.arraycopy(byArray3, 1, byArray2, n4, n5);
            n4 += n5;
        }
        return byArray2;
    }

    public BERBitString(byte[] byArray) {
        this(byArray, 0);
    }

    public BERBitString(byte by, int n) {
        super(by, n);
        this.elements = null;
        this.segmentLimit = 1000;
    }

    public BERBitString(byte[] byArray, int n) {
        this(byArray, n, 1000);
    }

    public BERBitString(byte[] byArray, int n, int n2) {
        super(byArray, n);
        this.elements = null;
        this.segmentLimit = n2;
    }

    public BERBitString(ASN1Encodable aSN1Encodable) throws IOException {
        this(aSN1Encodable.toASN1Primitive().getEncoded("DER"), 0);
    }

    public BERBitString(ASN1BitString[] aSN1BitStringArray) {
        this(aSN1BitStringArray, 1000);
    }

    public BERBitString(ASN1BitString[] aSN1BitStringArray, int n) {
        super(BERBitString.flattenBitStrings(aSN1BitStringArray), false);
        this.elements = aSN1BitStringArray;
        this.segmentLimit = n;
    }

    BERBitString(byte[] byArray, boolean bl) {
        super(byArray, bl);
        this.elements = null;
        this.segmentLimit = 1000;
    }

    @Override
    boolean encodeConstructed() {
        return null != this.elements || this.contents.length > this.segmentLimit;
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        int n;
        if (!this.encodeConstructed()) {
            return DLBitString.encodedLength(bl, this.contents.length);
        }
        int n2 = n = bl ? 4 : 3;
        if (null != this.elements) {
            for (int i = 0; i < this.elements.length; ++i) {
                n += this.elements[i].encodedLength(true);
            }
        } else if (this.contents.length >= 2) {
            int n3 = (this.contents.length - 2) / (this.segmentLimit - 1);
            n += n3 * DLBitString.encodedLength(true, this.segmentLimit);
            int n4 = this.contents.length - n3 * (this.segmentLimit - 1);
            n += DLBitString.encodedLength(true, n4);
        }
        return n;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        if (!this.encodeConstructed()) {
            DLBitString.encode(aSN1OutputStream, bl, this.contents, 0, this.contents.length);
            return;
        }
        aSN1OutputStream.writeIdentifier(bl, 35);
        aSN1OutputStream.write(128);
        if (null != this.elements) {
            aSN1OutputStream.writePrimitives(this.elements);
        } else if (this.contents.length >= 2) {
            int n;
            byte by = this.contents[0];
            int n2 = this.contents.length;
            int n3 = this.segmentLimit - 1;
            for (n = n2 - 1; n > n3; n -= n3) {
                DLBitString.encode(aSN1OutputStream, true, (byte)0, this.contents, n2 - n, n3);
            }
            DLBitString.encode(aSN1OutputStream, true, by, this.contents, n2 - n, n);
        }
        aSN1OutputStream.write(0);
        aSN1OutputStream.write(0);
    }
}

