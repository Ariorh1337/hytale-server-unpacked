/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.server.core.auth.IAuthCredentialStore;
import javax.annotation.Nonnull;

public interface AuthCredentialStoreProvider {
    public static final BuilderCodecMapCodec<AuthCredentialStoreProvider> CODEC = new BuilderCodecMapCodec("Type", true);

    @Nonnull
    public IAuthCredentialStore createStore();
}

