/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.hpke.HPKEContext;
import org.bouncycastle.util.Arrays;

public class HPKEContextWithEncapsulation
extends HPKEContext {
    final byte[] encapsulation;

    public HPKEContextWithEncapsulation(HPKEContext hPKEContext, byte[] byArray) {
        super(hPKEContext.aead, hPKEContext.hkdf, hPKEContext.exporterSecret, hPKEContext.suiteId);
        this.encapsulation = byArray;
    }

    public byte[] getEncapsulation() {
        return Arrays.clone(this.encapsulation);
    }
}

