/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.its.ETSIEncryptedData;
import org.bouncycastle.its.ETSIRecipientInfoBuilder;
import org.bouncycastle.its.operator.ETSIDataEncryptor;
import org.bouncycastle.oer.its.ieee1609dot2.AesCcmCiphertext;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfRecipientInfo;
import org.bouncycastle.oer.its.ieee1609dot2.SymmetricCiphertext;

public class ETSIEncryptedDataBuilder {
    private final List<ETSIRecipientInfoBuilder> recipientInfoBuilders = new ArrayList<ETSIRecipientInfoBuilder>();

    public void addRecipientInfoBuilder(ETSIRecipientInfoBuilder eTSIRecipientInfoBuilder) {
        this.recipientInfoBuilders.add(eTSIRecipientInfoBuilder);
    }

    public ETSIEncryptedData build(ETSIDataEncryptor eTSIDataEncryptor, byte[] byArray) {
        byte[] byArray2 = eTSIDataEncryptor.encrypt(byArray);
        byte[] byArray3 = eTSIDataEncryptor.getKey();
        byte[] byArray4 = eTSIDataEncryptor.getNonce();
        SequenceOfRecipientInfo.Builder builder = SequenceOfRecipientInfo.builder();
        for (ETSIRecipientInfoBuilder eTSIRecipientInfoBuilder : this.recipientInfoBuilders) {
            builder.addRecipients(eTSIRecipientInfoBuilder.build(byArray3));
        }
        return new ETSIEncryptedData(EncryptedData.builder().setRecipients(builder.createSequenceOfRecipientInfo()).setCiphertext(SymmetricCiphertext.aes128ccm(AesCcmCiphertext.builder().setCcmCiphertext(byArray2).setNonce(byArray4).createAesCcmCiphertext())).createEncryptedData());
    }
}

