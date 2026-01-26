/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import org.jline.reader.Binding;
import org.jline.reader.LineReader;
import org.jline.reader.Macro;
import org.jline.reader.Reference;
import org.jline.terminal.Terminal;
import org.jline.utils.Log;

public final class InputRC {
    private final LineReader reader;

    public static void configure(LineReader reader, URL url) throws IOException {
        try (InputStream is = url.openStream();){
            InputRC.configure(reader, is);
        }
    }

    public static void configure(LineReader reader, InputStream is) throws IOException {
        try (InputStreamReader r = new InputStreamReader(is);){
            InputRC.configure(reader, r);
        }
    }

    public static void configure(LineReader reader, Reader r) throws IOException {
        BufferedReader br = r instanceof BufferedReader ? (BufferedReader)r : new BufferedReader(r);
        Terminal terminal = reader.getTerminal();
        if ("dumb".equals(terminal.getType()) || "dumb-color".equals(terminal.getType())) {
            reader.getVariables().putIfAbsent("editing-mode", "dumb");
        } else {
            reader.getVariables().putIfAbsent("editing-mode", "emacs");
        }
        reader.setKeyMap("main");
        new InputRC(reader).parse(br);
        if ("vi".equals(reader.getVariable("editing-mode"))) {
            reader.getKeyMaps().put("main", reader.getKeyMaps().get("viins"));
        } else if ("emacs".equals(reader.getVariable("editing-mode"))) {
            reader.getKeyMaps().put("main", reader.getKeyMaps().get("emacs"));
        } else if ("dumb".equals(reader.getVariable("editing-mode"))) {
            reader.getKeyMaps().put("main", reader.getKeyMaps().get("dumb"));
        }
    }

    private InputRC(LineReader reader) {
        this.reader = reader;
    }

    private void parse(BufferedReader br) throws IOException, IllegalArgumentException {
        String line;
        boolean parsing = true;
        ArrayList<Boolean> ifsStack = new ArrayList<Boolean>();
        while ((line = br.readLine()) != null) {
            try {
                String val;
                if ((line = line.trim()).length() == 0 || line.charAt(0) == '#') continue;
                int i = 0;
                if (line.charAt(i) == '$') {
                    ++i;
                    while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        ++i;
                    }
                    int s = i;
                    while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        ++i;
                    }
                    String cmd = line.substring(s, i);
                    while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        ++i;
                    }
                    s = i;
                    while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        ++i;
                    }
                    String args = line.substring(s, i);
                    if ("if".equalsIgnoreCase(cmd)) {
                        ifsStack.add(parsing);
                        if (!parsing || args.startsWith("term=")) continue;
                        if (args.startsWith("mode=")) {
                            String mode = (String)this.reader.getVariable("editing-mode");
                            parsing = args.substring("mode=".length()).equalsIgnoreCase(mode);
                            continue;
                        }
                        parsing = args.equalsIgnoreCase(this.reader.getAppName());
                        continue;
                    }
                    if ("else".equalsIgnoreCase(cmd)) {
                        if (ifsStack.isEmpty()) {
                            throw new IllegalArgumentException("$else found without matching $if");
                        }
                        boolean invert = true;
                        Iterator iterator = ifsStack.iterator();
                        while (iterator.hasNext()) {
                            boolean b = (Boolean)iterator.next();
                            if (b) continue;
                            invert = false;
                            break;
                        }
                        if (!invert) continue;
                        parsing = !parsing;
                        continue;
                    }
                    if ("endif".equalsIgnoreCase(cmd)) {
                        if (ifsStack.isEmpty()) {
                            throw new IllegalArgumentException("endif found without matching $if");
                        }
                        parsing = (Boolean)ifsStack.remove(ifsStack.size() - 1);
                        continue;
                    }
                    if (!"include".equalsIgnoreCase(cmd)) continue;
                }
                if (!parsing) continue;
                if (line.charAt(i++) == '\"') {
                    boolean esc = false;
                    while (true) {
                        if (i >= line.length()) {
                            throw new IllegalArgumentException("Missing closing quote on line '" + line + "'");
                        }
                        if (esc) {
                            esc = false;
                        } else if (line.charAt(i) == '\\') {
                            esc = true;
                        } else if (line.charAt(i) == '\"') break;
                        ++i;
                    }
                }
                while (i < line.length() && line.charAt(i) != ':' && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                    ++i;
                }
                String keySeq = line.substring(0, i);
                boolean equivalency = i + 1 < line.length() && line.charAt(i) == ':' && line.charAt(i + 1) == '=';
                ++i;
                if (equivalency) {
                    ++i;
                }
                if (keySeq.equalsIgnoreCase("set")) {
                    while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        ++i;
                    }
                    int s = i;
                    while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        ++i;
                    }
                    String key = line.substring(s, i);
                    while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                        ++i;
                    }
                    s = i;
                    while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                        ++i;
                    }
                    val = line.substring(s, i);
                    InputRC.setVar(this.reader, key, val);
                    continue;
                }
                while (i < line.length() && (line.charAt(i) == ' ' || line.charAt(i) == '\t')) {
                    ++i;
                }
                int start = i;
                if (i < line.length() && (line.charAt(i) == '\'' || line.charAt(i) == '\"')) {
                    char delim = line.charAt(i++);
                    boolean esc = false;
                    while (i < line.length()) {
                        if (esc) {
                            esc = false;
                        } else if (line.charAt(i) == '\\') {
                            esc = true;
                        } else if (line.charAt(i) == delim) break;
                        ++i;
                    }
                }
                while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
                    ++i;
                }
                val = line.substring(Math.min(start, line.length()), Math.min(i, line.length()));
                if (keySeq.charAt(0) == '\"') {
                    keySeq = InputRC.translateQuoted(keySeq);
                } else {
                    String keyName = keySeq.lastIndexOf(45) > 0 ? keySeq.substring(keySeq.lastIndexOf(45) + 1) : keySeq;
                    char key = InputRC.getKeyFromName(keyName);
                    keyName = keySeq.toLowerCase();
                    keySeq = "";
                    if (keyName.contains("meta-") || keyName.contains("m-")) {
                        keySeq = keySeq + "\u001b";
                    }
                    if (keyName.contains("control-") || keyName.contains("c-") || keyName.contains("ctrl-")) {
                        key = (char)(Character.toUpperCase(key) & 0x1F);
                    }
                    keySeq = keySeq + key;
                }
                if (val.length() > 0 && (val.charAt(0) == '\'' || val.charAt(0) == '\"')) {
                    this.reader.getKeys().bind((Binding)new Macro(InputRC.translateQuoted(val)), (CharSequence)keySeq);
                    continue;
                }
                this.reader.getKeys().bind((Binding)new Reference(val), (CharSequence)keySeq);
            }
            catch (IllegalArgumentException e) {
                Log.warn("Unable to parse user configuration: ", e);
            }
        }
    }

    private static String translateQuoted(String keySeq) {
        String str = keySeq.substring(1, keySeq.length() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            int c = str.charAt(i);
            if (c == 92) {
                boolean meta;
                boolean ctrl = str.regionMatches(i, "\\C-", 0, 3) || str.regionMatches(i, "\\M-\\C-", 0, 6);
                if ((i += ((meta = str.regionMatches(i, "\\M-", 0, 3) || str.regionMatches(i, "\\C-\\M-", 0, 6)) ? 3 : 0) + (ctrl ? 3 : 0) + (!meta && !ctrl ? 1 : 0)) >= str.length()) break;
                c = str.charAt(i);
                if (meta) {
                    sb.append("\u001b");
                }
                if (ctrl) {
                    char c2 = (char)(c = (char)(c == 63 ? 127 : (char)(Character.toUpperCase((char)c) & 0x1F)));
                }
                if (!meta && !ctrl) {
                    switch (c) {
                        case 97: {
                            c = 7;
                            break;
                        }
                        case 98: {
                            c = 8;
                            break;
                        }
                        case 100: {
                            c = 127;
                            break;
                        }
                        case 101: {
                            c = 27;
                            break;
                        }
                        case 102: {
                            c = 12;
                            break;
                        }
                        case 110: {
                            c = 10;
                            break;
                        }
                        case 114: {
                            c = 13;
                            break;
                        }
                        case 116: {
                            c = 9;
                            break;
                        }
                        case 118: {
                            c = 11;
                            break;
                        }
                        case 92: {
                            c = 92;
                            break;
                        }
                        case 48: 
                        case 49: 
                        case 50: 
                        case 51: 
                        case 52: 
                        case 53: 
                        case 54: 
                        case 55: {
                            int k;
                            int j;
                            c = 0;
                            for (j = 0; j < 3 && i < str.length() && (k = Character.digit(str.charAt(i), 8)) >= 0; ++j, ++i) {
                                c = (char)(c * 8 + k);
                            }
                            c = (char)(c & 0xFF);
                            break;
                        }
                        case 120: {
                            int k;
                            int j;
                            ++i;
                            c = 0;
                            for (j = 0; j < 2 && i < str.length() && (k = Character.digit(str.charAt(i), 16)) >= 0; ++j, ++i) {
                                c = (char)(c * 16 + k);
                            }
                            c = (char)(c & 0xFF);
                            break;
                        }
                        case 117: {
                            int k;
                            int j;
                            ++i;
                            c = 0;
                            for (j = 0; j < 4 && i < str.length() && (k = Character.digit(str.charAt(i), 16)) >= 0; ++j, ++i) {
                                c = (char)(c * 16 + k);
                            }
                            break;
                        }
                    }
                }
                sb.append((char)c);
                continue;
            }
            sb.append((char)c);
        }
        return sb.toString();
    }

    private static char getKeyFromName(String name) {
        if ("DEL".equalsIgnoreCase(name) || "Rubout".equalsIgnoreCase(name)) {
            return '\u007f';
        }
        if ("ESC".equalsIgnoreCase(name) || "Escape".equalsIgnoreCase(name)) {
            return '\u001b';
        }
        if ("LFD".equalsIgnoreCase(name) || "NewLine".equalsIgnoreCase(name)) {
            return '\n';
        }
        if ("RET".equalsIgnoreCase(name) || "Return".equalsIgnoreCase(name)) {
            return '\r';
        }
        if ("SPC".equalsIgnoreCase(name) || "Space".equalsIgnoreCase(name)) {
            return ' ';
        }
        if ("Tab".equalsIgnoreCase(name)) {
            return '\t';
        }
        return name.charAt(0);
    }

    static void setVar(LineReader reader, String key, String val) {
        if ("keymap".equalsIgnoreCase(key)) {
            reader.setKeyMap(val);
            return;
        }
        for (LineReader.Option option : LineReader.Option.values()) {
            if (!option.name().toLowerCase(Locale.ENGLISH).replace('_', '-').equals(val)) continue;
            if ("on".equalsIgnoreCase(val)) {
                reader.setOpt(option);
            } else if ("off".equalsIgnoreCase(val)) {
                reader.unsetOpt(option);
            }
            return;
        }
        reader.setVariable(key, val);
    }
}

