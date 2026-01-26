/*
 * Decompiled with CFR 0.152.
 */
package org.jline.console;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.jline.console.ArgDesc;
import org.jline.utils.AttributedString;

public class CmdDesc {
    private List<AttributedString> mainDesc;
    private List<ArgDesc> argsDesc;
    private TreeMap<String, List<AttributedString>> optsDesc;
    private Pattern errorPattern;
    private int errorIndex = -1;
    private boolean valid = true;
    private boolean command = false;
    private boolean subcommand = false;
    private boolean highlighted = true;

    public CmdDesc() {
        this.command = false;
    }

    public CmdDesc(boolean valid) {
        this.valid = valid;
    }

    public CmdDesc(List<ArgDesc> argsDesc) {
        this(new ArrayList<AttributedString>(), argsDesc, new HashMap<String, List<AttributedString>>());
    }

    public CmdDesc(List<ArgDesc> argsDesc, Map<String, List<AttributedString>> optsDesc) {
        this(new ArrayList<AttributedString>(), argsDesc, optsDesc);
    }

    public CmdDesc(List<AttributedString> mainDesc, List<ArgDesc> argsDesc, Map<String, List<AttributedString>> optsDesc) {
        this.argsDesc = new ArrayList<ArgDesc>(argsDesc);
        this.optsDesc = new TreeMap<String, List<AttributedString>>(optsDesc);
        if (mainDesc.isEmpty() && optsDesc.containsKey("main")) {
            this.mainDesc = new ArrayList<AttributedString>((Collection)optsDesc.get("main"));
            this.optsDesc.remove("main");
        } else {
            this.mainDesc = new ArrayList<AttributedString>(mainDesc);
        }
        this.command = true;
    }

    public boolean isValid() {
        return this.valid;
    }

    public boolean isCommand() {
        return this.command;
    }

    public void setSubcommand(boolean subcommand) {
        this.subcommand = subcommand;
    }

    public boolean isSubcommand() {
        return this.subcommand;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return this.highlighted;
    }

    public CmdDesc mainDesc(List<AttributedString> mainDesc) {
        this.mainDesc = new ArrayList<AttributedString>(mainDesc);
        return this;
    }

    public void setMainDesc(List<AttributedString> mainDesc) {
        this.mainDesc = new ArrayList<AttributedString>(mainDesc);
    }

    public List<AttributedString> getMainDesc() {
        return this.mainDesc;
    }

    public TreeMap<String, List<AttributedString>> getOptsDesc() {
        return this.optsDesc;
    }

    public void setErrorPattern(Pattern errorPattern) {
        this.errorPattern = errorPattern;
    }

    public Pattern getErrorPattern() {
        return this.errorPattern;
    }

    public void setErrorIndex(int errorIndex) {
        this.errorIndex = errorIndex;
    }

    public int getErrorIndex() {
        return this.errorIndex;
    }

    public List<ArgDesc> getArgsDesc() {
        return this.argsDesc;
    }

    public boolean optionWithValue(String option) {
        for (String key : this.optsDesc.keySet()) {
            if (!key.matches("(^|.*\\s)" + option + "($|=.*|\\s.*)")) continue;
            return key.contains("=");
        }
        return false;
    }

    public AttributedString optionDescription(String key) {
        return !this.optsDesc.get(key).isEmpty() ? this.optsDesc.get(key).get(0) : new AttributedString("");
    }
}

