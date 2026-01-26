/*
 * Decompiled with CFR 0.152.
 */
package org.jline.style;

import java.util.Map;
import javax.annotation.Nullable;

public interface StyleSource {
    @Nullable
    public String get(String var1, String var2);

    public void set(String var1, String var2, String var3);

    public void remove(String var1);

    public void remove(String var1, String var2);

    public void clear();

    public Iterable<String> groups();

    public Map<String, String> styles(String var1);
}

