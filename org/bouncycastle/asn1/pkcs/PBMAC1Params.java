/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBMAC1Params
extends ASN1Object
implements PKCSObjectIdentifiers {
    private AlgorithmIdentifier func;
    private AlgorithmIdentifier scheme;

    public static PBMAC1Params getInstance(Object object) {
        if (object instanceof PBMAC1Params) {
            return (PBMAC1Params)object;
        }
        if (object != null) {
            return new PBMAC1Params(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public PBMAC1Params(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        this.func = algorithmIdentifier;
        this.scheme = algorithmIdentifier2;
    }

    private PBMAC1Params(ASN1Sequence aSN1Sequence) {
        Enumeration enumeration = aSN1Sequence.getObjects();
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive());
        this.func = aSN1Sequence2.getObjectAt(0).equals(id_PBKDF2) ? new AlgorithmIdentifier(id_PBKDF2, PBKDF2Params.getInstance(aSN1Sequence2.getObjectAt(1))) : AlgorithmIdentifier.getInstance(aSN1Sequence2);
        this.scheme = AlgorithmIdentifier.getInstance(enumeration.nextElement());
    }

    public AlgorithmIdentifier getKeyDerivationFunc() {
        return this.func;
    }

    public AlgorithmIdentifier getMessageAuthScheme() {
        return this.scheme;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.func, this.scheme);
    }
}

