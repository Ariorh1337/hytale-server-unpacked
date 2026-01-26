/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.TinkProtoParametersFormat;
import com.google.crypto.tink.internal.LegacyProtoParameters;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.TinkBugException;
import com.google.errorprone.annotations.Immutable;
import com.google.protobuf.ByteString;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@Immutable
public final class KeyTemplate {
    @Nullable
    private final com.google.crypto.tink.proto.KeyTemplate kt;
    @Nullable
    private final Parameters parameters;

    static OutputPrefixType fromProto(com.google.crypto.tink.proto.OutputPrefixType outputPrefixType) {
        switch (outputPrefixType) {
            case TINK: {
                return OutputPrefixType.TINK;
            }
            case LEGACY: {
                return OutputPrefixType.LEGACY;
            }
            case RAW: {
                return OutputPrefixType.RAW;
            }
            case CRUNCHY: {
                return OutputPrefixType.CRUNCHY;
            }
        }
        throw new IllegalArgumentException("Unknown output prefix type");
    }

    static com.google.crypto.tink.proto.OutputPrefixType toProto(OutputPrefixType outputPrefixType) {
        switch (outputPrefixType.ordinal()) {
            case 0: {
                return com.google.crypto.tink.proto.OutputPrefixType.TINK;
            }
            case 1: {
                return com.google.crypto.tink.proto.OutputPrefixType.LEGACY;
            }
            case 2: {
                return com.google.crypto.tink.proto.OutputPrefixType.RAW;
            }
            case 3: {
                return com.google.crypto.tink.proto.OutputPrefixType.CRUNCHY;
            }
        }
        throw new IllegalArgumentException("Unknown output prefix type");
    }

    @Deprecated
    public static KeyTemplate create(String typeUrl, byte[] value, OutputPrefixType outputPrefixType) {
        return new KeyTemplate(com.google.crypto.tink.proto.KeyTemplate.newBuilder().setTypeUrl(typeUrl).setValue(ByteString.copyFrom(value)).setOutputPrefixType(KeyTemplate.toProto(outputPrefixType)).build());
    }

    public static KeyTemplate createFrom(Parameters p) throws GeneralSecurityException {
        return new KeyTemplate(p);
    }

    private KeyTemplate(com.google.crypto.tink.proto.KeyTemplate kt) {
        this.kt = kt;
        this.parameters = null;
    }

    private KeyTemplate(Parameters parameters) {
        this.kt = null;
        this.parameters = parameters;
    }

    com.google.crypto.tink.proto.KeyTemplate getProto() {
        try {
            return this.getProtoMaybeThrow();
        }
        catch (GeneralSecurityException e) {
            throw new TinkBugException("Parsing parameters failed in getProto(). You probably want to call some Tink register function for " + this.parameters, e);
        }
    }

    com.google.crypto.tink.proto.KeyTemplate getProtoMaybeThrow() throws GeneralSecurityException {
        if (this.kt != null) {
            return this.kt;
        }
        if (this.parameters instanceof LegacyProtoParameters) {
            return ((LegacyProtoParameters)this.parameters).getSerialization().getKeyTemplate();
        }
        ProtoParametersSerialization s = MutableSerializationRegistry.globalInstance().serializeParameters(this.parameters, ProtoParametersSerialization.class);
        return s.getKeyTemplate();
    }

    @Deprecated
    public String getTypeUrl() {
        return this.getProto().getTypeUrl();
    }

    @Deprecated
    public byte[] getValue() {
        return this.getProto().getValue().toByteArray();
    }

    @Deprecated
    public OutputPrefixType getOutputPrefixType() {
        return KeyTemplate.fromProto(this.getProto().getOutputPrefixType());
    }

    public Parameters toParameters() throws GeneralSecurityException {
        if (this.parameters != null) {
            return this.parameters;
        }
        return TinkProtoParametersFormat.parse(this.getProto().toByteArray());
    }

    public static enum OutputPrefixType {
        TINK,
        LEGACY,
        RAW,
        CRUNCHY;

    }
}

