/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bouncycastle.tsp.ers.ByteArrayComparator;
import org.bouncycastle.tsp.ers.IndexedHash;

public class SortedIndexedHashList {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
    private final LinkedList<IndexedHash> baseList = new LinkedList();

    public IndexedHash getFirst() {
        return this.baseList.getFirst();
    }

    public void add(IndexedHash indexedHash) {
        if (this.baseList.size() == 0) {
            this.baseList.addFirst(indexedHash);
        } else if (hashComp.compare(indexedHash.digest, this.baseList.get((int)0).digest) < 0) {
            this.baseList.addFirst(indexedHash);
        } else {
            int n;
            for (n = 1; n < this.baseList.size() && hashComp.compare(this.baseList.get((int)n).digest, indexedHash.digest) <= 0; ++n) {
            }
            if (n == this.baseList.size()) {
                this.baseList.add(indexedHash);
            } else {
                this.baseList.add(n, indexedHash);
            }
        }
    }

    public int size() {
        return this.baseList.size();
    }

    public List<IndexedHash> toList() {
        return new ArrayList<IndexedHash>(this.baseList);
    }
}

