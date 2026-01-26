/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import java.security.SecureRandom;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;

public class DilithiumParameters {
    public static final DilithiumParameters dilithium2 = new DilithiumParameters("dilithium2", 2, false);
    public static final DilithiumParameters dilithium3 = new DilithiumParameters("dilithium3", 3, false);
    public static final DilithiumParameters dilithium5 = new DilithiumParameters("dilithium5", 5, false);
    private final int k;
    private final String name;
    @Deprecated
    private final boolean usingAES;

    private DilithiumParameters(String string, int n, boolean bl) {
        this.name = string;
        this.k = n;
        this.usingAES = bl;
    }

    DilithiumEngine getEngine(SecureRandom secureRandom) {
        return new DilithiumEngine(this.k, secureRandom, this.usingAES);
    }

    public String getName() {
        return this.name;
    }
}

