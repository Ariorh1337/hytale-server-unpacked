/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.spec.ScryptParameterSpec
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.jcajce.spec.ScryptKeySpec;

public class ScryptParameterSpec
extends ScryptKeySpec
implements AlgorithmParameterSpec {
    public ScryptParameterSpec(char[] cArray, byte[] byArray, int n, int n2, int n3, int n4) {
        super(cArray, byArray, n, n2, n3, n4);
    }
}

