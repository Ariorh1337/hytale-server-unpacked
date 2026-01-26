/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import java.util.List;
import org.bouncycastle.cert.dane.DANEException;

public interface DANEEntryFetcher {
    public List getEntries() throws DANEException;
}

