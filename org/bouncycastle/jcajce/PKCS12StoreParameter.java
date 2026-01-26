/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import java.io.OutputStream;
import java.security.KeyStore;

public class PKCS12StoreParameter
implements KeyStore.LoadStoreParameter {
    private final OutputStream out;
    private final KeyStore.ProtectionParameter protectionParameter;
    private final boolean forDEREncoding;
    private final boolean overwriteFriendlyName;

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray) {
        this(outputStream, cArray, false);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter) {
        this(outputStream, protectionParameter, false, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray, boolean bl) {
        this(outputStream, new KeyStore.PasswordProtection(cArray), bl, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter, boolean bl) {
        this(outputStream, protectionParameter, bl, true);
    }

    public PKCS12StoreParameter(OutputStream outputStream, char[] cArray, boolean bl, boolean bl2) {
        this(outputStream, new KeyStore.PasswordProtection(cArray), bl, bl2);
    }

    public PKCS12StoreParameter(OutputStream outputStream, KeyStore.ProtectionParameter protectionParameter, boolean bl, boolean bl2) {
        this.out = outputStream;
        this.protectionParameter = protectionParameter;
        this.forDEREncoding = bl;
        this.overwriteFriendlyName = bl2;
    }

    public OutputStream getOutputStream() {
        return this.out;
    }

    @Override
    public KeyStore.ProtectionParameter getProtectionParameter() {
        return this.protectionParameter;
    }

    public boolean isForDEREncoding() {
        return this.forDEREncoding;
    }

    public boolean isOverwriteFriendlyName() {
        return this.overwriteFriendlyName;
    }
}

