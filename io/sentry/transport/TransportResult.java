/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.transport;

import org.jetbrains.annotations.NotNull;

public abstract class TransportResult {
    @NotNull
    public static TransportResult success() {
        return SuccessTransportResult.INSTANCE;
    }

    @NotNull
    public static TransportResult error(int responseCode) {
        return new ErrorTransportResult(responseCode);
    }

    @NotNull
    public static TransportResult error() {
        return TransportResult.error(-1);
    }

    private TransportResult() {
    }

    public abstract boolean isSuccess();

    public abstract int getResponseCode();

    private static final class SuccessTransportResult
    extends TransportResult {
        static final SuccessTransportResult INSTANCE = new SuccessTransportResult();

        private SuccessTransportResult() {
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public int getResponseCode() {
            return -1;
        }
    }

    private static final class ErrorTransportResult
    extends TransportResult {
        private final int responseCode;

        ErrorTransportResult(int responseCode) {
            this.responseCode = responseCode;
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public int getResponseCode() {
            return this.responseCode;
        }
    }
}

