/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetStringParser;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

public abstract class ASN1OctetString
extends ASN1Primitive
implements ASN1OctetStringParser {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1OctetString.class, 4){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return dEROctetString;
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return aSN1Sequence.toASN1OctetString();
        }
    };
    static final byte[] EMPTY_OCTETS = new byte[0];
    byte[] string;

    public static ASN1OctetString getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1OctetString)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1OctetString getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1OctetString)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public static ASN1OctetString getInstance(Object object) {
        if (object == null || object instanceof ASN1OctetString) {
            return (ASN1OctetString)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1OctetString) {
                return (ASN1OctetString)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1OctetString)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct OCTET STRING from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public ASN1OctetString(byte[] byArray) {
        if (byArray == null) {
            throw new NullPointerException("'string' cannot be null");
        }
        this.string = byArray;
    }

    @Override
    public InputStream getOctetStream() {
        return new ByteArrayInputStream(this.string);
    }

    public ASN1OctetStringParser parser() {
        return this;
    }

    public byte[] getOctets() {
        return this.string;
    }

    public int getOctetsLength() {
        return this.getOctets().length;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.getOctets());
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1OctetString)) {
            return false;
        }
        ASN1OctetString aSN1OctetString = (ASN1OctetString)aSN1Primitive;
        return Arrays.areEqual(this.string, aSN1OctetString.string);
    }

    @Override
    public ASN1Primitive getLoadedObject() {
        return this.toASN1Primitive();
    }

    @Override
    ASN1Primitive toDERObject() {
        return new DEROctetString(this.string);
    }

    @Override
    ASN1Primitive toDLObject() {
        return new DEROctetString(this.string);
    }

    public String toString() {
        return "#" + Strings.fromByteArray(Hex.encode(this.string));
    }

    static ASN1OctetString createPrimitive(byte[] byArray) {
        return new DEROctetString(byArray);
    }
}

