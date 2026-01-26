/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmp.Challenge;
import org.bouncycastle.asn1.cmp.POPODecKeyChallContent;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.cmp.CMPException;
import org.bouncycastle.cert.cmp.POPODecryptionKeyChallengeContent;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSEnvelopedDataGenerator;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.RecipientInfoGenerator;
import org.bouncycastle.cms.jcajce.JceCMSContentEncryptorBuilder;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Arrays;

public class POPODecryptionKeyChallengeContentBuilder {
    private final DigestCalculator owfCalculator;
    private final ASN1ObjectIdentifier challengeEncAlg;
    private ASN1EncodableVector challenges = new ASN1EncodableVector();

    public POPODecryptionKeyChallengeContentBuilder(DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        this.owfCalculator = digestCalculator;
        this.challengeEncAlg = aSN1ObjectIdentifier;
    }

    public POPODecryptionKeyChallengeContentBuilder addChallenge(RecipientInfoGenerator recipientInfoGenerator, GeneralName generalName, byte[] byArray) throws CMPException {
        Object object;
        Object object2;
        byte[] byArray2 = Arrays.clone(byArray);
        try {
            object2 = this.owfCalculator.getOutputStream();
            ((OutputStream)object2).write(new ASN1Integer(byArray2).getEncoded());
            ((OutputStream)object2).close();
        }
        catch (IOException iOException) {
            throw new CMPException("unable to calculate witness", iOException);
        }
        try {
            object = new CMSEnvelopedDataGenerator();
            ((CMSEnvelopedGenerator)object).addRecipientInfoGenerator(recipientInfoGenerator);
            object2 = ((CMSEnvelopedDataGenerator)object).generate(new CMSProcessableByteArray(new Challenge.Rand(byArray, generalName).getEncoded()), new JceCMSContentEncryptorBuilder(this.challengeEncAlg).setProvider("BC").build());
        }
        catch (Exception exception) {
            throw new CMPException("unable to encrypt challenge", exception);
        }
        object = EnvelopedData.getInstance(((CMSEnvelopedData)object2).toASN1Structure().getContent());
        if (this.challenges.size() == 0) {
            this.challenges.add(new Challenge(this.owfCalculator.getAlgorithmIdentifier(), this.owfCalculator.getDigest(), (EnvelopedData)object));
        } else {
            this.challenges.add(new Challenge(this.owfCalculator.getDigest(), (EnvelopedData)object));
        }
        return this;
    }

    public POPODecryptionKeyChallengeContent build() {
        return new POPODecryptionKeyChallengeContent(POPODecKeyChallContent.getInstance(new DERSequence(this.challenges)), new DigestCalculatorProvider(){

            @Override
            public DigestCalculator get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                return POPODecryptionKeyChallengeContentBuilder.this.owfCalculator;
            }
        });
    }
}

