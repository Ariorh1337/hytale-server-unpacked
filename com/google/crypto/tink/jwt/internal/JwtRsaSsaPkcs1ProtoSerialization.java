/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt.internal;

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
import com.google.crypto.tink.jwt.JwtRsaSsaPkcs1Parameters;
import com.google.crypto.tink.jwt.JwtRsaSsaPkcs1PrivateKey;
import com.google.crypto.tink.jwt.JwtRsaSsaPkcs1PublicKey;
import com.google.crypto.tink.proto.JwtRsaSsaPkcs1Algorithm;
import com.google.crypto.tink.proto.JwtRsaSsaPkcs1KeyFormat;
import com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.util.Bytes;
import com.google.crypto.tink.util.SecretBigInteger;
import com.google.protobuf.ByteString;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import javax.annotation.Nullable;

@AccessesPartialKey
public final class JwtRsaSsaPkcs1ProtoSerialization {
    private static final String PRIVATE_TYPE_URL = "type.googleapis.com/google.crypto.tink.JwtRsaSsaPkcs1PrivateKey";
    private static final Bytes PRIVATE_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.JwtRsaSsaPkcs1PrivateKey");
    private static final String PUBLIC_TYPE_URL = "type.googleapis.com/google.crypto.tink.JwtRsaSsaPkcs1PublicKey";
    private static final Bytes PUBLIC_TYPE_URL_BYTES = Util.toBytesFromPrintableAscii("type.googleapis.com/google.crypto.tink.JwtRsaSsaPkcs1PublicKey");
    private static final ParametersSerializer<JwtRsaSsaPkcs1Parameters, ProtoParametersSerialization> PARAMETERS_SERIALIZER = ParametersSerializer.create(JwtRsaSsaPkcs1ProtoSerialization::serializeParameters, JwtRsaSsaPkcs1Parameters.class, ProtoParametersSerialization.class);
    private static final ParametersParser<ProtoParametersSerialization> PARAMETERS_PARSER = ParametersParser.create(JwtRsaSsaPkcs1ProtoSerialization::parseParameters, PRIVATE_TYPE_URL_BYTES, ProtoParametersSerialization.class);
    private static final KeySerializer<JwtRsaSsaPkcs1PublicKey, ProtoKeySerialization> PUBLIC_KEY_SERIALIZER = KeySerializer.create(JwtRsaSsaPkcs1ProtoSerialization::serializePublicKey, JwtRsaSsaPkcs1PublicKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> PUBLIC_KEY_PARSER = KeyParser.create(JwtRsaSsaPkcs1ProtoSerialization::parsePublicKey, PUBLIC_TYPE_URL_BYTES, ProtoKeySerialization.class);
    private static final KeySerializer<JwtRsaSsaPkcs1PrivateKey, ProtoKeySerialization> PRIVATE_KEY_SERIALIZER = KeySerializer.create(JwtRsaSsaPkcs1ProtoSerialization::serializePrivateKey, JwtRsaSsaPkcs1PrivateKey.class, ProtoKeySerialization.class);
    private static final KeyParser<ProtoKeySerialization> PRIVATE_KEY_PARSER = KeyParser.create(JwtRsaSsaPkcs1ProtoSerialization::parsePrivateKey, PRIVATE_TYPE_URL_BYTES, ProtoKeySerialization.class);
    private static final EnumTypeProtoConverter<JwtRsaSsaPkcs1Algorithm, JwtRsaSsaPkcs1Parameters.Algorithm> ALGORITHM_CONVERTER = EnumTypeProtoConverter.builder().add(JwtRsaSsaPkcs1Algorithm.RS256, JwtRsaSsaPkcs1Parameters.Algorithm.RS256).add(JwtRsaSsaPkcs1Algorithm.RS384, JwtRsaSsaPkcs1Parameters.Algorithm.RS384).add(JwtRsaSsaPkcs1Algorithm.RS512, JwtRsaSsaPkcs1Parameters.Algorithm.RS512).build();

    private static OutputPrefixType toProtoOutputPrefixType(JwtRsaSsaPkcs1Parameters parameters) {
        if (parameters.getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
            return OutputPrefixType.TINK;
        }
        return OutputPrefixType.RAW;
    }

    private static ByteString encodeBigInteger(BigInteger i) {
        byte[] encoded = BigIntegerEncoding.toBigEndianBytes(i);
        return ByteString.copyFrom(encoded);
    }

    private static JwtRsaSsaPkcs1KeyFormat getProtoKeyFormat(JwtRsaSsaPkcs1Parameters parameters) throws GeneralSecurityException {
        if (!parameters.getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED) && !parameters.getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.BASE64_ENCODED_KEY_ID)) {
            throw new GeneralSecurityException("Unable to serialize Parameters object with KidStrategy " + parameters.getKidStrategy());
        }
        return JwtRsaSsaPkcs1KeyFormat.newBuilder().setVersion(0).setAlgorithm(ALGORITHM_CONVERTER.toProtoEnum(parameters.getAlgorithm())).setModulusSizeInBits(parameters.getModulusSizeBits()).setPublicExponent(JwtRsaSsaPkcs1ProtoSerialization.encodeBigInteger(parameters.getPublicExponent())).build();
    }

    private static ProtoParametersSerialization serializeParameters(JwtRsaSsaPkcs1Parameters parameters) throws GeneralSecurityException {
        OutputPrefixType outputPrefixType = JwtRsaSsaPkcs1ProtoSerialization.toProtoOutputPrefixType(parameters);
        return ProtoParametersSerialization.create(KeyTemplate.newBuilder().setTypeUrl(PRIVATE_TYPE_URL).setValue(JwtRsaSsaPkcs1ProtoSerialization.getProtoKeyFormat(parameters).toByteString()).setOutputPrefixType(outputPrefixType).build());
    }

    private static com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey getProtoPublicKey(JwtRsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
        JwtRsaSsaPkcs1PublicKey.Builder builder = com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey.newBuilder().setVersion(0).setAlgorithm(ALGORITHM_CONVERTER.toProtoEnum(key.getParameters().getAlgorithm())).setN(JwtRsaSsaPkcs1ProtoSerialization.encodeBigInteger(key.getModulus())).setE(JwtRsaSsaPkcs1ProtoSerialization.encodeBigInteger(key.getParameters().getPublicExponent()));
        if (key.getParameters().getKidStrategy().equals(JwtRsaSsaPkcs1Parameters.KidStrategy.CUSTOM)) {
            builder.setCustomKid(JwtRsaSsaPkcs1PublicKey.CustomKid.newBuilder().setValue(key.getKid().get()).build());
        }
        return builder.build();
    }

    private static ProtoKeySerialization serializePublicKey(JwtRsaSsaPkcs1PublicKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        return ProtoKeySerialization.create(PUBLIC_TYPE_URL, JwtRsaSsaPkcs1ProtoSerialization.getProtoPublicKey(key).toByteString(), KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC, JwtRsaSsaPkcs1ProtoSerialization.toProtoOutputPrefixType(key.getParameters()), key.getIdRequirementOrNull());
    }

    private static ByteString encodeSecretBigInteger(SecretBigInteger i, SecretKeyAccess access) {
        return JwtRsaSsaPkcs1ProtoSerialization.encodeBigInteger(i.getBigInteger(access));
    }

    private static ProtoKeySerialization serializePrivateKey(JwtRsaSsaPkcs1PrivateKey key, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        SecretKeyAccess a = SecretKeyAccess.requireAccess(access);
        com.google.crypto.tink.proto.JwtRsaSsaPkcs1PrivateKey protoPrivateKey = com.google.crypto.tink.proto.JwtRsaSsaPkcs1PrivateKey.newBuilder().setVersion(0).setPublicKey(JwtRsaSsaPkcs1ProtoSerialization.getProtoPublicKey(key.getPublicKey())).setD(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrivateExponent(), a)).setP(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeP(), a)).setQ(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeQ(), a)).setDp(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeExponentP(), a)).setDq(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getPrimeExponentQ(), a)).setCrt(JwtRsaSsaPkcs1ProtoSerialization.encodeSecretBigInteger(key.getCrtCoefficient(), a)).build();
        return ProtoKeySerialization.create(PRIVATE_TYPE_URL, protoPrivateKey.toByteString(), KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE, JwtRsaSsaPkcs1ProtoSerialization.toProtoOutputPrefixType(key.getParameters()), key.getIdRequirementOrNull());
    }

    private static BigInteger decodeBigInteger(ByteString data) {
        return BigIntegerEncoding.fromUnsignedBigEndianBytes(data.toByteArray());
    }

    private static void validateVersion(int version) throws GeneralSecurityException {
        if (version != 0) {
            throw new GeneralSecurityException("Parsing failed: unknown version " + version);
        }
    }

    private static JwtRsaSsaPkcs1Parameters parseParameters(ProtoParametersSerialization serialization) throws GeneralSecurityException {
        JwtRsaSsaPkcs1KeyFormat format;
        if (!serialization.getKeyTemplate().getTypeUrl().equals(PRIVATE_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to JwtRsaSsaPkcs1ProtoSerialization.parseParameters: " + serialization.getKeyTemplate().getTypeUrl());
        }
        try {
            format = JwtRsaSsaPkcs1KeyFormat.parseFrom(serialization.getKeyTemplate().getValue(), ExtensionRegistryLite.getEmptyRegistry());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing JwtRsaSsaPkcs1Parameters failed: ", e);
        }
        JwtRsaSsaPkcs1ProtoSerialization.validateVersion(format.getVersion());
        JwtRsaSsaPkcs1Parameters.KidStrategy kidStrategy = null;
        if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.TINK)) {
            kidStrategy = JwtRsaSsaPkcs1Parameters.KidStrategy.BASE64_ENCODED_KEY_ID;
        }
        if (serialization.getKeyTemplate().getOutputPrefixType().equals(OutputPrefixType.RAW)) {
            kidStrategy = JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED;
        }
        if (kidStrategy == null) {
            throw new GeneralSecurityException("Invalid OutputPrefixType for JwtHmacKeyFormat");
        }
        return JwtRsaSsaPkcs1Parameters.builder().setKidStrategy(kidStrategy).setAlgorithm(ALGORITHM_CONVERTER.fromProtoEnum(format.getAlgorithm())).setPublicExponent(JwtRsaSsaPkcs1ProtoSerialization.decodeBigInteger(format.getPublicExponent())).setModulusSizeBits(format.getModulusSizeInBits()).build();
    }

    private static JwtRsaSsaPkcs1PublicKey getPublicKeyFromProto(com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey protoKey, OutputPrefixType outputPrefixType, @Nullable Integer idRequirement) throws GeneralSecurityException {
        JwtRsaSsaPkcs1ProtoSerialization.validateVersion(protoKey.getVersion());
        JwtRsaSsaPkcs1Parameters.Builder parametersBuilder = JwtRsaSsaPkcs1Parameters.builder();
        JwtRsaSsaPkcs1PublicKey.Builder keyBuilder = JwtRsaSsaPkcs1PublicKey.builder();
        if (outputPrefixType.equals(OutputPrefixType.TINK)) {
            if (protoKey.hasCustomKid()) {
                throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK should not have a custom kid");
            }
            if (idRequirement == null) {
                throw new GeneralSecurityException("Keys serialized with OutputPrefixType TINK need an ID Requirement");
            }
            parametersBuilder.setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.BASE64_ENCODED_KEY_ID);
            keyBuilder.setIdRequirement(idRequirement);
        } else if (outputPrefixType.equals(OutputPrefixType.RAW)) {
            if (protoKey.hasCustomKid()) {
                parametersBuilder.setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.CUSTOM);
                keyBuilder.setCustomKid(protoKey.getCustomKid().getValue());
            } else {
                parametersBuilder.setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED);
            }
        }
        BigInteger modulus = JwtRsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoKey.getN());
        int modulusSizeInBits = modulus.bitLength();
        parametersBuilder.setAlgorithm(ALGORITHM_CONVERTER.fromProtoEnum(protoKey.getAlgorithm())).setPublicExponent(JwtRsaSsaPkcs1ProtoSerialization.decodeBigInteger(protoKey.getE())).setModulusSizeBits(modulusSizeInBits);
        keyBuilder.setModulus(modulus).setParameters(parametersBuilder.build());
        return keyBuilder.build();
    }

    private static JwtRsaSsaPkcs1PublicKey parsePublicKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(PUBLIC_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to JwtRsaSsaPkcs1ProtoSerialization.parsePublicKey: " + serialization.getTypeUrl());
        }
        try {
            com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey protoKey = com.google.crypto.tink.proto.JwtRsaSsaPkcs1PublicKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            return JwtRsaSsaPkcs1ProtoSerialization.getPublicKeyFromProto(protoKey, serialization.getOutputPrefixType(), serialization.getIdRequirementOrNull());
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing JwtRsaSsaPkcs1PublicKey failed");
        }
    }

    private static SecretBigInteger decodeSecretBigInteger(ByteString data, SecretKeyAccess access) {
        return SecretBigInteger.fromBigInteger(BigIntegerEncoding.fromUnsignedBigEndianBytes(data.toByteArray()), access);
    }

    private static JwtRsaSsaPkcs1PrivateKey parsePrivateKey(ProtoKeySerialization serialization, @Nullable SecretKeyAccess access) throws GeneralSecurityException {
        if (!serialization.getTypeUrl().equals(PRIVATE_TYPE_URL)) {
            throw new IllegalArgumentException("Wrong type URL in call to JwtRsaSsaPkcs1ProtoSerialization.parsePrivateKey: " + serialization.getTypeUrl());
        }
        try {
            com.google.crypto.tink.proto.JwtRsaSsaPkcs1PrivateKey protoKey = com.google.crypto.tink.proto.JwtRsaSsaPkcs1PrivateKey.parseFrom(serialization.getValue(), ExtensionRegistryLite.getEmptyRegistry());
            JwtRsaSsaPkcs1ProtoSerialization.validateVersion(protoKey.getVersion());
            JwtRsaSsaPkcs1PublicKey publicKey = JwtRsaSsaPkcs1ProtoSerialization.getPublicKeyFromProto(protoKey.getPublicKey(), serialization.getOutputPrefixType(), serialization.getIdRequirementOrNull());
            SecretKeyAccess a = SecretKeyAccess.requireAccess(access);
            return JwtRsaSsaPkcs1PrivateKey.builder().setPublicKey(publicKey).setPrimes(JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getP(), a), JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getQ(), a)).setPrivateExponent(JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getD(), a)).setPrimeExponents(JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getDp(), a), JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getDq(), a)).setCrtCoefficient(JwtRsaSsaPkcs1ProtoSerialization.decodeSecretBigInteger(protoKey.getCrt(), a)).build();
        }
        catch (InvalidProtocolBufferException e) {
            throw new GeneralSecurityException("Parsing JwtRsaSsaPkcs1PrivateKey failed");
        }
    }

    public static void register() throws GeneralSecurityException {
        JwtRsaSsaPkcs1ProtoSerialization.register(MutableSerializationRegistry.globalInstance());
    }

    public static void register(MutableSerializationRegistry registry) throws GeneralSecurityException {
        registry.registerParametersSerializer(PARAMETERS_SERIALIZER);
        registry.registerParametersParser(PARAMETERS_PARSER);
        registry.registerKeySerializer(PUBLIC_KEY_SERIALIZER);
        registry.registerKeyParser(PUBLIC_KEY_PARSER);
        registry.registerKeySerializer(PRIVATE_KEY_SERIALIZER);
        registry.registerKeyParser(PRIVATE_KEY_PARSER);
    }

    private JwtRsaSsaPkcs1ProtoSerialization() {
    }
}

