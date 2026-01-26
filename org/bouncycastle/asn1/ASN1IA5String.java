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
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1IA5String
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1IA5String.class, 22){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1IA5String.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1IA5String getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1IA5String) {
            return (ASN1IA5String)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1IA5String) {
            return (ASN1IA5String)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1IA5String)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1IA5String getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1IA5String)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1IA5String getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1IA5String)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1IA5String(String string, boolean bl) {
        if (string == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        if (bl && !ASN1IA5String.isIA5String(string)) {
            throw new IllegalArgumentException("'string' contains illegal characters");
        }
        this.contents = Strings.toByteArray(string);
    }

    ASN1IA5String(byte[] byArray, boolean bl) {
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
        aSN1OutputStream.writeEncodingDL(bl, 22, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1IA5String)) {
            return false;
        }
        ASN1IA5String aSN1IA5String = (ASN1IA5String)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1IA5String.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    public static boolean isIA5String(String string) {
        for (int i = string.length() - 1; i >= 0; --i) {
            char c = string.charAt(i);
            if (c <= '\u007f') continue;
            return false;
        }
        return true;
    }

    static ASN1IA5String createPrimitive(byte[] byArray) {
        return new DERIA5String(byArray, false);
    }
}

