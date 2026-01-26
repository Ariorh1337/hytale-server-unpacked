/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.errorprone.annotations.Immutable;
import java.util.Objects;

@Immutable
public final class LegacyProtoParameters
extends Parameters {
    private final ProtoParametersSerialization serialization;

    public LegacyProtoParameters(ProtoParametersSerialization serialization) {
        this.serialization = serialization;
    }

    @Override
    public boolean hasIdRequirement() {
        return this.serialization.getKeyTemplate().getOutputPrefixType() != OutputPrefixType.RAW;
    }

    public ProtoParametersSerialization getSerialization() {
        return this.serialization;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LegacyProtoParameters)) {
            return false;
        }
        ProtoParametersSerialization other = ((LegacyProtoParameters)o).serialization;
        return this.serialization.getKeyTemplate().getOutputPrefixType().equals(other.getKeyTemplate().getOutputPrefixType()) && this.serialization.getKeyTemplate().getTypeUrl().equals(other.getKeyTemplate().getTypeUrl()) && this.serialization.getKeyTemplate().getValue().equals(other.getKeyTemplate().getValue());
    }

    public int hashCode() {
        return Objects.hash(this.serialization.getKeyTemplate(), this.serialization.getObjectIdentifier());
    }

    private static String outputPrefixToString(OutputPrefixType outputPrefixType) {
        switch (outputPrefixType) {
            case TINK: {
                return "TINK";
            }
            case LEGACY: {
                return "LEGACY";
            }
            case RAW: {
                return "RAW";
            }
            case CRUNCHY: {
                return "CRUNCHY";
            }
        }
        return "UNKNOWN";
    }

    public String toString() {
        return String.format("(typeUrl=%s, outputPrefixType=%s)", this.serialization.getKeyTemplate().getTypeUrl(), LegacyProtoParameters.outputPrefixToString(this.serialization.getKeyTemplate().getOutputPrefixType()));
    }
}

