/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import java.io.Serializable;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Objects;

public final class KeyUse
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final KeyUse SIGNATURE = new KeyUse("sig");
    public static final KeyUse ENCRYPTION = new KeyUse("enc");
    private final String identifier;

    public KeyUse(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("The key use identifier must not be null");
        }
        this.identifier = identifier;
    }

    public String identifier() {
        return this.identifier;
    }

    public String getValue() {
        return this.identifier();
    }

    public String toString() {
        return this.identifier();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyUse)) {
            return false;
        }
        KeyUse keyUse = (KeyUse)o;
        return Objects.equals(this.identifier, keyUse.identifier);
    }

    public int hashCode() {
        return Objects.hash(this.identifier);
    }

    public static KeyUse parse(String s) throws ParseException {
        if (s == null) {
            return null;
        }
        if (s.equals(SIGNATURE.identifier())) {
            return SIGNATURE;
        }
        if (s.equals(ENCRYPTION.identifier())) {
            return ENCRYPTION;
        }
        if (s.trim().isEmpty()) {
            throw new ParseException("JWK use value must not be empty or blank", 0);
        }
        return new KeyUse(s);
    }

    public static KeyUse from(X509Certificate cert) {
        if (cert.getKeyUsage() == null) {
            return null;
        }
        HashSet<KeyUse> foundUses = new HashSet<KeyUse>();
        if (cert.getKeyUsage()[0] || cert.getKeyUsage()[1]) {
            foundUses.add(SIGNATURE);
        }
        if (cert.getKeyUsage()[0] && cert.getKeyUsage()[2]) {
            foundUses.add(ENCRYPTION);
        }
        if (cert.getKeyUsage()[0] && cert.getKeyUsage()[4]) {
            foundUses.add(ENCRYPTION);
        }
        if (cert.getKeyUsage()[2] || cert.getKeyUsage()[3] || cert.getKeyUsage()[4]) {
            foundUses.add(ENCRYPTION);
        }
        if (cert.getKeyUsage()[5] || cert.getKeyUsage()[6]) {
            foundUses.add(SIGNATURE);
        }
        if (foundUses.size() == 1) {
            return (KeyUse)foundUses.iterator().next();
        }
        return null;
    }
}

