/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.FastDoubleMath;
import ch.randelshofer.fastdoubleparser.FastDoubleSwar;
import java.math.BigInteger;

final class FftMultiplier {
    public static final double COS_0_25 = Math.cos(0.7853981633974483);
    public static final double SIN_0_25 = Math.sin(0.7853981633974483);
    private static final int FFT_THRESHOLD = 33220;
    private static final int MAX_MAG_LENGTH = 0x4000000;
    private static final int ROOTS3_CACHE_SIZE = 20;
    private static final int ROOTS_CACHE2_SIZE = 20;
    private static final int TOOM_COOK_THRESHOLD = 1920;
    private static volatile ComplexVector[] ROOTS2_CACHE = new ComplexVector[20];
    private static volatile ComplexVector[] ROOTS3_CACHE = new ComplexVector[20];

    FftMultiplier() {
    }

    static int bitsPerFftPoint(int bitLen) {
        if (bitLen <= 9728) {
            return 19;
        }
        if (bitLen <= 18432) {
            return 18;
        }
        if (bitLen <= 69632) {
            return 17;
        }
        if (bitLen <= 262144) {
            return 16;
        }
        if (bitLen <= 983040) {
            return 15;
        }
        if (bitLen <= 0x380000) {
            return 14;
        }
        if (bitLen <= 0xD00000) {
            return 13;
        }
        if (bitLen <= 0x1800000) {
            return 12;
        }
        if (bitLen <= 0x5800000) {
            return 11;
        }
        if (bitLen <= 0x14000000) {
            return 10;
        }
        if (bitLen <= 0x48000000) {
            return 9;
        }
        return 8;
    }

    private static ComplexVector calculateRootsOfUnity(int n) {
        if (n == 1) {
            ComplexVector v = new ComplexVector(1);
            v.real(0, 1.0);
            v.imag(0, 0.0);
            return v;
        }
        ComplexVector roots = new ComplexVector(n);
        roots.set(0, 1.0, 0.0);
        double cos = COS_0_25;
        double sin = SIN_0_25;
        roots.set(n / 2, cos, sin);
        double angleTerm = 1.5707963267948966 / (double)n;
        for (int i = 1; i < n / 2; ++i) {
            double angle = angleTerm * (double)i;
            cos = Math.cos(angle);
            sin = Math.sin(angle);
            roots.set(i, cos, sin);
            roots.set(n - i, sin, cos);
        }
        return roots;
    }

    private static void fft(ComplexVector a, ComplexVector[] roots) {
        int s;
        int n = a.length;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        MutableComplex a0 = new MutableComplex();
        MutableComplex a1 = new MutableComplex();
        MutableComplex a2 = new MutableComplex();
        MutableComplex a3 = new MutableComplex();
        MutableComplex omega1 = new MutableComplex();
        MutableComplex omega2 = new MutableComplex();
        for (s = logN; s >= 2; s -= 2) {
            ComplexVector rootsS = roots[s - 2];
            int m = 1 << s;
            for (int i = 0; i < n; i += m) {
                for (int j = 0; j < m / 4; ++j) {
                    omega1.set(rootsS, j);
                    omega1.squareInto(omega2);
                    int idx0 = i + j;
                    int idx1 = i + j + m / 4;
                    int idx2 = i + j + m / 2;
                    int idx3 = i + j + m * 3 / 4;
                    a.addInto(idx0, a, idx1, a0);
                    a0.add(a, idx2);
                    a0.add(a, idx3);
                    a.subtractTimesIInto(idx0, a, idx1, a1);
                    a1.subtract(a, idx2);
                    a1.addTimesI(a, idx3);
                    a1.multiplyConjugate(omega1);
                    a.subtractInto(idx0, a, idx1, a2);
                    a2.add(a, idx2);
                    a2.subtract(a, idx3);
                    a2.multiplyConjugate(omega2);
                    a.addTimesIInto(idx0, a, idx1, a3);
                    a3.subtract(a, idx2);
                    a3.subtractTimesI(a, idx3);
                    a3.multiply(omega1);
                    a0.copyInto(a, idx0);
                    a1.copyInto(a, idx1);
                    a2.copyInto(a, idx2);
                    a3.copyInto(a, idx3);
                }
            }
        }
        if (s > 0) {
            for (int i = 0; i < n; i += 2) {
                a.copyInto(i, a0);
                a.copyInto(i + 1, a1);
                a.add(i, a1);
                a0.subtractInto(a1, a, i + 1);
            }
        }
    }

    private static void fft3(ComplexVector a0, ComplexVector a1, ComplexVector a2, int sign, double scale) {
        double omegaImag = (double)sign * -0.5 * Math.sqrt(3.0);
        for (int i = 0; i < a0.length; ++i) {
            double a0Real = a0.real(i) + a1.real(i) + a2.real(i);
            double a0Imag = a0.imag(i) + a1.imag(i) + a2.imag(i);
            double c = omegaImag * (a2.imag(i) - a1.imag(i));
            double d = omegaImag * (a1.real(i) - a2.real(i));
            double e = 0.5 * (a1.real(i) + a2.real(i));
            double f = 0.5 * (a1.imag(i) + a2.imag(i));
            double a1Real = a0.real(i) - e + c;
            double a1Imag = a0.imag(i) + d - f;
            double a2Real = a0.real(i) - e - c;
            double a2Imag = a0.imag(i) - d - f;
            a0.real(i, a0Real * scale);
            a0.imag(i, a0Imag * scale);
            a1.real(i, a1Real * scale);
            a1.imag(i, a1Imag * scale);
            a2.real(i, a2Real * scale);
            a2.imag(i, a2Imag * scale);
        }
    }

    private static void fftMixedRadix(ComplexVector a, ComplexVector[] roots2, ComplexVector roots3) {
        int i;
        int oneThird = a.length / 3;
        ComplexVector a0 = new ComplexVector(a, 0, oneThird);
        ComplexVector a1 = new ComplexVector(a, oneThird, oneThird * 2);
        ComplexVector a2 = new ComplexVector(a, oneThird * 2, a.length);
        FftMultiplier.fft3(a0, a1, a2, 1, 1.0);
        MutableComplex omega = new MutableComplex();
        for (i = 0; i < a.length / 4; ++i) {
            omega.set(roots3, i);
            a1.multiplyConjugate(i, omega);
            a2.multiplyConjugate(i, omega);
            a2.multiplyConjugate(i, omega);
        }
        for (i = a.length / 4; i < oneThird; ++i) {
            omega.set(roots3, i - a.length / 4);
            a1.multiplyConjugateTimesI(i, omega);
            a2.multiplyConjugateTimesI(i, omega);
            a2.multiplyConjugateTimesI(i, omega);
        }
        FftMultiplier.fft(a0, roots2);
        FftMultiplier.fft(a1, roots2);
        FftMultiplier.fft(a2, roots2);
    }

    static BigInteger fromFftVector(ComplexVector fftVec, int signum, int bitsPerFftPoint) {
        assert (bitsPerFftPoint <= 25) : bitsPerFftPoint + " does not fit into an int with slack";
        int fftLen = (int)Math.min((long)fftVec.length, 0x80000000L / (long)bitsPerFftPoint + 1L);
        int magLen = (int)(8L * ((long)fftLen * (long)bitsPerFftPoint + 31L) / 32L);
        byte[] mag = new byte[magLen];
        int base = 1 << bitsPerFftPoint;
        int bitMask = base - 1;
        int bitPadding = 32 - bitsPerFftPoint;
        long carry = 0L;
        int bitLength = mag.length * 8;
        int bitIdx = bitLength - bitsPerFftPoint;
        int magComponent = 0;
        int prevIdx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
        for (int part = 0; part <= 1; ++part) {
            for (int fftIdx = 0; fftIdx < fftLen; ++fftIdx) {
                long fftElem = Math.round(fftVec.part(fftIdx, part)) + carry;
                carry = fftElem >> bitsPerFftPoint;
                int idx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
                magComponent >>>= prevIdx - idx << 3;
                int shift = bitPadding - bitIdx + (idx << 3);
                FastDoubleSwar.writeIntBE(mag, idx, magComponent |= (int)((fftElem & (long)bitMask) << shift));
                prevIdx = idx;
                bitIdx -= bitsPerFftPoint;
            }
        }
        return new BigInteger(signum, mag);
    }

    private static ComplexVector[] getRootsOfUnity2(int logN) {
        ComplexVector[] roots = new ComplexVector[logN + 1];
        for (int i = logN; i >= 0; i -= 2) {
            if (i < 20) {
                if (ROOTS2_CACHE[i] == null) {
                    FftMultiplier.ROOTS2_CACHE[i] = FftMultiplier.calculateRootsOfUnity(1 << i);
                }
                roots[i] = ROOTS2_CACHE[i];
                continue;
            }
            roots[i] = FftMultiplier.calculateRootsOfUnity(1 << i);
        }
        return roots;
    }

    private static ComplexVector getRootsOfUnity3(int logN) {
        if (logN < 20) {
            if (ROOTS3_CACHE[logN] == null) {
                FftMultiplier.ROOTS3_CACHE[logN] = FftMultiplier.calculateRootsOfUnity(3 << logN);
            }
            return ROOTS3_CACHE[logN];
        }
        return FftMultiplier.calculateRootsOfUnity(3 << logN);
    }

    private static void ifft(ComplexVector a, ComplexVector[] roots) {
        int n = a.length;
        int logN = 31 - Integer.numberOfLeadingZeros(n);
        MutableComplex a0 = new MutableComplex();
        MutableComplex a1 = new MutableComplex();
        MutableComplex a2 = new MutableComplex();
        MutableComplex a3 = new MutableComplex();
        MutableComplex b0 = new MutableComplex();
        MutableComplex b1 = new MutableComplex();
        MutableComplex b2 = new MutableComplex();
        MutableComplex b3 = new MutableComplex();
        int s = 1;
        if (logN % 2 != 0) {
            for (int i = 0; i < n; i += 2) {
                a.copyInto(i + 1, a2);
                a.copyInto(i, a0);
                a.add(i, a2);
                a0.subtractInto(a2, a, i + 1);
            }
            ++s;
        }
        MutableComplex omega1 = new MutableComplex();
        MutableComplex omega2 = new MutableComplex();
        while (s <= logN) {
            ComplexVector rootsS = roots[s - 1];
            int m = 1 << s + 1;
            for (int i = 0; i < n; i += m) {
                for (int j = 0; j < m / 4; ++j) {
                    omega1.set(rootsS, j);
                    omega1.squareInto(omega2);
                    int idx0 = i + j;
                    int idx1 = i + j + m / 4;
                    int idx2 = i + j + m / 2;
                    int idx3 = i + j + m * 3 / 4;
                    a.copyInto(idx0, a0);
                    a.multiplyInto(idx1, omega1, a1);
                    a.multiplyInto(idx2, omega2, a2);
                    a.multiplyConjugateInto(idx3, omega1, a3);
                    a0.addInto(a1, b0);
                    b0.add(a2);
                    b0.add(a3);
                    a0.addTimesIInto(a1, b1);
                    b1.subtract(a2);
                    b1.subtractTimesI(a3);
                    a0.subtractInto(a1, b2);
                    b2.add(a2);
                    b2.subtract(a3);
                    a0.subtractTimesIInto(a1, b3);
                    b3.subtract(a2);
                    b3.addTimesI(a3);
                    b0.copyInto(a, idx0);
                    b1.copyInto(a, idx1);
                    b2.copyInto(a, idx2);
                    b3.copyInto(a, idx3);
                }
            }
            s += 2;
        }
        for (int i = 0; i < n; ++i) {
            a.timesTwoToThe(i, -logN);
        }
    }

    private static void ifftMixedRadix(ComplexVector a, ComplexVector[] roots2, ComplexVector roots3) {
        int i;
        int oneThird = a.length / 3;
        ComplexVector a0 = new ComplexVector(a, 0, oneThird);
        ComplexVector a1 = new ComplexVector(a, oneThird, oneThird * 2);
        ComplexVector a2 = new ComplexVector(a, oneThird * 2, a.length);
        FftMultiplier.ifft(a0, roots2);
        FftMultiplier.ifft(a1, roots2);
        FftMultiplier.ifft(a2, roots2);
        MutableComplex omega = new MutableComplex();
        for (i = 0; i < a.length / 4; ++i) {
            omega.set(roots3, i);
            a1.multiply(i, omega);
            a2.multiply(i, omega);
            a2.multiply(i, omega);
        }
        for (i = a.length / 4; i < oneThird; ++i) {
            omega.set(roots3, i - a.length / 4);
            a1.multiplyByIAnd(i, omega);
            a2.multiplyByIAnd(i, omega);
            a2.multiplyByIAnd(i, omega);
        }
        FftMultiplier.fft3(a0, a1, a2, -1, 0.3333333333333333);
    }

    static BigInteger multiply(BigInteger a, BigInteger b) {
        int ylen;
        assert (a != null) : "a==null";
        assert (b != null) : "b==null";
        if (b.signum() == 0 || a.signum() == 0) {
            return BigInteger.ZERO;
        }
        if (b == a) {
            return FftMultiplier.square(b);
        }
        int xlen = a.bitLength();
        if ((long)xlen + (long)(ylen = b.bitLength()) > 0x80000000L) {
            throw new ArithmeticException("BigInteger would overflow supported range");
        }
        if (xlen > 1920 && ylen > 1920 && (xlen > 33220 || ylen > 33220)) {
            return FftMultiplier.multiplyFft(a, b);
        }
        return a.multiply(b);
    }

    static BigInteger multiplyFft(BigInteger a, BigInteger b) {
        int logFFTLen;
        int fftLen2;
        int fftLen3;
        int bitsPerPoint;
        byte[] bMag;
        int signum = a.signum() * b.signum();
        byte[] aMag = (a.signum() < 0 ? a.negate() : a).toByteArray();
        int bitLen = Math.max(aMag.length, (bMag = (b.signum() < 0 ? b.negate() : b).toByteArray()).length) * 8;
        int fftLen = (bitLen + (bitsPerPoint = FftMultiplier.bitsPerFftPoint(bitLen)) - 1) / bitsPerPoint + 1;
        if (fftLen < (fftLen3 = (fftLen2 = 1 << (logFFTLen = 32 - Integer.numberOfLeadingZeros(fftLen - 1))) * 3 / 4) && logFFTLen > 3) {
            ComplexVector[] roots2 = FftMultiplier.getRootsOfUnity2(logFFTLen - 2);
            ComplexVector weights = FftMultiplier.getRootsOfUnity3(logFFTLen - 2);
            ComplexVector twiddles = FftMultiplier.getRootsOfUnity3(logFFTLen - 4);
            ComplexVector aVec = FftMultiplier.toFftVector(aMag, fftLen3, bitsPerPoint);
            aVec.applyWeights(weights);
            FftMultiplier.fftMixedRadix(aVec, roots2, twiddles);
            ComplexVector bVec = FftMultiplier.toFftVector(bMag, fftLen3, bitsPerPoint);
            bVec.applyWeights(weights);
            FftMultiplier.fftMixedRadix(bVec, roots2, twiddles);
            aVec.multiplyPointwise(bVec);
            FftMultiplier.ifftMixedRadix(aVec, roots2, twiddles);
            aVec.applyInverseWeights(weights);
            return FftMultiplier.fromFftVector(aVec, signum, bitsPerPoint);
        }
        ComplexVector[] roots = FftMultiplier.getRootsOfUnity2(logFFTLen);
        ComplexVector aVec = FftMultiplier.toFftVector(aMag, fftLen2, bitsPerPoint);
        aVec.applyWeights(roots[logFFTLen]);
        FftMultiplier.fft(aVec, roots);
        ComplexVector bVec = FftMultiplier.toFftVector(bMag, fftLen2, bitsPerPoint);
        bVec.applyWeights(roots[logFFTLen]);
        FftMultiplier.fft(bVec, roots);
        aVec.multiplyPointwise(bVec);
        FftMultiplier.ifft(aVec, roots);
        aVec.applyInverseWeights(roots[logFFTLen]);
        return FftMultiplier.fromFftVector(aVec, signum, bitsPerPoint);
    }

    static BigInteger square(BigInteger a) {
        if (a.signum() == 0) {
            return BigInteger.ZERO;
        }
        return a.bitLength() < 33220 ? a.multiply(a) : FftMultiplier.squareFft(a);
    }

    static BigInteger squareFft(BigInteger a) {
        int logFFTLen;
        int fftLen2;
        int fftLen3;
        int bitsPerPoint;
        byte[] mag = a.toByteArray();
        int bitLen = mag.length * 8;
        int fftLen = (bitLen + (bitsPerPoint = FftMultiplier.bitsPerFftPoint(bitLen)) - 1) / bitsPerPoint + 1;
        if (fftLen < (fftLen3 = (fftLen2 = 1 << (logFFTLen = 32 - Integer.numberOfLeadingZeros(fftLen - 1))) * 3 / 4)) {
            fftLen = fftLen3;
            ComplexVector vec = FftMultiplier.toFftVector(mag, fftLen, bitsPerPoint);
            ComplexVector[] roots2 = FftMultiplier.getRootsOfUnity2(logFFTLen - 2);
            ComplexVector weights = FftMultiplier.getRootsOfUnity3(logFFTLen - 2);
            ComplexVector twiddles = FftMultiplier.getRootsOfUnity3(logFFTLen - 4);
            vec.applyWeights(weights);
            FftMultiplier.fftMixedRadix(vec, roots2, twiddles);
            vec.squarePointwise();
            FftMultiplier.ifftMixedRadix(vec, roots2, twiddles);
            vec.applyInverseWeights(weights);
            return FftMultiplier.fromFftVector(vec, 1, bitsPerPoint);
        }
        fftLen = fftLen2;
        ComplexVector vec = FftMultiplier.toFftVector(mag, fftLen, bitsPerPoint);
        ComplexVector[] roots = FftMultiplier.getRootsOfUnity2(logFFTLen);
        vec.applyWeights(roots[logFFTLen]);
        FftMultiplier.fft(vec, roots);
        vec.squarePointwise();
        FftMultiplier.ifft(vec, roots);
        vec.applyInverseWeights(roots[logFFTLen]);
        return FftMultiplier.fromFftVector(vec, 1, bitsPerPoint);
    }

    static ComplexVector toFftVector(byte[] mag, int fftLen, int bitsPerFftPoint) {
        assert (bitsPerFftPoint <= 25) : bitsPerFftPoint + " does not fit into an int with slack";
        ComplexVector fftVec = new ComplexVector(fftLen);
        if (mag.length < 4) {
            byte[] paddedMag = new byte[4];
            System.arraycopy(mag, 0, paddedMag, 4 - mag.length, mag.length);
            mag = paddedMag;
        }
        int base = 1 << bitsPerFftPoint;
        int halfBase = base / 2;
        int bitMask = base - 1;
        int bitPadding = 32 - bitsPerFftPoint;
        int bitLength = mag.length * 8;
        int carry = 0;
        int fftIdx = 0;
        for (int bitIdx = bitLength - bitsPerFftPoint; bitIdx > -bitsPerFftPoint; bitIdx -= bitsPerFftPoint) {
            int idx = Math.min(Math.max(0, bitIdx >> 3), mag.length - 4);
            int shift = bitPadding - bitIdx + (idx << 3);
            int fftPoint = FastDoubleSwar.readIntBE(mag, idx) >>> shift & bitMask;
            carry = halfBase - (fftPoint += carry) >>> 31;
            fftVec.real(fftIdx, fftPoint -= base & -carry);
            ++fftIdx;
        }
        if (carry > 0) {
            fftVec.real(fftIdx, carry);
        }
        return fftVec;
    }

    static final class ComplexVector {
        private static final int COMPLEX_SIZE_SHIFT = 1;
        static final int IMAG = 1;
        static final int REAL = 0;
        private final double[] a;
        private final int length;
        private final int offset;

        ComplexVector(int length) {
            this.a = new double[length << 1];
            this.length = length;
            this.offset = 0;
        }

        ComplexVector(ComplexVector c, int from, int to) {
            this.length = to - from;
            this.a = c.a;
            this.offset = from << 1;
        }

        void add(int idxa, MutableComplex c) {
            int n = this.realIdx(idxa);
            this.a[n] = this.a[n] + c.real;
            int n2 = this.imagIdx(idxa);
            this.a[n2] = this.a[n2] + c.imag;
        }

        void addInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[this.realIdx(idxa)] + c.real(idxc);
            destination.imag = this.a[this.imagIdx(idxa)] + c.imag(idxc);
        }

        void addTimesIInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[this.realIdx(idxa)] - c.imag(idxc);
            destination.imag = this.a[this.imagIdx(idxa)] + c.real(idxc);
        }

        void applyInverseWeights(ComplexVector weights) {
            int offw = weights.offset;
            double[] w = weights.a;
            int end = this.offset + this.length << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                this.a[offa] = FastDoubleSwar.fma(real, w[offw + 0], imag * w[offw + 1]);
                this.a[offa + 1] = FastDoubleSwar.fma(-real, w[offw + 1], imag * w[offw + 0]);
                offw += 2;
            }
        }

        void applyWeights(ComplexVector weights) {
            int offw = weights.offset;
            double[] w = weights.a;
            int end = this.offset + this.length << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                this.a[offa + 0] = real * w[offw + 0];
                this.a[offa + 1] = real * w[offw + 1];
                offw += 2;
            }
        }

        void copyInto(int idxa, MutableComplex destination) {
            destination.real = this.a[this.realIdx(idxa)];
            destination.imag = this.a[this.imagIdx(idxa)];
        }

        double imag(int idxa) {
            return this.a[(idxa << 1) + this.offset + 1];
        }

        void imag(int idxa, double value) {
            this.a[(idxa << 1) + this.offset + 1] = value;
        }

        private int imagIdx(int idxa) {
            return (idxa << 1) + this.offset + 1;
        }

        void multiply(int idxa, MutableComplex c) {
            int ri = this.realIdx(idxa);
            int ii = this.imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(real, c.real, -imag * c.imag);
            this.a[ii] = FastDoubleSwar.fma(real, c.imag, imag * c.real);
        }

        void multiplyByIAnd(int idxa, MutableComplex c) {
            int ri = this.realIdx(idxa);
            int ii = this.imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(-real, c.imag, -imag * c.real);
            this.a[ii] = FastDoubleSwar.fma(real, c.real, -imag * c.imag);
        }

        void multiplyConjugate(int idxa, MutableComplex c) {
            int ri = this.realIdx(idxa);
            int ii = this.imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(real, c.real, imag * c.imag);
            this.a[ii] = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
        }

        void multiplyConjugateInto(int idxa, MutableComplex c, MutableComplex destination) {
            double real = this.a[this.realIdx(idxa)];
            double imag = this.a[this.imagIdx(idxa)];
            destination.real = FastDoubleSwar.fma(real, c.real, imag * c.imag);
            destination.imag = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
        }

        void multiplyConjugateTimesI(int idxa, MutableComplex c) {
            int ri = this.realIdx(idxa);
            int ii = this.imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleSwar.fma(-real, c.imag, imag * c.real);
            this.a[ii] = FastDoubleSwar.fma(-real, c.real, -imag * c.imag);
        }

        void multiplyInto(int idxa, MutableComplex c, MutableComplex destination) {
            double real = this.a[this.realIdx(idxa)];
            double imag = this.a[this.imagIdx(idxa)];
            destination.real = FastDoubleSwar.fma(real, c.real, -imag * c.imag);
            destination.imag = FastDoubleSwar.fma(real, c.imag, imag * c.real);
        }

        void multiplyPointwise(ComplexVector cvec) {
            int offc = cvec.offset;
            double[] c = cvec.a;
            int end = this.offset + this.length << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                double creal = c[offc + 0];
                double cimag = c[offc + 1];
                this.a[offa + 0] = FastDoubleSwar.fma(real, creal, -imag * cimag);
                this.a[offa + 1] = FastDoubleSwar.fma(real, cimag, imag * creal);
                offc += 2;
            }
        }

        double part(int idxa, int part) {
            return this.a[(idxa << 1) + part];
        }

        double real(int idxa) {
            return this.a[(idxa << 1) + this.offset];
        }

        void real(int idxa, double value) {
            this.a[(idxa << 1) + this.offset] = value;
        }

        private int realIdx(int idxa) {
            return (idxa << 1) + this.offset;
        }

        void set(int idxa, double real, double imag) {
            int idx = this.realIdx(idxa);
            this.a[idx] = real;
            this.a[idx + 1] = imag;
        }

        void squarePointwise() {
            int end = this.offset + this.length << 1;
            for (int offa = this.offset; offa < end; offa += 2) {
                double real = this.a[offa + 0];
                double imag = this.a[offa + 1];
                this.a[offa + 0] = FastDoubleSwar.fma(real, real, -imag * imag);
                this.a[offa + 1] = 2.0 * real * imag;
            }
        }

        void subtractInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[this.realIdx(idxa)] - c.real(idxc);
            destination.imag = this.a[this.imagIdx(idxa)] - c.imag(idxc);
        }

        void subtractTimesIInto(int idxa, ComplexVector c, int idxc, MutableComplex destination) {
            destination.real = this.a[this.realIdx(idxa)] + c.imag(idxc);
            destination.imag = this.a[this.imagIdx(idxa)] - c.real(idxc);
        }

        void timesTwoToThe(int idxa, int n) {
            int ri = this.realIdx(idxa);
            int ii = this.imagIdx(idxa);
            double real = this.a[ri];
            double imag = this.a[ii];
            this.a[ri] = FastDoubleMath.fastScalb(real, n);
            this.a[ii] = FastDoubleMath.fastScalb(imag, n);
        }
    }

    static final class MutableComplex {
        double real;
        double imag;

        MutableComplex() {
        }

        void add(MutableComplex c) {
            this.real += c.real;
            this.imag += c.imag;
        }

        void add(ComplexVector c, int idxc) {
            this.real += c.real(idxc);
            this.imag += c.imag(idxc);
        }

        void addInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real + c.real;
            destination.imag = this.imag + c.imag;
        }

        void addTimesI(MutableComplex c) {
            this.real -= c.imag;
            this.imag += c.real;
        }

        void addTimesI(ComplexVector c, int idxc) {
            this.real -= c.imag(idxc);
            this.imag += c.real(idxc);
        }

        void addTimesIInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real - c.imag;
            destination.imag = this.imag + c.real;
        }

        void copyInto(ComplexVector c, int idxc) {
            c.real(idxc, this.real);
            c.imag(idxc, this.imag);
        }

        void multiply(MutableComplex c) {
            double temp = this.real;
            this.real = FastDoubleSwar.fma(temp, c.real, -this.imag * c.imag);
            this.imag = FastDoubleSwar.fma(temp, c.imag, this.imag * c.real);
        }

        void multiplyConjugate(MutableComplex c) {
            double temp = this.real;
            this.real = FastDoubleSwar.fma(temp, c.real, this.imag * c.imag);
            this.imag = FastDoubleSwar.fma(-temp, c.imag, this.imag * c.real);
        }

        void set(ComplexVector c, int idxc) {
            this.real = c.real(idxc);
            this.imag = c.imag(idxc);
        }

        void squareInto(MutableComplex destination) {
            destination.real = FastDoubleSwar.fma(this.real, this.real, -this.imag * this.imag);
            destination.imag = 2.0 * this.real * this.imag;
        }

        void subtract(MutableComplex c) {
            this.real -= c.real;
            this.imag -= c.imag;
        }

        void subtract(ComplexVector c, int idxc) {
            this.real -= c.real(idxc);
            this.imag -= c.imag(idxc);
        }

        void subtractInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real - c.real;
            destination.imag = this.imag - c.imag;
        }

        void subtractInto(MutableComplex c, ComplexVector destination, int idxd) {
            destination.real(idxd, this.real - c.real);
            destination.imag(idxd, this.imag - c.imag);
        }

        void subtractTimesI(MutableComplex c) {
            this.real += c.imag;
            this.imag -= c.real;
        }

        void subtractTimesI(ComplexVector c, int idxc) {
            this.real += c.imag(idxc);
            this.imag -= c.real(idxc);
        }

        void subtractTimesIInto(MutableComplex c, MutableComplex destination) {
            destination.real = this.real + c.imag;
            destination.imag = this.imag - c.real;
        }
    }
}

