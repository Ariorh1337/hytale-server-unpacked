/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.pqc.crypto.sphincsplus.SPHINCSPlusEngine;

interface SPHINCSPlusEngineProvider {
    public int getN();

    public SPHINCSPlusEngine get();
}

