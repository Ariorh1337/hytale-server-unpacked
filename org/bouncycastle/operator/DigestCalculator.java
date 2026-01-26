/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.operator;

import java.io.OutputStream;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public interface DigestCalculator {
    public static final AlgorithmIdentifier SHA_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256);
    public static final AlgorithmIdentifier SHA_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512);

    public AlgorithmIdentifier getAlgorithmIdentifier();

    public OutputStream getOutputStream();

    public byte[] getDigest();
}

