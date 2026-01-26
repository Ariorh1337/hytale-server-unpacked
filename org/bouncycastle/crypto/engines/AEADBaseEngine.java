/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

abstract class AEADBaseEngine
implements AEADCipher {
    protected boolean forEncryption;
    protected String algorithmName;
    protected int KEY_SIZE;
    protected int IV_SIZE;
    protected int MAC_SIZE;
    protected int macSizeLowerBound = 0;
    protected byte[] initialAssociatedText;
    protected byte[] mac;
    protected byte[] m_buf;
    protected byte[] m_aad;
    protected int m_bufPos;
    protected int m_aadPos;
    protected int AADBufferSize;
    protected int BlockSize;
    protected State m_state = State.Uninitialized;
    protected int m_bufferSizeDecrypt;
    protected AADProcessingBuffer processor;
    protected AADOperator aadOperator;
    protected DataOperator dataOperator;
    protected DecryptionFailureCounter decryptionFailureCounter = null;
    protected DataLimitCounter dataLimitCounter = null;

    AEADBaseEngine() {
    }

    @Override
    public String getAlgorithmName() {
        return this.algorithmName;
    }

    public int getKeyBytesSize() {
        return this.KEY_SIZE;
    }

    public int getIVBytesSize() {
        return this.IV_SIZE;
    }

    @Override
    public byte[] getMac() {
        return this.mac;
    }

    @Override
    public void init(boolean bl, CipherParameters cipherParameters) {
        byte[] byArray;
        KeyParameter keyParameter;
        this.forEncryption = bl;
        if (cipherParameters instanceof AEADParameters) {
            AEADParameters aEADParameters = (AEADParameters)cipherParameters;
            keyParameter = aEADParameters.getKey();
            byArray = aEADParameters.getNonce();
            this.initialAssociatedText = aEADParameters.getAssociatedText();
            int n = aEADParameters.getMacSize();
            if (this.macSizeLowerBound == 0) {
                if (n != this.MAC_SIZE << 3) {
                    throw new IllegalArgumentException("Invalid value for MAC size: " + n);
                }
            } else {
                if (n > 128 || n < this.macSizeLowerBound << 3 || (n & 7) != 0) {
                    throw new IllegalArgumentException("MAC size must be between " + (this.macSizeLowerBound << 3) + " and 128 bits for " + this.algorithmName);
                }
                this.MAC_SIZE = n >>> 3;
            }
        } else if (cipherParameters instanceof ParametersWithIV) {
            ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            keyParameter = (KeyParameter)parametersWithIV.getParameters();
            byArray = parametersWithIV.getIV();
            this.initialAssociatedText = null;
        } else {
            throw new IllegalArgumentException("invalid parameters passed to " + this.algorithmName);
        }
        if (keyParameter == null) {
            throw new IllegalArgumentException(this.algorithmName + " Init parameters must include a key");
        }
        if (byArray == null || byArray.length != this.IV_SIZE) {
            throw new IllegalArgumentException(this.algorithmName + " requires exactly " + this.IV_SIZE + " bytes of IV");
        }
        byte[] byArray2 = keyParameter.getKey();
        if (byArray2.length != this.KEY_SIZE) {
            throw new IllegalArgumentException(this.algorithmName + " key must be " + this.KEY_SIZE + " bytes long");
        }
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, cipherParameters, Utils.getPurpose(bl)));
        this.m_state = bl ? State.EncInit : State.DecInit;
        this.init(byArray2, byArray);
        if (this.dataLimitCounter != null) {
            this.dataLimitCounter.increment(byArray.length);
        }
        this.reset(true);
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }

    @Override
    public void reset() {
        this.reset(true);
    }

    protected void reset(boolean bl) {
        this.ensureInitialized();
        if (bl) {
            this.mac = null;
        }
        if (this.m_buf != null) {
            Arrays.fill(this.m_buf, (byte)0);
            this.m_bufPos = 0;
        }
        if (this.m_aad != null) {
            Arrays.fill(this.m_aad, (byte)0);
            this.m_aadPos = 0;
        }
        switch (this.m_state.ord) {
            case 1: 
            case 5: {
                break;
            }
            case 6: 
            case 7: 
            case 8: {
                this.m_state = State.DecFinal;
                break;
            }
            case 2: 
            case 3: 
            case 4: {
                this.m_state = State.EncFinal;
                return;
            }
            default: {
                throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
            }
        }
        this.aadOperator.reset();
        this.dataOperator.reset();
    }

    protected void setInnerMembers(ProcessingBufferType processingBufferType, AADOperatorType aADOperatorType, DataOperatorType dataOperatorType) {
        switch (processingBufferType.ord) {
            case 0: {
                this.processor = new BufferedAADProcessor();
                break;
            }
            case 1: {
                this.processor = new ImmediateAADProcessor();
            }
        }
        this.m_bufferSizeDecrypt = this.BlockSize + this.MAC_SIZE;
        switch (aADOperatorType.ord) {
            case 0: {
                this.m_aad = new byte[this.AADBufferSize];
                this.aadOperator = new DefaultAADOperator();
                break;
            }
            case 1: {
                this.m_aad = new byte[this.AADBufferSize];
                this.aadOperator = new CounterAADOperator();
                break;
            }
            case 2: {
                this.AADBufferSize = 0;
                this.aadOperator = new StreamAADOperator();
                break;
            }
            case 3: {
                this.m_aad = new byte[this.AADBufferSize];
                this.dataLimitCounter = new DataLimitCounter();
                this.aadOperator = new DataLimitAADOperator();
            }
        }
        switch (dataOperatorType.ord) {
            case 0: {
                this.m_buf = new byte[this.m_bufferSizeDecrypt];
                this.dataOperator = new DefaultDataOperator();
                break;
            }
            case 1: {
                this.m_buf = new byte[this.m_bufferSizeDecrypt];
                this.dataOperator = new CounterDataOperator();
                break;
            }
            case 2: {
                this.m_buf = new byte[this.MAC_SIZE];
                this.dataOperator = new StreamDataOperator();
                break;
            }
            case 3: {
                this.BlockSize = 0;
                this.m_buf = new byte[this.m_bufferSizeDecrypt];
                this.dataOperator = new StreamCipherOperator();
                break;
            }
            case 4: {
                this.m_buf = new byte[this.m_bufferSizeDecrypt];
                this.dataOperator = new DataLimitDataOperator();
            }
        }
    }

    @Override
    public void processAADByte(byte by) {
        this.checkAAD();
        this.aadOperator.processAADByte(by);
    }

    @Override
    public void processAADBytes(byte[] byArray, int n, int n2) {
        this.ensureSufficientInputBuffer(byArray, n, n2);
        if (n2 <= 0) {
            return;
        }
        this.checkAAD();
        this.aadOperator.processAADBytes(byArray, n, n2);
    }

    private void processAadBytes(byte[] byArray, int n, int n2) {
        if (this.m_aadPos > 0) {
            int n3 = this.AADBufferSize - this.m_aadPos;
            if (this.processor.isLengthWithinAvailableSpace(n2, n3)) {
                System.arraycopy(byArray, n, this.m_aad, this.m_aadPos, n2);
                this.m_aadPos += n2;
                return;
            }
            System.arraycopy(byArray, n, this.m_aad, this.m_aadPos, n3);
            n += n3;
            n2 -= n3;
            this.processBufferAAD(this.m_aad, 0);
        }
        while (this.processor.isLengthExceedingBlockSize(n2, this.AADBufferSize)) {
            this.processBufferAAD(byArray, n);
            n += this.AADBufferSize;
            n2 -= this.AADBufferSize;
        }
        System.arraycopy(byArray, n, this.m_aad, 0, n2);
        this.m_aadPos = n2;
    }

    @Override
    public int processByte(byte by, byte[] byArray, int n) throws DataLengthException {
        return this.dataOperator.processByte(by, byArray, n);
    }

    protected int processEncDecByte(byte[] byArray, int n) {
        int n2 = 0;
        int n3 = (this.forEncryption ? this.BlockSize : this.m_bufferSizeDecrypt) - this.m_bufPos;
        if (n3 == 0) {
            this.ensureSufficientOutputBuffer(byArray, n, this.BlockSize);
            if (this.forEncryption) {
                this.processBufferEncrypt(this.m_buf, 0, byArray, n);
            } else {
                this.processBufferDecrypt(this.m_buf, 0, byArray, n);
                System.arraycopy(this.m_buf, this.BlockSize, this.m_buf, 0, this.m_bufPos - this.BlockSize);
            }
            this.m_bufPos -= this.BlockSize;
            n2 = this.BlockSize;
        }
        return n2;
    }

    @Override
    public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) throws DataLengthException {
        this.ensureSufficientInputBuffer(byArray, n, n2);
        return this.dataOperator.processBytes(byArray, n, n2, byArray2, n3);
    }

    protected int processEncDecBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
        boolean bl = this.checkData(false);
        int n4 = (bl ? this.BlockSize : this.m_bufferSizeDecrypt) - this.m_bufPos;
        if (this.processor.isLengthWithinAvailableSpace(n2, n4)) {
            System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n2);
            this.m_bufPos += n2;
            return 0;
        }
        int n5 = this.processor.getUpdateOutputSize(n2);
        int n6 = n5 + this.m_bufPos - (bl ? 0 : this.MAC_SIZE);
        this.ensureSufficientOutputBuffer(byArray2, n3, n6 - n6 % this.BlockSize);
        n6 = 0;
        if (byArray == byArray2 && Arrays.segmentsOverlap(n, n2, n3, n5)) {
            byArray = new byte[n2];
            System.arraycopy(byArray2, n, byArray, 0, n2);
            n = 0;
        }
        if (bl) {
            if (this.m_bufPos > 0) {
                System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n4);
                n += n4;
                n2 -= n4;
                this.processBufferEncrypt(this.m_buf, 0, byArray2, n3);
                n6 = this.BlockSize;
            }
            while (this.processor.isLengthExceedingBlockSize(n2, this.BlockSize)) {
                this.processBufferEncrypt(byArray, n, byArray2, n3 + n6);
                n += this.BlockSize;
                n2 -= this.BlockSize;
                n6 += this.BlockSize;
            }
        } else {
            while (this.processor.isLengthExceedingBlockSize(this.m_bufPos, this.BlockSize) && this.processor.isLengthExceedingBlockSize(n2 + this.m_bufPos, this.m_bufferSizeDecrypt)) {
                this.processBufferDecrypt(this.m_buf, n6, byArray2, n3 + n6);
                this.m_bufPos -= this.BlockSize;
                n6 += this.BlockSize;
            }
            if (this.m_bufPos > 0) {
                System.arraycopy(this.m_buf, n6, this.m_buf, 0, this.m_bufPos);
                if (this.processor.isLengthWithinAvailableSpace(this.m_bufPos + n2, this.m_bufferSizeDecrypt)) {
                    System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n2);
                    this.m_bufPos += n2;
                    return n6;
                }
                n4 = Math.max(this.BlockSize - this.m_bufPos, 0);
                System.arraycopy(byArray, n, this.m_buf, this.m_bufPos, n4);
                n += n4;
                n2 -= n4;
                this.processBufferDecrypt(this.m_buf, 0, byArray2, n3 + n6);
                n6 += this.BlockSize;
            }
            while (this.processor.isLengthExceedingBlockSize(n2, this.m_bufferSizeDecrypt)) {
                this.processBufferDecrypt(byArray, n, byArray2, n3 + n6);
                n += this.BlockSize;
                n2 -= this.BlockSize;
                n6 += this.BlockSize;
            }
        }
        System.arraycopy(byArray, n, this.m_buf, 0, n2);
        this.m_bufPos = n2;
        return n6;
    }

    @Override
    public int doFinal(byte[] byArray, int n) throws IllegalStateException, InvalidCipherTextException {
        int n2;
        boolean bl = this.checkData(true);
        if (bl) {
            n2 = this.m_bufPos + this.MAC_SIZE;
        } else {
            if (this.m_bufPos < this.MAC_SIZE) {
                throw new InvalidCipherTextException("data too short");
            }
            this.m_bufPos -= this.MAC_SIZE;
            n2 = this.m_bufPos;
        }
        this.ensureSufficientOutputBuffer(byArray, n, n2);
        this.mac = new byte[this.MAC_SIZE];
        this.processFinalBlock(byArray, n);
        if (bl) {
            System.arraycopy(this.mac, 0, byArray, n + n2 - this.MAC_SIZE, this.MAC_SIZE);
        } else if (!Arrays.constantTimeAreEqual(this.MAC_SIZE, this.mac, 0, this.m_buf, this.m_bufPos)) {
            if (this.decryptionFailureCounter != null && this.decryptionFailureCounter.increment()) {
                throw new InvalidCipherTextException(this.algorithmName + " decryption failure limit exceeded");
            }
            throw new InvalidCipherTextException(this.algorithmName + " mac does not match");
        }
        this.reset(!bl);
        return n2;
    }

    public final int getBlockSize() {
        return this.BlockSize;
    }

    @Override
    public int getUpdateOutputSize(int n) {
        int n2 = this.getTotalBytesForUpdate(n);
        return n2 - n2 % this.BlockSize;
    }

    protected int getTotalBytesForUpdate(int n) {
        int n2 = this.processor.getUpdateOutputSize(n);
        switch (this.m_state.ord) {
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                n2 = Math.max(0, n2 + this.m_bufPos - this.MAC_SIZE);
                break;
            }
            case 3: 
            case 4: {
                n2 = Math.max(0, n2 + this.m_bufPos);
                break;
            }
        }
        return n2;
    }

    @Override
    public int getOutputSize(int n) {
        int n2 = Math.max(0, n);
        switch (this.m_state.ord) {
            case 5: 
            case 6: 
            case 7: 
            case 8: {
                return Math.max(0, n2 + this.m_bufPos - this.MAC_SIZE);
            }
            case 3: 
            case 4: {
                return n2 + this.m_bufPos + this.MAC_SIZE;
            }
        }
        return n2 + this.MAC_SIZE;
    }

    protected void checkAAD() {
        switch (this.m_state.ord) {
            case 5: {
                this.m_state = State.DecAad;
                break;
            }
            case 1: {
                this.m_state = State.EncAad;
                break;
            }
            case 2: 
            case 6: {
                break;
            }
            case 4: {
                throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
            }
            default: {
                throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
            }
        }
    }

    protected boolean checkData(boolean bl) {
        switch (this.m_state.ord) {
            case 5: 
            case 6: {
                this.finishAAD(State.DecData, bl);
                return false;
            }
            case 1: 
            case 2: {
                this.finishAAD(State.EncData, bl);
                return true;
            }
            case 7: {
                return false;
            }
            case 3: {
                return true;
            }
            case 4: {
                throw new IllegalStateException(this.getAlgorithmName() + " cannot be reused for encryption");
            }
        }
        throw new IllegalStateException(this.getAlgorithmName() + " needs to be initialized");
    }

    protected final void ensureSufficientOutputBuffer(byte[] byArray, int n, int n2) {
        if (n + n2 > byArray.length) {
            throw new OutputLengthException("output buffer too short");
        }
    }

    protected final void ensureSufficientInputBuffer(byte[] byArray, int n, int n2) {
        if (n + n2 > byArray.length) {
            throw new DataLengthException("input buffer too short");
        }
    }

    protected final void ensureInitialized() {
        if (this.m_state == State.Uninitialized) {
            throw new IllegalStateException("Need to call init function before operation");
        }
    }

    protected void finishAAD1(State state) {
        switch (this.m_state.ord) {
            case 1: 
            case 2: 
            case 5: 
            case 6: {
                this.processFinalAAD();
                break;
            }
        }
        this.m_state = state;
    }

    protected void finishAAD2(State state) {
        switch (this.m_state.ord) {
            case 2: 
            case 6: {
                this.processFinalAAD();
                break;
            }
        }
        this.m_aadPos = 0;
        this.m_state = state;
    }

    protected void finishAAD3(State state, boolean bl) {
        switch (this.m_state.ord) {
            case 5: 
            case 6: {
                if (!bl && this.dataOperator.getLen() <= this.MAC_SIZE) {
                    return;
                }
            }
            case 1: 
            case 2: {
                this.processFinalAAD();
            }
        }
        this.m_aadPos = 0;
        this.m_state = state;
    }

    protected abstract void finishAAD(State var1, boolean var2);

    protected abstract void init(byte[] var1, byte[] var2);

    protected abstract void processFinalBlock(byte[] var1, int var2);

    protected abstract void processBufferAAD(byte[] var1, int var2);

    protected abstract void processFinalAAD();

    protected abstract void processBufferEncrypt(byte[] var1, int var2, byte[] var3, int var4);

    protected abstract void processBufferDecrypt(byte[] var1, int var2, byte[] var3, int var4);

    protected static interface AADOperator {
        public void processAADByte(byte var1);

        public void processAADBytes(byte[] var1, int var2, int var3);

        public void reset();

        public int getLen();
    }

    protected static class AADOperatorType {
        public static final int DEFAULT = 0;
        public static final int COUNTER = 1;
        public static final int STREAM = 2;
        public static final int DATA_LIMIT = 3;
        public static final AADOperatorType Default = new AADOperatorType(0);
        public static final AADOperatorType Counter = new AADOperatorType(1);
        public static final AADOperatorType Stream = new AADOperatorType(2);
        public static final AADOperatorType DataLimit = new AADOperatorType(3);
        private final int ord;

        AADOperatorType(int n) {
            this.ord = n;
        }
    }

    private static interface AADProcessingBuffer {
        public void processAADByte(byte var1);

        public int processByte(byte var1, byte[] var2, int var3);

        public int getUpdateOutputSize(int var1);

        public boolean isLengthWithinAvailableSpace(int var1, int var2);

        public boolean isLengthExceedingBlockSize(int var1, int var2);
    }

    private class BufferedAADProcessor
    implements AADProcessingBuffer {
        private BufferedAADProcessor() {
        }

        @Override
        public void processAADByte(byte by) {
            if (AEADBaseEngine.this.m_aadPos == AEADBaseEngine.this.AADBufferSize) {
                AEADBaseEngine.this.processBufferAAD(AEADBaseEngine.this.m_aad, 0);
                AEADBaseEngine.this.m_aadPos = 0;
            }
            AEADBaseEngine.this.m_aad[AEADBaseEngine.this.m_aadPos++] = by;
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            AEADBaseEngine.this.checkData(false);
            int n2 = AEADBaseEngine.this.processEncDecByte(byArray, n);
            AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = by;
            return n2;
        }

        @Override
        public boolean isLengthWithinAvailableSpace(int n, int n2) {
            return n <= n2;
        }

        @Override
        public boolean isLengthExceedingBlockSize(int n, int n2) {
            return n > n2;
        }

        @Override
        public int getUpdateOutputSize(int n) {
            return Math.max(0, n) - 1;
        }
    }

    private class CounterAADOperator
    implements AADOperator {
        private int aadLen;

        private CounterAADOperator() {
        }

        @Override
        public void processAADByte(byte by) {
            ++this.aadLen;
            AEADBaseEngine.this.processor.processAADByte(by);
        }

        @Override
        public void processAADBytes(byte[] byArray, int n, int n2) {
            this.aadLen += n2;
            AEADBaseEngine.this.processAadBytes(byArray, n, n2);
        }

        @Override
        public int getLen() {
            return this.aadLen;
        }

        @Override
        public void reset() {
            this.aadLen = 0;
        }
    }

    private class CounterDataOperator
    implements DataOperator {
        private int messegeLen;

        private CounterDataOperator() {
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            ++this.messegeLen;
            return AEADBaseEngine.this.processor.processByte(by, byArray, n);
        }

        @Override
        public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
            this.messegeLen += n2;
            return AEADBaseEngine.this.processEncDecBytes(byArray, n, n2, byArray2, n3);
        }

        @Override
        public int getLen() {
            return this.messegeLen;
        }

        @Override
        public void reset() {
            this.messegeLen = 0;
        }
    }

    private class DataLimitAADOperator
    implements AADOperator {
        private DataLimitAADOperator() {
        }

        @Override
        public void processAADByte(byte by) {
            AEADBaseEngine.this.dataLimitCounter.increment();
            AEADBaseEngine.this.processor.processAADByte(by);
        }

        @Override
        public void processAADBytes(byte[] byArray, int n, int n2) {
            AEADBaseEngine.this.dataLimitCounter.increment(n2);
            AEADBaseEngine.this.processAadBytes(byArray, n, n2);
        }

        @Override
        public void reset() {
        }

        @Override
        public int getLen() {
            return AEADBaseEngine.this.m_aadPos;
        }
    }

    protected static class DataLimitCounter {
        private long count;
        private long max;
        private int n;

        protected DataLimitCounter() {
        }

        public void init(int n) {
            this.n = n;
            this.max = 1L << n;
        }

        public void increment() {
            if (++this.count > this.max) {
                throw new IllegalStateException("Total data limit exceeded: maximum 2^" + this.n + " bytes per key (including nonce, AAD, and message)");
            }
        }

        public void increment(int n) {
            this.count += (long)n;
            if (this.count > this.max) {
                throw new IllegalStateException("Total data limit exceeded: maximum 2^" + n + " bytes per key (including nonce, AAD, and message)");
            }
        }

        public void reset() {
            this.count = 0L;
        }
    }

    private class DataLimitDataOperator
    implements DataOperator {
        private DataLimitDataOperator() {
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            AEADBaseEngine.this.dataLimitCounter.increment();
            return AEADBaseEngine.this.processor.processByte(by, byArray, n);
        }

        @Override
        public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
            AEADBaseEngine.this.dataLimitCounter.increment(n2);
            return AEADBaseEngine.this.processEncDecBytes(byArray, n, n2, byArray2, n3);
        }

        @Override
        public int getLen() {
            return AEADBaseEngine.this.m_bufPos;
        }

        @Override
        public void reset() {
        }
    }

    protected static interface DataOperator {
        public int processByte(byte var1, byte[] var2, int var3);

        public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5);

        public int getLen();

        public void reset();
    }

    protected static class DataOperatorType {
        public static final int DEFAULT = 0;
        public static final int COUNTER = 1;
        public static final int STREAM = 2;
        public static final int STREAM_CIPHER = 3;
        public static final int DATA_LIMIT = 4;
        public static final DataOperatorType Default = new DataOperatorType(0);
        public static final DataOperatorType Counter = new DataOperatorType(1);
        public static final DataOperatorType Stream = new DataOperatorType(2);
        public static final DataOperatorType StreamCipher = new DataOperatorType(3);
        public static final DataOperatorType DataLimit = new DataOperatorType(4);
        private final int ord;

        DataOperatorType(int n) {
            this.ord = n;
        }
    }

    protected static class DecryptionFailureCounter {
        private int n;
        private int[] counter;

        protected DecryptionFailureCounter() {
        }

        public void init(int n) {
            if (this.n != n) {
                this.n = n;
                int n2 = n + 31 >>> 5;
                if (this.counter == null || n2 != this.counter.length) {
                    this.counter = new int[n2];
                } else {
                    this.reset();
                }
            }
        }

        public boolean increment() {
            int n = this.counter.length;
            while (--n >= 0) {
                int n2 = n;
                this.counter[n2] = this.counter[n2] + 1;
                if (this.counter[n2] == 0) continue;
            }
            int n3 = this.n & 0x1F;
            return n <= 0 && this.counter[0] == (n3 == 0 ? 0 : 1 << n3);
        }

        public void reset() {
            Arrays.fill(this.counter, 0);
        }
    }

    private class DefaultAADOperator
    implements AADOperator {
        private DefaultAADOperator() {
        }

        @Override
        public void processAADByte(byte by) {
            AEADBaseEngine.this.processor.processAADByte(by);
        }

        @Override
        public void processAADBytes(byte[] byArray, int n, int n2) {
            AEADBaseEngine.this.processAadBytes(byArray, n, n2);
        }

        @Override
        public void reset() {
        }

        @Override
        public int getLen() {
            return AEADBaseEngine.this.m_aadPos;
        }
    }

    private class DefaultDataOperator
    implements DataOperator {
        private DefaultDataOperator() {
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            return AEADBaseEngine.this.processor.processByte(by, byArray, n);
        }

        @Override
        public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
            return AEADBaseEngine.this.processEncDecBytes(byArray, n, n2, byArray2, n3);
        }

        @Override
        public int getLen() {
            return AEADBaseEngine.this.m_bufPos;
        }

        @Override
        public void reset() {
        }
    }

    protected static final class ErasableOutputStream
    extends ByteArrayOutputStream {
        public byte[] getBuf() {
            return this.buf;
        }
    }

    private class ImmediateAADProcessor
    implements AADProcessingBuffer {
        private ImmediateAADProcessor() {
        }

        @Override
        public void processAADByte(byte by) {
            AEADBaseEngine.this.m_aad[AEADBaseEngine.this.m_aadPos++] = by;
            if (AEADBaseEngine.this.m_aadPos == AEADBaseEngine.this.AADBufferSize) {
                AEADBaseEngine.this.processBufferAAD(AEADBaseEngine.this.m_aad, 0);
                AEADBaseEngine.this.m_aadPos = 0;
            }
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            AEADBaseEngine.this.checkData(false);
            AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = by;
            return AEADBaseEngine.this.processEncDecByte(byArray, n);
        }

        @Override
        public int getUpdateOutputSize(int n) {
            return Math.max(0, n);
        }

        @Override
        public boolean isLengthWithinAvailableSpace(int n, int n2) {
            return n < n2;
        }

        @Override
        public boolean isLengthExceedingBlockSize(int n, int n2) {
            return n >= n2;
        }
    }

    protected static class ProcessingBufferType {
        public static final int BUFFERED = 0;
        public static final int IMMEDIATE = 1;
        public static final ProcessingBufferType Buffered = new ProcessingBufferType(0);
        public static final ProcessingBufferType Immediate = new ProcessingBufferType(1);
        private final int ord;

        ProcessingBufferType(int n) {
            this.ord = n;
        }
    }

    protected static class State {
        public static final int UNINITIALIZED = 0;
        public static final int ENC_INIT = 1;
        public static final int ENC_AAD = 2;
        public static final int ENC_DATA = 3;
        public static final int ENC_FINAL = 4;
        public static final int DEC_INIT = 5;
        public static final int DEC_AAD = 6;
        public static final int DEC_DATA = 7;
        public static final int DEC_FINAL = 8;
        public static final State Uninitialized = new State(0);
        public static final State EncInit = new State(1);
        public static final State EncAad = new State(2);
        public static final State EncData = new State(3);
        public static final State EncFinal = new State(4);
        public static final State DecInit = new State(5);
        public static final State DecAad = new State(6);
        public static final State DecData = new State(7);
        public static final State DecFinal = new State(8);
        final int ord;

        State(int n) {
            this.ord = n;
        }
    }

    protected static class StreamAADOperator
    implements AADOperator {
        private final ErasableOutputStream stream = new ErasableOutputStream();

        protected StreamAADOperator() {
        }

        @Override
        public void processAADByte(byte by) {
            this.stream.write(by);
        }

        @Override
        public void processAADBytes(byte[] byArray, int n, int n2) {
            this.stream.write(byArray, n, n2);
        }

        public byte[] getBytes() {
            return this.stream.getBuf();
        }

        @Override
        public void reset() {
            this.stream.reset();
        }

        @Override
        public int getLen() {
            return this.stream.size();
        }
    }

    private class StreamCipherOperator
    implements DataOperator {
        private int len;

        private StreamCipherOperator() {
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            boolean bl = AEADBaseEngine.this.checkData(false);
            if (bl) {
                this.len = 1;
                AEADBaseEngine.this.processBufferEncrypt(new byte[]{by}, 0, byArray, n);
                return 1;
            }
            if (AEADBaseEngine.this.m_bufPos == AEADBaseEngine.this.MAC_SIZE) {
                this.len = 1;
                AEADBaseEngine.this.processBufferDecrypt(AEADBaseEngine.this.m_buf, 0, byArray, n);
                System.arraycopy(AEADBaseEngine.this.m_buf, 1, AEADBaseEngine.this.m_buf, 0, AEADBaseEngine.this.m_bufPos - 1);
                AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos - 1] = by;
                return 1;
            }
            AEADBaseEngine.this.m_buf[AEADBaseEngine.this.m_bufPos++] = by;
            return 0;
        }

        @Override
        public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
            boolean bl;
            if (byArray == byArray2 && Arrays.segmentsOverlap(n, n2, n3, AEADBaseEngine.this.processor.getUpdateOutputSize(n2))) {
                byArray = new byte[n2];
                System.arraycopy(byArray2, n, byArray, 0, n2);
                n = 0;
            }
            if (bl = AEADBaseEngine.this.checkData(false)) {
                this.len = n2;
                AEADBaseEngine.this.processBufferEncrypt(byArray, n, byArray2, n3);
                return n2;
            }
            int n4 = Math.max(AEADBaseEngine.this.m_bufPos + n2 - AEADBaseEngine.this.MAC_SIZE, 0);
            int n5 = 0;
            if (AEADBaseEngine.this.m_bufPos > 0) {
                n5 = this.len = Math.min(n4, AEADBaseEngine.this.m_bufPos);
                AEADBaseEngine.this.processBufferDecrypt(AEADBaseEngine.this.m_buf, 0, byArray2, n3);
                n4 -= n5;
                AEADBaseEngine.this.m_bufPos -= n5;
                System.arraycopy(AEADBaseEngine.this.m_buf, n5, AEADBaseEngine.this.m_buf, 0, AEADBaseEngine.this.m_bufPos);
            }
            if (n4 > 0) {
                this.len = n4;
                AEADBaseEngine.this.processBufferDecrypt(byArray, n, byArray2, n3);
                n5 += n4;
                n2 -= n4;
                n += n4;
            }
            System.arraycopy(byArray, n, AEADBaseEngine.this.m_buf, AEADBaseEngine.this.m_bufPos, n2);
            AEADBaseEngine.this.m_bufPos += n2;
            return n5;
        }

        @Override
        public int getLen() {
            return this.len;
        }

        @Override
        public void reset() {
        }
    }

    protected class StreamDataOperator
    implements DataOperator {
        private final ErasableOutputStream stream = new ErasableOutputStream();

        protected StreamDataOperator() {
        }

        @Override
        public int processByte(byte by, byte[] byArray, int n) {
            AEADBaseEngine.this.checkData(false);
            AEADBaseEngine.this.ensureInitialized();
            this.stream.write(by);
            AEADBaseEngine.this.m_bufPos = this.stream.size();
            return 0;
        }

        @Override
        public int processBytes(byte[] byArray, int n, int n2, byte[] byArray2, int n3) {
            AEADBaseEngine.this.checkData(false);
            AEADBaseEngine.this.ensureInitialized();
            this.stream.write(byArray, n, n2);
            AEADBaseEngine.this.m_bufPos = this.stream.size();
            return 0;
        }

        public byte[] getBytes() {
            return this.stream.getBuf();
        }

        @Override
        public int getLen() {
            return this.stream.size();
        }

        @Override
        public void reset() {
            this.stream.reset();
        }
    }
}

