/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.math.ec.rfc7748.X25519;
import org.bouncycastle.math.ec.rfc7748.X25519Field;
import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.Scalar25519;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Interleave;
import org.bouncycastle.math.raw.Nat256;

public abstract class Ed25519 {
    private static final int COORD_INTS = 8;
    private static final int POINT_BYTES = 32;
    private static final int SCALAR_INTS = 8;
    private static final int SCALAR_BYTES = 32;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 32;
    public static final int SECRET_KEY_SIZE = 32;
    public static final int SIGNATURE_SIZE = 64;
    private static final byte[] DOM2_PREFIX = new byte[]{83, 105, 103, 69, 100, 50, 53, 53, 49, 57, 32, 110, 111, 32, 69, 100, 50, 53, 53, 49, 57, 32, 99, 111, 108, 108, 105, 115, 105, 111, 110, 115};
    private static final int[] P = new int[]{-19, -1, -1, -1, -1, -1, -1, Integer.MAX_VALUE};
    private static final int[] ORDER8_y1 = new int[]{1886001095, 1339575613, 1980447930, 258412557, -95215574, -959694548, 2013120334, 2047061138};
    private static final int[] ORDER8_y2 = new int[]{-1886001114, -1339575614, -1980447931, -258412558, 95215573, 959694547, -2013120335, 100422509};
    private static final int[] B_x = new int[]{52811034, 25909283, 8072341, 50637101, 13785486, 30858332, 20483199, 20966410, 43936626, 4379245};
    private static final int[] B_y = new int[]{40265304, 0x1999999, 0x666666, 0x3333333, 0xCCCCCC, 0x2666666, 0x1999999, 0x666666, 0x3333333, 0xCCCCCC};
    private static final int[] B128_x = new int[]{12052516, 1174424, 4087752, 38672185, 20040971, 21899680, 55468344, 20105554, 66708015, 9981791};
    private static final int[] B128_y = new int[]{66430571, 45040722, 4842939, 15895846, 18981244, 46308410, 4697481, 8903007, 53646190, 12474675};
    private static final int[] C_d = new int[]{56195235, 47411844, 25868126, 40503822, 57364, 58321048, 30416477, 31930572, 57760639, 10749657};
    private static final int[] C_d2 = new int[]{45281625, 27714825, 18181821, 0xD4141D, 114729, 49533232, 60832955, 30306712, 48412415, 4722099};
    private static final int[] C_d4 = new int[]{23454386, 55429651, 2809210, 27797563, 229458, 31957600, 54557047, 27058993, 29715967, 9444199};
    private static final int WNAF_WIDTH_128 = 4;
    private static final int WNAF_WIDTH_BASE = 6;
    private static final int PRECOMP_BLOCKS = 8;
    private static final int PRECOMP_TEETH = 4;
    private static final int PRECOMP_SPACING = 8;
    private static final int PRECOMP_RANGE = 256;
    private static final int PRECOMP_POINTS = 8;
    private static final int PRECOMP_MASK = 7;
    private static final Object PRECOMP_LOCK = new Object();
    private static PointPrecomp[] PRECOMP_BASE_WNAF = null;
    private static PointPrecomp[] PRECOMP_BASE128_WNAF = null;
    private static int[] PRECOMP_BASE_COMB = null;

    private static byte[] calculateS(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int[] nArray = new int[16];
        Scalar25519.decode(byArray, nArray);
        int[] nArray2 = new int[8];
        Scalar25519.decode(byArray2, nArray2);
        int[] nArray3 = new int[8];
        Scalar25519.decode(byArray3, nArray3);
        Nat256.mulAddTo(nArray2, nArray3, nArray);
        byte[] byArray4 = new byte[64];
        Codec.encode32(nArray, 0, nArray.length, byArray4, 0);
        return Scalar25519.reduce512(byArray4);
    }

    private static boolean checkContextVar(byte[] byArray, byte by) {
        return byArray == null && by == 0 || byArray != null && byArray.length < 256;
    }

    private static int checkPoint(PointAffine pointAffine) {
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        int[] nArray3 = F.create();
        F.sqr(pointAffine.x, nArray2);
        F.sqr(pointAffine.y, nArray3);
        F.mul(nArray2, nArray3, nArray);
        F.sub(nArray2, nArray3, nArray2);
        F.mul(nArray, C_d, nArray);
        F.addOne(nArray);
        F.add(nArray, nArray2, nArray);
        F.normalize(nArray);
        F.normalize(nArray3);
        return F.isZero(nArray) & ~F.isZero(nArray3);
    }

    private static int checkPoint(PointAccum pointAccum) {
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        int[] nArray3 = F.create();
        int[] nArray4 = F.create();
        F.sqr(pointAccum.x, nArray2);
        F.sqr(pointAccum.y, nArray3);
        F.sqr(pointAccum.z, nArray4);
        F.mul(nArray2, nArray3, nArray);
        F.sub(nArray2, nArray3, nArray2);
        F.mul(nArray2, nArray4, nArray2);
        F.sqr(nArray4, nArray4);
        F.mul(nArray, C_d, nArray);
        F.add(nArray, nArray4, nArray);
        F.add(nArray, nArray2, nArray);
        F.normalize(nArray);
        F.normalize(nArray3);
        F.normalize(nArray4);
        return F.isZero(nArray) & ~F.isZero(nArray3) & ~F.isZero(nArray4);
    }

    private static boolean checkPointFullVar(byte[] byArray) {
        int n;
        int n2;
        int n3 = n2 = Codec.decode32(byArray, 28) & Integer.MAX_VALUE;
        int n4 = n2 ^ P[7];
        int n5 = n2 ^ ORDER8_y1[7];
        int n6 = n2 ^ ORDER8_y2[7];
        for (n = 6; n > 0; --n) {
            int n7 = Codec.decode32(byArray, n * 4);
            n3 |= n7;
            n4 |= n7 ^ P[n];
            n5 |= n7 ^ ORDER8_y1[n];
            n6 |= n7 ^ ORDER8_y2[n];
        }
        n = Codec.decode32(byArray, 0);
        if (n3 == 0 && n + Integer.MIN_VALUE <= -2147483647) {
            return false;
        }
        if (n4 == 0 && n + Integer.MIN_VALUE >= P[0] - 1 + Integer.MIN_VALUE) {
            return false;
        }
        return (n5 |= n ^ ORDER8_y1[0]) != 0 & (n6 |= n ^ ORDER8_y2[0]) != 0;
    }

    private static boolean checkPointOrderVar(PointAffine pointAffine) {
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultOrderVar(pointAffine, pointAccum);
        return Ed25519.normalizeToNeutralElementVar(pointAccum);
    }

    private static boolean checkPointVar(byte[] byArray) {
        if ((Codec.decode32(byArray, 28) & Integer.MAX_VALUE) < P[7]) {
            return true;
        }
        int[] nArray = new int[8];
        Codec.decode32(byArray, 0, nArray, 0, 8);
        nArray[7] = nArray[7] & Integer.MAX_VALUE;
        return !Nat256.gte(nArray, P);
    }

    private static byte[] copy(byte[] byArray, int n, int n2) {
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, n, byArray2, 0, n2);
        return byArray2;
    }

    private static Digest createDigest() {
        SHA512Digest sHA512Digest = new SHA512Digest();
        if (sHA512Digest.getDigestSize() != 64) {
            throw new IllegalStateException();
        }
        return sHA512Digest;
    }

    public static Digest createPrehash() {
        return Ed25519.createDigest();
    }

    private static boolean decodePointVar(byte[] byArray, boolean bl, PointAffine pointAffine) {
        int n = (byArray[31] & 0x80) >>> 7;
        F.decode(byArray, pointAffine.y);
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        F.sqr(pointAffine.y, nArray);
        F.mul(C_d, nArray, nArray2);
        F.subOne(nArray);
        F.addOne(nArray2);
        if (!F.sqrtRatioVar(nArray, nArray2, pointAffine.x)) {
            return false;
        }
        F.normalize(pointAffine.x);
        if (n == 1 && F.isZeroVar(pointAffine.x)) {
            return false;
        }
        if (bl ^ n != (pointAffine.x[0] & 1)) {
            F.negate(pointAffine.x, pointAffine.x);
            F.normalize(pointAffine.x);
        }
        return true;
    }

    private static void dom2(Digest digest, byte by, byte[] byArray) {
        int n = DOM2_PREFIX.length;
        byte[] byArray2 = new byte[n + 2 + byArray.length];
        System.arraycopy(DOM2_PREFIX, 0, byArray2, 0, n);
        byArray2[n] = by;
        byArray2[n + 1] = (byte)byArray.length;
        System.arraycopy(byArray, 0, byArray2, n + 2, byArray.length);
        digest.update(byArray2, 0, byArray2.length);
    }

    private static void encodePoint(PointAffine pointAffine, byte[] byArray, int n) {
        F.encode(pointAffine.y, byArray, n);
        int n2 = n + 32 - 1;
        byArray[n2] = (byte)(byArray[n2] | (pointAffine.x[0] & 1) << 7);
    }

    public static void encodePublicPoint(PublicPoint publicPoint, byte[] byArray, int n) {
        F.encode(publicPoint.data, 10, byArray, n);
        int n2 = n + 32 - 1;
        byArray[n2] = (byte)(byArray[n2] | (publicPoint.data[0] & 1) << 7);
    }

    private static int encodeResult(PointAccum pointAccum, byte[] byArray, int n) {
        PointAffine pointAffine = new PointAffine();
        Ed25519.normalizeToAffine(pointAccum, pointAffine);
        int n2 = Ed25519.checkPoint(pointAffine);
        Ed25519.encodePoint(pointAffine, byArray, n);
        return n2;
    }

    private static PublicPoint exportPoint(PointAffine pointAffine) {
        int[] nArray = new int[20];
        F.copy(pointAffine.x, 0, nArray, 0);
        F.copy(pointAffine.y, 0, nArray, 10);
        return new PublicPoint(nArray);
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] byArray) {
        if (byArray.length != 32) {
            throw new IllegalArgumentException("k");
        }
        secureRandom.nextBytes(byArray);
    }

    public static void generatePublicKey(byte[] byArray, int n, byte[] byArray2, int n2) {
        Digest digest = Ed25519.createDigest();
        byte[] byArray3 = new byte[64];
        digest.update(byArray, n, 32);
        digest.doFinal(byArray3, 0);
        byte[] byArray4 = new byte[32];
        Ed25519.pruneScalar(byArray3, 0, byArray4);
        Ed25519.scalarMultBaseEncoded(byArray4, byArray2, n2);
    }

    public static PublicPoint generatePublicKey(byte[] byArray, int n) {
        Digest digest = Ed25519.createDigest();
        byte[] byArray2 = new byte[64];
        digest.update(byArray, n, 32);
        digest.doFinal(byArray2, 0);
        byte[] byArray3 = new byte[32];
        Ed25519.pruneScalar(byArray2, 0, byArray3);
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultBase(byArray3, pointAccum);
        PointAffine pointAffine = new PointAffine();
        Ed25519.normalizeToAffine(pointAccum, pointAffine);
        if (0 == Ed25519.checkPoint(pointAffine)) {
            throw new IllegalStateException();
        }
        return Ed25519.exportPoint(pointAffine);
    }

    private static int getWindow4(int[] nArray, int n) {
        int n2 = n >>> 3;
        int n3 = (n & 7) << 2;
        return nArray[n2] >>> n3 & 0xF;
    }

    private static void groupCombBits(int[] nArray) {
        for (int i = 0; i < nArray.length; ++i) {
            nArray[i] = Interleave.shuffle2(nArray[i]);
        }
    }

    private static void implSign(Digest digest, byte[] byArray, byte[] byArray2, byte[] byArray3, int n, byte[] byArray4, byte by, byte[] byArray5, int n2, int n3, byte[] byArray6, int n4) {
        if (byArray4 != null) {
            Ed25519.dom2(digest, by, byArray4);
        }
        digest.update(byArray, 32, 32);
        digest.update(byArray5, n2, n3);
        digest.doFinal(byArray, 0);
        byte[] byArray7 = Scalar25519.reduce512(byArray);
        byte[] byArray8 = new byte[32];
        Ed25519.scalarMultBaseEncoded(byArray7, byArray8, 0);
        if (byArray4 != null) {
            Ed25519.dom2(digest, by, byArray4);
        }
        digest.update(byArray8, 0, 32);
        digest.update(byArray3, n, 32);
        digest.update(byArray5, n2, n3);
        digest.doFinal(byArray, 0);
        byte[] byArray9 = Scalar25519.reduce512(byArray);
        byte[] byArray10 = Ed25519.calculateS(byArray7, byArray9, byArray2);
        System.arraycopy(byArray8, 0, byArray6, n4, 32);
        System.arraycopy(byArray10, 0, byArray6, n4 + 32, 32);
    }

    private static void implSign(byte[] byArray, int n, byte[] byArray2, byte by, byte[] byArray3, int n2, int n3, byte[] byArray4, int n4) {
        if (!Ed25519.checkContextVar(byArray2, by)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest digest = Ed25519.createDigest();
        byte[] byArray5 = new byte[64];
        digest.update(byArray, n, 32);
        digest.doFinal(byArray5, 0);
        byte[] byArray6 = new byte[32];
        Ed25519.pruneScalar(byArray5, 0, byArray6);
        byte[] byArray7 = new byte[32];
        Ed25519.scalarMultBaseEncoded(byArray6, byArray7, 0);
        Ed25519.implSign(digest, byArray5, byArray6, byArray7, 0, byArray2, by, byArray3, n2, n3, byArray4, n4);
    }

    private static void implSign(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte by, byte[] byArray4, int n3, int n4, byte[] byArray5, int n5) {
        if (!Ed25519.checkContextVar(byArray3, by)) {
            throw new IllegalArgumentException("ctx");
        }
        Digest digest = Ed25519.createDigest();
        byte[] byArray6 = new byte[64];
        digest.update(byArray, n, 32);
        digest.doFinal(byArray6, 0);
        byte[] byArray7 = new byte[32];
        Ed25519.pruneScalar(byArray6, 0, byArray7);
        Ed25519.implSign(digest, byArray6, byArray7, byArray2, n2, byArray3, by, byArray4, n3, n4, byArray5, n5);
    }

    private static boolean implVerify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte by, byte[] byArray4, int n3, int n4) {
        if (!Ed25519.checkContextVar(byArray3, by)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] byArray5 = Ed25519.copy(byArray, n, 32);
        byte[] byArray6 = Ed25519.copy(byArray, n + 32, 32);
        byte[] byArray7 = Ed25519.copy(byArray2, n2, 32);
        if (!Ed25519.checkPointVar(byArray5)) {
            return false;
        }
        int[] nArray = new int[8];
        if (!Scalar25519.checkVar(byArray6, nArray)) {
            return false;
        }
        if (!Ed25519.checkPointFullVar(byArray7)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed25519.decodePointVar(byArray5, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        if (!Ed25519.decodePointVar(byArray7, true, pointAffine2)) {
            return false;
        }
        Digest digest = Ed25519.createDigest();
        byte[] byArray8 = new byte[64];
        if (byArray3 != null) {
            Ed25519.dom2(digest, by, byArray3);
        }
        digest.update(byArray5, 0, 32);
        digest.update(byArray7, 0, 32);
        digest.update(byArray4, n3, n4);
        digest.doFinal(byArray8, 0);
        byte[] byArray9 = Scalar25519.reduce512(byArray8);
        int[] nArray2 = new int[8];
        Scalar25519.decode(byArray9, nArray2);
        int[] nArray3 = new int[4];
        int[] nArray4 = new int[4];
        if (!Scalar25519.reduceBasisVar(nArray2, nArray3, nArray4)) {
            throw new IllegalStateException();
        }
        Scalar25519.multiply128Var(nArray, nArray4, nArray);
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultStraus128Var(nArray, nArray3, pointAffine2, nArray4, pointAffine, pointAccum);
        return Ed25519.normalizeToNeutralElementVar(pointAccum);
    }

    private static boolean implVerify(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte by, byte[] byArray3, int n2, int n3) {
        if (!Ed25519.checkContextVar(byArray2, by)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] byArray4 = Ed25519.copy(byArray, n, 32);
        byte[] byArray5 = Ed25519.copy(byArray, n + 32, 32);
        if (!Ed25519.checkPointVar(byArray4)) {
            return false;
        }
        int[] nArray = new int[8];
        if (!Scalar25519.checkVar(byArray5, nArray)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed25519.decodePointVar(byArray4, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        F.negate(publicPoint.data, pointAffine2.x);
        F.copy(publicPoint.data, 10, pointAffine2.y, 0);
        byte[] byArray6 = new byte[32];
        Ed25519.encodePublicPoint(publicPoint, byArray6, 0);
        Digest digest = Ed25519.createDigest();
        byte[] byArray7 = new byte[64];
        if (byArray2 != null) {
            Ed25519.dom2(digest, by, byArray2);
        }
        digest.update(byArray4, 0, 32);
        digest.update(byArray6, 0, 32);
        digest.update(byArray3, n2, n3);
        digest.doFinal(byArray7, 0);
        byte[] byArray8 = Scalar25519.reduce512(byArray7);
        int[] nArray2 = new int[8];
        Scalar25519.decode(byArray8, nArray2);
        int[] nArray3 = new int[4];
        int[] nArray4 = new int[4];
        if (!Scalar25519.reduceBasisVar(nArray2, nArray3, nArray4)) {
            throw new IllegalStateException();
        }
        Scalar25519.multiply128Var(nArray, nArray4, nArray);
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultStraus128Var(nArray, nArray3, pointAffine2, nArray4, pointAffine, pointAccum);
        return Ed25519.normalizeToNeutralElementVar(pointAccum);
    }

    private static void invertDoubleZs(PointExtended[] pointExtendedArray) {
        int n = pointExtendedArray.length;
        int[] nArray = F.createTable(n);
        int[] nArray2 = F.create();
        F.copy(pointExtendedArray[0].z, 0, nArray2, 0);
        F.copy(nArray2, 0, nArray, 0);
        int n2 = 0;
        while (++n2 < n) {
            F.mul(nArray2, pointExtendedArray[n2].z, nArray2);
            F.copy(nArray2, 0, nArray, n2 * 10);
        }
        F.add(nArray2, nArray2, nArray2);
        F.invVar(nArray2, nArray2);
        --n2;
        int[] nArray3 = F.create();
        while (n2 > 0) {
            int n3 = n2--;
            F.copy(nArray, n2 * 10, nArray3, 0);
            F.mul(nArray3, nArray2, nArray3);
            F.mul(nArray2, pointExtendedArray[n3].z, nArray2);
            F.copy(nArray3, 0, pointExtendedArray[n3].z, 0);
        }
        F.copy(nArray2, 0, pointExtendedArray[0].z, 0);
    }

    private static void normalizeToAffine(PointAccum pointAccum, PointAffine pointAffine) {
        F.inv(pointAccum.z, pointAffine.y);
        F.mul(pointAffine.y, pointAccum.x, pointAffine.x);
        F.mul(pointAffine.y, pointAccum.y, pointAffine.y);
        F.normalize(pointAffine.x);
        F.normalize(pointAffine.y);
    }

    private static boolean normalizeToNeutralElementVar(PointAccum pointAccum) {
        F.normalize(pointAccum.x);
        F.normalize(pointAccum.y);
        F.normalize(pointAccum.z);
        return F.isZeroVar(pointAccum.x) && !F.isZeroVar(pointAccum.y) && F.areEqualVar(pointAccum.y, pointAccum.z);
    }

    private static void pointAdd(PointExtended pointExtended, PointExtended pointExtended2, PointExtended pointExtended3, PointTemp pointTemp) {
        int[] nArray = pointExtended3.x;
        int[] nArray2 = pointExtended3.y;
        int[] nArray3 = pointTemp.r0;
        int[] nArray4 = pointTemp.r1;
        int[] nArray5 = nArray;
        int[] nArray6 = nArray3;
        int[] nArray7 = nArray4;
        int[] nArray8 = nArray2;
        F.apm(pointExtended.y, pointExtended.x, nArray2, nArray);
        F.apm(pointExtended2.y, pointExtended2.x, nArray4, nArray3);
        F.mul(nArray, nArray3, nArray);
        F.mul(nArray2, nArray4, nArray2);
        F.mul(pointExtended.t, pointExtended2.t, nArray3);
        F.mul(nArray3, C_d2, nArray3);
        F.add(pointExtended.z, pointExtended.z, nArray4);
        F.mul(nArray4, pointExtended2.z, nArray4);
        F.apm(nArray2, nArray, nArray8, nArray5);
        F.apm(nArray4, nArray3, nArray7, nArray6);
        F.mul(nArray5, nArray8, pointExtended3.t);
        F.mul(nArray6, nArray7, pointExtended3.z);
        F.mul(nArray5, nArray6, pointExtended3.x);
        F.mul(nArray8, nArray7, pointExtended3.y);
    }

    private static void pointAdd(PointPrecomp pointPrecomp, PointAccum pointAccum, PointTemp pointTemp) {
        int[] nArray = pointAccum.x;
        int[] nArray2 = pointAccum.y;
        int[] nArray3 = pointTemp.r0;
        int[] nArray4 = pointAccum.u;
        int[] nArray5 = nArray;
        int[] nArray6 = nArray2;
        int[] nArray7 = pointAccum.v;
        F.apm(pointAccum.y, pointAccum.x, nArray2, nArray);
        F.mul(nArray, pointPrecomp.ymx_h, nArray);
        F.mul(nArray2, pointPrecomp.ypx_h, nArray2);
        F.mul(pointAccum.u, pointAccum.v, nArray3);
        F.mul(nArray3, pointPrecomp.xyd, nArray3);
        F.apm(nArray2, nArray, nArray7, nArray4);
        F.apm(pointAccum.z, nArray3, nArray6, nArray5);
        F.mul(nArray5, nArray6, pointAccum.z);
        F.mul(nArray5, nArray4, pointAccum.x);
        F.mul(nArray6, nArray7, pointAccum.y);
    }

    private static void pointAdd(PointPrecompZ pointPrecompZ, PointAccum pointAccum, PointTemp pointTemp) {
        int[] nArray = pointAccum.x;
        int[] nArray2 = pointAccum.y;
        int[] nArray3 = pointTemp.r0;
        int[] nArray4 = pointAccum.z;
        int[] nArray5 = pointAccum.u;
        int[] nArray6 = nArray;
        int[] nArray7 = nArray2;
        int[] nArray8 = pointAccum.v;
        F.apm(pointAccum.y, pointAccum.x, nArray2, nArray);
        F.mul(nArray, pointPrecompZ.ymx_h, nArray);
        F.mul(nArray2, pointPrecompZ.ypx_h, nArray2);
        F.mul(pointAccum.u, pointAccum.v, nArray3);
        F.mul(nArray3, pointPrecompZ.xyd, nArray3);
        F.mul(pointAccum.z, pointPrecompZ.z, nArray4);
        F.apm(nArray2, nArray, nArray8, nArray5);
        F.apm(nArray4, nArray3, nArray7, nArray6);
        F.mul(nArray6, nArray7, pointAccum.z);
        F.mul(nArray6, nArray5, pointAccum.x);
        F.mul(nArray7, nArray8, pointAccum.y);
    }

    private static void pointAddVar(boolean bl, PointPrecomp pointPrecomp, PointAccum pointAccum, PointTemp pointTemp) {
        int[] nArray;
        int[] nArray2;
        int[] nArray3 = pointAccum.x;
        int[] nArray4 = pointAccum.y;
        int[] nArray5 = pointTemp.r0;
        int[] nArray6 = pointAccum.u;
        int[] nArray7 = nArray3;
        int[] nArray8 = nArray4;
        int[] nArray9 = pointAccum.v;
        if (bl) {
            nArray2 = nArray4;
            nArray = nArray3;
        } else {
            nArray2 = nArray3;
            nArray = nArray4;
        }
        int[] nArray10 = nArray2;
        int[] nArray11 = nArray;
        F.apm(pointAccum.y, pointAccum.x, nArray4, nArray3);
        F.mul(nArray2, pointPrecomp.ymx_h, nArray2);
        F.mul(nArray, pointPrecomp.ypx_h, nArray);
        F.mul(pointAccum.u, pointAccum.v, nArray5);
        F.mul(nArray5, pointPrecomp.xyd, nArray5);
        F.apm(nArray4, nArray3, nArray9, nArray6);
        F.apm(pointAccum.z, nArray5, nArray11, nArray10);
        F.mul(nArray7, nArray8, pointAccum.z);
        F.mul(nArray7, nArray6, pointAccum.x);
        F.mul(nArray8, nArray9, pointAccum.y);
    }

    private static void pointAddVar(boolean bl, PointPrecompZ pointPrecompZ, PointAccum pointAccum, PointTemp pointTemp) {
        int[] nArray;
        int[] nArray2;
        int[] nArray3 = pointAccum.x;
        int[] nArray4 = pointAccum.y;
        int[] nArray5 = pointTemp.r0;
        int[] nArray6 = pointAccum.z;
        int[] nArray7 = pointAccum.u;
        int[] nArray8 = nArray3;
        int[] nArray9 = nArray4;
        int[] nArray10 = pointAccum.v;
        if (bl) {
            nArray2 = nArray4;
            nArray = nArray3;
        } else {
            nArray2 = nArray3;
            nArray = nArray4;
        }
        int[] nArray11 = nArray2;
        int[] nArray12 = nArray;
        F.apm(pointAccum.y, pointAccum.x, nArray4, nArray3);
        F.mul(nArray2, pointPrecompZ.ymx_h, nArray2);
        F.mul(nArray, pointPrecompZ.ypx_h, nArray);
        F.mul(pointAccum.u, pointAccum.v, nArray5);
        F.mul(nArray5, pointPrecompZ.xyd, nArray5);
        F.mul(pointAccum.z, pointPrecompZ.z, nArray6);
        F.apm(nArray4, nArray3, nArray10, nArray7);
        F.apm(nArray6, nArray5, nArray12, nArray11);
        F.mul(nArray8, nArray9, pointAccum.z);
        F.mul(nArray8, nArray7, pointAccum.x);
        F.mul(nArray9, nArray10, pointAccum.y);
    }

    private static void pointCopy(PointAccum pointAccum, PointExtended pointExtended) {
        F.copy(pointAccum.x, 0, pointExtended.x, 0);
        F.copy(pointAccum.y, 0, pointExtended.y, 0);
        F.copy(pointAccum.z, 0, pointExtended.z, 0);
        F.mul(pointAccum.u, pointAccum.v, pointExtended.t);
    }

    private static void pointCopy(PointAffine pointAffine, PointExtended pointExtended) {
        F.copy(pointAffine.x, 0, pointExtended.x, 0);
        F.copy(pointAffine.y, 0, pointExtended.y, 0);
        F.one(pointExtended.z);
        F.mul(pointAffine.x, pointAffine.y, pointExtended.t);
    }

    private static void pointCopy(PointExtended pointExtended, PointPrecompZ pointPrecompZ) {
        F.apm(pointExtended.y, pointExtended.x, pointPrecompZ.ypx_h, pointPrecompZ.ymx_h);
        F.mul(pointExtended.t, C_d2, pointPrecompZ.xyd);
        F.add(pointExtended.z, pointExtended.z, pointPrecompZ.z);
    }

    private static void pointDouble(PointAccum pointAccum) {
        int[] nArray = pointAccum.x;
        int[] nArray2 = pointAccum.y;
        int[] nArray3 = pointAccum.z;
        int[] nArray4 = pointAccum.u;
        int[] nArray5 = nArray;
        int[] nArray6 = nArray2;
        int[] nArray7 = pointAccum.v;
        F.add(pointAccum.x, pointAccum.y, nArray4);
        F.sqr(pointAccum.x, nArray);
        F.sqr(pointAccum.y, nArray2);
        F.sqr(pointAccum.z, nArray3);
        F.add(nArray3, nArray3, nArray3);
        F.apm(nArray, nArray2, nArray7, nArray6);
        F.sqr(nArray4, nArray4);
        F.sub(nArray7, nArray4, nArray4);
        F.add(nArray3, nArray6, nArray5);
        F.carry(nArray5);
        F.mul(nArray5, nArray6, pointAccum.z);
        F.mul(nArray5, nArray4, pointAccum.x);
        F.mul(nArray6, nArray7, pointAccum.y);
    }

    private static void pointLookup(int n, int n2, PointPrecomp pointPrecomp) {
        int n3 = n * 8 * 3 * 10;
        for (int i = 0; i < 8; ++i) {
            int n4 = (i ^ n2) - 1 >> 31;
            F.cmov(n4, PRECOMP_BASE_COMB, n3, pointPrecomp.ymx_h, 0);
            F.cmov(n4, PRECOMP_BASE_COMB, n3 += 10, pointPrecomp.ypx_h, 0);
            F.cmov(n4, PRECOMP_BASE_COMB, n3 += 10, pointPrecomp.xyd, 0);
            n3 += 10;
        }
    }

    private static void pointLookupZ(int[] nArray, int n, int[] nArray2, PointPrecompZ pointPrecompZ) {
        int n2 = Ed25519.getWindow4(nArray, n);
        int n3 = n2 >>> 3 ^ 1;
        int n4 = (n2 ^ -n3) & 7;
        int n5 = 0;
        for (int i = 0; i < 8; ++i) {
            int n6 = (i ^ n4) - 1 >> 31;
            F.cmov(n6, nArray2, n5, pointPrecompZ.ymx_h, 0);
            F.cmov(n6, nArray2, n5 += 10, pointPrecompZ.ypx_h, 0);
            F.cmov(n6, nArray2, n5 += 10, pointPrecompZ.xyd, 0);
            F.cmov(n6, nArray2, n5 += 10, pointPrecompZ.z, 0);
            n5 += 10;
        }
        F.cswap(n3, pointPrecompZ.ymx_h, pointPrecompZ.ypx_h);
        F.cnegate(n3, pointPrecompZ.xyd);
    }

    private static void pointPrecompute(PointAffine pointAffine, PointExtended[] pointExtendedArray, int n, int n2, PointTemp pointTemp) {
        pointExtendedArray[n] = new PointExtended();
        Ed25519.pointCopy(pointAffine, pointExtendedArray[n]);
        PointExtended pointExtended = new PointExtended();
        Ed25519.pointAdd(pointExtendedArray[n], pointExtendedArray[n], pointExtended, pointTemp);
        for (int i = 1; i < n2; ++i) {
            PointExtended pointExtended2 = new PointExtended();
            pointExtendedArray[n + i] = pointExtended2;
            Ed25519.pointAdd(pointExtendedArray[n + i - 1], pointExtended, pointExtended2, pointTemp);
        }
    }

    private static int[] pointPrecomputeZ(PointAffine pointAffine, int n, PointTemp pointTemp) {
        PointExtended pointExtended = new PointExtended();
        Ed25519.pointCopy(pointAffine, pointExtended);
        PointExtended pointExtended2 = new PointExtended();
        Ed25519.pointAdd(pointExtended, pointExtended, pointExtended2, pointTemp);
        PointPrecompZ pointPrecompZ = new PointPrecompZ();
        int[] nArray = F.createTable(n * 4);
        int n2 = 0;
        int n3 = 0;
        while (true) {
            Ed25519.pointCopy(pointExtended, pointPrecompZ);
            F.copy(pointPrecompZ.ymx_h, 0, nArray, n2);
            F.copy(pointPrecompZ.ypx_h, 0, nArray, n2 += 10);
            F.copy(pointPrecompZ.xyd, 0, nArray, n2 += 10);
            F.copy(pointPrecompZ.z, 0, nArray, n2 += 10);
            n2 += 10;
            if (++n3 == n) break;
            Ed25519.pointAdd(pointExtended, pointExtended2, pointExtended, pointTemp);
        }
        return nArray;
    }

    private static void pointPrecomputeZ(PointAffine pointAffine, PointPrecompZ[] pointPrecompZArray, int n, PointTemp pointTemp) {
        PointExtended pointExtended = new PointExtended();
        Ed25519.pointCopy(pointAffine, pointExtended);
        PointExtended pointExtended2 = new PointExtended();
        Ed25519.pointAdd(pointExtended, pointExtended, pointExtended2, pointTemp);
        int n2 = 0;
        while (true) {
            PointPrecompZ pointPrecompZ = pointPrecompZArray[n2] = new PointPrecompZ();
            Ed25519.pointCopy(pointExtended, pointPrecompZ);
            if (++n2 == n) break;
            Ed25519.pointAdd(pointExtended, pointExtended2, pointExtended, pointTemp);
        }
    }

    private static void pointSetNeutral(PointAccum pointAccum) {
        F.zero(pointAccum.x);
        F.one(pointAccum.y);
        F.one(pointAccum.z);
        F.zero(pointAccum.u);
        F.one(pointAccum.v);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void precompute() {
        Object object = PRECOMP_LOCK;
        synchronized (object) {
            PointExtended pointExtended;
            int n;
            if (PRECOMP_BASE_COMB != null) {
                return;
            }
            int n2 = 16;
            int n3 = 64;
            int n4 = n2 * 2 + n3;
            PointExtended[] pointExtendedArray = new PointExtended[n4];
            PointTemp pointTemp = new PointTemp();
            PointAffine pointAffine = new PointAffine();
            F.copy(B_x, 0, pointAffine.x, 0);
            F.copy(B_y, 0, pointAffine.y, 0);
            Ed25519.pointPrecompute(pointAffine, pointExtendedArray, 0, n2, pointTemp);
            PointAffine pointAffine2 = new PointAffine();
            F.copy(B128_x, 0, pointAffine2.x, 0);
            F.copy(B128_y, 0, pointAffine2.y, 0);
            Ed25519.pointPrecompute(pointAffine2, pointExtendedArray, n2, n2, pointTemp);
            PointAccum pointAccum = new PointAccum();
            F.copy(B_x, 0, pointAccum.x, 0);
            F.copy(B_y, 0, pointAccum.y, 0);
            F.one(pointAccum.z);
            F.copy(pointAccum.x, 0, pointAccum.u, 0);
            F.copy(pointAccum.y, 0, pointAccum.v, 0);
            int n5 = n2 * 2;
            PointExtended[] pointExtendedArray2 = new PointExtended[4];
            for (int i = 0; i < 4; ++i) {
                pointExtendedArray2[i] = new PointExtended();
            }
            PointExtended pointExtended2 = new PointExtended();
            for (n = 0; n < 8; ++n) {
                int n6;
                int n7;
                int n8 = n5++;
                PointExtended pointExtended3 = new PointExtended();
                pointExtendedArray[n8] = pointExtended3;
                pointExtended = pointExtended3;
                for (n7 = 0; n7 < 4; ++n7) {
                    if (n7 == 0) {
                        Ed25519.pointCopy(pointAccum, pointExtended);
                    } else {
                        Ed25519.pointCopy(pointAccum, pointExtended2);
                        Ed25519.pointAdd(pointExtended, pointExtended2, pointExtended, pointTemp);
                    }
                    Ed25519.pointDouble(pointAccum);
                    Ed25519.pointCopy(pointAccum, pointExtendedArray2[n7]);
                    if (n + n7 == 10) continue;
                    for (n6 = 1; n6 < 8; ++n6) {
                        Ed25519.pointDouble(pointAccum);
                    }
                }
                F.negate(pointExtended.x, pointExtended.x);
                F.negate(pointExtended.t, pointExtended.t);
                for (n7 = 0; n7 < 3; ++n7) {
                    n6 = 1 << n7;
                    int n9 = 0;
                    while (n9 < n6) {
                        pointExtendedArray[n5] = new PointExtended();
                        Ed25519.pointAdd(pointExtendedArray[n5 - n6], pointExtendedArray2[n7], pointExtendedArray[n5], pointTemp);
                        ++n9;
                        ++n5;
                    }
                }
            }
            Ed25519.invertDoubleZs(pointExtendedArray);
            PRECOMP_BASE_WNAF = new PointPrecomp[n2];
            for (n = 0; n < n2; ++n) {
                pointExtended = pointExtendedArray[n];
                PointPrecomp pointPrecomp = Ed25519.PRECOMP_BASE_WNAF[n] = new PointPrecomp();
                F.mul(pointExtended.x, pointExtended.z, pointExtended.x);
                F.mul(pointExtended.y, pointExtended.z, pointExtended.y);
                F.apm(pointExtended.y, pointExtended.x, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                F.mul(pointExtended.x, pointExtended.y, pointPrecomp.xyd);
                F.mul(pointPrecomp.xyd, C_d4, pointPrecomp.xyd);
                F.normalize(pointPrecomp.ymx_h);
                F.normalize(pointPrecomp.ypx_h);
                F.normalize(pointPrecomp.xyd);
            }
            PRECOMP_BASE128_WNAF = new PointPrecomp[n2];
            for (n = 0; n < n2; ++n) {
                pointExtended = pointExtendedArray[n2 + n];
                PointPrecomp pointPrecomp = Ed25519.PRECOMP_BASE128_WNAF[n] = new PointPrecomp();
                F.mul(pointExtended.x, pointExtended.z, pointExtended.x);
                F.mul(pointExtended.y, pointExtended.z, pointExtended.y);
                F.apm(pointExtended.y, pointExtended.x, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                F.mul(pointExtended.x, pointExtended.y, pointPrecomp.xyd);
                F.mul(pointPrecomp.xyd, C_d4, pointPrecomp.xyd);
                F.normalize(pointPrecomp.ymx_h);
                F.normalize(pointPrecomp.ypx_h);
                F.normalize(pointPrecomp.xyd);
            }
            PRECOMP_BASE_COMB = F.createTable(n3 * 3);
            PointPrecomp pointPrecomp = new PointPrecomp();
            int n10 = 0;
            for (int i = n2 * 2; i < n4; ++i) {
                PointExtended pointExtended4 = pointExtendedArray[i];
                F.mul(pointExtended4.x, pointExtended4.z, pointExtended4.x);
                F.mul(pointExtended4.y, pointExtended4.z, pointExtended4.y);
                F.apm(pointExtended4.y, pointExtended4.x, pointPrecomp.ypx_h, pointPrecomp.ymx_h);
                F.mul(pointExtended4.x, pointExtended4.y, pointPrecomp.xyd);
                F.mul(pointPrecomp.xyd, C_d4, pointPrecomp.xyd);
                F.normalize(pointPrecomp.ymx_h);
                F.normalize(pointPrecomp.ypx_h);
                F.normalize(pointPrecomp.xyd);
                F.copy(pointPrecomp.ymx_h, 0, PRECOMP_BASE_COMB, n10);
                F.copy(pointPrecomp.ypx_h, 0, PRECOMP_BASE_COMB, n10 += 10);
                F.copy(pointPrecomp.xyd, 0, PRECOMP_BASE_COMB, n10 += 10);
                n10 += 10;
            }
        }
    }

    private static void pruneScalar(byte[] byArray, int n, byte[] byArray2) {
        System.arraycopy(byArray, n, byArray2, 0, 32);
        byArray2[0] = (byte)(byArray2[0] & 0xF8);
        byArray2[31] = (byte)(byArray2[31] & 0x7F);
        byArray2[31] = (byte)(byArray2[31] | 0x40);
    }

    private static void scalarMult(byte[] byArray, PointAffine pointAffine, PointAccum pointAccum) {
        int[] nArray = new int[8];
        Scalar25519.decode(byArray, nArray);
        Scalar25519.toSignedDigits(256, nArray);
        PointPrecompZ pointPrecompZ = new PointPrecompZ();
        PointTemp pointTemp = new PointTemp();
        int[] nArray2 = Ed25519.pointPrecomputeZ(pointAffine, 8, pointTemp);
        Ed25519.pointSetNeutral(pointAccum);
        int n = 63;
        block0: while (true) {
            Ed25519.pointLookupZ(nArray, n, nArray2, pointPrecompZ);
            Ed25519.pointAdd(pointPrecompZ, pointAccum, pointTemp);
            if (--n < 0) break;
            int n2 = 0;
            while (true) {
                if (n2 >= 4) continue block0;
                Ed25519.pointDouble(pointAccum);
                ++n2;
            }
            break;
        }
    }

    private static void scalarMultBase(byte[] byArray, PointAccum pointAccum) {
        Ed25519.precompute();
        int[] nArray = new int[8];
        Scalar25519.decode(byArray, nArray);
        Scalar25519.toSignedDigits(256, nArray);
        Ed25519.groupCombBits(nArray);
        PointPrecomp pointPrecomp = new PointPrecomp();
        PointTemp pointTemp = new PointTemp();
        Ed25519.pointSetNeutral(pointAccum);
        int n = 0;
        int n2 = 28;
        while (true) {
            for (int i = 0; i < 8; ++i) {
                int n3 = nArray[i] >>> n2;
                int n4 = n3 >>> 3 & 1;
                int n5 = (n3 ^ -n4) & 7;
                Ed25519.pointLookup(i, n5, pointPrecomp);
                F.cnegate(n ^ n4, pointAccum.x);
                F.cnegate(n ^ n4, pointAccum.u);
                n = n4;
                Ed25519.pointAdd(pointPrecomp, pointAccum, pointTemp);
            }
            if ((n2 -= 4) < 0) break;
            Ed25519.pointDouble(pointAccum);
        }
        F.cnegate(n, pointAccum.x);
        F.cnegate(n, pointAccum.u);
    }

    private static void scalarMultBaseEncoded(byte[] byArray, byte[] byArray2, int n) {
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultBase(byArray, pointAccum);
        if (0 == Ed25519.encodeResult(pointAccum, byArray2, n)) {
            throw new IllegalStateException();
        }
    }

    public static void scalarMultBaseYZ(X25519.Friend friend, byte[] byArray, int n, int[] nArray, int[] nArray2) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by X25519");
        }
        byte[] byArray2 = new byte[32];
        Ed25519.pruneScalar(byArray, n, byArray2);
        PointAccum pointAccum = new PointAccum();
        Ed25519.scalarMultBase(byArray2, pointAccum);
        if (0 == Ed25519.checkPoint(pointAccum)) {
            throw new IllegalStateException();
        }
        F.copy(pointAccum.y, 0, nArray, 0);
        F.copy(pointAccum.z, 0, nArray2, 0);
    }

    private static void scalarMultOrderVar(PointAffine pointAffine, PointAccum pointAccum) {
        byte[] byArray = new byte[253];
        Scalar25519.getOrderWnafVar(4, byArray);
        int n = 4;
        PointPrecompZ[] pointPrecompZArray = new PointPrecompZ[n];
        PointTemp pointTemp = new PointTemp();
        Ed25519.pointPrecomputeZ(pointAffine, pointPrecompZArray, n, pointTemp);
        Ed25519.pointSetNeutral(pointAccum);
        int n2 = 252;
        while (true) {
            byte by;
            if ((by = byArray[n2]) != 0) {
                int n3 = by >> 1 ^ by >> 31;
                Ed25519.pointAddVar(by < 0, pointPrecompZArray[n3], pointAccum, pointTemp);
            }
            if (--n2 < 0) break;
            Ed25519.pointDouble(pointAccum);
        }
    }

    private static void scalarMultStraus128Var(int[] nArray, int[] nArray2, PointAffine pointAffine, int[] nArray3, PointAffine pointAffine2, PointAccum pointAccum) {
        Ed25519.precompute();
        byte[] byArray = new byte[256];
        byte[] byArray2 = new byte[128];
        byte[] byArray3 = new byte[128];
        Wnaf.getSignedVar(nArray, 6, byArray);
        Wnaf.getSignedVar(nArray2, 4, byArray2);
        Wnaf.getSignedVar(nArray3, 4, byArray3);
        int n = 4;
        PointPrecompZ[] pointPrecompZArray = new PointPrecompZ[n];
        PointPrecompZ[] pointPrecompZArray2 = new PointPrecompZ[n];
        PointTemp pointTemp = new PointTemp();
        Ed25519.pointPrecomputeZ(pointAffine, pointPrecompZArray, n, pointTemp);
        Ed25519.pointPrecomputeZ(pointAffine2, pointPrecompZArray2, n, pointTemp);
        Ed25519.pointSetNeutral(pointAccum);
        int n2 = 128;
        while (--n2 >= 0 && (byArray[n2] | byArray[128 + n2] | byArray2[n2] | byArray3[n2]) == 0) {
        }
        while (n2 >= 0) {
            int n3;
            int n4;
            int n5;
            byte by = byArray[n2];
            if (by != 0) {
                n5 = by >> 1 ^ by >> 31;
                Ed25519.pointAddVar(by < 0, PRECOMP_BASE_WNAF[n5], pointAccum, pointTemp);
            }
            if ((n5 = byArray[128 + n2]) != 0) {
                n4 = n5 >> 1 ^ n5 >> 31;
                Ed25519.pointAddVar(n5 < 0, PRECOMP_BASE128_WNAF[n4], pointAccum, pointTemp);
            }
            if ((n4 = byArray2[n2]) != 0) {
                n3 = n4 >> 1 ^ n4 >> 31;
                Ed25519.pointAddVar(n4 < 0, pointPrecompZArray[n3], pointAccum, pointTemp);
            }
            if ((n3 = byArray3[n2]) != 0) {
                int n6 = n3 >> 1 ^ n3 >> 31;
                Ed25519.pointAddVar(n3 < 0, pointPrecompZArray2[n6], pointAccum, pointTemp);
            }
            Ed25519.pointDouble(pointAccum);
            --n2;
        }
        Ed25519.pointDouble(pointAccum);
        Ed25519.pointDouble(pointAccum);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, int n2, int n3, byte[] byArray3, int n4) {
        byte[] byArray4 = null;
        byte by = 0;
        Ed25519.implSign(byArray, n, byArray4, by, byArray2, n2, n3, byArray3, n4);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3, int n4, byte[] byArray4, int n5) {
        byte[] byArray5 = null;
        byte by = 0;
        Ed25519.implSign(byArray, n, byArray2, n2, byArray5, by, byArray3, n3, n4, byArray4, n5);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, int n3, byte[] byArray4, int n4) {
        byte by = 0;
        Ed25519.implSign(byArray, n, byArray2, by, byArray3, n2, n3, byArray4, n4);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, int n4, byte[] byArray5, int n5) {
        byte by = 0;
        Ed25519.implSign(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, n4, byArray5, n5);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, byte[] byArray4, int n3) {
        byte by = 1;
        Ed25519.implSign(byArray, n, byArray2, by, byArray3, n2, 64, byArray4, n3);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, byte[] byArray5, int n4) {
        byte by = 1;
        Ed25519.implSign(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, 64, byArray5, n4);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, Digest digest, byte[] byArray3, int n2) {
        byte[] byArray4 = new byte[64];
        if (64 != digest.doFinal(byArray4, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        Ed25519.implSign(byArray, n, byArray2, by, byArray4, 0, byArray4.length, byArray3, n2);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, Digest digest, byte[] byArray4, int n3) {
        byte[] byArray5 = new byte[64];
        if (64 != digest.doFinal(byArray5, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        Ed25519.implSign(byArray, n, byArray2, n2, byArray3, by, byArray5, 0, byArray5.length, byArray4, n3);
    }

    public static boolean validatePublicKeyFull(byte[] byArray, int n) {
        byte[] byArray2 = Ed25519.copy(byArray, n, 32);
        if (!Ed25519.checkPointFullVar(byArray2)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed25519.decodePointVar(byArray2, false, pointAffine)) {
            return false;
        }
        return Ed25519.checkPointOrderVar(pointAffine);
    }

    public static PublicPoint validatePublicKeyFullExport(byte[] byArray, int n) {
        byte[] byArray2 = Ed25519.copy(byArray, n, 32);
        if (!Ed25519.checkPointFullVar(byArray2)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed25519.decodePointVar(byArray2, false, pointAffine)) {
            return null;
        }
        if (!Ed25519.checkPointOrderVar(pointAffine)) {
            return null;
        }
        return Ed25519.exportPoint(pointAffine);
    }

    public static boolean validatePublicKeyPartial(byte[] byArray, int n) {
        byte[] byArray2 = Ed25519.copy(byArray, n, 32);
        if (!Ed25519.checkPointFullVar(byArray2)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        return Ed25519.decodePointVar(byArray2, false, pointAffine);
    }

    public static PublicPoint validatePublicKeyPartialExport(byte[] byArray, int n) {
        byte[] byArray2 = Ed25519.copy(byArray, n, 32);
        if (!Ed25519.checkPointFullVar(byArray2)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed25519.decodePointVar(byArray2, false, pointAffine)) {
            return null;
        }
        return Ed25519.exportPoint(pointAffine);
    }

    public static boolean verify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, int n3, int n4) {
        byte[] byArray4 = null;
        byte by = 0;
        return Ed25519.implVerify(byArray, n, byArray2, n2, byArray4, by, byArray3, n3, n4);
    }

    public static boolean verify(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, int n2, int n3) {
        byte[] byArray3 = null;
        byte by = 0;
        return Ed25519.implVerify(byArray, n, publicPoint, byArray3, by, byArray2, n2, n3);
    }

    public static boolean verify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, int n4) {
        byte by = 0;
        return Ed25519.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, n4);
    }

    public static boolean verify(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte[] byArray3, int n2, int n3) {
        byte by = 0;
        return Ed25519.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, n2, n3);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3) {
        byte by = 1;
        return Ed25519.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, 64);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte[] byArray3, int n2) {
        byte by = 1;
        return Ed25519.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, n2, 64);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, Digest digest) {
        byte[] byArray4 = new byte[64];
        if (64 != digest.doFinal(byArray4, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        return Ed25519.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, 0, byArray4.length);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, Digest digest) {
        byte[] byArray3 = new byte[64];
        if (64 != digest.doFinal(byArray3, 0)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        return Ed25519.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, 0, byArray3.length);
    }

    public static final class Algorithm {
        public static final int Ed25519 = 0;
        public static final int Ed25519ctx = 1;
        public static final int Ed25519ph = 2;
    }

    private static class F
    extends X25519Field {
        private F() {
        }
    }

    private static class PointAccum {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();
        int[] u = F.create();
        int[] v = F.create();

        private PointAccum() {
        }
    }

    private static class PointAffine {
        int[] x = F.create();
        int[] y = F.create();

        private PointAffine() {
        }
    }

    private static class PointExtended {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();
        int[] t = F.create();

        private PointExtended() {
        }
    }

    private static class PointPrecomp {
        int[] ymx_h = F.create();
        int[] ypx_h = F.create();
        int[] xyd = F.create();

        private PointPrecomp() {
        }
    }

    private static class PointPrecompZ {
        int[] ymx_h = F.create();
        int[] ypx_h = F.create();
        int[] xyd = F.create();
        int[] z = F.create();

        private PointPrecompZ() {
        }
    }

    private static class PointTemp {
        int[] r0 = F.create();
        int[] r1 = F.create();

        private PointTemp() {
        }
    }

    public static final class PublicPoint {
        final int[] data;

        PublicPoint(int[] nArray) {
            this.data = nArray;
        }
    }
}

