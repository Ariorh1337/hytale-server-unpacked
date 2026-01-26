/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.cms.CMSAttributeTableGenerator;

public class CMSEncryptedGenerator {
    protected CMSAttributeTableGenerator unprotectedAttributeGenerator = null;

    protected CMSEncryptedGenerator() {
    }

    public void setUnprotectedAttributeGenerator(CMSAttributeTableGenerator cMSAttributeTableGenerator) {
        this.unprotectedAttributeGenerator = cMSAttributeTableGenerator;
    }
}

