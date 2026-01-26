/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.CMSInputAEADDecryptor;
import org.bouncycastle.cms.jcajce.JceKEKRecipient;

public class JceKEKAuthEnvelopedRecipient
extends JceKEKRecipient {
    public JceKEKAuthEnvelopedRecipient(SecretKey secretKey) {
        super(secretKey);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] byArray) throws CMSException {
        Key key = this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, byArray);
        Cipher cipher = this.contentHelper.createContentCipher(key, algorithmIdentifier2);
        return new RecipientOperator(new CMSInputAEADDecryptor(algorithmIdentifier2, cipher));
    }
}

