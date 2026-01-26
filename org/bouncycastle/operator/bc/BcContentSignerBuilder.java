/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.bc.BcDefaultDigestProvider;
import org.bouncycastle.operator.bc.BcDigestProvider;
import org.bouncycastle.operator.bc.BcSignerOutputStream;

public abstract class BcContentSignerBuilder {
    private SecureRandom random;
    private AlgorithmIdentifier sigAlgId;
    private AlgorithmIdentifier digAlgId;
    protected BcDigestProvider digestProvider;

    public BcContentSignerBuilder(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        this.sigAlgId = algorithmIdentifier;
        this.digAlgId = algorithmIdentifier2;
        this.digestProvider = BcDefaultDigestProvider.INSTANCE;
    }

    public BcContentSignerBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public ContentSigner build(AsymmetricKeyParameter asymmetricKeyParameter) throws OperatorCreationException {
        final Signer signer = this.createSigner(this.sigAlgId, this.digAlgId);
        if (this.random != null) {
            signer.init(true, new ParametersWithRandom(asymmetricKeyParameter, this.random));
        } else {
            signer.init(true, asymmetricKeyParameter);
        }
        return new ContentSigner(){
            private BcSignerOutputStream stream;
            final /* synthetic */ BcContentSignerBuilder this$0;
            {
                this.this$0 = bcContentSignerBuilder;
                this.stream = new BcSignerOutputStream(signer);
            }

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return this.this$0.sigAlgId;
            }

            @Override
            public OutputStream getOutputStream() {
                return this.stream;
            }

            @Override
            public byte[] getSignature() {
                try {
                    return this.stream.getSignature();
                }
                catch (CryptoException cryptoException) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + cryptoException.getMessage(), cryptoException);
                }
            }
        };
    }

    protected abstract Signer createSigner(AlgorithmIdentifier var1, AlgorithmIdentifier var2) throws OperatorCreationException;
}

