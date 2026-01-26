/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.transport;

import java.net.Authenticator;
import org.jetbrains.annotations.NotNull;

final class AuthenticatorWrapper {
    private static final AuthenticatorWrapper instance = new AuthenticatorWrapper();

    public static AuthenticatorWrapper getInstance() {
        return instance;
    }

    private AuthenticatorWrapper() {
    }

    public void setDefault(@NotNull Authenticator authenticator) {
        Authenticator.setDefault(authenticator);
    }
}

