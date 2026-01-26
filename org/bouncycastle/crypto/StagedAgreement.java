/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto;

import org.bouncycastle.crypto.BasicAgreement;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public interface StagedAgreement
extends BasicAgreement {
    public AsymmetricKeyParameter calculateStage(CipherParameters var1);
}

