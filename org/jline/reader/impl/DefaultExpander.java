/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl;

import java.util.ListIterator;
import org.jline.reader.Expander;
import org.jline.reader.History;

public class DefaultExpander
implements Expander {
    @Override
    public String expandHistory(History history, String line) {
        boolean inQuote = false;
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        int unicode = 0;
        block16: for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (unicode > 0) {
                escaped = --unicode >= 0;
                sb.append(c);
                continue;
            }
            if (escaped) {
                if (c == 'u') {
                    unicode = 4;
                } else {
                    escaped = false;
                }
                sb.append(c);
                continue;
            }
            if (c == '\'') {
                inQuote = !inQuote;
                sb.append(c);
                continue;
            }
            if (inQuote) {
                sb.append(c);
                continue;
            }
            switch (c) {
                case '\\': {
                    escaped = true;
                    sb.append(c);
                    continue block16;
                }
                case '!': {
                    if (i + 1 < line.length()) {
                        c = line.charAt(++i);
                        boolean neg = false;
                        String rep = null;
                        switch (c) {
                            case '!': {
                                if (history.size() == 0) {
                                    throw new IllegalArgumentException("!!: event not found");
                                }
                                rep = history.get(history.index() - 1);
                                break;
                            }
                            case '#': {
                                sb.append(sb.toString());
                                break;
                            }
                            case '?': {
                                int i1 = line.indexOf(63, i + 1);
                                if (i1 < 0) {
                                    i1 = line.length();
                                }
                                String sc = line.substring(i + 1, i1);
                                i = i1;
                                int idx = this.searchBackwards(history, sc, history.index(), false);
                                if (idx < 0) {
                                    throw new IllegalArgumentException("!?" + sc + ": event not found");
                                }
                                rep = history.get(idx);
                                break;
                            }
                            case '$': {
                                if (history.size() == 0) {
                                    throw new IllegalArgumentException("!$: event not found");
                                }
                                String previous = history.get(history.index() - 1).trim();
                                int lastSpace = previous.lastIndexOf(32);
                                if (lastSpace != -1) {
                                    rep = previous.substring(lastSpace + 1);
                                    break;
                                }
                                rep = previous;
                                break;
                            }
                            case '\t': 
                            case ' ': {
                                sb.append('!');
                                sb.append(c);
                                break;
                            }
                            case '-': {
                                neg = true;
                            }
                            case '0': 
                            case '1': 
                            case '2': 
                            case '3': 
                            case '4': 
                            case '5': 
                            case '6': 
                            case '7': 
                            case '8': 
                            case '9': {
                                int idx;
                                int i1 = ++i;
                                while (i < line.length() && (c = line.charAt(i)) >= '0' && c <= '9') {
                                    ++i;
                                }
                                try {
                                    idx = Integer.parseInt(line.substring(i1, i));
                                }
                                catch (NumberFormatException e) {
                                    throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                                }
                                if (neg && idx > 0 && idx <= history.size()) {
                                    rep = history.get(history.index() - idx);
                                    break;
                                }
                                if (!neg && idx > history.index() - history.size() && idx <= history.index()) {
                                    rep = history.get(idx - 1);
                                    break;
                                }
                                throw new IllegalArgumentException((neg ? "!-" : "!") + line.substring(i1, i) + ": event not found");
                            }
                            default: {
                                String ss = line.substring(i);
                                i = line.length();
                                int idx = this.searchBackwards(history, ss, history.index(), true);
                                if (idx < 0) {
                                    throw new IllegalArgumentException("!" + ss + ": event not found");
                                }
                                rep = history.get(idx);
                            }
                        }
                        if (rep == null) continue block16;
                        sb.append(rep);
                        continue block16;
                    }
                    sb.append(c);
                    continue block16;
                }
                case '^': {
                    if (i == 0) {
                        int i1 = line.indexOf(94, i + 1);
                        int i2 = line.indexOf(94, i1 + 1);
                        if (i2 < 0) {
                            i2 = line.length();
                        }
                        if (i1 > 0 && i2 > 0) {
                            String s1 = line.substring(i + 1, i1);
                            String s2 = line.substring(i1 + 1, i2);
                            String s = history.get(history.index() - 1).replace(s1, s2);
                            sb.append(s);
                            i = i2 + 1;
                            continue block16;
                        }
                    }
                    sb.append(c);
                    continue block16;
                }
                default: {
                    sb.append(c);
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String expandVar(String word) {
        return word;
    }

    protected int searchBackwards(History history, String searchTerm, int startIndex, boolean startsWith) {
        ListIterator<History.Entry> it = history.iterator(startIndex);
        while (it.hasPrevious()) {
            History.Entry e = it.previous();
            if (!(startsWith ? e.line().startsWith(searchTerm) : e.line().contains(searchTerm))) continue;
            return e.index();
        }
        return -1;
    }
}

