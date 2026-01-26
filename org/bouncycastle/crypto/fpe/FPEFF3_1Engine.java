/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.fpe.FPEEngine;
import org.bouncycastle.crypto.fpe.SP80038G;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Properties;

public class FPEFF3_1Engine
extends FPEEngine {
    public FPEFF3_1Engine() {
        this(AESEngine.newInstance());
    }

    public FPEFF3_1Engine(BlockCipher blockCipher) {
        super(blockCipher);
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("base cipher needs to be 128 bits");
        }
        if (Properties.isOverrideSet("org.bouncycastle.fpe.disable")) {
            throw new UnsupportedOperationException("FPE disabled");
        }
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        this.forEncryption = bl;
        this.fpeParameters = (FPEParameters)cipherParameters;
        this.baseCipher.init(!this.fpeParameters.isUsingInverseFunction(), this.fpeParameters.getKey().reverse());
        if (this.fpeParameters.getTweak().length != 7) {
            throw new IllegalArgumentException("tweak should be 56 bits");
        }
    }

    @Override
    public String getAlgorithmName() {
        return "FF3-1";
    }

    @Override
    protected int encryptBlock(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        byte[] byArray3 = this.fpeParameters.getRadix() > 256 ? FPEFF3_1Engine.toByteArray(SP80038G.encryptFF3_1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), FPEFF3_1Engine.toShortArray(byArray), n, n2 / 2)) : SP80038G.encryptFF3_1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, n2);
        return n2;
    }

    @Override
    protected int decryptBlock(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        byte[] byArray3 = this.fpeParameters.getRadix() > 256 ? FPEFF3_1Engine.toByteArray(SP80038G.decryptFF3_1w(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), FPEFF3_1Engine.toShortArray(byArray), n, n2 / 2)) : SP80038G.decryptFF3_1(this.baseCipher, this.fpeParameters.getRadixConverter(), this.fpeParameters.getTweak(), byArray, n, n2);
        System.arraycopy(byArray3, 0, byArray2, n3, n2);
        return n2;
    }
}

