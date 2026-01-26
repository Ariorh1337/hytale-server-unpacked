/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KEMKeyWrapper;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;

public abstract class KEMRecipientInfoGenerator
implements RecipientInfoGenerator {
    protected final KEMKeyWrapper wrapper;
    private IssuerAndSerialNumber issuerAndSerial;
    private byte[] subjectKeyIdentifier;

    protected KEMRecipientInfoGenerator(IssuerAndSerialNumber issuerAndSerialNumber, KEMKeyWrapper kEMKeyWrapper) {
        this.issuerAndSerial = issuerAndSerialNumber;
        this.wrapper = kEMKeyWrapper;
    }

    protected KEMRecipientInfoGenerator(byte[] byArray, KEMKeyWrapper kEMKeyWrapper) {
        this.subjectKeyIdentifier = byArray;
        this.wrapper = kEMKeyWrapper;
    }

    @Override
    public final RecipientInfo generate(GenericKey genericKey) throws CMSException {
        byte[] byArray;
        try {
            byArray = this.wrapper.generateWrappedKey(genericKey);
        }
        catch (OperatorException operatorException) {
            throw new CMSException("exception wrapping content key: " + operatorException.getMessage(), operatorException);
        }
        RecipientIdentifier recipientIdentifier = this.issuerAndSerial != null ? new RecipientIdentifier(this.issuerAndSerial) : new RecipientIdentifier(new DEROctetString(this.subjectKeyIdentifier));
        return new RecipientInfo(new OtherRecipientInfo(CMSObjectIdentifiers.id_ori_kem, new KEMRecipientInfo(recipientIdentifier, this.wrapper.getAlgorithmIdentifier(), new DEROctetString(this.wrapper.getEncapsulation()), this.wrapper.getKdfAlgorithmIdentifier(), new ASN1Integer(this.wrapper.getKekLength()), null, this.wrapper.getWrapAlgorithmIdentifier(), new DEROctetString(byArray))));
    }
}

