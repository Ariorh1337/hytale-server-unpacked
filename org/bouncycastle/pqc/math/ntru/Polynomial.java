/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

public abstract class Polynomial {
    public short[] coeffs;
    protected NTRUParameterSet params;

    public Polynomial(NTRUParameterSet nTRUParameterSet) {
        this.coeffs = new short[nTRUParameterSet.n()];
        this.params = nTRUParameterSet;
    }

    static short bothNegativeMask(short s, short s2) {
        return (short)((s & s2) >>> 15);
    }

    static short mod3(short s) {
        return (short)((s & 0xFFFF) % 3);
    }

    static byte mod3(byte by) {
        return (byte)((by & 0xFF) % 3);
    }

    static int modQ(int n, int n2) {
        return n % n2;
    }

    public void mod3PhiN() {
        int n = this.params.n();
        for (int i = 0; i < n; ++i) {
            this.coeffs[i] = Polynomial.mod3((short)(this.coeffs[i] + 2 * this.coeffs[n - 1]));
        }
    }

    public void modQPhiN() {
        int n = this.params.n();
        for (int i = 0; i < n; ++i) {
            this.coeffs[i] = (short)(this.coeffs[i] - this.coeffs[n - 1]);
        }
    }

    public abstract byte[] sqToBytes(int var1);

    public abstract void sqFromBytes(byte[] var1);

    public byte[] rqSumZeroToBytes(int n) {
        return this.sqToBytes(n);
    }

    public void rqSumZeroFromBytes(byte[] byArray) {
        int n = this.coeffs.length;
        this.sqFromBytes(byArray);
        this.coeffs[n - 1] = 0;
        for (int i = 0; i < this.params.packDegree(); ++i) {
            int n2 = n - 1;
            this.coeffs[n2] = (short)(this.coeffs[n2] - this.coeffs[i]);
        }
    }

    public byte[] s3ToBytes(int n) {
        byte[] byArray = new byte[n];
        this.s3ToBytes(byArray, 0);
        return byArray;
    }

    public void s3ToBytes(byte[] byArray, int n) {
        int n2;
        int n3;
        int n4;
        int n5 = this.params.packDegree();
        int n6 = n5 - 5;
        for (n4 = 0; n4 <= n6; n4 += 5) {
            n3 = this.coeffs[n4 + 0] & 0xFF;
            n2 = (this.coeffs[n4 + 1] & 0xFF) * 3;
            int n7 = (this.coeffs[n4 + 2] & 0xFF) * 9;
            int n8 = (this.coeffs[n4 + 3] & 0xFF) * 27;
            int n9 = (this.coeffs[n4 + 4] & 0xFF) * 81;
            byArray[n++] = (byte)(n3 + n2 + n7 + n8 + n9);
        }
        if (n4 < n5) {
            n3 = n5 - 1;
            n2 = this.coeffs[n3] & 0xFF;
            while (--n3 >= n4) {
                n2 *= 3;
                n2 += this.coeffs[n3] & 0xFF;
            }
            byArray[n++] = (byte)n2;
        }
    }

    public void s3FromBytes(byte[] byArray) {
        byte by;
        int n;
        int n2 = this.coeffs.length;
        for (n = 0; n < this.params.packDegree() / 5; ++n) {
            by = byArray[n];
            this.coeffs[5 * n + 0] = by;
            this.coeffs[5 * n + 1] = (short)((by & 0xFF) * 171 >>> 9);
            this.coeffs[5 * n + 2] = (short)((by & 0xFF) * 57 >>> 9);
            this.coeffs[5 * n + 3] = (short)((by & 0xFF) * 19 >>> 9);
            this.coeffs[5 * n + 4] = (short)((by & 0xFF) * 203 >>> 14);
        }
        if (this.params.packDegree() > this.params.packDegree() / 5 * 5) {
            n = this.params.packDegree() / 5;
            by = byArray[n];
            int n3 = 0;
            while (5 * n + n3 < this.params.packDegree()) {
                this.coeffs[5 * n + n3] = by;
                by = (byte)((by & 0xFF) * 171 >> 9);
                ++n3;
            }
        }
        this.coeffs[n2 - 1] = 0;
        this.mod3PhiN();
    }

    public void sqMul(Polynomial polynomial, Polynomial polynomial2) {
        this.rqMul(polynomial, polynomial2);
        this.modQPhiN();
    }

    public void rqMul(Polynomial polynomial, Polynomial polynomial2) {
        int n = this.coeffs.length;
        for (int i = 0; i < n; ++i) {
            int n2;
            this.coeffs[i] = 0;
            for (n2 = 1; n2 < n - i; ++n2) {
                int n3 = i;
                this.coeffs[n3] = (short)(this.coeffs[n3] + polynomial.coeffs[i + n2] * polynomial2.coeffs[n - n2]);
            }
            for (n2 = 0; n2 < i + 1; ++n2) {
                int n4 = i;
                this.coeffs[n4] = (short)(this.coeffs[n4] + polynomial.coeffs[i - n2] * polynomial2.coeffs[n2]);
            }
        }
    }

    public void s3Mul(Polynomial polynomial, Polynomial polynomial2) {
        this.rqMul(polynomial, polynomial2);
        this.mod3PhiN();
    }

    public abstract void lift(Polynomial var1);

    public void rqToS3(Polynomial polynomial) {
        int n = this.coeffs.length;
        int n2 = 0;
        while (n2 < n) {
            this.coeffs[n2] = (short)Polynomial.modQ(polynomial.coeffs[n2] & 0xFFFF, this.params.q());
            short s = (short)(this.coeffs[n2] >>> this.params.logQ() - 1);
            int n3 = n2++;
            this.coeffs[n3] = (short)(this.coeffs[n3] + (s << 1 - (this.params.logQ() & 1)));
        }
        this.mod3PhiN();
    }

    public void r2Inv(Polynomial polynomial) {
        Polynomial polynomial2 = this.params.createPolynomial();
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = this.params.createPolynomial();
        Polynomial polynomial5 = this.params.createPolynomial();
        this.r2Inv(polynomial, polynomial2, polynomial3, polynomial4, polynomial5);
    }

    public void rqInv(Polynomial polynomial) {
        Polynomial polynomial2 = this.params.createPolynomial();
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = this.params.createPolynomial();
        Polynomial polynomial5 = this.params.createPolynomial();
        this.rqInv(polynomial, polynomial2, polynomial3, polynomial4, polynomial5);
    }

    public void s3Inv(Polynomial polynomial) {
        Polynomial polynomial2 = this.params.createPolynomial();
        Polynomial polynomial3 = this.params.createPolynomial();
        Polynomial polynomial4 = this.params.createPolynomial();
        Polynomial polynomial5 = this.params.createPolynomial();
        this.s3Inv(polynomial, polynomial2, polynomial3, polynomial4, polynomial5);
    }

    void r2Inv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        int n;
        int n2 = this.coeffs.length;
        polynomial5.coeffs[0] = 1;
        for (n = 0; n < n2; ++n) {
            polynomial2.coeffs[n] = 1;
        }
        for (n = 0; n < n2 - 1; ++n) {
            polynomial3.coeffs[n2 - 2 - n] = (short)((polynomial.coeffs[n] ^ polynomial.coeffs[n2 - 1]) & 1);
        }
        polynomial3.coeffs[n2 - 1] = 0;
        int n3 = 1;
        for (int i = 0; i < 2 * (n2 - 1) - 1; ++i) {
            for (n = n2 - 1; n > 0; --n) {
                polynomial4.coeffs[n] = polynomial4.coeffs[n - 1];
            }
            polynomial4.coeffs[0] = 0;
            short s = (short)(polynomial3.coeffs[0] & polynomial2.coeffs[0]);
            short s2 = Polynomial.bothNegativeMask((short)(-n3), -polynomial3.coeffs[0]);
            n3 = (short)(n3 ^ s2 & (n3 ^ -n3));
            n3 = (short)(n3 + 1);
            n = 0;
            while (n < n2) {
                short s3 = (short)(s2 & (polynomial2.coeffs[n] ^ polynomial3.coeffs[n]));
                int n4 = n;
                polynomial2.coeffs[n4] = (short)(polynomial2.coeffs[n4] ^ s3);
                int n5 = n;
                polynomial3.coeffs[n5] = (short)(polynomial3.coeffs[n5] ^ s3);
                s3 = (short)(s2 & (polynomial4.coeffs[n] ^ polynomial5.coeffs[n]));
                int n6 = n;
                polynomial4.coeffs[n6] = (short)(polynomial4.coeffs[n6] ^ s3);
                int n7 = n++;
                polynomial5.coeffs[n7] = (short)(polynomial5.coeffs[n7] ^ s3);
            }
            for (n = 0; n < n2; ++n) {
                polynomial3.coeffs[n] = (short)(polynomial3.coeffs[n] ^ s & polynomial2.coeffs[n]);
            }
            for (n = 0; n < n2; ++n) {
                polynomial5.coeffs[n] = (short)(polynomial5.coeffs[n] ^ s & polynomial4.coeffs[n]);
            }
            for (n = 0; n < n2 - 1; ++n) {
                polynomial3.coeffs[n] = polynomial3.coeffs[n + 1];
            }
            polynomial3.coeffs[n2 - 1] = 0;
        }
        for (n = 0; n < n2 - 1; ++n) {
            this.coeffs[n] = polynomial4.coeffs[n2 - 2 - n];
        }
        this.coeffs[n2 - 1] = 0;
    }

    void rqInv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        polynomial2.r2Inv(polynomial);
        this.r2InvToRqInv(polynomial2, polynomial, polynomial3, polynomial4, polynomial5);
    }

    private void r2InvToRqInv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        int n;
        int n2 = this.coeffs.length;
        for (n = 0; n < n2; ++n) {
            polynomial3.coeffs[n] = -polynomial2.coeffs[n];
        }
        for (n = 0; n < n2; ++n) {
            this.coeffs[n] = polynomial.coeffs[n];
        }
        polynomial4.rqMul(this, polynomial3);
        polynomial4.coeffs[0] = (short)(polynomial4.coeffs[0] + 2);
        polynomial5.rqMul(polynomial4, this);
        polynomial4.rqMul(polynomial5, polynomial3);
        polynomial4.coeffs[0] = (short)(polynomial4.coeffs[0] + 2);
        this.rqMul(polynomial4, polynomial5);
        polynomial4.rqMul(this, polynomial3);
        polynomial4.coeffs[0] = (short)(polynomial4.coeffs[0] + 2);
        polynomial5.rqMul(polynomial4, this);
        polynomial4.rqMul(polynomial5, polynomial3);
        polynomial4.coeffs[0] = (short)(polynomial4.coeffs[0] + 2);
        this.rqMul(polynomial4, polynomial5);
    }

    void s3Inv(Polynomial polynomial, Polynomial polynomial2, Polynomial polynomial3, Polynomial polynomial4, Polynomial polynomial5) {
        short s;
        int n;
        int n2 = this.coeffs.length;
        polynomial5.coeffs[0] = 1;
        for (n = 0; n < n2; ++n) {
            polynomial2.coeffs[n] = 1;
        }
        for (n = 0; n < n2 - 1; ++n) {
            polynomial3.coeffs[n2 - 2 - n] = Polynomial.mod3((short)((polynomial.coeffs[n] & 3) + 2 * (polynomial.coeffs[n2 - 1] & 3)));
        }
        polynomial3.coeffs[n2 - 1] = 0;
        int n3 = 1;
        for (int i = 0; i < 2 * (n2 - 1) - 1; ++i) {
            for (n = n2 - 1; n > 0; --n) {
                polynomial4.coeffs[n] = polynomial4.coeffs[n - 1];
            }
            polynomial4.coeffs[0] = 0;
            s = Polynomial.mod3((byte)(2 * polynomial3.coeffs[0] * polynomial2.coeffs[0]));
            short s2 = Polynomial.bothNegativeMask((short)(-n3), -polynomial3.coeffs[0]);
            n3 = (short)(n3 ^ s2 & (n3 ^ -n3));
            n3 = (short)(n3 + 1);
            n = 0;
            while (n < n2) {
                short s3 = (short)(s2 & (polynomial2.coeffs[n] ^ polynomial3.coeffs[n]));
                int n4 = n;
                polynomial2.coeffs[n4] = (short)(polynomial2.coeffs[n4] ^ s3);
                int n5 = n;
                polynomial3.coeffs[n5] = (short)(polynomial3.coeffs[n5] ^ s3);
                s3 = (short)(s2 & (polynomial4.coeffs[n] ^ polynomial5.coeffs[n]));
                int n6 = n;
                polynomial4.coeffs[n6] = (short)(polynomial4.coeffs[n6] ^ s3);
                int n7 = n++;
                polynomial5.coeffs[n7] = (short)(polynomial5.coeffs[n7] ^ s3);
            }
            for (n = 0; n < n2; ++n) {
                polynomial3.coeffs[n] = Polynomial.mod3((byte)(polynomial3.coeffs[n] + s * polynomial2.coeffs[n]));
            }
            for (n = 0; n < n2; ++n) {
                polynomial5.coeffs[n] = Polynomial.mod3((byte)(polynomial5.coeffs[n] + s * polynomial4.coeffs[n]));
            }
            for (n = 0; n < n2 - 1; ++n) {
                polynomial3.coeffs[n] = polynomial3.coeffs[n + 1];
            }
            polynomial3.coeffs[n2 - 1] = 0;
        }
        s = polynomial2.coeffs[0];
        for (n = 0; n < n2 - 1; ++n) {
            this.coeffs[n] = Polynomial.mod3((byte)(s * polynomial4.coeffs[n2 - 2 - n]));
        }
        this.coeffs[n2 - 1] = 0;
    }

    public void z3ToZq() {
        int n = this.coeffs.length;
        for (int i = 0; i < n; ++i) {
            this.coeffs[i] = (short)(this.coeffs[i] | -(this.coeffs[i] >>> 1) & this.params.q() - 1);
        }
    }

    public void trinaryZqToZ3() {
        int n = this.coeffs.length;
        for (int i = 0; i < n; ++i) {
            this.coeffs[i] = (short)Polynomial.modQ(this.coeffs[i] & 0xFFFF, this.params.q());
            this.coeffs[i] = (short)(3 & (this.coeffs[i] ^ this.coeffs[i] >>> this.params.logQ() - 1));
        }
    }
}

