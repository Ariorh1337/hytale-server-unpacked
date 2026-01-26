/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.ntru.NTRUOWCPA;
import org.bouncycastle.pqc.crypto.ntru.NTRUPublicKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUSampling;
import org.bouncycastle.pqc.crypto.ntru.PolynomialPair;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

public class NTRUKEMGenerator
implements EncapsulatedSecretGenerator {
    private final SecureRandom random;

    public NTRUKEMGenerator(SecureRandom secureRandom) {
        if (secureRandom == null) {
            throw new NullPointerException("'random' cannot be null");
        }
        this.random = secureRandom;
    }

    @Override
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        if (asymmetricKeyParameter == null) {
            throw new NullPointerException("'recipientKey' cannot be null");
        }
        NTRUPublicKeyParameters nTRUPublicKeyParameters = (NTRUPublicKeyParameters)asymmetricKeyParameter;
        NTRUParameterSet nTRUParameterSet = nTRUPublicKeyParameters.getParameters().getParameterSet();
        NTRUSampling nTRUSampling = new NTRUSampling(nTRUParameterSet);
        NTRUOWCPA nTRUOWCPA = new NTRUOWCPA(nTRUParameterSet);
        byte[] byArray = new byte[nTRUParameterSet.owcpaMsgBytes()];
        byte[] byArray2 = new byte[nTRUParameterSet.sampleRmBytes()];
        this.random.nextBytes(byArray2);
        PolynomialPair polynomialPair = nTRUSampling.sampleRm(byArray2);
        Polynomial polynomial = polynomialPair.r();
        Polynomial polynomial2 = polynomialPair.m();
        polynomial.s3ToBytes(byArray, 0);
        polynomial2.s3ToBytes(byArray, nTRUParameterSet.packTrinaryBytes());
        SHA3Digest sHA3Digest = new SHA3Digest(256);
        byte[] byArray3 = new byte[sHA3Digest.getDigestSize()];
        sHA3Digest.update(byArray, 0, byArray.length);
        sHA3Digest.doFinal(byArray3, 0);
        polynomial.z3ToZq();
        byte[] byArray4 = nTRUOWCPA.encrypt(polynomial, polynomial2, nTRUPublicKeyParameters.publicKey);
        byte[] byArray5 = Arrays.copyOfRange(byArray3, 0, nTRUParameterSet.sharedKeyBytes());
        Arrays.clear(byArray3);
        return new SecretWithEncapsulationImpl(byArray5, byArray4);
    }
}

