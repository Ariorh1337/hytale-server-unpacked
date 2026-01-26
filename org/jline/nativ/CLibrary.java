/*
 * Decompiled with CFR 0.152.
 */
package org.jline.nativ;

import org.jline.nativ.JLineNativeLoader;

public class CLibrary {
    public static int TCSANOW;
    public static int TCSADRAIN;
    public static int TCSAFLUSH;
    public static long TIOCGWINSZ;
    public static long TIOCSWINSZ;

    private static native void init();

    public static native int isatty(int var0);

    public static native String ttyname(int var0);

    public static native int openpty(int[] var0, int[] var1, byte[] var2, Termios var3, WinSize var4);

    public static native int tcgetattr(int var0, Termios var1);

    public static native int tcsetattr(int var0, int var1, Termios var2);

    public static native int ioctl(int var0, long var1, int[] var3);

    public static native int ioctl(int var0, long var1, WinSize var3);

    public static short getTerminalWidth(int fd) {
        WinSize sz = new WinSize();
        CLibrary.ioctl(fd, TIOCGWINSZ, sz);
        return sz.ws_col;
    }

    static {
        if (JLineNativeLoader.initialize()) {
            CLibrary.init();
        }
    }

    public static class WinSize {
        public static int SIZEOF;
        public short ws_row;
        public short ws_col;
        public short ws_xpixel;
        public short ws_ypixel;

        private static native void init();

        public WinSize() {
        }

        public WinSize(short ws_row, short ws_col) {
            this.ws_row = ws_row;
            this.ws_col = ws_col;
        }

        static {
            if (JLineNativeLoader.initialize()) {
                WinSize.init();
            }
        }
    }

    public static class Termios {
        public static int SIZEOF;
        public long c_iflag;
        public long c_oflag;
        public long c_cflag;
        public long c_lflag;
        public byte[] c_cc = new byte[32];
        public long c_ispeed;
        public long c_ospeed;

        private static native void init();

        static {
            if (JLineNativeLoader.initialize()) {
                Termios.init();
            }
        }
    }
}

