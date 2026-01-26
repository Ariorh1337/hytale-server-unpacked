/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.LastErrorException
 *  com.sun.jna.Native
 *  com.sun.jna.Pointer
 *  com.sun.jna.Structure
 *  com.sun.jna.Structure$ByReference
 *  com.sun.jna.Structure$ByValue
 *  com.sun.jna.Union
 *  com.sun.jna.ptr.IntByReference
 *  com.sun.jna.win32.StdCallLibrary
 *  com.sun.jna.win32.W32APIOptions
 */
package org.jline.terminal.impl.jna.win;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

interface Kernel32
extends StdCallLibrary {
    public static final Kernel32 INSTANCE = (Kernel32)Native.load((String)"kernel32", Kernel32.class, (Map)W32APIOptions.UNICODE_OPTIONS);
    public static final Pointer INVALID_HANDLE_VALUE = Pointer.createConstant((long)-1L);
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;
    public static final int ENABLE_PROCESSED_INPUT = 1;
    public static final int ENABLE_LINE_INPUT = 2;
    public static final int ENABLE_ECHO_INPUT = 4;
    public static final int ENABLE_WINDOW_INPUT = 8;
    public static final int ENABLE_MOUSE_INPUT = 16;
    public static final int ENABLE_INSERT_MODE = 32;
    public static final int ENABLE_QUICK_EDIT_MODE = 64;
    public static final int ENABLE_EXTENDED_FLAGS = 128;
    public static final int RIGHT_ALT_PRESSED = 1;
    public static final int LEFT_ALT_PRESSED = 2;
    public static final int RIGHT_CTRL_PRESSED = 4;
    public static final int LEFT_CTRL_PRESSED = 8;
    public static final int SHIFT_PRESSED = 16;
    public static final int FOREGROUND_BLUE = 1;
    public static final int FOREGROUND_GREEN = 2;
    public static final int FOREGROUND_RED = 4;
    public static final int FOREGROUND_INTENSITY = 8;
    public static final int BACKGROUND_BLUE = 16;
    public static final int BACKGROUND_GREEN = 32;
    public static final int BACKGROUND_RED = 64;
    public static final int BACKGROUND_INTENSITY = 128;
    public static final int FROM_LEFT_1ST_BUTTON_PRESSED = 1;
    public static final int RIGHTMOST_BUTTON_PRESSED = 2;
    public static final int FROM_LEFT_2ND_BUTTON_PRESSED = 4;
    public static final int FROM_LEFT_3RD_BUTTON_PRESSED = 8;
    public static final int FROM_LEFT_4TH_BUTTON_PRESSED = 16;
    public static final int MOUSE_MOVED = 1;
    public static final int DOUBLE_CLICK = 2;
    public static final int MOUSE_WHEELED = 4;
    public static final int MOUSE_HWHEELED = 8;

    public int WaitForSingleObject(Pointer var1, int var2);

    public Pointer GetStdHandle(int var1);

    public void AllocConsole() throws LastErrorException;

    public void FreeConsole() throws LastErrorException;

    public Pointer GetConsoleWindow();

    public int GetConsoleCP();

    public void FillConsoleOutputCharacter(Pointer var1, char var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void FillConsoleOutputAttribute(Pointer var1, short var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void GetConsoleCursorInfo(Pointer var1, CONSOLE_CURSOR_INFO.ByReference var2) throws LastErrorException;

    public void GetConsoleMode(Pointer var1, IntByReference var2) throws LastErrorException;

    public void GetConsoleScreenBufferInfo(Pointer var1, CONSOLE_SCREEN_BUFFER_INFO var2) throws LastErrorException;

    public void GetNumberOfConsoleInputEvents(Pointer var1, IntByReference var2) throws LastErrorException;

    public void ReadConsoleInput(Pointer var1, INPUT_RECORD[] var2, int var3, IntByReference var4) throws LastErrorException;

    public void SetConsoleCtrlHandler(Pointer var1, boolean var2) throws LastErrorException;

    public void ReadConsoleOutput(Pointer var1, CHAR_INFO[] var2, COORD var3, COORD var4, SMALL_RECT var5) throws LastErrorException;

    public void ReadConsoleOutputA(Pointer var1, CHAR_INFO[] var2, COORD var3, COORD var4, SMALL_RECT var5) throws LastErrorException;

    public void ReadConsoleOutputCharacter(Pointer var1, char[] var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void ReadConsoleOutputCharacterA(Pointer var1, byte[] var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void SetConsoleCursorInfo(Pointer var1, CONSOLE_CURSOR_INFO var2) throws LastErrorException;

    public void SetConsoleCP(int var1) throws LastErrorException;

    public void SetConsoleOutputCP(int var1) throws LastErrorException;

    public void SetConsoleCursorPosition(Pointer var1, COORD var2) throws LastErrorException;

    public void SetConsoleMode(Pointer var1, int var2) throws LastErrorException;

    public void SetConsoleScreenBufferSize(Pointer var1, COORD var2) throws LastErrorException;

    public void SetConsoleTextAttribute(Pointer var1, short var2) throws LastErrorException;

    public void SetConsoleTitle(String var1) throws LastErrorException;

    public void SetConsoleWindowInfo(Pointer var1, boolean var2, SMALL_RECT var3) throws LastErrorException;

    public void WriteConsoleW(Pointer var1, char[] var2, int var3, IntByReference var4, Pointer var5) throws LastErrorException;

    public void WriteConsoleOutput(Pointer var1, CHAR_INFO[] var2, COORD var3, COORD var4, SMALL_RECT var5) throws LastErrorException;

    public void WriteConsoleOutputA(Pointer var1, CHAR_INFO[] var2, COORD var3, COORD var4, SMALL_RECT var5) throws LastErrorException;

    public void WriteConsoleOutputCharacter(Pointer var1, char[] var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void WriteConsoleOutputCharacterA(Pointer var1, byte[] var2, int var3, COORD var4, IntByReference var5) throws LastErrorException;

    public void ScrollConsoleScreenBuffer(Pointer var1, SMALL_RECT var2, SMALL_RECT var3, COORD var4, CHAR_INFO var5) throws LastErrorException;

    public static class UnionChar
    extends Union {
        public char UnicodeChar;
        public byte AsciiChar;

        public UnionChar() {
        }

        public UnionChar(char c) {
            this.setType(Character.TYPE);
            this.UnicodeChar = c;
        }

        public UnionChar(byte c) {
            this.setType(Byte.TYPE);
            this.AsciiChar = c;
        }

        public void set(char c) {
            this.setType(Character.TYPE);
            this.UnicodeChar = c;
        }

        public void set(byte c) {
            this.setType(Byte.TYPE);
            this.AsciiChar = c;
        }
    }

    public static class SMALL_RECT
    extends Structure {
        public short Left;
        public short Top;
        public short Right;
        public short Bottom;
        private static String[] fieldOrder = new String[]{"Left", "Top", "Right", "Bottom"};

        public SMALL_RECT() {
        }

        public SMALL_RECT(SMALL_RECT org) {
            this(org.Top, org.Left, org.Bottom, org.Right);
        }

        public SMALL_RECT(short Top, short Left, short Bottom, short Right) {
            this.Top = Top;
            this.Left = Left;
            this.Bottom = Bottom;
            this.Right = Right;
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }

        public short width() {
            return (short)(this.Right - this.Left);
        }

        public short height() {
            return (short)(this.Bottom - this.Top);
        }
    }

    public static class FOCUS_EVENT_RECORD
    extends Structure {
        public boolean bSetFocus;
        private static String[] fieldOrder = new String[]{"bSetFocus"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class MENU_EVENT_RECORD
    extends Structure {
        public int dwCommandId;
        private static String[] fieldOrder = new String[]{"dwCommandId"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class WINDOW_BUFFER_SIZE_RECORD
    extends Structure {
        public COORD dwSize;
        private static String[] fieldOrder = new String[]{"dwSize"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class MOUSE_EVENT_RECORD
    extends Structure {
        public COORD dwMousePosition;
        public int dwButtonState;
        public int dwControlKeyState;
        public int dwEventFlags;
        private static String[] fieldOrder = new String[]{"dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class KEY_EVENT_RECORD
    extends Structure {
        public boolean bKeyDown;
        public short wRepeatCount;
        public short wVirtualKeyCode;
        public short wVirtualScanCode;
        public UnionChar uChar;
        public int dwControlKeyState;
        private static String[] fieldOrder = new String[]{"bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class INPUT_RECORD
    extends Structure {
        public static final short KEY_EVENT = 1;
        public static final short MOUSE_EVENT = 2;
        public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
        public static final short MENU_EVENT = 8;
        public static final short FOCUS_EVENT = 16;
        public short EventType;
        public EventUnion Event;
        private static String[] fieldOrder = new String[]{"EventType", "Event"};

        public void read() {
            this.readField("EventType");
            switch (this.EventType) {
                case 1: {
                    this.Event.setType(KEY_EVENT_RECORD.class);
                    break;
                }
                case 2: {
                    this.Event.setType(MOUSE_EVENT_RECORD.class);
                    break;
                }
                case 4: {
                    this.Event.setType(WINDOW_BUFFER_SIZE_RECORD.class);
                    break;
                }
                case 8: {
                    this.Event.setType(MENU_EVENT_RECORD.class);
                    break;
                }
                case 16: {
                    this.Event.setType(MENU_EVENT_RECORD.class);
                }
            }
            super.read();
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }

        public static class EventUnion
        extends Union {
            public KEY_EVENT_RECORD KeyEvent;
            public MOUSE_EVENT_RECORD MouseEvent;
            public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
            public MENU_EVENT_RECORD MenuEvent;
            public FOCUS_EVENT_RECORD FocusEvent;
        }
    }

    public static class COORD
    extends Structure
    implements Structure.ByValue {
        public short X;
        public short Y;
        private static String[] fieldOrder = new String[]{"X", "Y"};

        public COORD() {
        }

        public COORD(short X, short Y) {
            this.X = X;
            this.Y = Y;
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }

    public static class CONSOLE_SCREEN_BUFFER_INFO
    extends Structure {
        public COORD dwSize;
        public COORD dwCursorPosition;
        public short wAttributes;
        public SMALL_RECT srWindow;
        public COORD dwMaximumWindowSize;
        private static String[] fieldOrder = new String[]{"dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }

        public int windowWidth() {
            return this.srWindow.width() + 1;
        }

        public int windowHeight() {
            return this.srWindow.height() + 1;
        }
    }

    public static class CONSOLE_CURSOR_INFO
    extends Structure {
        public int dwSize;
        public boolean bVisible;
        private static String[] fieldOrder = new String[]{"dwSize", "bVisible"};

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }

        public static class ByReference
        extends CONSOLE_CURSOR_INFO
        implements Structure.ByReference {
        }
    }

    public static class CHAR_INFO
    extends Structure {
        public UnionChar uChar;
        public short Attributes;
        private static String[] fieldOrder = new String[]{"uChar", "Attributes"};

        public CHAR_INFO() {
        }

        public CHAR_INFO(char c, short attr) {
            this.uChar = new UnionChar(c);
            this.Attributes = attr;
        }

        public CHAR_INFO(byte c, short attr) {
            this.uChar = new UnionChar(c);
            this.Attributes = attr;
        }

        public static CHAR_INFO[] createArray(int size) {
            return (CHAR_INFO[])new CHAR_INFO().toArray(size);
        }

        protected List<String> getFieldOrder() {
            return Arrays.asList(fieldOrder);
        }
    }
}

