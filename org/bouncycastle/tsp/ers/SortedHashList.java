/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bouncycastle.tsp.ers.ByteArrayComparator;

public class SortedHashList {
    private static final Comparator<byte[]> hashComp = new ByteArrayComparator();
    private final LinkedList<byte[]> baseList = new LinkedList();

    public byte[] getFirst() {
        return this.baseList.getFirst();
    }

    public void add(byte[] byArray) {
        if (this.baseList.size() == 0) {
            this.baseList.addFirst(byArray);
        } else if (hashComp.compare(byArray, this.baseList.get(0)) < 0) {
            this.baseList.addFirst(byArray);
        } else {
            int n;
            for (n = 1; n < this.baseList.size() && hashComp.compare(this.baseList.get(n), byArray) <= 0; ++n) {
            }
            if (n == this.baseList.size()) {
                this.baseList.add(byArray);
            } else {
                this.baseList.add(n, byArray);
            }
        }
    }

    public int size() {
        return this.baseList.size();
    }

    public List<byte[]> toList() {
        return new ArrayList<byte[]>(this.baseList);
    }
}

