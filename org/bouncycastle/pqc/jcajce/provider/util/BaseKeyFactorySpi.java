/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.provider.util;

import java.security.KeyFactorySpi;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Set;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;

public abstract class BaseKeyFactorySpi
extends KeyFactorySpi
implements AsymmetricKeyInfoConverter {
    private final Set<ASN1ObjectIdentifier> keyOids;
    private final ASN1ObjectIdentifier keyOid;

    protected BaseKeyFactorySpi(Set<ASN1ObjectIdentifier> set) {
        this.keyOid = null;
        this.keyOids = set;
    }

    protected BaseKeyFactorySpi(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.keyOid = aSN1ObjectIdentifier;
        this.keyOids = null;
    }

    @Override
    public PrivateKey engineGeneratePrivate(KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            byte[] byArray = ((PKCS8EncodedKeySpec)keySpec).getEncoded();
            try {
                PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(byArray);
                this.checkAlgorithm(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm());
                return this.generatePrivate(privateKeyInfo);
            }
            catch (InvalidKeySpecException invalidKeySpecException) {
                throw invalidKeySpecException;
            }
            catch (IllegalStateException illegalStateException) {
                throw new InvalidKeySpecException(illegalStateException.getMessage());
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
                SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(byArray);
                this.checkAlgorithm(subjectPublicKeyInfo.getAlgorithm().getAlgorithm());
                return this.generatePublic(subjectPublicKeyInfo);
            }
            catch (InvalidKeySpecException invalidKeySpecException) {
                throw invalidKeySpecException;
            }
            catch (Exception exception) {
                throw new InvalidKeySpecException(exception.toString());
            }
        }
        throw new InvalidKeySpecException("Unknown key specification: " + keySpec + ".");
    }

    private void checkAlgorithm(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws InvalidKeySpecException {
        if (this.keyOid != null ? !this.keyOid.equals(aSN1ObjectIdentifier) : !this.keyOids.contains(aSN1ObjectIdentifier)) {
            throw new InvalidKeySpecException("incorrect algorithm OID for key: " + aSN1ObjectIdentifier);
        }
    }
}

