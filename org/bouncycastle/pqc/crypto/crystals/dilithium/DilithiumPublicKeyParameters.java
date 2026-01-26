/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.util.Arrays;

public class DilithiumPublicKeyParameters
extends DilithiumKeyParameters {
    final byte[] rho;
    final byte[] t1;

    static byte[] getEncoded(byte[] byArray, byte[] byArray2) {
        return Arrays.concatenate(byArray, byArray2);
    }

    public DilithiumPublicKeyParameters(DilithiumParameters dilithiumParameters, byte[] byArray) {
        super(false, dilithiumParameters);
        this.rho = Arrays.copyOfRange(byArray, 0, 32);
        this.t1 = Arrays.copyOfRange(byArray, 32, byArray.length);
    }

    public DilithiumPublicKeyParameters(DilithiumParameters dilithiumParameters, byte[] byArray, byte[] byArray2) {
        super(false, dilithiumParameters);
        this.rho = Arrays.clone(byArray);
        this.t1 = Arrays.clone(byArray2);
    }

    public byte[] getEncoded() {
        return DilithiumPublicKeyParameters.getEncoded(this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }
}

