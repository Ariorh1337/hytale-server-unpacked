/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.pqc.crypto.KEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMEngine;

public class MLKEMParameters
implements KEMParameters {
    public static final MLKEMParameters ml_kem_512 = new MLKEMParameters("ML-KEM-512", 2, 256);
    public static final MLKEMParameters ml_kem_768 = new MLKEMParameters("ML-KEM-768", 3, 256);
    public static final MLKEMParameters ml_kem_1024 = new MLKEMParameters("ML-KEM-1024", 4, 256);
    private final String name;
    private final int k;
    private final int sessionKeySize;

    private MLKEMParameters(String string, int n, int n2) {
        this.name = string;
        this.k = n;
        this.sessionKeySize = n2;
    }

    public String getName() {
        return this.name;
    }

    public MLKEMEngine getEngine() {
        return new MLKEMEngine(this.k);
    }

    public int getSessionKeySize() {
        return this.sessionKeySize;
    }
}

