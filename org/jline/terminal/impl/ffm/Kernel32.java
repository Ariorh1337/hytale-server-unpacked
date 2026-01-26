/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl.ffm;

import java.io.IOException;
import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.jline.terminal.impl.ffm.FfmTerminalProvider;

final class Kernel32 {
    public static final int FORMAT_MESSAGE_FROM_SYSTEM = 4096;
    public static final long INVALID_HANDLE_VALUE = -1L;
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
    public static final short KEY_EVENT = 1;
    public static final short MOUSE_EVENT = 2;
    public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
    public static final short MENU_EVENT = 8;
    public static final short FOCUS_EVENT = 16;
    private static final SymbolLookup SYMBOL_LOOKUP;
    static final ValueLayout.OfBoolean C_BOOL$LAYOUT;
    static final ValueLayout.OfByte C_CHAR$LAYOUT;
    static final ValueLayout.OfChar C_WCHAR$LAYOUT;
    static final ValueLayout.OfShort C_SHORT$LAYOUT;
    static final ValueLayout.OfShort C_WORD$LAYOUT;
    static final ValueLayout.OfInt C_DWORD$LAYOUT;
    static final ValueLayout.OfInt C_INT$LAYOUT;
    static final ValueLayout.OfLong C_LONG$LAYOUT;
    static final ValueLayout.OfLong C_LONG_LONG$LAYOUT;
    static final ValueLayout.OfFloat C_FLOAT$LAYOUT;
    static final ValueLayout.OfDouble C_DOUBLE$LAYOUT;
    static final AddressLayout C_POINTER$LAYOUT;
    static final MethodHandle WaitForSingleObject$MH;
    static final MethodHandle GetStdHandle$MH;
    static final MethodHandle FormatMessageW$MH;
    static final MethodHandle SetConsoleTextAttribute$MH;
    static final MethodHandle SetConsoleMode$MH;
    static final MethodHandle GetConsoleMode$MH;
    static final MethodHandle SetConsoleTitleW$MH;
    static final MethodHandle SetConsoleCursorPosition$MH;
    static final MethodHandle FillConsoleOutputCharacterW$MH;
    static final MethodHandle FillConsoleOutputAttribute$MH;
    static final MethodHandle WriteConsoleW$MH;
    static final MethodHandle ReadConsoleInputW$MH;
    static final MethodHandle PeekConsoleInputW$MH;
    static final MethodHandle GetConsoleScreenBufferInfo$MH;
    static final MethodHandle ScrollConsoleScreenBufferW$MH;
    static final MethodHandle GetLastError$MH;
    static final MethodHandle GetFileType$MH;
    static final MethodHandle _get_osfhandle$MH;

    Kernel32() {
    }

    public static int WaitForSingleObject(MemorySegment hHandle, int dwMilliseconds) {
        MethodHandle mh$ = Kernel32.requireNonNull(WaitForSingleObject$MH, "WaitForSingleObject");
        try {
            return mh$.invokeExact(hHandle, dwMilliseconds);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static MemorySegment GetStdHandle(int nStdHandle) {
        MethodHandle mh$ = Kernel32.requireNonNull(GetStdHandle$MH, "GetStdHandle");
        try {
            return mh$.invokeExact(nStdHandle);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int FormatMessageW(int dwFlags, MemorySegment lpSource, int dwMessageId, int dwLanguageId, MemorySegment lpBuffer, int nSize, MemorySegment Arguments2) {
        MethodHandle mh$ = Kernel32.requireNonNull(FormatMessageW$MH, "FormatMessageW");
        try {
            return mh$.invokeExact(dwFlags, lpSource, dwMessageId, dwLanguageId, lpBuffer, nSize, Arguments2);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int SetConsoleTextAttribute(MemorySegment hConsoleOutput, short wAttributes) {
        MethodHandle mh$ = Kernel32.requireNonNull(SetConsoleTextAttribute$MH, "SetConsoleTextAttribute");
        try {
            return mh$.invokeExact(hConsoleOutput, wAttributes);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int SetConsoleMode(MemorySegment hConsoleHandle, int dwMode) {
        MethodHandle mh$ = Kernel32.requireNonNull(SetConsoleMode$MH, "SetConsoleMode");
        try {
            return mh$.invokeExact(hConsoleHandle, dwMode);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int GetConsoleMode(MemorySegment hConsoleHandle, MemorySegment lpMode) {
        MethodHandle mh$ = Kernel32.requireNonNull(GetConsoleMode$MH, "GetConsoleMode");
        try {
            return mh$.invokeExact(hConsoleHandle, lpMode);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int SetConsoleTitleW(MemorySegment lpConsoleTitle) {
        MethodHandle mh$ = Kernel32.requireNonNull(SetConsoleTitleW$MH, "SetConsoleTitleW");
        try {
            return mh$.invokeExact(lpConsoleTitle);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int SetConsoleCursorPosition(MemorySegment hConsoleOutput, COORD dwCursorPosition) {
        MethodHandle mh$ = Kernel32.requireNonNull(SetConsoleCursorPosition$MH, "SetConsoleCursorPosition");
        try {
            return mh$.invokeExact(hConsoleOutput, dwCursorPosition.seg);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int FillConsoleOutputCharacterW(MemorySegment hConsoleOutput, char cCharacter, int nLength, COORD dwWriteCoord, MemorySegment lpNumberOfCharsWritten) {
        MethodHandle mh$ = Kernel32.requireNonNull(FillConsoleOutputCharacterW$MH, "FillConsoleOutputCharacterW");
        try {
            return mh$.invokeExact(hConsoleOutput, cCharacter, nLength, dwWriteCoord.seg, lpNumberOfCharsWritten);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int FillConsoleOutputAttribute(MemorySegment hConsoleOutput, short wAttribute, int nLength, COORD dwWriteCoord, MemorySegment lpNumberOfAttrsWritten) {
        MethodHandle mh$ = Kernel32.requireNonNull(FillConsoleOutputAttribute$MH, "FillConsoleOutputAttribute");
        try {
            return mh$.invokeExact(hConsoleOutput, wAttribute, nLength, dwWriteCoord.seg, lpNumberOfAttrsWritten);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int WriteConsoleW(MemorySegment hConsoleOutput, MemorySegment lpBuffer, int nNumberOfCharsToWrite, MemorySegment lpNumberOfCharsWritten, MemorySegment lpReserved) {
        MethodHandle mh$ = Kernel32.requireNonNull(WriteConsoleW$MH, "WriteConsoleW");
        try {
            return mh$.invokeExact(hConsoleOutput, lpBuffer, nNumberOfCharsToWrite, lpNumberOfCharsWritten, lpReserved);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int ReadConsoleInputW(MemorySegment hConsoleInput, MemorySegment lpBuffer, int nLength, MemorySegment lpNumberOfEventsRead) {
        MethodHandle mh$ = Kernel32.requireNonNull(ReadConsoleInputW$MH, "ReadConsoleInputW");
        try {
            return mh$.invokeExact(hConsoleInput, lpBuffer, nLength, lpNumberOfEventsRead);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int PeekConsoleInputW(MemorySegment hConsoleInput, MemorySegment lpBuffer, int nLength, MemorySegment lpNumberOfEventsRead) {
        MethodHandle mh$ = Kernel32.requireNonNull(PeekConsoleInputW$MH, "PeekConsoleInputW");
        try {
            return mh$.invokeExact(hConsoleInput, lpBuffer, nLength, lpNumberOfEventsRead);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int GetConsoleScreenBufferInfo(MemorySegment hConsoleOutput, CONSOLE_SCREEN_BUFFER_INFO lpConsoleScreenBufferInfo) {
        MethodHandle mh$ = Kernel32.requireNonNull(GetConsoleScreenBufferInfo$MH, "GetConsoleScreenBufferInfo");
        try {
            return mh$.invokeExact(hConsoleOutput, lpConsoleScreenBufferInfo.seg);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int ScrollConsoleScreenBuffer(MemorySegment hConsoleOutput, SMALL_RECT lpScrollRectangle, SMALL_RECT lpClipRectangle, COORD dwDestinationOrigin, CHAR_INFO lpFill) {
        MethodHandle mh$ = Kernel32.requireNonNull(ScrollConsoleScreenBufferW$MH, "ScrollConsoleScreenBuffer");
        try {
            return mh$.invokeExact(hConsoleOutput, lpScrollRectangle.seg, lpClipRectangle.seg, dwDestinationOrigin.seg, lpFill.seg);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int GetLastError() {
        MethodHandle mh$ = Kernel32.requireNonNull(GetLastError$MH, "GetLastError");
        try {
            return mh$.invokeExact();
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static int GetFileType(MemorySegment hFile) {
        MethodHandle mh$ = Kernel32.requireNonNull(GetFileType$MH, "GetFileType");
        try {
            return mh$.invokeExact(hFile);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static MemorySegment _get_osfhandle(int fd) {
        MethodHandle mh$ = Kernel32.requireNonNull(_get_osfhandle$MH, "_get_osfhandle");
        try {
            return mh$.invokeExact(fd);
        }
        catch (Throwable ex$) {
            throw new AssertionError("should not reach here", ex$);
        }
    }

    public static INPUT_RECORD[] readConsoleInputHelper(MemorySegment handle, int count, boolean peek) throws IOException {
        return Kernel32.readConsoleInputHelper(Arena.ofAuto(), handle, count, peek);
    }

    public static INPUT_RECORD[] readConsoleInputHelper(Arena arena, MemorySegment handle, int count, boolean peek) throws IOException {
        int res;
        MemorySegment inputRecordPtr = arena.allocate(INPUT_RECORD.LAYOUT, count);
        MemorySegment length = arena.allocate(ValueLayout.JAVA_INT, 1L);
        int n = res = peek ? Kernel32.PeekConsoleInputW(handle, inputRecordPtr, count, length) : Kernel32.ReadConsoleInputW(handle, inputRecordPtr, count, length);
        if (res == 0) {
            throw new IOException("ReadConsoleInputW failed: " + Kernel32.getLastErrorMessage());
        }
        int len = length.get(ValueLayout.JAVA_INT, 0L);
        return (INPUT_RECORD[])inputRecordPtr.elements(INPUT_RECORD.LAYOUT).map(INPUT_RECORD::new).limit(len).toArray(INPUT_RECORD[]::new);
    }

    public static String getLastErrorMessage() {
        int errorCode = Kernel32.GetLastError();
        return Kernel32.getErrorMessage(errorCode);
    }

    public static String getErrorMessage(int errorCode) {
        int bufferSize = 160;
        try (Arena arena = Arena.ofConfined();){
            MemorySegment data = arena.allocate(bufferSize);
            Kernel32.FormatMessageW(4096, MemorySegment.NULL, errorCode, 0, data, bufferSize, MemorySegment.NULL);
            String string = new String(data.toArray(ValueLayout.JAVA_BYTE), StandardCharsets.UTF_16LE).trim();
            return string;
        }
    }

    static MethodHandle downcallHandle(String name, FunctionDescriptor fdesc) {
        return SYMBOL_LOOKUP.find(name).map(addr -> Linker.nativeLinker().downcallHandle((MemorySegment)addr, fdesc, new Linker.Option[0])).orElse(null);
    }

    static <T> T requireNonNull(T obj, String symbolName) {
        if (obj == null) {
            throw new UnsatisfiedLinkError("unresolved symbol: " + symbolName);
        }
        return obj;
    }

    static VarHandle varHandle(MemoryLayout layout, String name) {
        return FfmTerminalProvider.lookupVarHandle(layout, MemoryLayout.PathElement.groupElement(name));
    }

    static VarHandle varHandle(MemoryLayout layout, String e1, String name) {
        return FfmTerminalProvider.lookupVarHandle(layout, MemoryLayout.PathElement.groupElement(e1), MemoryLayout.PathElement.groupElement(name));
    }

    static long byteOffset(MemoryLayout layout, String name) {
        return layout.byteOffset(MemoryLayout.PathElement.groupElement(name));
    }

    static {
        System.loadLibrary("msvcrt");
        System.loadLibrary("Kernel32");
        SYMBOL_LOOKUP = SymbolLookup.loaderLookup();
        C_BOOL$LAYOUT = ValueLayout.JAVA_BOOLEAN;
        C_CHAR$LAYOUT = ValueLayout.JAVA_BYTE;
        C_WCHAR$LAYOUT = ValueLayout.JAVA_CHAR;
        C_SHORT$LAYOUT = ValueLayout.JAVA_SHORT;
        C_WORD$LAYOUT = ValueLayout.JAVA_SHORT;
        C_DWORD$LAYOUT = ValueLayout.JAVA_INT;
        C_INT$LAYOUT = ValueLayout.JAVA_INT;
        C_LONG$LAYOUT = ValueLayout.JAVA_LONG;
        C_LONG_LONG$LAYOUT = ValueLayout.JAVA_LONG;
        C_FLOAT$LAYOUT = ValueLayout.JAVA_FLOAT;
        C_DOUBLE$LAYOUT = ValueLayout.JAVA_DOUBLE;
        C_POINTER$LAYOUT = ValueLayout.ADDRESS;
        WaitForSingleObject$MH = Kernel32.downcallHandle("WaitForSingleObject", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT));
        GetStdHandle$MH = Kernel32.downcallHandle("GetStdHandle", FunctionDescriptor.of(C_POINTER$LAYOUT, C_INT$LAYOUT));
        FormatMessageW$MH = Kernel32.downcallHandle("FormatMessageW", FunctionDescriptor.of(C_INT$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT));
        SetConsoleTextAttribute$MH = Kernel32.downcallHandle("SetConsoleTextAttribute", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT));
        SetConsoleMode$MH = Kernel32.downcallHandle("SetConsoleMode", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT));
        GetConsoleMode$MH = Kernel32.downcallHandle("GetConsoleMode", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT));
        SetConsoleTitleW$MH = Kernel32.downcallHandle("SetConsoleTitleW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT));
        SetConsoleCursorPosition$MH = Kernel32.downcallHandle("SetConsoleCursorPosition", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, COORD.LAYOUT));
        FillConsoleOutputCharacterW$MH = Kernel32.downcallHandle("FillConsoleOutputCharacterW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_WCHAR$LAYOUT, C_INT$LAYOUT, COORD.LAYOUT, C_POINTER$LAYOUT));
        FillConsoleOutputAttribute$MH = Kernel32.downcallHandle("FillConsoleOutputAttribute", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_SHORT$LAYOUT, C_INT$LAYOUT, COORD.LAYOUT, C_POINTER$LAYOUT));
        WriteConsoleW$MH = Kernel32.downcallHandle("WriteConsoleW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT));
        ReadConsoleInputW$MH = Kernel32.downcallHandle("ReadConsoleInputW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT));
        PeekConsoleInputW$MH = Kernel32.downcallHandle("PeekConsoleInputW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_INT$LAYOUT, C_POINTER$LAYOUT));
        GetConsoleScreenBufferInfo$MH = Kernel32.downcallHandle("GetConsoleScreenBufferInfo", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT));
        ScrollConsoleScreenBufferW$MH = Kernel32.downcallHandle("ScrollConsoleScreenBufferW", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, C_POINTER$LAYOUT, COORD.LAYOUT, C_POINTER$LAYOUT));
        GetLastError$MH = Kernel32.downcallHandle("GetLastError", FunctionDescriptor.of(C_INT$LAYOUT, new MemoryLayout[0]));
        GetFileType$MH = Kernel32.downcallHandle("GetFileType", FunctionDescriptor.of(C_INT$LAYOUT, C_POINTER$LAYOUT));
        _get_osfhandle$MH = Kernel32.downcallHandle("_get_osfhandle", FunctionDescriptor.of(C_POINTER$LAYOUT, C_INT$LAYOUT));
    }

    public static final class COORD {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(C_SHORT$LAYOUT.withName("x"), C_SHORT$LAYOUT.withName("y"));
        static final VarHandle x$VH = Kernel32.varHandle(LAYOUT, "x");
        static final VarHandle y$VH = Kernel32.varHandle(LAYOUT, "y");
        private final MemorySegment seg;

        public COORD() {
            this(Arena.ofAuto());
        }

        public COORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public COORD(Arena arena, short x, short y) {
            this(arena.allocate(LAYOUT));
            this.x(x);
            this.y(y);
        }

        public COORD(MemorySegment seg) {
            this.seg = seg;
        }

        public COORD(MemorySegment seg, long offset) {
            this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
        }

        public short x() {
            return x$VH.get(this.seg);
        }

        public void x(short x) {
            x$VH.set(this.seg, x);
        }

        public short y() {
            return y$VH.get(this.seg);
        }

        public void y(short y) {
            y$VH.set(this.seg, y);
        }

        public COORD copy(Arena arena) {
            return new COORD(arena.allocate(LAYOUT).copyFrom(this.seg));
        }
    }

    public static final class CONSOLE_SCREEN_BUFFER_INFO {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(COORD.LAYOUT.withName("dwSize"), COORD.LAYOUT.withName("dwCursorPosition"), C_WORD$LAYOUT.withName("wAttributes"), SMALL_RECT.LAYOUT.withName("srWindow"), COORD.LAYOUT.withName("dwMaximumWindowSize"));
        static final long dwSize$OFFSET = Kernel32.byteOffset(LAYOUT, "dwSize");
        static final long dwCursorPosition$OFFSET = Kernel32.byteOffset(LAYOUT, "dwCursorPosition");
        static final VarHandle wAttributes$VH = Kernel32.varHandle(LAYOUT, "wAttributes");
        static final long srWindow$OFFSET = Kernel32.byteOffset(LAYOUT, "srWindow");
        private final MemorySegment seg;

        public CONSOLE_SCREEN_BUFFER_INFO() {
            this(Arena.ofAuto());
        }

        public CONSOLE_SCREEN_BUFFER_INFO(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public CONSOLE_SCREEN_BUFFER_INFO(MemorySegment seg) {
            this.seg = seg;
        }

        public COORD size() {
            return new COORD(this.seg, dwSize$OFFSET);
        }

        public COORD cursorPosition() {
            return new COORD(this.seg, dwCursorPosition$OFFSET);
        }

        public short attributes() {
            return wAttributes$VH.get(this.seg);
        }

        public SMALL_RECT window() {
            return new SMALL_RECT(this.seg, srWindow$OFFSET);
        }

        public int windowWidth() {
            return this.window().width() + 1;
        }

        public int windowHeight() {
            return this.window().height() + 1;
        }

        public void attributes(short attr) {
            wAttributes$VH.set(this.seg, attr);
        }
    }

    public static final class SMALL_RECT {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(C_SHORT$LAYOUT.withName("Left"), C_SHORT$LAYOUT.withName("Top"), C_SHORT$LAYOUT.withName("Right"), C_SHORT$LAYOUT.withName("Bottom"));
        static final VarHandle Left$VH = Kernel32.varHandle(LAYOUT, "Left");
        static final VarHandle Top$VH = Kernel32.varHandle(LAYOUT, "Top");
        static final VarHandle Right$VH = Kernel32.varHandle(LAYOUT, "Right");
        static final VarHandle Bottom$VH = Kernel32.varHandle(LAYOUT, "Bottom");
        private final MemorySegment seg;

        public SMALL_RECT() {
            this(Arena.ofAuto());
        }

        public SMALL_RECT(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public SMALL_RECT(Arena arena, SMALL_RECT rect) {
            this(arena);
            this.left(rect.left());
            this.right(rect.right());
            this.top(rect.top());
            this.bottom(rect.bottom());
        }

        public SMALL_RECT(MemorySegment seg, long offset) {
            this(seg.asSlice(offset, LAYOUT.byteSize()));
        }

        public SMALL_RECT(MemorySegment seg) {
            this.seg = seg;
        }

        public short left() {
            return Left$VH.get(this.seg);
        }

        public short top() {
            return Top$VH.get(this.seg);
        }

        public short right() {
            return Right$VH.get(this.seg);
        }

        public short bottom() {
            return Bottom$VH.get(this.seg);
        }

        public short width() {
            return (short)(this.right() - this.left());
        }

        public short height() {
            return (short)(this.bottom() - this.top());
        }

        public void left(short l) {
            Left$VH.set(this.seg, l);
        }

        public void top(short t) {
            Top$VH.set(this.seg, t);
        }

        public void right(short r) {
            Right$VH.set(this.seg, r);
        }

        public void bottom(short b) {
            Bottom$VH.set(this.seg, b);
        }

        public SMALL_RECT copy(Arena arena) {
            return new SMALL_RECT(arena.allocate(LAYOUT).copyFrom(this.seg));
        }
    }

    public static final class CHAR_INFO {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(MemoryLayout.unionLayout(C_WCHAR$LAYOUT.withName("UnicodeChar"), C_CHAR$LAYOUT.withName("AsciiChar")).withName("Char"), C_WORD$LAYOUT.withName("Attributes"));
        static final VarHandle UnicodeChar$VH = Kernel32.varHandle(LAYOUT, "Char", "UnicodeChar");
        static final VarHandle Attributes$VH = Kernel32.varHandle(LAYOUT, "Attributes");
        final MemorySegment seg;

        public CHAR_INFO() {
            this(Arena.ofAuto());
        }

        public CHAR_INFO(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public CHAR_INFO(Arena arena, char c, short a) {
            this(arena);
            UnicodeChar$VH.set(this.seg, c);
            Attributes$VH.set(this.seg, a);
        }

        public CHAR_INFO(MemorySegment seg) {
            this.seg = seg;
        }

        public char unicodeChar() {
            return UnicodeChar$VH.get(this.seg);
        }
    }

    public static final class INPUT_RECORD {
        static final MemoryLayout LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_SHORT.withName("EventType"), ValueLayout.JAVA_SHORT, MemoryLayout.unionLayout(KEY_EVENT_RECORD.LAYOUT.withName("KeyEvent"), MOUSE_EVENT_RECORD.LAYOUT.withName("MouseEvent"), WINDOW_BUFFER_SIZE_RECORD.LAYOUT.withName("WindowBufferSizeEvent"), MENU_EVENT_RECORD.LAYOUT.withName("MenuEvent"), FOCUS_EVENT_RECORD.LAYOUT.withName("FocusEvent")).withName("Event"));
        static final VarHandle EventType$VH = Kernel32.varHandle(LAYOUT, "EventType");
        static final long Event$OFFSET = Kernel32.byteOffset(LAYOUT, "Event");
        private final MemorySegment seg;

        public INPUT_RECORD() {
            this(Arena.ofAuto());
        }

        public INPUT_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public INPUT_RECORD(MemorySegment seg) {
            this.seg = seg;
        }

        public short eventType() {
            return EventType$VH.get(this.seg);
        }

        public KEY_EVENT_RECORD keyEvent() {
            return new KEY_EVENT_RECORD(this.seg, Event$OFFSET);
        }

        public MOUSE_EVENT_RECORD mouseEvent() {
            return new MOUSE_EVENT_RECORD(this.seg, Event$OFFSET);
        }

        public FOCUS_EVENT_RECORD focusEvent() {
            return new FOCUS_EVENT_RECORD(this.seg, Event$OFFSET);
        }
    }

    public static final class KEY_EVENT_RECORD {
        static final MemoryLayout LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("bKeyDown"), ValueLayout.JAVA_SHORT.withName("wRepeatCount"), ValueLayout.JAVA_SHORT.withName("wVirtualKeyCode"), ValueLayout.JAVA_SHORT.withName("wVirtualScanCode"), MemoryLayout.unionLayout(ValueLayout.JAVA_CHAR.withName("UnicodeChar"), ValueLayout.JAVA_BYTE.withName("AsciiChar")).withName("uChar"), ValueLayout.JAVA_INT.withName("dwControlKeyState"));
        static final VarHandle bKeyDown$VH = Kernel32.varHandle(LAYOUT, "bKeyDown");
        static final VarHandle wRepeatCount$VH = Kernel32.varHandle(LAYOUT, "wRepeatCount");
        static final VarHandle wVirtualKeyCode$VH = Kernel32.varHandle(LAYOUT, "wVirtualKeyCode");
        static final VarHandle wVirtualScanCode$VH = Kernel32.varHandle(LAYOUT, "wVirtualScanCode");
        static final VarHandle UnicodeChar$VH = Kernel32.varHandle(LAYOUT, "uChar", "UnicodeChar");
        static final VarHandle AsciiChar$VH = Kernel32.varHandle(LAYOUT, "uChar", "AsciiChar");
        static final VarHandle dwControlKeyState$VH = Kernel32.varHandle(LAYOUT, "dwControlKeyState");
        final MemorySegment seg;

        public KEY_EVENT_RECORD() {
            this(Arena.ofAuto());
        }

        public KEY_EVENT_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public KEY_EVENT_RECORD(MemorySegment seg) {
            this.seg = seg;
        }

        public KEY_EVENT_RECORD(MemorySegment seg, long offset) {
            this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
        }

        public boolean keyDown() {
            return bKeyDown$VH.get(this.seg) != 0;
        }

        public int repeatCount() {
            return wRepeatCount$VH.get(this.seg);
        }

        public short keyCode() {
            return wVirtualKeyCode$VH.get(this.seg);
        }

        public short scanCode() {
            return wVirtualScanCode$VH.get(this.seg);
        }

        public char uchar() {
            return UnicodeChar$VH.get(this.seg);
        }

        public int controlKeyState() {
            return dwControlKeyState$VH.get(this.seg);
        }

        public String toString() {
            return "KEY_EVENT_RECORD{keyDown=" + this.keyDown() + ", repeatCount=" + this.repeatCount() + ", keyCode=" + this.keyCode() + ", scanCode=" + this.scanCode() + ", uchar=" + this.uchar() + ", controlKeyState=" + this.controlKeyState() + "}";
        }
    }

    public static final class MOUSE_EVENT_RECORD {
        static final MemoryLayout LAYOUT = MemoryLayout.structLayout(COORD.LAYOUT.withName("dwMousePosition"), C_DWORD$LAYOUT.withName("dwButtonState"), C_DWORD$LAYOUT.withName("dwControlKeyState"), C_DWORD$LAYOUT.withName("dwEventFlags"));
        static final long MOUSE_POSITION_OFFSET = Kernel32.byteOffset(LAYOUT, "dwMousePosition");
        static final VarHandle BUTTON_STATE = Kernel32.varHandle(LAYOUT, "dwButtonState");
        static final VarHandle CONTROL_KEY_STATE = Kernel32.varHandle(LAYOUT, "dwControlKeyState");
        static final VarHandle EVENT_FLAGS = Kernel32.varHandle(LAYOUT, "dwEventFlags");
        private final MemorySegment seg;

        public MOUSE_EVENT_RECORD() {
            this(Arena.ofAuto());
        }

        public MOUSE_EVENT_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public MOUSE_EVENT_RECORD(MemorySegment seg) {
            this.seg = Objects.requireNonNull(seg);
        }

        public MOUSE_EVENT_RECORD(MemorySegment seg, long offset) {
            this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
        }

        public COORD mousePosition() {
            return new COORD(this.seg, MOUSE_POSITION_OFFSET);
        }

        public int buttonState() {
            return BUTTON_STATE.get(this.seg);
        }

        public int controlKeyState() {
            return CONTROL_KEY_STATE.get(this.seg);
        }

        public int eventFlags() {
            return EVENT_FLAGS.get(this.seg);
        }

        public String toString() {
            return "MOUSE_EVENT_RECORD{mousePosition=" + String.valueOf(this.mousePosition()) + ", buttonState=" + this.buttonState() + ", controlKeyState=" + this.controlKeyState() + ", eventFlags=" + this.eventFlags() + "}";
        }
    }

    public static final class WINDOW_BUFFER_SIZE_RECORD {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(COORD.LAYOUT.withName("size"));
        static final long SIZE_OFFSET = Kernel32.byteOffset(LAYOUT, "size");
        private final MemorySegment seg;

        public WINDOW_BUFFER_SIZE_RECORD() {
            this(Arena.ofAuto());
        }

        public WINDOW_BUFFER_SIZE_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public WINDOW_BUFFER_SIZE_RECORD(MemorySegment seg) {
            this.seg = seg;
        }

        public COORD size() {
            return new COORD(this.seg, SIZE_OFFSET);
        }

        public String toString() {
            return "WINDOW_BUFFER_SIZE_RECORD{size=" + String.valueOf(this.size()) + "}";
        }
    }

    public static final class FOCUS_EVENT_RECORD {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(C_INT$LAYOUT.withName("bSetFocus"));
        static final VarHandle SET_FOCUS = Kernel32.varHandle(LAYOUT, "bSetFocus");
        private final MemorySegment seg;

        public FOCUS_EVENT_RECORD() {
            this(Arena.ofAuto());
        }

        public FOCUS_EVENT_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public FOCUS_EVENT_RECORD(MemorySegment seg) {
            this.seg = Objects.requireNonNull(seg);
        }

        public FOCUS_EVENT_RECORD(MemorySegment seg, long offset) {
            this.seg = Objects.requireNonNull(seg).asSlice(offset, LAYOUT.byteSize());
        }

        public boolean setFocus() {
            return SET_FOCUS.get(this.seg) != 0;
        }

        public void setFocus(boolean setFocus) {
            SET_FOCUS.set(this.seg, setFocus ? 1 : 0);
        }
    }

    public static final class MENU_EVENT_RECORD {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(C_DWORD$LAYOUT.withName("dwCommandId"));
        static final VarHandle COMMAND_ID = Kernel32.varHandle(LAYOUT, "dwCommandId");
        private final MemorySegment seg;

        public MENU_EVENT_RECORD() {
            this(Arena.ofAuto());
        }

        public MENU_EVENT_RECORD(Arena arena) {
            this(arena.allocate(LAYOUT));
        }

        public MENU_EVENT_RECORD(MemorySegment seg) {
            this.seg = seg;
        }

        public int commandId() {
            return COMMAND_ID.get(this.seg);
        }

        public void commandId(int commandId) {
            COMMAND_ID.set(this.seg, commandId);
        }
    }
}

