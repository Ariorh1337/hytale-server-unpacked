/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.jcajce.spec.PBKDF2ParameterSpec
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEKeySpec;

public class PBKDF2ParameterSpec
extends PBEKeySpec
implements AlgorithmParameterSpec {
    public PBKDF2ParameterSpec(char[] cArray) {
        super(cArray);
    }

    public PBKDF2ParameterSpec(char[] cArray, byte[] byArray, int n, int n2) {
        super(cArray, byArray, n, n2);
    }

    public PBKDF2ParameterSpec(char[] cArray, byte[] byArray, int n) {
        super(cArray, byArray, n);
    }
}

