/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.opts;

import com.nimbusds.jose.Option;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.crypto.opts.AllowWeakRSAKey;
import java.security.PrivateKey;
import java.util.Set;

public class OptionUtils {
    @Deprecated
    public static <T extends Option> boolean optionIsPresent(Set<? extends Option> opts, Class<T> tClass) {
        if (opts == null || opts.isEmpty()) {
            return false;
        }
        for (Option option : opts) {
            if (!option.getClass().isAssignableFrom(tClass)) continue;
            return true;
        }
        return false;
    }

    public static void ensureMinRSAPrivateKeySize(PrivateKey privateKey, Set<? extends Option> opts) {
        int keyBitLength;
        if (!opts.contains(AllowWeakRSAKey.getInstance()) && (keyBitLength = RSAKeyUtils.keyBitLength(privateKey)) > 0 && keyBitLength < 2048) {
            throw new IllegalArgumentException("The RSA key size must be at least 2048 bits");
        }
    }
}

