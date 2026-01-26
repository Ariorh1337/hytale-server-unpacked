/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1GraphicString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;

public final class ASN1ObjectDescriptor
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectDescriptor.class, 7){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString dEROctetString) {
            return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitPrimitive(dEROctetString));
        }

        @Override
        ASN1Primitive fromImplicitConstructed(ASN1Sequence aSN1Sequence) {
            return new ASN1ObjectDescriptor((ASN1GraphicString)ASN1GraphicString.TYPE.fromImplicitConstructed(aSN1Sequence));
        }
    };
    private final ASN1GraphicString baseGraphicString;

    public static ASN1ObjectDescriptor getInstance(Object object) {
        if (object == null || object instanceof ASN1ObjectDescriptor) {
            return (ASN1ObjectDescriptor)object;
        }
        if (object instanceof ASN1Encodable) {
            ASN1Primitive aSN1Primitive = ((ASN1Encodable)object).toASN1Primitive();
            if (aSN1Primitive instanceof ASN1ObjectDescriptor) {
                return (ASN1ObjectDescriptor)aSN1Primitive;
            }
        } else if (object instanceof byte[]) {
            try {
                return (ASN1ObjectDescriptor)TYPE.fromByteArray((byte[])object);
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("failed to construct object descriptor from byte[]: " + iOException.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + object.getClass().getName());
    }

    public static ASN1ObjectDescriptor getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1ObjectDescriptor)TYPE.getContextTagged(aSN1TaggedObject, bl);
    }

    public static ASN1ObjectDescriptor getTagged(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return (ASN1ObjectDescriptor)TYPE.getTagged(aSN1TaggedObject, bl);
    }

    public ASN1ObjectDescriptor(ASN1GraphicString aSN1GraphicString) {
        if (null == aSN1GraphicString) {
            throw new NullPointerException("'baseGraphicString' cannot be null");
        }
        this.baseGraphicString = aSN1GraphicString;
    }

    public ASN1GraphicString getBaseGraphicString() {
        return this.baseGraphicString;
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean bl) {
        return this.baseGraphicString.encodedLength(bl);
    }

    @Override
    void encode(ASN1OutputStream aSN1OutputStream, boolean bl) throws IOException {
        aSN1OutputStream.writeIdentifier(bl, 7);
        this.baseGraphicString.encode(aSN1OutputStream, false);
    }

    @Override
    ASN1Primitive toDERObject() {
        ASN1GraphicString aSN1GraphicString = (ASN1GraphicString)this.baseGraphicString.toDERObject();
        return aSN1GraphicString == this.baseGraphicString ? this : new ASN1ObjectDescriptor(aSN1GraphicString);
    }

    @Override
    ASN1Primitive toDLObject() {
        ASN1GraphicString aSN1GraphicString = (ASN1GraphicString)this.baseGraphicString.toDLObject();
        return aSN1GraphicString == this.baseGraphicString ? this : new ASN1ObjectDescriptor(aSN1GraphicString);
    }

    @Override
    boolean asn1Equals(ASN1Primitive aSN1Primitive) {
        if (!(aSN1Primitive instanceof ASN1ObjectDescriptor)) {
            return false;
        }
        ASN1ObjectDescriptor aSN1ObjectDescriptor = (ASN1ObjectDescriptor)aSN1Primitive;
        return this.baseGraphicString.asn1Equals(aSN1ObjectDescriptor.baseGraphicString);
    }

    @Override
    public int hashCode() {
        return ~this.baseGraphicString.hashCode();
    }

    static ASN1ObjectDescriptor createPrimitive(byte[] byArray) {
        return new ASN1ObjectDescriptor(ASN1GraphicString.createPrimitive(byArray));
    }
}

