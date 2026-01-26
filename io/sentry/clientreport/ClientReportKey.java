/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.clientreport;

import io.sentry.util.Objects;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
final class ClientReportKey {
    @NotNull
    private final String reason;
    @NotNull
    private final String category;

    ClientReportKey(@NotNull String reason, @NotNull String category) {
        this.reason = reason;
        this.category = category;
    }

    @NotNull
    public String getReason() {
        return this.reason;
    }

    @NotNull
    public String getCategory() {
        return this.category;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ClientReportKey)) {
            return false;
        }
        ClientReportKey that = (ClientReportKey)o;
        return Objects.equals(this.getReason(), that.getReason()) && Objects.equals(this.getCategory(), that.getCategory());
    }

    public int hashCode() {
        return Objects.hash(this.getReason(), this.getCategory());
    }
}

