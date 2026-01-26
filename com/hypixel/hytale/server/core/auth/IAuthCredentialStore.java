/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.auth;

import java.time.Instant;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAuthCredentialStore {
    public void setTokens(@Nonnull OAuthTokens var1);

    @Nonnull
    public OAuthTokens getTokens();

    public void setProfile(@Nullable UUID var1);

    @Nullable
    public UUID getProfile();

    public void clear();

    public record OAuthTokens(@Nullable String accessToken, @Nullable String refreshToken, @Nullable Instant accessTokenExpiresAt) {
        public boolean isValid() {
            return this.refreshToken != null;
        }
    }
}

