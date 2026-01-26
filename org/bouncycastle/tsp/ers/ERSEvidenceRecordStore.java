/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.tsp.ers.ERSEvidenceRecordSelector;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.StoreException;

public class ERSEvidenceRecordStore
implements Store<ERSEvidenceRecord> {
    private Map<HashNode, List<ERSEvidenceRecord>> recordMap = new HashMap<HashNode, List<ERSEvidenceRecord>>();
    private DigestCalculator digCalc = null;

    public ERSEvidenceRecordStore(Collection<ERSEvidenceRecord> collection) throws OperatorCreationException {
        for (ERSEvidenceRecord eRSEvidenceRecord : collection) {
            Object object;
            ArchiveTimeStamp archiveTimeStamp = eRSEvidenceRecord.getArchiveTimeStamps()[0];
            if (this.digCalc == null) {
                object = eRSEvidenceRecord.getDigestAlgorithmProvider();
                this.digCalc = object.get(archiveTimeStamp.getDigestAlgorithmIdentifier());
            }
            if ((object = archiveTimeStamp.getHashTreeLeaf()) != null) {
                byte[][] byArray = ((PartialHashtree)object).getValues();
                if (byArray.length > 1) {
                    for (int i = 0; i != byArray.length; ++i) {
                        this.addRecord(new HashNode(byArray[i]), eRSEvidenceRecord);
                    }
                    this.addRecord(new HashNode(ERSUtil.computeNodeHash(this.digCalc, (PartialHashtree)object)), eRSEvidenceRecord);
                    continue;
                }
                this.addRecord(new HashNode(byArray[0]), eRSEvidenceRecord);
                continue;
            }
            this.addRecord(new HashNode(archiveTimeStamp.getTimeStampDigestValue()), eRSEvidenceRecord);
        }
    }

    private void addRecord(HashNode hashNode, ERSEvidenceRecord eRSEvidenceRecord) {
        List<ERSEvidenceRecord> list = this.recordMap.get(hashNode);
        if (list != null) {
            ArrayList<ERSEvidenceRecord> arrayList = new ArrayList<ERSEvidenceRecord>(list.size() + 1);
            arrayList.addAll(list);
            arrayList.add(eRSEvidenceRecord);
            this.recordMap.put(hashNode, arrayList);
        } else {
            this.recordMap.put(hashNode, Collections.singletonList(eRSEvidenceRecord));
        }
    }

    @Override
    public Collection<ERSEvidenceRecord> getMatches(Selector<ERSEvidenceRecord> selector) throws StoreException {
        if (selector instanceof ERSEvidenceRecordSelector) {
            HashNode hashNode = new HashNode(((ERSEvidenceRecordSelector)selector).getData().getHash(this.digCalc, null));
            List<ERSEvidenceRecord> list = this.recordMap.get(hashNode);
            if (list != null) {
                ArrayList<ERSEvidenceRecord> arrayList = new ArrayList<ERSEvidenceRecord>(list.size());
                for (int i = 0; i != list.size(); ++i) {
                    ERSEvidenceRecord eRSEvidenceRecord = list.get(i);
                    if (!selector.match(eRSEvidenceRecord)) continue;
                    arrayList.add(eRSEvidenceRecord);
                }
                return Collections.unmodifiableList(arrayList);
            }
            return Collections.emptyList();
        }
        if (selector == null) {
            HashSet<ERSEvidenceRecord> hashSet = new HashSet<ERSEvidenceRecord>(this.recordMap.size());
            Iterator<List<ERSEvidenceRecord>> iterator = this.recordMap.values().iterator();
            while (iterator.hasNext()) {
                hashSet.addAll(iterator.next());
            }
            return Collections.unmodifiableList(new ArrayList(hashSet));
        }
        HashSet<ERSEvidenceRecord> hashSet = new HashSet<ERSEvidenceRecord>();
        for (List<ERSEvidenceRecord> list : this.recordMap.values()) {
            for (int i = 0; i != list.size(); ++i) {
                if (!selector.match(list.get(i))) continue;
                hashSet.add(list.get(i));
            }
        }
        return Collections.unmodifiableList(new ArrayList(hashSet));
    }

    private static class HashNode {
        private final byte[] dataHash;
        private final int hashCode;

        public HashNode(byte[] byArray) {
            this.dataHash = byArray;
            this.hashCode = Arrays.hashCode(byArray);
        }

        public int hashCode() {
            return this.hashCode;
        }

        public boolean equals(Object object) {
            if (object instanceof HashNode) {
                return Arrays.areEqual(this.dataHash, ((HashNode)object).dataHash);
            }
            return false;
        }
    }
}

