/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

import java.util.Random;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;

public class CoPrimeGenerator {
    public static long[] generateCoPrimes(long seed, int bucketSize, int numberOfBuckets, long floor) {
        if (bucketSize < 1 || numberOfBuckets < 1) {
            throw new IllegalArgumentException("invalid sizes");
        }
        Random rand = new Random(seed);
        int[] primes = new int[bucketSize * numberOfBuckets];
        CoPrimeGenerator.fillWithPrimes(primes);
        int[][] buckets = new int[numberOfBuckets][bucketSize];
        long[] output = new long[numberOfBuckets];
        IntStream.range(0, output.length).forEach(i -> {
            output[i] = 1L;
        });
        int indexOfBucket = 0;
        int indexInsideBucket = 0;
        for (int indexOfPrime = 0; indexOfPrime < primes.length; ++indexOfPrime) {
            buckets[indexOfBucket][indexInsideBucket] = primes[indexOfPrime];
            if (indexOfBucket == numberOfBuckets - 1) {
                ++indexInsideBucket;
            }
            indexOfBucket = (indexOfBucket + 1) % numberOfBuckets;
        }
        for (int i2 = 0; i2 < numberOfBuckets; ++i2) {
            while (output[i2] < floor) {
                int n = i2;
                output[n] = output[n] * (long)buckets[i2][rand.nextInt(bucketSize)];
            }
        }
        return output;
    }

    public static void fillWithPrimes(@Nonnull int[] bucket) {
        int number = 2;
        int index = 0;
        while (index < bucket.length) {
            if (CoPrimeGenerator.isPrime(number)) {
                bucket[index] = number;
                ++index;
            }
            ++number;
        }
    }

    public static boolean isPrime(int number) {
        for (int i = 2; i < number; ++i) {
            if (number % i != 0) continue;
            return false;
        }
        return true;
    }
}

