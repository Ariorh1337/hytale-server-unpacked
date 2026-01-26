/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.dane;

import org.bouncycastle.cert.dane.DANEEntryFetcher;

public interface DANEEntryFetcherFactory {
    public DANEEntryFetcher build(String var1);
}

