/*
 * Decompiled with CFR 0.152.
 */
package org.jline.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.utils.AttributedCharSequence;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;

public class AttributedStringBuilder
extends AttributedCharSequence
implements Appendable {
    private char[] buffer;
    private long[] style;
    private int length;
    private TabStops tabs = new TabStops(0);
    private char[] altIn;
    private char[] altOut;
    private boolean inAltCharset;
    private int lastLineLength = 0;
    private AttributedStyle current = AttributedStyle.DEFAULT;

    public static AttributedString append(CharSequence ... strings) {
        AttributedStringBuilder sb = new AttributedStringBuilder();
        for (CharSequence s : strings) {
            sb.append(s);
        }
        return sb.toAttributedString();
    }

    public AttributedStringBuilder() {
        this(64);
    }

    public AttributedStringBuilder(int capacity) {
        this.buffer = new char[capacity];
        this.style = new long[capacity];
        this.length = 0;
    }

    @Override
    public int length() {
        return this.length;
    }

    @Override
    public char charAt(int index) {
        return this.buffer[index];
    }

    @Override
    public AttributedStyle styleAt(int index) {
        return new AttributedStyle(this.style[index], this.style[index]);
    }

    @Override
    long styleCodeAt(int index) {
        return this.style[index];
    }

    @Override
    protected char[] buffer() {
        return this.buffer;
    }

    @Override
    protected int offset() {
        return 0;
    }

    @Override
    public AttributedString subSequence(int start, int end) {
        return new AttributedString(Arrays.copyOfRange(this.buffer, start, end), Arrays.copyOfRange(this.style, start, end), 0, end - start);
    }

    @Override
    public AttributedStringBuilder append(CharSequence csq) {
        if (csq == null) {
            csq = "null";
        }
        return this.append(new AttributedString(csq, this.current));
    }

    @Override
    public AttributedStringBuilder append(CharSequence csq, int start, int end) {
        if (csq == null) {
            csq = "null";
        }
        return this.append(csq.subSequence(start, end));
    }

    @Override
    public AttributedStringBuilder append(char c) {
        return this.append(Character.toString(c));
    }

    public AttributedStringBuilder append(char c, int repeat) {
        AttributedString s = new AttributedString(Character.toString(c), this.current);
        while (repeat-- > 0) {
            this.append(s);
        }
        return this;
    }

    public AttributedStringBuilder append(CharSequence csq, AttributedStyle style) {
        return this.append(new AttributedString(csq, style));
    }

    public AttributedStringBuilder style(AttributedStyle style) {
        this.current = style;
        return this;
    }

    public AttributedStringBuilder style(Function<AttributedStyle, AttributedStyle> style) {
        this.current = style.apply(this.current);
        return this;
    }

    public AttributedStringBuilder styled(Function<AttributedStyle, AttributedStyle> style, CharSequence cs) {
        return this.styled(style, (AttributedStringBuilder sb) -> sb.append(cs));
    }

    public AttributedStringBuilder styled(AttributedStyle style, CharSequence cs) {
        return this.styled((AttributedStyle s) -> style, (AttributedStringBuilder sb) -> sb.append(cs));
    }

    public AttributedStringBuilder styled(Function<AttributedStyle, AttributedStyle> style, Consumer<AttributedStringBuilder> consumer) {
        AttributedStyle prev = this.current;
        this.current = style.apply(prev);
        consumer.accept(this);
        this.current = prev;
        return this;
    }

    public AttributedStyle style() {
        return this.current;
    }

    public AttributedStringBuilder append(AttributedString str) {
        return this.append((AttributedCharSequence)str, 0, str.length());
    }

    public AttributedStringBuilder append(AttributedString str, int start, int end) {
        return this.append((AttributedCharSequence)str, start, end);
    }

    public AttributedStringBuilder append(AttributedCharSequence str) {
        return this.append(str, 0, str.length());
    }

    public AttributedStringBuilder append(AttributedCharSequence str, int start, int end) {
        this.ensureCapacity(this.length + end - start);
        for (int i = start; i < end; ++i) {
            char c = str.charAt(i);
            long s = str.styleCodeAt(i) & (this.current.getMask() ^ 0xFFFFFFFFFFFFFFFFL) | this.current.getStyle();
            if (this.tabs.defined() && c == '\t') {
                this.insertTab(new AttributedStyle(s, 0L));
                continue;
            }
            this.ensureCapacity(this.length + 1);
            this.buffer[this.length] = c;
            this.style[this.length] = s;
            this.lastLineLength = c == '\n' ? 0 : ++this.lastLineLength;
            ++this.length;
        }
        return this;
    }

    protected void ensureCapacity(int nl) {
        if (nl > this.buffer.length) {
            int s;
            for (s = Math.max(this.buffer.length, 1); s <= nl; s *= 2) {
            }
            this.buffer = Arrays.copyOf(this.buffer, s);
            this.style = Arrays.copyOf(this.style, s);
        }
    }

    public void appendAnsi(String ansi) {
        this.ansiAppend(ansi);
    }

    public AttributedStringBuilder ansiAppend(String ansi) {
        int ansiStart = 0;
        int ansiState = 0;
        this.ensureCapacity(this.length + ansi.length());
        for (int i = 0; i < ansi.length(); ++i) {
            char[] alt;
            int c = ansi.charAt(i);
            if (ansiState == 0 && c == 27) {
                ++ansiState;
                continue;
            }
            if (ansiState == 1 && c == 91) {
                ++ansiState;
                ansiStart = i + 1;
                continue;
            }
            if (ansiState == 2) {
                if (c == 109) {
                    String[] params = ansi.substring(ansiStart, i).split(";");
                    block39: for (int j = 0; j < params.length; ++j) {
                        int ansiParam = params[j].isEmpty() ? 0 : Integer.parseInt(params[j]);
                        switch (ansiParam) {
                            case 0: {
                                this.current = AttributedStyle.DEFAULT;
                                continue block39;
                            }
                            case 1: {
                                this.current = this.current.bold();
                                continue block39;
                            }
                            case 2: {
                                this.current = this.current.faint();
                                continue block39;
                            }
                            case 3: {
                                this.current = this.current.italic();
                                continue block39;
                            }
                            case 4: {
                                this.current = this.current.underline();
                                continue block39;
                            }
                            case 5: {
                                this.current = this.current.blink();
                                continue block39;
                            }
                            case 7: {
                                this.current = this.current.inverse();
                                continue block39;
                            }
                            case 8: {
                                this.current = this.current.conceal();
                                continue block39;
                            }
                            case 9: {
                                this.current = this.current.crossedOut();
                                continue block39;
                            }
                            case 22: {
                                this.current = this.current.boldOff().faintOff();
                                continue block39;
                            }
                            case 23: {
                                this.current = this.current.italicOff();
                                continue block39;
                            }
                            case 24: {
                                this.current = this.current.underlineOff();
                                continue block39;
                            }
                            case 25: {
                                this.current = this.current.blinkOff();
                                continue block39;
                            }
                            case 27: {
                                this.current = this.current.inverseOff();
                                continue block39;
                            }
                            case 28: {
                                this.current = this.current.concealOff();
                                continue block39;
                            }
                            case 29: {
                                this.current = this.current.crossedOutOff();
                                continue block39;
                            }
                            case 30: 
                            case 31: 
                            case 32: 
                            case 33: 
                            case 34: 
                            case 35: 
                            case 36: 
                            case 37: {
                                this.current = this.current.foreground(ansiParam - 30);
                                continue block39;
                            }
                            case 39: {
                                this.current = this.current.foregroundOff();
                                continue block39;
                            }
                            case 40: 
                            case 41: 
                            case 42: 
                            case 43: 
                            case 44: 
                            case 45: 
                            case 46: 
                            case 47: {
                                this.current = this.current.background(ansiParam - 40);
                                continue block39;
                            }
                            case 49: {
                                this.current = this.current.backgroundOff();
                                continue block39;
                            }
                            case 38: 
                            case 48: {
                                int ansiParam2;
                                if (j + 1 >= params.length) continue block39;
                                if ((ansiParam2 = Integer.parseInt(params[++j])) == 2) {
                                    if (j + 3 >= params.length) continue block39;
                                    int r = Integer.parseInt(params[++j]);
                                    int g = Integer.parseInt(params[++j]);
                                    int b = Integer.parseInt(params[++j]);
                                    if (ansiParam == 38) {
                                        this.current = this.current.foreground(r, g, b);
                                        continue block39;
                                    }
                                    this.current = this.current.background(r, g, b);
                                    continue block39;
                                }
                                if (ansiParam2 != 5 || j + 1 >= params.length) continue block39;
                                int col = Integer.parseInt(params[++j]);
                                if (ansiParam == 38) {
                                    this.current = this.current.foreground(col);
                                    continue block39;
                                }
                                this.current = this.current.background(col);
                                continue block39;
                            }
                            case 90: 
                            case 91: 
                            case 92: 
                            case 93: 
                            case 94: 
                            case 95: 
                            case 96: 
                            case 97: {
                                this.current = this.current.foreground(ansiParam - 90 + 8);
                                continue block39;
                            }
                            case 100: 
                            case 101: 
                            case 102: 
                            case 103: 
                            case 104: 
                            case 105: 
                            case 106: 
                            case 107: {
                                this.current = this.current.background(ansiParam - 100 + 8);
                            }
                        }
                    }
                    ansiState = 0;
                    continue;
                }
                if (c >= 48 && c <= 57 || c == 59) continue;
                ansiState = 0;
                continue;
            }
            if (ansiState >= 1) {
                this.ensureCapacity(this.length + 1);
                this.buffer[this.length++] = 27;
                if (ansiState >= 2) {
                    this.ensureCapacity(this.length + 1);
                    this.buffer[this.length++] = 91;
                }
                ansiState = 0;
            }
            if (c == 9 && this.tabs.defined()) {
                this.insertTab(this.current);
                continue;
            }
            this.ensureCapacity(this.length + 1);
            if (this.inAltCharset) {
                switch (c) {
                    case 106: {
                        c = 9496;
                        break;
                    }
                    case 107: {
                        c = 9488;
                        break;
                    }
                    case 108: {
                        c = 9484;
                        break;
                    }
                    case 109: {
                        c = 9492;
                        break;
                    }
                    case 110: {
                        c = 9532;
                        break;
                    }
                    case 113: {
                        c = 9472;
                        break;
                    }
                    case 116: {
                        c = 9500;
                        break;
                    }
                    case 117: {
                        c = 9508;
                        break;
                    }
                    case 118: {
                        c = 9524;
                        break;
                    }
                    case 119: {
                        c = 9516;
                        break;
                    }
                    case 120: {
                        c = 9474;
                    }
                }
            }
            this.buffer[this.length] = c;
            this.style[this.length] = this.current.getStyle();
            this.lastLineLength = c == 10 ? 0 : ++this.lastLineLength;
            ++this.length;
            if (this.altIn == null || this.altOut == null) continue;
            char[] cArray = alt = this.inAltCharset ? this.altOut : this.altIn;
            if (!AttributedStringBuilder.equals(this.buffer, this.length - alt.length, alt, 0, alt.length)) continue;
            this.inAltCharset = !this.inAltCharset;
            this.length -= alt.length;
        }
        return this;
    }

    private static boolean equals(char[] a, int aFromIndex, char[] b, int bFromIndex, int length) {
        if (aFromIndex < 0 || bFromIndex < 0 || aFromIndex + length > a.length || bFromIndex + length > b.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (a[aFromIndex + i] == b[bFromIndex + i]) continue;
            return false;
        }
        return true;
    }

    protected void insertTab(AttributedStyle s) {
        int nb = this.tabs.spaces(this.lastLineLength);
        this.ensureCapacity(this.length + nb);
        for (int i = 0; i < nb; ++i) {
            this.buffer[this.length] = 32;
            this.style[this.length] = s.getStyle();
            ++this.length;
        }
        this.lastLineLength += nb;
    }

    public void setLength(int l) {
        this.length = l;
    }

    public AttributedStringBuilder tabs(int tabsize) {
        if (tabsize < 0) {
            throw new IllegalArgumentException("Tab size must be non negative");
        }
        return this.tabs(Arrays.asList(tabsize));
    }

    public AttributedStringBuilder tabs(List<Integer> tabs) {
        if (this.length > 0) {
            throw new IllegalStateException("Cannot change tab size after appending text");
        }
        this.tabs = new TabStops(tabs);
        return this;
    }

    public AttributedStringBuilder altCharset(String altIn, String altOut) {
        if (this.length > 0) {
            throw new IllegalStateException("Cannot change alternative charset after appending text");
        }
        this.altIn = altIn != null ? altIn.toCharArray() : null;
        this.altOut = altOut != null ? altOut.toCharArray() : null;
        return this;
    }

    public AttributedStringBuilder styleMatches(Pattern pattern, AttributedStyle s) {
        Matcher matcher = pattern.matcher(this);
        while (matcher.find()) {
            for (int i = matcher.start(); i < matcher.end(); ++i) {
                this.style[i] = this.style[i] & (s.getMask() ^ 0xFFFFFFFFFFFFFFFFL) | s.getStyle();
            }
        }
        return this;
    }

    public AttributedStringBuilder styleMatches(Pattern pattern, List<AttributedStyle> styles) {
        Matcher matcher = pattern.matcher(this);
        while (matcher.find()) {
            for (int group = 0; group < matcher.groupCount(); ++group) {
                AttributedStyle s = styles.get(group);
                for (int i = matcher.start(group + 1); i < matcher.end(group + 1); ++i) {
                    this.style[i] = this.style[i] & (s.getMask() ^ 0xFFFFFFFFFFFFFFFFL) | s.getStyle();
                }
            }
        }
        return this;
    }

    private static class TabStops {
        private List<Integer> tabs = new ArrayList<Integer>();
        private int lastStop = 0;
        private int lastSize = 0;

        public TabStops(int tabs) {
            this.lastSize = tabs;
        }

        public TabStops(List<Integer> tabs) {
            this.tabs = tabs;
            int p = 0;
            for (int s : tabs) {
                if (s <= p) continue;
                this.lastStop = s;
                this.lastSize = s - p;
                p = s;
            }
        }

        boolean defined() {
            return this.lastSize > 0;
        }

        int spaces(int lastLineLength) {
            int out = 0;
            if (lastLineLength >= this.lastStop) {
                out = this.lastSize - (lastLineLength - this.lastStop) % this.lastSize;
            } else {
                for (int s : this.tabs) {
                    if (s <= lastLineLength) continue;
                    out = s - lastLineLength;
                    break;
                }
            }
            return out;
        }
    }
}

