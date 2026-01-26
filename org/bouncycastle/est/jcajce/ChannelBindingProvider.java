/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import java.net.Socket;

public interface ChannelBindingProvider {
    public boolean canAccessChannelBinding(Socket var1);

    public byte[] getChannelBinding(Socket var1, String var2);
}

