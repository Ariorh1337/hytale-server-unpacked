/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jline.reader.Completer;

public interface ScriptEngine {
    public String getEngineName();

    public Collection<String> getExtensions();

    public Completer getScriptCompleter();

    public boolean hasVariable(String var1);

    public void put(String var1, Object var2);

    public Object get(String var1);

    default public Map<String, Object> find() {
        return this.find(null);
    }

    public Map<String, Object> find(String var1);

    public void del(String ... var1);

    public String toJson(Object var1);

    public String toString(Object var1);

    public Map<String, Object> toMap(Object var1);

    default public Object deserialize(String value) {
        return this.deserialize(value, null);
    }

    public Object deserialize(String var1, String var2);

    public List<String> getSerializationFormats();

    public List<String> getDeserializationFormats();

    public void persist(Path var1, Object var2);

    public void persist(Path var1, Object var2, String var3);

    public Object execute(String var1) throws Exception;

    default public Object execute(Path script) throws Exception {
        return this.execute(script.toFile(), null);
    }

    default public Object execute(File script) throws Exception {
        return this.execute(script, null);
    }

    default public Object execute(Path script, Object[] args) throws Exception {
        return this.execute(script.toFile(), args);
    }

    public Object execute(File var1, Object[] var2) throws Exception;

    public Object execute(Object var1, Object ... var2);
}

