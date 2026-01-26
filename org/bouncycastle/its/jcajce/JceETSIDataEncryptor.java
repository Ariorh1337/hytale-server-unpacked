/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.its.jcajce.ClassUtil;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;

public class JceETSIDataEncryptor
implements ETSIDataEncryptor {
    private final SecureRandom random;
    private final JcaJceHelper helper;
    private byte[] nonce;
    private byte[] key;

    private JceETSIDataEncryptor(SecureRandom secureRandom, JcaJceHelper jcaJceHelper) {
        this.random = secureRandom;
        this.helper = jcaJceHelper;
    }

    @Override
    public byte[] encrypt(byte[] byArray) {
        this.key = new byte[16];
        this.random.nextBytes(this.key);
        this.nonce = new byte[12];
        this.random.nextBytes(this.nonce);
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(this.key, "AES");
            Cipher cipher = this.helper.createCipher("CCM");
            cipher.init(1, (Key)secretKeySpec, ClassUtil.getGCMSpec(this.nonce, 128));
            return cipher.doFinal(byArray);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    @Override
    public byte[] getKey() {
        return this.key;
    }

    @Override
    public byte[] getNonce() {
        return this.nonce;
    }

    public static class Builder {
        private SecureRandom random;
        private JcaJceHelper helper = new DefaultJcaJceHelper();

        public Builder setRandom(SecureRandom secureRandom) {
            this.random = secureRandom;
            return this;
        }

        public Builder setProvider(Provider provider) {
            this.helper = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder setProvider(String string) {
            this.helper = new NamedJcaJceHelper(string);
            return this;
        }

        public JceETSIDataEncryptor build() {
            if (this.random == null) {
                this.random = new SecureRandom();
            }
            return new JceETSIDataEncryptor(this.random, this.helper);
        }
    }
}

