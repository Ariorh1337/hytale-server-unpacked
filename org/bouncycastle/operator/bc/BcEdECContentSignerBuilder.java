/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator.bc;

import org.bouncycastle.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcContentSignerBuilder;

public class BcEdECContentSignerBuilder
extends BcContentSignerBuilder {
    public BcEdECContentSignerBuilder(AlgorithmIdentifier algorithmIdentifier) {
        super(algorithmIdentifier, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
    }

    @Override
    protected Signer createSigner(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) throws OperatorCreationException {
        if (algorithmIdentifier.getAlgorithm().equals(EdECObjectIdentifiers.id_Ed25519)) {
            return new Ed25519Signer();
        }
        throw new IllegalStateException("unknown signature type");
    }
}

