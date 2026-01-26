/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.AccessesPartialKey;
import com.google.crypto.tink.Key;
import com.google.crypto.tink.KeyStatus;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.JsonParser;
import com.google.crypto.tink.jwt.JwtEcdsaParameters;
import com.google.crypto.tink.jwt.JwtEcdsaPublicKey;
import com.google.crypto.tink.jwt.JwtRsaSsaPkcs1Parameters;
import com.google.crypto.tink.jwt.JwtRsaSsaPkcs1PublicKey;
import com.google.crypto.tink.jwt.JwtRsaSsaPssParameters;
import com.google.crypto.tink.jwt.JwtRsaSsaPssPublicKey;
import com.google.crypto.tink.subtle.Base64;
import com.google.crypto.tink.tinkkey.KeyAccess;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.spec.ECPoint;
import java.util.Optional;

public final class JwkSetConverter {
    public static String fromPublicKeysetHandle(KeysetHandle handle) throws IOException, GeneralSecurityException {
        handle = KeysetHandle.newBuilder(handle).build();
        JsonArray keys = new JsonArray();
        for (int i = 0; i < handle.size(); ++i) {
            KeysetHandle.Entry entry = handle.getAt(i);
            if (entry.getStatus() != KeyStatus.ENABLED) continue;
            Key key = entry.getKey();
            if (key instanceof JwtEcdsaPublicKey) {
                keys.add(JwkSetConverter.convertJwtEcdsaKey((JwtEcdsaPublicKey)key));
                continue;
            }
            if (key instanceof JwtRsaSsaPkcs1PublicKey) {
                keys.add(JwkSetConverter.convertJwtRsaSsaPkcs1Key((JwtRsaSsaPkcs1PublicKey)key));
                continue;
            }
            if (key instanceof JwtRsaSsaPssPublicKey) {
                keys.add(JwkSetConverter.convertJwtRsaSsaPssKey((JwtRsaSsaPssPublicKey)key));
                continue;
            }
            throw new GeneralSecurityException("unsupported key with parameters " + key.getParameters());
        }
        JsonObject jwkSet = new JsonObject();
        jwkSet.add("keys", keys);
        return jwkSet.toString();
    }

    public static KeysetHandle toPublicKeysetHandle(String jwkSet) throws IOException, GeneralSecurityException {
        JsonObject jsonKeyset;
        try {
            jsonKeyset = JsonParser.parse(jwkSet).getAsJsonObject();
        }
        catch (IOException | IllegalStateException ex) {
            throw new GeneralSecurityException("JWK set is invalid JSON", ex);
        }
        KeysetHandle.Builder builder = KeysetHandle.newBuilder();
        JsonArray jsonKeys = jsonKeyset.get("keys").getAsJsonArray();
        block12: for (JsonElement element : jsonKeys) {
            String algPrefix;
            JsonObject jsonKey = element.getAsJsonObject();
            switch (algPrefix = JwkSetConverter.getStringItem(jsonKey, "alg").substring(0, 2)) {
                case "RS": {
                    builder.addEntry(KeysetHandle.importKey(JwkSetConverter.convertToRsaSsaPkcs1Key(jsonKey)).withRandomId());
                    continue block12;
                }
                case "PS": {
                    builder.addEntry(KeysetHandle.importKey(JwkSetConverter.convertToRsaSsaPssKey(jsonKey)).withRandomId());
                    continue block12;
                }
                case "ES": {
                    builder.addEntry(KeysetHandle.importKey(JwkSetConverter.convertToEcdsaKey(jsonKey)).withRandomId());
                    continue block12;
                }
            }
            throw new GeneralSecurityException("unexpected alg value: " + JwkSetConverter.getStringItem(jsonKey, "alg"));
        }
        if (builder.size() <= 0) {
            throw new GeneralSecurityException("empty keyset");
        }
        builder.getAt(0).makePrimary();
        return builder.build();
    }

    @AccessesPartialKey
    private static JsonObject convertJwtEcdsaKey(JwtEcdsaPublicKey key) throws GeneralSecurityException {
        int encLength;
        String crv;
        String alg;
        JwtEcdsaParameters.Algorithm algorithm = key.getParameters().getAlgorithm();
        if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES256)) {
            alg = "ES256";
            crv = "P-256";
            encLength = 32;
        } else if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES384)) {
            alg = "ES384";
            crv = "P-384";
            encLength = 48;
        } else if (algorithm.equals(JwtEcdsaParameters.Algorithm.ES512)) {
            alg = "ES512";
            crv = "P-521";
            encLength = 66;
        } else {
            throw new GeneralSecurityException("unknown algorithm");
        }
        JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty("kty", "EC");
        jsonKey.addProperty("crv", crv);
        BigInteger x = key.getPublicPoint().getAffineX();
        BigInteger y = key.getPublicPoint().getAffineY();
        jsonKey.addProperty("x", Base64.urlSafeEncode(BigIntegerEncoding.toBigEndianBytesOfFixedLength(x, encLength)));
        jsonKey.addProperty("y", Base64.urlSafeEncode(BigIntegerEncoding.toBigEndianBytesOfFixedLength(y, encLength)));
        jsonKey.addProperty("use", "sig");
        jsonKey.addProperty("alg", alg);
        JsonArray keyOps = new JsonArray();
        keyOps.add("verify");
        jsonKey.add("key_ops", keyOps);
        Optional<String> kid = key.getKid();
        if (kid.isPresent()) {
            jsonKey.addProperty("kid", kid.get());
        }
        return jsonKey;
    }

    private static byte[] base64urlUInt(BigInteger n) {
        if (n.equals(BigInteger.ZERO)) {
            return new byte[]{0};
        }
        return BigIntegerEncoding.toUnsignedBigEndianBytes(n);
    }

    @AccessesPartialKey
    private static JsonObject convertJwtRsaSsaPkcs1Key(JwtRsaSsaPkcs1PublicKey key) throws GeneralSecurityException {
        String alg = key.getParameters().getAlgorithm().getStandardName();
        JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty("kty", "RSA");
        jsonKey.addProperty("n", Base64.urlSafeEncode(JwkSetConverter.base64urlUInt(key.getModulus())));
        jsonKey.addProperty("e", Base64.urlSafeEncode(JwkSetConverter.base64urlUInt(key.getParameters().getPublicExponent())));
        jsonKey.addProperty("use", "sig");
        jsonKey.addProperty("alg", alg);
        JsonArray keyOps = new JsonArray();
        keyOps.add("verify");
        jsonKey.add("key_ops", keyOps);
        Optional<String> kid = key.getKid();
        if (kid.isPresent()) {
            jsonKey.addProperty("kid", kid.get());
        }
        return jsonKey;
    }

    @AccessesPartialKey
    private static JsonObject convertJwtRsaSsaPssKey(JwtRsaSsaPssPublicKey key) throws GeneralSecurityException {
        String alg = key.getParameters().getAlgorithm().getStandardName();
        JsonObject jsonKey = new JsonObject();
        jsonKey.addProperty("kty", "RSA");
        jsonKey.addProperty("n", Base64.urlSafeEncode(JwkSetConverter.base64urlUInt(key.getModulus())));
        jsonKey.addProperty("e", Base64.urlSafeEncode(JwkSetConverter.base64urlUInt(key.getParameters().getPublicExponent())));
        jsonKey.addProperty("use", "sig");
        jsonKey.addProperty("alg", alg);
        JsonArray keyOps = new JsonArray();
        keyOps.add("verify");
        jsonKey.add("key_ops", keyOps);
        Optional<String> kid = key.getKid();
        if (kid.isPresent()) {
            jsonKey.addProperty("kid", kid.get());
        }
        return jsonKey;
    }

    private static String getStringItem(JsonObject obj, String name) throws GeneralSecurityException {
        if (!obj.has(name)) {
            throw new GeneralSecurityException(name + " not found");
        }
        if (!obj.get(name).isJsonPrimitive() || !obj.get(name).getAsJsonPrimitive().isString()) {
            throw new GeneralSecurityException(name + " is not a string");
        }
        return obj.get(name).getAsString();
    }

    private static void expectStringItem(JsonObject obj, String name, String expectedValue) throws GeneralSecurityException {
        String value = JwkSetConverter.getStringItem(obj, name);
        if (!value.equals(expectedValue)) {
            throw new GeneralSecurityException("unexpected " + name + " value: " + value);
        }
    }

    private static void validateUseIsSig(JsonObject jsonKey) throws GeneralSecurityException {
        if (!jsonKey.has("use")) {
            return;
        }
        JwkSetConverter.expectStringItem(jsonKey, "use", "sig");
    }

    private static void validateKeyOpsIsVerify(JsonObject jsonKey) throws GeneralSecurityException {
        if (!jsonKey.has("key_ops")) {
            return;
        }
        if (!jsonKey.get("key_ops").isJsonArray()) {
            throw new GeneralSecurityException("key_ops is not an array");
        }
        JsonArray keyOps = jsonKey.get("key_ops").getAsJsonArray();
        if (keyOps.size() != 1) {
            throw new GeneralSecurityException("key_ops must contain exactly one element");
        }
        if (!keyOps.get(0).isJsonPrimitive() || !keyOps.get(0).getAsJsonPrimitive().isString()) {
            throw new GeneralSecurityException("key_ops is not a string");
        }
        if (!keyOps.get(0).getAsString().equals("verify")) {
            throw new GeneralSecurityException("unexpected keyOps value: " + keyOps.get(0).getAsString());
        }
    }

    @AccessesPartialKey
    private static JwtRsaSsaPkcs1PublicKey convertToRsaSsaPkcs1Key(JsonObject jsonKey) throws GeneralSecurityException {
        JwtRsaSsaPkcs1Parameters.Algorithm algorithm;
        switch (JwkSetConverter.getStringItem(jsonKey, "alg")) {
            case "RS256": {
                algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS256;
                break;
            }
            case "RS384": {
                algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS384;
                break;
            }
            case "RS512": {
                algorithm = JwtRsaSsaPkcs1Parameters.Algorithm.RS512;
                break;
            }
            default: {
                throw new GeneralSecurityException("Unknown Rsa Algorithm: " + JwkSetConverter.getStringItem(jsonKey, "alg"));
            }
        }
        if (jsonKey.has("p") || jsonKey.has("q") || jsonKey.has("dp") || jsonKey.has("dq") || jsonKey.has("d") || jsonKey.has("qi")) {
            throw new UnsupportedOperationException("importing RSA private keys is not implemented");
        }
        JwkSetConverter.expectStringItem(jsonKey, "kty", "RSA");
        JwkSetConverter.validateUseIsSig(jsonKey);
        JwkSetConverter.validateKeyOpsIsVerify(jsonKey);
        BigInteger publicExponent = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "e")));
        BigInteger modulus = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "n")));
        if (jsonKey.has("kid")) {
            return JwtRsaSsaPkcs1PublicKey.builder().setParameters(JwtRsaSsaPkcs1Parameters.builder().setModulusSizeBits(modulus.bitLength()).setPublicExponent(publicExponent).setAlgorithm(algorithm).setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.CUSTOM).build()).setModulus(modulus).setCustomKid(JwkSetConverter.getStringItem(jsonKey, "kid")).build();
        }
        return JwtRsaSsaPkcs1PublicKey.builder().setParameters(JwtRsaSsaPkcs1Parameters.builder().setModulusSizeBits(modulus.bitLength()).setPublicExponent(publicExponent).setAlgorithm(algorithm).setKidStrategy(JwtRsaSsaPkcs1Parameters.KidStrategy.IGNORED).build()).setModulus(modulus).build();
    }

    @AccessesPartialKey
    private static JwtRsaSsaPssPublicKey convertToRsaSsaPssKey(JsonObject jsonKey) throws GeneralSecurityException {
        JwtRsaSsaPssParameters.Algorithm algorithm;
        switch (JwkSetConverter.getStringItem(jsonKey, "alg")) {
            case "PS256": {
                algorithm = JwtRsaSsaPssParameters.Algorithm.PS256;
                break;
            }
            case "PS384": {
                algorithm = JwtRsaSsaPssParameters.Algorithm.PS384;
                break;
            }
            case "PS512": {
                algorithm = JwtRsaSsaPssParameters.Algorithm.PS512;
                break;
            }
            default: {
                throw new GeneralSecurityException("Unknown Rsa Algorithm: " + JwkSetConverter.getStringItem(jsonKey, "alg"));
            }
        }
        if (jsonKey.has("p") || jsonKey.has("q") || jsonKey.has("dq") || jsonKey.has("dq") || jsonKey.has("d") || jsonKey.has("qi")) {
            throw new UnsupportedOperationException("importing RSA private keys is not implemented");
        }
        JwkSetConverter.expectStringItem(jsonKey, "kty", "RSA");
        JwkSetConverter.validateUseIsSig(jsonKey);
        JwkSetConverter.validateKeyOpsIsVerify(jsonKey);
        BigInteger publicExponent = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "e")));
        BigInteger modulus = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "n")));
        if (jsonKey.has("kid")) {
            return JwtRsaSsaPssPublicKey.builder().setParameters(JwtRsaSsaPssParameters.builder().setModulusSizeBits(modulus.bitLength()).setPublicExponent(publicExponent).setAlgorithm(algorithm).setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.CUSTOM).build()).setModulus(modulus).setCustomKid(JwkSetConverter.getStringItem(jsonKey, "kid")).build();
        }
        return JwtRsaSsaPssPublicKey.builder().setParameters(JwtRsaSsaPssParameters.builder().setModulusSizeBits(modulus.bitLength()).setPublicExponent(publicExponent).setAlgorithm(algorithm).setKidStrategy(JwtRsaSsaPssParameters.KidStrategy.IGNORED).build()).setModulus(modulus).build();
    }

    @AccessesPartialKey
    private static JwtEcdsaPublicKey convertToEcdsaKey(JsonObject jsonKey) throws GeneralSecurityException {
        JwtEcdsaParameters.Algorithm algorithm;
        switch (JwkSetConverter.getStringItem(jsonKey, "alg")) {
            case "ES256": {
                JwkSetConverter.expectStringItem(jsonKey, "crv", "P-256");
                algorithm = JwtEcdsaParameters.Algorithm.ES256;
                break;
            }
            case "ES384": {
                JwkSetConverter.expectStringItem(jsonKey, "crv", "P-384");
                algorithm = JwtEcdsaParameters.Algorithm.ES384;
                break;
            }
            case "ES512": {
                JwkSetConverter.expectStringItem(jsonKey, "crv", "P-521");
                algorithm = JwtEcdsaParameters.Algorithm.ES512;
                break;
            }
            default: {
                throw new GeneralSecurityException("Unknown Ecdsa Algorithm: " + JwkSetConverter.getStringItem(jsonKey, "alg"));
            }
        }
        if (jsonKey.has("d")) {
            throw new UnsupportedOperationException("importing ECDSA private keys is not implemented");
        }
        JwkSetConverter.expectStringItem(jsonKey, "kty", "EC");
        JwkSetConverter.validateUseIsSig(jsonKey);
        JwkSetConverter.validateKeyOpsIsVerify(jsonKey);
        BigInteger x = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "x")));
        BigInteger y = new BigInteger(1, Base64.urlSafeDecode(JwkSetConverter.getStringItem(jsonKey, "y")));
        ECPoint publicPoint = new ECPoint(x, y);
        if (jsonKey.has("kid")) {
            return JwtEcdsaPublicKey.builder().setParameters(JwtEcdsaParameters.builder().setKidStrategy(JwtEcdsaParameters.KidStrategy.CUSTOM).setAlgorithm(algorithm).build()).setPublicPoint(publicPoint).setCustomKid(JwkSetConverter.getStringItem(jsonKey, "kid")).build();
        }
        return JwtEcdsaPublicKey.builder().setParameters(JwtEcdsaParameters.builder().setKidStrategy(JwtEcdsaParameters.KidStrategy.IGNORED).setAlgorithm(algorithm).build()).setPublicPoint(publicPoint).build();
    }

    @Deprecated
    @InlineMe(replacement="JwkSetConverter.fromPublicKeysetHandle(handle)", imports={"com.google.crypto.tink.jwt.JwkSetConverter"})
    public static String fromKeysetHandle(KeysetHandle handle, KeyAccess keyAccess) throws IOException, GeneralSecurityException {
        return JwkSetConverter.fromPublicKeysetHandle(handle);
    }

    @Deprecated
    @InlineMe(replacement="JwkSetConverter.toPublicKeysetHandle(jwkSet)", imports={"com.google.crypto.tink.jwt.JwkSetConverter"})
    public static KeysetHandle toKeysetHandle(String jwkSet, KeyAccess keyAccess) throws IOException, GeneralSecurityException {
        return JwkSetConverter.toPublicKeysetHandle(jwkSet);
    }

    private JwkSetConverter() {
    }
}

