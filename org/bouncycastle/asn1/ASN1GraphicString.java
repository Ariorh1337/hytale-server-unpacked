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
import org.bouncycastle.asn1.DERGraphicString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1GraphicString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1GraphicString.class, 25){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1GraphicString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1GraphicString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1GraphicString) {
            return (ASN1GraphicString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1GraphicString) {
            return (ASN1GraphicString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1GraphicString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1GraphicString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1GraphicString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1GraphicString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1GraphicString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1GraphicString(byte[] byArray, boolean bl) {
        if (null == byArray) {
            throw new NullPointerException("'contents' cannot be null");
        }
        this.contents = bl ? Arrays.clone(byArray) : byArray;
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
        aSN1OutputStream.writeEncodingDL(bl, 25, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1GraphicString)) {
            return false;
        }
        ASN1GraphicString aSN1GraphicString = (ASN1GraphicString)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1GraphicString.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1GraphicString createPrimitive(byte[] byArray) {
        return new DERGraphicString(byArray, false);
    }
}

