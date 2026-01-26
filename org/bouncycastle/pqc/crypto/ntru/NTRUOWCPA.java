/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.crypto.ntru.NTRUSampling;
import org.bouncycastle.pqc.crypto.ntru.OWCPADecryptResult;
import org.bouncycastle.pqc.crypto.ntru.OWCPAKeyPair;
import org.bouncycastle.pqc.crypto.ntru.PolynomialPair;
import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

class NTRUOWCPA {
    private final NTRUParameterSet params;
    private final NTRUSampling sampling;

    public NTRUOWCPA(NTRUParameterSet nTRUParameterSet) {
        this.params = nTRUParameterSet;
        this.sampling = new NTRUSampling(nTRUParameterSet);
    }

    public OWCPAKeyPair keypair(byte[] byArray) {
        byte[] byArray2 = new byte[this.params.owcpaSecretKeyBytes()];
        int n = this.params.n();
        int n2 = this.params.q();
        Polynomial polynomial = this.params.createPolynomial();
        Polynomial polynomial2 = this.params.createPolynomial();
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = polynomial;
        Polynomial polynomial5 = polynomial;
        Polynomial polynomial6 = polynomial2;
        Polynomial polynomial7 = polynomial3;
        Polynomial polynomial8 = polynomial;
        Polynomial polynomial9 = polynomial;
        PolynomialPair polynomialPair = this.sampling.sampleFg(byArray);
        Polynomial polynomial10 = polynomialPair.f();
        Polynomial polynomial11 = polynomialPair.g();
        polynomial4.s3Inv(polynomial10);
        polynomial10.s3ToBytes(byArray2, 0);
        polynomial4.s3ToBytes(byArray2, this.params.packTrinaryBytes());
        polynomial10.z3ToZq();
        polynomial11.z3ToZq();
        if (this.params instanceof NTRUHRSSParameterSet) {
            for (int i = n - 1; i > 0; --i) {
                polynomial11.coeffs[i] = (short)(3 * (polynomial11.coeffs[i - 1] - polynomial11.coeffs[i]));
            }
            polynomial11.coeffs[0] = (short)(-(3 * polynomial11.coeffs[0]));
        } else {
            for (int i = 0; i < n; ++i) {
                polynomial11.coeffs[i] = (short)(3 * polynomial11.coeffs[i]);
            }
        }
        polynomial5.rqMul(polynomial11, polynomial10);
        polynomial6.rqInv(polynomial5);
        polynomial7.rqMul(polynomial6, polynomial10);
        polynomial8.sqMul(polynomial7, polynomial10);
        byte[] byArray3 = polynomial8.sqToBytes(byArray2.length - 2 * this.params.packTrinaryBytes());
        System.arraycopy(byArray3, 0, byArray2, 2 * this.params.packTrinaryBytes(), byArray3.length);
        polynomial7.rqMul(polynomial6, polynomial11);
        polynomial9.rqMul(polynomial7, polynomial11);
        byte[] byArray4 = polynomial9.rqSumZeroToBytes(this.params.owcpaPublicKeyBytes());
        return new OWCPAKeyPair(byArray4, byArray2);
    }

    public byte[] encrypt(Polynomial polynomial, Polynomial polynomial2, byte[] byArray) {
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = this.params.createPolynomial();
        Polynomial polynomial5 = polynomial3;
        Polynomial polynomial6 = polynomial3;
        Polynomial polynomial7 = polynomial4;
        polynomial5.rqSumZeroFromBytes(byArray);
        polynomial7.rqMul(polynomial, polynomial5);
        polynomial6.lift(polynomial2);
        for (int i = 0; i < this.params.n(); ++i) {
            int n = i;
            polynomial7.coeffs[n] = (short)(polynomial7.coeffs[n] + polynomial6.coeffs[i]);
        }
        return polynomial7.rqSumZeroToBytes(this.params.ntruCiphertextBytes());
    }

    public OWCPADecryptResult decrypt(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = byArray2;
        byte[] byArray4 = new byte[this.params.owcpaMsgBytes()];
        Polynomial polynomial = this.params.createPolynomial();
        Polynomial polynomial2 = this.params.createPolynomial();
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = this.params.createPolynomial();
        Polynomial polynomial5 = polynomial;
        Polynomial polynomial6 = polynomial2;
        Polynomial polynomial7 = polynomial3;
        Polynomial polynomial8 = polynomial2;
        Polynomial polynomial9 = polynomial3;
        Polynomial polynomial10 = polynomial4;
        Polynomial polynomial11 = polynomial2;
        Polynomial polynomial12 = polynomial3;
        Polynomial polynomial13 = polynomial4;
        Polynomial polynomial14 = polynomial;
        polynomial5.rqSumZeroFromBytes(byArray);
        polynomial6.s3FromBytes(byArray3);
        polynomial6.z3ToZq();
        polynomial7.rqMul(polynomial5, polynomial6);
        polynomial8.rqToS3(polynomial7);
        polynomial9.s3FromBytes(Arrays.copyOfRange(byArray3, this.params.packTrinaryBytes(), byArray3.length));
        polynomial10.s3Mul(polynomial8, polynomial9);
        polynomial10.s3ToBytes(byArray4, this.params.packTrinaryBytes());
        int n = 0;
        n |= this.checkCiphertext(byArray);
        if (this.params instanceof NTRUHPSParameterSet) {
            n |= this.checkM((HPSPolynomial)polynomial10);
        }
        polynomial11.lift(polynomial10);
        for (int i = 0; i < this.params.n(); ++i) {
            polynomial14.coeffs[i] = (short)(polynomial5.coeffs[i] - polynomial11.coeffs[i]);
        }
        polynomial12.sqFromBytes(Arrays.copyOfRange(byArray3, 2 * this.params.packTrinaryBytes(), byArray3.length));
        polynomial13.sqMul(polynomial14, polynomial12);
        polynomial13.trinaryZqToZ3();
        polynomial13.s3ToBytes(byArray4, 0);
        return new OWCPADecryptResult(byArray4, n |= this.checkR(polynomial13));
    }

    private int checkCiphertext(byte[] byArray) {
        short s = byArray[this.params.ntruCiphertextBytes() - 1];
        s = (short)(s & 255 << 8 - (7 & this.params.logQ() * this.params.packDegree()));
        return 1 & ~s + 1 >>> 15;
    }

    private int checkR(Polynomial polynomial) {
        int n = 0;
        for (int i = 0; i < this.params.n() - 1; ++i) {
            short s = polynomial.coeffs[i];
            n |= s + 1 & this.params.q() - 4;
            n |= s + 2 & 4;
        }
        return 1 & ~(n |= polynomial.coeffs[this.params.n() - 1]) + 1 >>> 31;
    }

    private int checkM(HPSPolynomial hPSPolynomial) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        for (int i = 0; i < this.params.n() - 1; ++i) {
            n2 = (short)(n2 + (hPSPolynomial.coeffs[i] & 1));
            n3 = (short)(n3 + (hPSPolynomial.coeffs[i] & 2));
        }
        n |= n2 ^ n3 >>> 1;
        return 1 & ~(n |= n3 ^ ((NTRUHPSParameterSet)this.params).weight()) + 1 >>> 31;
    }
}

