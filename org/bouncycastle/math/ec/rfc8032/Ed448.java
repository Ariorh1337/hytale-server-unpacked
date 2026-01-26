/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.ec.rfc8032;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.math.ec.rfc7748.X448;
import org.bouncycastle.math.ec.rfc7748.X448Field;
import org.bouncycastle.math.ec.rfc8032.Codec;
import org.bouncycastle.math.ec.rfc8032.Scalar448;
import org.bouncycastle.math.ec.rfc8032.Wnaf;
import org.bouncycastle.math.raw.Nat;

public abstract class Ed448 {
    private static final int COORD_INTS = 14;
    private static final int POINT_BYTES = 57;
    private static final int SCALAR_INTS = 14;
    private static final int SCALAR_BYTES = 57;
    public static final int PREHASH_SIZE = 64;
    public static final int PUBLIC_KEY_SIZE = 57;
    public static final int SECRET_KEY_SIZE = 57;
    public static final int SIGNATURE_SIZE = 114;
    private static final byte[] DOM4_PREFIX = new byte[]{83, 105, 103, 69, 100, 52, 52, 56};
    private static final int[] P = new int[]{-1, -1, -1, -1, -1, -1, -1, -2, -1, -1, -1, -1, -1, -1};
    private static final int[] B_x = new int[]{118276190, 40534716, 9670182, 135141552, 85017403, 259173222, 68333082, 171784774, 174973732, 15824510, 73756743, 57518561, 94773951, 248652241, 107736333, 82941708};
    private static final int[] B_y = new int[]{36764180, 8885695, 130592152, 20104429, 163904957, 30304195, 121295871, 5901357, 125344798, 171541512, 175338348, 209069246, 3626697, 38307682, 24032956, 110359655};
    private static final int[] B225_x = new int[]{110141154, 30892124, 160820362, 264558960, 217232225, 47722141, 19029845, 8326902, 183409749, 170134547, 90340180, 222600478, 61097333, 7431335, 198491505, 102372861};
    private static final int[] B225_y = new int[]{221945828, 50763449, 132637478, 109250759, 216053960, 61612587, 50649998, 138339097, 98949899, 248139835, 186410297, 126520782, 47339196, 78164062, 198835543, 169622712};
    private static final int C_d = 39081;
    private static final int WNAF_WIDTH_225 = 5;
    private static final int WNAF_WIDTH_BASE = 7;
    private static final int PRECOMP_BLOCKS = 5;
    private static final int PRECOMP_TEETH = 5;
    private static final int PRECOMP_SPACING = 18;
    private static final int PRECOMP_RANGE = 450;
    private static final int PRECOMP_POINTS = 16;
    private static final int PRECOMP_MASK = 15;
    private static final Object PRECOMP_LOCK = new Object();
    private static PointAffine[] PRECOMP_BASE_WNAF = null;
    private static PointAffine[] PRECOMP_BASE225_WNAF = null;
    private static int[] PRECOMP_BASE_COMB = null;

    private static byte[] calculateS(byte[] byArray, byte[] byArray2, byte[] byArray3) {
        int[] nArray = new int[28];
        Scalar448.decode(byArray, nArray);
        int[] nArray2 = new int[14];
        Scalar448.decode(byArray2, nArray2);
        int[] nArray3 = new int[14];
        Scalar448.decode(byArray3, nArray3);
        Nat.mulAddTo(14, nArray2, nArray3, nArray);
        byte[] byArray4 = new byte[114];
        Codec.encode32(nArray, 0, nArray.length, byArray4, 0);
        return Scalar448.reduce912(byArray4);
    }

    private static boolean checkContextVar(byte[] byArray) {
        return byArray != null && byArray.length < 256;
    }

    private static int checkPoint(PointAffine pointAffine) {
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        int[] nArray3 = F.create();
        F.sqr(pointAffine.x, nArray2);
        F.sqr(pointAffine.y, nArray3);
        F.mul(nArray2, nArray3, nArray);
        F.add(nArray2, nArray3, nArray2);
        F.mul(nArray, 39081, nArray);
        F.subOne(nArray);
        F.add(nArray, nArray2, nArray);
        F.normalize(nArray);
        F.normalize(nArray3);
        return F.isZero(nArray) & ~F.isZero(nArray3);
    }

    private static int checkPoint(PointProjective pointProjective) {
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        int[] nArray3 = F.create();
        int[] nArray4 = F.create();
        F.sqr(pointProjective.x, nArray2);
        F.sqr(pointProjective.y, nArray3);
        F.sqr(pointProjective.z, nArray4);
        F.mul(nArray2, nArray3, nArray);
        F.add(nArray2, nArray3, nArray2);
        F.mul(nArray2, nArray4, nArray2);
        F.sqr(nArray4, nArray4);
        F.mul(nArray, 39081, nArray);
        F.sub(nArray, nArray4, nArray);
        F.add(nArray, nArray2, nArray);
        F.normalize(nArray);
        F.normalize(nArray3);
        F.normalize(nArray4);
        return F.isZero(nArray) & ~F.isZero(nArray3) & ~F.isZero(nArray4);
    }

    private static boolean checkPointFullVar(byte[] byArray) {
        int n;
        int n2;
        if ((byArray[56] & 0x7F) != 0) {
            return false;
        }
        int n3 = n2 = Codec.decode32(byArray, 52);
        int n4 = n2 ^ P[13];
        for (n = 12; n > 0; --n) {
            int n5 = Codec.decode32(byArray, n * 4);
            if (n4 == 0 && n5 + Integer.MIN_VALUE > P[n] + Integer.MIN_VALUE) {
                return false;
            }
            n3 |= n5;
            n4 |= n5 ^ P[n];
        }
        n = Codec.decode32(byArray, 0);
        if (n3 == 0 && n + Integer.MIN_VALUE <= -2147483647) {
            return false;
        }
        return n4 != 0 || n + Integer.MIN_VALUE < P[0] - 1 + Integer.MIN_VALUE;
    }

    private static boolean checkPointOrderVar(PointAffine pointAffine) {
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultOrderVar(pointAffine, pointProjective);
        return Ed448.normalizeToNeutralElementVar(pointProjective);
    }

    private static boolean checkPointVar(byte[] byArray) {
        if ((byArray[56] & 0x7F) != 0) {
            return false;
        }
        if (Codec.decode32(byArray, 52) != P[13]) {
            return true;
        }
        int[] nArray = new int[14];
        Codec.decode32(byArray, 0, nArray, 0, 14);
        return !Nat.gte(14, nArray, P);
    }

    private static byte[] copy(byte[] byArray, int n, int n2) {
        byte[] byArray2 = new byte[n2];
        System.arraycopy(byArray, n, byArray2, 0, n2);
        return byArray2;
    }

    public static Xof createPrehash() {
        return Ed448.createXof();
    }

    private static Xof createXof() {
        return new SHAKEDigest(256);
    }

    private static boolean decodePointVar(byte[] byArray, boolean bl, PointAffine pointAffine) {
        int n = (byArray[56] & 0x80) >>> 7;
        F.decode(byArray, pointAffine.y);
        int[] nArray = F.create();
        int[] nArray2 = F.create();
        F.sqr(pointAffine.y, nArray);
        F.mul(nArray, 39081, nArray2);
        F.negate(nArray, nArray);
        F.addOne(nArray);
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

    private static void dom4(Xof xof, byte by, byte[] byArray) {
        int n = DOM4_PREFIX.length;
        byte[] byArray2 = new byte[n + 2 + byArray.length];
        System.arraycopy(DOM4_PREFIX, 0, byArray2, 0, n);
        byArray2[n] = by;
        byArray2[n + 1] = (byte)byArray.length;
        System.arraycopy(byArray, 0, byArray2, n + 2, byArray.length);
        xof.update(byArray2, 0, byArray2.length);
    }

    private static void encodePoint(PointAffine pointAffine, byte[] byArray, int n) {
        F.encode(pointAffine.y, byArray, n);
        byArray[n + 57 - 1] = (byte)((pointAffine.x[0] & 1) << 7);
    }

    public static void encodePublicPoint(PublicPoint publicPoint, byte[] byArray, int n) {
        F.encode(publicPoint.data, 16, byArray, n);
        byArray[n + 57 - 1] = (byte)((publicPoint.data[0] & 1) << 7);
    }

    private static int encodeResult(PointProjective pointProjective, byte[] byArray, int n) {
        PointAffine pointAffine = new PointAffine();
        Ed448.normalizeToAffine(pointProjective, pointAffine);
        int n2 = Ed448.checkPoint(pointAffine);
        Ed448.encodePoint(pointAffine, byArray, n);
        return n2;
    }

    private static PublicPoint exportPoint(PointAffine pointAffine) {
        int[] nArray = new int[32];
        F.copy(pointAffine.x, 0, nArray, 0);
        F.copy(pointAffine.y, 0, nArray, 16);
        return new PublicPoint(nArray);
    }

    public static void generatePrivateKey(SecureRandom secureRandom, byte[] byArray) {
        if (byArray.length != 57) {
            throw new IllegalArgumentException("k");
        }
        secureRandom.nextBytes(byArray);
    }

    public static void generatePublicKey(byte[] byArray, int n, byte[] byArray2, int n2) {
        Xof xof = Ed448.createXof();
        byte[] byArray3 = new byte[114];
        xof.update(byArray, n, 57);
        xof.doFinal(byArray3, 0, byArray3.length);
        byte[] byArray4 = new byte[57];
        Ed448.pruneScalar(byArray3, 0, byArray4);
        Ed448.scalarMultBaseEncoded(byArray4, byArray2, n2);
    }

    public static PublicPoint generatePublicKey(byte[] byArray, int n) {
        Xof xof = Ed448.createXof();
        byte[] byArray2 = new byte[114];
        xof.update(byArray, n, 57);
        xof.doFinal(byArray2, 0, byArray2.length);
        byte[] byArray3 = new byte[57];
        Ed448.pruneScalar(byArray2, 0, byArray3);
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultBase(byArray3, pointProjective);
        PointAffine pointAffine = new PointAffine();
        Ed448.normalizeToAffine(pointProjective, pointAffine);
        if (0 == Ed448.checkPoint(pointAffine)) {
            throw new IllegalStateException();
        }
        return Ed448.exportPoint(pointAffine);
    }

    private static int getWindow4(int[] nArray, int n) {
        int n2 = n >>> 3;
        int n3 = (n & 7) << 2;
        return nArray[n2] >>> n3 & 0xF;
    }

    private static void implSign(Xof xof, byte[] byArray, byte[] byArray2, byte[] byArray3, int n, byte[] byArray4, byte by, byte[] byArray5, int n2, int n3, byte[] byArray6, int n4) {
        Ed448.dom4(xof, by, byArray4);
        xof.update(byArray, 57, 57);
        xof.update(byArray5, n2, n3);
        xof.doFinal(byArray, 0, byArray.length);
        byte[] byArray7 = Scalar448.reduce912(byArray);
        byte[] byArray8 = new byte[57];
        Ed448.scalarMultBaseEncoded(byArray7, byArray8, 0);
        Ed448.dom4(xof, by, byArray4);
        xof.update(byArray8, 0, 57);
        xof.update(byArray3, n, 57);
        xof.update(byArray5, n2, n3);
        xof.doFinal(byArray, 0, byArray.length);
        byte[] byArray9 = Scalar448.reduce912(byArray);
        byte[] byArray10 = Ed448.calculateS(byArray7, byArray9, byArray2);
        System.arraycopy(byArray8, 0, byArray6, n4, 57);
        System.arraycopy(byArray10, 0, byArray6, n4 + 57, 57);
    }

    private static void implSign(byte[] byArray, int n, byte[] byArray2, byte by, byte[] byArray3, int n2, int n3, byte[] byArray4, int n4) {
        if (!Ed448.checkContextVar(byArray2)) {
            throw new IllegalArgumentException("ctx");
        }
        Xof xof = Ed448.createXof();
        byte[] byArray5 = new byte[114];
        xof.update(byArray, n, 57);
        xof.doFinal(byArray5, 0, byArray5.length);
        byte[] byArray6 = new byte[57];
        Ed448.pruneScalar(byArray5, 0, byArray6);
        byte[] byArray7 = new byte[57];
        Ed448.scalarMultBaseEncoded(byArray6, byArray7, 0);
        Ed448.implSign(xof, byArray5, byArray6, byArray7, 0, byArray2, by, byArray3, n2, n3, byArray4, n4);
    }

    private static void implSign(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte by, byte[] byArray4, int n3, int n4, byte[] byArray5, int n5) {
        if (!Ed448.checkContextVar(byArray3)) {
            throw new IllegalArgumentException("ctx");
        }
        Xof xof = Ed448.createXof();
        byte[] byArray6 = new byte[114];
        xof.update(byArray, n, 57);
        xof.doFinal(byArray6, 0, byArray6.length);
        byte[] byArray7 = new byte[57];
        Ed448.pruneScalar(byArray6, 0, byArray7);
        Ed448.implSign(xof, byArray6, byArray7, byArray2, n2, byArray3, by, byArray4, n3, n4, byArray5, n5);
    }

    private static boolean implVerify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte by, byte[] byArray4, int n3, int n4) {
        if (!Ed448.checkContextVar(byArray3)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] byArray5 = Ed448.copy(byArray, n, 57);
        byte[] byArray6 = Ed448.copy(byArray, n + 57, 57);
        byte[] byArray7 = Ed448.copy(byArray2, n2, 57);
        if (!Ed448.checkPointVar(byArray5)) {
            return false;
        }
        int[] nArray = new int[14];
        if (!Scalar448.checkVar(byArray6, nArray)) {
            return false;
        }
        if (!Ed448.checkPointFullVar(byArray7)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed448.decodePointVar(byArray5, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        if (!Ed448.decodePointVar(byArray7, true, pointAffine2)) {
            return false;
        }
        Xof xof = Ed448.createXof();
        byte[] byArray8 = new byte[114];
        Ed448.dom4(xof, by, byArray3);
        xof.update(byArray5, 0, 57);
        xof.update(byArray7, 0, 57);
        xof.update(byArray4, n3, n4);
        xof.doFinal(byArray8, 0, byArray8.length);
        byte[] byArray9 = Scalar448.reduce912(byArray8);
        int[] nArray2 = new int[14];
        Scalar448.decode(byArray9, nArray2);
        int[] nArray3 = new int[8];
        int[] nArray4 = new int[8];
        if (!Scalar448.reduceBasisVar(nArray2, nArray3, nArray4)) {
            throw new IllegalStateException();
        }
        Scalar448.multiply225Var(nArray, nArray4, nArray);
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultStraus225Var(nArray, nArray3, pointAffine2, nArray4, pointAffine, pointProjective);
        return Ed448.normalizeToNeutralElementVar(pointProjective);
    }

    private static boolean implVerify(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte by, byte[] byArray3, int n2, int n3) {
        if (!Ed448.checkContextVar(byArray2)) {
            throw new IllegalArgumentException("ctx");
        }
        byte[] byArray4 = Ed448.copy(byArray, n, 57);
        byte[] byArray5 = Ed448.copy(byArray, n + 57, 57);
        if (!Ed448.checkPointVar(byArray4)) {
            return false;
        }
        int[] nArray = new int[14];
        if (!Scalar448.checkVar(byArray5, nArray)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed448.decodePointVar(byArray4, true, pointAffine)) {
            return false;
        }
        PointAffine pointAffine2 = new PointAffine();
        F.negate(publicPoint.data, pointAffine2.x);
        F.copy(publicPoint.data, 16, pointAffine2.y, 0);
        byte[] byArray6 = new byte[57];
        Ed448.encodePublicPoint(publicPoint, byArray6, 0);
        Xof xof = Ed448.createXof();
        byte[] byArray7 = new byte[114];
        Ed448.dom4(xof, by, byArray2);
        xof.update(byArray4, 0, 57);
        xof.update(byArray6, 0, 57);
        xof.update(byArray3, n2, n3);
        xof.doFinal(byArray7, 0, byArray7.length);
        byte[] byArray8 = Scalar448.reduce912(byArray7);
        int[] nArray2 = new int[14];
        Scalar448.decode(byArray8, nArray2);
        int[] nArray3 = new int[8];
        int[] nArray4 = new int[8];
        if (!Scalar448.reduceBasisVar(nArray2, nArray3, nArray4)) {
            throw new IllegalStateException();
        }
        Scalar448.multiply225Var(nArray, nArray4, nArray);
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultStraus225Var(nArray, nArray3, pointAffine2, nArray4, pointAffine, pointProjective);
        return Ed448.normalizeToNeutralElementVar(pointProjective);
    }

    private static void invertZs(PointProjective[] pointProjectiveArray) {
        int n = pointProjectiveArray.length;
        int[] nArray = F.createTable(n);
        int[] nArray2 = F.create();
        F.copy(pointProjectiveArray[0].z, 0, nArray2, 0);
        F.copy(nArray2, 0, nArray, 0);
        int n2 = 0;
        while (++n2 < n) {
            F.mul(nArray2, pointProjectiveArray[n2].z, nArray2);
            F.copy(nArray2, 0, nArray, n2 * 16);
        }
        F.invVar(nArray2, nArray2);
        --n2;
        int[] nArray3 = F.create();
        while (n2 > 0) {
            int n3 = n2--;
            F.copy(nArray, n2 * 16, nArray3, 0);
            F.mul(nArray3, nArray2, nArray3);
            F.mul(nArray2, pointProjectiveArray[n3].z, nArray2);
            F.copy(nArray3, 0, pointProjectiveArray[n3].z, 0);
        }
        F.copy(nArray2, 0, pointProjectiveArray[0].z, 0);
    }

    private static void normalizeToAffine(PointProjective pointProjective, PointAffine pointAffine) {
        F.inv(pointProjective.z, pointAffine.y);
        F.mul(pointAffine.y, pointProjective.x, pointAffine.x);
        F.mul(pointAffine.y, pointProjective.y, pointAffine.y);
        F.normalize(pointAffine.x);
        F.normalize(pointAffine.y);
    }

    private static boolean normalizeToNeutralElementVar(PointProjective pointProjective) {
        F.normalize(pointProjective.x);
        F.normalize(pointProjective.y);
        F.normalize(pointProjective.z);
        return F.isZeroVar(pointProjective.x) && !F.isZeroVar(pointProjective.y) && F.areEqualVar(pointProjective.y, pointProjective.z);
    }

    private static void pointAdd(PointAffine pointAffine, PointProjective pointProjective, PointTemp pointTemp) {
        int[] nArray = pointTemp.r1;
        int[] nArray2 = pointTemp.r2;
        int[] nArray3 = pointTemp.r3;
        int[] nArray4 = pointTemp.r4;
        int[] nArray5 = pointTemp.r5;
        int[] nArray6 = pointTemp.r6;
        int[] nArray7 = pointTemp.r7;
        F.sqr(pointProjective.z, nArray);
        F.mul(pointAffine.x, pointProjective.x, nArray2);
        F.mul(pointAffine.y, pointProjective.y, nArray3);
        F.mul(nArray2, nArray3, nArray4);
        F.mul(nArray4, 39081, nArray4);
        F.add(nArray, nArray4, nArray5);
        F.sub(nArray, nArray4, nArray6);
        F.add(pointAffine.y, pointAffine.x, nArray7);
        F.add(pointProjective.y, pointProjective.x, nArray4);
        F.mul(nArray7, nArray4, nArray7);
        F.add(nArray3, nArray2, nArray);
        F.sub(nArray3, nArray2, nArray4);
        F.carry(nArray);
        F.sub(nArray7, nArray, nArray7);
        F.mul(nArray7, pointProjective.z, nArray7);
        F.mul(nArray4, pointProjective.z, nArray4);
        F.mul(nArray5, nArray7, pointProjective.x);
        F.mul(nArray4, nArray6, pointProjective.y);
        F.mul(nArray5, nArray6, pointProjective.z);
    }

    private static void pointAdd(PointProjective pointProjective, PointProjective pointProjective2, PointTemp pointTemp) {
        int[] nArray = pointTemp.r0;
        int[] nArray2 = pointTemp.r1;
        int[] nArray3 = pointTemp.r2;
        int[] nArray4 = pointTemp.r3;
        int[] nArray5 = pointTemp.r4;
        int[] nArray6 = pointTemp.r5;
        int[] nArray7 = pointTemp.r6;
        int[] nArray8 = pointTemp.r7;
        F.mul(pointProjective.z, pointProjective2.z, nArray);
        F.sqr(nArray, nArray2);
        F.mul(pointProjective.x, pointProjective2.x, nArray3);
        F.mul(pointProjective.y, pointProjective2.y, nArray4);
        F.mul(nArray3, nArray4, nArray5);
        F.mul(nArray5, 39081, nArray5);
        F.add(nArray2, nArray5, nArray6);
        F.sub(nArray2, nArray5, nArray7);
        F.add(pointProjective.y, pointProjective.x, nArray8);
        F.add(pointProjective2.y, pointProjective2.x, nArray5);
        F.mul(nArray8, nArray5, nArray8);
        F.add(nArray4, nArray3, nArray2);
        F.sub(nArray4, nArray3, nArray5);
        F.carry(nArray2);
        F.sub(nArray8, nArray2, nArray8);
        F.mul(nArray8, nArray, nArray8);
        F.mul(nArray5, nArray, nArray5);
        F.mul(nArray6, nArray8, pointProjective2.x);
        F.mul(nArray5, nArray7, pointProjective2.y);
        F.mul(nArray6, nArray7, pointProjective2.z);
    }

    private static void pointAddVar(boolean bl, PointAffine pointAffine, PointProjective pointProjective, PointTemp pointTemp) {
        int[] nArray;
        int[] nArray2;
        int[] nArray3;
        int[] nArray4;
        int[] nArray5 = pointTemp.r1;
        int[] nArray6 = pointTemp.r2;
        int[] nArray7 = pointTemp.r3;
        int[] nArray8 = pointTemp.r4;
        int[] nArray9 = pointTemp.r5;
        int[] nArray10 = pointTemp.r6;
        int[] nArray11 = pointTemp.r7;
        if (bl) {
            nArray4 = nArray8;
            nArray3 = nArray5;
            nArray2 = nArray10;
            nArray = nArray9;
            F.sub(pointAffine.y, pointAffine.x, nArray11);
        } else {
            nArray4 = nArray5;
            nArray3 = nArray8;
            nArray2 = nArray9;
            nArray = nArray10;
            F.add(pointAffine.y, pointAffine.x, nArray11);
        }
        F.sqr(pointProjective.z, nArray5);
        F.mul(pointAffine.x, pointProjective.x, nArray6);
        F.mul(pointAffine.y, pointProjective.y, nArray7);
        F.mul(nArray6, nArray7, nArray8);
        F.mul(nArray8, 39081, nArray8);
        F.add(nArray5, nArray8, nArray2);
        F.sub(nArray5, nArray8, nArray);
        F.add(pointProjective.y, pointProjective.x, nArray8);
        F.mul(nArray11, nArray8, nArray11);
        F.add(nArray7, nArray6, nArray4);
        F.sub(nArray7, nArray6, nArray3);
        F.carry(nArray4);
        F.sub(nArray11, nArray5, nArray11);
        F.mul(nArray11, pointProjective.z, nArray11);
        F.mul(nArray8, pointProjective.z, nArray8);
        F.mul(nArray9, nArray11, pointProjective.x);
        F.mul(nArray8, nArray10, pointProjective.y);
        F.mul(nArray9, nArray10, pointProjective.z);
    }

    private static void pointAddVar(boolean bl, PointProjective pointProjective, PointProjective pointProjective2, PointTemp pointTemp) {
        int[] nArray;
        int[] nArray2;
        int[] nArray3;
        int[] nArray4;
        int[] nArray5 = pointTemp.r0;
        int[] nArray6 = pointTemp.r1;
        int[] nArray7 = pointTemp.r2;
        int[] nArray8 = pointTemp.r3;
        int[] nArray9 = pointTemp.r4;
        int[] nArray10 = pointTemp.r5;
        int[] nArray11 = pointTemp.r6;
        int[] nArray12 = pointTemp.r7;
        if (bl) {
            nArray4 = nArray9;
            nArray3 = nArray6;
            nArray2 = nArray11;
            nArray = nArray10;
            F.sub(pointProjective.y, pointProjective.x, nArray12);
        } else {
            nArray4 = nArray6;
            nArray3 = nArray9;
            nArray2 = nArray10;
            nArray = nArray11;
            F.add(pointProjective.y, pointProjective.x, nArray12);
        }
        F.mul(pointProjective.z, pointProjective2.z, nArray5);
        F.sqr(nArray5, nArray6);
        F.mul(pointProjective.x, pointProjective2.x, nArray7);
        F.mul(pointProjective.y, pointProjective2.y, nArray8);
        F.mul(nArray7, nArray8, nArray9);
        F.mul(nArray9, 39081, nArray9);
        F.add(nArray6, nArray9, nArray2);
        F.sub(nArray6, nArray9, nArray);
        F.add(pointProjective2.y, pointProjective2.x, nArray9);
        F.mul(nArray12, nArray9, nArray12);
        F.add(nArray8, nArray7, nArray4);
        F.sub(nArray8, nArray7, nArray3);
        F.carry(nArray4);
        F.sub(nArray12, nArray6, nArray12);
        F.mul(nArray12, nArray5, nArray12);
        F.mul(nArray9, nArray5, nArray9);
        F.mul(nArray10, nArray12, pointProjective2.x);
        F.mul(nArray9, nArray11, pointProjective2.y);
        F.mul(nArray10, nArray11, pointProjective2.z);
    }

    private static void pointCopy(PointAffine pointAffine, PointProjective pointProjective) {
        F.copy(pointAffine.x, 0, pointProjective.x, 0);
        F.copy(pointAffine.y, 0, pointProjective.y, 0);
        F.one(pointProjective.z);
    }

    private static void pointCopy(PointProjective pointProjective, PointProjective pointProjective2) {
        F.copy(pointProjective.x, 0, pointProjective2.x, 0);
        F.copy(pointProjective.y, 0, pointProjective2.y, 0);
        F.copy(pointProjective.z, 0, pointProjective2.z, 0);
    }

    private static void pointDouble(PointProjective pointProjective, PointTemp pointTemp) {
        int[] nArray = pointTemp.r1;
        int[] nArray2 = pointTemp.r2;
        int[] nArray3 = pointTemp.r3;
        int[] nArray4 = pointTemp.r4;
        int[] nArray5 = pointTemp.r7;
        int[] nArray6 = pointTemp.r0;
        F.add(pointProjective.x, pointProjective.y, nArray);
        F.sqr(nArray, nArray);
        F.sqr(pointProjective.x, nArray2);
        F.sqr(pointProjective.y, nArray3);
        F.add(nArray2, nArray3, nArray4);
        F.carry(nArray4);
        F.sqr(pointProjective.z, nArray5);
        F.add(nArray5, nArray5, nArray5);
        F.carry(nArray5);
        F.sub(nArray4, nArray5, nArray6);
        F.sub(nArray, nArray4, nArray);
        F.sub(nArray2, nArray3, nArray2);
        F.mul(nArray, nArray6, pointProjective.x);
        F.mul(nArray4, nArray2, pointProjective.y);
        F.mul(nArray4, nArray6, pointProjective.z);
    }

    private static void pointLookup(int n, int n2, PointAffine pointAffine) {
        int n3 = n * 16 * 2 * 16;
        for (int i = 0; i < 16; ++i) {
            int n4 = (i ^ n2) - 1 >> 31;
            F.cmov(n4, PRECOMP_BASE_COMB, n3, pointAffine.x, 0);
            F.cmov(n4, PRECOMP_BASE_COMB, n3 += 16, pointAffine.y, 0);
            n3 += 16;
        }
    }

    private static void pointLookup(int[] nArray, int n, int[] nArray2, PointProjective pointProjective) {
        int n2 = Ed448.getWindow4(nArray, n);
        int n3 = n2 >>> 3 ^ 1;
        int n4 = (n2 ^ -n3) & 7;
        int n5 = 0;
        for (int i = 0; i < 8; ++i) {
            int n6 = (i ^ n4) - 1 >> 31;
            F.cmov(n6, nArray2, n5, pointProjective.x, 0);
            F.cmov(n6, nArray2, n5 += 16, pointProjective.y, 0);
            F.cmov(n6, nArray2, n5 += 16, pointProjective.z, 0);
            n5 += 16;
        }
        F.cnegate(n3, pointProjective.x);
    }

    private static void pointLookup15(int[] nArray, PointProjective pointProjective) {
        int n = 336;
        F.copy(nArray, n, pointProjective.x, 0);
        F.copy(nArray, n += 16, pointProjective.y, 0);
        F.copy(nArray, n += 16, pointProjective.z, 0);
    }

    private static int[] pointPrecompute(PointProjective pointProjective, int n, PointTemp pointTemp) {
        PointProjective pointProjective2 = new PointProjective();
        Ed448.pointCopy(pointProjective, pointProjective2);
        PointProjective pointProjective3 = new PointProjective();
        Ed448.pointCopy(pointProjective, pointProjective3);
        Ed448.pointDouble(pointProjective3, pointTemp);
        int[] nArray = F.createTable(n * 3);
        int n2 = 0;
        int n3 = 0;
        while (true) {
            F.copy(pointProjective2.x, 0, nArray, n2);
            F.copy(pointProjective2.y, 0, nArray, n2 += 16);
            F.copy(pointProjective2.z, 0, nArray, n2 += 16);
            n2 += 16;
            if (++n3 == n) break;
            Ed448.pointAdd(pointProjective3, pointProjective2, pointTemp);
        }
        return nArray;
    }

    private static void pointPrecompute(PointAffine pointAffine, PointProjective[] pointProjectiveArray, int n, int n2, PointTemp pointTemp) {
        PointProjective pointProjective = new PointProjective();
        Ed448.pointCopy(pointAffine, pointProjective);
        Ed448.pointDouble(pointProjective, pointTemp);
        pointProjectiveArray[n] = new PointProjective();
        Ed448.pointCopy(pointAffine, pointProjectiveArray[n]);
        for (int i = 1; i < n2; ++i) {
            pointProjectiveArray[n + i] = new PointProjective();
            Ed448.pointCopy(pointProjectiveArray[n + i - 1], pointProjectiveArray[n + i]);
            Ed448.pointAdd(pointProjective, pointProjectiveArray[n + i], pointTemp);
        }
    }

    private static void pointSetNeutral(PointProjective pointProjective) {
        F.zero(pointProjective.x);
        F.one(pointProjective.y);
        F.one(pointProjective.z);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void precompute() {
        Object object = PRECOMP_LOCK;
        synchronized (object) {
            PointProjective pointProjective;
            int n;
            if (PRECOMP_BASE_COMB != null) {
                return;
            }
            int n2 = 32;
            int n3 = 80;
            int n4 = n2 * 2 + n3;
            PointProjective[] pointProjectiveArray = new PointProjective[n4];
            PointTemp pointTemp = new PointTemp();
            PointAffine pointAffine = new PointAffine();
            F.copy(B_x, 0, pointAffine.x, 0);
            F.copy(B_y, 0, pointAffine.y, 0);
            Ed448.pointPrecompute(pointAffine, pointProjectiveArray, 0, n2, pointTemp);
            PointAffine pointAffine2 = new PointAffine();
            F.copy(B225_x, 0, pointAffine2.x, 0);
            F.copy(B225_y, 0, pointAffine2.y, 0);
            Ed448.pointPrecompute(pointAffine2, pointProjectiveArray, n2, n2, pointTemp);
            PointProjective pointProjective2 = new PointProjective();
            Ed448.pointCopy(pointAffine, pointProjective2);
            int n5 = n2 * 2;
            PointProjective[] pointProjectiveArray2 = new PointProjective[5];
            for (n = 0; n < 5; ++n) {
                pointProjectiveArray2[n] = new PointProjective();
            }
            for (n = 0; n < 5; ++n) {
                int n6;
                int n7;
                int n8 = n5++;
                PointProjective pointProjective3 = new PointProjective();
                pointProjectiveArray[n8] = pointProjective3;
                pointProjective = pointProjective3;
                for (n7 = 0; n7 < 5; ++n7) {
                    if (n7 == 0) {
                        Ed448.pointCopy(pointProjective2, pointProjective);
                    } else {
                        Ed448.pointAdd(pointProjective2, pointProjective, pointTemp);
                    }
                    Ed448.pointDouble(pointProjective2, pointTemp);
                    Ed448.pointCopy(pointProjective2, pointProjectiveArray2[n7]);
                    if (n + n7 == 8) continue;
                    for (n6 = 1; n6 < 18; ++n6) {
                        Ed448.pointDouble(pointProjective2, pointTemp);
                    }
                }
                F.negate(pointProjective.x, pointProjective.x);
                for (n7 = 0; n7 < 4; ++n7) {
                    n6 = 1 << n7;
                    int n9 = 0;
                    while (n9 < n6) {
                        pointProjectiveArray[n5] = new PointProjective();
                        Ed448.pointCopy(pointProjectiveArray[n5 - n6], pointProjectiveArray[n5]);
                        Ed448.pointAdd(pointProjectiveArray2[n7], pointProjectiveArray[n5], pointTemp);
                        ++n9;
                        ++n5;
                    }
                }
            }
            Ed448.invertZs(pointProjectiveArray);
            PRECOMP_BASE_WNAF = new PointAffine[n2];
            for (n = 0; n < n2; ++n) {
                pointProjective = pointProjectiveArray[n];
                PointAffine pointAffine3 = Ed448.PRECOMP_BASE_WNAF[n] = new PointAffine();
                F.mul(pointProjective.x, pointProjective.z, pointAffine3.x);
                F.normalize(pointAffine3.x);
                F.mul(pointProjective.y, pointProjective.z, pointAffine3.y);
                F.normalize(pointAffine3.y);
            }
            PRECOMP_BASE225_WNAF = new PointAffine[n2];
            for (n = 0; n < n2; ++n) {
                pointProjective = pointProjectiveArray[n2 + n];
                PointAffine pointAffine4 = Ed448.PRECOMP_BASE225_WNAF[n] = new PointAffine();
                F.mul(pointProjective.x, pointProjective.z, pointAffine4.x);
                F.normalize(pointAffine4.x);
                F.mul(pointProjective.y, pointProjective.z, pointAffine4.y);
                F.normalize(pointAffine4.y);
            }
            PRECOMP_BASE_COMB = F.createTable(n3 * 2);
            n = 0;
            for (int i = n2 * 2; i < n4; ++i) {
                PointProjective pointProjective4 = pointProjectiveArray[i];
                F.mul(pointProjective4.x, pointProjective4.z, pointProjective4.x);
                F.normalize(pointProjective4.x);
                F.mul(pointProjective4.y, pointProjective4.z, pointProjective4.y);
                F.normalize(pointProjective4.y);
                F.copy(pointProjective4.x, 0, PRECOMP_BASE_COMB, n);
                F.copy(pointProjective4.y, 0, PRECOMP_BASE_COMB, n += 16);
                n += 16;
            }
        }
    }

    private static void pruneScalar(byte[] byArray, int n, byte[] byArray2) {
        System.arraycopy(byArray, n, byArray2, 0, 56);
        byArray2[0] = (byte)(byArray2[0] & 0xFC);
        byArray2[55] = (byte)(byArray2[55] | 0x80);
        byArray2[56] = 0;
    }

    private static void scalarMult(byte[] byArray, PointProjective pointProjective, PointProjective pointProjective2) {
        int[] nArray = new int[15];
        Scalar448.decode(byArray, nArray);
        Scalar448.toSignedDigits(449, nArray, nArray);
        PointProjective pointProjective3 = new PointProjective();
        PointTemp pointTemp = new PointTemp();
        int[] nArray2 = Ed448.pointPrecompute(pointProjective, 8, pointTemp);
        Ed448.pointLookup15(nArray2, pointProjective2);
        Ed448.pointAdd(pointProjective, pointProjective2, pointTemp);
        int n = 111;
        block0: while (true) {
            Ed448.pointLookup(nArray, n, nArray2, pointProjective3);
            Ed448.pointAdd(pointProjective3, pointProjective2, pointTemp);
            if (--n < 0) break;
            int n2 = 0;
            while (true) {
                if (n2 >= 4) continue block0;
                Ed448.pointDouble(pointProjective2, pointTemp);
                ++n2;
            }
            break;
        }
    }

    private static void scalarMultBase(byte[] byArray, PointProjective pointProjective) {
        Ed448.precompute();
        int[] nArray = new int[15];
        Scalar448.decode(byArray, nArray);
        Scalar448.toSignedDigits(450, nArray, nArray);
        PointAffine pointAffine = new PointAffine();
        PointTemp pointTemp = new PointTemp();
        Ed448.pointSetNeutral(pointProjective);
        int n = 17;
        while (true) {
            int n2 = n;
            for (int i = 0; i < 5; ++i) {
                int n3;
                int n4;
                int n5 = 0;
                for (n4 = 0; n4 < 5; ++n4) {
                    n3 = nArray[n2 >>> 5] >>> (n2 & 0x1F);
                    n5 &= ~(1 << n4);
                    n5 ^= n3 << n4;
                    n2 += 18;
                }
                n4 = n5 >>> 4 & 1;
                n3 = (n5 ^ -n4) & 0xF;
                Ed448.pointLookup(i, n3, pointAffine);
                F.cnegate(n4, pointAffine.x);
                Ed448.pointAdd(pointAffine, pointProjective, pointTemp);
            }
            if (--n < 0) break;
            Ed448.pointDouble(pointProjective, pointTemp);
        }
    }

    private static void scalarMultBaseEncoded(byte[] byArray, byte[] byArray2, int n) {
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultBase(byArray, pointProjective);
        if (0 == Ed448.encodeResult(pointProjective, byArray2, n)) {
            throw new IllegalStateException();
        }
    }

    public static void scalarMultBaseXY(X448.Friend friend, byte[] byArray, int n, int[] nArray, int[] nArray2) {
        if (null == friend) {
            throw new NullPointerException("This method is only for use by X448");
        }
        byte[] byArray2 = new byte[57];
        Ed448.pruneScalar(byArray, n, byArray2);
        PointProjective pointProjective = new PointProjective();
        Ed448.scalarMultBase(byArray2, pointProjective);
        if (0 == Ed448.checkPoint(pointProjective)) {
            throw new IllegalStateException();
        }
        F.copy(pointProjective.x, 0, nArray, 0);
        F.copy(pointProjective.y, 0, nArray2, 0);
    }

    private static void scalarMultOrderVar(PointAffine pointAffine, PointProjective pointProjective) {
        byte[] byArray = new byte[447];
        Scalar448.getOrderWnafVar(5, byArray);
        int n = 8;
        PointProjective[] pointProjectiveArray = new PointProjective[n];
        PointTemp pointTemp = new PointTemp();
        Ed448.pointPrecompute(pointAffine, pointProjectiveArray, 0, n, pointTemp);
        Ed448.pointSetNeutral(pointProjective);
        int n2 = 446;
        while (true) {
            byte by;
            if ((by = byArray[n2]) != 0) {
                int n3 = by >> 1 ^ by >> 31;
                Ed448.pointAddVar(by < 0, pointProjectiveArray[n3], pointProjective, pointTemp);
            }
            if (--n2 < 0) break;
            Ed448.pointDouble(pointProjective, pointTemp);
        }
    }

    private static void scalarMultStraus225Var(int[] nArray, int[] nArray2, PointAffine pointAffine, int[] nArray3, PointAffine pointAffine2, PointProjective pointProjective) {
        Ed448.precompute();
        byte[] byArray = new byte[450];
        byte[] byArray2 = new byte[225];
        byte[] byArray3 = new byte[225];
        Wnaf.getSignedVar(nArray, 7, byArray);
        Wnaf.getSignedVar(nArray2, 5, byArray2);
        Wnaf.getSignedVar(nArray3, 5, byArray3);
        int n = 8;
        PointProjective[] pointProjectiveArray = new PointProjective[n];
        PointProjective[] pointProjectiveArray2 = new PointProjective[n];
        PointTemp pointTemp = new PointTemp();
        Ed448.pointPrecompute(pointAffine, pointProjectiveArray, 0, n, pointTemp);
        Ed448.pointPrecompute(pointAffine2, pointProjectiveArray2, 0, n, pointTemp);
        Ed448.pointSetNeutral(pointProjective);
        int n2 = 225;
        while (--n2 >= 0 && (byArray[n2] | byArray[225 + n2] | byArray2[n2] | byArray3[n2]) == 0) {
        }
        while (n2 >= 0) {
            int n3;
            int n4;
            int n5;
            byte by = byArray[n2];
            if (by != 0) {
                n5 = by >> 1 ^ by >> 31;
                Ed448.pointAddVar(by < 0, PRECOMP_BASE_WNAF[n5], pointProjective, pointTemp);
            }
            if ((n5 = byArray[225 + n2]) != 0) {
                n4 = n5 >> 1 ^ n5 >> 31;
                Ed448.pointAddVar(n5 < 0, PRECOMP_BASE225_WNAF[n4], pointProjective, pointTemp);
            }
            if ((n4 = byArray2[n2]) != 0) {
                n3 = n4 >> 1 ^ n4 >> 31;
                Ed448.pointAddVar(n4 < 0, pointProjectiveArray[n3], pointProjective, pointTemp);
            }
            if ((n3 = byArray3[n2]) != 0) {
                int n6 = n3 >> 1 ^ n3 >> 31;
                Ed448.pointAddVar(n3 < 0, pointProjectiveArray2[n6], pointProjective, pointTemp);
            }
            Ed448.pointDouble(pointProjective, pointTemp);
            --n2;
        }
        Ed448.pointDouble(pointProjective, pointTemp);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, int n3, byte[] byArray4, int n4) {
        byte by = 0;
        Ed448.implSign(byArray, n, byArray2, by, byArray3, n2, n3, byArray4, n4);
    }

    public static void sign(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, int n4, byte[] byArray5, int n5) {
        byte by = 0;
        Ed448.implSign(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, n4, byArray5, n5);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, byte[] byArray3, int n2, byte[] byArray4, int n3) {
        byte by = 1;
        Ed448.implSign(byArray, n, byArray2, by, byArray3, n2, 64, byArray4, n3);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, byte[] byArray5, int n4) {
        byte by = 1;
        Ed448.implSign(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, 64, byArray5, n4);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, Xof xof, byte[] byArray3, int n2) {
        byte[] byArray4 = new byte[64];
        if (64 != xof.doFinal(byArray4, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        Ed448.implSign(byArray, n, byArray2, by, byArray4, 0, byArray4.length, byArray3, n2);
    }

    public static void signPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, Xof xof, byte[] byArray4, int n3) {
        byte[] byArray5 = new byte[64];
        if (64 != xof.doFinal(byArray5, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        Ed448.implSign(byArray, n, byArray2, n2, byArray3, by, byArray5, 0, byArray5.length, byArray4, n3);
    }

    public static boolean validatePublicKeyFull(byte[] byArray, int n) {
        byte[] byArray2 = Ed448.copy(byArray, n, 57);
        if (!Ed448.checkPointFullVar(byArray2)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed448.decodePointVar(byArray2, false, pointAffine)) {
            return false;
        }
        return Ed448.checkPointOrderVar(pointAffine);
    }

    public static PublicPoint validatePublicKeyFullExport(byte[] byArray, int n) {
        byte[] byArray2 = Ed448.copy(byArray, n, 57);
        if (!Ed448.checkPointFullVar(byArray2)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed448.decodePointVar(byArray2, false, pointAffine)) {
            return null;
        }
        if (!Ed448.checkPointOrderVar(pointAffine)) {
            return null;
        }
        return Ed448.exportPoint(pointAffine);
    }

    public static boolean validatePublicKeyPartial(byte[] byArray, int n) {
        byte[] byArray2 = Ed448.copy(byArray, n, 57);
        if (!Ed448.checkPointFullVar(byArray2)) {
            return false;
        }
        PointAffine pointAffine = new PointAffine();
        return Ed448.decodePointVar(byArray2, false, pointAffine);
    }

    public static PublicPoint validatePublicKeyPartialExport(byte[] byArray, int n) {
        byte[] byArray2 = Ed448.copy(byArray, n, 57);
        if (!Ed448.checkPointFullVar(byArray2)) {
            return null;
        }
        PointAffine pointAffine = new PointAffine();
        if (!Ed448.decodePointVar(byArray2, false, pointAffine)) {
            return null;
        }
        return Ed448.exportPoint(pointAffine);
    }

    public static boolean verify(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3, int n4) {
        byte by = 0;
        return Ed448.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, n4);
    }

    public static boolean verify(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte[] byArray3, int n2, int n3) {
        byte by = 0;
        return Ed448.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, n2, n3);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, byte[] byArray4, int n3) {
        byte by = 1;
        return Ed448.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, n3, 64);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, byte[] byArray3, int n2) {
        byte by = 1;
        return Ed448.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, n2, 64);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, byte[] byArray2, int n2, byte[] byArray3, Xof xof) {
        byte[] byArray4 = new byte[64];
        if (64 != xof.doFinal(byArray4, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        return Ed448.implVerify(byArray, n, byArray2, n2, byArray3, by, byArray4, 0, byArray4.length);
    }

    public static boolean verifyPrehash(byte[] byArray, int n, PublicPoint publicPoint, byte[] byArray2, Xof xof) {
        byte[] byArray3 = new byte[64];
        if (64 != xof.doFinal(byArray3, 0, 64)) {
            throw new IllegalArgumentException("ph");
        }
        byte by = 1;
        return Ed448.implVerify(byArray, n, publicPoint, byArray2, by, byArray3, 0, byArray3.length);
    }

    public static final class Algorithm {
        public static final int Ed448 = 0;
        public static final int Ed448ph = 1;
    }

    private static class F
    extends X448Field {
        private F() {
        }
    }

    private static class PointAffine {
        int[] x = F.create();
        int[] y = F.create();

        private PointAffine() {
        }
    }

    private static class PointProjective {
        int[] x = F.create();
        int[] y = F.create();
        int[] z = F.create();

        private PointProjective() {
        }
    }

    private static class PointTemp {
        int[] r0 = F.create();
        int[] r1 = F.create();
        int[] r2 = F.create();
        int[] r3 = F.create();
        int[] r4 = F.create();
        int[] r5 = F.create();
        int[] r6 = F.create();
        int[] r7 = F.create();

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

