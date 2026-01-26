/*
 * Decompiled with CFR 0.152.
 */
package com.nimbusds.jwt;

import com.nimbusds.jose.Payload;
import com.nimbusds.jose.shaded.jcip.Immutable;
import com.nimbusds.jose.util.JSONArrayUtils;
import com.nimbusds.jose.util.JSONObjectUtils;
import com.nimbusds.jwt.JWTClaimsSetTransformer;
import com.nimbusds.jwt.util.DateUtils;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Immutable
public final class JWTClaimsSet
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Set<String> REGISTERED_CLAIM_NAMES;
    private final Map<String, Object> claims = new LinkedHashMap<String, Object>();
    private final boolean serializeNullClaims;

    private JWTClaimsSet(Map<String, Object> claims, boolean serializeNullClaims) {
        this.claims.putAll(claims);
        this.serializeNullClaims = serializeNullClaims;
    }

    public static Set<String> getRegisteredNames() {
        return REGISTERED_CLAIM_NAMES;
    }

    public String getIssuer() {
        try {
            return this.getStringClaim("iss");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getSubject() {
        try {
            return this.getStringClaim("sub");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public List<String> getAudience() {
        List<String> aud;
        Object audValue = this.getClaim("aud");
        if (audValue instanceof String) {
            return Collections.singletonList((String)audValue);
        }
        try {
            aud = this.getStringListClaim("aud");
        }
        catch (ParseException e) {
            return Collections.emptyList();
        }
        return aud != null ? aud : Collections.emptyList();
    }

    public Date getExpirationTime() {
        try {
            return this.getDateClaim("exp");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getNotBeforeTime() {
        try {
            return this.getDateClaim("nbf");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Date getIssueTime() {
        try {
            return this.getDateClaim("iat");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public String getJWTID() {
        try {
            return this.getStringClaim("jti");
        }
        catch (ParseException e) {
            return null;
        }
    }

    public Object getClaim(String name) {
        return this.claims.get(name);
    }

    public String getStringClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null || value instanceof String) {
            return (String)value;
        }
        throw new ParseException("The " + name + " claim is not a String", 0);
    }

    public String getClaimAsString(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null || value instanceof String) {
            return (String)value;
        }
        Class<?> clazz = value.getClass();
        if (clazz.isPrimitive() || JWTClaimsSet.isWrapper(clazz)) {
            return String.valueOf(value);
        }
        throw new ParseException("The " + name + " claim is not and cannot be converted to a String", 0);
    }

    private static boolean isWrapper(Class<?> clazz) {
        return clazz == Integer.class || clazz == Double.class || clazz == Float.class || clazz == Long.class || clazz == Short.class || clazz == Byte.class || clazz == Character.class || clazz == Boolean.class;
    }

    public List<Object> getListClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        try {
            return (List)this.getClaim(name);
        }
        catch (ClassCastException e) {
            throw new ParseException("The " + name + " claim is not a list / JSON array", 0);
        }
    }

    public String[] getStringArrayClaim(String name) throws ParseException {
        List<Object> list = this.getListClaim(name);
        if (list == null) {
            return null;
        }
        String[] stringArray = new String[list.size()];
        for (int i = 0; i < stringArray.length; ++i) {
            try {
                stringArray[i] = (String)list.get(i);
                continue;
            }
            catch (ClassCastException e) {
                throw new ParseException("The " + name + " claim is not a list / JSON array of strings", 0);
            }
        }
        return stringArray;
    }

    public List<String> getStringListClaim(String name) throws ParseException {
        String[] stringArray = this.getStringArrayClaim(name);
        if (stringArray == null) {
            return null;
        }
        return Collections.unmodifiableList(Arrays.asList(stringArray));
    }

    public URI getURIClaim(String name) throws ParseException {
        String uriString = this.getStringClaim(name);
        if (uriString == null) {
            return null;
        }
        try {
            return new URI(uriString);
        }
        catch (URISyntaxException e) {
            throw new ParseException("The \"" + name + "\" claim is not a URI: " + e.getMessage(), 0);
        }
    }

    public Boolean getBooleanClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null || value instanceof Boolean) {
            return (Boolean)value;
        }
        throw new ParseException("The \"" + name + "\" claim is not a Boolean", 0);
    }

    public Integer getIntegerClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).intValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not an Integer", 0);
    }

    public Long getLongClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Number", 0);
    }

    public Date getDateClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Date) {
            return (Date)value;
        }
        if (value instanceof Number) {
            return DateUtils.fromSecondsSinceEpoch(((Number)value).longValue());
        }
        throw new ParseException("The \"" + name + "\" claim is not a Date", 0);
    }

    public Float getFloatClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return Float.valueOf(((Number)value).floatValue());
        }
        throw new ParseException("The \"" + name + "\" claim is not a Float", 0);
    }

    public Double getDoubleClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        throw new ParseException("The \"" + name + "\" claim is not a Double", 0);
    }

    public Map<String, Object> getJSONObjectClaim(String name) throws ParseException {
        Object value = this.getClaim(name);
        if (value == null) {
            return null;
        }
        if (value instanceof Map) {
            Map<String, Object> jsonObject = JSONObjectUtils.newJSONObject();
            Map map = (Map)value;
            for (Map.Entry entry : map.entrySet()) {
                if (!(entry.getKey() instanceof String)) continue;
                jsonObject.put((String)entry.getKey(), entry.getValue());
            }
            return jsonObject;
        }
        throw new ParseException("The \"" + name + "\" claim is not a JSON object or Map", 0);
    }

    public Map<String, Object> getClaims() {
        return Collections.unmodifiableMap(this.claims);
    }

    public Payload toPayload() {
        return new Payload(this.toJSONObject(this.serializeNullClaims));
    }

    public Payload toPayload(boolean serializeNullClaims) {
        return new Payload(this.toJSONObject(serializeNullClaims));
    }

    public Map<String, Object> toJSONObject() {
        return this.toJSONObject(this.serializeNullClaims);
    }

    public Map<String, Object> toJSONObject(boolean serializeNullClaims) {
        Map<String, Object> o = JSONObjectUtils.newJSONObject();
        for (Map.Entry<String, Object> claim : this.claims.entrySet()) {
            if (claim.getValue() instanceof Date) {
                Date dateValue = (Date)claim.getValue();
                o.put(claim.getKey(), DateUtils.toSecondsSinceEpoch(dateValue));
                continue;
            }
            if ("aud".equals(claim.getKey())) {
                List<String> audList = this.getAudience();
                if (audList != null && !audList.isEmpty()) {
                    if (audList.size() == 1) {
                        o.put("aud", audList.get(0));
                        continue;
                    }
                    List<Object> audArray = JSONArrayUtils.newJSONArray();
                    audArray.addAll(audList);
                    o.put("aud", audArray);
                    continue;
                }
                if (!serializeNullClaims) continue;
                o.put("aud", null);
                continue;
            }
            if (claim.getValue() != null) {
                o.put(claim.getKey(), claim.getValue());
                continue;
            }
            if (!serializeNullClaims) continue;
            o.put(claim.getKey(), null);
        }
        return o;
    }

    public String toString() {
        return JSONObjectUtils.toJSONString(this.toJSONObject());
    }

    public String toString(boolean serializeNullClaims) {
        return JSONObjectUtils.toJSONString(this.toJSONObject(serializeNullClaims));
    }

    public <T> T toType(JWTClaimsSetTransformer<T> transformer) {
        return transformer.transform(this);
    }

    public static JWTClaimsSet parse(Map<String, Object> json) throws ParseException {
        Builder builder = new Builder();
        Iterator<String> iterator = json.keySet().iterator();
        block18: while (iterator.hasNext()) {
            String name;
            switch (name = iterator.next()) {
                case "iss": {
                    builder.issuer(JSONObjectUtils.getString(json, "iss"));
                    continue block18;
                }
                case "sub": {
                    Object subValue = json.get("sub");
                    if (subValue instanceof String) {
                        builder.subject(JSONObjectUtils.getString(json, "sub"));
                        continue block18;
                    }
                    if (subValue instanceof Number) {
                        builder.subject(String.valueOf(subValue));
                        continue block18;
                    }
                    if (subValue == null) {
                        builder.subject(null);
                        continue block18;
                    }
                    throw new ParseException("Illegal sub claim", 0);
                }
                case "aud": {
                    Object audValue = json.get("aud");
                    if (audValue instanceof String) {
                        ArrayList<String> singleAud = new ArrayList<String>();
                        singleAud.add(JSONObjectUtils.getString(json, "aud"));
                        builder.audience(singleAud);
                        continue block18;
                    }
                    if (audValue instanceof List) {
                        builder.audience(JSONObjectUtils.getStringList(json, "aud"));
                        continue block18;
                    }
                    if (audValue == null) {
                        builder.audience((String)null);
                        continue block18;
                    }
                    throw new ParseException("Illegal aud claim", 0);
                }
                case "exp": {
                    builder.expirationTime(JSONObjectUtils.getEpochSecondAsDate(json, "exp"));
                    continue block18;
                }
                case "nbf": {
                    builder.notBeforeTime(JSONObjectUtils.getEpochSecondAsDate(json, "nbf"));
                    continue block18;
                }
                case "iat": {
                    builder.issueTime(JSONObjectUtils.getEpochSecondAsDate(json, "iat"));
                    continue block18;
                }
                case "jti": {
                    builder.jwtID(JSONObjectUtils.getString(json, "jti"));
                    continue block18;
                }
            }
            builder.claim(name, json.get(name));
        }
        return builder.build();
    }

    public static JWTClaimsSet parse(String s) throws ParseException {
        return JWTClaimsSet.parse(JSONObjectUtils.parse(s));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWTClaimsSet)) {
            return false;
        }
        JWTClaimsSet that = (JWTClaimsSet)o;
        return Objects.equals(this.claims, that.claims);
    }

    public int hashCode() {
        return Objects.hash(this.claims);
    }

    static {
        HashSet<String> n = new HashSet<String>();
        n.add("iss");
        n.add("sub");
        n.add("aud");
        n.add("exp");
        n.add("nbf");
        n.add("iat");
        n.add("jti");
        REGISTERED_CLAIM_NAMES = Collections.unmodifiableSet(n);
    }

    public static class Builder {
        private final Map<String, Object> claims = new LinkedHashMap<String, Object>();
        private boolean serializeNullClaims = false;

        public Builder() {
        }

        public Builder(JWTClaimsSet jwtClaimsSet) {
            this.claims.putAll(jwtClaimsSet.claims);
        }

        public Builder serializeNullClaims(boolean enable) {
            this.serializeNullClaims = enable;
            return this;
        }

        public Builder issuer(String iss) {
            this.claims.put("iss", iss);
            return this;
        }

        public Builder subject(String sub) {
            this.claims.put("sub", sub);
            return this;
        }

        public Builder audience(List<String> aud) {
            this.claims.put("aud", aud);
            return this;
        }

        public Builder audience(String aud) {
            if (aud == null) {
                this.claims.put("aud", null);
            } else {
                this.claims.put("aud", Collections.singletonList(aud));
            }
            return this;
        }

        public Builder expirationTime(Date exp) {
            this.claims.put("exp", exp);
            return this;
        }

        public Builder notBeforeTime(Date nbf) {
            this.claims.put("nbf", nbf);
            return this;
        }

        public Builder issueTime(Date iat) {
            this.claims.put("iat", iat);
            return this;
        }

        public Builder jwtID(String jti) {
            this.claims.put("jti", jti);
            return this;
        }

        public Builder claim(String name, Object value) {
            this.claims.put(name, value);
            return this;
        }

        public Map<String, Object> getClaims() {
            return Collections.unmodifiableMap(this.claims);
        }

        public JWTClaimsSet build() {
            return new JWTClaimsSet(this.claims, this.serializeNullClaims);
        }
    }
}

