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
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1UTF8String
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1UTF8String.class, 12){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1UTF8String.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1UTF8String getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1UTF8String) {
            return (ASN1UTF8String)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1UTF8String) {
            return (ASN1UTF8String)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1UTF8String)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1UTF8String getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UTF8String)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1UTF8String getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1UTF8String)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1UTF8String(String string) {
        this(Strings.toUTF8ByteArray(string), false);
    }

    ASN1UTF8String(byte[] byArray, boolean bl) {
        this.contents = bl ? Arrays.clone(byArray) : byArray;
    }

    @Override
    public final String getString() {
        return Strings.fromUTF8ByteArray(this.contents);
    }

    public String toString() {
        return this.getString();
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1UTF8String)) {
            return false;
        }
        ASN1UTF8String aSN1UTF8String = (ASN1UTF8String)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1UTF8String.contents);
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
        aSN1OutputStream.writeEncodingDL(bl, 12, this.contents);
    }

    static ASN1UTF8String createPrimitive(byte[] byArray) {
        return new DERUTF8String(byArray, false);
    }
}

