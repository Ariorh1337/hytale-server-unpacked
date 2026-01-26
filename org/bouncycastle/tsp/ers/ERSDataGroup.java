/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSCachingData;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSUtil;

public class ERSDataGroup
extends ERSCachingData {
    protected List<ERSData> dataObjects;

    public ERSDataGroup(ERSData ... eRSDataArray) {
        this.dataObjects = new ArrayList<ERSData>(eRSDataArray.length);
        this.dataObjects.addAll(Arrays.asList(eRSDataArray));
    }

    public ERSDataGroup(List<ERSData> list) {
        this.dataObjects = new ArrayList<ERSData>(list.size());
        this.dataObjects.addAll(list);
    }

    public ERSDataGroup(ERSData eRSData) {
        this.dataObjects = Collections.singletonList(eRSData);
    }

    public List<byte[]> getHashes(DigestCalculator digestCalculator, byte[] byArray) {
        return ERSUtil.buildHashList(digestCalculator, this.dataObjects, byArray);
    }

    @Override
    public byte[] getHash(DigestCalculator digestCalculator, byte[] byArray) {
        List<byte[]> list = this.getHashes(digestCalculator, byArray);
        if (list.size() > 1) {
            return ERSUtil.calculateDigest(digestCalculator, list.iterator());
        }
        return list.get(0);
    }

    @Override
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] byArray) {
        List<byte[]> list = this.getHashes(digestCalculator, byArray);
        if (list.size() > 1) {
            ArrayList<byte[]> arrayList = new ArrayList<byte[]>(list.size());
            for (int i = 0; i != arrayList.size(); ++i) {
                arrayList.add(list.get(i));
            }
            return ERSUtil.calculateDigest(digestCalculator, arrayList.iterator());
        }
        return list.get(0);
    }

    public int size() {
        return this.dataObjects.size();
    }
}

