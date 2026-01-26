/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.config;

import java.io.OutputStream;
import java.security.KeyStore;

public class PKCS12StoreParameter
extends org.bouncycastle.jcajce.PKCS12StoreParameter {
    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray) {
        super(outputStream, cArray, false);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter) {
        super(outputStream, protectionParameter, false);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray, boolean bl) {
        super(outputStream, new KeyStore.PasswordProtection(cArray), bl);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray, boolean bl, boolean bl2) {
        super(outputStream, new KeyStore.PasswordProtection(cArray), bl, bl2);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter, boolean bl) {
        super(outputStream, protectionParameter, bl);
    }
}

