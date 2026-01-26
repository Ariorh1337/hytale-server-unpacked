/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Experimental
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.Baggage;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Experimental
public final class BaggageHeader {
    @NotNull
    public static final String BAGGAGE_HEADER = "baggage";
    @NotNull
    private final String value;

    @Nullable
    public static BaggageHeader fromBaggageAndOutgoingHeader(@NotNull Baggage baggage, @Nullable List<String> outgoingBaggageHeaders) {
        Baggage thirdPartyBaggage = Baggage.fromHeader(outgoingBaggageHeaders, true, baggage.logger);
        String headerValue = baggage.toHeaderString(thirdPartyBaggage.getThirdPartyHeader());
        if (headerValue.isEmpty()) {
            return null;
        }
        return new BaggageHeader(headerValue);
    }

    public BaggageHeader(@NotNull String value) {
        this.value = value;
    }

    @NotNull
    public String getName() {
        return BAGGAGE_HEADER;
    }

    @NotNull
    public String getValue() {
        return this.value;
    }
}

