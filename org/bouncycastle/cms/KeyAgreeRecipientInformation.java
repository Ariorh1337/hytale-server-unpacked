/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.IOException;
import java.util.List;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.OriginatorIdentifierOrKey;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KeyAgreeRecipient;
import org.bouncycastle.cms.KeyAgreeRecipientId;
import org.bouncycastle.cms.OriginatorId;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientOperator;
import org.bouncycastle.util.Arrays;

public class KeyAgreeRecipientInformation
extends RecipientInformation {
    private KeyAgreeRecipientInfo info;
    private ASN1OctetString encryptedKey;

    static void readRecipientInfo(List list, KeyAgreeRecipientInfo keyAgreeRecipientInfo, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        ASN1Sequence aSN1Sequence = keyAgreeRecipientInfo.getRecipientEncryptedKeys();
        for (int i = 0; i < aSN1Sequence.size(); ++i) {
            KeyAgreeRecipientId keyAgreeRecipientId;
            RecipientEncryptedKey recipientEncryptedKey = RecipientEncryptedKey.getInstance(aSN1Sequence.getObjectAt(i));
            KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier = recipientEncryptedKey.getIdentifier();
            IssuerAndSerialNumber issuerAndSerialNumber = keyAgreeRecipientIdentifier.getIssuerAndSerialNumber();
            if (issuerAndSerialNumber != null) {
                keyAgreeRecipientId = new KeyAgreeRecipientId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
            } else {
                RecipientKeyIdentifier recipientKeyIdentifier = keyAgreeRecipientIdentifier.getRKeyID();
                keyAgreeRecipientId = new KeyAgreeRecipientId(recipientKeyIdentifier.getSubjectKeyIdentifier().getOctets());
            }
            list.add(new KeyAgreeRecipientInformation(keyAgreeRecipientInfo, keyAgreeRecipientId, recipientEncryptedKey.getEncryptedKey(), algorithmIdentifier, cMSSecureReadable));
        }
    }

    KeyAgreeRecipientInformation(KeyAgreeRecipientInfo keyAgreeRecipientInfo, RecipientId recipientId, ASN1OctetString aSN1OctetString, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        super(keyAgreeRecipientInfo.getKeyEncryptionAlgorithm(), algorithmIdentifier, cMSSecureReadable);
        this.info = keyAgreeRecipientInfo;
        this.rid = recipientId;
        this.encryptedKey = aSN1OctetString;
    }

    public OriginatorIdentifierOrKey getOriginator() {
        return this.info.getOriginator();
    }

    public byte[] getUserKeyingMaterial() {
        ASN1OctetString aSN1OctetString = this.info.getUserKeyingMaterial();
        if (aSN1OctetString != null) {
            return Arrays.clone(aSN1OctetString.getOctets());
        }
        return null;
    }

    private SubjectPublicKeyInfo getSenderPublicKeyInfo(AlgorithmIdentifier algorithmIdentifier, OriginatorIdentifierOrKey originatorIdentifierOrKey) throws CMSException, IOException {
        OriginatorId originatorId;
        OriginatorPublicKey originatorPublicKey = originatorIdentifierOrKey.getOriginatorKey();
        if (originatorPublicKey != null) {
            return this.getPublicKeyInfoFromOriginatorPublicKey(algorithmIdentifier, originatorPublicKey);
        }
        IssuerAndSerialNumber issuerAndSerialNumber = originatorIdentifierOrKey.getIssuerAndSerialNumber();
        if (issuerAndSerialNumber != null) {
            originatorId = new OriginatorId(issuerAndSerialNumber.getName(), issuerAndSerialNumber.getSerialNumber().getValue());
        } else {
            SubjectKeyIdentifier subjectKeyIdentifier = originatorIdentifierOrKey.getSubjectKeyIdentifier();
            originatorId = new OriginatorId(subjectKeyIdentifier.getKeyIdentifier());
        }
        return this.getPublicKeyInfoFromOriginatorId(originatorId);
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorPublicKey(AlgorithmIdentifier algorithmIdentifier, OriginatorPublicKey originatorPublicKey) {
        return new SubjectPublicKeyInfo(algorithmIdentifier, originatorPublicKey.getPublicKeyData());
    }

    private SubjectPublicKeyInfo getPublicKeyInfoFromOriginatorId(OriginatorId originatorId) throws CMSException {
        throw new CMSException("No support for 'originator' as IssuerAndSerialNumber or SubjectKeyIdentifier");
    }

    @Override
    protected RecipientOperator getRecipientOperator(Recipient recipient) throws CMSException, IOException {
        KeyAgreeRecipient keyAgreeRecipient = (KeyAgreeRecipient)recipient;
        AlgorithmIdentifier algorithmIdentifier = keyAgreeRecipient.getPrivateKeyAlgorithmIdentifier();
        return ((KeyAgreeRecipient)recipient).getRecipientOperator(this.keyEncAlg, this.messageAlgorithm, this.getSenderPublicKeyInfo(algorithmIdentifier, this.info.getOriginator()), this.info.getUserKeyingMaterial(), this.encryptedKey.getOctets());
    }
}

