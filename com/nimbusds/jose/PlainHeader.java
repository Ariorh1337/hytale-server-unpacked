/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jose;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.Header;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Immutable
public final class PlainHeader
extends Header {
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_PARAMETER_NAMES;

    public PlainHeader() {
        this(null, null, null, null, null);
    }

    public PlainHeader(JOSEObjectType typ, String cty, Set<String> crit, Map<String, Object> customParams, Base64URL parsedBase64URL) {
        super(Algorithm.NONE, typ, cty, crit, customParams, parsedBase64URL);
    }

    public PlainHeader(PlainHeader plainHeader) {
        this(plainHeader.getType(), plainHeader.getContentType(), plainHeader.getCriticalParams(), plainHeader.getCustomParams(), plainHeader.getParsedBase64URL());
    }

    public static Set<String> getRegisteredParameterNames() {
        return REGISTERED_PARAMETER_NAMES;
    }

    @Override
    public Algorithm getAlgorithm() {
        return Algorithm.NONE;
    }

    public static PlainHeader parse(Map<String, Object> jsonObject) throws ParseException {
        return PlainHeader.parse(jsonObject, null);
    }

    public static PlainHeader parse(Map<String, Object> jsonObject, Base64URL parsedBase64URL) throws ParseException {
        Algorithm alg = Header.parseAlgorithm(jsonObject);
        if (alg != Algorithm.NONE) {
            throw new ParseException("The algorithm \"alg\" header parameter must be \"none\"", 0);
        }
        Builder header = new Builder().parsedBase64URL(parsedBase64URL);
        for (String name : jsonObject.keySet()) {
            if ("alg".equals(name)) continue;
            if ("typ".equals(name)) {
                String typValue = JSONObjectUtils.getString(jsonObject, name);
                if (typValue == null) continue;
                header = header.type(new JOSEObjectType(typValue));
                continue;
            }
            if ("cty".equals(name)) {
                header = header.contentType(JSONObjectUtils.getString(jsonObject, name));
                continue;
            }
            if ("crit".equals(name)) {
                List<String> critValues = JSONObjectUtils.getStringList(jsonObject, name);
                if (critValues == null) continue;
                header = header.criticalParams(new HashSet<String>(critValues));
                continue;
            }
            header = header.customParam(name, jsonObject.get(name));
        }
        return header.build();
    }

    public static PlainHeader parse(String jsonString) throws ParseException {
        return PlainHeader.parse(jsonString, null);
    }

    public static PlainHeader parse(String jsonString, Base64URL parsedBase64URL) throws ParseException {
        return PlainHeader.parse(JSONObjectUtils.parse(jsonString, 20000), parsedBase64URL);
    }

    public static PlainHeader parse(Base64URL base64URL) throws ParseException {
        return PlainHeader.parse(base64URL.decodeToString(), base64URL);
    }

    static {
        HashSet<String> p = new HashSet<String>();
        p.add("alg");
        p.add("typ");
        p.add("cty");
        p.add("crit");
        REGISTERED_PARAMETER_NAMES = Collections.unmodifiableSet(p);
    }

    public static class Builder {
        private JOSEObjectType typ;
        private String cty;
        private Set<String> crit;
        private Map<String, Object> customParams;
        private Base64URL parsedBase64URL;

        public Builder() {
        }

        public Builder(PlainHeader plainHeader) {
            this.typ = plainHeader.getType();
            this.cty = plainHeader.getContentType();
            this.crit = plainHeader.getCriticalParams();
            this.customParams = plainHeader.getCustomParams();
        }

        public Builder type(JOSEObjectType typ) {
            this.typ = typ;
            return this;
        }

        public Builder contentType(String cty) {
            this.cty = cty;
            return this;
        }

        public Builder criticalParams(Set<String> crit) {
            this.crit = crit;
            return this;
        }

        public Builder customParam(String name, Object value) {
            if (PlainHeader.getRegisteredParameterNames().contains(name)) {
                throw new IllegalArgumentException("The parameter name \"" + name + "\" matches a registered name");
            }
            if (this.customParams == null) {
                this.customParams = new HashMap<String, Object>();
            }
            this.customParams.put(name, value);
            return this;
        }

        public Builder customParams(Map<String, Object> customParameters) {
            this.customParams = customParameters;
            return this;
        }

        public Builder parsedBase64URL(Base64URL base64URL) {
            this.parsedBase64URL = base64URL;
            return this;
        }

        public PlainHeader build() {
            return new PlainHeader(this.typ, this.cty, this.crit, this.customParams, this.parsedBase64URL);
        }
    }
}

