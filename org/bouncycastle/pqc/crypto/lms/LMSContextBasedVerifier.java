/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.pqc.crypto.lms.LMSContext;

public interface LMSContextBasedVerifier {
    public LMSContext generateLMSContext(byte[] var1);

    public boolean verify(LMSContext var1);
}

