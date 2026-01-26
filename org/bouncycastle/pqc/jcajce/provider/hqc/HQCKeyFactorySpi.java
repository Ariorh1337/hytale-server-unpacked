/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.hqc;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.hqc.BCHQCPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.BaseKeyFactorySpi;

public class HQCKeyFactorySpi
extends BaseKeyFactorySpi {
    private static final Set<ASN1ObjectIdentifier> keyOids = new HashSet<ASN1ObjectIdentifier>();

    public HQCKeyFactorySpi() {
        super(keyOids);
    }

    public HQCKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(aSN1ObjectIdentifier);
    }

    @Override
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            byte[] byArray = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePrivate(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(byArray)));
            }
            catch (Exception exception) {
                throw new InvalidKeySpecException(exception.toString());
            }
        }
        throw new InvalidKeySpecException("Unsupported key specification: " + keySpec.getClass() + ".");
    }

    @Override
    public PublicKey engineGeneratePublic(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof X509EncodedKeySpec) {
            byte[] byArray = ((X509EncodedKeySpec)keySpec).getEncoded();
            try {
                return this.generatePublic(SubjectPublicKeyInfo.getInstance(byArray));
            }
            catch (Exception exception) {
                throw new InvalidKeySpecException(exception.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + keySpec + ".");
    }

    public final KeySpec engineGetKeySpec(Key key, Class clazz) throws InvalidKeySpecException {
        if (key instanceof BCHQCPrivateKey) {
            if (PKCS8EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new PKCS8EncodedKeySpec(key.getEncoded());
            }
        } else if (key instanceof BCHQCPublicKey) {
            if (X509EncodedKeySpec.class.isAssignableFrom(clazz)) {
                return new X509EncodedKeySpec(key.getEncoded());
            }
        } else {
            throw new InvalidKeySpecException("Unsupported key type: " + key.getClass() + ".");
        }
        throw new InvalidKeySpecException("Unknown key specification: " + clazz + ".");
    }

    @Override
    public final Key engineTranslateKey(Key key) throws InvalidKeyException {
        if (key instanceof BCHQCPrivateKey || key instanceof BCHQCPublicKey) {
            return key;
        }
        throw new InvalidKeyException("Unsupported key type");
    }

    @Override
    public PrivateKey generatePrivate(PrivateKeyInfo privateKeyInfo) throws IOException {
        return new BCHQCPrivateKey(privateKeyInfo);
    }

    @Override
    public PublicKey generatePublic(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return new BCHQCPublicKey(subjectPublicKeyInfo);
    }

    static {
        keyOids.add(BCObjectIdentifiers.hqc128);
        keyOids.add(BCObjectIdentifiers.hqc192);
        keyOids.add(BCObjectIdentifiers.hqc256);
    }

    public static class HQC128
    extends HQCKeyFactorySpi {
        public HQC128() {
            super(BCObjectIdentifiers.hqc128);
        }
    }

    public static class HQC192
    extends HQCKeyFactorySpi {
        public HQC192() {
            super(BCObjectIdentifiers.hqc192);
        }
    }

    public static class HQC256
    extends HQCKeyFactorySpi {
        public HQC256() {
            super(BCObjectIdentifiers.hqc256);
        }
    }
}

