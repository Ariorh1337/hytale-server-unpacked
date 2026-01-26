/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.its.jcajce.ClassUtil;
import org.bouncycastle.its.operator.ETSIDataDecryptor;
import org.bouncycastle.jcajce.spec.IESKEMParameterSpec;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.util.Arrays;

public class JcaETSIDataDecryptor
implements ETSIDataDecryptor {
    private final PrivateKey privateKey;
    private final JcaJceHelper helper;
    private final byte[] recipientHash;
    private SecretKey secretKey = null;

    JcaETSIDataDecryptor(PrivateKey privateKey, byte[] byArray, JcaJceHelper jcaJceHelper) {
        this.privateKey = privateKey;
        this.helper = jcaJceHelper;
        this.recipientHash = byArray;
    }

    @Override
    public byte[] decrypt(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        try {
            Cipher cipher = this.helper.createCipher("ETSIKEMwithSHA256");
            cipher.init(4, (Key)this.privateKey, new IESKEMParameterSpec(this.recipientHash));
            this.secretKey = (SecretKey)cipher.unwrap(byArray, "AES", 3);
            Cipher cipher2 = this.helper.createCipher("CCM");
            cipher2.init(2, (Key)this.secretKey, ClassUtil.getGCMSpec(byArray3, 128));
            return cipher2.doFinal(byArray2);
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    @Override
    public byte[] getKey() {
        if (this.secretKey == null) {
            throw new IllegalStateException("no secret key recovered");
        }
        return this.secretKey.getEncoded();
    }

    public static Builder builder(PrivateKey privateKey, byte[] byArray) {
        return new Builder(privateKey, byArray);
    }

    public static class Builder {
        private JcaJceHelper provider;
        private final byte[] recipientHash;
        private final PrivateKey key;

        public Builder(PrivateKey privateKey, byte[] byArray) {
            this.key = privateKey;
            this.recipientHash = Arrays.clone(byArray);
        }

        public Builder provider(Provider provider) {
            this.provider = new ProviderJcaJceHelper(provider);
            return this;
        }

        public Builder provider(String string) {
            this.provider = new NamedJcaJceHelper(string);
            return this;
        }

        public JcaETSIDataDecryptor build() {
            return new JcaETSIDataDecryptor(this.key, this.recipientHash, this.provider);
        }
    }
}

