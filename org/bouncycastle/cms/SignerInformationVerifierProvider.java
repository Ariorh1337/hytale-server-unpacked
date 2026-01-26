/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.cms;

import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.operator.OperatorCreationException;

public interface SignerInformationVerifierProvider {
    public SignerInformationVerifier get(SignerId var1) throws OperatorCreationException;
}

