/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.mime;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bouncycastle.mime.Headers;

public abstract class MimeWriter {
    protected final Headers headers;

    protected MimeWriter(Headers headers) {
        this.headers = headers;
    }

    public Headers getHeaders() {
        return this.headers;
    }

    public abstract OutputStream getContentStream() throws IOException;

    protected static List<String> mapToLines(Map<String, String> map) {
        ArrayList<String> arrayList = new ArrayList<String>(map.size());
        for (String string : map.keySet()) {
            arrayList.add(string + ": " + map.get(string));
        }
        return arrayList;
    }
}

