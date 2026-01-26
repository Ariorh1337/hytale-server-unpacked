/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.aead.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.aead.AesEaxKey;
import com.google.crypto.tink.aead.AesEaxParameters;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.AesEaxKeyFormat;
import com.google.crypto.tink.proto.AesEaxParams;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBytes;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class AesEaxProtoSerialization {
    private static final String TYPE_URL = "type.googleapis.com/google.crypto.tink.AesEaxKey";
    private static final Bytes TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.AesEaxKey");
    private static final ParametersSerializer<AesEaxParameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(AesEaxProtoSerialization::serializeParameters, AesEaxParameters.class, ProtoParametersSerialization.class);
    private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(AesEaxProtoSerialization::parseParameters, TYPE_URL_BYTES, ProtoParametersSerialization.class);
    private static final KeySerializer<AesEaxKey, ProtoKeySerialization> KEY_SERIALIZER = KeySerializer.create(AesEaxProtoSerialization::serializeKey, AesEaxKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> KEY_PARSER = KeyParser.create(AesEaxProtoSerialization::parseKey, TYPE_URL_BYTES, ProtoKeySerialization.class);

    private static OutputPrefixType toProtoOutputPrefixType(AesEaxParameters.Variant variant) throws GeneralSecurityException {
        if (AesEaxParameters.Variant.TINK.equals(variant)) {
            return OutputPrefixType.TINK;
        }
        if (AesEaxParameters.Variant.CRUNCHY.equals(variant)) {
            return OutputPrefixType.CRUNCHY;
        }
        if (AesEaxParameters.Variant.NO_PREFIX.equals(variant)) {
            return OutputPrefixType.RAW;
        }
        throw new GeneralSecurityException("Unable to serialize variant: " + variant);
    }

    private static AesEaxParameters.Variant toVariant(OutputPrefixType outputPrefixType) throws GeneralSecurityException {
        switch (outputPrefixType) {
            case TINK: {
                return AesEaxParameters.Variant.TINK;
            }
            case CRUNCHY: 
            case LEGACY: {
                return AesEaxParameters.Variant.CRUNCHY;
            }
            case RAW: {
                return AesEaxParameters.Variant.NO_PREFIX;
            }
        }
        throw new GeneralSecurityException("Unable to parse OutputPrefixType: " + outputPrefixType.getNumber());
    }

    private static AesEaxParams getProtoParams(AesEaxParameters parameters) throws GeneralSecurityException {
        if (parameters.getTagSizeBytes() != 16) {
            throw new GeneralSecurityException(String.format("Invalid tag size in bytes %d. Currently Tink only supports aes eax keys with tag size equal to 16 bytes.", parameters.getTagSizeBytes()));
        }
        return AesEaxParams.newBuilder().setIvSize(parameters.getIvSizeBytes()).build();
    }

    private static ProtoParametersSerialization serializeParameters(AesEaxParameters parameters) throws GeneralSecurityException {
        return ProtoParametersSerialization.create(KeyTemplate.newBuilder().setTypeUrl(TYPE_URL).setValue(AesEaxKeyFormat.newBuilder().setParams(AesEaxProtoSerialization.getProtoParams(parameters)).setKeySize(parameters.getKeySizeBytes()).build().toByteString()).setOutputPrefixType(AesEaxProtoSerialization.toProtoOutputPrefixType(parameters.getVariant())).build());
    }

    private static ProtoKeySerialization serializeKey(AesEaxKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        return ProtoKeySerialization.create(TYPE_URL, com.google.crypto.tink.proto.AesEaxKey.newBuilder().setParams(AesEaxProtoSerialization.getProtoParams(key.getParameters())).setKeyValue(ByteString.copyFrom(key.getKeyBytes().toByteArray(SecretKeyAccess.requireAccess(access)))).build().toByteString(), KeyData.KeyMaterialType.SYMMETRIC, AesEaxProtoSerialization.toProtoOutputPrefixType(key.getParameters().getVariant()), key.getIdRequirementOrNull());
    }

    private static AesEaxParameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
        AesEaxKeyFormat format;
        if (!serialization.getKeyTemplate().getTypeUrl().equals(TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to AesEaxProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
        }
        try {
            format = AesEaxKeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing AesEaxParameters failed: ", e);
        }
        return AesEaxParameters.builder().setKeySizeBytes(format.getKeySize()).setIvSizeBytes(format.getParams().getIvSize()).setTagSizeBytes(16).setVariant(AesEaxProtoSerialization.toVariant(serialization.getKeyTemplate().getOutputPrefixType())).build();
    }

    private static AesEaxKey parseKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to AesEaxProtoSerialization.parseKey");
        }
        try {
            com.google.crypto.tink.proto.AesEaxKey protoKey = com.google.crypto.tink.proto.AesEaxKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
                throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
            AesEaxParameters parameters = AesEaxParameters.builder().setKeySizeBytes(protoKey.getKeyValue().size()).setIvSizeBytes(protoKey.getParams().getIvSize()).setTagSizeBytes(16).setVariant(AesEaxProtoSerialization.toVariant(serialization.getOutputPrefixType())).build();
            return AesEaxKey.builder().setParameters(parameters).setKeyBytes(SecretBytes.copyFrom(protoKey.getKeyValue().toByteArray(), SecretKeyAccess.requireAccess(access))).setIdRequirement(serialization.getIdRequirementOrNull()).build();
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing AesEaxcKey failed");
        }
    }

    public static void register() throws GeneralSecurityException {
        AesEaxProtoSerialization.register(MutableSerializationRegistry.globalInstance());
    }

    public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
        registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
        registry.registerParametersParser(PARAMETERS_PARSER);
        registry.registerKeySerializer(KEY_SERIALIZER);
        registry.registerKeyParser(KEY_PARSER);
    }

    private AesEaxProtoSerialization() {
    }
}

