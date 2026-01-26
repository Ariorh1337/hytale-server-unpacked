/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.internal.ObjectUtil;

public class HttpMethod
implements Comparable<HttpMethod> {
    private static final String GET_STRING = "GET";
    private static final String POST_STRING = "POST";
    public static final HttpMethod OPTIONS = new HttpMethod("OPTIONS");
    public static final HttpMethod GET = new HttpMethod("GET");
    public static final HttpMethod HEAD = new HttpMethod("HEAD");
    public static final HttpMethod POST = new HttpMethod("POST");
    public static final HttpMethod PUT = new HttpMethod("PUT");
    public static final HttpMethod PATCH = new HttpMethod("PATCH");
    public static final HttpMethod DELETE = new HttpMethod("DELETE");
    public static final HttpMethod TRACE = new HttpMethod("TRACE");
    public static final HttpMethod CONNECT = new HttpMethod("CONNECT");
    private final AsciiString name;

    public static HttpMethod valueOf(String name) {
        switch (name) {
            case "OPTIONS": {
                return OPTIONS;
            }
            case "GET": {
                return GET;
            }
            case "HEAD": {
                return HEAD;
            }
            case "POST": {
                return POST;
            }
            case "PUT": {
                return PUT;
            }
            case "PATCH": {
                return PATCH;
            }
            case "DELETE": {
                return DELETE;
            }
            case "TRACE": {
                return TRACE;
            }
            case "CONNECT": {
                return CONNECT;
            }
        }
        return new HttpMethod(name);
    }

    public HttpMethod(String name) {
        name = ObjectUtil.checkNonEmptyAfterTrim(name, "name");
        int index = HttpUtil.validateToken(name);
        if (index != -1) {
            throw new IllegalArgumentException("Illegal character in HTTP Method: 0x" + Integer.toHexString(name.charAt(index)));
        }
        this.name = AsciiString.cached(name);
    }

    public String name() {
        return this.name.toString();
    }

    public AsciiString asciiName() {
        return this.name;
    }

    public int hashCode() {
        return this.name().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpMethod)) {
            return false;
        }
        HttpMethod that = (HttpMethod)o;
        return this.name().equals(that.name());
    }

    public String toString() {
        return this.name.toString();
    }

    @Override
    public int compareTo(HttpMethod o) {
        if (o == this) {
            return 0;
        }
        return this.name().compareTo(o.name());
    }
}

