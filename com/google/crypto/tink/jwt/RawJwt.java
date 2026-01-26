/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.jwt.JwtInvalidException;
import com.google.crypto.tink.jwt.internal.JsonUtil;
import com.google.crypto.tink.jwt.internal.JwtNames;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.Immutable;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Immutable
public final class RawJwt {
    private static final long MAX_TIMESTAMP_VALUE = 253402300799L;
    private final JsonObject payload;
    private final Optional<String> typeHeader;

    private RawJwt(Builder builder) {
        if (!builder.payload.has("exp") && !builder.withoutExpiration) {
            throw new IllegalArgumentException("neither setExpiration() nor withoutExpiration() was called");
        }
        if (builder.payload.has("exp") && builder.withoutExpiration) {
            throw new IllegalArgumentException("setExpiration() and withoutExpiration() must not be called together");
        }
        this.typeHeader = builder.typeHeader;
        this.payload = builder.payload.deepCopy();
    }

    private RawJwt(Optional<String> typeHeader, String jsonPayload) throws JwtInvalidException {
        this.typeHeader = typeHeader;
        this.payload = JsonUtil.parseJson(jsonPayload);
        this.validateStringClaim("iss");
        this.validateStringClaim("sub");
        this.validateStringClaim("jti");
        this.validateTimestampClaim("exp");
        this.validateTimestampClaim("nbf");
        this.validateTimestampClaim("iat");
        this.validateAudienceClaim();
    }

    private void validateStringClaim(String name) throws JwtInvalidException {
        if (!this.payload.has(name)) {
            return;
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isString()) {
            throw new JwtInvalidException("invalid JWT payload: claim " + name + " is not a string.");
        }
    }

    private void validateTimestampClaim(String name) throws JwtInvalidException {
        if (!this.payload.has(name)) {
            return;
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isNumber()) {
            throw new JwtInvalidException("invalid JWT payload: claim " + name + " is not a number.");
        }
        double timestamp = this.payload.get(name).getAsJsonPrimitive().getAsDouble();
        if (timestamp > 2.53402300799E11 || timestamp < 0.0) {
            throw new JwtInvalidException("invalid JWT payload: claim " + name + " has an invalid timestamp");
        }
    }

    private void validateAudienceClaim() throws JwtInvalidException {
        if (!this.payload.has("aud")) {
            return;
        }
        if (this.payload.get("aud").isJsonPrimitive() && this.payload.get("aud").getAsJsonPrimitive().isString()) {
            return;
        }
        List<String> audiences = this.getAudiences();
        if (audiences.size() < 1) {
            throw new JwtInvalidException("invalid JWT payload: claim aud is present but empty.");
        }
    }

    static RawJwt fromJsonPayload(Optional<String> typeHeader, String jsonPayload) throws JwtInvalidException {
        return new RawJwt(typeHeader, jsonPayload);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getJsonPayload() {
        return this.payload.toString();
    }

    boolean hasBooleanClaim(String name) {
        JwtNames.validate(name);
        return this.payload.has(name) && this.payload.get(name).isJsonPrimitive() && this.payload.get(name).getAsJsonPrimitive().isBoolean();
    }

    Boolean getBooleanClaim(String name) throws JwtInvalidException {
        JwtNames.validate(name);
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isBoolean()) {
            throw new JwtInvalidException("claim " + name + " is not a boolean");
        }
        return this.payload.get(name).getAsBoolean();
    }

    boolean hasNumberClaim(String name) {
        JwtNames.validate(name);
        return this.payload.has(name) && this.payload.get(name).isJsonPrimitive() && this.payload.get(name).getAsJsonPrimitive().isNumber();
    }

    Double getNumberClaim(String name) throws JwtInvalidException {
        JwtNames.validate(name);
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isNumber()) {
            throw new JwtInvalidException("claim " + name + " is not a number");
        }
        return this.payload.get(name).getAsDouble();
    }

    boolean hasStringClaim(String name) {
        JwtNames.validate(name);
        return this.payload.has(name) && this.payload.get(name).isJsonPrimitive() && this.payload.get(name).getAsJsonPrimitive().isString();
    }

    String getStringClaim(String name) throws JwtInvalidException {
        JwtNames.validate(name);
        return this.getStringClaimInternal(name);
    }

    private String getStringClaimInternal(String name) throws JwtInvalidException {
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isString()) {
            throw new JwtInvalidException("claim " + name + " is not a string");
        }
        return this.payload.get(name).getAsString();
    }

    boolean isNullClaim(String name) {
        JwtNames.validate(name);
        try {
            return JsonNull.INSTANCE.equals(this.payload.get(name));
        }
        catch (JsonParseException ex) {
            return false;
        }
    }

    boolean hasJsonObjectClaim(String name) {
        JwtNames.validate(name);
        return this.payload.has(name) && this.payload.get(name).isJsonObject();
    }

    String getJsonObjectClaim(String name) throws JwtInvalidException {
        JwtNames.validate(name);
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonObject()) {
            throw new JwtInvalidException("claim " + name + " is not a JSON object");
        }
        return this.payload.get(name).getAsJsonObject().toString();
    }

    boolean hasJsonArrayClaim(String name) {
        JwtNames.validate(name);
        return this.payload.has(name) && this.payload.get(name).isJsonArray();
    }

    String getJsonArrayClaim(String name) throws JwtInvalidException {
        JwtNames.validate(name);
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonArray()) {
            throw new JwtInvalidException("claim " + name + " is not a JSON array");
        }
        return this.payload.get(name).getAsJsonArray().toString();
    }

    boolean hasTypeHeader() {
        return this.typeHeader.isPresent();
    }

    String getTypeHeader() throws JwtInvalidException {
        if (!this.typeHeader.isPresent()) {
            throw new JwtInvalidException("type header is not set");
        }
        return this.typeHeader.get();
    }

    boolean hasIssuer() {
        return this.payload.has("iss");
    }

    String getIssuer() throws JwtInvalidException {
        return this.getStringClaimInternal("iss");
    }

    boolean hasSubject() {
        return this.payload.has("sub");
    }

    String getSubject() throws JwtInvalidException {
        return this.getStringClaimInternal("sub");
    }

    boolean hasJwtId() {
        return this.payload.has("jti");
    }

    String getJwtId() throws JwtInvalidException {
        return this.getStringClaimInternal("jti");
    }

    boolean hasAudiences() {
        return this.payload.has("aud");
    }

    List<String> getAudiences() throws JwtInvalidException {
        if (!this.hasAudiences()) {
            throw new JwtInvalidException("claim aud does not exist");
        }
        JsonElement aud = this.payload.get("aud");
        if (aud.isJsonPrimitive()) {
            if (!aud.getAsJsonPrimitive().isString()) {
                throw new JwtInvalidException(String.format("invalid audience: got %s; want a string", aud));
            }
            return Collections.unmodifiableList(Arrays.asList(aud.getAsString()));
        }
        if (!aud.isJsonArray()) {
            throw new JwtInvalidException("claim aud is not a string or a JSON array");
        }
        JsonArray audiences = aud.getAsJsonArray();
        ArrayList<String> result = new ArrayList<String>(audiences.size());
        for (int i = 0; i < audiences.size(); ++i) {
            if (!audiences.get(i).isJsonPrimitive() || !audiences.get(i).getAsJsonPrimitive().isString()) {
                throw new JwtInvalidException(String.format("invalid audience: got %s; want a string", audiences.get(i)));
            }
            String audience = audiences.get(i).getAsString();
            result.add(audience);
        }
        return Collections.unmodifiableList(result);
    }

    private Instant getInstant(String name) throws JwtInvalidException {
        if (!this.payload.has(name)) {
            throw new JwtInvalidException("claim " + name + " does not exist");
        }
        if (!this.payload.get(name).isJsonPrimitive() || !this.payload.get(name).getAsJsonPrimitive().isNumber()) {
            throw new JwtInvalidException("claim " + name + " is not a timestamp");
        }
        try {
            double millis = this.payload.get(name).getAsJsonPrimitive().getAsDouble() * 1000.0;
            return Instant.ofEpochMilli((long)millis);
        }
        catch (NumberFormatException ex) {
            throw new JwtInvalidException("claim " + name + " is not a timestamp: " + ex);
        }
    }

    boolean hasExpiration() {
        return this.payload.has("exp");
    }

    Instant getExpiration() throws JwtInvalidException {
        return this.getInstant("exp");
    }

    boolean hasNotBefore() {
        return this.payload.has("nbf");
    }

    Instant getNotBefore() throws JwtInvalidException {
        return this.getInstant("nbf");
    }

    boolean hasIssuedAt() {
        return this.payload.has("iat");
    }

    Instant getIssuedAt() throws JwtInvalidException {
        return this.getInstant("iat");
    }

    Set<String> customClaimNames() {
        HashSet<String> names = new HashSet<String>();
        for (String name : this.payload.keySet()) {
            if (JwtNames.isRegisteredName(name)) continue;
            names.add(name);
        }
        return Collections.unmodifiableSet(names);
    }

    public String toString() {
        JsonObject header = new JsonObject();
        if (this.typeHeader.isPresent()) {
            header.add("typ", new JsonPrimitive(this.typeHeader.get()));
        }
        return header + "." + this.payload;
    }

    public static final class Builder {
        private Optional<String> typeHeader = Optional.empty();
        private boolean withoutExpiration = false;
        private final JsonObject payload = new JsonObject();

        private Builder() {
        }

        @CanIgnoreReturnValue
        public Builder setTypeHeader(String value) {
            this.typeHeader = Optional.of(value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIssuer(String value) {
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException();
            }
            this.payload.add("iss", new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setSubject(String value) {
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException();
            }
            this.payload.add("sub", new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setAudience(String value) {
            if (this.payload.has("aud") && this.payload.get("aud").isJsonArray()) {
                throw new IllegalArgumentException("setAudience can't be used together with setAudiences or addAudience");
            }
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException("invalid string");
            }
            this.payload.add("aud", new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setAudiences(List<String> values) {
            if (this.payload.has("aud") && !this.payload.get("aud").isJsonArray()) {
                throw new IllegalArgumentException("setAudiences can't be used together with setAudience");
            }
            if (values.isEmpty()) {
                throw new IllegalArgumentException("audiences must not be empty");
            }
            JsonArray audiences = new JsonArray();
            for (String value : values) {
                if (!JsonUtil.isValidString(value)) {
                    throw new IllegalArgumentException("invalid string");
                }
                audiences.add(value);
            }
            this.payload.add("aud", audiences);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addAudience(String value) {
            JsonArray audiences;
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException("invalid string");
            }
            if (this.payload.has("aud")) {
                JsonElement aud = this.payload.get("aud");
                if (!aud.isJsonArray()) {
                    throw new IllegalArgumentException("addAudience can't be used together with setAudience");
                }
                audiences = aud.getAsJsonArray();
            } else {
                audiences = new JsonArray();
            }
            audiences.add(value);
            this.payload.add("aud", audiences);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setJwtId(String value) {
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException();
            }
            this.payload.add("jti", new JsonPrimitive(value));
            return this;
        }

        private void setTimestampClaim(String name, Instant value) {
            long timestamp = value.getEpochSecond();
            if (timestamp > 253402300799L || timestamp < 0L) {
                throw new IllegalArgumentException("timestamp of claim " + name + " is out of range");
            }
            this.payload.add(name, new JsonPrimitive(timestamp));
        }

        @CanIgnoreReturnValue
        public Builder setExpiration(Instant value) {
            this.setTimestampClaim("exp", value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder withoutExpiration() {
            this.withoutExpiration = true;
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setNotBefore(Instant value) {
            this.setTimestampClaim("nbf", value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder setIssuedAt(Instant value) {
            this.setTimestampClaim("iat", value);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addBooleanClaim(String name, boolean value) {
            JwtNames.validate(name);
            this.payload.add(name, new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addNumberClaim(String name, long value) {
            JwtNames.validate(name);
            this.payload.add(name, new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addNumberClaim(String name, double value) {
            JwtNames.validate(name);
            this.payload.add(name, new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addStringClaim(String name, String value) {
            if (!JsonUtil.isValidString(value)) {
                throw new IllegalArgumentException();
            }
            JwtNames.validate(name);
            this.payload.add(name, new JsonPrimitive(value));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addNullClaim(String name) {
            JwtNames.validate(name);
            this.payload.add(name, JsonNull.INSTANCE);
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addJsonObjectClaim(String name, String encodedJsonObject) throws JwtInvalidException {
            JwtNames.validate(name);
            this.payload.add(name, JsonUtil.parseJson(encodedJsonObject));
            return this;
        }

        @CanIgnoreReturnValue
        public Builder addJsonArrayClaim(String name, String encodedJsonArray) throws JwtInvalidException {
            JwtNames.validate(name);
            this.payload.add(name, JsonUtil.parseJsonArray(encodedJsonArray));
            return this;
        }

        public RawJwt build() {
            return new RawJwt(this);
        }
    }
}

