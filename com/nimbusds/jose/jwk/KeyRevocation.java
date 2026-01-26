/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.util.DateUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Immutable
public final class KeyRevocation
implements Serializable {
    private final Date revokedAt;
    private final Reason reason;

    public KeyRevocation(Date revokedAt, Reason reason) {
        this.revokedAt = Objects.requireNonNull(revokedAt);
        this.reason = reason;
    }

    public Date getRevocationTime() {
        return this.revokedAt;
    }

    public Reason getReason() {
        return this.reason;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyRevocation)) {
            return false;
        }
        KeyRevocation that = (KeyRevocation)o;
        return Objects.equals(this.revokedAt, that.revokedAt) && Objects.equals(this.getReason(), that.getReason());
    }

    public int hashCode() {
        return Objects.hash(this.revokedAt, this.getReason());
    }

    public Map<String, Object> toJSONObject() {
        Map<String, Object> o = JSONObjectUtils.newJSONObject();
        o.put("revoked_at", DateUtils.toSecondsSinceEpoch(this.getRevocationTime()));
        if (this.getReason() != null) {
            o.put("reason", this.getReason().getValue());
        }
        return o;
    }

    public static KeyRevocation parse(Map<String, Object> jsonObject) throws ParseException {
        Date revokedAt = DateUtils.fromSecondsSinceEpoch(JSONObjectUtils.getLong(jsonObject, "revoked_at"));
        Reason reason = null;
        if (jsonObject.get("reason") != null) {
            reason = Reason.parse(JSONObjectUtils.getString(jsonObject, "reason"));
        }
        return new KeyRevocation(revokedAt, reason);
    }

    public static class Reason {
        public static final Reason UNSPECIFIED = new Reason("unspecified");
        public static final Reason COMPROMISED = new Reason("compromised");
        public static final Reason SUPERSEDED = new Reason("superseded");
        private final String value;

        public Reason(String value) {
            this.value = Objects.requireNonNull(value);
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return this.getValue();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Reason)) {
                return false;
            }
            Reason reason = (Reason)o;
            return Objects.equals(this.getValue(), reason.getValue());
        }

        public int hashCode() {
            return Objects.hashCode(this.getValue());
        }

        public static Reason parse(String s) {
            if (UNSPECIFIED.getValue().equals(s)) {
                return UNSPECIFIED;
            }
            if (COMPROMISED.getValue().equals(s)) {
                return COMPROMISED;
            }
            if (SUPERSEDED.getValue().equals(s)) {
                return SUPERSEDED;
            }
            return new Reason(s);
        }
    }
}

