/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.framework.math;

import javax.annotation.Nonnull;

public class BitConverter {
    public static void main(String[] args) {
        boolean[] output;
        int i;
        System.out.println("LONG TEST:");
        for (i = -4; i < 10; ++i) {
            System.out.println();
            System.out.print("INPUT [" + i + "] -> BINARY -> [");
            for (boolean bit : output = BitConverter.toBitArray((long)i)) {
                System.out.print(bit ? "1" : "0");
            }
            System.out.print("] -> DECIMAL -> [" + BitConverter.toLong(output) + "]");
        }
        System.out.println();
        System.out.println("INT TEST:");
        for (i = -4; i < 10; ++i) {
            System.out.println();
            System.out.print("INPUT [" + i + "] -> BINARY -> [");
            for (boolean bit : output = BitConverter.toBitArray(i)) {
                System.out.print(bit ? "1" : "0");
            }
            System.out.print("] -> DECIMAL -> [" + BitConverter.toInt(output) + "]");
        }
        System.out.println();
        System.out.println("BYTE TEST:");
        for (i = -4; i < 10; ++i) {
            System.out.println();
            System.out.print("INPUT [" + i + "] -> BINARY -> [");
            for (boolean bit : output = BitConverter.toBitArray((byte)i)) {
                System.out.print(bit ? "1" : "0");
            }
            System.out.print("] -> DECIMAL -> [" + BitConverter.toByte(output) + "]");
        }
        System.out.println();
    }

    public static boolean[] toBitArray(long number) {
        int PRECISION = 64;
        boolean[] bits = new boolean[64];
        long position = 1L;
        for (int i = 63; i >= 0; i = (int)((byte)(i - 1))) {
            bits[i] = (number & position) != 0L;
            position <<= 1;
        }
        return bits;
    }

    public static boolean[] toBitArray(int number) {
        int PRECISION = 32;
        boolean[] bits = new boolean[32];
        int position = 1;
        for (int i = 31; i >= 0; i = (int)((byte)(i - 1))) {
            bits[i] = (number & position) != 0;
            position <<= 1;
        }
        return bits;
    }

    public static boolean[] toBitArray(byte number) {
        int PRECISION = 8;
        boolean[] bits = new boolean[8];
        byte position = 1;
        for (int i = 7; i >= 0; i = (int)((byte)(i - 1))) {
            bits[i] = (number & position) != 0;
            position = (byte)(position << 1);
        }
        return bits;
    }

    public static long toLong(@Nonnull boolean[] bits) {
        int PRECISION = 64;
        if (bits.length != 64) {
            throw new IllegalArgumentException("array must have length 64");
        }
        long position = 1L;
        long number = 0L;
        for (int i = 63; i >= 0; i = (int)((byte)(i - 1))) {
            if (bits[i]) {
                number += position;
            }
            position <<= 1;
        }
        return number;
    }

    public static int toInt(@Nonnull boolean[] bits) {
        int PRECISION = 32;
        if (bits.length != 32) {
            throw new IllegalArgumentException("array must have length 32");
        }
        int position = 1;
        int number = 0;
        for (int i = 31; i >= 0; i = (int)((byte)(i - 1))) {
            if (bits[i]) {
                number += position;
            }
            position <<= 1;
        }
        return number;
    }

    public static int toByte(@Nonnull boolean[] bits) {
        int PRECISION = 8;
        if (bits.length != 8) {
            throw new IllegalArgumentException("array must have length 8");
        }
        int position = 1;
        int number = 0;
        for (int i = 7; i >= 0; i = (int)((byte)(i - 1))) {
            if (bits[i]) {
                number = (byte)(number + position);
            }
            position = (byte)(position << 1);
        }
        return number;
    }
}

