/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.EncodingType;
import org.rocksdb.TableFormatConfig;

public class PlainTableConfig
extends TableFormatConfig {
    public static final int VARIABLE_LENGTH = 0;
    public static final int DEFAULT_BLOOM_BITS_PER_KEY = 10;
    public static final double DEFAULT_HASH_TABLE_RATIO = 0.75;
    public static final int DEFAULT_INDEX_SPARSENESS = 16;
    public static final int DEFAULT_HUGE_TLB_SIZE = 0;
    public static final EncodingType DEFAULT_ENCODING_TYPE = EncodingType.kPlain;
    public static final boolean DEFAULT_FULL_SCAN_MODE = false;
    public static final boolean DEFAULT_STORE_INDEX_IN_FILE = false;
    private int keySize_ = 0;
    private int bloomBitsPerKey_ = 10;
    private double hashTableRatio_ = 0.75;
    private int indexSparseness_ = 16;
    private int hugePageTlbSize_ = 0;
    private EncodingType encodingType_ = DEFAULT_ENCODING_TYPE;
    private boolean fullScanMode_ = false;
    private boolean storeIndexInFile_ = false;

    public PlainTableConfig setKeySize(int n) {
        this.keySize_ = n;
        return this;
    }

    public int keySize() {
        return this.keySize_;
    }

    public PlainTableConfig setBloomBitsPerKey(int n) {
        this.bloomBitsPerKey_ = n;
        return this;
    }

    public int bloomBitsPerKey() {
        return this.bloomBitsPerKey_;
    }

    public PlainTableConfig setHashTableRatio(double d) {
        this.hashTableRatio_ = d;
        return this;
    }

    public double hashTableRatio() {
        return this.hashTableRatio_;
    }

    public PlainTableConfig setIndexSparseness(int n) {
        this.indexSparseness_ = n;
        return this;
    }

    public long indexSparseness() {
        return this.indexSparseness_;
    }

    public PlainTableConfig setHugePageTlbSize(int n) {
        this.hugePageTlbSize_ = n;
        return this;
    }

    public int hugePageTlbSize() {
        return this.hugePageTlbSize_;
    }

    public PlainTableConfig setEncodingType(EncodingType encodingType) {
        this.encodingType_ = encodingType;
        return this;
    }

    public EncodingType encodingType() {
        return this.encodingType_;
    }

    public PlainTableConfig setFullScanMode(boolean bl) {
        this.fullScanMode_ = bl;
        return this;
    }

    public boolean fullScanMode() {
        return this.fullScanMode_;
    }

    public PlainTableConfig setStoreIndexInFile(boolean bl) {
        this.storeIndexInFile_ = bl;
        return this;
    }

    public boolean storeIndexInFile() {
        return this.storeIndexInFile_;
    }

    @Override
    protected long newTableFactoryHandle() {
        return PlainTableConfig.newTableFactoryHandle(this.keySize_, this.bloomBitsPerKey_, this.hashTableRatio_, this.indexSparseness_, this.hugePageTlbSize_, this.encodingType_.getValue(), this.fullScanMode_, this.storeIndexInFile_);
    }

    private static native long newTableFactoryHandle(int var0, int var1, double var2, int var4, int var5, byte var6, boolean var7, boolean var8);
}

