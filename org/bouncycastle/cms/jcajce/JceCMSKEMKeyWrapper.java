/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.KEMKeyWrapper;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.DefaultKemEncapsulationLengthProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.KemEncapsulationLengthProvider;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyWrapper
extends KEMKeyWrapper {
    private final KemEncapsulationLengthProvider kemEncLenProvider = new DefaultKemEncapsulationLengthProvider();
    private final AlgorithmIdentifier symWrapAlgorithm;
    private final int kekLength;
    private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
    private Map extraMappings = new HashMap();
    private PublicKey publicKey;
    private SecureRandom random;
    private AlgorithmIdentifier kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
    private byte[] encapsulation;

    public JceCMSKEMKeyWrapper(PublicKey publicKey, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(publicKey instanceof RSAPublicKey ? new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa) : SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm());
        this.publicKey = publicKey;
        this.symWrapAlgorithm = new AlgorithmIdentifier(aSN1ObjectIdentifier);
        this.kekLength = CMSUtils.getKekSize(aSN1ObjectIdentifier);
    }

    public JceCMSKEMKeyWrapper setProvider(Provider provider) {
        this.helper = new ProviderJcaJceExtHelper(provider);
        return this;
    }

    public JceCMSKEMKeyWrapper setProvider(String string) {
        this.helper = new NamedJcaJceExtHelper(string);
        return this;
    }

    public JceCMSKEMKeyWrapper setKDF(AlgorithmIdentifier algorithmIdentifier) {
        this.kdfAlgorithm = algorithmIdentifier;
        return this;
    }

    public JceCMSKEMKeyWrapper setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceCMSKEMKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        this.extraMappings.put(aSN1ObjectIdentifier, string);
        return this;
    }

    @Override
    public byte[] getEncapsulation() {
        return this.encapsulation;
    }

    @Override
    public AlgorithmIdentifier getKdfAlgorithmIdentifier() {
        return this.kdfAlgorithm;
    }

    @Override
    public int getKekLength() {
        return this.kekLength;
    }

    @Override
    public AlgorithmIdentifier getWrapAlgorithmIdentifier() {
        return this.symWrapAlgorithm;
    }

    @Override
    public byte[] generateWrappedKey(GenericKey genericKey) throws OperatorException {
        try {
            byte[] byArray = new CMSORIforKEMOtherInfo(this.symWrapAlgorithm, this.kekLength).getEncoded();
            if (this.publicKey instanceof RSAPublicKey) {
                Cipher cipher = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
                try {
                    KTSParameterSpec kTSParameterSpec = new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, byArray).withKdfAlgorithm(this.kdfAlgorithm).build();
                    cipher.init(3, (Key)this.publicKey, kTSParameterSpec, this.random);
                    byte[] byArray2 = cipher.wrap(CMSUtils.getJceKey(genericKey));
                    int n = (((RSAPublicKey)this.publicKey).getModulus().bitLength() + 7) / 8;
                    this.encapsulation = Arrays.copyOfRange(byArray2, 0, n);
                    return Arrays.copyOfRange(byArray2, n, byArray2.length);
                }
                catch (Exception exception) {
                    throw new OperatorException("Unable to wrap contents key: " + exception.getMessage(), exception);
                }
            }
            Cipher cipher = CMSUtils.createAsymmetricWrapper(this.helper, this.getAlgorithmIdentifier().getAlgorithm(), new HashMap());
            try {
                KTSParameterSpec kTSParameterSpec = new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, byArray).withKdfAlgorithm(this.kdfAlgorithm).build();
                cipher.init(3, (Key)this.publicKey, kTSParameterSpec, this.random);
                byte[] byArray3 = cipher.wrap(CMSUtils.getJceKey(genericKey));
                int n = this.getKemEncLength(this.publicKey);
                this.encapsulation = Arrays.copyOfRange(byArray3, 0, n);
                return Arrays.copyOfRange(byArray3, n, byArray3.length);
            }
            catch (Exception exception) {
                throw new OperatorException("Unable to wrap contents key: " + exception.getMessage(), exception);
            }
        }
        catch (Exception exception) {
            throw new OperatorException("unable to wrap contents key: " + exception.getMessage(), exception);
        }
    }

    private int getKemEncLength(PublicKey publicKey) {
        return this.kemEncLenProvider.getEncapsulationLength(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm());
    }
}

