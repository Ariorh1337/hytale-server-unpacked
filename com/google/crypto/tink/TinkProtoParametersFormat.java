/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.LegacyProtoParameters;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.protobuf.ExtensionRegistryLite;
import java.io.IOException;
import java.security.GeneralSecurityException;

public final class TinkProtoParametersFormat {
    public static byte[] serialize(Parameters parameters) throws GeneralSecurityException {
        if (parameters instanceof LegacyProtoParameters) {
            return ((LegacyProtoParameters)parameters).getSerialization().getKeyTemplate().toByteArray();
        }
        ProtoParametersSerialization s = MutableSerializationRegistry.globalInstance().serializeParameters(parameters, ProtoParametersSerialization.class);
        return s.getKeyTemplate().toByteArray();
    }

    public static Parameters parse(byte[] serializedParameters) throws GeneralSecurityException {
        KeyTemplate t;
        try {
            t = KeyTemplate.parseFrom(serializedParameters, ExtensionRegistryLite.getEmptyRegistry());
        }
        catch (IOException e) {
            throw new GeneralSecurityException("Failed to parse proto", e);
        }
        return MutableSerializationRegistry.globalInstance().parseParametersWithLegacyFallback(ProtoParametersSerialization.checkedCreate(t));
    }

    private TinkProtoParametersFormat() {
    }
}

