/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.tsp.ArchiveTimeStamp;
import org.bouncycastle.asn1.tsp.ArchiveTimeStampSequence;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.asn1.tsp.TSTInfo;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.ers.BinaryTreeRootCalculator;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSDataGroup;
import org.bouncycastle.tsp.ers.ERSException;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.tsp.ers.IndexedHash;
import org.bouncycastle.util.Arrays;

public class ERSArchiveTimeStampGenerator {
    private final DigestCalculator digCalc;
    private List<ERSData> dataObjects = new ArrayList<ERSData>();
    private ERSRootNodeCalculator rootNodeCalculator = new BinaryTreeRootCalculator();
    private byte[] previousChainHash;

    public ERSArchiveTimeStampGenerator(DigestCalculator digestCalculator) {
        this.digCalc = digestCalculator;
    }

    public void addData(ERSData eRSData) {
        this.dataObjects.add(eRSData);
    }

    public void addAllData(List<ERSData> list) {
        this.dataObjects.addAll(list);
    }

    void addPreviousChains(ArchiveTimeStampSequence archiveTimeStampSequence) throws IOException {
        OutputStream outputStream = this.digCalc.getOutputStream();
        outputStream.write(archiveTimeStampSequence.getEncoded("DER"));
        outputStream.close();
        this.previousChainHash = this.digCalc.getDigest();
    }

    public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator timeStampRequestGenerator) throws TSPException, IOException {
        PartialHashtree[] partialHashtreeArray = this.getPartialHashtrees();
        byte[] byArray = this.rootNodeCalculator.computeRootHash(this.digCalc, partialHashtreeArray);
        return timeStampRequestGenerator.generate(this.digCalc.getAlgorithmIdentifier(), byArray);
    }

    public TimeStampRequest generateTimeStampRequest(TimeStampRequestGenerator timeStampRequestGenerator, BigInteger bigInteger) throws TSPException, IOException {
        PartialHashtree[] partialHashtreeArray = this.getPartialHashtrees();
        byte[] byArray = this.rootNodeCalculator.computeRootHash(this.digCalc, partialHashtreeArray);
        return timeStampRequestGenerator.generate(this.digCalc.getAlgorithmIdentifier(), byArray, bigInteger);
    }

    public ERSArchiveTimeStamp generateArchiveTimeStamp(TimeStampResponse timeStampResponse) throws TSPException, ERSException {
        PartialHashtree[] partialHashtreeArray = this.getPartialHashtrees();
        if (partialHashtreeArray.length != 1) {
            throw new ERSException("multiple reduced hash trees found");
        }
        byte[] byArray = this.rootNodeCalculator.computeRootHash(this.digCalc, partialHashtreeArray);
        if (timeStampResponse.getStatus() != 0) {
            throw new TSPException("TSP response error status: " + timeStampResponse.getStatusString());
        }
        TSTInfo tSTInfo = timeStampResponse.getTimeStampToken().getTimeStampInfo().toASN1Structure();
        if (!tSTInfo.getMessageImprint().getHashAlgorithm().equals(this.digCalc.getAlgorithmIdentifier())) {
            throw new ERSException("time stamp imprint for wrong algorithm");
        }
        if (!Arrays.areEqual(tSTInfo.getMessageImprint().getHashedMessage(), byArray)) {
            throw new ERSException("time stamp imprint for wrong root hash");
        }
        if (partialHashtreeArray[0].getValueCount() == 1) {
            return new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, timeStampResponse.getTimeStampToken().toCMSSignedData().toASN1Structure()), this.digCalc);
        }
        return new ERSArchiveTimeStamp(new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), partialHashtreeArray, timeStampResponse.getTimeStampToken().toCMSSignedData().toASN1Structure()), this.digCalc);
    }

    public List<ERSArchiveTimeStamp> generateArchiveTimeStamps(TimeStampResponse timeStampResponse) throws TSPException, ERSException {
        PartialHashtree[] partialHashtreeArray = this.getPartialHashtrees();
        byte[] byArray = this.rootNodeCalculator.computeRootHash(this.digCalc, partialHashtreeArray);
        if (timeStampResponse.getStatus() != 0) {
            throw new TSPException("TSP response error status: " + timeStampResponse.getStatusString());
        }
        TSTInfo tSTInfo = timeStampResponse.getTimeStampToken().getTimeStampInfo().toASN1Structure();
        if (!tSTInfo.getMessageImprint().getHashAlgorithm().equals(this.digCalc.getAlgorithmIdentifier())) {
            throw new ERSException("time stamp imprint for wrong algorithm");
        }
        if (!Arrays.areEqual(tSTInfo.getMessageImprint().getHashedMessage(), byArray)) {
            throw new ERSException("time stamp imprint for wrong root hash");
        }
        ContentInfo contentInfo = timeStampResponse.getTimeStampToken().toCMSSignedData().toASN1Structure();
        ArrayList<ERSArchiveTimeStamp> arrayList = new ArrayList<ERSArchiveTimeStamp>();
        if (partialHashtreeArray.length == 1 && partialHashtreeArray[0].getValueCount() == 1) {
            arrayList.add(new ERSArchiveTimeStamp(new ArchiveTimeStamp(null, null, contentInfo), this.digCalc));
        } else {
            int n;
            ERSArchiveTimeStamp[] eRSArchiveTimeStampArray = new ERSArchiveTimeStamp[partialHashtreeArray.length];
            for (n = 0; n != partialHashtreeArray.length; ++n) {
                PartialHashtree[] partialHashtreeArray2 = this.rootNodeCalculator.computePathToRoot(this.digCalc, partialHashtreeArray[n], n);
                eRSArchiveTimeStampArray[((IndexedPartialHashtree)partialHashtreeArray[n]).order] = new ERSArchiveTimeStamp(new ArchiveTimeStamp(this.digCalc.getAlgorithmIdentifier(), partialHashtreeArray2, contentInfo), this.digCalc);
            }
            for (n = 0; n != partialHashtreeArray.length; ++n) {
                arrayList.add(eRSArchiveTimeStampArray[n]);
            }
        }
        return arrayList;
    }

    private IndexedPartialHashtree[] getPartialHashtrees() {
        int n;
        List<IndexedHash> list = ERSUtil.buildIndexedHashList(this.digCalc, this.dataObjects, this.previousChainHash);
        IndexedPartialHashtree[] indexedPartialHashtreeArray = new IndexedPartialHashtree[list.size()];
        HashSet<ERSDataGroup> hashSet = new HashSet<ERSDataGroup>();
        for (n = 0; n != this.dataObjects.size(); ++n) {
            if (!(this.dataObjects.get(n) instanceof ERSDataGroup)) continue;
            hashSet.add((ERSDataGroup)this.dataObjects.get(n));
        }
        for (n = 0; n != list.size(); ++n) {
            byte[] byArray = list.get((int)n).digest;
            ERSData eRSData = this.dataObjects.get(list.get((int)n).order);
            if (eRSData instanceof ERSDataGroup) {
                ERSDataGroup eRSDataGroup = (ERSDataGroup)eRSData;
                List<byte[]> list2 = eRSDataGroup.getHashes(this.digCalc, this.previousChainHash);
                indexedPartialHashtreeArray[n] = new IndexedPartialHashtree(list.get((int)n).order, (byte[][])list2.toArray((T[])new byte[list2.size()][]));
                continue;
            }
            indexedPartialHashtreeArray[n] = new IndexedPartialHashtree(list.get((int)n).order, byArray);
        }
        return indexedPartialHashtreeArray;
    }

    private static class IndexedPartialHashtree
    extends PartialHashtree {
        final int order;

        private IndexedPartialHashtree(int n, byte[] byArray) {
            super(byArray);
            this.order = n;
        }

        private IndexedPartialHashtree(int n, byte[][] byArray) {
            super(byArray);
            this.order = n;
        }
    }
}

