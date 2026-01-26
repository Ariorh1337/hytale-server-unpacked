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
import org.bouncycastle.asn1.DERT61String;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1T61String
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1T61String.class, 20){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1T61String.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1T61String getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1T61String) {
            return (ASN1T61String)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1T61String) {
            return (ASN1T61String)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1T61String)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1T61String getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1T61String)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1T61String getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1T61String)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1T61String(String string) {
        this.contents = Strings.toByteArray(string);
    }

    ASN1T61String(byte[] byArray, boolean bl) {
        this.contents = bl ? Arrays.clone(byArray) : byArray;
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
    }

    public String toString() {
        return this.getString();
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
        aSN1OutputStream.writeEncodingDL(bl, 20, this.contents);
    }

    public final byte[] getOctets() {
        return Arrays.clone(this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1T61String)) {
            return false;
        }
        ASN1T61String aSN1T61String = (ASN1T61String)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1T61String.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    static ASN1T61String createPrimitive(byte[] byArray) {
        return new DERT61String(byArray, false);
    }
}

