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
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;

public abstract class ASN1BMPString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1BMPString.class, 30){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1BMPString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final char[] string;

    public static ASN1BMPString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1BMPString) {
            return (ASN1BMPString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1BMPString) {
            return (ASN1BMPString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1BMPString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1BMPString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1BMPString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1BMPString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1BMPString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1BMPString(String string) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = string.toCharArray();
    }

    ASN1BMPString(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        int n = byArray.length;
        if (0 != (n & 1)) {
            throw new IllegalArgumentException("malformed BMPString encoding encountered");
        }
        int n2 = n / 2;
        char[] cArray = new char[n2];
        for (int i = 0; i != n2; ++i) {
            cArray[i] = (char)(byArray[2 * i] << 8 | byArray[2 * i + 1] & 0xFF);
        }
        this.string = cArray;
    }

    ASN1BMPString(char[] cArray) {
        if (cArray == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = cArray;
    }

    @Override
    public final String getString() {
        return new String(this.string);
    }

    public String toString() {
        return this.getString();
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1BMPString)) {
            return false;
        }
        ASN1BMPString aSN1BMPString = (ASN1BMPString)aSN1Primitive;
        return Arrays.areEqual(this.string, aSN1BMPString.string);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.string);
    }

    @Override
    final boolean encodeConstructed() {
        return false;
    }

    @Override
    final int encodedLength(boolean bl) {
        return ASN1OutputStream.getLengthOfEncodingDL(bl, this.string.length * 2);
    }

    @Override
    final void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        char c;
        int n;
        int n2;
        int n3 = this.string.length;
        aSN1OutputStream.writeIdentifier(bl, 30);
        aSN1OutputStream.writeDL(n3 * 2);
        byte[] byArray = new byte[8];
        int n4 = n3 & 0xFFFFFFFC;
        for (n2 = 0; n2 < n4; n2 += 4) {
            n = this.string[n2];
            c = this.string[n2 + 1];
            char c2 = this.string[n2 + 2];
            char c3 = this.string[n2 + 3];
            byArray[0] = (byte)(n >> 8);
            byArray[1] = (byte)n;
            byArray[2] = (byte)(c >> 8);
            byArray[3] = (byte)c;
            byArray[4] = (byte)(c2 >> 8);
            byArray[5] = (byte)c2;
            byArray[6] = (byte)(c3 >> 8);
            byArray[7] = (byte)c3;
            aSN1OutputStream.write(byArray, 0, 8);
        }
        if (n2 < n3) {
            n = 0;
            do {
                c = this.string[n2];
                byArray[n++] = (byte)(c >> 8);
                byArray[n++] = (byte)c;
            } while (++n2 < n3);
            aSN1OutputStream.write(byArray, 0, n);
        }
    }

    static ASN1BMPString createPrimitive(byte[] byArray) {
        return new DERBMPString(byArray);
    }

    static ASN1BMPString createPrimitive(char[] cArray) {
        return new DERBMPString(cArray);
    }
}

