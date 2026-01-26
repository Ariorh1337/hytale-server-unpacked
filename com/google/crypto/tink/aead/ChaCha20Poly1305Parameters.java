/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.aead.AeadParameters;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

public final class ChaCha20Poly1305Parameters
extends AeadParameters {
    private final Variant variant;

    public static ChaCha20Poly1305Parameters create() {
        return new ChaCha20Poly1305Parameters(Variant.NO_PREFIX);
    }

    public static ChaCha20Poly1305Parameters create(Variant variant) {
        return new ChaCha20Poly1305Parameters(variant);
    }

    private ChaCha20Poly1305Parameters(Variant variant) {
        this.variant = variant;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ChaCha20Poly1305Parameters)) {
            return false;
        }
        ChaCha20Poly1305Parameters that = (ChaCha20Poly1305Parameters)o;
        return that.getVariant() == this.getVariant();
    }

    public int hashCode() {
        return Objects.hash(ChaCha20Poly1305Parameters.class, this.variant);
    }

    @Override
    public boolean hasIdRequirement() {
        return this.variant != Variant.NO_PREFIX;
    }

    public String toString() {
        return "ChaCha20Poly1305 Parameters (variant: " + this.variant + ")";
    }

    @Immutable
    public static final class Variant {
        public static final Variant TINK = new Variant("TINK");
        public static final Variant CRUNCHY = new Variant("CRUNCHY");
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

