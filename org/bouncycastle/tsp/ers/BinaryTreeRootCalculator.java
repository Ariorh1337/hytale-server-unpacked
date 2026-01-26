/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.tsp.ers.SortedHashList;
import org.bouncycastle.util.Arrays;

public class BinaryTreeRootCalculator
implements ERSRootNodeCalculator {
    private List<List<byte[]>> tree;

    @Override
    public byte[] computeRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArray) {
        Object object;
        SortedHashList sortedHashList = new SortedHashList();
        for (int i = 0; i < partialHashtreeArray.length; ++i) {
            object = ERSUtil.computeNodeHash(digestCalculator, partialHashtreeArray[i]);
            sortedHashList.add((byte[])object);
        }
        Object object2 = sortedHashList.toList();
        this.tree = new ArrayList<List<byte[]>>();
        this.tree.add((List<byte[]>)object2);
        if (object2.size() > 1) {
            do {
                object = new ArrayList(object2.size() / 2 + 1);
                for (int i = 0; i <= object2.size() - 2; i += 2) {
                    object.add(ERSUtil.calculateBranchHash(digestCalculator, object2.get(i), object2.get(i + 1)));
                }
                if (object2.size() % 2 == 1) {
                    object.add(object2.get(object2.size() - 1));
                }
                this.tree.add((List<byte[]>)object);
            } while ((object2 = (Object)object).size() > 1);
        }
        return object2.get(0);
    }

    @Override
    public PartialHashtree[] computePathToRoot(DigestCalculator digestCalculator, PartialHashtree partialHashtree, int n) {
        ArrayList<PartialHashtree> arrayList = new ArrayList<PartialHashtree>();
        byte[] byArray = ERSUtil.computeNodeHash(digestCalculator, partialHashtree);
        arrayList.add(partialHashtree);
        for (int i = 0; i < this.tree.size() - 1; ++i) {
            Object object;
            if (n == this.tree.get(i).size() - 1) {
                while (Arrays.areEqual(byArray, (object = this.tree.get(i + 1)).get(object.size() - 1))) {
                    n = this.tree.get(++i).size() - 1;
                }
            }
            object = (n & 1) == 0 ? (Object)this.tree.get(i).get(n + 1) : (Object)this.tree.get(i).get(n - 1);
            arrayList.add(new PartialHashtree((byte[])object));
            byArray = ERSUtil.calculateBranchHash(digestCalculator, byArray, (byte[])object);
            n /= 2;
        }
        return arrayList.toArray(new PartialHashtree[0]);
    }

    @Override
    public byte[] recoverRootHash(DigestCalculator digestCalculator, PartialHashtree[] partialHashtreeArray) {
        byte[] byArray = ERSUtil.computeNodeHash(digestCalculator, partialHashtreeArray[0]);
        for (int i = 1; i < partialHashtreeArray.length; ++i) {
            byArray = ERSUtil.calculateBranchHash(digestCalculator, byArray, ERSUtil.computeNodeHash(digestCalculator, partialHashtreeArray[i]));
        }
        return byArray;
    }
}

