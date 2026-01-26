/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;

public interface MLDSAKey
extends Key {
    public MLDSAParameterSpec getParameterSpec();
}

