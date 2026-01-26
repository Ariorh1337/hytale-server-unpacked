/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;

public class AltSignatureAlgorithm
extends ASN1Object {
    private final AlgorithmIdentifier algorithm;

    public static AltSignatureAlgorithm getInstance(ASN1TaggedObject aSN1TaggedObject, boolean bl) {
        return AltSignatureAlgorithm.getInstance(AlgorithmIdentifier.getInstance(aSN1TaggedObject, bl));
    }

    public static AltSignatureAlgorithm getInstance(Object object) {
        if (object instanceof AltSignatureAlgorithm) {
            return (AltSignatureAlgorithm)object;
        }
        if (object != null) {
            return new AltSignatureAlgorithm(AlgorithmIdentifier.getInstance(object));
        }
        return null;
    }

    public static AltSignatureAlgorithm fromExtensions(Extensions extensions) {
        return AltSignatureAlgorithm.getInstance(Extensions.getExtensionParsedValue(extensions, Extension.altSignatureAlgorithm));
    }

    public AltSignatureAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
        this.algorithm = algorithmIdentifier;
    }

    public AltSignatureAlgorithm(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this(aSN1ObjectIdentifier, null);
    }

    public AltSignatureAlgorithm(ASN1ObjectIdentifier aSN1ObjectIdentifier, ASN1Encodable aSN1Encodable) {
        this.algorithm = new AlgorithmIdentifier(aSN1ObjectIdentifier, aSN1Encodable);
    }

    public AlgorithmIdentifier getAlgorithm() {
        return this.algorithm;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.algorithm.toASN1Primitive();
    }
}

