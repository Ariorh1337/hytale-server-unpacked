/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.kems;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.KeyEncapsulation;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.kems.RSAKEMExtractor;
import org.bouncycastle.crypto.kems.RSAKEMGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.util.Arrays;

public class RSAKeyEncapsulation
implements KeyEncapsulation {
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private RSAKeyParameters key;

    public RSAKeyEncapsulation(DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
    }

    @Override
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof RSAKeyParameters)) {
            throw new IllegalArgumentException("RSA key required");
        }
        this.key = (RSAKeyParameters)cipherParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSAKem", ConstraintUtils.bitsOfSecurityFor(this.key.getModulus()), cipherParameters, this.key.isPrivate() ? CryptoServicePurpose.DECRYPTION : CryptoServicePurpose.ENCRYPTION));
    }

    @Override
    public CipherParameters encrypt(byte[] byArray, int n, int n2) throws IllegalArgumentException {
        if (this.key.isPrivate()) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        RSAKEMGenerator rSAKEMGenerator = new RSAKEMGenerator(n2, this.kdf, this.rnd);
        SecretWithEncapsulation secretWithEncapsulation = rSAKEMGenerator.generateEncapsulated(this.key);
        byte[] byArray2 = secretWithEncapsulation.getEncapsulation();
        System.arraycopy(byArray2, 0, byArray, n, byArray2.length);
        return new KeyParameter(secretWithEncapsulation.getSecret());
    }

    public CipherParameters encrypt(byte[] byArray, int n) {
        return this.encrypt(byArray, 0, n);
    }

    @Override
    public CipherParameters decrypt(byte[] byArray, int n, int n2, int n3) throws IllegalArgumentException {
        if (!this.key.isPrivate()) {
            throw new IllegalArgumentException("Private key required for decryption");
        }
        RSAKEMExtractor rSAKEMExtractor = new RSAKEMExtractor(this.key, n3, this.kdf);
        byte[] byArray2 = rSAKEMExtractor.extractSecret(Arrays.copyOfRange(byArray, n, n + n2));
        return new KeyParameter(byArray2);
    }

    public CipherParameters decrypt(byte[] byArray, int n) {
        return this.decrypt(byArray, 0, byArray.length, n);
    }
}

