/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink;

import com.google.crypto.tink.KeysetReader;
import com.google.crypto.tink.Util;
import com.google.crypto.tink.internal.JsonParser;
import com.google.crypto.tink.proto.EncryptedKeyset;
import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.Keyset;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.subtle.Base64;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.InlineMe;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.protobuf.ByteString;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;

public final class JsonKeysetReader
implements KeysetReader {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final InputStream inputStream;
    private boolean urlSafeBase64 = false;
    private static final long MAX_KEY_ID = 0xFFFFFFFFL;
    private static final long MIN_KEY_ID = Integer.MIN_VALUE;

    private JsonKeysetReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static JsonKeysetReader withInputStream(InputStream input) throws IOException {
        return new JsonKeysetReader(input);
    }

    @Deprecated
    @InlineMe(replacement="JsonKeysetReader.withString(input.toString())", imports={"com.google.crypto.tink.JsonKeysetReader"})
    public static JsonKeysetReader withJsonObject(Object input) {
        return JsonKeysetReader.withString(input.toString());
    }

    public static JsonKeysetReader withString(String input) {
        return new JsonKeysetReader(new ByteArrayInputStream(input.getBytes(UTF_8)));
    }

    @Deprecated
    public static JsonKeysetReader withBytes(byte[] bytes) {
        return new JsonKeysetReader(new ByteArrayInputStream(bytes));
    }

    @Deprecated
    @InlineMe(replacement="JsonKeysetReader.withInputStream(new FileInputStream(file))", imports={"com.google.crypto.tink.JsonKeysetReader", "java.io.FileInputStream"})
    public static JsonKeysetReader withFile(File file) throws IOException {
        return JsonKeysetReader.withInputStream(new FileInputStream(file));
    }

    @Deprecated
    @InlineMe(replacement="JsonKeysetReader.withInputStream(new FileInputStream(new File(path)))", imports={"com.google.crypto.tink.JsonKeysetReader", "java.io.File", "java.io.FileInputStream"})
    public static JsonKeysetReader withPath(String path) throws IOException {
        return JsonKeysetReader.withInputStream(new FileInputStream(new File(path)));
    }

    @Deprecated
    @InlineMe(replacement="JsonKeysetReader.withInputStream(new FileInputStream(path.toFile()))", imports={"com.google.crypto.tink.JsonKeysetReader", "java.io.FileInputStream"})
    public static JsonKeysetReader withPath(Path path) throws IOException {
        return JsonKeysetReader.withInputStream(new FileInputStream(path.toFile()));
    }

    @CanIgnoreReturnValue
    public JsonKeysetReader withUrlSafeBase64() {
        this.urlSafeBase64 = true;
        return this;
    }

    @Override
    public Keyset read() throws IOException {
        try {
            Keyset keyset = this.keysetFromJson(JsonParser.parse(new String(Util.readAll(this.inputStream), UTF_8)).getAsJsonObject());
            return keyset;
        }
        catch (JsonParseException | IllegalStateException e) {
            throw new IOException(e);
        }
        finally {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
    }

    @Override
    public EncryptedKeyset readEncrypted() throws IOException {
        try {
            EncryptedKeyset encryptedKeyset = this.encryptedKeysetFromJson(JsonParser.parse(new String(Util.readAll(this.inputStream), UTF_8)).getAsJsonObject());
            return encryptedKeyset;
        }
        catch (JsonParseException | IllegalStateException e) {
            throw new IOException(e);
        }
        finally {
            if (this.inputStream != null) {
                this.inputStream.close();
            }
        }
    }

    private static int getKeyId(JsonElement element) throws IOException {
        long id;
        if (!element.isJsonPrimitive()) {
            throw new IOException("invalid key id: not a JSON primitive");
        }
        if (!element.getAsJsonPrimitive().isNumber()) {
            throw new IOException("invalid key id: not a JSON number");
        }
        Number number = element.getAsJsonPrimitive().getAsNumber();
        try {
            id = JsonParser.getParsedNumberAsLongOrThrow(number);
        }
        catch (NumberFormatException e) {
            throw new IOException(e);
        }
        if (id > 0xFFFFFFFFL || id < Integer.MIN_VALUE) {
            throw new IOException("invalid key id");
        }
        return (int)id;
    }

    private Keyset keysetFromJson(JsonObject json) throws IOException {
        if (!json.has("key")) {
            throw new JsonParseException("invalid keyset: no key");
        }
        JsonElement key = json.get("key");
        if (!key.isJsonArray()) {
            throw new JsonParseException("invalid keyset: key must be an array");
        }
        JsonArray keys = key.getAsJsonArray();
        if (keys.size() == 0) {
            throw new JsonParseException("invalid keyset: key is empty");
        }
        Keyset.Builder builder = Keyset.newBuilder();
        if (json.has("primaryKeyId")) {
            builder.setPrimaryKeyId(JsonKeysetReader.getKeyId(json.get("primaryKeyId")));
        }
        for (int i = 0; i < keys.size(); ++i) {
            builder.addKey(this.keyFromJson(keys.get(i).getAsJsonObject()));
        }
        return builder.build();
    }

    private EncryptedKeyset encryptedKeysetFromJson(JsonObject json) throws IOException {
        JsonKeysetReader.validateEncryptedKeyset(json);
        byte[] encryptedKeyset = this.urlSafeBase64 ? Base64.urlSafeDecode(json.get("encryptedKeyset").getAsString()) : Base64.decode(json.get("encryptedKeyset").getAsString());
        if (json.has("keysetInfo")) {
            return EncryptedKeyset.newBuilder().setEncryptedKeyset(ByteString.copyFrom(encryptedKeyset)).setKeysetInfo(JsonKeysetReader.keysetInfoFromJson(json.getAsJsonObject("keysetInfo"))).build();
        }
        return EncryptedKeyset.newBuilder().setEncryptedKeyset(ByteString.copyFrom(encryptedKeyset)).build();
    }

    private Keyset.Key keyFromJson(JsonObject json) throws IOException {
        if (!(json.has("keyData") && json.has("status") && json.has("keyId") && json.has("outputPrefixType"))) {
            throw new JsonParseException("invalid key");
        }
        JsonElement keyData = json.get("keyData");
        if (!keyData.isJsonObject()) {
            throw new JsonParseException("invalid key: keyData must be an object");
        }
        return Keyset.Key.newBuilder().setStatus(JsonKeysetReader.getStatus(json.get("status").getAsString())).setKeyId(JsonKeysetReader.getKeyId(json.get("keyId"))).setOutputPrefixType(JsonKeysetReader.getOutputPrefixType(json.get("outputPrefixType").getAsString())).setKeyData(this.keyDataFromJson(keyData.getAsJsonObject())).build();
    }

    private static KeysetInfo keysetInfoFromJson(JsonObject json) throws IOException {
        KeysetInfo.Builder builder = KeysetInfo.newBuilder();
        if (json.has("primaryKeyId")) {
            builder.setPrimaryKeyId(JsonKeysetReader.getKeyId(json.get("primaryKeyId")));
        }
        if (json.has("keyInfo")) {
            JsonArray keyInfos = json.getAsJsonArray("keyInfo");
            for (int i = 0; i < keyInfos.size(); ++i) {
                builder.addKeyInfo(JsonKeysetReader.keyInfoFromJson(keyInfos.get(i).getAsJsonObject()));
            }
        }
        return builder.build();
    }

    private static KeysetInfo.KeyInfo keyInfoFromJson(JsonObject json) throws IOException {
        return KeysetInfo.KeyInfo.newBuilder().setStatus(JsonKeysetReader.getStatus(json.get("status").getAsString())).setKeyId(JsonKeysetReader.getKeyId(json.get("keyId"))).setOutputPrefixType(JsonKeysetReader.getOutputPrefixType(json.get("outputPrefixType").getAsString())).setTypeUrl(json.get("typeUrl").getAsString()).build();
    }

    private KeyData keyDataFromJson(JsonObject json) {
        if (!(json.has("typeUrl") && json.has("value") && json.has("keyMaterialType"))) {
            throw new JsonParseException("invalid keyData");
        }
        byte[] value = this.urlSafeBase64 ? Base64.urlSafeDecode(json.get("value").getAsString()) : Base64.decode(json.get("value").getAsString());
        return KeyData.newBuilder().setTypeUrl(json.get("typeUrl").getAsString()).setValue(ByteString.copyFrom(value)).setKeyMaterialType(JsonKeysetReader.getKeyMaterialType(json.get("keyMaterialType").getAsString())).build();
    }

    private static KeyStatusType getStatus(String status) {
        switch (status) {
            case "ENABLED": {
                return KeyStatusType.ENABLED;
            }
            case "DISABLED": {
                return KeyStatusType.DISABLED;
            }
            case "DESTROYED": {
                return KeyStatusType.DESTROYED;
            }
        }
        throw new JsonParseException("unknown status: " + status);
    }

    private static OutputPrefixType getOutputPrefixType(String type) {
        switch (type) {
            case "TINK": {
                return OutputPrefixType.TINK;
            }
            case "RAW": {
                return OutputPrefixType.RAW;
            }
            case "LEGACY": {
                return OutputPrefixType.LEGACY;
            }
            case "CRUNCHY": {
                return OutputPrefixType.CRUNCHY;
            }
        }
        throw new JsonParseException("unknown output prefix type: " + type);
    }

    private static KeyData.KeyMaterialType getKeyMaterialType(String type) {
        switch (type) {
            case "SYMMETRIC": {
                return KeyData.KeyMaterialType.SYMMETRIC;
            }
            case "ASYMMETRIC_PRIVATE": {
                return KeyData.KeyMaterialType.ASYMMETRIC_PRIVATE;
            }
            case "ASYMMETRIC_PUBLIC": {
                return KeyData.KeyMaterialType.ASYMMETRIC_PUBLIC;
            }
            case "REMOTE": {
                return KeyData.KeyMaterialType.REMOTE;
            }
        }
        throw new JsonParseException("unknown key material type: " + type);
    }

    private static void validateEncryptedKeyset(JsonObject json) {
        if (!json.has("encryptedKeyset")) {
            throw new JsonParseException("invalid encrypted keyset");
        }
    }
}

