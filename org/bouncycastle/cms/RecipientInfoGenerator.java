/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.GenericKey;

public interface RecipientInfoGenerator {
    public RecipientInfo generate(GenericKey var1) throws CMSException;
}

