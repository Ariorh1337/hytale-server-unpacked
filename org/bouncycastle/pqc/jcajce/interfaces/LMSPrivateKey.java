/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.LMSKey;

public interface LMSPrivateKey
extends LMSKey,
PrivateKey {
    public long getIndex();

    public long getUsagesRemaining();

    public LMSPrivateKey extractKeyShard(int var1);
}

