/*
 * Decompiled with CFR 0.152.
 */
package io.sentry.util;

import io.sentry.util.Random;
import io.sentry.util.SentryRandom;
import java.util.UUID;

public final class UUIDGenerator {
    public static long randomHalfLengthUUID() {
        Random ng = SentryRandom.current();
        byte[] randomBytes = new byte[8];
        ng.nextBytes(randomBytes);
        randomBytes[6] = (byte)(randomBytes[6] & 0xF);
        randomBytes[6] = (byte)(randomBytes[6] | 0x40);
        long msb = 0L;
        for (int i = 0; i < 8; ++i) {
            msb = msb << 8 | (long)(randomBytes[i] & 0xFF);
        }
        return msb;
    }

    public static UUID randomUUID() {
        int i;
        Random ng = SentryRandom.current();
        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        randomBytes[6] = (byte)(randomBytes[6] & 0xF);
        randomBytes[6] = (byte)(randomBytes[6] | 0x40);
        randomBytes[8] = (byte)(randomBytes[8] & 0x3F);
        randomBytes[8] = (byte)(randomBytes[8] | 0x80);
        long msb = 0L;
        long lsb = 0L;
        for (i = 0; i < 8; ++i) {
            msb = msb << 8 | (long)(randomBytes[i] & 0xFF);
        }
        for (i = 8; i < 16; ++i) {
            lsb = lsb << 8 | (long)(randomBytes[i] & 0xFF);
        }
        return new UUID(msb, lsb);
    }
}

