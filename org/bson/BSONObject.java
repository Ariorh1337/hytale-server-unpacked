/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.util.Map;
import java.util.Set;

public interface BSONObject {
    public Object put(String var1, Object var2);

    public void putAll(BSONObject var1);

    public void putAll(Map var1);

    public Object get(String var1);

    public Map toMap();

    public Object removeField(String var1);

    public boolean containsField(String var1);

    public Set<String> keySet();
}

