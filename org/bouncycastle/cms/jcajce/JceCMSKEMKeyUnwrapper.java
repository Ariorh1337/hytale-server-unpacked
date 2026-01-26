/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.JcaJceExtHelper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.AsymmetricKeyUnwrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceGenericKey;
import org.bouncycastle.util.Arrays;

class JceCMSKEMKeyUnwrapper
extends AsymmetricKeyUnwrapper {
    private final AlgorithmIdentifier symWrapAlgorithm;
    private final int kekLength;
    private JcaJceExtHelper helper = new DefaultJcaJceExtHelper();
    private Map extraMappings = new HashMap();
    private PrivateKey privateKey;

    public JceCMSKEMKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privateKey) {
        super(PrivateKeyInfo.getInstance(privateKey.getEncoded()).getPrivateKeyAlgorithm());
        KEMRecipientInfo kEMRecipientInfo = KEMRecipientInfo.getInstance(algorithmIdentifier.getParameters());
        this.privateKey = privateKey;
        this.symWrapAlgorithm = algorithmIdentifier;
        this.kekLength = CMSUtils.getKekSize(kEMRecipientInfo.getWrap().getAlgorithm());
    }

    public JceCMSKEMKeyUnwrapper setProvider(Provider provider) {
        this.helper = new ProviderJcaJceExtHelper(provider);
        return this;
    }

    public JceCMSKEMKeyUnwrapper setProvider(String string) {
        this.helper = new NamedJcaJceExtHelper(string);
        return this;
    }

    public JceCMSKEMKeyUnwrapper setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        this.extraMappings.put(aSN1ObjectIdentifier, string);
        return this;
    }

    public int getKekLength() {
        return this.kekLength;
    }

    @Override
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier algorithmIdentifier, byte[] byArray) throws OperatorException {
        KEMRecipientInfo kEMRecipientInfo = KEMRecipientInfo.getInstance(this.symWrapAlgorithm.getParameters());
        AlgorithmIdentifier algorithmIdentifier2 = kEMRecipientInfo.getWrap();
        try {
            byte[] byArray2 = new CMSORIforKEMOtherInfo(algorithmIdentifier2, this.kekLength, kEMRecipientInfo.getUkm()).getEncoded();
            if (this.privateKey instanceof RSAPrivateKey) {
                Cipher cipher = CMSUtils.createAsymmetricWrapper(this.helper, kEMRecipientInfo.getKem().getAlgorithm(), new HashMap());
                try {
                    String string = CMSUtils.getWrapAlgorithmName(algorithmIdentifier2.getAlgorithm());
                    KTSParameterSpec kTSParameterSpec = new KTSParameterSpec.Builder(string, this.kekLength * 8, byArray2).withKdfAlgorithm(kEMRecipientInfo.getKdf()).build();
                    cipher.init(4, (Key)this.privateKey, kTSParameterSpec);
                    Key key = cipher.unwrap(Arrays.concatenate(kEMRecipientInfo.getKemct().getOctets(), kEMRecipientInfo.getEncryptedKey().getOctets()), string, 3);
                    return new JceGenericKey(algorithmIdentifier, key);
                }
                catch (Exception exception) {
                    throw new OperatorException("Unable to wrap contents key: " + exception.getMessage(), exception);
                }
            }
            Cipher cipher = CMSUtils.createAsymmetricWrapper(this.helper, kEMRecipientInfo.getKem().getAlgorithm(), new HashMap());
            String string = CMSUtils.getWrapAlgorithmName(algorithmIdentifier2.getAlgorithm());
            KTSParameterSpec kTSParameterSpec = new KTSParameterSpec.Builder(string, this.kekLength * 8, byArray2).withKdfAlgorithm(kEMRecipientInfo.getKdf()).build();
            cipher.init(4, (Key)this.privateKey, kTSParameterSpec);
            Key key = cipher.unwrap(Arrays.concatenate(kEMRecipientInfo.getKemct().getOctets(), kEMRecipientInfo.getEncryptedKey().getOctets()), string, 3);
            return new JceGenericKey(algorithmIdentifier, key);
        }
        catch (Exception exception) {
            throw new OperatorException("exception encrypting key: " + exception.getMessage(), exception);
        }
    }
}

