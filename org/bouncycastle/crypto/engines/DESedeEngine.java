/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.DESBase;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;

public class DESedeEngine
extends DESBase
implements BlockCipher {
    protected static final int BLOCK_SIZE = 8;
    private int[] workingKey1 = null;
    private int[] workingKey2 = null;
    private int[] workingKey3 = null;
    private boolean forEncryption;

    public DESedeEngine() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity()));
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (!(cipherParameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to DESede init - " + cipherParameters.getClass().getName());
        }
        byte[] byArray = ((KeyParameter)cipherParameters).getKey();
        if (byArray.length != 24 && byArray.length != 16) {
            throw new IllegalArgumentException("key size must be 16 or 24 bytes.");
        }
        this.forEncryption = bl;
        byte[] byArray2 = new byte[8];
        System.arraycopy(byArray, 0, byArray2, 0, byArray2.length);
        this.workingKey1 = this.generateWorkingKey(bl, byArray2);
        byte[] byArray3 = new byte[8];
        System.arraycopy(byArray, 8, byArray3, 0, byArray3.length);
        this.workingKey2 = this.generateWorkingKey(!bl, byArray3);
        if (byArray.length == 24) {
            byte[] byArray4 = new byte[8];
            System.arraycopy(byArray, 16, byArray4, 0, byArray4.length);
            this.workingKey3 = this.generateWorkingKey(bl, byArray4);
        } else {
            this.workingKey3 = this.workingKey1;
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), this.bitsOfSecurity(), cipherParameters, Utils.getPurpose(this.forEncryption)));
    }

    @Override
    public String getAlgorithmName() {
        return "DESede";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public int processBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (this.workingKey1 == null) {
            throw new IllegalStateException("DESede engine not initialised");
        }
        if (n + 8 > byArray.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 8 > byArray2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        byte[] byArray3 = new byte[8];
        if (this.forEncryption) {
            this.desFunc(this.workingKey1, byArray, n, byArray3, 0);
            this.desFunc(this.workingKey2, byArray3, 0, byArray3, 0);
            this.desFunc(this.workingKey3, byArray3, 0, byArray2, n2);
        } else {
            this.desFunc(this.workingKey3, byArray, n, byArray3, 0);
            this.desFunc(this.workingKey2, byArray3, 0, byArray3, 0);
            this.desFunc(this.workingKey1, byArray3, 0, byArray2, n2);
        }
        return 8;
    }

    @Override
    public void reset() {
    }

    private int bitsOfSecurity() {
        if (this.workingKey1 != null && this.workingKey1 == this.workingKey3) {
            return 80;
        }
        return 112;
    }
}

