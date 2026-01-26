/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.jcajce;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.Provider;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.OperatorHelper;

public class JcaDigestCalculatorProviderBuilder {
    private OperatorHelper helper = new OperatorHelper(new DefaultJcaJceHelper());

    public JcaDigestCalculatorProviderBuilder setHelper(JcaJceHelper jcaJceHelper) {
        this.helper = new OperatorHelper(jcaJceHelper);
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JcaDigestCalculatorProviderBuilder setProvider(String string) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(string));
        return this;
    }

    public DigestCalculatorProvider build() throws OperatorCreationException {
        return new DigestCalculatorProvider(){

            @Override
            public DigestCalculator get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                DigestOutputStream digestOutputStream;
                try {
                    MessageDigest messageDigest = JcaDigestCalculatorProviderBuilder.this.helper.createDigest(algorithmIdentifier);
                    digestOutputStream = new DigestOutputStream(messageDigest);
                }
                catch (GeneralSecurityException generalSecurityException) {
                    throw new OperatorCreationException("exception on setup: " + generalSecurityException, generalSecurityException);
                }
                return new DigestCalculator(){
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                    }

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return algorithmIdentifier;
                    }

                    @Override
                    public OutputStream getOutputStream() {
                        return digestOutputStream;
                    }

                    @Override
                    public byte[] getDigest() {
                        return digestOutputStream.getDigest();
                    }
                };
            }
        };
    }

    private static class DigestOutputStream
    extends OutputStream {
        private MessageDigest dig;

        DigestOutputStream(MessageDigest messageDigest) {
            this.dig = messageDigest;
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this.dig.update(byArray, n, n2);
        }

        @Override
        public void write(byte[] byArray) throws IOException {
            this.dig.update(byArray);
        }

        @Override
        public void write(int n) throws IOException {
            this.dig.update((byte)n);
        }

        byte[] getDigest() {
            return this.dig.digest();
        }
    }
}

