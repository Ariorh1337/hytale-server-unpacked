/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryStringDecoder {
    private static final int DEFAULT_MAX_PARAMS = 1024;
    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private final boolean semicolonIsNormalChar;
    private final boolean htmlQueryDecoding;
    private int pathEndIdx;
    private String path;
    private Map<String, List<String>> params;

    public QueryStringDecoder(String uri) {
        this(QueryStringDecoder.builder(), uri);
    }

    public QueryStringDecoder(String uri, boolean hasPath) {
        this(QueryStringDecoder.builder().hasPath(hasPath), uri);
    }

    public QueryStringDecoder(String uri, Charset charset) {
        this(QueryStringDecoder.builder().charset(charset), uri);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(QueryStringDecoder.builder().hasPath(hasPath).charset(charset), uri);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        this(QueryStringDecoder.builder().hasPath(hasPath).charset(charset).maxParams(maxParams), uri);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams, boolean semicolonIsNormalChar) {
        this(QueryStringDecoder.builder().hasPath(hasPath).charset(charset).maxParams(maxParams).semicolonIsNormalChar(semicolonIsNormalChar), uri);
    }

    public QueryStringDecoder(URI uri) {
        this(QueryStringDecoder.builder(), uri);
    }

    public QueryStringDecoder(URI uri, Charset charset) {
        this(QueryStringDecoder.builder().charset(charset), uri);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams) {
        this(QueryStringDecoder.builder().charset(charset).maxParams(maxParams), uri);
    }

    public QueryStringDecoder(URI uri, Charset charset, int maxParams, boolean semicolonIsNormalChar) {
        this(QueryStringDecoder.builder().charset(charset).maxParams(maxParams).semicolonIsNormalChar(semicolonIsNormalChar), uri);
    }

    private QueryStringDecoder(Builder builder, String uri) {
        this.uri = ObjectUtil.checkNotNull(uri, "uri");
        this.charset = ObjectUtil.checkNotNull(builder.charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(builder.maxParams, "maxParams");
        this.semicolonIsNormalChar = builder.semicolonIsNormalChar;
        this.htmlQueryDecoding = builder.htmlQueryDecoding;
        this.pathEndIdx = builder.hasPath ? -1 : 0;
    }

    private QueryStringDecoder(Builder builder, URI uri) {
        String rawQuery;
        String rawPath = uri.getRawPath();
        if (rawPath == null) {
            rawPath = "";
        }
        this.uri = (rawQuery = uri.getRawQuery()) == null ? rawPath : rawPath + '?' + rawQuery;
        this.charset = ObjectUtil.checkNotNull(builder.charset, "charset");
        this.maxParams = ObjectUtil.checkPositive(builder.maxParams, "maxParams");
        this.semicolonIsNormalChar = builder.semicolonIsNormalChar;
        this.htmlQueryDecoding = builder.htmlQueryDecoding;
        this.pathEndIdx = rawPath.length();
    }

    public String toString() {
        return this.uri();
    }

    public String uri() {
        return this.uri;
    }

    public String path() {
        if (this.path == null) {
            this.path = QueryStringDecoder.decodeComponent(this.uri, 0, this.pathEndIdx(), this.charset, false);
        }
        return this.path;
    }

    public Map<String, List<String>> parameters() {
        if (this.params == null) {
            this.params = this.decodeParams(this.uri, this.pathEndIdx(), this.charset, this.maxParams);
        }
        return this.params;
    }

    public String rawPath() {
        return this.uri.substring(0, this.pathEndIdx());
    }

    public String rawQuery() {
        int start = this.pathEndIdx() + 1;
        return start < this.uri.length() ? this.uri.substring(start) : "";
    }

    private int pathEndIdx() {
        if (this.pathEndIdx == -1) {
            this.pathEndIdx = QueryStringDecoder.findPathEndIndex(this.uri);
        }
        return this.pathEndIdx;
    }

    /*
     * Enabled aggressive block sorting
     */
    private Map<String, List<String>> decodeParams(String s, int from, Charset charset, int paramsLimit) {
        int i;
        int len = s.length();
        if (from >= len) {
            return Collections.emptyMap();
        }
        if (s.charAt(from) == '?') {
            ++from;
        }
        LinkedHashMap<String, List<String>> params = new LinkedHashMap<String, List<String>>();
        int nameStart = from;
        int valueStart = -1;
        block6: for (i = from; i < len; ++i) {
            switch (s.charAt(i)) {
                case '=': {
                    if (nameStart == i) {
                        nameStart = i + 1;
                        break;
                    }
                    if (valueStart >= nameStart) break;
                    valueStart = i + 1;
                    break;
                }
                case ';': {
                    if (this.semicolonIsNormalChar) break;
                }
                case '&': {
                    if (this.addParam(s, nameStart, valueStart, i, params, charset) && --paramsLimit == 0) {
                        return params;
                    }
                    nameStart = i + 1;
                    break;
                }
                case '#': {
                    break block6;
                }
            }
        }
        this.addParam(s, nameStart, valueStart, i, params, charset);
        return params;
    }

    private boolean addParam(String s, int nameStart, int valueStart, int valueEnd, Map<String, List<String>> params, Charset charset) {
        if (nameStart >= valueEnd) {
            return false;
        }
        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }
        String name = QueryStringDecoder.decodeComponent(s, nameStart, valueStart - 1, charset, this.htmlQueryDecoding);
        String value = QueryStringDecoder.decodeComponent(s, valueStart, valueEnd, charset, this.htmlQueryDecoding);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<String>(1);
            params.put(name, values);
        }
        values.add(value);
        return true;
    }

    public static String decodeComponent(String s) {
        return QueryStringDecoder.decodeComponent(s, HttpConstants.DEFAULT_CHARSET);
    }

    public static String decodeComponent(String s, Charset charset) {
        if (s == null) {
            return "";
        }
        return QueryStringDecoder.decodeComponent(s, 0, s.length(), charset, true);
    }

    private static String decodeComponent(String s, int from, int toExcluded, Charset charset, boolean plusToSpace) {
        int len = toExcluded - from;
        if (len <= 0) {
            return "";
        }
        int firstEscaped = -1;
        for (int i = from; i < toExcluded; ++i) {
            char c = s.charAt(i);
            if (c != '%' && (c != '+' || !plusToSpace)) continue;
            firstEscaped = i;
            break;
        }
        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        byte[] buf = PlatformDependent.allocateUninitializedArray(decodedCapacity);
        StringBuilder strBuf = new StringBuilder(len);
        strBuf.append(s, from, firstEscaped);
        for (int i = firstEscaped; i < toExcluded; ++i) {
            char c = s.charAt(i);
            if (c != '%') {
                strBuf.append(c != '+' || !plusToSpace ? c : (char)' ');
                continue;
            }
            int bufIdx = 0;
            do {
                if (i + 3 > toExcluded) {
                    throw new IllegalArgumentException("unterminated escape sequence at index " + i + " of: " + s);
                }
                buf[bufIdx++] = StringUtil.decodeHexByte(s, i + 1);
            } while ((i += 3) < toExcluded && s.charAt(i) == '%');
            --i;
            strBuf.append(new String(buf, 0, bufIdx, charset));
        }
        return strBuf.toString();
    }

    private static int findPathEndIndex(String uri) {
        int len = uri.length();
        for (int i = 0; i < len; ++i) {
            char c = uri.charAt(i);
            if (c != '?' && c != '#') continue;
            return i;
        }
        return len;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean hasPath = true;
        private int maxParams = 1024;
        private boolean semicolonIsNormalChar;
        private Charset charset = HttpConstants.DEFAULT_CHARSET;
        private boolean htmlQueryDecoding = true;

        private Builder() {
        }

        public Builder hasPath(boolean hasPath) {
            this.hasPath = hasPath;
            return this;
        }

        public Builder maxParams(int maxParams) {
            this.maxParams = maxParams;
            return this;
        }

        public Builder semicolonIsNormalChar(boolean semicolonIsNormalChar) {
            this.semicolonIsNormalChar = semicolonIsNormalChar;
            return this;
        }

        public Builder charset(Charset charset) {
            this.charset = charset;
            return this;
        }

        public Builder htmlQueryDecoding(boolean htmlQueryDecoding) {
            this.htmlQueryDecoding = htmlQueryDecoding;
            return this;
        }

        public QueryStringDecoder build(String uri) {
            return new QueryStringDecoder(this, uri);
        }

        public QueryStringDecoder build(URI uri) {
            return new QueryStringDecoder(this, uri);
        }
    }
}

