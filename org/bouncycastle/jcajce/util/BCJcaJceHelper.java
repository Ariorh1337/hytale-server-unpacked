/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class BCJcaJceHelper
extends ProviderJcaJceHelper {
    private static volatile Provider bcProvider;

    private static synchronized Provider getBouncyCastleProvider() {
        Provider provider = Security.getProvider("BC");
        if (provider instanceof BouncyCastleProvider) {
            return provider;
        }
        if (bcProvider != null) {
            return bcProvider;
        }
        bcProvider = new BouncyCastleProvider();
        return bcProvider;
    }

    public BCJcaJceHelper() {
        super(BCJcaJceHelper.getBouncyCastleProvider());
    }
}

