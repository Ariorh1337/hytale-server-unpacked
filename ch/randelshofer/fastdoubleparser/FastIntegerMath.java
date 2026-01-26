/*
 * Decompiled with CFR 0.152.
 */
package ch.randelshofer.fastdoubleparser;

import ch.randelshofer.fastdoubleparser.FftMultiplier;
import java.math.BigInteger;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

final class FastIntegerMath {
    public static final BigInteger FIVE = BigInteger.valueOf(5L);
    static final BigInteger TEN_POW_16 = BigInteger.valueOf(10000000000000000L);
    static final BigInteger FIVE_POW_16 = BigInteger.valueOf(152587890625L);
    private static final BigInteger[] SMALL_POWERS_OF_TEN = new BigInteger[]{BigInteger.ONE, BigInteger.TEN, BigInteger.valueOf(100L), BigInteger.valueOf(1000L), BigInteger.valueOf(10000L), BigInteger.valueOf(100000L), BigInteger.valueOf(1000000L), BigInteger.valueOf(10000000L), BigInteger.valueOf(100000000L), BigInteger.valueOf(1000000000L), BigInteger.valueOf(10000000000L), BigInteger.valueOf(100000000000L), BigInteger.valueOf(1000000000000L), BigInteger.valueOf(10000000000000L), BigInteger.valueOf(100000000000000L), BigInteger.valueOf(1000000000000000L)};

    private FastIntegerMath() {
    }

    static BigInteger computePowerOfTen(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        if (n < SMALL_POWERS_OF_TEN.length) {
            return SMALL_POWERS_OF_TEN[n];
        }
        if (powersOfTen != null) {
            Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n);
            Integer floorN = floorEntry.getKey();
            if (floorN == n) {
                return floorEntry.getValue();
            }
            return FftMultiplier.multiply(floorEntry.getValue(), FastIntegerMath.computePowerOfTen(powersOfTen, n - floorN));
        }
        return FIVE.pow(n).shiftLeft(n);
    }

    static BigInteger computeTenRaisedByNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int n) {
        Map.Entry<Integer, BigInteger> floorEntry = powersOfTen.floorEntry(n &= 0xFFFFFFF0);
        int floorPower = floorEntry.getKey();
        BigInteger floorValue = floorEntry.getValue();
        if (floorPower == n) {
            return floorValue;
        }
        int diff = n - floorPower;
        BigInteger diffValue = (BigInteger)powersOfTen.get(diff);
        if (diffValue == null) {
            diffValue = FastIntegerMath.computeTenRaisedByNFloor16Recursive(powersOfTen, diff);
            powersOfTen.put(diff, diffValue);
        }
        return FftMultiplier.multiply(floorValue, diffValue);
    }

    static NavigableMap<Integer, BigInteger> createPowersOfTenFloor16Map() {
        TreeMap<Integer, BigInteger> powersOfTen = new TreeMap<Integer, BigInteger>();
        powersOfTen.put(0, BigInteger.ONE);
        powersOfTen.put(16, TEN_POW_16);
        return powersOfTen;
    }

    public static long estimateNumBits(long numDecimalDigits) {
        return (numDecimalDigits * 3402L >>> 10) + 1L;
    }

    static NavigableMap<Integer, BigInteger> fillPowersOf10Floor16(int from, int to) {
        TreeMap<Integer, BigInteger> powers = new TreeMap<Integer, BigInteger>();
        powers.put(0, BigInteger.valueOf(5L));
        powers.put(16, FIVE_POW_16);
        FastIntegerMath.fillPowersOfNFloor16Recursive(powers, from, to);
        for (Map.Entry entry : powers.entrySet()) {
            entry.setValue(((BigInteger)entry.getValue()).shiftLeft((Integer)entry.getKey()));
        }
        return powers;
    }

    static void fillPowersOfNFloor16Recursive(NavigableMap<Integer, BigInteger> powersOfTen, int from, int to) {
        int numDigits = to - from;
        if (numDigits <= 18) {
            return;
        }
        int mid = FastIntegerMath.splitFloor16(from, to);
        int n = to - mid;
        if (!powersOfTen.containsKey(n)) {
            FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, from, mid);
            FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, mid, to);
            powersOfTen.put(n, FastIntegerMath.computeTenRaisedByNFloor16Recursive(powersOfTen, n));
        }
    }

    static long unsignedMultiplyHigh(long x, long y) {
        return Math.unsignedMultiplyHigh(x, y);
    }

    static int splitFloor16(int from, int to) {
        int range = to - from + 31 >>> 5 << 4;
        return to - range;
    }
}

