/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http3;

import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.http3.CharSequenceMap;
import io.netty.handler.codec.http3.QpackHeaderField;
import io.netty.handler.codec.http3.QpackUtil;
import io.netty.util.AsciiString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class QpackStaticTable {
    static final int NOT_FOUND = -1;
    static final int MASK_NAME_REF = 1024;
    private static final List<QpackHeaderField> STATIC_TABLE = Arrays.asList(QpackStaticTable.newEmptyHeaderField(":authority"), QpackStaticTable.newHeaderField(":path", "/"), QpackStaticTable.newHeaderField("age", "0"), QpackStaticTable.newEmptyHeaderField("content-disposition"), QpackStaticTable.newHeaderField("content-length", "0"), QpackStaticTable.newEmptyHeaderField("cookie"), QpackStaticTable.newEmptyHeaderField("date"), QpackStaticTable.newEmptyHeaderField("etag"), QpackStaticTable.newEmptyHeaderField("if-modified-since"), QpackStaticTable.newEmptyHeaderField("if-none-match"), QpackStaticTable.newEmptyHeaderField("last-modified"), QpackStaticTable.newEmptyHeaderField("link"), QpackStaticTable.newEmptyHeaderField("location"), QpackStaticTable.newEmptyHeaderField("referer"), QpackStaticTable.newEmptyHeaderField("set-cookie"), QpackStaticTable.newHeaderField(":method", "CONNECT"), QpackStaticTable.newHeaderField(":method", "DELETE"), QpackStaticTable.newHeaderField(":method", "GET"), QpackStaticTable.newHeaderField(":method", "HEAD"), QpackStaticTable.newHeaderField(":method", "OPTIONS"), QpackStaticTable.newHeaderField(":method", "POST"), QpackStaticTable.newHeaderField(":method", "PUT"), QpackStaticTable.newHeaderField(":scheme", "http"), QpackStaticTable.newHeaderField(":scheme", "https"), QpackStaticTable.newHeaderField(":status", "103"), QpackStaticTable.newHeaderField(":status", "200"), QpackStaticTable.newHeaderField(":status", "304"), QpackStaticTable.newHeaderField(":status", "404"), QpackStaticTable.newHeaderField(":status", "503"), QpackStaticTable.newHeaderField("accept", "*/*"), QpackStaticTable.newHeaderField("accept", "application/dns-message"), QpackStaticTable.newHeaderField("accept-encoding", "gzip, deflate, br"), QpackStaticTable.newHeaderField("accept-ranges", "bytes"), QpackStaticTable.newHeaderField("access-control-allow-headers", "cache-control"), QpackStaticTable.newHeaderField("access-control-allow-headers", "content-type"), QpackStaticTable.newHeaderField("access-control-allow-origin", "*"), QpackStaticTable.newHeaderField("cache-control", "max-age=0"), QpackStaticTable.newHeaderField("cache-control", "max-age=2592000"), QpackStaticTable.newHeaderField("cache-control", "max-age=604800"), QpackStaticTable.newHeaderField("cache-control", "no-cache"), QpackStaticTable.newHeaderField("cache-control", "no-store"), QpackStaticTable.newHeaderField("cache-control", "public, max-age=31536000"), QpackStaticTable.newHeaderField("content-encoding", "br"), QpackStaticTable.newHeaderField("content-encoding", "gzip"), QpackStaticTable.newHeaderField("content-type", "application/dns-message"), QpackStaticTable.newHeaderField("content-type", "application/javascript"), QpackStaticTable.newHeaderField("content-type", "application/json"), QpackStaticTable.newHeaderField("content-type", "application/x-www-form-urlencoded"), QpackStaticTable.newHeaderField("content-type", "image/gif"), QpackStaticTable.newHeaderField("content-type", "image/jpeg"), QpackStaticTable.newHeaderField("content-type", "image/png"), QpackStaticTable.newHeaderField("content-type", "text/css"), QpackStaticTable.newHeaderField("content-type", "text/html;charset=utf-8"), QpackStaticTable.newHeaderField("content-type", "text/plain"), QpackStaticTable.newHeaderField("content-type", "text/plain;charset=utf-8"), QpackStaticTable.newHeaderField("range", "bytes=0-"), QpackStaticTable.newHeaderField("strict-transport-security", "max-age=31536000"), QpackStaticTable.newHeaderField("strict-transport-security", "max-age=31536000;includesubdomains"), QpackStaticTable.newHeaderField("strict-transport-security", "max-age=31536000;includesubdomains;preload"), QpackStaticTable.newHeaderField("vary", "accept-encoding"), QpackStaticTable.newHeaderField("vary", "origin"), QpackStaticTable.newHeaderField("x-content-type-options", "nosniff"), QpackStaticTable.newHeaderField("x-xss-protection", "1; mode=block"), QpackStaticTable.newHeaderField(":status", "100"), QpackStaticTable.newHeaderField(":status", "204"), QpackStaticTable.newHeaderField(":status", "206"), QpackStaticTable.newHeaderField(":status", "302"), QpackStaticTable.newHeaderField(":status", "400"), QpackStaticTable.newHeaderField(":status", "403"), QpackStaticTable.newHeaderField(":status", "421"), QpackStaticTable.newHeaderField(":status", "425"), QpackStaticTable.newHeaderField(":status", "500"), QpackStaticTable.newEmptyHeaderField("accept-language"), QpackStaticTable.newHeaderField("access-control-allow-credentials", "FALSE"), QpackStaticTable.newHeaderField("access-control-allow-credentials", "TRUE"), QpackStaticTable.newHeaderField("access-control-allow-headers", "*"), QpackStaticTable.newHeaderField("access-control-allow-methods", "get"), QpackStaticTable.newHeaderField("access-control-allow-methods", "get, post, options"), QpackStaticTable.newHeaderField("access-control-allow-methods", "options"), QpackStaticTable.newHeaderField("access-control-expose-headers", "content-length"), QpackStaticTable.newHeaderField("access-control-request-headers", "content-type"), QpackStaticTable.newHeaderField("access-control-request-method", "get"), QpackStaticTable.newHeaderField("access-control-request-method", "post"), QpackStaticTable.newHeaderField("alt-svc", "clear"), QpackStaticTable.newEmptyHeaderField("authorization"), QpackStaticTable.newHeaderField("content-security-policy", "script-src 'none';object-src 'none';base-uri 'none'"), QpackStaticTable.newHeaderField("early-data", "1"), QpackStaticTable.newEmptyHeaderField("expect-ct"), QpackStaticTable.newEmptyHeaderField("forwarded"), QpackStaticTable.newEmptyHeaderField("if-range"), QpackStaticTable.newEmptyHeaderField("origin"), QpackStaticTable.newHeaderField("purpose", "prefetch"), QpackStaticTable.newEmptyHeaderField("server"), QpackStaticTable.newHeaderField("timing-allow-origin", "*"), QpackStaticTable.newHeaderField("upgrade-insecure-requests", "1"), QpackStaticTable.newEmptyHeaderField("user-agent"), QpackStaticTable.newEmptyHeaderField("x-forwarded-for"), QpackStaticTable.newHeaderField("x-frame-options", "deny"), QpackStaticTable.newHeaderField("x-frame-options", "sameorigin"));
    static final int length = STATIC_TABLE.size();
    private static final CharSequenceMap<List<Integer>> STATIC_INDEX_BY_NAME = QpackStaticTable.createMap(length);

    private static QpackHeaderField newEmptyHeaderField(String name) {
        return new QpackHeaderField(AsciiString.cached(name), AsciiString.EMPTY_STRING);
    }

    private static QpackHeaderField newHeaderField(String name, String value) {
        return new QpackHeaderField(AsciiString.cached(name), AsciiString.cached(value));
    }

    static QpackHeaderField getField(int index) {
        return STATIC_TABLE.get(index);
    }

    static int getIndex(CharSequence name) {
        List index = (List)STATIC_INDEX_BY_NAME.get(name);
        if (index == null) {
            return -1;
        }
        return (Integer)index.get(0);
    }

    static int findFieldIndex(CharSequence name, CharSequence value) {
        List nameIndex = (List)STATIC_INDEX_BY_NAME.get(name);
        if (nameIndex == null) {
            return -1;
        }
        Iterator iterator = nameIndex.iterator();
        while (iterator.hasNext()) {
            int index = (Integer)iterator.next();
            QpackHeaderField field = STATIC_TABLE.get(index);
            if (!QpackUtil.equalsVariableTime(value, field.value)) continue;
            return index;
        }
        return (Integer)nameIndex.get(0) | 0x400;
    }

    private static CharSequenceMap<List<Integer>> createMap(int length) {
        CharSequenceMap<List<Integer>> mapping = new CharSequenceMap<List<Integer>>(true, UnsupportedValueConverter.instance(), length);
        for (int index = 0; index < length; ++index) {
            QpackHeaderField field = QpackStaticTable.getField(index);
            List cursor = (List)mapping.get(field.name);
            if (cursor == null) {
                ArrayList<Integer> holder = new ArrayList<Integer>(16);
                holder.add(index);
                mapping.set(field.name, (List<Integer>)holder);
                continue;
            }
            cursor.add(index);
        }
        return mapping;
    }

    private QpackStaticTable() {
    }
}

