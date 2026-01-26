/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.jcajce.spec.SLHDSAParameterSpec;

public interface SLHDSAKey
extends Key {
    public SLHDSAParameterSpec getParameterSpec();
}

