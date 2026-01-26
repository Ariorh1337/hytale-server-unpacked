/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.security.Key;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.CMSInputAEADDecryptor;
import org.bouncycastle.cms.jcajce.JcePasswordRecipient;

public class JcePasswordAuthEnvelopedRecipient
extends JcePasswordRecipient {
    public JcePasswordAuthEnvelopedRecipient(char[] cArray) {
        super(cArray);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] byArray, byte[] byArray2) throws CMSException {
        Key key = this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, byArray, byArray2);
        Cipher cipher = this.helper.createContentCipher(key, algorithmIdentifier2);
        return new RecipientOperator(new CMSInputAEADDecryptor(algorithmIdentifier2, cipher));
    }
}

