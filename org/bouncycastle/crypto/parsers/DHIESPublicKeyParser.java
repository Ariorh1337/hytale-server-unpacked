/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import org.bouncycastle.crypto.KeyParser;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.util.io.Streams;

public class DHIESPublicKeyParser
implements KeyParser {
    private DHParameters dhParams;

    public DHIESPublicKeyParser(DHParameters dHParameters) {
        this.dhParams = dHParameters;
    }

    @Override
    public AsymmetricKeyParameter readKey(InputStream inputStream) throws IOException {
        byte[] byArray = new byte[(this.dhParams.getP().bitLength() + 7) / 8];
        Streams.readFully(inputStream, byArray, 0, byArray.length);
        return new DHPublicKeyParameters(new BigInteger(1, byArray), this.dhParams);
    }
}

