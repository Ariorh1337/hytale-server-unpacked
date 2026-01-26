/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.its.operator;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.operator.ContentVerifier;
import org.bouncycastle.operator.OperatorCreationException;

public interface ITSContentVerifierProvider {
    public boolean hasAssociatedCertificate();

    public ITSCertificate getAssociatedCertificate();

    public ContentVerifier get(int var1) throws OperatorCreationException;
}

