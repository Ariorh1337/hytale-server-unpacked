/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.util;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.EncapsulatedSecretExtractor;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.DEROtherInfo;
import org.bouncycastle.pqc.crypto.KEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMExtractor;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMExtractor;
import org.bouncycastle.pqc.crypto.ntru.NTRUKEMGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.crypto.ntru.NTRUPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;

public class PQCOtherInfoGenerator {
    protected final DEROtherInfo.Builder otherInfoBuilder;
    protected final SecureRandom random;
    protected boolean used = false;

    public PQCOtherInfoGenerator(AlgorithmIdentifier algorithmIdentifier, byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
        this.otherInfoBuilder = new DEROtherInfo.Builder(algorithmIdentifier, byArray, byArray2);
        this.random = secureRandom;
    }

    private static byte[] getEncoded(AsymmetricKeyParameter asymmetricKeyParameter) {
        try {
            return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricKeyParameter).getEncoded();
        }
        catch (IOException iOException) {
            return null;
        }
    }

    private static AsymmetricKeyParameter getPublicKey(byte[] byArray) throws IOException {
        return PublicKeyFactory.createKey(byArray);
    }

    public static class PartyU
    extends PQCOtherInfoGenerator {
        private AsymmetricCipherKeyPair aKp;
        private EncapsulatedSecretExtractor encSE;

        public PartyU(KEMParameters kEMParameters, AlgorithmIdentifier algorithmIdentifier, byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
            super(algorithmIdentifier, byArray, byArray2, secureRandom);
            if (kEMParameters instanceof MLKEMParameters) {
                MLKEMKeyPairGenerator mLKEMKeyPairGenerator = new MLKEMKeyPairGenerator();
                mLKEMKeyPairGenerator.init(new MLKEMKeyGenerationParameters(secureRandom, (MLKEMParameters)kEMParameters));
                this.aKp = mLKEMKeyPairGenerator.generateKeyPair();
                this.encSE = new MLKEMExtractor((MLKEMPrivateKeyParameters)this.aKp.getPrivate());
            } else if (kEMParameters instanceof NTRUParameters) {
                NTRUKeyPairGenerator nTRUKeyPairGenerator = new NTRUKeyPairGenerator();
                nTRUKeyPairGenerator.init(new NTRUKeyGenerationParameters(secureRandom, (NTRUParameters)kEMParameters));
                this.aKp = nTRUKeyPairGenerator.generateKeyPair();
                this.encSE = new NTRUKEMExtractor((NTRUPrivateKeyParameters)this.aKp.getPrivate());
            } else {
                throw new IllegalArgumentException("unknown KEMParameters");
            }
        }

        public PQCOtherInfoGenerator withSuppPubInfo(byte[] byArray) {
            this.otherInfoBuilder.withSuppPubInfo(byArray);
            return this;
        }

        public byte[] getSuppPrivInfoPartA() {
            return PQCOtherInfoGenerator.getEncoded(this.aKp.getPublic());
        }

        public DEROtherInfo generate(byte[] byArray) {
            this.otherInfoBuilder.withSuppPrivInfo(this.encSE.extractSecret(byArray));
            return this.otherInfoBuilder.build();
        }
    }

    public static class PartyV
    extends PQCOtherInfoGenerator {
        private EncapsulatedSecretGenerator encSG;

        public PartyV(KEMParameters kEMParameters, AlgorithmIdentifier algorithmIdentifier, byte[] byArray, byte[] byArray2, SecureRandom secureRandom) {
            super(algorithmIdentifier, byArray, byArray2, secureRandom);
            if (kEMParameters instanceof MLKEMParameters) {
                this.encSG = new MLKEMGenerator(secureRandom);
            } else if (kEMParameters instanceof NTRUParameters) {
                this.encSG = new NTRUKEMGenerator(secureRandom);
            } else {
                throw new IllegalArgumentException("unknown KEMParameters");
            }
        }

        public PQCOtherInfoGenerator withSuppPubInfo(byte[] byArray) {
            this.otherInfoBuilder.withSuppPubInfo(byArray);
            return this;
        }

        public byte[] getSuppPrivInfoPartB(byte[] byArray) {
            this.used = false;
            try {
                SecretWithEncapsulation secretWithEncapsulation = this.encSG.generateEncapsulated(PQCOtherInfoGenerator.getPublicKey(byArray));
                this.otherInfoBuilder.withSuppPrivInfo(secretWithEncapsulation.getSecret());
                return secretWithEncapsulation.getEncapsulation();
            }
            catch (IOException iOException) {
                throw new IllegalArgumentException("cannot decode public key");
            }
        }

        public DEROtherInfo generate() {
            if (this.used) {
                throw new IllegalStateException("builder already used");
            }
            this.used = true;
            return this.otherInfoBuilder.build();
        }
    }
}

