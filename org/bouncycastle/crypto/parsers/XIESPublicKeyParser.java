/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.util.io.Streams;

public class XIESPublicKeyParser
implements KeyParser {
    private final boolean isX25519;

    public XIESPublicKeyParser(boolean bl) {
        this.isX25519 = bl;
    }

    @Override
    public AsymmetricKeyParameter readKey(InputStream inputStream) throws IOException {
        int n = this.isX25519 ? 32 : 56;
        byte[] byArray = new byte[n];
        Streams.readFully(inputStream, byArray, 0, byArray.length);
        return this.isX25519 ? new X25519PublicKeyParameters(byArray, 0) : new X448PublicKeyParameters(byArray, 0);
    }
}

