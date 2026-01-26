/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Requirement;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONStringUtils;
import java.io.Serializable;
import java.util.Objects;

@Immutable
public class Algorithm
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final Algorithm NONE = new Algorithm("none", Requirement.REQUIRED);
    private final String name;
    private final Requirement requirement;

    public Algorithm(String name, Requirement req) {
        this.name = Objects.requireNonNull(name);
        this.requirement = req;
    }

    public Algorithm(String name) {
        this(name, null);
    }

    public final String getName() {
        return this.name;
    }

    public final Requirement getRequirement() {
        return this.requirement;
    }

    public final int hashCode() {
        return this.name.hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof Algorithm && this.toString().equals(object.toString());
    }

    public final String toString() {
        return this.name;
    }

    public final String toJSONString() {
        return JSONStringUtils.toJSONString(this.name);
    }

    public static Algorithm parse(String s) {
        if (s == null) {
            return null;
        }
        return new Algorithm(s);
    }
}

