/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtInvalidException;
import com.google.crypto.tink.jwt.RawJwt;
import com.google.crypto.tink.jwt.VerifiedJwt;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

@Immutable
public final class JwtValidator {
    private static final Duration MAX_CLOCK_SKEW = Duration.ofMinutes(10L);
    private final Optional<String> expectedTypeHeader;
    private final boolean ignoreTypeHeader;
    private final Optional<String> expectedIssuer;
    private final boolean ignoreIssuer;
    private final Optional<String> expectedAudience;
    private final boolean ignoreAudiences;
    private final boolean allowMissingExpiration;
    private final boolean expectIssuedInThePast;
    private final Clock clock;
    private final Duration clockSkew;

    private JwtValidator(Builder builder) {
        this.expectedTypeHeader = builder.expectedTypeHeader;
        this.ignoreTypeHeader = builder.ignoreTypeHeader;
        this.expectedIssuer = builder.expectedIssuer;
        this.ignoreIssuer = builder.ignoreIssuer;
        this.expectedAudience = builder.expectedAudience;
        this.ignoreAudiences = builder.ignoreAudiences;
        this.allowMissingExpiration = builder.allowMissingExpiration;
        this.expectIssuedInThePast = builder.expectIssuedInThePast;
        this.clock = builder.clock;
        this.clockSkew = builder.clockSkew;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    private void validateTypeHeader(RawJwt target) throws JwtInvalidException {
        if (this.expectedTypeHeader.isPresent()) {
            if (!target.hasTypeHeader()) {
                throw new JwtInvalidException(String.format("invalid JWT; missing expected type header %s.", this.expectedTypeHeader.get()));
            }
            if (!target.getTypeHeader().equals(this.expectedTypeHeader.get())) {
                throw new JwtInvalidException(String.format("invalid JWT; expected type header %s, but got %s", this.expectedTypeHeader.get(), target.getTypeHeader()));
            }
        } else if (target.hasTypeHeader() && !this.ignoreTypeHeader) {
            throw new JwtInvalidException("invalid JWT; token has type header set, but validator not.");
        }
    }

    private void validateIssuer(RawJwt target) throws JwtInvalidException {
        if (this.expectedIssuer.isPresent()) {
            if (!target.hasIssuer()) {
                throw new JwtInvalidException(String.format("invalid JWT; missing expected issuer %s.", this.expectedIssuer.get()));
            }
            if (!target.getIssuer().equals(this.expectedIssuer.get())) {
                throw new JwtInvalidException(String.format("invalid JWT; expected issuer %s, but got %s", this.expectedIssuer.get(), target.getIssuer()));
            }
        } else if (target.hasIssuer() && !this.ignoreIssuer) {
            throw new JwtInvalidException("invalid JWT; token has issuer set, but validator not.");
        }
    }

    private void validateAudiences(RawJwt target) throws JwtInvalidException {
        if (this.expectedAudience.isPresent()) {
            if (!target.hasAudiences() || !target.getAudiences().contains(this.expectedAudience.get())) {
                throw new JwtInvalidException(String.format("invalid JWT; missing expected audience %s.", this.expectedAudience.get()));
            }
        } else if (target.hasAudiences() && !this.ignoreAudiences) {
            throw new JwtInvalidException("invalid JWT; token has audience set, but validator not.");
        }
    }

    VerifiedJwt validate(RawJwt target) throws JwtInvalidException {
        this.validateTimestampClaims(target);
        this.validateTypeHeader(target);
        this.validateIssuer(target);
        this.validateAudiences(target);
        return new VerifiedJwt(target);
    }

    private void validateTimestampClaims(RawJwt target) throws JwtInvalidException {
        Instant now = this.clock.instant();
        if (!target.hasExpiration() && !this.allowMissingExpiration) {
            throw new JwtInvalidException("token does not have an expiration set");
        }
        if (target.hasExpiration() && !target.getExpiration().isAfter(now.minus(this.clockSkew))) {
            throw new JwtInvalidException("token has expired since " + target.getExpiration());
        }
        if (target.hasNotBefore() && target.getNotBefore().isAfter(now.plus(this.clockSkew))) {
            throw new JwtInvalidException("token cannot be used before " + target.getNotBefore());
        }
        if (this.expectIssuedInThePast) {
            if (!target.hasIssuedAt()) {
                throw new JwtInvalidException("token does not have an iat claim");
            }
            if (target.getIssuedAt().isAfter(now.plus(this.clockSkew))) {
                throw new JwtInvalidException("token has a invalid iat claim in the future: " + target.getIssuedAt());
            }
        }
    }

    public String toString() {
        ArrayList<String> items = new ArrayList<String>();
        if (this.expectedTypeHeader.isPresent()) {
            items.add("expectedTypeHeader=" + this.expectedTypeHeader.get());
        }
        if (this.ignoreTypeHeader) {
            items.add("ignoreTypeHeader");
        }
        if (this.expectedIssuer.isPresent()) {
            items.add("expectedIssuer=" + this.expectedIssuer.get());
        }
        if (this.ignoreIssuer) {
            items.add("ignoreIssuer");
        }
        if (this.expectedAudience.isPresent()) {
            items.add("expectedAudience=" + this.expectedAudience.get());
        }
        if (this.ignoreAudiences) {
            items.add("ignoreAudiences");
        }
        if (this.allowMissingExpiration) {
            items.add("allowMissingExpiration");
        }
        if (this.expectIssuedInThePast) {
            items.add("expectIssuedInThePast");
        }
        if (!this.clockSkew.isZero()) {
            items.add("clockSkew=" + this.clockSkew);
        }
        StringBuilder b = new StringBuilder();
        b.append("JwtValidator{");
        String currentSeparator = "";
        for (String i : items) {
            b.append(currentSeparator);
            b.append(i);
            currentSeparator = ",";
        }
        b.append("}");
        return b.toString();
    }

    public static final class Builder {
        private Optional<String> expectedTypeHeader;
        private boolean ignoreTypeHeader = false;
        private Optional<String> expectedIssuer;
        private boolean ignoreIssuer = false;
        private Optional<String> expectedAudience;
        private boolean ignoreAudiences = false;
        private boolean allowMissingExpiration = false;
        private boolean expectIssuedInThePast = false;
        private Clock clock = Clock.systemUTC();
        private Duration clockSkew = Duration.ZERO;

        private Builder() {
            this.expectedTypeHeader = Optional.empty();
            this.expectedIssuer = Optional.empty();
            this.expectedAudience = Optional.empty();
        }

        @CanIgnoreReturnValue
        public Builder expectTypeHeader(String value) {
            if (value == null) {
                throw new NullPointerException("typ header cannot be null");
            }
            this.expectedTypeHeader = Optional.of(value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder ignoreTypeHeader() {
            this.ignoreTypeHeader = true;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder expectIssuer(String value) {
            if (value == null) {
                throw new NullPointerException("issuer cannot be null");
            }
            this.expectedIssuer = Optional.of(value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder ignoreIssuer() {
            this.ignoreIssuer = true;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder expectAudience(String value) {
            if (value == null) {
                throw new NullPointerException("audience cannot be null");
            }
            this.expectedAudience = Optional.of(value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder ignoreAudiences() {
            this.ignoreAudiences = true;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder expectIssuedInThePast() {
            this.expectIssuedInThePast = true;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setClock(Clock clock) {
            if (clock == null) {
                throw new NullPointerException("clock cannot be null");
            }
            this.clock = clock;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setClockSkew(Duration clockSkew) {
            if (clockSkew.compareTo(MAX_CLOCK_SKEW) > 0) {
                throw new IllegalArgumentException("Clock skew too large, max is 10 minutes");
            }
            this.clockSkew = clockSkew;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder allowMissingExpiration() {
            this.allowMissingExpiration = true;
            return this;
        }

        public JwtValidator build() {
            if (this.ignoreTypeHeader && this.expectedTypeHeader.isPresent()) {
                throw new IllegalArgumentException("ignoreTypeHeader() and expectedTypeHeader() cannot be used together.");
            }
            if (this.ignoreIssuer && this.expectedIssuer.isPresent()) {
                throw new IllegalArgumentException("ignoreIssuer() and expectedIssuer() cannot be used together.");
            }
            if (this.ignoreAudiences && this.expectedAudience.isPresent()) {
                throw new IllegalArgumentException("ignoreAudiences() and expectedAudience() cannot be used together.");
            }
            return new JwtValidator(this);
        }
    }
}

