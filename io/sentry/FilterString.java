/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FilterString {
    @NotNull
    private final String filterString;
    @Nullable
    private final Pattern pattern;

    public FilterString(@NotNull String filterString) {
        this.filterString = filterString;
        @Nullable Pattern pattern = null;
        try {
            pattern = Pattern.compile(filterString);
        }
        catch (Throwable t) {
            Sentry.getCurrentScopes().getOptions().getLogger().log(SentryLevel.DEBUG, "Only using filter string for String comparison as it could not be parsed as regex: %s", filterString);
        }
        this.pattern = pattern;
    }

    @NotNull
    public String getFilterString() {
        return this.filterString;
    }

    public boolean matches(String input) {
        if (this.pattern == null) {
            return false;
        }
        return this.pattern.matcher(input).matches();
    }

    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FilterString that = (FilterString)o;
        return Objects.equals(this.filterString, that.filterString);
    }

    public int hashCode() {
        return Objects.hash(this.filterString);
    }
}

