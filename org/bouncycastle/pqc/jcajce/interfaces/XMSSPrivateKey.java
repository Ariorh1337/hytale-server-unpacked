/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSKey;

public interface XMSSPrivateKey
extends XMSSKey,
PrivateKey {
    public long getIndex();

    public long getUsagesRemaining();

    public XMSSPrivateKey extractKeyShard(int var1);
}

