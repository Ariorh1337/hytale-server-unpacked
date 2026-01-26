/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.pqc.crypto.lms.LMSContext;

public interface LMSContextBasedSigner {
    public LMSContext generateLMSContext();

    public byte[] generateSignature(LMSContext var1);

    public long getUsagesRemaining();
}

