/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.UpdateInfo;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public abstract class UpdateStatus {

    public static final class NoNetwork
    extends UpdateStatus {
        @NotNull
        private final String message;

        public NoNetwork(@NotNull String message) {
            this.message = message;
        }

        @NotNull
        public String getMessage() {
            return this.message;
        }

        public String toString() {
            return "UpdateStatus.NoNetwork{message='" + this.message + '\'' + '}';
        }
    }

    public static final class UpdateError
    extends UpdateStatus {
        @NotNull
        private final String message;

        public UpdateError(@NotNull String message) {
            this.message = message;
        }

        @NotNull
        public String getMessage() {
            return this.message;
        }

        public String toString() {
            return "UpdateStatus.UpdateError{message='" + this.message + '\'' + '}';
        }
    }

    public static final class NewRelease
    extends UpdateStatus {
        @NotNull
        private final UpdateInfo info;

        public NewRelease(@NotNull UpdateInfo info) {
            this.info = info;
        }

        @NotNull
        public UpdateInfo getInfo() {
            return this.info;
        }

        public String toString() {
            return "UpdateStatus.NewRelease{info=" + this.info + '}';
        }
    }

    public static final class UpToDate
    extends UpdateStatus {
        private static final UpToDate INSTANCE = new UpToDate();

        private UpToDate() {
        }

        public static UpToDate getInstance() {
            return INSTANCE;
        }

        public String toString() {
            return "UpdateStatus.UpToDate{}";
        }
    }
}

