/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.its.ETSIRecipientInfo;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class ETSIEncryptedData {
    private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Encrypted.build();
    private final EncryptedData encryptedData;

    public ETSIEncryptedData(byte[] byArray) throws IOException {
        this(new ByteArrayInputStream(byArray));
    }

    public ETSIEncryptedData(InputStream inputStream) throws IOException {
        OERInputStream oERInputStream = inputStream instanceof OERInputStream ? (OERInputStream)inputStream : new OERInputStream(inputStream);
        ASN1Object aSN1Object = oERInputStream.parse(oerDef);
        Ieee1609Dot2Content ieee1609Dot2Content = EtsiTs103097DataEncrypted.getInstance(aSN1Object).getContent();
        if (ieee1609Dot2Content.getChoice() != 2) {
            throw new IllegalStateException("EtsiTs103097Data-Encrypted did not have encrypted data content");
        }
        this.encryptedData = EncryptedData.getInstance(ieee1609Dot2Content.getIeee1609Dot2Content());
    }

    ETSIEncryptedData(EncryptedData encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getEncoded() {
        return OEREncoder.toByteArray(new EtsiTs103097DataEncrypted(Ieee1609Dot2Content.encryptedData(this.encryptedData)), oerDef);
    }

    public EncryptedData getEncryptedData() {
        return this.encryptedData;
    }

    public Store<ETSIRecipientInfo> getRecipients() {
        ArrayList<ETSIRecipientInfo> arrayList = new ArrayList<ETSIRecipientInfo>();
        for (RecipientInfo recipientInfo : this.encryptedData.getRecipients().getRecipientInfos()) {
            arrayList.add(new ETSIRecipientInfo(this.encryptedData, recipientInfo));
        }
        return new CollectionStore<ETSIRecipientInfo>(arrayList);
    }
}

