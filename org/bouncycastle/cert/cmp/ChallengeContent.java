/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.PKIHeader;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.cmp.CMPChallengeFailedException;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Arrays;

public class ChallengeContent {
    private final Challenge challenge;
    private final DigestCalculator owfCalc;

    ChallengeContent(Challenge challenge, DigestCalculator digestCalculator) {
        this.challenge = challenge;
        this.owfCalc = digestCalculator;
    }

    public byte[] extractChallenge(PKIHeader pKIHeader, Recipient recipient) throws CMPException {
        try {
            CMSEnvelopedData cMSEnvelopedData = new CMSEnvelopedData(new ContentInfo(PKCSObjectIdentifiers.envelopedData, this.challenge.getEncryptedRand()));
            Collection<RecipientInformation> collection = cMSEnvelopedData.getRecipientInfos().getRecipients();
            RecipientInformation recipientInformation = collection.iterator().next();
            byte[] byArray = recipientInformation.getContent(recipient);
            Challenge.Rand rand = Challenge.Rand.getInstance(byArray);
            if (!Arrays.constantTimeAreEqual(rand.getSender().getEncoded(), pKIHeader.getSender().getEncoded())) {
                throw new CMPChallengeFailedException("incorrect sender found");
            }
            OutputStream outputStream = this.owfCalc.getOutputStream();
            outputStream.write(rand.getInt().getEncoded());
            outputStream.close();
            if (!Arrays.constantTimeAreEqual(this.challenge.getWitness(), this.owfCalc.getDigest())) {
                throw new CMPChallengeFailedException("corrupted challenge found");
            }
            return rand.getInt().getValue().toByteArray();
        }
        catch (CMSException cMSException) {
            throw new CMPException(cMSException.getMessage(), cMSException);
        }
        catch (IOException iOException) {
            throw new CMPException(iOException.getMessage(), iOException);
        }
    }
}

