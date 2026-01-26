/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.IOException;
import org.bouncycastle.est.ESTRequest;
import org.bouncycastle.est.Source;

public interface ESTSourceConnectionListener<T, I> {
    public ESTRequest onConnection(Source<T> var1, ESTRequest var2) throws IOException;
}

