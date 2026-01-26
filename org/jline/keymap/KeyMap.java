/*
 * Decompiled with CFR 0.152.
 */
package org.jline.keymap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;

public class KeyMap<T> {
    public static final int KEYMAP_LENGTH = 128;
    public static final long DEFAULT_AMBIGUOUS_TIMEOUT = 1000L;
    private Object[] mapping = new Object[128];
    private T anotherKey = null;
    private T unicode;
    private T nomatch;
    private long ambiguousTimeout = 1000L;
    public static final Comparator<String> KEYSEQ_COMPARATOR = (s1, s2) -> {
        int len1 = s1.length();
        int len2 = s2.length();
        int lim = Math.min(len1, len2);
        for (int k = 0; k < lim; ++k) {
            char c2;
            char c1 = s1.charAt(k);
            if (c1 == (c2 = s2.charAt(k))) continue;
            int l = len1 - len2;
            return l != 0 ? l : c1 - c2;
        }
        return len1 - len2;
    };

    public static String display(String key) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        for (int i = 0; i < key.length(); ++i) {
            char c = key.charAt(i);
            if (c < ' ') {
                sb.append('^');
                sb.append((char)(c + 65 - 1));
                continue;
            }
            if (c == '\u007f') {
                sb.append("^?");
                continue;
            }
            if (c == '^' || c == '\\') {
                sb.append('\\').append(c);
                continue;
            }
            if (c >= '\u0080') {
                sb.append(String.format("\\u%04x", c));
                continue;
            }
            sb.append(c);
        }
        sb.append("\"");
        return sb.toString();
    }

    public static String translate(String str) {
        char c;
        if (!(str.isEmpty() || (c = str.charAt(0)) != '\'' && c != '\"' || str.charAt(str.length() - 1) != c)) {
            str = str.substring(1, str.length() - 1);
        }
        StringBuilder keySeq = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            int c2;
            block25: {
                block24: {
                    c2 = str.charAt(i);
                    if (c2 != 92) break block24;
                    if (++i >= str.length()) break;
                    c2 = str.charAt(i);
                    block0 : switch (c2) {
                        case 97: {
                            c2 = 7;
                            break;
                        }
                        case 98: {
                            c2 = 8;
                            break;
                        }
                        case 100: {
                            c2 = 127;
                            break;
                        }
                        case 69: 
                        case 101: {
                            c2 = 27;
                            break;
                        }
                        case 102: {
                            c2 = 12;
                            break;
                        }
                        case 110: {
                            c2 = 10;
                            break;
                        }
                        case 114: {
                            c2 = 13;
                            break;
                        }
                        case 116: {
                            c2 = 9;
                            break;
                        }
                        case 118: {
                            c2 = 11;
                            break;
                        }
                        case 92: {
                            c2 = 92;
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
                            c2 = 0;
                            for (j = 0; j < 3 && i < str.length() && (k = Character.digit(str.charAt(i), 8)) >= 0; ++j, ++i) {
                                c2 = (char)(c2 * 8 + k);
                            }
                            --i;
                            c2 = (char)(c2 & 0xFF);
                            break;
                        }
                        case 120: {
                            int k;
                            int j;
                            ++i;
                            c2 = 0;
                            for (j = 0; j < 2 && i < str.length() && (k = Character.digit(str.charAt(i), 16)) >= 0; ++j, ++i) {
                                c2 = (char)(c2 * 16 + k);
                            }
                            --i;
                            c2 = (char)(c2 & 0xFF);
                            break;
                        }
                        case 117: {
                            int k;
                            ++i;
                            c2 = 0;
                            int j = 0;
                            while (j < 4) {
                                if (i >= str.length() || (k = Character.digit(str.charAt(i), 16)) < 0) break block0;
                                c2 = (char)(c2 * 16 + k);
                                ++j;
                                ++i;
                            }
                            break block25;
                        }
                        case 67: {
                            if (++i >= str.length()) break;
                            c2 = str.charAt(i);
                            if (c2 == 45) {
                                if (++i >= str.length()) break;
                                c2 = str.charAt(i);
                            }
                            c2 = (char)(c2 == 63 ? 127 : (char)(Character.toUpperCase((char)c2) & 0x1F));
                        }
                    }
                    break block25;
                }
                if (c2 == 94) {
                    if (++i >= str.length()) break;
                    c2 = str.charAt(i);
                    if (c2 != 94) {
                        c2 = (char)(c2 == 63 ? 127 : (char)(Character.toUpperCase((char)c2) & 0x1F));
                    }
                }
            }
            keySeq.append((char)c2);
        }
        return keySeq.toString();
    }

    public static Collection<String> range(String range) {
        char c1;
        char c0;
        String pfx;
        String[] keys = range.split("-");
        if (keys.length != 2) {
            return null;
        }
        keys[0] = KeyMap.translate(keys[0]);
        keys[1] = KeyMap.translate(keys[1]);
        if (keys[0].length() != keys[1].length()) {
            return null;
        }
        if (keys[0].length() > 1) {
            pfx = keys[0].substring(0, keys[0].length() - 1);
            if (!keys[1].startsWith(pfx)) {
                return null;
            }
        } else {
            pfx = "";
        }
        if ((c0 = keys[0].charAt(keys[0].length() - 1)) > (c1 = keys[1].charAt(keys[1].length() - 1))) {
            return null;
        }
        ArrayList<String> seqs = new ArrayList<String>();
        for (char c = c0; c <= c1; c = (char)(c + '\u0001')) {
            seqs.add(pfx + c);
        }
        return seqs;
    }

    public static String esc() {
        return "\u001b";
    }

    public static String alt(char c) {
        return "\u001b" + c;
    }

    public static String alt(String c) {
        return "\u001b" + c;
    }

    public static String del() {
        return "\u007f";
    }

    public static String ctrl(char key) {
        return key == '?' ? KeyMap.del() : Character.toString((char)(Character.toUpperCase(key) & 0x1F));
    }

    public static String key(Terminal terminal, InfoCmp.Capability capability) {
        return Curses.tputs(terminal.getStringCapability(capability), new Object[0]);
    }

    public T getUnicode() {
        return this.unicode;
    }

    public void setUnicode(T unicode) {
        this.unicode = unicode;
    }

    public T getNomatch() {
        return this.nomatch;
    }

    public void setNomatch(T nomatch) {
        this.nomatch = nomatch;
    }

    public long getAmbiguousTimeout() {
        return this.ambiguousTimeout;
    }

    public void setAmbiguousTimeout(long ambiguousTimeout) {
        this.ambiguousTimeout = ambiguousTimeout;
    }

    public T getAnotherKey() {
        return this.anotherKey;
    }

    public Map<String, T> getBoundKeys() {
        TreeMap bound = new TreeMap(KEYSEQ_COMPARATOR);
        KeyMap.doGetBoundKeys(this, "", bound);
        return bound;
    }

    private static <T> void doGetBoundKeys(KeyMap<T> keyMap, String prefix, Map<String, T> bound) {
        if (keyMap.anotherKey != null) {
            bound.put(prefix, keyMap.anotherKey);
        }
        for (int c = 0; c < keyMap.mapping.length; ++c) {
            if (keyMap.mapping[c] instanceof KeyMap) {
                KeyMap.doGetBoundKeys((KeyMap)keyMap.mapping[c], prefix + (char)c, bound);
                continue;
            }
            if (keyMap.mapping[c] == null) continue;
            bound.put(prefix + (char)c, keyMap.mapping[c]);
        }
    }

    public T getBound(CharSequence keySeq, int[] remaining) {
        remaining[0] = -1;
        if (keySeq != null && keySeq.length() > 0) {
            char c = keySeq.charAt(0);
            if (c >= this.mapping.length) {
                remaining[0] = Character.codePointCount(keySeq, 0, keySeq.length());
                return null;
            }
            if (this.mapping[c] instanceof KeyMap) {
                CharSequence sub = keySeq.subSequence(1, keySeq.length());
                return ((KeyMap)this.mapping[c]).getBound(sub, remaining);
            }
            if (this.mapping[c] != null) {
                remaining[0] = keySeq.length() - 1;
                return (T)this.mapping[c];
            }
            remaining[0] = keySeq.length();
            return this.anotherKey;
        }
        return this.anotherKey;
    }

    public T getBound(CharSequence keySeq) {
        int[] remaining = new int[1];
        T res = this.getBound(keySeq, remaining);
        return (T)(remaining[0] <= 0 ? res : null);
    }

    public void bindIfNotBound(T function, CharSequence keySeq) {
        if (function != null && keySeq != null) {
            KeyMap.bind(this, keySeq, function, true);
        }
    }

    public void bind(T function, CharSequence ... keySeqs) {
        for (CharSequence keySeq : keySeqs) {
            this.bind(function, keySeq);
        }
    }

    public void bind(T function, Iterable<? extends CharSequence> keySeqs) {
        for (CharSequence charSequence : keySeqs) {
            this.bind(function, charSequence);
        }
    }

    public void bind(T function, CharSequence keySeq) {
        if (keySeq != null) {
            if (function == null) {
                this.unbind(keySeq);
            } else {
                KeyMap.bind(this, keySeq, function, false);
            }
        }
    }

    public void unbind(CharSequence ... keySeqs) {
        for (CharSequence keySeq : keySeqs) {
            this.unbind(keySeq);
        }
    }

    public void unbind(CharSequence keySeq) {
        if (keySeq != null) {
            KeyMap.unbind(this, keySeq);
        }
    }

    private static <T> T unbind(KeyMap<T> map, CharSequence keySeq) {
        KeyMap prev = null;
        if (keySeq != null && keySeq.length() > 0) {
            for (int i = 0; i < keySeq.length() - 1; ++i) {
                char c = keySeq.charAt(i);
                if (c > map.mapping.length) {
                    return null;
                }
                if (!(map.mapping[c] instanceof KeyMap)) {
                    return null;
                }
                prev = map;
                map = (KeyMap)map.mapping[c];
            }
            char c = keySeq.charAt(keySeq.length() - 1);
            if (c > map.mapping.length) {
                return null;
            }
            if (map.mapping[c] instanceof KeyMap) {
                KeyMap sub = (KeyMap)map.mapping[c];
                T res = sub.anotherKey;
                sub.anotherKey = null;
                return res;
            }
            Object res = map.mapping[c];
            map.mapping[c] = null;
            int nb = 0;
            for (int i = 0; i < map.mapping.length; ++i) {
                if (map.mapping[i] == null) continue;
                ++nb;
            }
            if (nb == 0 && prev != null) {
                prev.mapping[keySeq.charAt((int)(keySeq.length() - 2))] = map.anotherKey;
            }
            return (T)res;
        }
        return null;
    }

    private static <T> void bind(KeyMap<T> map, CharSequence keySeq, T function, boolean onlyIfNotBound) {
        if (keySeq != null && keySeq.length() > 0) {
            for (int i = 0; i < keySeq.length(); ++i) {
                char c = keySeq.charAt(i);
                if (c >= map.mapping.length) {
                    return;
                }
                if (i < keySeq.length() - 1) {
                    if (!(map.mapping[c] instanceof KeyMap)) {
                        KeyMap<T> m = new KeyMap<T>();
                        m.anotherKey = map.mapping[c];
                        map.mapping[c] = m;
                    }
                    map = (KeyMap)map.mapping[c];
                    continue;
                }
                if (map.mapping[c] instanceof KeyMap) {
                    ((KeyMap)map.mapping[c]).anotherKey = function;
                    continue;
                }
                Object op = map.mapping[c];
                if (onlyIfNotBound && op != null) continue;
                map.mapping[c] = function;
            }
        }
    }
}

