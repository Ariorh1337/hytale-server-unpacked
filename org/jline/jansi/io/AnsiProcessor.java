/*
 * Decompiled with CFR 0.152.
 */
package org.jline.jansi.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class AnsiProcessor {
    protected final OutputStream os;
    protected static final int ERASE_SCREEN_TO_END = 0;
    protected static final int ERASE_SCREEN_TO_BEGINING = 1;
    protected static final int ERASE_SCREEN = 2;
    protected static final int ERASE_LINE_TO_END = 0;
    protected static final int ERASE_LINE_TO_BEGINING = 1;
    protected static final int ERASE_LINE = 2;
    protected static final int ATTRIBUTE_INTENSITY_BOLD = 1;
    protected static final int ATTRIBUTE_INTENSITY_FAINT = 2;
    protected static final int ATTRIBUTE_ITALIC = 3;
    protected static final int ATTRIBUTE_UNDERLINE = 4;
    protected static final int ATTRIBUTE_BLINK_SLOW = 5;
    protected static final int ATTRIBUTE_BLINK_FAST = 6;
    protected static final int ATTRIBUTE_NEGATIVE_ON = 7;
    protected static final int ATTRIBUTE_CONCEAL_ON = 8;
    protected static final int ATTRIBUTE_UNDERLINE_DOUBLE = 21;
    protected static final int ATTRIBUTE_INTENSITY_NORMAL = 22;
    protected static final int ATTRIBUTE_UNDERLINE_OFF = 24;
    protected static final int ATTRIBUTE_BLINK_OFF = 25;
    protected static final int ATTRIBUTE_NEGATIVE_OFF = 27;
    protected static final int ATTRIBUTE_CONCEAL_OFF = 28;
    protected static final int BLACK = 0;
    protected static final int RED = 1;
    protected static final int GREEN = 2;
    protected static final int YELLOW = 3;
    protected static final int BLUE = 4;
    protected static final int MAGENTA = 5;
    protected static final int CYAN = 6;
    protected static final int WHITE = 7;

    public AnsiProcessor(OutputStream os) {
        this.os = os;
    }

    protected int getNextOptionInt(Iterator<Object> optionsIterator) throws IOException {
        Object arg;
        do {
            if (optionsIterator.hasNext()) continue;
            throw new IllegalArgumentException();
        } while ((arg = optionsIterator.next()) == null);
        return (Integer)arg;
    }

    protected boolean processEscapeCommand(ArrayList<Object> options, int command) throws IOException {
        try {
            switch (command) {
                case 65: {
                    this.processCursorUp(this.optionInt(options, 0, 1));
                    return true;
                }
                case 66: {
                    this.processCursorDown(this.optionInt(options, 0, 1));
                    return true;
                }
                case 67: {
                    this.processCursorRight(this.optionInt(options, 0, 1));
                    return true;
                }
                case 68: {
                    this.processCursorLeft(this.optionInt(options, 0, 1));
                    return true;
                }
                case 69: {
                    this.processCursorDownLine(this.optionInt(options, 0, 1));
                    return true;
                }
                case 70: {
                    this.processCursorUpLine(this.optionInt(options, 0, 1));
                    return true;
                }
                case 71: {
                    this.processCursorToColumn(this.optionInt(options, 0));
                    return true;
                }
                case 72: 
                case 102: {
                    this.processCursorTo(this.optionInt(options, 0, 1), this.optionInt(options, 1, 1));
                    return true;
                }
                case 74: {
                    this.processEraseScreen(this.optionInt(options, 0, 0));
                    return true;
                }
                case 75: {
                    this.processEraseLine(this.optionInt(options, 0, 0));
                    return true;
                }
                case 76: {
                    this.processInsertLine(this.optionInt(options, 0, 1));
                    return true;
                }
                case 77: {
                    this.processDeleteLine(this.optionInt(options, 0, 1));
                    return true;
                }
                case 83: {
                    this.processScrollUp(this.optionInt(options, 0, 1));
                    return true;
                }
                case 84: {
                    this.processScrollDown(this.optionInt(options, 0, 1));
                    return true;
                }
                case 109: {
                    for (Object next : options) {
                        if (next == null || next.getClass() == Integer.class) continue;
                        throw new IllegalArgumentException();
                    }
                    int count = 0;
                    Iterator<Object> optionsIterator = options.iterator();
                    block27: while (optionsIterator.hasNext()) {
                        Object next = optionsIterator.next();
                        if (next == null) continue;
                        ++count;
                        int value = (Integer)next;
                        if (30 <= value && value <= 37) {
                            this.processSetForegroundColor(value - 30);
                            continue;
                        }
                        if (40 <= value && value <= 47) {
                            this.processSetBackgroundColor(value - 40);
                            continue;
                        }
                        if (90 <= value && value <= 97) {
                            this.processSetForegroundColor(value - 90, true);
                            continue;
                        }
                        if (100 <= value && value <= 107) {
                            this.processSetBackgroundColor(value - 100, true);
                            continue;
                        }
                        if (value == 38 || value == 48) {
                            if (!optionsIterator.hasNext()) continue;
                            int arg2or5 = this.getNextOptionInt(optionsIterator);
                            if (arg2or5 == 2) {
                                int r = this.getNextOptionInt(optionsIterator);
                                int g = this.getNextOptionInt(optionsIterator);
                                int b = this.getNextOptionInt(optionsIterator);
                                if (r >= 0 && r <= 255 && g >= 0 && g <= 255 && b >= 0 && b <= 255) {
                                    if (value == 38) {
                                        this.processSetForegroundColorExt(r, g, b);
                                        continue;
                                    }
                                    this.processSetBackgroundColorExt(r, g, b);
                                    continue;
                                }
                                throw new IllegalArgumentException();
                            }
                            if (arg2or5 == 5) {
                                int paletteIndex = this.getNextOptionInt(optionsIterator);
                                if (paletteIndex >= 0 && paletteIndex <= 255) {
                                    if (value == 38) {
                                        this.processSetForegroundColorExt(paletteIndex);
                                        continue;
                                    }
                                    this.processSetBackgroundColorExt(paletteIndex);
                                    continue;
                                }
                                throw new IllegalArgumentException();
                            }
                            throw new IllegalArgumentException();
                        }
                        switch (value) {
                            case 39: {
                                this.processDefaultTextColor();
                                continue block27;
                            }
                            case 49: {
                                this.processDefaultBackgroundColor();
                                continue block27;
                            }
                            case 0: {
                                this.processAttributeReset();
                                continue block27;
                            }
                        }
                        this.processSetAttribute(value);
                    }
                    if (count == 0) {
                        this.processAttributeReset();
                    }
                    return true;
                }
                case 115: {
                    this.processSaveCursorPosition();
                    return true;
                }
                case 117: {
                    this.processRestoreCursorPosition();
                    return true;
                }
            }
            if (97 <= command && command <= 122) {
                this.processUnknownExtension(options, command);
                return true;
            }
            if (65 <= command && command <= 90) {
                this.processUnknownExtension(options, command);
                return true;
            }
            return false;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    protected boolean processOperatingSystemCommand(ArrayList<Object> options) {
        int command = this.optionInt(options, 0);
        String label = (String)options.get(1);
        try {
            switch (command) {
                case 0: {
                    this.processChangeIconNameAndWindowTitle(label);
                    return true;
                }
                case 1: {
                    this.processChangeIconName(label);
                    return true;
                }
                case 2: {
                    this.processChangeWindowTitle(label);
                    return true;
                }
            }
            this.processUnknownOperatingSystemCommand(command, label);
            return true;
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return false;
        }
    }

    protected boolean processCharsetSelect(ArrayList<Object> options) {
        int set = this.optionInt(options, 0);
        char seq = ((Character)options.get(1)).charValue();
        this.processCharsetSelect(set, seq);
        return true;
    }

    private int optionInt(ArrayList<Object> options, int index) {
        if (options.size() <= index) {
            throw new IllegalArgumentException();
        }
        Object value = options.get(index);
        if (value == null) {
            throw new IllegalArgumentException();
        }
        if (!value.getClass().equals(Integer.class)) {
            throw new IllegalArgumentException();
        }
        return (Integer)value;
    }

    private int optionInt(ArrayList<Object> options, int index, int defaultValue) {
        if (options.size() > index) {
            Object value = options.get(index);
            if (value == null) {
                return defaultValue;
            }
            return (Integer)value;
        }
        return defaultValue;
    }

    protected void processRestoreCursorPosition() throws IOException {
    }

    protected void processSaveCursorPosition() throws IOException {
    }

    protected void processInsertLine(int optionInt) throws IOException {
    }

    protected void processDeleteLine(int optionInt) throws IOException {
    }

    protected void processScrollDown(int optionInt) throws IOException {
    }

    protected void processScrollUp(int optionInt) throws IOException {
    }

    protected void processEraseScreen(int eraseOption) throws IOException {
    }

    protected void processEraseLine(int eraseOption) throws IOException {
    }

    protected void processSetAttribute(int attribute) throws IOException {
    }

    protected void processSetForegroundColor(int color) throws IOException {
        this.processSetForegroundColor(color, false);
    }

    protected void processSetForegroundColor(int color, boolean bright) throws IOException {
    }

    protected void processSetForegroundColorExt(int paletteIndex) throws IOException {
    }

    protected void processSetForegroundColorExt(int r, int g, int b) throws IOException {
    }

    protected void processSetBackgroundColor(int color) throws IOException {
        this.processSetBackgroundColor(color, false);
    }

    protected void processSetBackgroundColor(int color, boolean bright) throws IOException {
    }

    protected void processSetBackgroundColorExt(int paletteIndex) throws IOException {
    }

    protected void processSetBackgroundColorExt(int r, int g, int b) throws IOException {
    }

    protected void processDefaultTextColor() throws IOException {
    }

    protected void processDefaultBackgroundColor() throws IOException {
    }

    protected void processAttributeReset() throws IOException {
    }

    protected void processCursorTo(int row, int col) throws IOException {
    }

    protected void processCursorToColumn(int x) throws IOException {
    }

    protected void processCursorUpLine(int count) throws IOException {
    }

    protected void processCursorDownLine(int count) throws IOException {
        for (int i = 0; i < count; ++i) {
            this.os.write(10);
        }
    }

    protected void processCursorLeft(int count) throws IOException {
    }

    protected void processCursorRight(int count) throws IOException {
        for (int i = 0; i < count; ++i) {
            this.os.write(32);
        }
    }

    protected void processCursorDown(int count) throws IOException {
    }

    protected void processCursorUp(int count) throws IOException {
    }

    protected void processUnknownExtension(ArrayList<Object> options, int command) {
    }

    protected void processChangeIconNameAndWindowTitle(String label) {
        this.processChangeIconName(label);
        this.processChangeWindowTitle(label);
    }

    protected void processChangeIconName(String label) {
    }

    protected void processChangeWindowTitle(String label) {
    }

    protected void processUnknownOperatingSystemCommand(int command, String param) {
    }

    protected void processCharsetSelect(int set, char seq) {
    }
}

