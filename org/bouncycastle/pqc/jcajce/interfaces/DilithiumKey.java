/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.jcajce.interfaces;

import java.security.Key;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;

public interface DilithiumKey
extends Key {
    public DilithiumParameterSpec getParameterSpec();
}

