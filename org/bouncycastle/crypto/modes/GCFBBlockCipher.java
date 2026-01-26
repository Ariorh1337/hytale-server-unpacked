/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithSBox;

public class GCFBBlockCipher
extends StreamBlockCipher {
    private static final byte[] C = new byte[]{105, 0, 114, 34, 100, -55, 4, 35, -115, 58, -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 0, -19, 7, 18, -64, -122, -36, -62, -17, 76, -87, 43};
    private final CFBBlockCipher cfbEngine;
    private ParametersWithIV initParams;
    private KeyParameter key;
    private long counter = 0L;
    private boolean forEncryption;

    public GCFBBlockCipher(BlockCipher blockCipher) {
        super(blockCipher);
        this.cfbEngine = new CFBBlockCipher(blockCipher, blockCipher.getBlockSize() * 8);
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.counter = 0L;
        this.cfbEngine.init(bl, cipherParameters);
        byte[] byArray = null;
        this.forEncryption = bl;
        if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            cipherParameters = parametersWithIV.getParameters();
            byArray = parametersWithIV.getIV();
        }
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom)cipherParameters).getParameters();
        }
        if (cipherParameters instanceof ParametersWithSBox) {
            cipherParameters = ((ParametersWithSBox)cipherParameters).getParameters();
        }
        this.key = (KeyParameter)cipherParameters;
        if (this.key == null && this.initParams != null) {
            this.key = (KeyParameter)this.initParams.getParameters();
        }
        byArray = byArray == null && this.initParams != null ? this.initParams.getIV() : this.cfbEngine.getCurrentIV();
        this.initParams = new ParametersWithIV(this.key, byArray);
    }

    @Override
    public String getAlgorithmName() {
        String string = this.cfbEngine.getAlgorithmName();
        return string.substring(0, string.indexOf(47)) + "/G" + string.substring(string.indexOf(47) + 1);
    }

    @Override
    public int getBlockSize() {
        return this.cfbEngine.getBlockSize();
    }

    @Override
    public int processBlock(byte[] byArray, int n, byte[] byArray2, int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(byArray, n, this.cfbEngine.getBlockSize(), byArray2, n2);
        return this.cfbEngine.getBlockSize();
    }

    @Override
    protected byte calculateByte(byte by) {
        if (this.counter > 0L && (this.counter & 0x3FFL) == 0L) {
            BlockCipher blockCipher = this.cfbEngine.getUnderlyingCipher();
            blockCipher.init(false, this.key);
            byte[] byArray = new byte[32];
            int n = blockCipher.getBlockSize();
            for (int i = 0; i < byArray.length; i += n) {
                blockCipher.processBlock(C, i, byArray, i);
            }
            this.key = new KeyParameter(byArray);
            blockCipher.init(true, this.key);
            byte[] byArray2 = this.cfbEngine.getCurrentIV();
            blockCipher.processBlock(byArray2, 0, byArray2, 0);
            this.cfbEngine.init(this.forEncryption, new ParametersWithIV(this.key, byArray2));
        }
        ++this.counter;
        return this.cfbEngine.calculateByte(by);
    }

    @Override
    public void reset() {
        this.counter = 0L;
        if (this.initParams != null) {
            this.key = (KeyParameter)this.initParams.getParameters();
            this.cfbEngine.init(this.forEncryption, this.initParams);
        } else {
            this.cfbEngine.reset();
        }
    }
}

