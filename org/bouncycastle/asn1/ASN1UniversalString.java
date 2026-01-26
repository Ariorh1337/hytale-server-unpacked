/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERUniversalString;
import org.bouncycastle.util.Arrays;

public abstract class ASN1UniversalString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UniversalString.class, 28){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1UniversalString.createPrimitive(dEROctetString.getOctets());
        }
    };
    private static final char[] table = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    final byte[] contents;

    public static ASN1UniversalString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1UniversalString) {
            return (ASN1UniversalString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1UniversalString) {
            return (ASN1UniversalString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1UniversalString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1UniversalString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UniversalString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1UniversalString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UniversalString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1UniversalString(byte[] byArray, boolean bl) {
        this.contents = bl ? Arrays.clone(byArray) : byArray;
    }

    @Override
    public final String getString() {
        int n = this.contents.length;
        StringBuilder stringBuilder = new StringBuilder(3 + 2 * (ASN1OutputStream.getLengthOfDL(n) + n));
        stringBuilder.append("#1C");
        ASN1UniversalString.encodeHexDL(stringBuilder, n);
        for (int i = 0; i < n; ++i) {
            ASN1UniversalString.encodeHexByte(stringBuilder, this.contents[i]);
        }
        return stringBuilder.toString();
    }

    public String toString() {
        return this.getString();
    }

    public final byte[] getOctets() {
        return Arrays.clone(this.contents);
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    final int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.contents.length);
    }

    @Override
    final void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeEncodingDL(bl, 28, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1UniversalString)) {
            return false;
        }
        ASN1UniversalString aSN1UniversalString = (ASN1UniversalString)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1UniversalString.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1UniversalString createPrimitive(byte[] byArray) {
        return new DERUniversalString(byArray, false);
    }

    private static void encodeHexByte(StringBuilder stringBuilder, int n) {
        stringBuilder.append(table[n >>> 4 & 0xF]);
        stringBuilder.append(table[n & 0xF]);
    }

    private static void encodeHexDL(StringBuilder stringBuilder, int n) {
        if (n < 128) {
            ASN1UniversalString.encodeHexByte(stringBuilder, n);
            return;
        }
        byte[] byArray = new byte[5];
        int n2 = 5;
        do {
            byArray[--n2] = (byte)n;
        } while ((n >>>= 8) != 0);
        int n3 = byArray.length - n2;
        byArray[--n2] = (byte)(0x80 | n3);
        do {
            ASN1UniversalString.encodeHexByte(stringBuilder, byArray[n2++]);
        } while (n2 < byArray.length);
    }
}

