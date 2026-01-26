/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkcs.bc;

import java.io.InputStream;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.generators.PKCS12ParametersGenerator;
import org.bouncycastle.crypto.io.CipherInputStream;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.bc.PKCS12PBEUtils;

public class BcPKCS12PBEInputDecryptorProviderBuilder {
    private ExtendedDigest digest;

    public BcPKCS12PBEInputDecryptorProviderBuilder() {
        this(new SHA1Digest());
    }

    public BcPKCS12PBEInputDecryptorProviderBuilder(ExtendedDigest extendedDigest) {
        this.digest = extendedDigest;
    }

    public InputDecryptorProvider build(final char[] cArray) {
        return new InputDecryptorProvider(){
            final /* synthetic */ BcPKCS12PBEInputDecryptorProviderBuilder this$0;
            {
                this.this$0 = bcPKCS12PBEInputDecryptorProviderBuilder;
            }

            @Override
            public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier) {
                final PaddedBufferedBlockCipher paddedBufferedBlockCipher = PKCS12PBEUtils.getEngine(algorithmIdentifier.getAlgorithm());
                PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                CipherParameters cipherParameters = PKCS12PBEUtils.createCipherParameters(algorithmIdentifier.getAlgorithm(), this.this$0.digest, paddedBufferedBlockCipher.getBlockSize(), pKCS12PBEParams, cArray);
                paddedBufferedBlockCipher.init(false, cipherParameters);
                return new InputDecryptor(){
                    final /* synthetic */ 1 this$1;
                    {
                        this.this$1 = var1_1;
                    }

                    @Override
                    public AlgorithmIdentifier getAlgorithmIdentifier() {
                        return algorithmIdentifier;
                    }

                    @Override
                    public InputStream getInputStream(InputStream inputStream) {
                        return new CipherInputStream(inputStream, paddedBufferedBlockCipher);
                    }

                    public GenericKey getKey() {
                        return new GenericKey(algorithmIdentifier, PKCS12ParametersGenerator.PKCS12PasswordToBytes(cArray));
                    }
                };
            }
        };
    }
}

