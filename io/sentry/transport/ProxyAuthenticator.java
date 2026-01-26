/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.transport;

import io.sentry.util.Objects;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ProxyAuthenticator
extends Authenticator {
    @NotNull
    private final String user;
    @NotNull
    private final String password;

    ProxyAuthenticator(@NotNull String user, @NotNull String password) {
        this.user = Objects.requireNonNull(user, "user is required");
        this.password = Objects.requireNonNull(password, "password is required");
    }

    @Override
    @Nullable
    protected PasswordAuthentication getPasswordAuthentication() {
        if (this.getRequestorType() == Authenticator.RequestorType.PROXY) {
            return new PasswordAuthentication(this.user, this.password.toCharArray());
        }
        return null;
    }

    @NotNull
    String getUser() {
        return this.user;
    }

    @NotNull
    String getPassword() {
        return this.password;
    }
}

