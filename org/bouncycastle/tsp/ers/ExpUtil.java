/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

class ExpUtil {
    ExpUtil() {
    }

    static IllegalStateException createIllegalState(String string, Throwable throwable) {
        return new IllegalStateException(string, throwable);
    }
}

