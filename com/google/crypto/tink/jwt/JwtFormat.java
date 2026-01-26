/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.jwt;

import com.google.crypto.tink.internal.Util;
import com.google.crypto.tink.jwt.JwtInvalidException;
import com.google.crypto.tink.jwt.RawJwt;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Base64;
import com.google.gson.JsonObject;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.util.Optional;

final class JwtFormat {
    private JwtFormat() {
    }

    static boolean isValidUrlsafeBase64Char(char c) {
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '-' || c == '_';
    }

    static void validateUtf8(byte[] data) throws JwtInvalidException {
        CharsetDecoder decoder = Util.UTF_8.newDecoder();
        try {
            decoder.decode(ByteBuffer.wrap(data));
        }
        catch (CharacterCodingException ex) {
            throw new JwtInvalidException(ex.getMessage());
        }
    }

    static byte[] strictUrlSafeDecode(String encodedData) throws JwtInvalidException {
        for (int i = 0; i < encodedData.length(); ++i) {
            char c = encodedData.charAt(i);
            if (JwtFormat.isValidUrlsafeBase64Char(c)) continue;
            throw new JwtInvalidException("invalid encoding");
        }
        try {
            return Base64.urlSafeDecode(encodedData);
        }
        catch (IllegalArgumentException ex) {
            throw new JwtInvalidException("invalid encoding: " + ex);
        }
    }

    private static void validateAlgorithm(String algo) throws InvalidAlgorithmParameterException {
        switch (algo) {
            case "HS256": 
            case "HS384": 
            case "HS512": 
            case "ES256": 
            case "ES384": 
            case "ES512": 
            case "RS256": 
            case "RS384": 
            case "RS512": 
            case "PS256": 
            case "PS384": 
            case "PS512": {
                return;
            }
        }
        throw new InvalidAlgorithmParameterException("invalid algorithm: " + algo);
    }

    static String createHeader(String algorithm, Optional<String> typeHeader, Optional<String> kid) throws InvalidAlgorithmParameterException {
        JwtFormat.validateAlgorithm(algorithm);
        JsonObject header = new JsonObject();
        if (kid.isPresent()) {
            header.addProperty("kid", kid.get());
        }
        header.addProperty("alg", algorithm);
        if (typeHeader.isPresent()) {
            header.addProperty("typ", typeHeader.get());
        }
        return Base64.urlSafeEncode(header.toString().getBytes(Util.UTF_8));
    }

    private static void validateKidInHeader(String expectedKid, JsonObject parsedHeader) throws JwtInvalidException {
        String kid = JwtFormat.getStringHeader(parsedHeader, "kid");
        if (!kid.equals(expectedKid)) {
            throw new JwtInvalidException("invalid kid in header");
        }
    }

    static void validateHeader(JsonObject parsedHeader, String algorithmFromKey, Optional<String> kidFromKey, boolean allowKidAbsent) throws GeneralSecurityException {
        String receivedAlgorithm = JwtFormat.getStringHeader(parsedHeader, "alg");
        if (!receivedAlgorithm.equals(algorithmFromKey)) {
            throw new InvalidAlgorithmParameterException(String.format("invalid algorithm; expected %s, got %s", algorithmFromKey, receivedAlgorithm));
        }
        if (parsedHeader.has("crit")) {
            throw new JwtInvalidException("all tokens with crit headers are rejected");
        }
        boolean headerHasKid = parsedHeader.has("kid");
        if (!headerHasKid && allowKidAbsent) {
            return;
        }
        if (!headerHasKid && !allowKidAbsent) {
            throw new JwtInvalidException("missing kid in header");
        }
        if (!kidFromKey.isPresent()) {
            return;
        }
        String kid = JwtFormat.getStringHeader(parsedHeader, "kid");
        if (!kid.equals(kidFromKey.get())) {
            throw new JwtInvalidException("invalid kid in header");
        }
    }

    static void validateHeader(String expectedAlgorithm, Optional<String> tinkKid, Optional<String> customKid, JsonObject parsedHeader) throws InvalidAlgorithmParameterException, JwtInvalidException {
        JwtFormat.validateAlgorithm(expectedAlgorithm);
        String algorithm = JwtFormat.getStringHeader(parsedHeader, "alg");
        if (!algorithm.equals(expectedAlgorithm)) {
            throw new InvalidAlgorithmParameterException(String.format("invalid algorithm; expected %s, got %s", expectedAlgorithm, algorithm));
        }
        if (parsedHeader.has("crit")) {
            throw new JwtInvalidException("all tokens with crit headers are rejected");
        }
        if (tinkKid.isPresent() && customKid.isPresent()) {
            throw new JwtInvalidException("custom_kid can only be set for RAW keys.");
        }
        boolean headerHasKid = parsedHeader.has("kid");
        if (tinkKid.isPresent()) {
            if (!headerHasKid) {
                throw new JwtInvalidException("missing kid in header");
            }
            JwtFormat.validateKidInHeader(tinkKid.get(), parsedHeader);
        }
        if (customKid.isPresent() && headerHasKid) {
            JwtFormat.validateKidInHeader(customKid.get(), parsedHeader);
        }
    }

    static Optional<String> getTypeHeader(JsonObject header) throws JwtInvalidException {
        if (header.has("typ")) {
            return Optional.of(JwtFormat.getStringHeader(header, "typ"));
        }
        return Optional.empty();
    }

    static String getStringHeader(JsonObject header, String name) throws JwtInvalidException {
        if (!header.has(name)) {
            throw new JwtInvalidException("header " + name + " does not exist");
        }
        if (!header.get(name).isJsonPrimitive() || !header.get(name).getAsJsonPrimitive().isString()) {
            throw new JwtInvalidException("header " + name + " is not a string");
        }
        return header.get(name).getAsString();
    }

    static String decodeHeader(String headerStr) throws JwtInvalidException {
        byte[] data = JwtFormat.strictUrlSafeDecode(headerStr);
        JwtFormat.validateUtf8(data);
        return new String(data, Util.UTF_8);
    }

    static String encodePayload(String jsonPayload) {
        return Base64.urlSafeEncode(jsonPayload.getBytes(Util.UTF_8));
    }

    static String decodePayload(String payloadStr) throws JwtInvalidException {
        byte[] data = JwtFormat.strictUrlSafeDecode(payloadStr);
        JwtFormat.validateUtf8(data);
        return new String(data, Util.UTF_8);
    }

    static String encodeSignature(byte[] signature) {
        return Base64.urlSafeEncode(signature);
    }

    static byte[] decodeSignature(String signatureStr) throws JwtInvalidException {
        return JwtFormat.strictUrlSafeDecode(signatureStr);
    }

    static Optional<String> getKid(int keyId, OutputPrefixType prefix) throws JwtInvalidException {
        if (prefix == OutputPrefixType.RAW) {
            return Optional.empty();
        }
        if (prefix == OutputPrefixType.TINK) {
            byte[] bigEndianKeyId = ByteBuffer.allocate(4).putInt(keyId).array();
            return Optional.of(Base64.urlSafeEncode(bigEndianKeyId));
        }
        throw new JwtInvalidException("unsupported output prefix type");
    }

    static Optional<Integer> getKeyId(String kid) {
        byte[] encodedKeyId = Base64.urlSafeDecode(kid);
        if (encodedKeyId.length != 4) {
            return Optional.empty();
        }
        return Optional.of(ByteBuffer.wrap(encodedKeyId).getInt());
    }

    static Parts splitSignedCompact(String signedCompact) throws JwtInvalidException {
        JwtFormat.validateASCII(signedCompact);
        int sigPos = signedCompact.lastIndexOf(46);
        if (sigPos < 0) {
            throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
        }
        String unsignedCompact = signedCompact.substring(0, sigPos);
        String encodedMac = signedCompact.substring(sigPos + 1);
        byte[] mac = JwtFormat.decodeSignature(encodedMac);
        int payloadPos = unsignedCompact.indexOf(46);
        if (payloadPos < 0) {
            throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
        }
        String encodedHeader = unsignedCompact.substring(0, payloadPos);
        String encodedPayload = unsignedCompact.substring(payloadPos + 1);
        if (encodedPayload.indexOf(46) > 0) {
            throw new JwtInvalidException("only tokens in JWS compact serialization format are supported");
        }
        String header = JwtFormat.decodeHeader(encodedHeader);
        String payload = JwtFormat.decodePayload(encodedPayload);
        return new Parts(unsignedCompact, mac, header, payload);
    }

    static String createUnsignedCompact(String algorithm, Optional<String> kid, RawJwt rawJwt) throws InvalidAlgorithmParameterException, JwtInvalidException {
        String jsonPayload = rawJwt.getJsonPayload();
        Optional<String> typeHeader = rawJwt.hasTypeHeader() ? Optional.of(rawJwt.getTypeHeader()) : Optional.empty();
        return JwtFormat.createHeader(algorithm, typeHeader, kid) + "." + JwtFormat.encodePayload(jsonPayload);
    }

    static String createSignedCompact(String unsignedCompact, byte[] signature) {
        return unsignedCompact + "." + JwtFormat.encodeSignature(signature);
    }

    static void validateASCII(String data) throws JwtInvalidException {
        for (int i = 0; i < data.length(); ++i) {
            char c = data.charAt(i);
            if ((c & 0x80) <= 0) continue;
            throw new JwtInvalidException("Non ascii character");
        }
    }

    static class Parts {
        String unsignedCompact;
        byte[] signatureOrMac;
        String header;
        String payload;

        Parts(String unsignedCompact, byte[] signatureOrMac, String header, String payload) {
            this.unsignedCompact = unsignedCompact;
            this.signatureOrMac = signatureOrMac;
            this.header = header;
            this.payload = payload;
        }
    }
}

