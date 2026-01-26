/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature;

import com.google.crypto.tink.signature.SignatureParameters;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class Ed25519Parameters
extends SignatureParameters {
    private final Variant variant;

    public static Ed25519Parameters create() {
        return new Ed25519Parameters(Variant.NO_PREFIX);
    }

    public static Ed25519Parameters create(Variant variant) {
        return new Ed25519Parameters(variant);
    }

    private Ed25519Parameters(Variant variant) {
        this.variant = variant;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public boolean equals(Object o) {
        if (!(o instanceof Ed25519Parameters)) {
            return false;
        }
        Ed25519Parameters that = (Ed25519Parameters)o;
        return that.getVariant() == this.getVariant();
    }

    public int hashCode() {
        return Objects.hash(Ed25519Parameters.class, this.variant);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "Ed25519 Parameters (variant: " + this.variant + ")";
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant CRUNCHY = new Variant("CRUNCHY");
        public static final Variant LEGACY = new Variant("LEGACY");
        public static final Variant NO_PREFIX = new Variant("NO_PREFIX");
        private final String name;

        private Variant(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }
    }
}

