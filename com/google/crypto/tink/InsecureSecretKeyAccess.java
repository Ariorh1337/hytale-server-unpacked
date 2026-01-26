/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.SecretKeyAccess;
import com.google.errorprone.annotations.CheckReturnValue;
import com.google.errorprone.annotations.Immutable;

@CheckReturnValue
@Immutable
public final class InsecureSecretKeyAccess {
    private InsecureSecretKeyAccess() {
    }

    public static SecretKeyAccess get() {
        return SecretKeyAccess.instance();
    }
}

