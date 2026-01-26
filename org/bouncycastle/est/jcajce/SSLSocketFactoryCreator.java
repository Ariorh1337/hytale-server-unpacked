/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.est.jcajce;

import javax.net.ssl.SSLSocketFactory;

public interface SSLSocketFactoryCreator {
    public SSLSocketFactory createFactory() throws Exception;

    public boolean isTrusted();
}

