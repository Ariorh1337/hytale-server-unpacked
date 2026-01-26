/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.tsp.ers;

import java.util.Date;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.util.Selector;

public class ERSEvidenceRecordSelector
implements Selector<ERSEvidenceRecord> {
    private final ERSData data;
    private final Date date;

    public ERSEvidenceRecordSelector(ERSData eRSData) {
        this(eRSData, new Date());
    }

    public ERSEvidenceRecordSelector(ERSData eRSData, Date date) {
        this.data = eRSData;
        this.date = new Date(date.getTime());
    }

    public ERSData getData() {
        return this.data;
    }

    @Override
    public boolean match(ERSEvidenceRecord eRSEvidenceRecord) {
        try {
            if (eRSEvidenceRecord.isContaining(this.data, this.date)) {
                try {
                    eRSEvidenceRecord.validatePresent(this.data, this.date);
                    return true;
                }
                catch (Exception exception) {
                    return false;
                }
            }
            return false;
        }
        catch (Exception exception) {
            return false;
        }
    }

    @Override
    public Object clone() {
        return this;
    }
}

