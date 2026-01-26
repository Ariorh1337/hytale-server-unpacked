/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl;

import java.io.EOFException;
import java.io.IOError;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.IntSupplier;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.InputStreamReader;

public class MouseSupport {
    public static boolean hasMouseSupport(Terminal terminal) {
        return terminal.getStringCapability(InfoCmp.Capability.key_mouse) != null;
    }

    public static boolean trackMouse(Terminal terminal, Terminal.MouseTracking tracking) {
        if (MouseSupport.hasMouseSupport(terminal)) {
            switch (tracking) {
                case Off: {
                    terminal.writer().write("\u001b[?1000l\u001b[?1002l\u001b[?1003l\u001b[?1005l\u001b[?1006l\u001b[?1015l\u001b[?1016l");
                    break;
                }
                case Normal: {
                    terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1000h");
                    break;
                }
                case Button: {
                    terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1002h");
                    break;
                }
                case Any: {
                    terminal.writer().write("\u001b[?1005h\u001b[?1006h\u001b[?1003h");
                }
            }
            terminal.flush();
            return true;
        }
        return false;
    }

    public static MouseEvent readMouse(Terminal terminal, MouseEvent last) {
        return MouseSupport.readMouse(() -> MouseSupport.readExt(terminal), last, null);
    }

    public static MouseEvent readMouse(Terminal terminal, MouseEvent last, String prefix) {
        return MouseSupport.readMouse(() -> MouseSupport.readExt(terminal), last, prefix);
    }

    public static MouseEvent readMouse(IntSupplier reader, MouseEvent last) {
        return MouseSupport.readMouse(reader, last, null);
    }

    public static MouseEvent readMouse(IntSupplier reader, MouseEvent last, String prefix) {
        int c;
        if (prefix != null && !prefix.isEmpty()) {
            if (prefix.equals("\u001b[<")) {
                IntSupplier prefixReader = MouseSupport.createReaderFromString("<");
                return MouseSupport.readMouse(MouseSupport.chainReaders(prefixReader, reader), last, null);
            }
            if (prefix.equals("\u001b[M")) {
                IntSupplier prefixReader = MouseSupport.createReaderFromString("M");
                return MouseSupport.readMouse(MouseSupport.chainReaders(prefixReader, reader), last, null);
            }
        }
        if ((c = reader.getAsInt()) == 60) {
            return MouseSupport.readMouseSGR(reader, last);
        }
        if (c >= 48 && c <= 57) {
            return MouseSupport.readMouseURXVT(c, reader, last);
        }
        if (c == 77) {
            int cb = reader.getAsInt();
            int cx = reader.getAsInt();
            int cy = reader.getAsInt();
            if ((cx & 0x80) != 0 || (cy & 0x80) != 0) {
                return MouseSupport.readMouseUTF8(cb, cx, cy, reader, last);
            }
            return MouseSupport.readMouseX10(cb - 32, cx - 32 - 1, cy - 32 - 1, last);
        }
        return MouseSupport.readMouseX10(c - 32, reader, last);
    }

    private static MouseEvent readMouseX10(int cb, IntSupplier reader, MouseEvent last) {
        int cx = reader.getAsInt() - 32 - 1;
        int cy = reader.getAsInt() - 32 - 1;
        return MouseSupport.parseMouseEvent(cb, cx, cy, false, last);
    }

    private static MouseEvent readMouseX10(int cb, int cx, int cy, MouseEvent last) {
        return MouseSupport.parseMouseEvent(cb, cx, cy, false, last);
    }

    private static MouseEvent readMouseUTF8(int cb, int cx, int cy, IntSupplier reader, MouseEvent last) {
        int x = MouseSupport.decodeUtf8Coordinate(cx, reader);
        int y = MouseSupport.decodeUtf8Coordinate(cy, reader);
        return MouseSupport.parseMouseEvent(cb - 32, --x, --y, false, last);
    }

    private static int decodeUtf8Coordinate(int firstByte, IntSupplier reader) {
        if ((firstByte & 0x80) == 0) {
            return firstByte - 32;
        }
        if ((firstByte & 0xE0) == 192) {
            int secondByte = reader.getAsInt();
            int value = (firstByte & 0x1F) << 6 | secondByte & 0x3F;
            return value - 32;
        }
        if ((firstByte & 0xF0) == 224) {
            int secondByte = reader.getAsInt();
            int thirdByte = reader.getAsInt();
            int value = (firstByte & 0xF) << 12 | (secondByte & 0x3F) << 6 | thirdByte & 0x3F;
            return value - 32;
        }
        return firstByte - 32;
    }

    private static MouseEvent readMouseSGR(IntSupplier reader, MouseEvent last) {
        int c;
        StringBuilder sb = new StringBuilder();
        int[] params = new int[3];
        int paramIndex = 0;
        boolean isPixels = false;
        boolean isRelease = false;
        while ((c = reader.getAsInt()) != -1) {
            if (c == 77 || c == 109) {
                isRelease = c == 109;
                break;
            }
            if (c == 59) {
                if (paramIndex >= params.length) continue;
                try {
                    params[paramIndex++] = Integer.parseInt(sb.toString());
                }
                catch (NumberFormatException e) {
                    params[paramIndex++] = 0;
                }
                sb.setLength(0);
                continue;
            }
            if (c < 48 || c > 57) continue;
            sb.append((char)c);
        }
        if (sb.length() > 0 && paramIndex < params.length) {
            try {
                params[paramIndex] = Integer.parseInt(sb.toString());
            }
            catch (NumberFormatException e) {
                params[paramIndex] = 0;
            }
        }
        int cb = params[0];
        int cx = params[1] - 1;
        int cy = params[2] - 1;
        return MouseSupport.parseMouseEvent(cb, cx, cy, isRelease, last);
    }

    private static MouseEvent readMouseURXVT(int firstDigit, IntSupplier reader, MouseEvent last) {
        int c;
        StringBuilder sb = new StringBuilder().append((char)firstDigit);
        int[] params = new int[3];
        int paramIndex = 0;
        while ((c = reader.getAsInt()) != -1 && c != 77) {
            if (c == 59) {
                if (paramIndex >= params.length) continue;
                try {
                    params[paramIndex++] = Integer.parseInt(sb.toString());
                }
                catch (NumberFormatException e) {
                    params[paramIndex++] = 0;
                }
                sb.setLength(0);
                continue;
            }
            if (c < 48 || c > 57) continue;
            sb.append((char)c);
        }
        if (sb.length() > 0 && paramIndex < params.length) {
            try {
                params[paramIndex] = Integer.parseInt(sb.toString());
            }
            catch (NumberFormatException e) {
                params[paramIndex] = 0;
            }
        }
        int cb = params[0];
        int cx = params[1] - 1;
        int cy = params[2] - 1;
        return MouseSupport.parseMouseEvent(cb, cx, cy, false, last);
    }

    private static MouseEvent parseMouseEvent(int cb, int cx, int cy, boolean isRelease, MouseEvent last) {
        MouseEvent.Button button;
        MouseEvent.Type type;
        EnumSet<MouseEvent.Modifier> modifiers = EnumSet.noneOf(MouseEvent.Modifier.class);
        if ((cb & 4) == 4) {
            modifiers.add(MouseEvent.Modifier.Shift);
        }
        if ((cb & 8) == 8) {
            modifiers.add(MouseEvent.Modifier.Alt);
        }
        if ((cb & 0x10) == 16) {
            modifiers.add(MouseEvent.Modifier.Control);
        }
        if ((cb & 0x40) == 64) {
            type = MouseEvent.Type.Wheel;
            button = (cb & 1) == 1 ? MouseEvent.Button.WheelDown : MouseEvent.Button.WheelUp;
        } else if (isRelease) {
            button = MouseSupport.getButtonForCode(cb & 3);
            type = MouseEvent.Type.Released;
        } else {
            int b = cb & 3;
            switch (b) {
                case 0: {
                    button = MouseEvent.Button.Button1;
                    if (last.getButton() == button && (last.getType() == MouseEvent.Type.Pressed || last.getType() == MouseEvent.Type.Dragged)) {
                        type = MouseEvent.Type.Dragged;
                        break;
                    }
                    type = MouseEvent.Type.Pressed;
                    break;
                }
                case 1: {
                    button = MouseEvent.Button.Button2;
                    if (last.getButton() == button && (last.getType() == MouseEvent.Type.Pressed || last.getType() == MouseEvent.Type.Dragged)) {
                        type = MouseEvent.Type.Dragged;
                        break;
                    }
                    type = MouseEvent.Type.Pressed;
                    break;
                }
                case 2: {
                    button = MouseEvent.Button.Button3;
                    if (last.getButton() == button && (last.getType() == MouseEvent.Type.Pressed || last.getType() == MouseEvent.Type.Dragged)) {
                        type = MouseEvent.Type.Dragged;
                        break;
                    }
                    type = MouseEvent.Type.Pressed;
                    break;
                }
                default: {
                    if (last.getType() == MouseEvent.Type.Pressed || last.getType() == MouseEvent.Type.Dragged) {
                        button = last.getButton();
                        type = MouseEvent.Type.Released;
                        break;
                    }
                    button = MouseEvent.Button.NoButton;
                    type = MouseEvent.Type.Moved;
                }
            }
        }
        return new MouseEvent(type, button, modifiers, cx, cy);
    }

    private static MouseEvent.Button getButtonForCode(int code) {
        switch (code) {
            case 0: {
                return MouseEvent.Button.Button1;
            }
            case 1: {
                return MouseEvent.Button.Button2;
            }
            case 2: {
                return MouseEvent.Button.Button3;
            }
        }
        return MouseEvent.Button.NoButton;
    }

    public static String[] keys() {
        return new String[]{"\u001b[<", "\u001b[M"};
    }

    public static String[] keys(Terminal terminal) {
        String keyMouse = terminal.getStringCapability(InfoCmp.Capability.key_mouse);
        if (keyMouse != null) {
            if (Arrays.asList(MouseSupport.keys()).contains(keyMouse)) {
                return MouseSupport.keys();
            }
            return new String[]{keyMouse, "\u001b[<", "\u001b[M"};
        }
        return MouseSupport.keys();
    }

    private static int readExt(Terminal terminal) {
        try {
            int c = terminal.encoding() != StandardCharsets.UTF_8 ? new InputStreamReader(terminal.input(), StandardCharsets.UTF_8).read() : terminal.reader().read();
            if (c < 0) {
                throw new EOFException();
            }
            return c;
        }
        catch (IOException e) {
            throw new IOError(e);
        }
    }

    private static IntSupplier createReaderFromString(String s) {
        int[] chars = s.chars().toArray();
        int[] index = new int[]{0};
        return () -> {
            if (index[0] < chars.length) {
                int n = index[0];
                index[0] = n + 1;
                return chars[n];
            }
            return -1;
        };
    }

    private static IntSupplier chainReaders(final IntSupplier first, final IntSupplier second) {
        return new IntSupplier(){
            private boolean firstExhausted = false;

            @Override
            public int getAsInt() {
                if (!this.firstExhausted) {
                    int c = first.getAsInt();
                    if (c != -1) {
                        return c;
                    }
                    this.firstExhausted = true;
                }
                return second.getAsInt();
            }
        };
    }
}

