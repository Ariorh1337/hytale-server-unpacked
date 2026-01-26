/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import java.math.BigInteger;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.math.Primes;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Properties;

public class RSAKeyParameters
extends AsymmetricKeyParameter {
    private static final BigIntegers.Cache validated = new BigIntegers.Cache();
    private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger("8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f", 16);
    private BigInteger modulus;
    private BigInteger exponent;

    public RSAKeyParameters(boolean bl, BigInteger bigInteger, BigInteger bigInteger2) {
        this(bl, bigInteger, bigInteger2, false);
    }

    public RSAKeyParameters(boolean bl, BigInteger bigInteger, BigInteger bigInteger2, boolean bl2) {
        super(bl);
        if (!bl && (bigInteger2.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA publicExponent is even");
        }
        this.modulus = validated.contains(bigInteger) ? bigInteger : RSAKeyParameters.validate(bigInteger, bl2);
        this.exponent = bigInteger2;
    }

    private static boolean hasAnySmallFactors(BigInteger bigInteger) {
        BigInteger bigInteger2 = bigInteger;
        BigInteger bigInteger3 = SMALL_PRIMES_PRODUCT;
        if (bigInteger.bitLength() < SMALL_PRIMES_PRODUCT.bitLength()) {
            bigInteger2 = SMALL_PRIMES_PRODUCT;
            bigInteger3 = bigInteger;
        }
        return !BigIntegers.modOddIsCoprimeVar(bigInteger2, bigInteger3);
    }

    private static BigInteger validate(BigInteger bigInteger, boolean bl) {
        Primes.MROutput mROutput;
        if (bl) {
            validated.add(bigInteger);
            return bigInteger;
        }
        if ((bigInteger.intValue() & 1) == 0) {
            throw new IllegalArgumentException("RSA modulus is even");
        }
        if (Properties.isOverrideSet("org.bouncycastle.rsa.allow_unsafe_mod")) {
            return bigInteger;
        }
        int n = Properties.asInteger("org.bouncycastle.rsa.max_size", 16384);
        if (n < bigInteger.bitLength()) {
            throw new IllegalArgumentException("RSA modulus out of range");
        }
        if (RSAKeyParameters.hasAnySmallFactors(bigInteger)) {
            throw new IllegalArgumentException("RSA modulus has a small prime factor");
        }
        int n2 = bigInteger.bitLength() / 2;
        int n3 = Properties.asInteger("org.bouncycastle.rsa.max_mr_tests", RSAKeyParameters.getMRIterations(n2));
        if (n3 > 0 && !(mROutput = Primes.enhancedMRProbablePrimeTest(bigInteger, CryptoServicesRegistrar.getSecureRandom(), n3)).isProvablyComposite()) {
            throw new IllegalArgumentException("RSA modulus is not composite");
        }
        validated.add(bigInteger);
        return bigInteger;
    }

    private static int getMRIterations(int n) {
        int n2 = n >= 1536 ? 3 : (n >= 1024 ? 4 : (n >= 512 ? 7 : 50));
        return n2;
    }

    public BigInteger getModulus() {
        return this.modulus;
    }

    public BigInteger getExponent() {
        return this.exponent;
    }
}

