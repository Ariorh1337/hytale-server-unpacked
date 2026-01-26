/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.picnic;

import java.util.logging.Logger;
import org.bouncycastle.pqc.crypto.picnic.PicnicEngine;
import org.bouncycastle.pqc.crypto.picnic.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class Tree {
    private static final Logger LOG = Logger.getLogger(Tree.class.getName());
    private static final int MAX_SEED_SIZE_BYTES = 32;
    private int depth;
    byte[][] nodes;
    private int dataSize;
    private boolean[] haveNode;
    private boolean[] exists;
    private int numNodes;
    private int numLeaves;
    private PicnicEngine engine;

    protected byte[][] getLeaves() {
        return this.nodes;
    }

    protected int getLeavesOffset() {
        return this.numNodes - this.numLeaves;
    }

    public Tree(PicnicEngine picnicEngine, int n, int n2) {
        int n3;
        this.engine = picnicEngine;
        this.depth = Utils.ceil_log2(n) + 1;
        this.numNodes = (1 << this.depth) - 1 - ((1 << this.depth - 1) - n);
        this.numLeaves = n;
        this.dataSize = n2;
        this.nodes = new byte[this.numNodes][n2];
        for (n3 = 0; n3 < this.numNodes; ++n3) {
            this.nodes[n3] = new byte[n2];
        }
        this.haveNode = new boolean[this.numNodes];
        this.exists = new boolean[this.numNodes];
        Arrays.fill(this.exists, this.numNodes - this.numLeaves, this.numNodes, true);
        for (n3 = this.numNodes - this.numLeaves; n3 > 0; --n3) {
            if (!this.exists(2 * n3 + 1) && !this.exists(2 * n3 + 2)) continue;
            this.exists[n3] = true;
        }
        this.exists[0] = true;
    }

    protected void buildMerkleTree(byte[][] byArray, byte[] byArray2) {
        int n;
        int n2 = this.numNodes - this.numLeaves;
        for (n = 0; n < this.numLeaves; ++n) {
            if (byArray[n] == null) continue;
            System.arraycopy(byArray[n], 0, this.nodes[n2 + n], 0, this.dataSize);
            this.haveNode[n2 + n] = true;
        }
        for (n = this.numNodes; n > 0; --n) {
            this.computeParentHash(n, byArray2);
        }
    }

    protected int verifyMerkleTree(byte[][] byArray, byte[] byArray2) {
        int n;
        int n2 = this.numNodes - this.numLeaves;
        for (n = 0; n < this.numLeaves; ++n) {
            if (byArray[n] == null) continue;
            if (this.haveNode[n2 + n]) {
                return -1;
            }
            if (byArray[n] == null) continue;
            System.arraycopy(byArray[n], 0, this.nodes[n2 + n], 0, this.dataSize);
            this.haveNode[n2 + n] = true;
        }
        for (n = this.numNodes; n > 0; --n) {
            this.computeParentHash(n, byArray2);
        }
        if (!this.haveNode[0]) {
            return -1;
        }
        return 0;
    }

    protected int reconstructSeeds(int[] nArray, int n, byte[] byArray, int n2, byte[] byArray2, int n3) {
        int n4 = 0;
        int n5 = n2;
        int[] nArray2 = new int[]{0};
        int[] nArray3 = this.getRevealedNodes(nArray, n, nArray2);
        for (int i = 0; i < nArray2[0]; ++i) {
            if ((n5 -= this.engine.seedSizeBytes) < 0) {
                return -1;
            }
            System.arraycopy(byArray, i * this.engine.seedSizeBytes, this.nodes[nArray3[i]], 0, this.engine.seedSizeBytes);
            this.haveNode[nArray3[i]] = true;
        }
        this.expandSeeds(byArray2, n3);
        return n4;
    }

    protected byte[] openMerkleTree(int[] nArray, int n, int[] nArray2) {
        byte[] byArray;
        int[] nArray3 = new int[1];
        int[] nArray4 = this.getRevealedMerkleNodes(nArray, n, nArray3);
        nArray2[0] = nArray3[0] * this.dataSize;
        byte[] byArray2 = byArray = new byte[nArray2[0]];
        for (int i = 0; i < nArray3[0]; ++i) {
            System.arraycopy(this.nodes[nArray4[i]], 0, byArray, i * this.dataSize, this.dataSize);
        }
        return byArray2;
    }

    private int[] getRevealedNodes(int[] nArray, int n, int[] nArray2) {
        int n2;
        int n3;
        int n4 = this.depth - 1;
        int[][] nArray3 = new int[n4][n];
        for (int i = 0; i < n; ++i) {
            n3 = 0;
            nArray3[n3][i] = n2 = nArray[i] + (this.numNodes - this.numLeaves);
            ++n3;
            while ((n2 = this.getParent(n2)) != 0) {
                nArray3[n3][i] = n2;
                ++n3;
            }
        }
        int[] nArray4 = new int[this.numLeaves];
        n3 = 0;
        for (n2 = 0; n2 < n4; ++n2) {
            for (int i = 0; i < n; ++i) {
                int n5;
                if (!this.hasSibling(nArray3[n2][i]) || this.contains(nArray3[n2], n, n5 = this.getSibling(nArray3[n2][i]))) continue;
                while (!this.hasRightChild(n5) && !this.isLeafNode(n5)) {
                    n5 = 2 * n5 + 1;
                }
                if (this.contains(nArray4, n3, n5)) continue;
                nArray4[n3] = n5;
                ++n3;
            }
        }
        nArray2[0] = n3;
        return nArray4;
    }

    private int getSibling(int n) {
        if (this.isLeftChild(n)) {
            if (n + 1 < this.numNodes) {
                return n + 1;
            }
            LOG.fine("getSibling: request for node with not sibling");
            return 0;
        }
        return n - 1;
    }

    private boolean isLeafNode(int n) {
        return 2 * n + 1 >= this.numNodes;
    }

    private boolean hasSibling(int n) {
        if (!this.exists(n)) {
            return false;
        }
        return !this.isLeftChild(n) || this.exists(n + 1);
    }

    protected int revealSeedsSize(int[] nArray, int n) {
        int[] nArray2 = new int[]{0};
        this.getRevealedNodes(nArray, n, nArray2);
        return nArray2[0] * this.engine.seedSizeBytes;
    }

    protected int revealSeeds(int[] nArray, int n, byte[] byArray, int n2) {
        int[] nArray2 = new int[]{0};
        int n3 = n2;
        int[] nArray3 = this.getRevealedNodes(nArray, n, nArray2);
        for (int i = 0; i < nArray2[0]; ++i) {
            if ((n3 -= this.engine.seedSizeBytes) < 0) {
                LOG.fine("Insufficient sized buffer provided to revealSeeds");
                return 0;
            }
            System.arraycopy(this.nodes[nArray3[i]], 0, byArray, i * this.engine.seedSizeBytes, this.engine.seedSizeBytes);
        }
        return byArray.length - n3;
    }

    protected int openMerkleTreeSize(int[] nArray, int n) {
        int[] nArray2 = new int[1];
        this.getRevealedMerkleNodes(nArray, n, nArray2);
        return nArray2[0] * this.engine.digestSizeBytes;
    }

    private int[] getRevealedMerkleNodes(int[] nArray, int n, int[] nArray2) {
        int n2;
        int n3 = this.numNodes - this.numLeaves;
        boolean[] blArray = new boolean[this.numNodes];
        for (n2 = 0; n2 < n; ++n2) {
            blArray[n3 + nArray[n2]] = true;
        }
        for (int i = n2 = this.getParent(this.numNodes - 1); i > 0; --i) {
            if (!this.exists(i)) continue;
            if (this.exists(2 * i + 2)) {
                if (!blArray[2 * i + 1] || !blArray[2 * i + 2]) continue;
                blArray[i] = true;
                continue;
            }
            if (!blArray[2 * i + 1]) continue;
            blArray[i] = true;
        }
        int[] nArray3 = new int[this.numLeaves];
        int n4 = 0;
        block2: for (int i = 0; i < n; ++i) {
            int n5 = nArray[i] + n3;
            do {
                if (blArray[this.getParent(n5)]) continue;
                if (this.contains(nArray3, n4, n5)) continue block2;
                nArray3[n4] = n5;
                ++n4;
                continue block2;
            } while ((n5 = this.getParent(n5)) != 0);
        }
        nArray2[0] = n4;
        return nArray3;
    }

    private boolean contains(int[] nArray, int n, int n2) {
        for (int i = 0; i < n; ++i) {
            if (nArray[i] != n2) continue;
            return true;
        }
        return false;
    }

    private void computeParentHash(int n, byte[] byArray) {
        if (!this.exists(n)) {
            return;
        }
        int n2 = this.getParent(n);
        if (this.haveNode[n2]) {
            return;
        }
        if (!this.haveNode[2 * n2 + 1]) {
            return;
        }
        if (this.exists(2 * n2 + 2) && !this.haveNode[2 * n2 + 2]) {
            return;
        }
        this.engine.digest.update((byte)3);
        this.engine.digest.update(this.nodes[2 * n2 + 1], 0, this.engine.digestSizeBytes);
        if (this.hasRightChild(n2)) {
            this.engine.digest.update(this.nodes[2 * n2 + 2], 0, this.engine.digestSizeBytes);
        }
        this.engine.digest.update(byArray, 0, 32);
        this.engine.digest.update(Pack.intToLittleEndian(n2), 0, 2);
        this.engine.digest.doFinal(this.nodes[n2], 0, this.engine.digestSizeBytes);
        this.haveNode[n2] = true;
    }

    protected byte[] getLeaf(int n) {
        int n2 = this.numNodes - this.numLeaves;
        return this.nodes[n2 + n];
    }

    protected int addMerkleNodes(int[] nArray, int n, byte[] byArray, int n2) {
        int n3 = n2;
        int[] nArray2 = new int[]{0};
        int[] nArray3 = this.getRevealedMerkleNodes(nArray, n, nArray2);
        for (int i = 0; i < nArray2[0]; ++i) {
            if ((n3 -= this.dataSize) < 0) {
                return -1;
            }
            System.arraycopy(byArray, i * this.dataSize, this.nodes[nArray3[i]], 0, this.dataSize);
            this.haveNode[nArray3[i]] = true;
        }
        if (n3 != 0) {
            return -1;
        }
        return 0;
    }

    protected void generateSeeds(byte[] byArray, byte[] byArray2, int n) {
        this.nodes[0] = byArray;
        this.haveNode[0] = true;
        this.expandSeeds(byArray2, n);
    }

    private void expandSeeds(byte[] byArray, int n) {
        byte[] byArray2 = new byte[64];
        int n2 = this.getParent(this.numNodes - 1);
        for (int i = 0; i <= n2; ++i) {
            if (!this.haveNode[i]) continue;
            this.hashSeed(byArray2, this.nodes[i], byArray, (byte)1, n, i);
            if (!this.haveNode[2 * i + 1]) {
                System.arraycopy(byArray2, 0, this.nodes[2 * i + 1], 0, this.engine.seedSizeBytes);
                this.haveNode[2 * i + 1] = true;
            }
            if (!this.exists(2 * i + 2) || this.haveNode[2 * i + 2]) continue;
            System.arraycopy(byArray2, this.engine.seedSizeBytes, this.nodes[2 * i + 2], 0, this.engine.seedSizeBytes);
            this.haveNode[2 * i + 2] = true;
        }
    }

    private void hashSeed(byte[] byArray, byte[] byArray2, byte[] byArray3, byte by, int n, int n2) {
        this.engine.digest.update(by);
        this.engine.digest.update(byArray2, 0, this.engine.seedSizeBytes);
        this.engine.digest.update(byArray3, 0, 32);
        this.engine.digest.update(Pack.shortToLittleEndian((short)(n & 0xFFFF)), 0, 2);
        this.engine.digest.update(Pack.shortToLittleEndian((short)(n2 & 0xFFFF)), 0, 2);
        this.engine.digest.doFinal(byArray, 0, 2 * this.engine.seedSizeBytes);
    }

    private boolean isLeftChild(int n) {
        return n % 2 == 1;
    }

    private boolean hasRightChild(int n) {
        return 2 * n + 2 < this.numNodes && this.exists(n);
    }

    boolean hasLeftChild(Tree tree, int n) {
        return 2 * n + 1 < this.numNodes;
    }

    private int getParent(int n) {
        if (this.isLeftChild(n)) {
            return (n - 1) / 2;
        }
        return (n - 2) / 2;
    }

    private boolean exists(int n) {
        if (n >= this.numNodes) {
            return false;
        }
        return this.exists[n];
    }
}

