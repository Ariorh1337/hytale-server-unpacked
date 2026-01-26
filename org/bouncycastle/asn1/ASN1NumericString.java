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
import org.bouncycastle.asn1.DERNumericString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1NumericString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1NumericString.class, 18){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1NumericString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1NumericString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1NumericString) {
            return (ASN1NumericString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1NumericString) {
            return (ASN1NumericString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1NumericString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1NumericString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1NumericString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1NumericString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1NumericString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1NumericString(String string, boolean bl) {
        if (bl && !ASN1NumericString.isNumericString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1NumericString(byte[] byArray, boolean bl) {
        this.contents = bl ? Arrays.clone(byArray) : byArray;
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
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
        aSN1OutputStream.writeEncodingDL(bl, 18, this.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1NumericString)) {
            return false;
        }
        ASN1NumericString aSN1NumericString = (ASN1NumericString)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1NumericString.contents);
    }

    public static boolean isNumericString(String string) {
        for (int i = string.length() - 1; i >= 0; --i) {
            char c = string.charAt(i);
            if (c > '\u007f') {
                return false;
            }
            if ('0' <= c && c <= '9' || c == ' ') continue;
            return false;
        }
        return true;
    }

    static boolean isNumericString(byte[] byArray) {
        block3: for (int i = 0; i < byArray.length; ++i) {
            switch (byArray[i]) {
                case 32: 
                case 48: 
                case 49: 
                case 50: 
                case 51: 
                case 52: 
                case 53: 
                case 54: 
                case 55: 
                case 56: 
                case 57: {
                    continue block3;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }

    static ASN1NumericString createPrimitive(byte[] byArray) {
        return new DERNumericString(byArray, false);
    }
}

