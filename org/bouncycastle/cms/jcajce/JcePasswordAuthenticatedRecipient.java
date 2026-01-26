/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms.jcajce;

import java.io.OutputStream;
import java.security.Key;
import javax.crypto.Mac;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.cms.jcajce.JcePasswordRecipient;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.jcajce.JceGenericKey;

public class JcePasswordAuthenticatedRecipient
extends JcePasswordRecipient {
    public JcePasswordAuthenticatedRecipient(char[] cArray) {
        super(cArray);
    }

    @Override
    public RecipientOperator getRecipientOperator(AlgorithmIdentifier algorithmIdentifier, final AlgorithmIdentifier algorithmIdentifier2, byte[] byArray, byte[] byArray2) throws CMSException {
        final Key key = this.extractSecretKey(algorithmIdentifier, algorithmIdentifier2, byArray, byArray2);
        final Mac mac = this.helper.createContentMac(key, algorithmIdentifier2);
        return new RecipientOperator(new MacCalculator(){
            final /* synthetic */ JcePasswordAuthenticatedRecipient this$0;
            {
                this.this$0 = jcePasswordAuthenticatedRecipient;
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return algorithmIdentifier2;
            }

            @Override
            public GenericKey getKey() {
                return new JceGenericKey(algorithmIdentifier2, key);
            }

            @Override
            public OutputStream getOutputStream() {
                return new MacOutputStream(mac);
            }

            @Override
            public byte[] getMac() {
                return mac.doFinal();
            }
        });
    }
}

