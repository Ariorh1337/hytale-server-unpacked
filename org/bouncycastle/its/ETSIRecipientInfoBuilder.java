/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ETSIKeyWrapper;
import org.bouncycastle.oer.its.ieee1609dot2.PKRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId8;

public class ETSIRecipientInfoBuilder {
    private final ETSIKeyWrapper keyWrapper;
    private final byte[] recipientID;

    public ETSIRecipientInfoBuilder(ETSIKeyWrapper eTSIKeyWrapper, byte[] byArray) {
        this.keyWrapper = eTSIKeyWrapper;
        this.recipientID = byArray;
    }

    public RecipientInfo build(byte[] byArray) {
        try {
            return RecipientInfo.certRecipInfo(PKRecipientInfo.builder().setRecipientId(new HashedId8(this.recipientID)).setEncKey(this.keyWrapper.wrap(byArray)).createPKRecipientInfo());
        }
        catch (Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }
}

