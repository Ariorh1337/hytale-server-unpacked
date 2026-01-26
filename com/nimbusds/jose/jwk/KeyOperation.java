/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose.jwk;

import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public enum KeyOperation {
    SIGN("sign"),
    VERIFY("verify"),
    ENCRYPT("encrypt"),
    DECRYPT("decrypt"),
    WRAP_KEY("wrapKey"),
    UNWRAP_KEY("unwrapKey"),
    DERIVE_KEY("deriveKey"),
    DERIVE_BITS("deriveBits");

    private final String identifier;

    private KeyOperation(String identifier) {
        if (identifier == null) {
            throw new IllegalArgumentException("The key operation identifier must not be null");
        }
        this.identifier = identifier;
    }

    public String identifier() {
        return this.identifier;
    }

    public String toString() {
        return this.identifier();
    }

    public static Set<KeyOperation> parse(List<String> sl) throws ParseException {
        if (sl == null) {
            return null;
        }
        LinkedHashSet<KeyOperation> keyOps = new LinkedHashSet<KeyOperation>();
        for (String s : sl) {
            if (s == null) continue;
            KeyOperation parsedOp = null;
            for (KeyOperation op : KeyOperation.values()) {
                if (!s.equals(op.identifier())) continue;
                parsedOp = op;
                break;
            }
            if (parsedOp != null) {
                keyOps.add(parsedOp);
                continue;
            }
            throw new ParseException("Invalid JWK operation: " + s, 0);
        }
        return keyOps;
    }
}

