/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est;

import java.io.IOException;
import org.bouncycastle.est.Source;

public interface ESTClientSourceProvider {
    public Source makeSource(String var1, int var2) throws IOException;
}

