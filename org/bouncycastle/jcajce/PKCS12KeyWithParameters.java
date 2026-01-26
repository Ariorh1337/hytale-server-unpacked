/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce;

import javax.crypto.interfaces.PBEKey;
import org.bouncycastle.jcajce.PKCS12Key;
import org.bouncycastle.util.Arrays;

public class PKCS12KeyWithParameters
extends PKCS12Key
implements PBEKey {
    private final byte[] salt;
    private final int iterationCount;

    public PKCS12KeyWithParameters(char[] cArray, byte[] byArray, int n) {
        super(cArray);
        this.salt = Arrays.clone(byArray);
        this.iterationCount = n;
    }

    public PKCS12KeyWithParameters(char[] cArray, boolean bl, byte[] byArray, int n) {
        super(cArray, bl);
        this.salt = Arrays.clone(byArray);
        this.iterationCount = n;
    }

    @Override
    public byte[] getSalt() {
        return this.salt;
    }

    @Override
    public int getIterationCount() {
        return this.iterationCount;
    }
}

