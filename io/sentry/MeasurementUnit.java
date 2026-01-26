/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface MeasurementUnit {
    @ApiStatus.Internal
    public static final String NONE = "none";

    @NotNull
    public String name();

    @ApiStatus.Internal
    @NotNull
    public String apiName();

    public static final class Custom
    implements MeasurementUnit {
        @NotNull
        private final String name;

        public Custom(@NotNull String name) {
            this.name = name;
        }

        @Override
        @NotNull
        public String name() {
            return this.name;
        }

        @Override
        @NotNull
        public String apiName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static enum Fraction implements MeasurementUnit
    {
        RATIO,
        PERCENT;


        @Override
        @NotNull
        public String apiName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static enum Information implements MeasurementUnit
    {
        BIT,
        BYTE,
        KILOBYTE,
        KIBIBYTE,
        MEGABYTE,
        MEBIBYTE,
        GIGABYTE,
        GIBIBYTE,
        TERABYTE,
        TEBIBYTE,
        PETABYTE,
        PEBIBYTE,
        EXABYTE,
        EXBIBYTE;


        @Override
        @NotNull
        public String apiName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public static enum Duration implements MeasurementUnit
    {
        NANOSECOND,
        MICROSECOND,
        MILLISECOND,
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK;


        @Override
        @NotNull
        public String apiName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}

