/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

class RSACoreEngine {
    static final String NO_LENSTRA_CHECK = "org.bouncycastle.rsa.no_lenstra_check";
    private RSAKeyParameters key;
    private boolean forEncryption;

    RSACoreEngine() {
    }

    public void init(boolean bl, CipherParameters cipherParameters) {
        if (cipherParameters instanceof ParametersWithRandom) {
            ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            cipherParameters = parametersWithRandom.getParameters();
        }
        this.forEncryption = bl;
        this.key = (RSAKeyParameters)cipherParameters;
        int n = ConstraintUtils.bitsOfSecurityFor(this.key.getModulus());
        CryptoServicePurpose cryptoServicePurpose = this.getPurpose(this.key.isPrivate(), bl);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", n, this.key, cryptoServicePurpose));
    }

    public int getInputBlockSize() {
        int n = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (n + 7) / 8 - 1;
        }
        return (n + 7) / 8;
    }

    public int getOutputBlockSize() {
        int n = this.key.getModulus().bitLength();
        if (this.forEncryption) {
            return (n + 7) / 8;
        }
        return (n + 7) / 8 - 1;
    }

    public BigInteger convertInput(byte[] byArray, int n, int n2) {
        byte[] byArray2;
        if (n2 > this.getInputBlockSize() + 1) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (n2 == this.getInputBlockSize() + 1 && !this.forEncryption) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        if (n != 0 || n2 != byArray.length) {
            byArray2 = new byte[n2];
            System.arraycopy(byArray, n, byArray2, 0, n2);
        } else {
            byArray2 = byArray;
        }
        BigInteger bigInteger = new BigInteger(1, byArray2);
        if (bigInteger.compareTo(this.key.getModulus()) >= 0) {
            throw new DataLengthException("input too large for RSA cipher.");
        }
        return bigInteger;
    }

    public byte[] convertOutput(BigInteger bigInteger) {
        byte[] byArray;
        byte[] byArray2 = bigInteger.toByteArray();
        if (this.forEncryption) {
            if (byArray2[0] == 0 && byArray2.length > this.getOutputBlockSize()) {
                byte[] byArray3 = new byte[byArray2.length - 1];
                System.arraycopy(byArray2, 1, byArray3, 0, byArray3.length);
                return byArray3;
            }
            if (byArray2.length < this.getOutputBlockSize()) {
                byte[] byArray4 = new byte[this.getOutputBlockSize()];
                System.arraycopy(byArray2, 0, byArray4, byArray4.length - byArray2.length, byArray2.length);
                return byArray4;
            }
            return byArray2;
        }
        if (byArray2[0] == 0) {
            byArray = new byte[byArray2.length - 1];
            System.arraycopy(byArray2, 1, byArray, 0, byArray.length);
        } else {
            byArray = new byte[byArray2.length];
            System.arraycopy(byArray2, 0, byArray, 0, byArray.length);
        }
        Arrays.fill(byArray2, (byte)0);
        return byArray;
    }

    public BigInteger processBlock(BigInteger bigInteger) {
        RSAPrivateCrtKeyParameters rSAPrivateCrtKeyParameters;
        BigInteger bigInteger2;
        if (this.key instanceof RSAPrivateCrtKeyParameters && ((bigInteger2 = (rSAPrivateCrtKeyParameters = (RSAPrivateCrtKeyParameters)this.key).getPublicExponent()) != null || Properties.isOverrideSet(NO_LENSTRA_CHECK))) {
            BigInteger bigInteger3;
            BigInteger bigInteger4 = rSAPrivateCrtKeyParameters.getP();
            BigInteger bigInteger5 = rSAPrivateCrtKeyParameters.getQ();
            BigInteger bigInteger6 = rSAPrivateCrtKeyParameters.getDP();
            BigInteger bigInteger7 = rSAPrivateCrtKeyParameters.getDQ();
            BigInteger bigInteger8 = rSAPrivateCrtKeyParameters.getQInv();
            BigInteger bigInteger9 = bigInteger.remainder(bigInteger4).modPow(bigInteger6, bigInteger4);
            BigInteger bigInteger10 = bigInteger.remainder(bigInteger5).modPow(bigInteger7, bigInteger5);
            BigInteger bigInteger11 = bigInteger9.subtract(bigInteger10);
            bigInteger11 = bigInteger11.multiply(bigInteger8);
            bigInteger11 = bigInteger11.mod(bigInteger4);
            BigInteger bigInteger12 = bigInteger11.multiply(bigInteger5).add(bigInteger10);
            if (bigInteger2 != null && !(bigInteger3 = bigInteger12.modPow(bigInteger2, rSAPrivateCrtKeyParameters.getModulus())).equals(bigInteger)) {
                throw new IllegalStateException("RSA engine faulty decryption/signing detected");
            }
            return bigInteger12;
        }
        return bigInteger.modPow(this.key.getExponent(), this.key.getModulus());
    }

    private CryptoServicePurpose getPurpose(boolean bl, boolean bl2) {
        boolean bl3;
        boolean bl4 = bl && bl2;
        boolean bl5 = !bl && bl2;
        boolean bl6 = bl3 = !bl && !bl2;
        if (bl4) {
            return CryptoServicePurpose.SIGNING;
        }
        if (bl5) {
            return CryptoServicePurpose.ENCRYPTION;
        }
        if (bl3) {
            return CryptoServicePurpose.VERIFYING;
        }
        return CryptoServicePurpose.DECRYPTION;
    }
}

