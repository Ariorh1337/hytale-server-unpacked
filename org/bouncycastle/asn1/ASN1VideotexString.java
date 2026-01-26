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
import org.bouncycastle.asn1.DERVideotexString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

public abstract class ASN1VideotexString
extends ASN1Primitive
implements ASN1String {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1VideotexString.class, 21){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return ASN1VideotexString.createPrimitive(dEROctetString.getOctets());
        }
    };
    final byte[] contents;

    public static ASN1VideotexString getInstance(Object object) {
        ASN1Primitive aSN1Primitive;
        if (object == null || object instanceof ASN1VideotexString) {
            return (ASN1VideotexString)object;
        }
        if (object instanceof ASN1Encodable && (aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive()) instanceof ASN1VideotexString) {
            return (ASN1VideotexString)aSN1Primitive;
        }
        if (object instanceof byte[]) {
            try {
                return (ASN1VideotexString)TYPE.fromByteArray((byte[])object);
            }
            catch (Exception exception) {
                throw new IllegalArgumentException("encoding error in getInstance: " + exception.toString());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1VideotexString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1VideotexString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1VideotexString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1VideotexString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1VideotexString(byte[] byArray, boolean bl) {
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
        aSN1OutputStream.writeEncodingDL(bl, 21, this.contents);
    }

    @Override
    final boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1VideotexString)) {
            return false;
        }
        ASN1VideotexString aSN1VideotexString = (ASN1VideotexString)aSN1Primitive;
        return Arrays.areEqual(this.contents, aSN1VideotexString.contents);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(this.contents);
    }

    @Override
    public final String getString() {
        return Strings.fromByteArray(this.contents);
    }

    static ASN1VideotexString createPrimitive(byte[] byArray) {
        return new DERVideotexString(byArray, false);
    }
}

