/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.operator.ContentSigner;

public class NoSignatureContentSigner
implements ContentSigner {
    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return new AlgorithmIdentifier(X509ObjectIdentifiers.id_alg_unsigned);
    }

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream(){

            @Override
            public void write(byte[] byArray, int n, int n2) throws IOException {
            }

            @Override
            public void write(int n) throws IOException {
            }
        };
    }

    @Override
    public byte[] getSignature() {
        return new byte[0];
    }
}

