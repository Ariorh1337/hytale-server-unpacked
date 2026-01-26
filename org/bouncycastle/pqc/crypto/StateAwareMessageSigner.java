/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.MessageSigner;

public interface StateAwareMessageSigner
extends MessageSigner {
    public AsymmetricKeyParameter getUpdatedPrivateKey();
}

