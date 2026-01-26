/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.KeyTemplate;
import com.google.crypto.tink.Parameters;
import com.google.crypto.tink.internal.LegacyProtoParameters;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.proto.OutputPrefixType;
import java.security.GeneralSecurityException;

public final class KeyTemplateProtoConverter {
    public static KeyTemplate.OutputPrefixType prefixFromProto(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
        switch (outputPrefixType) {
            case TINK: {
                return KeyTemplate.OutputPrefixType.TINK;
            }
            case LEGACY: {
                return KeyTemplate.OutputPrefixType.LEGACY;
            }
            case RAW: {
                return KeyTemplate.OutputPrefixType.RAW;
            }
            case CRUNCHY: {
                return KeyTemplate.OutputPrefixType.CRUNCHY;
            }
        }
        throw new GeneralSecurityException("Unknown output prefix type");
    }

    public static com.google.crypto.tink.proto.KeyTemplate toProto(KeyTemplate keyTemplate) throws GeneralSecurityException {
        Parameters parameters = keyTemplate.toParameters();
        if (parameters instanceof LegacyProtoParameters) {
            return ((LegacyProtoParameters)parameters).getSerialization().getKeyTemplate();
        }
        ProtoParametersSerialization s = MutableSerializationRegistry.globalInstance().serializeParameters(parameters, ProtoParametersSerialization.class);
        return s.getKeyTemplate();
    }

    public static byte[] toByteArray(KeyTemplate keyTemplate) throws GeneralSecurityException {
        return KeyTemplateProtoConverter.toProto(keyTemplate).toByteArray();
    }

    public static KeyTemplate.OutputPrefixType getOutputPrefixType(KeyTemplate t) throws GeneralSecurityException {
        return KeyTemplateProtoConverter.prefixFromProto(KeyTemplateProtoConverter.toProto(t).getOutputPrefixType());
    }

    private KeyTemplateProtoConverter() {
    }
}

