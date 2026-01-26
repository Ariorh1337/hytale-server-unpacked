/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class VMPCEngine
implements StreamCipher {
    protected byte n = 0;
    protected byte[] P = null;
    protected byte s = 0;
    protected byte[] workingIV;
    protected byte[] workingKey;

    @Override
    public String getAlgorithmName() {
        return "VMPC";
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof ParametersWithIV)) {
            throw new IllegalArgumentException("VMPC init parameters must include an IV");
        }
        ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
        if (!(parametersWithIV.getParameters() instanceof KeyParameter)) {
            throw new IllegalArgumentException("VMPC init parameters must include a key");
        }
        KeyParameter keyParameter = (KeyParameter)parametersWithIV.getParameters();
        this.workingIV = parametersWithIV.getIV();
        if (this.workingIV == null || this.workingIV.length < 1 || this.workingIV.length > 768) {
            throw new IllegalArgumentException("VMPC requires 1 to 768 bytes of IV");
        }
        this.workingKey = keyParameter.getKey();
        this.initKey(this.workingKey, this.workingIV);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.workingKey.length >= 32 ? 256 : this.workingKey.length * 8, cipherParameters, Utils.getPurpose(bl)));
    }

    protected void initKey(byte[] byArray, byte[] byArray2) {
        byte by;
        int n;
        this.s = 0;
        this.P = new byte[256];
        for (n = 0; n < 256; ++n) {
            this.P[n] = (byte)n;
        }
        for (n = 0; n < 768; ++n) {
            this.s = this.P[this.s + this.P[n & 0xFF] + byArray[n % byArray.length] & 0xFF];
            by = this.P[n & 0xFF];
            this.P[n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = by;
        }
        for (n = 0; n < 768; ++n) {
            this.s = this.P[this.s + this.P[n & 0xFF] + byArray2[n % byArray2.length] & 0xFF];
            by = this.P[n & 0xFF];
            this.P[n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = by;
        }
        this.n = 0;
    }

    @Override
    public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        if (n + n2 > byArray.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n3 + n2 > byArray2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        for (int i = 0; i < n2; ++i) {
            this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
            byte by = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
            byte by2 = this.P[this.n & 0xFF];
            this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
            this.P[this.s & 0xFF] = by2;
            this.n = (byte)(this.n + 1 & 0xFF);
            byArray2[i + n3] = (byte)(byArray[i + n] ^ by);
        }
        return n2;
    }

    @Override
    public void reset() {
        this.initKey(this.workingKey, this.workingIV);
    }

    @Override
    public byte returnByte(byte by) {
        this.s = this.P[this.s + this.P[this.n & 0xFF] & 0xFF];
        byte by2 = this.P[this.P[this.P[this.s & 0xFF] & 0xFF] + 1 & 0xFF];
        byte by3 = this.P[this.n & 0xFF];
        this.P[this.n & 0xFF] = this.P[this.s & 0xFF];
        this.P[this.s & 0xFF] = by3;
        this.n = (byte)(this.n + 1 & 0xFF);
        return (byte)(by ^ by2);
    }
}

