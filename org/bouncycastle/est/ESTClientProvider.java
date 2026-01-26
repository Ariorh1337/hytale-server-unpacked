/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import org.bouncycastle.est.ESTClient;
import org.bouncycastle.est.ESTException;

public interface ESTClientProvider {
    public ESTClient makeClient() throws ESTException;

    public boolean isTrusted();
}

