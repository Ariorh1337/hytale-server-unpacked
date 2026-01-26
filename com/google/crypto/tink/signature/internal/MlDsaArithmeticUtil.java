/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature.internal;

import java.security.GeneralSecurityException;
import java.util.Arrays;

final class MlDsaArithmeticUtil {
    private MlDsaArithmeticUtil() {
    }

    static final class RingZq {
        static final RingZq INVALID = new RingZq(-1);
        static final int Q = 8380417;
        final int r;

        RingZq(int r) {
            if ((r < 0 || r >= 8380417) && INVALID != null) {
                this.r = RingZq.INVALID.r;
                return;
            }
            this.r = r;
        }

        RingZq plus(RingZq other) {
            return new RingZq((this.r + other.r) % 8380417);
        }

        RingZq minus(RingZq other) {
            return new RingZq((this.r - other.r + 8380417) % 8380417);
        }

        RingZq multiply(RingZq other) {
            return new RingZq((int)((long)this.r * (long)other.r % 8380417L));
        }

        RingZq negative() {
            return new RingZq((8380417 - this.r) % 8380417);
        }

        RingZqPair power2Round() {
            int rPlus = this.r % 8380417;
            int rZero = ((rPlus + 4096 - 1 & 0x1FFF) - 4095 + 8380417) % 8380417;
            int rOne = (rPlus - rZero + 8380417) % 8380417 >> 13;
            return new RingZqPair(rOne, rZero);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RingZq)) {
                return false;
            }
            RingZq other = (RingZq)o;
            return this.r == other.r;
        }

        public int hashCode() {
            return Integer.hashCode(this.r);
        }
    }

    static final class RingZqPair {
        final RingZq r1;
        final RingZq r0;

        RingZqPair(int r1, int r0) {
            this.r1 = new RingZq(r1);
            this.r0 = new RingZq(r0);
        }
    }

    static final class PolyRq {
        final RingZq[] polynomial = new RingZq[256];

        static PolyRq copyFromVector(RingTq vector) {
            PolyRq result = new PolyRq();
            System.arraycopy(vector.vector, 0, result.polynomial, 0, 256);
            return result;
        }

        PolyRq() {
            for (int i = 0; i < 256; ++i) {
                this.polynomial[i] = new RingZq(0);
            }
        }

        PolyRq plus(PolyRq other) {
            PolyRq result = new PolyRq();
            for (int i = 0; i < 256; ++i) {
                result.polynomial[i] = this.polynomial[i].plus(other.polynomial[i]);
            }
            return result;
        }

        PolyRqPair power2Round() {
            PolyRq t1Bold = new PolyRq();
            PolyRq t0Bold = new PolyRq();
            for (int i = 0; i < 256; ++i) {
                RingZqPair result = this.polynomial[i].power2Round();
                t1Bold.polynomial[i] = result.r1;
                t0Bold.polynomial[i] = result.r0;
            }
            return new PolyRqPair(t1Bold, t0Bold);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof PolyRq)) {
                return false;
            }
            PolyRq other = (PolyRq)o;
            return Arrays.equals(this.polynomial, other.polynomial);
        }

        public int hashCode() {
            return Arrays.hashCode(this.polynomial);
        }
    }

    static final class PolyRqPair {
        final PolyRq t1Bold;
        final PolyRq t0Bold;

        PolyRqPair(PolyRq t1Bold, PolyRq t0Bold) {
            this.t1Bold = t1Bold;
            this.t0Bold = t0Bold;
        }
    }

    static final class VectorRq {
        final PolyRq[] vector;

        VectorRq(int l) {
            this.vector = new PolyRq[l];
            for (int i = 0; i < l; ++i) {
                this.vector[i] = new PolyRq();
            }
        }
    }

    static final class VectorRqPair {
        VectorRq s1;
        VectorRq s2;

        VectorRqPair(int l1, int l2) {
            this.s1 = new VectorRq(l1);
            this.s2 = new VectorRq(l2);
        }
    }

    static final class RingTq {
        final RingZq[] vector = new RingZq[256];

        RingTq() {
            for (int i = 0; i < 256; ++i) {
                this.vector[i] = new RingZq(0);
            }
        }

        static RingTq copyFromPolynomial(PolyRq polynomial) {
            RingTq result = new RingTq();
            System.arraycopy(polynomial.polynomial, 0, result.vector, 0, 256);
            return result;
        }

        RingTq plus(RingTq other) {
            RingTq result = new RingTq();
            for (int i = 0; i < 256; ++i) {
                result.vector[i] = this.vector[i].plus(other.vector[i]);
            }
            return result;
        }

        RingTq multiply(RingTq other) {
            RingTq result = new RingTq();
            for (int i = 0; i < 256; ++i) {
                result.vector[i] = this.vector[i].multiply(other.vector[i]);
            }
            return result;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RingTq)) {
                return false;
            }
            RingTq other = (RingTq)o;
            return Arrays.equals(this.vector, other.vector);
        }

        public int hashCode() {
            return Arrays.hashCode(this.vector);
        }
    }

    static final class VectorTq {
        final RingTq[] vector;

        VectorTq(int l) {
            this.vector = new RingTq[l];
            for (int i = 0; i < l; ++i) {
                this.vector[i] = new RingTq();
            }
        }
    }

    static final class MatrixTq {
        final RingTq[][] matrix;

        MatrixTq(int k, int l) throws GeneralSecurityException {
            if (!(k == 6 && l == 5 || k == 8 && l == 7)) {
                throw new GeneralSecurityException("Wrong size of the ML-DSA matrix: k=" + k + ", l=" + l);
            }
            this.matrix = new RingTq[k][l];
            for (int i = 0; i < k; ++i) {
                for (int j = 0; j < l; ++j) {
                    this.matrix[i][j] = new RingTq();
                }
            }
        }

        VectorTq multiplyVector(VectorTq other) throws GeneralSecurityException {
            if (this.matrix[0].length != other.vector.length) {
                throw new GeneralSecurityException("Invalid parameters for matrix multiplication: matrix size (" + this.matrix.length + ", " + this.matrix[0].length + "), vector size " + other.vector.length);
            }
            VectorTq result = new VectorTq(this.matrix.length);
            for (int i = 0; i < this.matrix.length; ++i) {
                for (int j = 0; j < other.vector.length; ++j) {
                    result.vector[i] = result.vector[i].plus(this.matrix[i][j].multiply(other.vector[j]));
                }
            }
            return result;
        }
    }
}

