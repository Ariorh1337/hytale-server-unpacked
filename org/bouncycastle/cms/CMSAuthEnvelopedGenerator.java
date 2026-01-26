/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.cms.RecipientInfoGenerator;

public class CMSAuthEnvelopedGenerator
extends CMSEnvelopedGenerator {
    public static final String AES128_CCM = CMSAlgorithm.AES128_CCM.getId();
    public static final String AES192_CCM = CMSAlgorithm.AES192_CCM.getId();
    public static final String AES256_CCM = CMSAlgorithm.AES256_CCM.getId();
    public static final String AES128_GCM = CMSAlgorithm.AES128_GCM.getId();
    public static final String AES192_GCM = CMSAlgorithm.AES192_GCM.getId();
    public static final String AES256_GCM = CMSAlgorithm.AES256_GCM.getId();
    public static final String ChaCha20Poly1305 = CMSAlgorithm.ChaCha20Poly1305.getId();
    protected CMSAttributeTableGenerator authAttrsGenerator = null;
    protected CMSAttributeTableGenerator unauthAttrsGenerator = null;
    protected OriginatorInfo originatorInfo;

    protected CMSAuthEnvelopedGenerator() {
    }

    public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.authAttrsGenerator = cMSAttributeTableGenerator;
    }

    public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unauthAttrsGenerator = cMSAttributeTableGenerator;
    }

    @Override
    public void setOriginatorInfo(OriginatorInformation originatorInformation) {
        this.originatorInfo = originatorInformation.toASN1Structure();
    }

    @Override
    public void addRecipientInfoGenerator(RecipientInfoGenerator recipientInfoGenerator) {
        this.recipientInfoGenerators.add(recipientInfoGenerator);
    }
}

