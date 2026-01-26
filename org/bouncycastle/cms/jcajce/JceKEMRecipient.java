/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEMRecipient;
import org.bouncycastle.cms.jcajce.CMSUtils;
import org.bouncycastle.cms.jcajce.DefaultJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.EnvelopedDataHelper;
import org.bouncycastle.cms.jcajce.JceCMSKEMKeyUnwrapper;
import org.bouncycastle.cms.jcajce.NamedJcaJceExtHelper;
import org.bouncycastle.cms.jcajce.ProviderJcaJceExtHelper;
import org.bouncycastle.operator.OperatorException;

public abstract class JceKEMRecipient
implements KEMRecipient {
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper;
    protected EnvelopedDataHelper contentHelper;
    protected Map extraMappings;
    protected boolean validateKeySize;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKEMRecipient(PrivateKey privateKey) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.extraMappings = new HashMap();
        this.validateKeySize = false;
        this.recipientKey = CMSUtils.cleanPrivateKey(privateKey);
    }

    public JceKEMRecipient setProvider(Provider provider) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKEMRecipient setProvider(String string) {
        this.contentHelper = this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(string));
        return this;
    }

    public JceKEMRecipient setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String string) {
        this.extraMappings.put(aSN1ObjectIdentifier, string);
        return this;
    }

    public JceKEMRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKEMRecipient setMustProduceEncodableUnwrappedKey(boolean bl) {
        this.unwrappedKeyMustBeEncodable = bl;
        return this;
    }

    public JceKEMRecipient setContentProvider(String string) {
        this.contentHelper = CMSUtils.createContentHelper(string);
        return this;
    }

    public JceKEMRecipient setKeySizeValidation(boolean bl) {
        this.validateKeySize = bl;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] byArray) throws CMSException {
        KEMRecipientInfo kEMRecipientInfo = KEMRecipientInfo.getInstance(algorithmIdentifier.getParameters());
        JceCMSKEMKeyUnwrapper jceCMSKEMKeyUnwrapper = (JceCMSKEMKeyUnwrapper)this.helper.createKEMUnwrapper(algorithmIdentifier, this.recipientKey);
        if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.extraMappings.keySet()) {
                jceCMSKEMKeyUnwrapper.setAlgorithmMapping(aSN1ObjectIdentifier, (String)this.extraMappings.get(aSN1ObjectIdentifier));
            }
        }
        try {
            Key key = this.helper.getJceKey(algorithmIdentifier2, jceCMSKEMKeyUnwrapper.generateUnwrappedKey(algorithmIdentifier2, byArray));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(algorithmIdentifier2, key);
            }
            return key;
        }
        catch (OperatorException operatorException) {
            throw new CMSException("exception unwrapping key: " + operatorException.getMessage(), operatorException);
        }
    }
}

