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
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1PrintableString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1PrintableString.class, 19){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1PrintableString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1PrintableString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1PrintableString) {
            return (ASN1PrintableString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1PrintableString) {
            return (ASN1PrintableString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1PrintableString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1PrintableString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1PrintableString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1PrintableString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1PrintableString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1PrintableString(String string, boolean bl) {
        if (bl && !ASN1PrintableString.isPrintableString(string)) {
            throw new IllegalArgumentException("string contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1PrintableString(byte[] byArray, boolean bl) {
        this.contents = bl ? Arrays.clone(byArray) : byArray;
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
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
        aSN1OutputStream.writeEncodingDL(bl, 19, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1PrintableString)) {
            return false;
        }
        ASN1PrintableString aSN1PrintableString = (ASN1PrintableString)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1PrintableString.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public String toString() {
        return this.getString();
    }

    public static boolean isPrintableString(String string) {
        block3: for (int i = string.length() - 1; i >= 0; --i) {
            char c = string.charAt(i);
            if (c > '\u007f') {
                return false;
            }
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z' || '0' <= c && c <= '9') continue;
            switch (c) {
                case ' ': 
                case '\'': 
                case '(': 
                case ')': 
                case '+': 
                case ',': 
                case '-': 
                case '.': 
                case '/': 
                case ':': 
                case '=': 
                case '?': {
                    continue block3;
                }
                default: {
                    return false;
                }
            }
        }
        return true;
    }

    static ASN1PrintableString createPrimitive(byte[] byArray) {
        return new DERPrintableString(byArray, false);
    }
}

