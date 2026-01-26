/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.modes.GCMModeCipher;
import org.bouncycastle.crypto.modes.gcm.BasicGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

public class GCMBlockCipher
implements GCMModeCipher {
    private static final int BLOCK_SIZE = 16;
    private BlockCipher cipher;
    private GCMMultiplier multiplier;
    private GCMExponentiator exp;
    private boolean forEncryption;
    private boolean initialised;
    private int macSize;
    private byte[] lastKey;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private byte[] H;
    private byte[] J0;
    private byte[] bufBlock;
    private byte[] macBlock;
    private byte[] S;
    private byte[] S_at;
    private byte[] S_atPre;
    private byte[] counter;
    private int blocksRemaining;
    private int bufOff;
    private long totalLength;
    private byte[] atBlock;
    private int atBlockPos;
    private long atLength;
    private long atLengthPre;

    public static GCMModeCipher newInstance(BlockCipher blockCipher) {
        return new GCMBlockCipher(blockCipher);
    }

    public static GCMModeCipher newInstance(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        return new GCMBlockCipher(blockCipher, gCMMultiplier);
    }

    public GCMBlockCipher(BlockCipher blockCipher) {
        this(blockCipher, null);
    }

    public GCMBlockCipher(BlockCipher blockCipher, GCMMultiplier gCMMultiplier) {
        if (blockCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        if (gCMMultiplier == null) {
            gCMMultiplier = new Tables4kGCMMultiplier();
        }
        this.cipher = blockCipher;
        this.multiplier = gCMMultiplier;
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    @Override
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) throws IllegalArgumentException {
        KeyParameter keyParameter;
        CipherParameters cipherParameters2;
        this.forEncryption = bl;
        this.macBlock = null;
        this.initialised = true;
        byte[] byArray = null;
        if (cipherParameters instanceof AEADParameters) {
            cipherParameters2 = (AEADParameters)cipherParameters;
            byArray = ((AEADParameters)cipherParameters2).getNonce();
            this.initialAssociatedText = ((AEADParameters)cipherParameters2).getAssociatedText();
            int n = ((AEADParameters)cipherParameters2).getMacSize();
            if (n < 32 || n > 128 || n % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + n);
            }
            this.macSize = n / 8;
            keyParameter = ((AEADParameters)cipherParameters2).getKey();
        } else if (cipherParameters instanceof ParametersWithIV) {
            cipherParameters2 = (ParametersWithIV)cipherParameters;
            byArray = ((ParametersWithIV)cipherParameters2).getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            keyParameter = (KeyParameter)((ParametersWithIV)cipherParameters2).getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM");
        }
        int n = bl ? 16 : 16 + this.macSize;
        this.bufBlock = new byte[n];
        if (byArray == null || byArray.length < 1) {
            throw new IllegalArgumentException("IV must be at least 1 byte");
        }
        if (bl && this.nonce != null && Arrays.areEqual(this.nonce, byArray)) {
            if (keyParameter == null) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
            if (this.lastKey != null && Arrays.areEqual(this.lastKey, keyParameter.getKey())) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
        }
        this.nonce = byArray;
        if (keyParameter != null) {
            this.lastKey = keyParameter.getKey();
        }
        if (keyParameter != null) {
            this.cipher.init(true, keyParameter);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
        } else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
        }
        this.J0 = new byte[16];
        if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        } else {
            this.gHASH(this.J0, this.nonce, this.nonce.length);
            byte[] byArray2 = new byte[16];
            Pack.longToBigEndian((long)this.nonce.length * 8L, byArray2, 8);
            this.gHASHBlock(this.J0, byArray2);
        }
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }

    @Override
    public int getOutputSize(int n) {
        int n2 = n + this.bufOff;
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return n2 < this.macSize ? 0 : n2 - this.macSize;
    }

    @Override
    public int getUpdateOutputSize(int n) {
        int n2 = n + this.bufOff;
        if (!this.forEncryption) {
            if (n2 < this.macSize) {
                return 0;
            }
            n2 -= this.macSize;
        }
        return n2 - n2 % 16;
    }

    @Override
    public void processAADByte(byte by) {
        this.checkStatus();
        this.atBlock[this.atBlockPos] = by;
        if (++this.atBlockPos == 16) {
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16L;
        }
    }

    @Override
    public void processAADBytes(byte[] byArray, int n, int n2) {
        int n3;
        this.checkStatus();
        if (this.atBlockPos > 0) {
            n3 = 16 - this.atBlockPos;
            if (n2 < n3) {
                System.arraycopy(byArray, n, this.atBlock, this.atBlockPos, n2);
                this.atBlockPos += n2;
                return;
            }
            System.arraycopy(byArray, n, this.atBlock, this.atBlockPos, n3);
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atLength += 16L;
            n += n3;
            n2 -= n3;
        }
        n3 = n + n2 - 16;
        while (n <= n3) {
            this.gHASHBlock(this.S_at, byArray, n);
            this.atLength += 16L;
            n += 16;
        }
        this.atBlockPos = 16 + n3 - n;
        System.arraycopy(byArray, n, this.atBlock, 0, this.atBlockPos);
    }

    private void initCipher() {
        if (this.atLength > 0L) {
            System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
            this.atLengthPre = this.atLength;
        }
        if (this.atBlockPos > 0) {
            this.gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
            this.atLengthPre += (long)this.atBlockPos;
        }
        if (this.atLengthPre > 0L) {
            System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
        }
    }

    @Override
    public int processByte(byte by, byte[] byArray, int n) throws DataLengthException {
        this.checkStatus();
        this.bufBlock[this.bufOff] = by;
        if (++this.bufOff == this.bufBlock.length) {
            if (this.forEncryption) {
                this.encryptBlock(this.bufBlock, 0, byArray, n);
                this.bufOff = 0;
            } else {
                this.decryptBlock(this.bufBlock, 0, byArray, n);
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
                this.bufOff = this.macSize;
            }
            return 16;
        }
        return 0;
    }

    @Override
    public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws DataLengthException {
        this.checkStatus();
        if (byArray.length - n < n2) {
            throw new DataLengthException("Input buffer too short");
        }
        if (byArray == byArray2 && Arrays.segmentsOverlap(n, n2, n3, this.getUpdateOutputSize(n2))) {
            byArray = new byte[n2];
            System.arraycopy(byArray2, n, byArray, 0, n2);
            n = 0;
        }
        int n4 = 0;
        if (this.forEncryption) {
            int n5;
            if (this.bufOff > 0) {
                n5 = 16 - this.bufOff;
                if (n2 < n5) {
                    System.arraycopy(byArray, n, this.bufBlock, this.bufOff, n2);
                    this.bufOff += n2;
                    return 0;
                }
                System.arraycopy(byArray, n, this.bufBlock, this.bufOff, n5);
                this.encryptBlock(this.bufBlock, 0, byArray2, n3);
                n += n5;
                n2 -= n5;
                n4 = 16;
            }
            n5 = n + n2 - 16;
            while (n <= n5) {
                this.encryptBlock(byArray, n, byArray2, n3 + n4);
                n += 16;
                n4 += 16;
            }
            this.bufOff = 16 + n5 - n;
            System.arraycopy(byArray, n, this.bufBlock, 0, this.bufOff);
        } else {
            int n6 = this.bufBlock.length - this.bufOff;
            if (n2 < n6) {
                System.arraycopy(byArray, n, this.bufBlock, this.bufOff, n2);
                this.bufOff += n2;
                return 0;
            }
            if (this.bufOff >= 16) {
                this.decryptBlock(this.bufBlock, 0, byArray2, n3);
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.bufOff -= 16);
                n4 = 16;
                if (n2 < (n6 += 16)) {
                    System.arraycopy(byArray, n, this.bufBlock, this.bufOff, n2);
                    this.bufOff += n2;
                    return n4;
                }
            }
            int n7 = n + n2 - this.bufBlock.length;
            n6 = 16 - this.bufOff;
            System.arraycopy(byArray, n, this.bufBlock, this.bufOff, n6);
            this.decryptBlock(this.bufBlock, 0, byArray2, n3 + n4);
            n += n6;
            n4 += 16;
            while (n <= n7) {
                this.decryptBlock(byArray, n, byArray2, n3 + n4);
                n += 16;
                n4 += 16;
            }
            this.bufOff = this.bufBlock.length + n7 - n;
            System.arraycopy(byArray, n, this.bufBlock, 0, this.bufOff);
        }
        return n4;
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws IllegalStateException, InvalidCipherTextException {
        this.checkStatus();
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        int n2 = this.bufOff;
        if (this.forEncryption) {
            if (byArray.length - n < n2 + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
        } else {
            if (n2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            if (byArray.length - n < (n2 -= this.macSize)) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        if (n2 > 0) {
            this.processPartial(this.bufBlock, 0, n2, byArray, n);
        }
        this.atLength += (long)this.atBlockPos;
        if (this.atLength > this.atLengthPre) {
            if (this.atBlockPos > 0) {
                this.gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
            }
            if (this.atLengthPre > 0L) {
                GCMUtil.xor(this.S_at, this.S_atPre);
            }
            long l = this.totalLength * 8L + 127L >>> 7;
            byte[] byArray2 = new byte[16];
            if (this.exp == null) {
                this.exp = new BasicGCMExponentiator();
                this.exp.init(this.H);
            }
            this.exp.exponentiateX(l, byArray2);
            GCMUtil.multiply(this.S_at, byArray2);
            GCMUtil.xor(this.S, this.S_at);
        }
        byte[] byArray3 = new byte[16];
        Pack.longToBigEndian(this.atLength * 8L, byArray3, 0);
        Pack.longToBigEndian(this.totalLength * 8L, byArray3, 8);
        this.gHASHBlock(this.S, byArray3);
        byte[] byArray4 = new byte[16];
        this.cipher.processBlock(this.J0, 0, byArray4, 0);
        GCMUtil.xor(byArray4, this.S);
        int n3 = n2;
        this.macBlock = new byte[this.macSize];
        System.arraycopy(byArray4, 0, this.macBlock, 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, byArray, n + this.bufOff, this.macSize);
            n3 += this.macSize;
        } else {
            byte[] byArray5 = new byte[this.macSize];
            System.arraycopy(this.bufBlock, n2, byArray5, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, byArray5)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        this.reset(false);
        return n3;
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    private void reset(boolean bl) {
        this.cipher.reset();
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte)0);
        }
        if (bl) {
            this.macBlock = null;
        }
        if (this.forEncryption) {
            this.initialised = false;
        } else if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    private void decryptBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (byArray2.length - n2 < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        byte[] byArray3 = new byte[16];
        this.getNextCTRBlock(byArray3);
        this.gHASHBlock(this.S, byArray, n);
        GCMUtil.xor(byArray3, 0, byArray, n, byArray2, n2);
        this.totalLength += 16L;
    }

    private void encryptBlock(byte[] byArray, int n, byte[] byArray2, int n2) {
        if (byArray2.length - n2 < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        byte[] byArray3 = new byte[16];
        this.getNextCTRBlock(byArray3);
        GCMUtil.xor(byArray3, byArray, n);
        this.gHASHBlock(this.S, byArray3);
        System.arraycopy(byArray3, 0, byArray2, n2, 16);
        this.totalLength += 16L;
    }

    private void processPartial(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        byte[] byArray3 = new byte[16];
        this.getNextCTRBlock(byArray3);
        if (this.forEncryption) {
            GCMUtil.xor(byArray, n, byArray3, 0, n2);
            this.gHASHPartial(this.S, byArray, n, n2);
        } else {
            this.gHASHPartial(this.S, byArray, n, n2);
            GCMUtil.xor(byArray, n, byArray3, 0, n2);
        }
        System.arraycopy(byArray, n, byArray2, n3, n2);
        this.totalLength += (long)n2;
    }

    private void gHASH(byte[] byArray, byte[] byArray2, int n) {
        for (int i = 0; i < n; i += 16) {
            int n2 = Math.min(n - i, 16);
            this.gHASHPartial(byArray, byArray2, i, n2);
        }
    }

    private void gHASHBlock(byte[] byArray, byte[] byArray2) {
        GCMUtil.xor(byArray, byArray2);
        this.multiplier.multiplyH(byArray);
    }

    private void gHASHBlock(byte[] byArray, byte[] byArray2, int n) {
        GCMUtil.xor(byArray, byArray2, n);
        this.multiplier.multiplyH(byArray);
    }

    private void gHASHPartial(byte[] byArray, byte[] byArray2, int n, int n2) {
        GCMUtil.xor(byArray, byArray2, n, n2);
        this.multiplier.multiplyH(byArray);
    }

    private void getNextCTRBlock(byte[] byArray) {
        if (this.blocksRemaining == 0) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        --this.blocksRemaining;
        int n = 1;
        this.counter[15] = (byte)(n += this.counter[15] & 0xFF);
        n >>>= 8;
        this.counter[14] = (byte)(n += this.counter[14] & 0xFF);
        n >>>= 8;
        this.counter[13] = (byte)(n += this.counter[13] & 0xFF);
        n >>>= 8;
        this.counter[12] = (byte)(n += this.counter[12] & 0xFF);
        this.cipher.processBlock(this.counter, 0, byArray, 0);
    }

    private void checkStatus() {
        if (!this.initialised) {
            if (this.forEncryption) {
                throw new IllegalStateException("GCM cipher cannot be reused for encryption");
            }
            throw new IllegalStateException("GCM cipher needs to be initialised");
        }
    }
}

