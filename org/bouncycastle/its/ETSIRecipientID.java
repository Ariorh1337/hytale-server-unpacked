/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ETSIRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Selector;

public class ETSIRecipientID
implements Selector<ETSIRecipientInfo> {
    private final HashedId8 id;

    public ETSIRecipientID(byte[] byArray) {
        this(new HashedId8(byArray));
    }

    public ETSIRecipientID(HashedId8 hashedId8) {
        this.id = hashedId8;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ETSIRecipientID eTSIRecipientID = (ETSIRecipientID)object;
        return this.id != null ? this.id.equals(eTSIRecipientID.id) : eTSIRecipientID.id == null;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    @Override
    public boolean match(ETSIRecipientInfo eTSIRecipientInfo) {
        if (eTSIRecipientInfo.getRecipientInfo().getChoice() == 2) {
            PKRecipientInfo pKRecipientInfo = PKRecipientInfo.getInstance(eTSIRecipientInfo.getRecipientInfo().getRecipientInfo());
            return Arrays.areEqual(pKRecipientInfo.getRecipientId().getHashBytes(), this.id.getHashBytes());
        }
        return false;
    }

    @Override
    public Object clone() {
        return this;
    }
}

