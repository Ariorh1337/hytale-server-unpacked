/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.POPODecKeyRespContent;

public class POPODecryptionKeyResponseContent {
    private final POPODecKeyRespContent respContent;

    POPODecryptionKeyResponseContent(POPODecKeyRespContent pOPODecKeyRespContent) {
        this.respContent = pOPODecKeyRespContent;
    }

    public byte[][] getResponses() {
        ASN1Integer[] aSN1IntegerArray = this.respContent.toASN1IntegerArray();
        byte[][] byArrayArray = new byte[aSN1IntegerArray.length][];
        for (int i = 0; i != aSN1IntegerArray.length; ++i) {
            byArrayArray[i] = aSN1IntegerArray[i].getValue().toByteArray();
        }
        return byArrayArray;
    }

    public static POPODecryptionKeyResponseContent fromPKIBody(PKIBody pKIBody) {
        if (pKIBody.getType() != 6) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
        }
        return new POPODecryptionKeyResponseContent(POPODecKeyRespContent.getInstance(pKIBody.getContent()));
    }

    public POPODecKeyRespContent toASN1Structure() {
        return this.respContent;
    }
}

