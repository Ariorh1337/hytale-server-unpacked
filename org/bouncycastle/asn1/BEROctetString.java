/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.DEROctetString;

public class BEROctetString
extends ASN1OctetString {
    private static final int DEFAULT_SEGMENT_LIMIT = 1000;
    private final int segmentLimit;
    private final ASN1OctetString[] elements;

    static byte[] flattenOctetStrings(ASN1OctetString[] aSN1OctetStringArray) {
        int n = aSN1OctetStringArray.length;
        switch (n) {
            case 0: {
                return EMPTY_OCTETS;
            }
            case 1: {
                return aSN1OctetStringArray[0].string;
            }
        }
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            n2 += aSN1OctetStringArray[i].string.length;
        }
        byte[] byArray = new byte[n2];
        int n3 = 0;
        for (int i = 0; i < n; ++i) {
            byte[] byArray2 = aSN1OctetStringArray[i].string;
            System.arraycopy(byArray2, 0, byArray, n3, byArray2.length);
            n3 += byArray2.length;
        }
        return byArray;
    }

    public BEROctetString(byte[] byArray) {
        this(byArray, 1000);
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArray) {
        this(aSN1OctetStringArray, 1000);
    }

    public BEROctetString(byte[] byArray, int n) {
        this(byArray, null, n);
    }

    public BEROctetString(ASN1OctetString[] aSN1OctetStringArray, int n) {
        this(BEROctetString.flattenOctetStrings(aSN1OctetStringArray), aSN1OctetStringArray, n);
    }

    private BEROctetString(byte[] byArray, ASN1OctetString[] aSN1OctetStringArray, int n) {
        super(byArray);
        this.elements = aSN1OctetStringArray;
        this.segmentLimit = n;
    }

    @Override
    boolean encodeConstructed() {
        return true;
    }

    @Override
    int encodedLength(boolean bl) throws IOException {
        int n;
        int n2 = n = bl ? 4 : 3;
        if (null != this.elements) {
            for (int i = 0; i < this.elements.length; ++i) {
                n += this.elements[i].encodedLength(true);
            }
        } else {
            int n3 = this.string.length / this.segmentLimit;
            n += n3 * DEROctetString.encodedLength(true, this.segmentLimit);
            int n4 = this.string.length - n3 * this.segmentLimit;
            if (n4 > 0) {
                n += DEROctetString.encodedLength(true, n4);
            }
        }
        return n;
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeIdentifier(bl, 36);
        aSN1OutputStream.write(128);
        if (null != this.elements) {
            aSN1OutputStream.writePrimitives(this.elements);
        } else {
            int n;
            for (int i = 0; i < this.string.length; i += n) {
                n = Math.min(this.string.length - i, this.segmentLimit);
                DEROctetString.encode(aSN1OutputStream, true, this.string, i, n);
            }
        }
        aSN1OutputStream.write(0);
        aSN1OutputStream.write(0);
    }
}

