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
import org.bouncycastle.crypto.kems.ECIESKEMExtractor;
import org.bouncycastle.crypto.kems.ECIESKEMGenerator;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

public class ECIESKeyEncapsulation
implements KeyEncapsulation {
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private ECKeyParameters key;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKeyEncapsulation(DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKeyEncapsulation(DerivationFunction derivationFunction, SecureRandom secureRandom, boolean bl, boolean bl2, boolean bl3) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = bl;
        this.OldCofactorMode = bl ? false : bl2;
        this.SingleHashMode = bl3;
    }

    @Override
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ECKeyParameters)) {
            throw new IllegalArgumentException("EC key required");
        }
        this.key = (ECKeyParameters)cipherParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getCurve()), cipherParameters, CryptoServicePurpose.ANY));
    }

    @Override
    public CipherParameters encrypt(byte[] byArray, int n, int n2) throws IllegalArgumentException {
        if (!(this.key instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        ECIESKEMGenerator eCIESKEMGenerator = new ECIESKEMGenerator(n2, this.kdf, this.rnd, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode);
        SecretWithEncapsulation secretWithEncapsulation = eCIESKEMGenerator.generateEncapsulated(this.key);
        byte[] byArray2 = secretWithEncapsulation.getEncapsulation();
        System.arraycopy(byArray2, 0, byArray, n, byArray2.length);
        return new KeyParameter(secretWithEncapsulation.getSecret());
    }

    public CipherParameters encrypt(byte[] byArray, int n) {
        return this.encrypt(byArray, 0, n);
    }

    @Override
    public CipherParameters decrypt(byte[] byArray, int n, int n2, int n3) throws IllegalArgumentException {
        if (!(this.key instanceof ECPrivateKeyParameters)) {
            throw new IllegalArgumentException("Private key required for encryption");
        }
        ECPrivateKeyParameters eCPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
        ECIESKEMExtractor eCIESKEMExtractor = new ECIESKEMExtractor(eCPrivateKeyParameters, n3, this.kdf, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode);
        byte[] byArray2 = eCIESKEMExtractor.extractSecret(Arrays.copyOfRange(byArray, n, n + n2));
        return new KeyParameter(byArray2);
    }

    public CipherParameters decrypt(byte[] byArray, int n) {
        return this.decrypt(byArray, 0, byArray.length, n);
    }
}

