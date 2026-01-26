/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.signature.internal;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.SecretKeyAccess;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.EnumTypeProtoConverter;
import com.google.crypto.tink.internal.KeyParser;
import com.google.crypto.tink.internal.KeySerializer;
import com.google.crypto.tink.internal.MutableSerializationRegistry;
import com.google.crypto.tink.internal.ParametersParser;
import com.google.crypto.tink.internal.ParametersSerializer;
import com.google.crypto.tink.internal.ProtoKeySerialization;
import com.google.crypto.tink.internal.ProtoParametersSerialization;
import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.RsaSsaPkcs1KeyFormat;
import com.google.crypto.tink.proto.RsaSsaPkcs1Params;
import com.google.crypto.tink.signature.RsaSsaPkcs1Parameters;
import com.google.crypto.tink.signature.RsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.signature.RsaSsaPkcs1PublicKey;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class RsaSsaPkcs1ProtoSerialization {
    private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.RsaSsaPkcs1PrivateKey";
    private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.RsaSsaPkcs1PrivateKey");
    private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.RsaSsaPkcs1PublicKey";
    private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.RsaSsaPkcs1PublicKey");
    private static final ParametersSerializer<RsaSsaPkcs1Parameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(RsaSsaPkcs1ProtoSerialization::serializeParameters, RsaSsaPkcs1Parameters.class, ProtoParametersSerialization.class);
    private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(RsaSsaPkcs1ProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class);
    private static final KeySerializer<RsaSsaPkcs1PublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(RsaSsaPkcs1ProtoSerialization::serializePublicKey, RsaSsaPkcs1PublicKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(RsaSsaPkcs1ProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class);
    private static final KeySerializer<RsaSsaPkcs1PrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(RsaSsaPkcs1ProtoSerialization::serializePrivateKey, RsaSsaPkcs1PrivateKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(RsaSsaPkcs1ProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class);
    private static final EnumTypeProtoConverter<OutputPrefixType, RsaSsaPkcs1Parameters.Variant> VARIANT_CONVERTER = EnumTypeProtoConverter.builder().add(OutputPrefixType.RAW, RsaSsaPkcs1Parameters.Variant.NO_PREFIX).add(OutputPrefixType.TINK, RsaSsaPkcs1Parameters.Variant.TINK).add(OutputPrefixType.CRUNCHY, RsaSsaPkcs1Parameters.Variant.CRUNCHY).add(OutputPrefixType.LEGACY, RsaSsaPkcs1Parameters.Variant.LEGACY).build();
    private static final EnumTypeProtoConverter<HashType, RsaSsaPkcs1Parameters.HashType> HASH_TYPE_CONVERTER = EnumTypeProtoConverter.builder().add(HashType.SHA256, RsaSsaPkcs1Parameters.HashType.SHA256).add(HashType.SHA384, RsaSsaPkcs1Parameters.HashType.SHA384).add(HashType.SHA512, RsaSsaPkcs1Parameters.HashType.SHA512).build();

    private static RsaSsaPkcs1Params getProtoParams(RsaSsaPkcs1Parameters parameters) throws GeneralSecurityException {
        return RsaSsaPkcs1Params.newBuilder().setHashType(HASH_TYPE_CONVERTER.toProtoEnum(parameters.getHashType())).build();
    }

    private static ByteString encodeBigInteger(BigInteger i) {
        byte[] encoded = BigIntegerEncoding.toBigEndianBytes(i);
        return ByteString.copyFrom(encoded);
    }

    private static com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey getProtoPublicKey(RsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
        return com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey.newBuilder().setParams(RsaSsaPkcs1ProtoSerialization.getProtoParams(key.getParameters())).setN(RsaSsaPkcs1ProtoSerialization.encodeBigInteger(key.getModulus())).setE(RsaSsaPkcs1ProtoSerialization.encodeBigInteger(key.getParameters().getPublicExponent())).build();
    }

    private static ProtoParametersSerialization serializeParameters(RsaSsaPkcs1Parameters parameters) throws GeneralSecurityException {
        return ProtoParametersSerialization.create(KeyTemplate.newBuilder().setTypeUrl(PRIVATE_TYPE_URL).setValue(RsaSsaPkcs1KeyFormat.newBuilder().setParams(RsaSsaPkcs1ProtoSerialization.getProtoParams(parameters)).setModulusSizeInBits(parameters.getModulusSizeBits()).setPublicExponent(RsaSsaPkcs1ProtoSerialization.encodeBigInteger(parameters.getPublicExponent())).build().toByteString()).setOutputPrefixType(VARIANT_CONVERTER.toProtoEnum(parameters.getVariant())).build());
    }

    private static ProtoKeySerialization serializePublicKey(RsaSsaPkcs1PublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        return ProtoKeySerialization.create(PUBLIC_TYPE_URL, RsaSsaPkcs1ProtoSerialization.getProtoPublicKey(key).toByteString(), KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC, VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()), key.getIdRequirementOrNull());
    }

    private static ByteString encodeSecretBigInteger(SecretBigInteger i, SecretKeyAccess access) {
        return RsaSsaPkcs1ProtoSerialization.encodeBigInteger(i.getBigInteger(access));
    }

    private static ProtoKeySerialization serializePrivateKey(RsaSsaPkcs1PrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        SecretKeyAccess a = SecretKeyAccess.requireAccess(access);
        com.google.crypto.tink.proto.RsaSsaPkcs1PrivateKey protoPrivateKey = com.google.crypto.tink.proto.RsaSsaPkcs1PrivateKey.newBuilder().setVersion(0).setPublicKey(RsaSsaPkcs1ProtoSerialization.getProtoPublicKey(key.getPublicKey())).setD(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrivateExponent(), a)).setP(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeP(), a)).setQ(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeQ(), a)).setDp(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeExponentP(), a)).setDq(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeExponentQ(), a)).setCrt(RsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getCrtCoefficient(), a)).build();
        return ProtoKeySerialization.create(PRIVATE_TYPE_URL, protoPrivateKey.toByteString(), KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE, VARIANT_CONVERTER.toProtoEnum(key.getParameters().getVariant()), key.getIdRequirementOrNull());
    }

    private static BigInteger decodeBigInteger(ByteString data) {
        return BigIntegerEncoding.fromUnsignedBigEndianBytes(data.toByteArray());
    }

    private static RsaSsaPkcs1Parameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
        RsaSsaPkcs1KeyFormat format;
        if (!serialization.getKeyTemplate().getTypeUrl().equals(PRIVATE_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to RsaSsaPkcs1ProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
        }
        try {
            format = RsaSsaPkcs1KeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing RsaSsaPkcs1Parameters failed: ", e);
        }
        return RsaSsaPkcs1Parameters.builder().setHashType(HASH_TYPE_CONVERTER.fromProtoEnum(format.getParams().getHashType())).setPublicExponent(RsaSsaPkcs1ProtoSerialization.decodeBigInteger(format.getPublicExponent())).setModulusSizeBits(format.getModulusSizeInBits()).setVariant(VARIANT_CONVERTER.fromProtoEnum(serialization.getKeyTemplate().getOutputPrefixType())).build();
    }

    private static RsaSsaPkcs1PublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(PUBLIC_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to RsaSsaPkcs1ProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
        }
        try {
            com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey protoKey = com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
                throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
            BigInteger modulus = RsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoKey.getN());
            int modulusSizeInBits = modulus.bitLength();
            RsaSsaPkcs1Parameters parameters = RsaSsaPkcs1Parameters.builder().setHashType(HASH_TYPE_CONVERTER.fromProtoEnum(protoKey.getParams().getHashType())).setPublicExponent(RsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoKey.getE())).setModulusSizeBits(modulusSizeInBits).setVariant(VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType())).build();
            return RsaSsaPkcs1PublicKey.builder().setParameters(parameters).setModulus(modulus).setIdRequirement(serialization.getIdRequirementOrNull()).build();
        }
        catch (InvalidProtocolBufferException | IllegalArgumentException e) {
            throw new GeneralSecurityException("Parsing RsaSsaPkcs1PublicKey failed");
        }
    }

    private static SecretBigInteger decodeSecretBigInteger(ByteString data, SecretKeyAccess access) {
        return SecretBigInteger.fromBigInteger(BigIntegerEncoding.fromUnsignedBigEndianBytes(data.toByteArray()), access);
    }

    private static RsaSsaPkcs1PrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(PRIVATE_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to RsaSsaPkcs1ProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
        }
        try {
            com.google.crypto.tink.proto.RsaSsaPkcs1PrivateKey protoKey = com.google.crypto.tink.proto.RsaSsaPkcs1PrivateKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            if (protoKey.getVersion() != 0) {
                throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
            com.google.crypto.tink.proto.RsaSsaPkcs1PublicKey protoPublicKey = protoKey.getPublicKey();
            if (protoPublicKey.getVersion() != 0) {
                throw new GeneralSecurityException("Only version 0 keys are accepted");
            }
            BigInteger modulus = RsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoPublicKey.getN());
            int modulusSizeInBits = modulus.bitLength();
            BigInteger publicExponent = RsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoPublicKey.getE());
            RsaSsaPkcs1Parameters parameters = RsaSsaPkcs1Parameters.builder().setHashType(HASH_TYPE_CONVERTER.fromProtoEnum(protoPublicKey.getParams().getHashType())).setPublicExponent(publicExponent).setModulusSizeBits(modulusSizeInBits).setVariant(VARIANT_CONVERTER.fromProtoEnum(serialization.getOutputPrefixType())).build();
            RsaSsaPkcs1PublicKey publicKey = RsaSsaPkcs1PublicKey.builder().setParameters(parameters).setModulus(modulus).setIdRequirement(serialization.getIdRequirementOrNull()).build();
            SecretKeyAccess a = SecretKeyAccess.requireAccess(access);
            return RsaSsaPkcs1PrivateKey.builder().setPublicKey(publicKey).setPrimes(RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getP(), a), RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getQ(), a)).setPrivateExponent(RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getD(), a)).setPrimeExponents(RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getDp(), a), RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getDq(), a)).setCrtCoefficient(RsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getCrt(), a)).build();
        }
        catch (InvalidProtocolBufferException | IllegalArgumentException e) {
            throw new GeneralSecurityException("Parsing RsaSsaPkcs1PrivateKey failed");
        }
    }

    public static void register() throws GeneralSecurityException {
        RsaSsaPkcs1ProtoSerialization.register(MutableSerializationRegistry.globalInstance());
    }

    public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
        registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
        registry.registerParametersParser(PARAMETERS_PARSER);
        registry.registerKeySerializer(PUBLIC_KEY_SERIALIZER);
        registry.registerKeyParser(PUBLIC_KEY_PARSER);
        registry.registerKeySerializer(PRIVATE_KEY_SERIALIZER);
        registry.registerKeyParser(PRIVATE_KEY_PARSER);
    }

    private RsaSsaPkcs1ProtoSerialization() {
    }
}

