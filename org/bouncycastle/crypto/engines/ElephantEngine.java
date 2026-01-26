/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.util.Arrays;
import org.bouncycastle.crypto.engines.AEADBaseEngine;
import org.bouncycastle.util.Bytes;

public class ElephantEngine
extends AEADBaseEngine {
    private byte[] npub;
    private byte[] expanded_key;
    private int nb_its;
    private byte[] ad;
    private int adOff;
    private int adlen;
    private final byte[] tag_buffer;
    private byte[] previous_mask;
    private byte[] current_mask;
    private byte[] next_mask;
    private final byte[] buffer;
    private final byte[] previous_outputMessage;
    private final Permutation instance;

    public ElephantEngine(ElephantParameters elephantParameters) {
        this.KEY_SIZE = 16;
        this.IV_SIZE = 12;
        switch (elephantParameters.ordinal()) {
            case 0: {
                this.BlockSize = 20;
                this.instance = new Dumbo();
                this.MAC_SIZE = 8;
                this.algorithmName = "Elephant 160 AEAD";
                break;
            }
            case 1: {
                this.BlockSize = 22;
                this.instance = new Jumbo();
                this.algorithmName = "Elephant 176 AEAD";
                this.MAC_SIZE = 8;
                break;
            }
            case 2: {
                this.BlockSize = 25;
                this.instance = new Delirium();
                this.algorithmName = "Elephant 200 AEAD";
                this.MAC_SIZE = 16;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid parameter settings for Elephant");
            }
        }
        this.tag_buffer = new byte[this.BlockSize];
        this.previous_mask = new byte[this.BlockSize];
        this.current_mask = new byte[this.BlockSize];
        this.next_mask = new byte[this.BlockSize];
        this.buffer = new byte[this.BlockSize];
        this.previous_outputMessage = new byte[this.BlockSize];
        this.setInnerMembers(AEADBaseEngine.ProcessingBufferType.Immediate, AEADBaseEngine.AADOperatorType.Stream, AEADBaseEngine.DataOperatorType.Counter);
    }

    private byte rotl(byte by) {
        return (byte)(by << 1 | (by & 0xFF) >>> 7);
    }

    private void lfsr_step() {
        this.instance.lfsr_step();
        System.arraycopy(this.current_mask, 1, this.next_mask, 0, this.BlockSize - 1);
    }

    @Override
    protected void init(byte[] byArray, byte[] byArray2) throws IllegalArgumentException {
        this.npub = byArray2;
        this.expanded_key = new byte[this.BlockSize];
        System.arraycopy(byArray, 0, this.expanded_key, 0, this.KEY_SIZE);
        this.instance.permutation(this.expanded_key);
    }

    @Override
    protected void processBufferEncrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.processBuffer(byArray, n, byArray2, n2, AEADBaseEngine.State.EncData);
        System.arraycopy(byArray2, n2, this.previous_outputMessage, 0, this.BlockSize);
    }

    private void processBuffer(byte[] byArray, int n, byte[] byArray2, int n2, AEADBaseEngine.State state) {
        if (this.m_state == AEADBaseEngine.State.DecInit || this.m_state == AEADBaseEngine.State.EncInit) {
            this.processFinalAAD();
        }
        this.lfsr_step();
        this.computeCipherBlock(byArray, n, this.BlockSize, byArray2, n2);
        if (this.nb_its > 0) {
            System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, this.BlockSize);
            this.absorbCiphertext();
        }
        if (this.m_state != state) {
            this.absorbAAD();
        }
        this.swapMasks();
        ++this.nb_its;
    }

    @Override
    protected void processBufferDecrypt(byte[] byArray, int n, byte[] byArray2, int n2) {
        this.processBuffer(byArray, n, byArray2, n2, AEADBaseEngine.State.DecData);
        System.arraycopy(byArray, n, this.previous_outputMessage, 0, this.BlockSize);
    }

    private void computeCipherBlock(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        System.arraycopy(this.npub, 0, this.buffer, 0, this.IV_SIZE);
        Arrays.fill(this.buffer, this.IV_SIZE, this.BlockSize, (byte)0);
        ElephantEngine.xorTo(this.BlockSize, this.current_mask, this.next_mask, this.buffer);
        this.instance.permutation(this.buffer);
        ElephantEngine.xorTo(this.BlockSize, this.current_mask, this.next_mask, this.buffer);
        Bytes.xorTo(n2, byArray, n, this.buffer);
        System.arraycopy(this.buffer, 0, byArray2, n3, n2);
    }

    private void swapMasks() {
        byte[] byArray = this.previous_mask;
        this.previous_mask = this.current_mask;
        this.current_mask = this.next_mask;
        this.next_mask = byArray;
    }

    private void absorbAAD() {
        this.processAADBytes(this.buffer);
        Bytes.xorTo(this.BlockSize, this.next_mask, this.buffer);
        this.instance.permutation(this.buffer);
        Bytes.xorTo(this.BlockSize, this.next_mask, this.buffer);
        Bytes.xorTo(this.BlockSize, this.buffer, this.tag_buffer);
    }

    private void absorbCiphertext() {
        ElephantEngine.xorTo(this.BlockSize, this.previous_mask, this.next_mask, this.buffer);
        this.instance.permutation(this.buffer);
        ElephantEngine.xorTo(this.BlockSize, this.previous_mask, this.next_mask, this.buffer);
        Bytes.xorTo(this.BlockSize, this.buffer, this.tag_buffer);
    }

    @Override
    protected void processFinalBlock(byte[] byArray, int n) {
        int n2 = this.dataOperator.getLen() - (this.forEncryption ? 0 : this.MAC_SIZE);
        this.processFinalAAD();
        int n3 = 1 + n2 / this.BlockSize;
        int n4 = n2 % this.BlockSize != 0 ? n3 : n3 - 1;
        int n5 = 1 + (this.IV_SIZE + this.adlen) / this.BlockSize;
        int n6 = Math.max(n3 + 1, n5 - 1);
        this.processBytes(this.m_buf, byArray, n, n6, n4, n3, n2, n5);
        Bytes.xorTo(this.BlockSize, this.expanded_key, this.tag_buffer);
        this.instance.permutation(this.tag_buffer);
        Bytes.xorTo(this.BlockSize, this.expanded_key, this.tag_buffer);
        System.arraycopy(this.tag_buffer, 0, this.mac, 0, this.MAC_SIZE);
    }

    @Override
    protected void processBufferAAD(byte[] byArray, int n) {
    }

    @Override
    public int getUpdateOutputSize(int n) {
        switch (this.m_state.ord) {
            case 0: {
                throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
            }
            case 4: 
            case 8: {
                return 0;
            }
            case 1: 
            case 2: 
            case 3: {
                int n2 = this.m_bufPos + n;
                return n2 - n2 % this.BlockSize;
            }
            case 5: 
            case 6: 
            case 7: {
                int n3 = Math.max(0, this.m_bufPos + n - this.MAC_SIZE);
                return n3 - n3 % this.BlockSize;
            }
        }
        return Math.max(0, n + this.m_bufPos - this.MAC_SIZE);
    }

    @Override
    public int getOutputSize(int n) {
        switch (this.m_state.ord) {
            case 0: {
                throw new IllegalArgumentException(this.algorithmName + " needs call init function before getUpdateOutputSize");
            }
            case 4: 
            case 8: {
                return 0;
            }
            case 1: 
            case 2: 
            case 3: {
                return n + this.m_bufPos + this.MAC_SIZE;
            }
        }
        return Math.max(0, n + this.m_bufPos - this.MAC_SIZE);
    }

    @Override
    protected void finishAAD(AEADBaseEngine.State state, boolean bl) {
        this.finishAAD2(state);
    }

    @Override
    protected void processFinalAAD() {
        if (this.adOff == -1) {
            this.ad = ((AEADBaseEngine.StreamAADOperator)this.aadOperator).getBytes();
            this.adOff = 0;
            this.adlen = this.aadOperator.getLen();
            this.aadOperator.reset();
        }
        switch (this.m_state.ord) {
            case 1: 
            case 5: {
                this.processAADBytes(this.tag_buffer);
            }
        }
    }

    @Override
    protected void reset(boolean bl) {
        super.reset(bl);
        Arrays.fill(this.tag_buffer, (byte)0);
        Arrays.fill(this.previous_outputMessage, (byte)0);
        this.nb_its = 0;
        this.adOff = -1;
    }

    @Override
    protected void checkAAD() {
        switch (this.m_state.ord) {
            case 7: {
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the plaintext to be processed exceeds the a block size");
            }
            case 3: {
                throw new IllegalArgumentException(this.algorithmName + " cannot process AAD when the length of the ciphertext to be processed exceeds the a block size");
            }
            case 4: {
                throw new IllegalArgumentException(this.algorithmName + " cannot be reused for encryption");
            }
        }
    }

    @Override
    protected boolean checkData(boolean bl) {
        switch (this.m_state.ord) {
            case 5: 
            case 6: 
            case 7: {
                return false;
            }
            case 1: 
            case 2: 
            case 3: {
                return true;
            }
            case 4: {
                throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
            }
        }
        throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
    }

    private void processAADBytes(byte[] byArray) {
        int n = 0;
        switch (this.m_state.ord) {
            case 5: {
                System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BlockSize);
                System.arraycopy(this.npub, 0, byArray, 0, this.IV_SIZE);
                n += this.IV_SIZE;
                this.m_state = AEADBaseEngine.State.DecAad;
                break;
            }
            case 1: {
                System.arraycopy(this.expanded_key, 0, this.current_mask, 0, this.BlockSize);
                System.arraycopy(this.npub, 0, byArray, 0, this.IV_SIZE);
                n += this.IV_SIZE;
                this.m_state = AEADBaseEngine.State.EncAad;
                break;
            }
            case 2: 
            case 6: {
                if (this.adOff != this.adlen) break;
                Arrays.fill(byArray, 0, this.BlockSize, (byte)0);
                byArray[0] = 1;
                return;
            }
        }
        int n2 = this.BlockSize - n;
        int n3 = this.adlen - this.adOff;
        if (n2 <= n3) {
            System.arraycopy(this.ad, this.adOff, byArray, n, n2);
            this.adOff += n2;
        } else {
            if (n3 > 0) {
                System.arraycopy(this.ad, this.adOff, byArray, n, n3);
                this.adOff += n3;
            }
            Arrays.fill(byArray, n + n3, n + n2, (byte)0);
            byArray[n + n3] = 1;
            switch (this.m_state.ord) {
                case 6: {
                    this.m_state = AEADBaseEngine.State.DecData;
                    break;
                }
                case 2: {
                    this.m_state = AEADBaseEngine.State.EncData;
                }
            }
        }
    }

    private void processBytes(byte[] byArray, byte[] byArray2, int n, int n2, int n3, int n4, int n5, int n6) {
        int n7;
        int n8 = 0;
        byte[] byArray3 = new byte[this.BlockSize];
        for (n7 = this.nb_its; n7 < n2; ++n7) {
            int n9 = n7 == n3 - 1 ? n5 - n7 * this.BlockSize : this.BlockSize;
            this.lfsr_step();
            if (n7 < n3) {
                this.computeCipherBlock(byArray, n8, n9, byArray2, n);
                if (this.forEncryption) {
                    System.arraycopy(this.buffer, 0, byArray3, 0, n9);
                } else {
                    System.arraycopy(byArray, n8, byArray3, 0, n9);
                }
                n += n9;
                n8 += n9;
            }
            if (n7 > 0 && n7 <= n4) {
                int n10 = (n7 - 1) * this.BlockSize;
                if (n10 == n5) {
                    Arrays.fill(this.buffer, 1, this.BlockSize, (byte)0);
                    this.buffer[0] = 1;
                } else {
                    int n11 = n5 - n10;
                    if (this.BlockSize <= n11) {
                        System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, this.BlockSize);
                    } else if (n11 > 0) {
                        System.arraycopy(this.previous_outputMessage, 0, this.buffer, 0, n11);
                        Arrays.fill(this.buffer, n11, this.BlockSize, (byte)0);
                        this.buffer[n11] = 1;
                    }
                }
                this.absorbCiphertext();
            }
            if (n7 + 1 < n6) {
                this.absorbAAD();
            }
            this.swapMasks();
            System.arraycopy(byArray3, 0, this.previous_outputMessage, 0, this.BlockSize);
        }
        this.nb_its = n7;
    }

    public static void xorTo(int n, byte[] byArray, byte[] byArray2, byte[] byArray3) {
        for (int i = 0; i < n; ++i) {
            int n2 = i;
            byArray3[n2] = (byte)(byArray3[n2] ^ (byArray[i] ^ byArray2[i]));
        }
    }

    private class Delirium
    implements Permutation {
        private static final int nRounds = 18;
        private final byte[] KeccakRoundConstants = new byte[]{1, -126, -118, 0, -117, 1, -127, 9, -118, -120, 9, 10, -117, -117, -119, 3, 2, -128};
        private final int[] KeccakRhoOffsets = new int[]{0, 1, 6, 4, 3, 4, 4, 6, 7, 4, 3, 2, 3, 1, 7, 1, 5, 7, 5, 0, 2, 2, 5, 0, 6};

        private Delirium() {
        }

        @Override
        public void permutation(byte[] byArray) {
            for (int i = 0; i < 18; ++i) {
                this.KeccakP200Round(byArray, i);
            }
        }

        @Override
        public void lfsr_step() {
            ((ElephantEngine)ElephantEngine.this).next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(ElephantEngine.this.rotl(ElephantEngine.this.current_mask[0]) ^ ElephantEngine.this.rotl(ElephantEngine.this.current_mask[2]) ^ ElephantEngine.this.current_mask[13] << 1);
        }

        private void KeccakP200Round(byte[] byArray, int n) {
            int n2;
            int n3;
            byte[] byArray2 = new byte[25];
            for (n3 = 0; n3 < 5; ++n3) {
                for (n2 = 0; n2 < 5; ++n2) {
                    int n4 = n3;
                    byArray2[n4] = (byte)(byArray2[n4] ^ byArray[this.index(n3, n2)]);
                }
            }
            for (n3 = 0; n3 < 5; ++n3) {
                byArray2[n3 + 5] = (byte)(this.ROL8(byArray2[(n3 + 1) % 5], 1) ^ byArray2[(n3 + 4) % 5]);
            }
            for (n3 = 0; n3 < 5; ++n3) {
                for (n2 = 0; n2 < 5; ++n2) {
                    int n5 = this.index(n3, n2);
                    byArray[n5] = (byte)(byArray[n5] ^ byArray2[n3 + 5]);
                }
            }
            for (n3 = 0; n3 < 5; ++n3) {
                for (n2 = 0; n2 < 5; ++n2) {
                    byArray2[this.index((int)n3, (int)n2)] = this.ROL8(byArray[this.index(n3, n2)], this.KeccakRhoOffsets[this.index(n3, n2)]);
                }
            }
            for (n3 = 0; n3 < 5; ++n3) {
                for (n2 = 0; n2 < 5; ++n2) {
                    byArray[this.index((int)n2, (int)((2 * n3 + 3 * n2) % 5))] = byArray2[this.index(n3, n2)];
                }
            }
            for (n2 = 0; n2 < 5; ++n2) {
                for (n3 = 0; n3 < 5; ++n3) {
                    byArray2[n3] = (byte)(byArray[this.index(n3, n2)] ^ ~byArray[this.index((n3 + 1) % 5, n2)] & byArray[this.index((n3 + 2) % 5, n2)]);
                }
                for (n3 = 0; n3 < 5; ++n3) {
                    byArray[this.index((int)n3, (int)n2)] = byArray2[n3];
                }
            }
            byArray[0] = (byte)(byArray[0] ^ this.KeccakRoundConstants[n]);
        }

        private byte ROL8(byte by, int n) {
            return (byte)(by << n | (by & 0xFF) >>> 8 - n);
        }

        private int index(int n, int n2) {
            return n + n2 * 5;
        }
    }

    private class Dumbo
    extends Spongent {
        public Dumbo() {
            super(160, 20, 80, (byte)117);
        }

        @Override
        public void lfsr_step() {
            ((ElephantEngine)ElephantEngine.this).next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(((ElephantEngine.this.current_mask[0] & 0xFF) << 3 | (ElephantEngine.this.current_mask[0] & 0xFF) >>> 5) ^ (ElephantEngine.this.current_mask[3] & 0xFF) << 7 ^ (ElephantEngine.this.current_mask[13] & 0xFF) >>> 7);
        }
    }

    public static enum ElephantParameters {
        elephant160,
        elephant176,
        elephant200;

    }

    private class Jumbo
    extends Spongent {
        public Jumbo() {
            super(176, 22, 90, (byte)69);
        }

        @Override
        public void lfsr_step() {
            ((ElephantEngine)ElephantEngine.this).next_mask[ElephantEngine.this.BlockSize - 1] = (byte)(ElephantEngine.this.rotl(ElephantEngine.this.current_mask[0]) ^ (ElephantEngine.this.current_mask[3] & 0xFF) << 7 ^ (ElephantEngine.this.current_mask[19] & 0xFF) >>> 7);
        }
    }

    private static interface Permutation {
        public void permutation(byte[] var1);

        public void lfsr_step();
    }

    private static abstract class Spongent
    implements Permutation {
        private final byte lfsrIV;
        private final int nRounds;
        private final int nBits;
        private final int nSBox;
        private final byte[] sBoxLayer = new byte[]{-18, -19, -21, -32, -30, -31, -28, -17, -25, -22, -24, -27, -23, -20, -29, -26, -34, -35, -37, -48, -46, -47, -44, -33, -41, -38, -40, -43, -39, -36, -45, -42, -66, -67, -69, -80, -78, -79, -76, -65, -73, -70, -72, -75, -71, -68, -77, -74, 14, 13, 11, 0, 2, 1, 4, 15, 7, 10, 8, 5, 9, 12, 3, 6, 46, 45, 43, 32, 34, 33, 36, 47, 39, 42, 40, 37, 41, 44, 35, 38, 30, 29, 27, 16, 18, 17, 20, 31, 23, 26, 24, 21, 25, 28, 19, 22, 78, 77, 75, 64, 66, 65, 68, 79, 71, 74, 72, 69, 73, 76, 67, 70, -2, -3, -5, -16, -14, -15, -12, -1, -9, -6, -8, -11, -7, -4, -13, -10, 126, 125, 123, 112, 114, 113, 116, 127, 119, 122, 120, 117, 121, 124, 115, 118, -82, -83, -85, -96, -94, -95, -92, -81, -89, -86, -88, -91, -87, -84, -93, -90, -114, -115, -117, -128, -126, -127, -124, -113, -121, -118, -120, -123, -119, -116, -125, -122, 94, 93, 91, 80, 82, 81, 84, 95, 87, 90, 88, 85, 89, 92, 83, 86, -98, -99, -101, -112, -110, -111, -108, -97, -105, -102, -104, -107, -103, -100, -109, -106, -50, -51, -53, -64, -62, -63, -60, -49, -57, -54, -56, -59, -55, -52, -61, -58, 62, 61, 59, 48, 50, 49, 52, 63, 55, 58, 56, 53, 57, 60, 51, 54, 110, 109, 107, 96, 98, 97, 100, 111, 103, 106, 104, 101, 105, 108, 99, 102};

        public Spongent(int n, int n2, int n3, byte by) {
            this.nRounds = n3;
            this.nSBox = n2;
            this.lfsrIV = by;
            this.nBits = n;
        }

        @Override
        public void permutation(byte[] byArray) {
            byte by = this.lfsrIV;
            byte[] byArray2 = new byte[this.nSBox];
            for (int i = 0; i < this.nRounds; ++i) {
                int n;
                byArray[0] = (byte)(byArray[0] ^ by);
                int n2 = this.nSBox - 1;
                byArray[n2] = (byte)(byArray[n2] ^ (byte)((by & 1) << 7 | (by & 2) << 5 | (by & 4) << 3 | (by & 8) << 1 | (by & 0x10) >>> 1 | (by & 0x20) >>> 3 | (by & 0x40) >>> 5 | (by & 0x80) >>> 7));
                by = (byte)((by << 1 | (0x40 & by) >>> 6 ^ (0x20 & by) >>> 5) & 0x7F);
                for (n = 0; n < this.nSBox; ++n) {
                    byArray[n] = this.sBoxLayer[byArray[n] & 0xFF];
                }
                Arrays.fill(byArray2, (byte)0);
                for (int j = 0; j < this.nSBox; ++j) {
                    for (int k = 0; k < 8; ++k) {
                        n = (j << 3) + k;
                        if (n != this.nBits - 1) {
                            n = (n * this.nBits >> 2) % (this.nBits - 1);
                        }
                        int n3 = n >>> 3;
                        byArray2[n3] = (byte)(byArray2[n3] ^ ((byArray[j] & 0xFF) >>> k & 1) << (n & 7));
                    }
                }
                System.arraycopy(byArray2, 0, byArray, 0, this.nSBox);
            }
        }
    }
}

