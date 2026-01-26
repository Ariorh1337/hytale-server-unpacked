/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.tinkkey;

import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.errorprone.annotations.Immutable;

@Immutable
public final class SecretKeyAccess {
    private SecretKeyAccess() {
    }

    public static KeyAccess insecureSecretAccess() {
        return KeyAccess.secretAccess();
    }
}

