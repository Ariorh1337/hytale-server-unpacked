/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class AltSignatureValue
extends ASN1Object {
    private final ASN1BitString signature;

    public static AltSignatureValue getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return AltSignatureValue.getInstance(ASN1BitString.getInstance(aSN1TaggedObject, bl));
    }

    public static AltSignatureValue getInstance(Object object) {
        if (object instanceof AltSignatureValue) {
            return (AltSignatureValue)object;
        }
        if (object != null) {
            return new AltSignatureValue(ASN1BitString.getInstance(object));
        }
        return null;
    }

    public static AltSignatureValue fromExtensions(Extensions extensions) {
        return AltSignatureValue.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.altSignatureValue));
    }

    private AltSignatureValue(ASN1BitString aSN1BitString) {
        this.signature = aSN1BitString;
    }

    public AltSignatureValue(byte[] byArray) {
        this.signature = new DERBitString(byArray);
    }

    public ASN1BitString getSignature() {
        return this.signature;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.signature;
    }
}

