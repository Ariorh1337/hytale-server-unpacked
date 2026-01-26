/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

public class DigestingMessageSigner
implements Signer {
    private final Digest messDigest;
    private final MessageSigner messSigner;
    private boolean forSigning;

    public DigestingMessageSigner(MessageSigner messageSigner, Digest digest) {
        this.messSigner = messageSigner;
        this.messDigest = digest;
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forSigning = bl;
        AsymmetricKeyParameter asymmetricKeyParameter = cipherParameters instanceof ParametersWithRandom ? (AsymmetricKeyParameter)((ParametersWithRandom)cipherParameters).getParameters() : (AsymmetricKeyParameter)cipherParameters;
        if (bl && !asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("Signing Requires Private Key.");
        }
        if (!bl && asymmetricKeyParameter.isPrivate()) {
            throw new IllegalArgumentException("Verification Requires Public Key.");
        }
        this.reset();
        this.messSigner.init(bl, cipherParameters);
    }

    @Override
    public byte[] generateSignature() {
        if (!this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for signature generation.");
        }
        byte[] byArray = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(byArray, 0);
        return this.messSigner.generateSignature(byArray);
    }

    @Override
    public void update(byte by) {
        this.messDigest.update(by);
    }

    @Override
    public void update(byte[] byArray, int n, int n2) {
        this.messDigest.update(byArray, n, n2);
    }

    @Override
    public void reset() {
        this.messDigest.reset();
    }

    @Override
    public boolean verifySignature(byte[] byArray) {
        if (this.forSigning) {
            throw new IllegalStateException("DigestingMessageSigner not initialised for verification");
        }
        byte[] byArray2 = new byte[this.messDigest.getDigestSize()];
        this.messDigest.doFinal(byArray2, 0);
        return this.messSigner.verifySignature(byArray2, byArray);
    }
}

