/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSReadable;
import org.bouncycastle.cms.CMSSecureReadable;
import org.bouncycastle.cms.KEKRecipientInformation;
import org.bouncycastle.cms.KEMRecipientInformation;
import org.bouncycastle.cms.KeyAgreeRecipientInformation;
import org.bouncycastle.cms.KeyTransRecipientInformation;
import org.bouncycastle.cms.PasswordRecipientInformation;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.cms.RecipientInformationStore;
import org.bouncycastle.operator.DigestCalculator;

class CMSEnvelopedHelper {
    CMSEnvelopedHelper() {
    }

    static RecipientInformationStore buildRecipientInformationStore(ASN1Set aSN1Set, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        ArrayList<RecipientInformation> arrayList = new ArrayList<RecipientInformation>();
        for (int i = 0; i != aSN1Set.size(); ++i) {
            RecipientInfo recipientInfo = RecipientInfo.getInstance(aSN1Set.getObjectAt(i));
            CMSEnvelopedHelper.readRecipientInfo(arrayList, recipientInfo, algorithmIdentifier, cMSSecureReadable);
        }
        return new RecipientInformationStore(arrayList);
    }

    private static void readRecipientInfo(List list, RecipientInfo recipientInfo, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        ASN1Encodable aSN1Encodable = recipientInfo.getInfo();
        if (aSN1Encodable instanceof KeyTransRecipientInfo) {
            list.add(new KeyTransRecipientInformation((KeyTransRecipientInfo)aSN1Encodable, algorithmIdentifier, cMSSecureReadable));
        } else if (aSN1Encodable instanceof OtherRecipientInfo) {
            OtherRecipientInfo otherRecipientInfo = OtherRecipientInfo.getInstance(aSN1Encodable);
            if (CMSObjectIdentifiers.id_ori_kem.equals(otherRecipientInfo.getType())) {
                list.add(new KEMRecipientInformation(KEMRecipientInfo.getInstance(otherRecipientInfo.getValue()), algorithmIdentifier, cMSSecureReadable));
            }
        } else if (aSN1Encodable instanceof KEKRecipientInfo) {
            list.add(new KEKRecipientInformation((KEKRecipientInfo)aSN1Encodable, algorithmIdentifier, cMSSecureReadable));
        } else if (aSN1Encodable instanceof KeyAgreeRecipientInfo) {
            KeyAgreeRecipientInformation.readRecipientInfo(list, (KeyAgreeRecipientInfo)aSN1Encodable, algorithmIdentifier, cMSSecureReadable);
        } else if (aSN1Encodable instanceof PasswordRecipientInfo) {
            list.add(new PasswordRecipientInformation((PasswordRecipientInfo)aSN1Encodable, algorithmIdentifier, cMSSecureReadable));
        }
    }

    static class CMSAuthEnveSecureReadable
    extends CMSDefaultSecureReadable {
        private AlgorithmIdentifier algorithm;

        CMSAuthEnveSecureReadable(AlgorithmIdentifier algorithmIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            super(aSN1ObjectIdentifier, cMSReadable);
            this.algorithm = algorithmIdentifier;
        }

        @Override
        public InputStream getInputStream() throws IOException, CMSException {
            return this.readable.getInputStream();
        }

        @Override
        public boolean hasAdditionalData() {
            return false;
        }
    }

    static abstract class CMSDefaultSecureReadable
    implements CMSSecureReadable {
        protected final ASN1ObjectIdentifier contentType;
        protected CMSReadable readable;
        protected ASN1Set authAttrSet;

        CMSDefaultSecureReadable(ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            this.contentType = aSN1ObjectIdentifier;
            this.readable = cMSReadable;
        }

        @Override
        public ASN1ObjectIdentifier getContentType() {
            return this.contentType;
        }

        @Override
        public ASN1Set getAuthAttrSet() {
            return this.authAttrSet;
        }

        @Override
        public void setAuthAttrSet(ASN1Set aSN1Set) {
            this.authAttrSet = aSN1Set;
        }
    }

    static class CMSDigestAuthenticatedSecureReadable
    extends CMSDefaultSecureReadable {
        private DigestCalculator digestCalculator;

        public CMSDigestAuthenticatedSecureReadable(DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            super(aSN1ObjectIdentifier, cMSReadable);
            this.digestCalculator = digestCalculator;
        }

        @Override
        public InputStream getInputStream() throws IOException, CMSException {
            return new FilterInputStream(this.readable.getInputStream()){

                @Override
                public int read() throws IOException {
                    int n = this.in.read();
                    if (n >= 0) {
                        digestCalculator.getOutputStream().write(n);
                    }
                    return n;
                }

                @Override
                public int read(byte[] byArray, int n, int n2) throws IOException {
                    int n3 = this.in.read(byArray, n, n2);
                    if (n3 >= 0) {
                        digestCalculator.getOutputStream().write(byArray, n, n3);
                    }
                    return n3;
                }
            };
        }

        public byte[] getDigest() {
            return this.digestCalculator.getDigest();
        }

        @Override
        public boolean hasAdditionalData() {
            return true;
        }
    }
}

