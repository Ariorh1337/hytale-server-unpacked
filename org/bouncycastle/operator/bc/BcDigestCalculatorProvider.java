/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.operator.bc.BcDigestProvider;

public class BcDigestCalculatorProvider
implements DigestCalculatorProvider {
    private BcDigestProvider digestProvider = BcDefaultDigestProvider.INSTANCE;

    @Override
    public DigestCalculator get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        ExtendedDigest extendedDigest = this.digestProvider.get(algorithmIdentifier);
        final DigestOutputStream digestOutputStream = new DigestOutputStream(extendedDigest);
        return new DigestCalculator(){
            final /* synthetic */ BcDigestCalculatorProvider this$0;
            {
                this.this$0 = bcDigestCalculatorProvider;
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

    private static class DigestOutputStream
    extends OutputStream {
        private Digest dig;

        DigestOutputStream(Digest digest) {
            this.dig = digest;
        }

        @Override
        public void write(byte[] byArray, int n, int n2) throws IOException {
            this.dig.update(byArray, n, n2);
        }

        @Override
        public void write(byte[] byArray) throws IOException {
            this.dig.update(byArray, 0, byArray.length);
        }

        @Override
        public void write(int n) throws IOException {
            this.dig.update((byte)n);
        }

        byte[] getDigest() {
            byte[] byArray = new byte[this.dig.getDigestSize()];
            this.dig.doFinal(byArray, 0);
            return byArray;
        }
    }
}

