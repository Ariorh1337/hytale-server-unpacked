/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
import com.hypixel.hytale.server.core.auth.DefaultAuthCredentialStore;
import com.hypixel.hytale.server.core.auth.IAuthCredentialStore;
import javax.annotation.Nonnull;

public class MemoryAuthCredentialStoreProvider
implements AuthCredentialStoreProvider {
    public static final String ID = "Memory";
    public static final BuilderCodec<MemoryAuthCredentialStoreProvider> CODEC = BuilderCodec.builder(MemoryAuthCredentialStoreProvider.class, MemoryAuthCredentialStoreProvider::new).build();

    @Override
    @Nonnull
    public IAuthCredentialStore createStore() {
        return new DefaultAuthCredentialStore();
    }

    @Nonnull
    public String toString() {
        return "MemoryAuthCredentialStoreProvider{}";
    }
}

