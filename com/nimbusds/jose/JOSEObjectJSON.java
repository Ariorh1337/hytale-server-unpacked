/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.JWSObjectJSON;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;
import java.util.Objects;

public abstract class JOSEObjectJSON
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final String MIME_TYPE_JOSE_JSON = "application/jose+json; charset=UTF-8";
    private Payload payload;

    protected JOSEObjectJSON(Payload payload) {
        this.payload = payload;
    }

    protected void setPayload(Payload payload) {
        this.payload = payload;
    }

    public Payload getPayload() {
        return this.payload;
    }

    public abstract Map<String, Object> toGeneralJSONObject();

    public abstract Map<String, Object> toFlattenedJSONObject();

    public abstract String serializeGeneral();

    public abstract String serializeFlattened();

    public static JOSEObjectJSON parse(Map<String, Object> jsonObject) throws ParseException {
        if (jsonObject.containsKey("signature") || jsonObject.containsKey("signatures")) {
            return JWSObjectJSON.parse(jsonObject);
        }
        if (jsonObject.containsKey("ciphertext")) {
            throw new ParseException("JWE JSON not supported", 0);
        }
        throw new ParseException("Invalid JOSE object", 0);
    }

    public static JOSEObjectJSON parse(String json) throws ParseException {
        Objects.requireNonNull(json);
        return JOSEObjectJSON.parse(JSONObjectUtils.parse(json));
    }
}

