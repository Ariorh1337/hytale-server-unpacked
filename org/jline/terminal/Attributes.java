/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Attributes {
    final EnumSet<InputFlag> iflag = EnumSet.noneOf(InputFlag.class);
    final EnumSet<OutputFlag> oflag = EnumSet.noneOf(OutputFlag.class);
    final EnumSet<ControlFlag> cflag = EnumSet.noneOf(ControlFlag.class);
    final EnumSet<LocalFlag> lflag = EnumSet.noneOf(LocalFlag.class);
    final EnumMap<ControlChar, Integer> cchars = new EnumMap(ControlChar.class);

    public Attributes() {
    }

    public Attributes(Attributes attr) {
        this.copy(attr);
    }

    public EnumSet<InputFlag> getInputFlags() {
        return this.iflag;
    }

    public void setInputFlags(EnumSet<InputFlag> flags) {
        this.iflag.clear();
        this.iflag.addAll(flags);
    }

    public boolean getInputFlag(InputFlag flag) {
        return this.iflag.contains((Object)flag);
    }

    public void setInputFlags(EnumSet<InputFlag> flags, boolean value) {
        if (value) {
            this.iflag.addAll(flags);
        } else {
            this.iflag.removeAll(flags);
        }
    }

    public void setInputFlag(InputFlag flag, boolean value) {
        if (value) {
            this.iflag.add(flag);
        } else {
            this.iflag.remove((Object)flag);
        }
    }

    public EnumSet<OutputFlag> getOutputFlags() {
        return this.oflag;
    }

    public void setOutputFlags(EnumSet<OutputFlag> flags) {
        this.oflag.clear();
        this.oflag.addAll(flags);
    }

    public boolean getOutputFlag(OutputFlag flag) {
        return this.oflag.contains((Object)flag);
    }

    public void setOutputFlags(EnumSet<OutputFlag> flags, boolean value) {
        if (value) {
            this.oflag.addAll(flags);
        } else {
            this.oflag.removeAll(flags);
        }
    }

    public void setOutputFlag(OutputFlag flag, boolean value) {
        if (value) {
            this.oflag.add(flag);
        } else {
            this.oflag.remove((Object)flag);
        }
    }

    public EnumSet<ControlFlag> getControlFlags() {
        return this.cflag;
    }

    public void setControlFlags(EnumSet<ControlFlag> flags) {
        this.cflag.clear();
        this.cflag.addAll(flags);
    }

    public boolean getControlFlag(ControlFlag flag) {
        return this.cflag.contains((Object)flag);
    }

    public void setControlFlags(EnumSet<ControlFlag> flags, boolean value) {
        if (value) {
            this.cflag.addAll(flags);
        } else {
            this.cflag.removeAll(flags);
        }
    }

    public void setControlFlag(ControlFlag flag, boolean value) {
        if (value) {
            this.cflag.add(flag);
        } else {
            this.cflag.remove((Object)flag);
        }
    }

    public EnumSet<LocalFlag> getLocalFlags() {
        return this.lflag;
    }

    public void setLocalFlags(EnumSet<LocalFlag> flags) {
        this.lflag.clear();
        this.lflag.addAll(flags);
    }

    public boolean getLocalFlag(LocalFlag flag) {
        return this.lflag.contains((Object)flag);
    }

    public void setLocalFlags(EnumSet<LocalFlag> flags, boolean value) {
        if (value) {
            this.lflag.addAll(flags);
        } else {
            this.lflag.removeAll(flags);
        }
    }

    public void setLocalFlag(LocalFlag flag, boolean value) {
        if (value) {
            this.lflag.add(flag);
        } else {
            this.lflag.remove((Object)flag);
        }
    }

    public EnumMap<ControlChar, Integer> getControlChars() {
        return this.cchars;
    }

    public void setControlChars(EnumMap<ControlChar, Integer> chars) {
        this.cchars.clear();
        this.cchars.putAll(chars);
    }

    public int getControlChar(ControlChar c) {
        Integer v = this.cchars.get((Object)c);
        return v != null ? v : -1;
    }

    public void setControlChar(ControlChar c, int value) {
        this.cchars.put(c, value);
    }

    public void copy(Attributes attributes) {
        this.setControlFlags(attributes.getControlFlags());
        this.setInputFlags(attributes.getInputFlags());
        this.setLocalFlags(attributes.getLocalFlags());
        this.setOutputFlags(attributes.getOutputFlags());
        this.setControlChars(attributes.getControlChars());
    }

    public String toString() {
        return "Attributes[lflags: " + this.append(this.lflag) + ", iflags: " + this.append(this.iflag) + ", oflags: " + this.append(this.oflag) + ", cflags: " + this.append(this.cflag) + ", cchars: " + this.append(EnumSet.allOf(ControlChar.class), this::display) + "]";
    }

    private String display(ControlChar c) {
        int ch = this.getControlChar(c);
        String value = c == ControlChar.VMIN || c == ControlChar.VTIME ? Integer.toString(ch) : (ch < 0 ? "<undef>" : (ch < 32 ? "^" + (char)(ch + 65 - 1) : (ch == 127 ? "^?" : (ch >= 128 ? String.format("\\u%04x", ch) : String.valueOf((char)ch)))));
        return c.name().toLowerCase().substring(1) + "=" + value;
    }

    private <T extends Enum<T>> String append(EnumSet<T> set) {
        return this.append(set, e -> e.name().toLowerCase());
    }

    private <T extends Enum<T>> String append(EnumSet<T> set, Function<T, String> toString) {
        return set.stream().map(toString).collect(Collectors.joining(" "));
    }

    public static enum InputFlag {
        IGNBRK,
        BRKINT,
        IGNPAR,
        PARMRK,
        INPCK,
        ISTRIP,
        INLCR,
        IGNCR,
        ICRNL,
        IXON,
        IXOFF,
        IXANY,
        IMAXBEL,
        IUTF8,
        INORMEOL;

    }

    public static enum OutputFlag {
        OPOST,
        ONLCR,
        OXTABS,
        ONOEOT,
        OCRNL,
        ONOCR,
        ONLRET,
        OFILL,
        NLDLY,
        TABDLY,
        CRDLY,
        FFDLY,
        BSDLY,
        VTDLY,
        OFDEL;

    }

    public static enum ControlFlag {
        CIGNORE,
        CS5,
        CS6,
        CS7,
        CS8,
        CSTOPB,
        CREAD,
        PARENB,
        PARODD,
        HUPCL,
        CLOCAL,
        CCTS_OFLOW,
        CRTS_IFLOW,
        CDTR_IFLOW,
        CDSR_OFLOW,
        CCAR_OFLOW;

    }

    public static enum LocalFlag {
        ECHOKE,
        ECHOE,
        ECHOK,
        ECHO,
        ECHONL,
        ECHOPRT,
        ECHOCTL,
        ISIG,
        ICANON,
        ALTWERASE,
        IEXTEN,
        EXTPROC,
        TOSTOP,
        FLUSHO,
        NOKERNINFO,
        PENDIN,
        NOFLSH;

    }

    public static enum ControlChar {
        VEOF,
        VEOL,
        VEOL2,
        VERASE,
        VWERASE,
        VKILL,
        VREPRINT,
        VINTR,
        VQUIT,
        VSUSP,
        VDSUSP,
        VSTART,
        VSTOP,
        VLNEXT,
        VDISCARD,
        VMIN,
        VTIME,
        VSTATUS;

    }
}

