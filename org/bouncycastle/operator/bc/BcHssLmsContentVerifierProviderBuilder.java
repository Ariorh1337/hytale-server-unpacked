/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import java.io.IOException;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentVerifierProviderBuilder;
import org.bouncycastle.operator.bc.BcHssLmsContentSignerBuilder;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;

public class BcHssLmsContentVerifierProviderBuilder
extends BcContentVerifierProviderBuilder {
    @Override
    protected Signer createSigner(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        return new BcHssLmsContentSignerBuilder.HssSigner();
    }

    @Override
    protected AsymmetricKeyParameter extractKeyParameters(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }
}

