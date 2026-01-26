/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.sphincsplus;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class ADRS {
    static final int WOTS_HASH = 0;
    static final int WOTS_PK = 1;
    static final int TREE = 2;
    static final int FORS_TREE = 3;
    static final int FORS_PK = 4;
    static final int WOTS_PRF = 5;
    static final int FORS_PRF = 6;
    static final int OFFSET_LAYER = 0;
    static final int OFFSET_TREE = 4;
    static final int OFFSET_TREE_HGT = 24;
    static final int OFFSET_TREE_INDEX = 28;
    static final int OFFSET_TYPE = 16;
    static final int OFFSET_KP_ADDR = 20;
    static final int OFFSET_CHAIN_ADDR = 24;
    static final int OFFSET_HASH_ADDR = 28;
    final byte[] value = new byte[32];

    ADRS() {
    }

    ADRS(ADRS aDRS) {
        System.arraycopy(aDRS.value, 0, this.value, 0, aDRS.value.length);
    }

    public void setLayerAddress(int n) {
        Pack.intToBigEndian(n, this.value, 0);
    }

    public int getLayerAddress() {
        return Pack.bigEndianToInt(this.value, 0);
    }

    public void setTreeAddress(long l) {
        Pack.longToBigEndian(l, this.value, 8);
    }

    public long getTreeAddress() {
        return Pack.bigEndianToLong(this.value, 8);
    }

    public void setTreeHeight(int n) {
        Pack.intToBigEndian(n, this.value, 24);
    }

    public void setTreeIndex(int n) {
        Pack.intToBigEndian(n, this.value, 28);
    }

    public int getTreeIndex() {
        return Pack.bigEndianToInt(this.value, 28);
    }

    public void setTypeAndClear(int n) {
        Pack.intToBigEndian(n, this.value, 16);
        Arrays.fill(this.value, 20, this.value.length, (byte)0);
    }

    public void changeType(int n) {
        Pack.intToBigEndian(n, this.value, 16);
    }

    public void setKeyPairAddress(int n) {
        Pack.intToBigEndian(n, this.value, 20);
    }

    public int getKeyPairAddress() {
        return Pack.bigEndianToInt(this.value, 20);
    }

    public void setHashAddress(int n) {
        Pack.intToBigEndian(n, this.value, 28);
    }

    public void setChainAddress(int n) {
        Pack.intToBigEndian(n, this.value, 24);
    }
}

