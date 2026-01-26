/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead;

import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.LegacyKmsAeadKey;
import com.google.crypto.tink.aead.LegacyKmsAeadParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KmsAeadKey;
import com.google.crypto.tink.proto.KmsAeadKeyFormat;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

final class LegacyKmsAeadProtoSerialization {
    private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.KmsAeadKey";
    private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.KmsAeadKey");
    private static final ParametersSerializer<LegacyKmsAeadParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(LegacyKmsAeadProtoSerialization::serializeParameters, LegacyKmsAeadParameters.class, ProtoParametersSerialization.class);
    private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(LegacyKmsAeadProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class);
    private static final KeySerializer<LegacyKmsAeadKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(LegacyKmsAeadProtoSerialization::serializeKey, LegacyKmsAeadKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(LegacyKmsAeadProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class);

    private static OutputPrefixType toProtoOutputPrefixType(LegacyKmsAeadParameters.Variant variant) throws GeneralSecurityException {
        if (LegacyKmsAeadParameters.Variant.TINK.equals(variant)) {
            return OutputPrefixType.TINK;
        }
        if (LegacyKmsAeadParameters.Variant.NO_PREFIX.equals(variant)) {
            return OutputPrefixType.RAW;
        }
        throw new GeneralSecurityException("Unable to serialize variant: " + variant);
    }

    private static LegacyKmsAeadParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
        switch (outputPrefixType) {
            case TINK: {
                return LegacyKmsAeadParameters.Variant.TINK;
            }
            case RAW: {
                return LegacyKmsAeadParameters.Variant.NO_PREFIX;
            }
        }
        throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
    }

    private static ProtoParametersSerialization serializeParameters(LegacyKmsAeadParameters parameters) throws GeneralSecurityException {
        return ProtoParametersSerialization.create(KeyTemplate.newBuilder().setTypeUrl(TYPE_URL).setValue(KmsAeadKeyFormat.newBuilder().setKeyUri(parameters.keyUri()).build().toByteString()).setOutputPrefixType(LegacyKmsAeadProtoSerialization.toProtoOutputPrefixType(parameters.variant())).build());
    }

    private static LegacyKmsAeadParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
        KmsAeadKeyFormat format;
        if (!serialization.getKeyTemplate().getTypeUrl().equals(TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to LegacyKmsAeadProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
        }
        try {
            format = KmsAeadKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing KmsAeadKeyFormat failed: ", e);
        }
        return LegacyKmsAeadParameters.create(format.getKeyUri(), LegacyKmsAeadProtoSerialization.toVariant(serialization.getKeyTemplate().getOutputPrefixType()));
    }

    private static ProtoKeySerialization serializeKey(LegacyKmsAeadKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        return ProtoKeySerialization.create(TYPE_URL, KmsAeadKey.newBuilder().setParams(KmsAeadKeyFormat.newBuilder().setKeyUri(key.getParameters().keyUri()).build()).build().toByteString(), KeyData.KeyMaterialType.REMOTE, LegacyKmsAeadProtoSerialization.toProtoOutputPrefixType(key.getParameters().variant()), key.getIdRequirementOrNull());
    }

    private static LegacyKmsAeadKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to LegacyKmsAeadProtoSerialization.parseKey");
        }
        try {
            KmsAeadKey protoKey = KmsAeadKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
                throw new GeneralSecurityException("KmsAeadKey are only accepted with version 0, got " + protoKey);
            }
            LegacyKmsAeadParameters parameters = LegacyKmsAeadParameters.create(protoKey.getParams().getKeyUri(), LegacyKmsAeadProtoSerialization.toVariant(serialization.getOutputPrefixType()));
            return LegacyKmsAeadKey.create(parameters, serialization.getIdRequirementOrNull());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing KmsAeadKey failed: ", e);
        }
    }

    public static void register() throws GeneralSecurityException {
        LegacyKmsAeadProtoSerialization.register(MutableSerializationRegistry.globalInstance());
    }

    public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
        registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
        registry.registerParametersParser(PARAMETERS_PARSER);
        registry.registerKeySerializer(KEY_SERIALIZER);
        registry.registerKeyParser(KEY_PARSER);
    }

    private LegacyKmsAeadProtoSerialization() {
    }
}

