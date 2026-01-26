/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.util;

import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;

public class PrivateKeyAnnotator {
    public static AnnotatedPrivateKey annotate(PrivateKey privateKey, String string) {
        return new AnnotatedPrivateKey(privateKey, string);
    }

    public static AnnotatedPrivateKey annotate(PrivateKey privateKey, Map<String, Object> map) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>(map);
        return new AnnotatedPrivateKey(privateKey, Collections.unmodifiableMap(hashMap));
    }
}

