/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.PrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.XMSSMTKey;

public interface XMSSMTPrivateKey
extends XMSSMTKey,
PrivateKey {
    public long getIndex();

    public long getUsagesRemaining();

    public XMSSMTPrivateKey extractKeyShard(int var1);
}

