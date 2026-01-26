/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.tsp.ers.ERSException;

public class ERSEvidenceRecordGenerator {
    private final DigestCalculatorProvider digCalcProv;

    public ERSEvidenceRecordGenerator(DigestCalculatorProvider digestCalculatorProvider) {
        this.digCalcProv = digestCalculatorProvider;
    }

    public ERSEvidenceRecord generate(ERSArchiveTimeStamp eRSArchiveTimeStamp) throws TSPException, ERSException {
        return new ERSEvidenceRecord(new EvidenceRecord(null, null, eRSArchiveTimeStamp.toASN1Structure()), this.digCalcProv);
    }

    public List<ERSEvidenceRecord> generate(List<ERSArchiveTimeStamp> list) throws TSPException, ERSException {
        ArrayList<ERSEvidenceRecord> arrayList = new ArrayList<ERSEvidenceRecord>(list.size());
        for (int i = 0; i != list.size(); ++i) {
            arrayList.add(new ERSEvidenceRecord(new EvidenceRecord(null, null, list.get(i).toASN1Structure()), this.digCalcProv));
        }
        return arrayList;
    }
}

