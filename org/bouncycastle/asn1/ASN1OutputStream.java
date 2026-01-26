/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROutputStream;
import org.bouncycastle.asn1.DLOutputStream;

public class ASN1OutputStream {
    private OutputStream os;

    public static ASN1OutputStream create(OutputStream outputStream) {
        return new ASN1OutputStream(outputStream);
    }

    public static ASN1OutputStream create(OutputStream outputStream, String string) {
        if (string.equals("DER")) {
            return new DEROutputStream(outputStream);
        }
        if (string.equals("DL")) {
            return new DLOutputStream(outputStream);
        }
        return new ASN1OutputStream(outputStream);
    }

    ASN1OutputStream(OutputStream outputStream) {
        this.os = outputStream;
    }

    public void close() throws IOException {
        this.os.close();
    }

    public void flush() throws IOException {
        this.os.flush();
    }

    public final void writeObject(ASN1Encodable aSN1Encodable) throws IOException {
        if (null == aSN1Encodable) {
            throw new IOException("null object detected");
        }
        this.writePrimitive(aSN1Encodable.toASN1Primitive(), true);
        this.flushInternal();
    }

    public final void writeObject(ASN1Primitive aSN1Primitive) throws IOException {
        if (null == aSN1Primitive) {
            throw new IOException("null object detected");
        }
        this.writePrimitive(aSN1Primitive, true);
        this.flushInternal();
    }

    void flushInternal() throws IOException {
    }

    DEROutputStream getDERSubStream() {
        return new DEROutputStream(this.os);
    }

    DLOutputStream getDLSubStream() {
        return new DLOutputStream(this.os);
    }

    final void writeDL(int n) throws IOException {
        if (n < 128) {
            this.write(n);
        } else {
            byte[] byArray = new byte[5];
            int n2 = byArray.length;
            do {
                byArray[--n2] = (byte)n;
            } while ((n >>>= 8) != 0);
            int n3 = byArray.length - n2;
            byArray[--n2] = (byte)(0x80 | n3);
            this.write(byArray, n2, n3 + 1);
        }
    }

    final void write(int n) throws IOException {
        this.os.write(n);
    }

    final void write(byte[] byArray, int n, int n2) throws IOException {
        this.os.write(byArray, n, n2);
    }

    void writeElements(ASN1Encodable[] aSN1EncodableArray) throws IOException {
        int n = aSN1EncodableArray.length;
        for (int i = 0; i < n; ++i) {
            aSN1EncodableArray[i].toASN1Primitive().encode(this, true);
        }
    }

    final void writeEncodingDL(boolean bl, int n, byte by) throws IOException {
        this.writeIdentifier(bl, n);
        this.writeDL(1);
        this.write(by);
    }

    final void writeEncodingDL(boolean bl, int n, byte[] byArray) throws IOException {
        this.writeIdentifier(bl, n);
        this.writeDL(byArray.length);
        this.write(byArray, 0, byArray.length);
    }

    final void writeEncodingDL(boolean bl, int n, byte[] byArray, int n2, int n3) throws IOException {
        this.writeIdentifier(bl, n);
        this.writeDL(n3);
        this.write(byArray, n2, n3);
    }

    final void writeEncodingDL(boolean bl, int n, byte by, byte[] byArray, int n2, int n3) throws IOException {
        this.writeIdentifier(bl, n);
        this.writeDL(1 + n3);
        this.write(by);
        this.write(byArray, n2, n3);
    }

    final void writeEncodingDL(boolean bl, int n, byte[] byArray, int n2, int n3, byte by) throws IOException {
        this.writeIdentifier(bl, n);
        this.writeDL(n3 + 1);
        this.write(byArray, n2, n3);
        this.write(by);
    }

    final void writeEncodingDL(boolean bl, int n, int n2, byte[] byArray) throws IOException {
        this.writeIdentifier(bl, n, n2);
        this.writeDL(byArray.length);
        this.write(byArray, 0, byArray.length);
    }

    final void writeEncodingIL(boolean bl, int n, ASN1Encodable[] aSN1EncodableArray) throws IOException {
        this.writeIdentifier(bl, n);
        this.write(128);
        this.writeElements(aSN1EncodableArray);
        this.write(0);
        this.write(0);
    }

    final void writeIdentifier(boolean bl, int n) throws IOException {
        if (bl) {
            this.write(n);
        }
    }

    final void writeIdentifier(boolean bl, int n, int n2) throws IOException {
        if (bl) {
            if (n2 < 31) {
                this.write(n | n2);
            } else {
                byte[] byArray = new byte[6];
                int n3 = byArray.length;
                byArray[--n3] = (byte)(n2 & 0x7F);
                while (n2 > 127) {
                    byArray[--n3] = (byte)((n2 >>>= 7) & 0x7F | 0x80);
                }
                byArray[--n3] = (byte)(n | 0x1F);
                this.write(byArray, n3, byArray.length - n3);
            }
        }
    }

    void writePrimitive(ASN1Primitive aSN1Primitive, boolean bl) throws IOException {
        aSN1Primitive.encode(this, bl);
    }

    void writePrimitives(ASN1Primitive[] aSN1PrimitiveArray) throws IOException {
        int n = aSN1PrimitiveArray.length;
        for (int i = 0; i < n; ++i) {
            aSN1PrimitiveArray[i].encode(this, true);
        }
    }

    static int getLengthOfDL(int n) {
        if (n < 128) {
            return 1;
        }
        int n2 = 2;
        while ((n >>>= 8) != 0) {
            ++n2;
        }
        return n2;
    }

    static int getLengthOfEncodingDL(boolean bl, int n) {
        return (bl ? 1 : 0) + ASN1OutputStream.getLengthOfDL(n) + n;
    }

    static int getLengthOfIdentifier(int n) {
        if (n < 31) {
            return 1;
        }
        int n2 = 2;
        while ((n >>>= 7) != 0) {
            ++n2;
        }
        return n2;
    }
}

