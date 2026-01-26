/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1BitStringParser;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DLBitString;
import org.bouncycastle.util.Arrays;

public abstract class ASN1BitString
extends ASN1Primitive
implements ASN1String,
ASN1BitStringParser {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BitString.class, 3){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1BitString.createPrimitive(dEROctetString.getOctets());
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence.toASN1BitString();
        }
    };
    private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    final byte[] contents;

    public static ASN1BitString getInstance(Object object) {
        if (object == null || object instanceof ASN1BitString) {
            return (ASN1BitString)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1BitString) {
                return (ASN1BitString)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1BitString)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct BIT STRING from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1BitString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1BitString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1BitString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1BitString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    protected static int getPadBits(int n) {
        int n2;
        int n3 = 0;
        for (n2 = 3; n2 >= 0; --n2) {
            if (n2 != 0) {
                if (n >> n2 * 8 == 0) continue;
                n3 = n >> n2 * 8 & 0xFF;
                break;
            }
            if (n == 0) continue;
            n3 = n & 0xFF;
            break;
        }
        if (n3 == 0) {
            return 0;
        }
        n2 = 1;
        while (((n3 <<= 1) & 0xFF) != 0) {
            ++n2;
        }
        return 8 - n2;
    }

    protected static byte[] getBytes(int n) {
        if (n == 0) {
            return new byte[0];
        }
        int n2 = 4;
        for (int i = 3; i >= 1 && (n & 255 << i * 8) == 0; --i) {
            --n2;
        }
        byte[] byArray = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            byArray[i] = (byte)(n >> i * 8 & 0xFF);
        }
        return byArray;
    }

    ASN1BitString(byte by, int n) {
        if (n > 7 || n < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.contents = new byte[]{(byte)n, by};
    }

    ASN1BitString(byte[] byArray, int n) {
        if (byArray == null) {
            throw new NullPointerException("'data' cannot be null");
        }
        if (byArray.length == 0 && n != 0) {
            throw new IllegalArgumentException("zero length data with non-zero pad bits");
        }
        if (n > 7 || n < 0) {
            throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
        }
        this.contents = Arrays.prepend(byArray, (byte)n);
    }

    ASN1BitString(byte[] byArray, boolean bl) {
        if (bl) {
            if (null == byArray) {
                throw new NullPointerException("'contents' cannot be null");
            }
            if (byArray.length < 1) {
                throw new IllegalArgumentException("'contents' cannot be empty");
            }
            int n = byArray[0] & 0xFF;
            if (n > 0) {
                if (byArray.length < 2) {
                    throw new IllegalArgumentException("zero length data with non-zero pad bits");
                }
                if (n > 7) {
                    throw new IllegalArgumentException("pad bits cannot be greater than 7 or less than 0");
                }
            }
        }
        this.contents = byArray;
    }

    @Override
    public InputStream getBitStream() throws IOException {
        return new ByteArrayInputStream(this.contents, 1, this.contents.length - 1);
    }

    @Override
    public InputStream getOctetStream() throws IOException {
        int n = this.contents[0] & 0xFF;
        if (0 != n) {
            throw new IOException("expected octet-aligned bitstring, but found padBits: " + n);
        }
        return this.getBitStream();
    }

    public ASN1BitStringParser parser() {
        return this;
    }

    @Override
    public String getString() {
        byte[] byArray;
        try {
            byArray = this.getEncoded();
        }
        catch (IOException iOException) {
            throw new ASN1ParsingException("Internal error encoding BitString: " + iOException.getMessage(), iOException);
        }
        StringBuilder stringBuilder = new StringBuilder(1 + byArray.length * 2);
        stringBuilder.append('#');
        for (int i = 0; i != byArray.length; ++i) {
            byte by = byArray[i];
            stringBuilder.append(table[by >>> 4 & 0xF]);
            stringBuilder.append(table[by & 0xF]);
        }
        return stringBuilder.toString();
    }

    public int intValue() {
        int n;
        int n2 = 0;
        int n3 = Math.min(5, this.contents.length - 1);
        for (n = 1; n < n3; ++n) {
            n2 |= (this.contents[n] & 0xFF) << 8 * (n - 1);
        }
        if (1 <= n3 && n3 < 5) {
            n = this.contents[0] & 0xFF;
            byte by = (byte)(this.contents[n3] & 255 << n);
            n2 |= (by & 0xFF) << 8 * (n3 - 1);
        }
        return n2;
    }

    public byte[] getOctets() {
        if (this.contents[0] != 0) {
            throw new IllegalStateException("attempt to get non-octet aligned data from BIT STRING");
        }
        return Arrays.copyOfRange(this.contents, 1, this.contents.length);
    }

    public byte[] getBytes() {
        if (this.contents.length == 1) {
            return ASN1OctetString.EMPTY_OCTETS;
        }
        int n = this.contents[0] & 0xFF;
        byte[] byArray = Arrays.copyOfRange(this.contents, 1, this.contents.length);
        int n2 = byArray.length - 1;
        byArray[n2] = (byte)(byArray[n2] & (byte)(255 << n));
        return byArray;
    }

    public int getBytesLength() {
        return this.contents.length - 1;
    }

    public boolean isOctetAligned() {
        return this.getPadBits() == 0;
    }

    @Override
    public int getPadBits() {
        return this.contents[0] & 0xFF;
    }

    public String toString() {
        return this.getString();
    }

    @Override
    public int hashCode() {
        if (this.contents.length < 2) {
            return 1;
        }
        int n = this.contents[0] & 0xFF;
        int n2 = this.contents.length - 1;
        byte by = (byte)(this.contents[n2] & 255 << n);
        int n3 = Arrays.hashCode(this.contents, 0, n2);
        n3 *= 257;
        return n3 ^= by;
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        int n;
        if (!(aSN1Primitive instanceof ASN1BitString)) {
            return false;
        }
        ASN1BitString aSN1BitString = (ASN1BitString)aSN1Primitive;
        byte[] byArray = aSN1BitString.contents;
        byte[] byArray2 = this.contents;
        int n2 = byArray2.length;
        if (byArray.length != n2) {
            return false;
        }
        if (n2 == 1) {
            return true;
        }
        int n3 = n2 - 1;
        for (n = 0; n < n3; ++n) {
            if (byArray2[n] == byArray[n]) continue;
            return false;
        }
        n = byArray2[0] & 0xFF;
        byte by = (byte)(byArray2[n3] & 255 << n);
        byte by2 = (byte)(byArray[n3] & 255 << n);
        return by == by2;
    }

    @Override
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DERBitString(this.contents, false);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DLBitString(this.contents, false);
    }

    static ASN1BitString createPrimitive(byte[] byArray) {
        int n = byArray.length;
        if (n < 1) {
            throw new IllegalArgumentException("truncated BIT STRING detected");
        }
        int n2 = byArray[0] & 0xFF;
        if (n2 > 0) {
            if (n2 > 7 || n < 2) {
                throw new IllegalArgumentException("invalid pad bits detected");
            }
            byte by = byArray[n - 1];
            if (by != (byte)(by & 255 << n2)) {
                return new DLBitString(byArray, false);
            }
        }
        return new DERBitString(byArray, false);
    }
}

