/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.PKIBody;
import org.bouncycastle.asn1.cmp.POPODecKeyChallContent;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.ChallengeContent;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;

public class POPODecryptionKeyChallengeContent {
    private final ASN1Sequence content;
    private final DigestCalculatorProvider owfCalcProvider;

    POPODecryptionKeyChallengeContent(POPODecKeyChallContent pOPODecKeyChallContent, DigestCalculatorProvider digestCalculatorProvider) {
        this.content = ASN1Sequence.getInstance(pOPODecKeyChallContent.toASN1Primitive());
        this.owfCalcProvider = digestCalculatorProvider;
    }

    public ChallengeContent[] toChallengeArray() throws CMPException {
        ChallengeContent[] challengeContentArray = new ChallengeContent[this.content.size()];
        DigestCalculator digestCalculator = null;
        for (int i = 0; i != challengeContentArray.length; ++i) {
            Challenge challenge = Challenge.getInstance(this.content.getObjectAt(i));
            if (challenge.getOwf() != null) {
                try {
                    digestCalculator = this.owfCalcProvider.get(challenge.getOwf());
                }
                catch (OperatorCreationException operatorCreationException) {
                    throw new CMPException(operatorCreationException.getMessage(), operatorCreationException);
                }
            }
            challengeContentArray[i] = new ChallengeContent(Challenge.getInstance(this.content.getObjectAt(i)), digestCalculator);
        }
        return challengeContentArray;
    }

    public static POPODecryptionKeyChallengeContent fromPKIBody(PKIBody pKIBody, DigestCalculatorProvider digestCalculatorProvider) {
        if (pKIBody.getType() != 5) {
            throw new IllegalArgumentException("content of PKIBody wrong type: " + pKIBody.getType());
        }
        return new POPODecryptionKeyChallengeContent(POPODecKeyChallContent.getInstance(pKIBody.getContent()), digestCalculatorProvider);
    }

    public POPODecKeyChallContent toASN1Structure() {
        return POPODecKeyChallContent.getInstance(this.content);
    }
}

