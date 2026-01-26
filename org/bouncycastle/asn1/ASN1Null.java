/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;

public abstract class ASN1Null
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1Null.class, 5){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            ASN1Null.checkContentsLength(dEROctetString.getOctetsLength());
            return ASN1Null.createPrimitive();
        }
    };

    public static ASN1Null getInstance(Object object) {
        if (object instanceof ASN1Null) {
            return (ASN1Null)object;
        }
        if (object != null) {
            try {
                return (ASN1Null)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct NULL from byte[]: " + iOException.getMessage());
            }
        }
        return null;
    }

    public static ASN1Null getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Null)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1Null getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1Null)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    ASN1Null() {
    }

    @Override
    public int hashCode() {
        return -1;
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        return aSN1Primitive instanceof ASN1Null;
    }

    public String toString() {
        return "NULL";
    }

    static void checkContentsLength(int n) {
        if (0 != n) {
            throw new IllegalStateException("malformed NULL encoding encountered");
        }
    }

    static ASN1Null createPrimitive() {
        return DERNull.INSTANCE;
    }
}

