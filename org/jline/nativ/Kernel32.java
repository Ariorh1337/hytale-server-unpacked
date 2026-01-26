/*
 * Decompiled with CFR 0.152.
 */
package org.jline.nativ;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jline.nativ.JLineNativeLoader;

public class Kernel32 {
    public static short FOREGROUND_BLUE;
    public static short FOREGROUND_GREEN;
    public static short FOREGROUND_RED;
    public static short FOREGROUND_INTENSITY;
    public static short BACKGROUND_BLUE;
    public static short BACKGROUND_GREEN;
    public static short BACKGROUND_RED;
    public static short BACKGROUND_INTENSITY;
    public static short COMMON_LVB_LEADING_BYTE;
    public static short COMMON_LVB_TRAILING_BYTE;
    public static short COMMON_LVB_GRID_HORIZONTAL;
    public static short COMMON_LVB_GRID_LVERTICAL;
    public static short COMMON_LVB_GRID_RVERTICAL;
    public static short COMMON_LVB_REVERSE_VIDEO;
    public static short COMMON_LVB_UNDERSCORE;
    public static int FORMAT_MESSAGE_FROM_SYSTEM;
    public static int STD_INPUT_HANDLE;
    public static int STD_OUTPUT_HANDLE;
    public static int STD_ERROR_HANDLE;
    public static long INVALID_HANDLE_VALUE;

    private static native void init();

    public static native long malloc(long var0);

    public static native void free(long var0);

    public static native int SetConsoleTextAttribute(long var0, short var2);

    public static native int WaitForSingleObject(long var0, int var2);

    public static native int CloseHandle(long var0);

    public static native int GetLastError();

    public static native int FormatMessageW(int var0, long var1, int var3, int var4, byte[] var5, int var6, long[] var7);

    public static native int GetConsoleScreenBufferInfo(long var0, CONSOLE_SCREEN_BUFFER_INFO var2);

    public static native long GetStdHandle(int var0);

    public static native int SetConsoleCursorPosition(long var0, COORD var2);

    public static native int FillConsoleOutputCharacterW(long var0, char var2, int var3, COORD var4, int[] var5);

    public static native int FillConsoleOutputAttribute(long var0, short var2, int var3, COORD var4, int[] var5);

    public static native int WriteConsoleW(long var0, char[] var2, int var3, int[] var4, long var5);

    public static native int GetConsoleMode(long var0, int[] var2);

    public static native int SetConsoleMode(long var0, int var2);

    public static native int _getch();

    public static native int SetConsoleTitle(String var0);

    public static native int GetConsoleOutputCP();

    public static native int SetConsoleOutputCP(int var0);

    public static native int ScrollConsoleScreenBuffer(long var0, SMALL_RECT var2, SMALL_RECT var3, COORD var4, CHAR_INFO var5);

    private static native int ReadConsoleInputW(long var0, long var2, int var4, int[] var5);

    private static native int PeekConsoleInputW(long var0, long var2, int var4, int[] var5);

    public static native int GetNumberOfConsoleInputEvents(long var0, int[] var2);

    public static native int FlushConsoleInputBuffer(long var0);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static INPUT_RECORD[] readConsoleInputHelper(long handle, int count, boolean peek) throws IOException {
        int[] length = new int[1];
        long inputRecordPtr = 0L;
        try {
            int res;
            inputRecordPtr = Kernel32.malloc(INPUT_RECORD.SIZEOF * count);
            if (inputRecordPtr == 0L) {
                throw new IOException("cannot allocate memory with JNI");
            }
            int n = res = peek ? Kernel32.PeekConsoleInputW(handle, inputRecordPtr, count, length) : Kernel32.ReadConsoleInputW(handle, inputRecordPtr, count, length);
            if (res == 0) {
                int bufferSize = 160;
                byte[] data = new byte[bufferSize];
                Kernel32.FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0L, Kernel32.GetLastError(), 0, data, bufferSize, null);
                String lastErrorMessage = new String(data, StandardCharsets.UTF_16LE).trim();
                throw new IOException("ReadConsoleInputW failed: " + lastErrorMessage);
            }
            if (length[0] <= 0) {
                INPUT_RECORD[] bufferSize = new INPUT_RECORD[]{};
                return bufferSize;
            }
            INPUT_RECORD[] records = new INPUT_RECORD[length[0]];
            for (int i = 0; i < records.length; ++i) {
                records[i] = new INPUT_RECORD();
                INPUT_RECORD.memmove(records[i], inputRecordPtr + (long)(i * INPUT_RECORD.SIZEOF), INPUT_RECORD.SIZEOF);
            }
            INPUT_RECORD[] iNPUT_RECORDArray = records;
            return iNPUT_RECORDArray;
        }
        finally {
            if (inputRecordPtr != 0L) {
                Kernel32.free(inputRecordPtr);
            }
        }
    }

    public static INPUT_RECORD[] readConsoleKeyInput(long handle, int count, boolean peek) throws IOException {
        INPUT_RECORD[] evts;
        int keyEvtCount;
        do {
            evts = Kernel32.readConsoleInputHelper(handle, count, peek);
            keyEvtCount = 0;
            for (INPUT_RECORD evt : evts) {
                if (evt.eventType != INPUT_RECORD.KEY_EVENT) continue;
                ++keyEvtCount;
            }
        } while (keyEvtCount <= 0);
        INPUT_RECORD[] res = new INPUT_RECORD[keyEvtCount];
        int i = 0;
        for (INPUT_RECORD evt : evts) {
            if (evt.eventType != INPUT_RECORD.KEY_EVENT) continue;
            res[i++] = evt;
        }
        return res;
    }

    public static String getLastErrorMessage() {
        int bufferSize = 160;
        byte[] data = new byte[bufferSize];
        Kernel32.FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0L, Kernel32.GetLastError(), 0, data, bufferSize, null);
        return new String(data, StandardCharsets.UTF_16LE).trim();
    }

    public static native int isatty(int var0);

    static {
        if (JLineNativeLoader.initialize()) {
            Kernel32.init();
        }
    }

    public static class INPUT_RECORD {
        public static int SIZEOF;
        public static short KEY_EVENT;
        public static short MOUSE_EVENT;
        public static short WINDOW_BUFFER_SIZE_EVENT;
        public static short FOCUS_EVENT;
        public static short MENU_EVENT;
        public short eventType;
        public KEY_EVENT_RECORD keyEvent = new KEY_EVENT_RECORD();
        public MOUSE_EVENT_RECORD mouseEvent = new MOUSE_EVENT_RECORD();
        public WINDOW_BUFFER_SIZE_RECORD windowBufferSizeEvent = new WINDOW_BUFFER_SIZE_RECORD();
        public MENU_EVENT_RECORD menuEvent = new MENU_EVENT_RECORD();
        public FOCUS_EVENT_RECORD focusEvent = new FOCUS_EVENT_RECORD();

        private static native void init();

        public static native void memmove(INPUT_RECORD var0, long var1, long var3);

        static {
            if (JLineNativeLoader.initialize()) {
                INPUT_RECORD.init();
            }
        }
    }

    public static class MENU_EVENT_RECORD {
        public static int SIZEOF;
        public int commandId;

        private static native void init();

        static {
            if (JLineNativeLoader.initialize()) {
                MENU_EVENT_RECORD.init();
            }
        }
    }

    public static class FOCUS_EVENT_RECORD {
        public static int SIZEOF;
        public boolean setFocus;

        private static native void init();

        static {
            if (JLineNativeLoader.initialize()) {
                FOCUS_EVENT_RECORD.init();
            }
        }
    }

    public static class WINDOW_BUFFER_SIZE_RECORD {
        public static int SIZEOF;
        public COORD size = new COORD();

        private static native void init();

        public String toString() {
            return "WINDOW_BUFFER_SIZE_RECORD{size=" + this.size + '}';
        }

        static {
            if (JLineNativeLoader.initialize()) {
                WINDOW_BUFFER_SIZE_RECORD.init();
            }
        }
    }

    public static class MOUSE_EVENT_RECORD {
        public static int SIZEOF;
        public static int FROM_LEFT_1ST_BUTTON_PRESSED;
        public static int FROM_LEFT_2ND_BUTTON_PRESSED;
        public static int FROM_LEFT_3RD_BUTTON_PRESSED;
        public static int FROM_LEFT_4TH_BUTTON_PRESSED;
        public static int RIGHTMOST_BUTTON_PRESSED;
        public static int CAPSLOCK_ON;
        public static int NUMLOCK_ON;
        public static int SCROLLLOCK_ON;
        public static int ENHANCED_KEY;
        public static int LEFT_ALT_PRESSED;
        public static int LEFT_CTRL_PRESSED;
        public static int RIGHT_ALT_PRESSED;
        public static int RIGHT_CTRL_PRESSED;
        public static int SHIFT_PRESSED;
        public static int DOUBLE_CLICK;
        public static int MOUSE_HWHEELED;
        public static int MOUSE_MOVED;
        public static int MOUSE_WHEELED;
        public COORD mousePosition = new COORD();
        public int buttonState;
        public int controlKeyState;
        public int eventFlags;

        private static native void init();

        public String toString() {
            return "MOUSE_EVENT_RECORD{mousePosition=" + this.mousePosition + ", buttonState=" + this.buttonState + ", controlKeyState=" + this.controlKeyState + ", eventFlags=" + this.eventFlags + '}';
        }

        static {
            if (JLineNativeLoader.initialize()) {
                MOUSE_EVENT_RECORD.init();
            }
        }
    }

    public static class KEY_EVENT_RECORD {
        public static int SIZEOF;
        public static int CAPSLOCK_ON;
        public static int NUMLOCK_ON;
        public static int SCROLLLOCK_ON;
        public static int ENHANCED_KEY;
        public static int LEFT_ALT_PRESSED;
        public static int LEFT_CTRL_PRESSED;
        public static int RIGHT_ALT_PRESSED;
        public static int RIGHT_CTRL_PRESSED;
        public static int SHIFT_PRESSED;
        public boolean keyDown;
        public short repeatCount;
        public short keyCode;
        public short scanCode;
        public char uchar;
        public int controlKeyState;

        private static native void init();

        public String toString() {
            return "KEY_EVENT_RECORD{keyDown=" + this.keyDown + ", repeatCount=" + this.repeatCount + ", keyCode=" + this.keyCode + ", scanCode=" + this.scanCode + ", uchar=" + this.uchar + ", controlKeyState=" + this.controlKeyState + '}';
        }

        static {
            if (JLineNativeLoader.initialize()) {
                KEY_EVENT_RECORD.init();
            }
        }
    }

    public static class CHAR_INFO {
        public static int SIZEOF;
        public short attributes;
        public char unicodeChar;

        private static native void init();

        static {
            JLineNativeLoader.initialize();
            CHAR_INFO.init();
        }
    }

    public static class CONSOLE_SCREEN_BUFFER_INFO {
        public static int SIZEOF;
        public COORD size = new COORD();
        public COORD cursorPosition = new COORD();
        public short attributes;
        public SMALL_RECT window = new SMALL_RECT();
        public COORD maximumWindowSize = new COORD();

        private static native void init();

        public int windowWidth() {
            return this.window.width() + 1;
        }

        public int windowHeight() {
            return this.window.height() + 1;
        }

        static {
            if (JLineNativeLoader.initialize()) {
                CONSOLE_SCREEN_BUFFER_INFO.init();
            }
        }
    }

    public static class COORD {
        public static int SIZEOF;
        public short x;
        public short y;

        private static native void init();

        public COORD copy() {
            COORD rc = new COORD();
            rc.x = this.x;
            rc.y = this.y;
            return rc;
        }

        static {
            if (JLineNativeLoader.initialize()) {
                COORD.init();
            }
        }
    }

    public static class SMALL_RECT {
        public static int SIZEOF;
        public short left;
        public short top;
        public short right;
        public short bottom;

        private static native void init();

        public short width() {
            return (short)(this.right - this.left);
        }

        public short height() {
            return (short)(this.bottom - this.top);
        }

        public SMALL_RECT copy() {
            SMALL_RECT rc = new SMALL_RECT();
            rc.left = this.left;
            rc.top = this.top;
            rc.right = this.right;
            rc.bottom = this.bottom;
            return rc;
        }

        static {
            if (JLineNativeLoader.initialize()) {
                SMALL_RECT.init();
            }
        }
    }
}

