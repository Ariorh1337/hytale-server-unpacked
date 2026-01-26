/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.crypto.impl;

import com.nimbusds.jose.shaded.jcip.ThreadSafe;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

@ThreadSafe
public class CipherHelper {
    public static Cipher getInstance(String name, Provider provider) throws NoSuchAlgorithmException, NoSuchPaddingException {
        if (provider == null) {
            return Cipher.getInstance(name);
        }
        return Cipher.getInstance(name, provider);
    }
}

