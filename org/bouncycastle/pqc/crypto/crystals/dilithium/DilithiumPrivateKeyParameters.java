/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumEngine;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumKeyParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumParameters;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.util.Arrays;

public class DilithiumPrivateKeyParameters
extends DilithiumKeyParameters {
    final byte[] rho;
    final byte[] k;
    final byte[] tr;
    final byte[] s1;
    final byte[] s2;
    final byte[] t0;
    private final byte[] t1;

    public DilithiumPrivateKeyParameters(DilithiumParameters dilithiumParameters, byte[] byArray, byte[] byArray2, byte[] byArray3, byte[] byArray4, byte[] byArray5, byte[] byArray6, byte[] byArray7) {
        super(true, dilithiumParameters);
        this.rho = Arrays.clone(byArray);
        this.k = Arrays.clone(byArray2);
        this.tr = Arrays.clone(byArray3);
        this.s1 = Arrays.clone(byArray4);
        this.s2 = Arrays.clone(byArray5);
        this.t0 = Arrays.clone(byArray6);
        this.t1 = Arrays.clone(byArray7);
    }

    public DilithiumPrivateKeyParameters(DilithiumParameters dilithiumParameters, byte[] byArray, DilithiumPublicKeyParameters dilithiumPublicKeyParameters) {
        super(true, dilithiumParameters);
        DilithiumEngine dilithiumEngine = dilithiumParameters.getEngine(null);
        int n = 0;
        this.rho = Arrays.copyOfRange(byArray, 0, 32);
        this.k = Arrays.copyOfRange(byArray, n += 32, n + 32);
        this.tr = Arrays.copyOfRange(byArray, n += 32, n + 64);
        int n2 = dilithiumEngine.getDilithiumL() * dilithiumEngine.getDilithiumPolyEtaPackedBytes();
        this.s1 = Arrays.copyOfRange(byArray, n += 64, n + n2);
        n += n2;
        n2 = dilithiumEngine.getDilithiumK() * dilithiumEngine.getDilithiumPolyEtaPackedBytes();
        this.s2 = Arrays.copyOfRange(byArray, n, n + n2);
        n += n2;
        n2 = dilithiumEngine.getDilithiumK() * 416;
        this.t0 = Arrays.copyOfRange(byArray, n, n + n2);
        n += n2;
        this.t1 = (byte[])(dilithiumPublicKeyParameters != null ? dilithiumPublicKeyParameters.getT1() : null);
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(new byte[][]{this.rho, this.k, this.tr, this.s1, this.s2, this.t0});
    }

    public byte[] getK() {
        return Arrays.clone(this.k);
    }

    @Deprecated
    public byte[] getPrivateKey() {
        return this.getEncoded();
    }

    public byte[] getPublicKey() {
        return DilithiumPublicKeyParameters.getEncoded(this.rho, this.t1);
    }

    public DilithiumPublicKeyParameters getPublicKeyParameters() {
        return new DilithiumPublicKeyParameters(this.getParameters(), this.rho, this.t1);
    }

    public byte[] getRho() {
        return Arrays.clone(this.rho);
    }

    public byte[] getS1() {
        return Arrays.clone(this.s1);
    }

    public byte[] getS2() {
        return Arrays.clone(this.s2);
    }

    public byte[] getT0() {
        return Arrays.clone(this.t0);
    }

    public byte[] getT1() {
        return Arrays.clone(this.t1);
    }

    public byte[] getTr() {
        return Arrays.clone(this.tr);
    }
}

