/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.io.Flushable;
import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayDeque;

public final class Curses {
    private static final Object[] sv = new Object[26];
    private static final Object[] dv = new Object[26];
    private static final int IFTE_NONE = 0;
    private static final int IFTE_IF = 1;
    private static final int IFTE_THEN = 2;
    private static final int IFTE_ELSE = 3;

    private Curses() {
    }

    public static String tputs(String cap, Object ... params) {
        if (cap != null) {
            StringWriter sw = new StringWriter();
            Curses.tputs(sw, cap, params);
            return sw.toString();
        }
        return null;
    }

    public static void tputs(Appendable out, String str, Object ... params) {
        try {
            Curses.doTputs(out, str, params);
        }
        catch (Exception e) {
            throw new IOError(e);
        }
    }

    private static void doTputs(Appendable out, String str, Object ... params) throws IOException {
        int index = 0;
        int length = str.length();
        int ifte = 0;
        boolean exec = true;
        ArrayDeque<Object> stack = new ArrayDeque<Object>();
        block54: while (index < length) {
            char ch = str.charAt(index++);
            block1 : switch (ch) {
                case '\\': {
                    ch = str.charAt(index++);
                    if (ch >= '0' && ch <= '7') {
                        int val = ch - 48;
                        for (int i = 0; i < 2; ++i) {
                            if ((ch = str.charAt(index++)) < '0' || ch > '7') {
                                throw new IllegalStateException();
                            }
                            val = val * 8 + (ch - 48);
                        }
                        out.append((char)val);
                        break;
                    }
                    switch (ch) {
                        case 'E': 
                        case 'e': {
                            if (!exec) continue block54;
                            out.append('\u001b');
                            break block1;
                        }
                        case 'n': {
                            out.append('\n');
                            break block1;
                        }
                        case 'r': {
                            if (!exec) continue block54;
                            out.append('\r');
                            break block1;
                        }
                        case 't': {
                            if (!exec) continue block54;
                            out.append('\t');
                            break block1;
                        }
                        case 'b': {
                            if (!exec) continue block54;
                            out.append('\b');
                            break block1;
                        }
                        case 'f': {
                            if (!exec) continue block54;
                            out.append('\f');
                            break block1;
                        }
                        case 's': {
                            if (!exec) continue block54;
                            out.append(' ');
                            break block1;
                        }
                        case ':': 
                        case '\\': 
                        case '^': {
                            if (!exec) continue block54;
                            out.append(ch);
                            break block1;
                        }
                    }
                    throw new IllegalArgumentException();
                }
                case '^': {
                    ch = str.charAt(index++);
                    if (!exec) continue block54;
                    out.append((char)(ch - 64));
                    break;
                }
                case '%': {
                    ch = str.charAt(index++);
                    switch (ch) {
                        case '%': {
                            if (!exec) continue block54;
                            out.append('%');
                            break;
                        }
                        case 'p': {
                            ch = str.charAt(index++);
                            if (!exec) continue block54;
                            stack.push(params[ch - 49]);
                            break;
                        }
                        case 'P': {
                            ch = str.charAt(index++);
                            if (ch >= 'a' && ch <= 'z') {
                                if (!exec) continue block54;
                                Curses.dv[ch - 97] = stack.pop();
                                break;
                            }
                            if (ch >= 'A' && ch <= 'Z') {
                                if (!exec) continue block54;
                                Curses.sv[ch - 65] = stack.pop();
                                break;
                            }
                            throw new IllegalArgumentException();
                        }
                        case 'g': {
                            ch = str.charAt(index++);
                            if (ch >= 'a' && ch <= 'z') {
                                if (!exec) continue block54;
                                stack.push(dv[ch - 97]);
                                break;
                            }
                            if (ch >= 'A' && ch <= 'Z') {
                                if (!exec) continue block54;
                                stack.push(sv[ch - 65]);
                                break;
                            }
                            throw new IllegalArgumentException();
                        }
                        case '\'': {
                            ch = str.charAt(index++);
                            if (exec) {
                                stack.push(Integer.valueOf(ch));
                            }
                            if ((ch = str.charAt(index++)) == '\'') continue block54;
                            throw new IllegalArgumentException();
                        }
                        case '{': {
                            int start = index;
                            while (str.charAt(index++) != '}') {
                            }
                            if (!exec) continue block54;
                            int v = Integer.parseInt(str.substring(start, index - 1));
                            stack.push(v);
                            break;
                        }
                        case 'l': {
                            if (!exec) continue block54;
                            stack.push(stack.pop().toString().length());
                            break;
                        }
                        case '+': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 + v2);
                            break;
                        }
                        case '-': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 - v2);
                            break;
                        }
                        case '*': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 * v2);
                            break;
                        }
                        case '/': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 / v2);
                            break;
                        }
                        case 'm': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 % v2);
                            break;
                        }
                        case '&': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 & v2);
                            break;
                        }
                        case '|': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 | v2);
                            break;
                        }
                        case '^': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 ^ v2);
                            break;
                        }
                        case '=': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 == v2);
                            break;
                        }
                        case '>': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 > v2);
                            break;
                        }
                        case '<': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 < v2);
                            break;
                        }
                        case 'A': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 != 0 && v2 != 0);
                            break;
                        }
                        case '!': {
                            if (!exec) continue block54;
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 == 0);
                            break;
                        }
                        case '~': {
                            if (!exec) continue block54;
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(~v1);
                            break;
                        }
                        case 'O': {
                            if (!exec) continue block54;
                            int v2 = Curses.toInteger(stack.pop());
                            int v1 = Curses.toInteger(stack.pop());
                            stack.push(v1 != 0 || v2 != 0);
                            break;
                        }
                        case '?': {
                            if (ifte != 0) {
                                throw new IllegalArgumentException();
                            }
                            ifte = 1;
                            break;
                        }
                        case 't': {
                            if (ifte != 1 && ifte != 3) {
                                throw new IllegalArgumentException();
                            }
                            ifte = 2;
                            exec = Curses.toInteger(stack.pop()) != 0;
                            break;
                        }
                        case 'e': {
                            if (ifte != 2) {
                                throw new IllegalArgumentException();
                            }
                            ifte = 3;
                            exec = !exec;
                            break;
                        }
                        case ';': {
                            if (ifte == 0 || ifte == 1) {
                                throw new IllegalArgumentException();
                            }
                            ifte = 0;
                            exec = true;
                            break;
                        }
                        case 'i': {
                            if (params.length >= 1) {
                                params[0] = Curses.toInteger(params[0]) + 1;
                            }
                            if (params.length < 2) continue block54;
                            params[1] = Curses.toInteger(params[1]) + 1;
                            break;
                        }
                        case 'd': {
                            out.append(Integer.toString(Curses.toInteger(stack.pop())));
                            break;
                        }
                        default: {
                            String res;
                            if (ch == ':') {
                                ch = str.charAt(index++);
                            }
                            boolean alternate = false;
                            boolean left = false;
                            boolean space = false;
                            boolean plus = false;
                            int width = 0;
                            int prec = -1;
                            while ("-+# ".indexOf(ch) >= 0) {
                                switch (ch) {
                                    case '-': {
                                        left = true;
                                        break;
                                    }
                                    case '+': {
                                        plus = true;
                                        break;
                                    }
                                    case '#': {
                                        alternate = true;
                                        break;
                                    }
                                    case ' ': {
                                        space = true;
                                    }
                                }
                                ch = str.charAt(index++);
                            }
                            if ("123456789".indexOf(ch) >= 0) {
                                do {
                                    width = width * 10 + (ch - 48);
                                } while ("0123456789".indexOf(ch = str.charAt(index++)) >= 0);
                            }
                            if (ch == '.') {
                                prec = 0;
                                ch = str.charAt(index++);
                            }
                            if ("0123456789".indexOf(ch) >= 0) {
                                do {
                                    prec = prec * 10 + (ch - 48);
                                } while ("0123456789".indexOf(ch = str.charAt(index++)) >= 0);
                            }
                            if ("cdoxXs".indexOf(ch) < 0) {
                                throw new IllegalArgumentException();
                            }
                            char cnv = ch;
                            if (!exec) continue block54;
                            if (cnv == 's') {
                                res = (String)stack.pop();
                                if (prec >= 0) {
                                    res = res.substring(0, prec);
                                }
                            } else {
                                int p = Curses.toInteger(stack.pop());
                                StringBuilder fmt = new StringBuilder(16);
                                fmt.append('%');
                                if (alternate) {
                                    fmt.append('#');
                                }
                                if (plus) {
                                    fmt.append('+');
                                }
                                if (space) {
                                    fmt.append(' ');
                                }
                                if (prec >= 0) {
                                    fmt.append('0');
                                    fmt.append(prec);
                                }
                                fmt.append(cnv);
                                res = String.format(fmt.toString(), p);
                            }
                            if (width > res.length()) {
                                res = String.format("%" + (left ? "-" : "") + width + "s", res);
                            }
                            out.append(res);
                            break;
                        }
                    }
                    continue block54;
                }
                case '$': {
                    if (index < length && str.charAt(index) == '<') {
                        int nb = 0;
                        while ((ch = str.charAt(++index)) != '>') {
                            if (ch >= '0' && ch <= '9') {
                                nb = nb * 10 + (ch - 48);
                                continue;
                            }
                            if (ch == '*' || ch != '/') continue;
                        }
                        ++index;
                        try {
                            if (out instanceof Flushable) {
                                ((Flushable)((Object)out)).flush();
                            }
                            Thread.sleep(nb);
                        }
                        catch (InterruptedException interruptedException) {}
                        break;
                    }
                    if (!exec) continue block54;
                    out.append(ch);
                    break;
                }
                default: {
                    if (!exec) continue block54;
                    out.append(ch);
                }
            }
        }
    }

    private static int toInteger(Object pop) {
        if (pop instanceof Number) {
            return ((Number)pop).intValue();
        }
        if (pop instanceof Boolean) {
            return (Boolean)pop != false ? 1 : 0;
        }
        return Integer.parseInt(pop.toString());
    }
}

