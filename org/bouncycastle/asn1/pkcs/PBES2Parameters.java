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
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public class PBES2Parameters
extends ASN1Object
implements PKCSObjectIdentifiers {
    private KeyDerivationFunc func;
    private EncryptionScheme scheme;

    public static PBES2Parameters getInstance(Object object) {
        if (object instanceof PBES2Parameters) {
            return (PBES2Parameters)object;
        }
        if (object != null) {
            return new PBES2Parameters(ASN1Sequence.getInstance(object));
        }
        return null;
    }

    public PBES2Parameters(KeyDerivationFunc keyDerivationFunc, EncryptionScheme encryptionScheme) {
        this.func = keyDerivationFunc;
        this.scheme = encryptionScheme;
    }

    private PBES2Parameters(ASN1Sequence aSN1Sequence) {
        Enumeration enumeration = aSN1Sequence.getObjects();
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(((ASN1Encodable)enumeration.nextElement()).toASN1Primitive());
        this.func = aSN1Sequence2.getObjectAt(0).equals(id_PBKDF2) ? new KeyDerivationFunc(id_PBKDF2, PBKDF2Params.getInstance(aSN1Sequence2.getObjectAt(1))) : KeyDerivationFunc.getInstance(aSN1Sequence2);
        this.scheme = EncryptionScheme.getInstance(enumeration.nextElement());
    }

    public KeyDerivationFunc getKeyDerivationFunc() {
        return this.func;
    }

    public EncryptionScheme getEncryptionScheme() {
        return this.scheme;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        return new DERSequence(this.func, this.scheme);
    }
}

