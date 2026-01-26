/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class SerpentEngineBase
implements BlockCipher {
    protected static final int BLOCK_SIZE = 16;
    static final int ROUNDS = 32;
    static final int PHI = -1640531527;
    protected boolean encrypting;
    protected int[] wKey;
    protected int keyBits;

    SerpentEngineBase() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        if (cipherParameters instanceof KeyParameter) {
            this.encrypting = bl;
            byte[] byArray = ((KeyParameter)cipherParameters).getKey();
            this.wKey = this.makeWorkingKey(byArray);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), byArray.length * 8, cipherParameters, this.getPurpose()));
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to " + this.getAlgorithmName() + " init - " + cipherParameters.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "Serpent";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public final int processBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (this.wKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (n + 16 > byArray.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (n2 + 16 > byArray2.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            this.encryptBlock(byArray, n, byArray2, n2);
        } else {
            this.decryptBlock(byArray, n, byArray2, n2);
        }
        return 16;
    }

    @Override
    public void reset() {
    }

    protected static int rotateLeft(int n, int n2) {
        return n << n2 | n >>> -n2;
    }

    protected static int rotateRight(int n, int n2) {
        return n >>> n2 | n << -n2;
    }

    protected final void sb0(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n ^ n4;
        int n6 = n3 ^ n5;
        int n7 = n2 ^ n6;
        nArray[3] = n & n4 ^ n7;
        int n8 = n ^ n2 & n5;
        nArray[2] = n7 ^ (n3 | n8);
        int n9 = nArray[3] & (n6 ^ n8);
        nArray[1] = ~n6 ^ n9;
        nArray[0] = n9 ^ ~n8;
    }

    protected final void ib0(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n;
        int n6 = n ^ n2;
        int n7 = n4 ^ (n5 | n6);
        int n8 = n3 ^ n7;
        nArray[2] = n6 ^ n8;
        int n9 = n5 ^ n4 & n6;
        nArray[1] = n7 ^ nArray[2] & n9;
        nArray[3] = n & n7 ^ (n8 | nArray[1]);
        nArray[0] = nArray[3] ^ (n8 ^ n9);
    }

    protected final void sb1(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n2 ^ ~n;
        int n6 = n3 ^ (n | n5);
        nArray[2] = n4 ^ n6;
        int n7 = n2 ^ (n4 | n5);
        int n8 = n5 ^ nArray[2];
        nArray[3] = n8 ^ n6 & n7;
        int n9 = n6 ^ n7;
        nArray[1] = nArray[3] ^ n9;
        nArray[0] = n6 ^ n8 & n9;
    }

    protected final void ib1(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n2 ^ n4;
        int n6 = n ^ n2 & n5;
        int n7 = n5 ^ n6;
        nArray[3] = n3 ^ n7;
        int n8 = n2 ^ n5 & n6;
        int n9 = nArray[3] | n8;
        nArray[1] = n6 ^ n9;
        int n10 = ~nArray[1];
        int n11 = nArray[3] ^ n8;
        nArray[0] = n10 ^ n11;
        nArray[2] = n7 ^ (n10 | n11);
    }

    protected final void sb2(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n;
        int n6 = n2 ^ n4;
        int n7 = n3 & n5;
        nArray[0] = n6 ^ n7;
        int n8 = n3 ^ n5;
        int n9 = n3 ^ nArray[0];
        int n10 = n2 & n9;
        nArray[3] = n8 ^ n10;
        nArray[2] = n ^ (n4 | n10) & (nArray[0] | n8);
        nArray[1] = n6 ^ nArray[3] ^ (nArray[2] ^ (n4 | n5));
    }

    protected final void ib2(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n2 ^ n4;
        int n6 = ~n5;
        int n7 = n ^ n3;
        int n8 = n3 ^ n5;
        int n9 = n2 & n8;
        nArray[0] = n7 ^ n9;
        int n10 = n | n6;
        int n11 = n4 ^ n10;
        int n12 = n7 | n11;
        nArray[3] = n5 ^ n12;
        int n13 = ~n8;
        int n14 = nArray[0] | nArray[3];
        nArray[1] = n13 ^ n14;
        nArray[2] = n4 & n13 ^ (n7 ^ n14);
    }

    protected final void sb3(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n ^ n2;
        int n6 = n & n3;
        int n7 = n | n4;
        int n8 = n3 ^ n4;
        int n9 = n5 & n7;
        int n10 = n6 | n9;
        nArray[2] = n8 ^ n10;
        int n11 = n2 ^ n7;
        int n12 = n10 ^ n11;
        int n13 = n8 & n12;
        nArray[0] = n5 ^ n13;
        int n14 = nArray[2] & nArray[0];
        nArray[1] = n12 ^ n14;
        nArray[3] = (n2 | n4) ^ (n8 ^ n14);
    }

    protected final void ib3(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n | n2;
        int n6 = n2 ^ n3;
        int n7 = n2 & n6;
        int n8 = n ^ n7;
        int n9 = n3 ^ n8;
        int n10 = n4 | n8;
        nArray[0] = n6 ^ n10;
        int n11 = n6 | n10;
        int n12 = n4 ^ n11;
        nArray[2] = n9 ^ n12;
        int n13 = n5 ^ n12;
        int n14 = nArray[0] & n13;
        nArray[3] = n8 ^ n14;
        nArray[1] = nArray[3] ^ (nArray[0] ^ n13);
    }

    protected final void sb4(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n ^ n4;
        int n6 = n4 & n5;
        int n7 = n3 ^ n6;
        int n8 = n2 | n7;
        nArray[3] = n5 ^ n8;
        int n9 = ~n2;
        int n10 = n5 | n9;
        nArray[0] = n7 ^ n10;
        int n11 = n & nArray[0];
        int n12 = n5 ^ n9;
        int n13 = n8 & n12;
        nArray[2] = n11 ^ n13;
        nArray[1] = n ^ n7 ^ n12 & nArray[2];
    }

    protected final void ib4(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n3 | n4;
        int n6 = n & n5;
        int n7 = n2 ^ n6;
        int n8 = n & n7;
        int n9 = n3 ^ n8;
        nArray[1] = n4 ^ n9;
        int n10 = ~n;
        int n11 = n9 & nArray[1];
        nArray[3] = n7 ^ n11;
        int n12 = nArray[1] | n10;
        int n13 = n4 ^ n12;
        nArray[0] = nArray[3] ^ n13;
        nArray[2] = n7 & n13 ^ (nArray[1] ^ n10);
    }

    protected final void sb5(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n;
        int n6 = n ^ n2;
        int n7 = n ^ n4;
        int n8 = n3 ^ n5;
        int n9 = n6 | n7;
        nArray[0] = n8 ^ n9;
        int n10 = n4 & nArray[0];
        int n11 = n6 ^ nArray[0];
        nArray[1] = n10 ^ n11;
        int n12 = n5 | nArray[0];
        int n13 = n6 | n10;
        int n14 = n7 ^ n12;
        nArray[2] = n13 ^ n14;
        nArray[3] = n2 ^ n10 ^ nArray[1] & n14;
    }

    protected final void ib5(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n3;
        int n6 = n2 & n5;
        int n7 = n4 ^ n6;
        int n8 = n & n7;
        int n9 = n2 ^ n5;
        nArray[3] = n8 ^ n9;
        int n10 = n2 | nArray[3];
        int n11 = n & n10;
        nArray[1] = n7 ^ n11;
        int n12 = n | n4;
        int n13 = n5 ^ n10;
        nArray[0] = n12 ^ n13;
        nArray[2] = n2 & n12 ^ (n8 | n ^ n3);
    }

    protected final void sb6(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n;
        int n6 = n ^ n4;
        int n7 = n2 ^ n6;
        int n8 = n5 | n6;
        int n9 = n3 ^ n8;
        nArray[1] = n2 ^ n9;
        int n10 = n6 | nArray[1];
        int n11 = n4 ^ n10;
        int n12 = n9 & n11;
        nArray[2] = n7 ^ n12;
        int n13 = n9 ^ n11;
        nArray[0] = nArray[2] ^ n13;
        nArray[3] = ~n9 ^ n7 & n13;
    }

    protected final void ib6(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = ~n;
        int n6 = n ^ n2;
        int n7 = n3 ^ n6;
        int n8 = n3 | n5;
        int n9 = n4 ^ n8;
        nArray[1] = n7 ^ n9;
        int n10 = n7 & n9;
        int n11 = n6 ^ n10;
        int n12 = n2 | n11;
        nArray[3] = n9 ^ n12;
        int n13 = n2 | nArray[3];
        nArray[0] = n11 ^ n13;
        nArray[2] = n4 & n5 ^ (n7 ^ n13);
    }

    protected final void sb7(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n2 ^ n3;
        int n6 = n3 & n5;
        int n7 = n4 ^ n6;
        int n8 = n ^ n7;
        int n9 = n4 | n5;
        int n10 = n8 & n9;
        nArray[1] = n2 ^ n10;
        int n11 = n7 | nArray[1];
        int n12 = n & n8;
        nArray[3] = n5 ^ n12;
        int n13 = n8 ^ n11;
        int n14 = nArray[3] & n13;
        nArray[2] = n7 ^ n14;
        nArray[0] = ~n13 ^ nArray[3] & nArray[2];
    }

    protected final void ib7(int[] nArray, int n, int n2, int n3, int n4) {
        int n5 = n3 | n & n2;
        int n6 = n4 & (n | n2);
        nArray[3] = n5 ^ n6;
        int n7 = ~n4;
        int n8 = n2 ^ n6;
        int n9 = n8 | nArray[3] ^ n7;
        nArray[1] = n ^ n9;
        nArray[0] = n3 ^ n8 ^ (n4 | nArray[1]);
        nArray[2] = n5 ^ nArray[1] ^ (nArray[0] ^ n & nArray[3]);
    }

    protected final void LT(int[] nArray) {
        int n = SerpentEngineBase.rotateLeft(nArray[0], 13);
        int n2 = SerpentEngineBase.rotateLeft(nArray[2], 3);
        int n3 = nArray[1] ^ n ^ n2;
        int n4 = nArray[3] ^ n2 ^ n << 3;
        nArray[1] = SerpentEngineBase.rotateLeft(n3, 1);
        nArray[3] = SerpentEngineBase.rotateLeft(n4, 7);
        nArray[0] = SerpentEngineBase.rotateLeft(n ^ nArray[1] ^ nArray[3], 5);
        nArray[2] = SerpentEngineBase.rotateLeft(n2 ^ nArray[3] ^ nArray[1] << 7, 22);
    }

    protected final void inverseLT(int[] nArray) {
        int n = SerpentEngineBase.rotateRight(nArray[2], 22) ^ nArray[3] ^ nArray[1] << 7;
        int n2 = SerpentEngineBase.rotateRight(nArray[0], 5) ^ nArray[1] ^ nArray[3];
        int n3 = SerpentEngineBase.rotateRight(nArray[3], 7);
        int n4 = SerpentEngineBase.rotateRight(nArray[1], 1);
        nArray[3] = n3 ^ n ^ n2 << 3;
        nArray[1] = n4 ^ n2 ^ n;
        nArray[2] = SerpentEngineBase.rotateRight(n, 3);
        nArray[0] = SerpentEngineBase.rotateRight(n2, 13);
    }

    protected abstract int[] makeWorkingKey(byte[] var1);

    protected abstract void encryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    protected abstract void decryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    private CryptoServicePurpose getPurpose() {
        if (this.wKey == null) {
            return CryptoServicePurpose.ANY;
        }
        return this.encrypting ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
    }
}

