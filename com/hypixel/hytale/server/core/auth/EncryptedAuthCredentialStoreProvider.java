/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.server.core.auth.AuthCredentialStoreProvider;
import com.hypixel.hytale.server.core.auth.EncryptedAuthCredentialStore;
import com.hypixel.hytale.server.core.auth.IAuthCredentialStore;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class EncryptedAuthCredentialStoreProvider
implements AuthCredentialStoreProvider {
    public static final String ID = "Encrypted";
    public static final String DEFAULT_PATH = "auth.enc";
    public static final BuilderCodec<EncryptedAuthCredentialStoreProvider> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EncryptedAuthCredentialStoreProvider.class, EncryptedAuthCredentialStoreProvider::new).append(new KeyedCodec<String>("Path", Codec.STRING), (o, p) -> {
        o.path = p;
    }, o -> o.path).add()).build();
    private String path = "auth.enc";

    @Override
    @Nonnull
    public IAuthCredentialStore createStore() {
        return new EncryptedAuthCredentialStore(Path.of(this.path, new String[0]));
    }

    @Nonnull
    public String toString() {
        return "EncryptedAuthCredentialStoreProvider{path='" + this.path + "'}";
    }
}

