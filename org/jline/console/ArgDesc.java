/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.util.ArrayList;
import java.util.List;
import org.jline.utils.AttributedString;

public class ArgDesc {
    private final String name;
    private final List<AttributedString> description;

    public ArgDesc(String name) {
        this(name, new ArrayList<AttributedString>());
    }

    public ArgDesc(String name, List<AttributedString> description) {
        if (name.contains("\t") || name.contains(" ")) {
            throw new IllegalArgumentException("Bad argument name: " + name);
        }
        this.name = name;
        this.description = new ArrayList<AttributedString>(description);
    }

    public String getName() {
        return this.name;
    }

    public List<AttributedString> getDescription() {
        return this.description;
    }

    public static List<ArgDesc> doArgNames(List<String> names) {
        ArrayList<ArgDesc> out = new ArrayList<ArgDesc>();
        for (String n : names) {
            out.add(new ArgDesc(n));
        }
        return out;
    }
}

