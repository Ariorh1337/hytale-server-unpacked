/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ExtendedContentSigner;
import org.bouncycastle.util.io.BufferingOutputStream;

public class BufferingContentSigner
implements ExtendedContentSigner {
    private final ContentSigner contentSigner;
    private final OutputStream output;

    public BufferingContentSigner(ContentSigner contentSigner) {
        this.contentSigner = contentSigner;
        this.output = new BufferingOutputStream(contentSigner.getOutputStream());
    }

    public BufferingContentSigner(ContentSigner contentSigner, int n) {
        this.contentSigner = contentSigner;
        this.output = new BufferingOutputStream(contentSigner.getOutputStream(), n);
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.contentSigner.getAlgorithmIdentifier();
    }

    @Override
    public OutputStream getOutputStream() {
        return this.output;
    }

    @Override
    public byte[] getSignature() {
        return this.contentSigner.getSignature();
    }

    @Override
    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        if (this.contentSigner instanceof ExtendedContentSigner) {
            return ((ExtendedContentSigner)this.contentSigner).getDigestAlgorithmIdentifier();
        }
        return null;
    }
}

