/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import java.util.Objects;

@Immutable
public final class JOSEObjectType
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final JOSEObjectType JOSE = new JOSEObjectType("JOSE");
    public static final JOSEObjectType JOSE_JSON = new JOSEObjectType("JOSE+JSON");
    public static final JOSEObjectType JWT = new JOSEObjectType("JWT");
    private final String type;

    public JOSEObjectType(String type) {
        this.type = Objects.requireNonNull(type);
    }

    public String getType() {
        return this.type;
    }

    public int hashCode() {
        return this.type.toLowerCase().hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof JOSEObjectType && this.type.equalsIgnoreCase(((JOSEObjectType)object).type);
    }

    public String toString() {
        return this.type;
    }

    public String toJSONString() {
        return JSONStringUtils.toJSONString(this.type);
    }
}

