/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Native
 *  com.sun.jna.Platform
 */
package org.jline.terminal.impl.jna.solaris;

import com.sun.jna.Native;
import com.sun.jna.Platform;
import java.io.FileDescriptor;
import java.io.IOException;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.jna.JnaNativePty;
import org.jline.terminal.impl.jna.solaris.CLibrary;
import org.jline.terminal.spi.SystemStream;
import org.jline.terminal.spi.TerminalProvider;

public class SolarisNativePty
extends JnaNativePty {
    private static final CLibrary C_LIBRARY = (CLibrary)Native.load((String)Platform.C_LIBRARY_NAME, CLibrary.class);

    public static SolarisNativePty current(TerminalProvider provider, SystemStream systemStream) throws IOException {
        switch (systemStream) {
            case Output: {
                return new SolarisNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 1, FileDescriptor.out, SolarisNativePty.ttyname(0));
            }
            case Error: {
                return new SolarisNativePty(provider, systemStream, -1, null, 0, FileDescriptor.in, 2, FileDescriptor.err, SolarisNativePty.ttyname(0));
            }
        }
        throw new IllegalArgumentException("Unsupported stream for console: " + (Object)((Object)systemStream));
    }

    public static SolarisNativePty open(TerminalProvider provider, Attributes attr, Size size) throws IOException {
        int[] master = new int[1];
        int[] slave = new int[1];
        byte[] buf = new byte[64];
        C_LIBRARY.openpty(master, slave, buf, attr != null ? new CLibrary.termios(attr) : null, size != null ? new CLibrary.winsize(size) : null);
        int len = 0;
        while (buf[len] != 0) {
            ++len;
        }
        String name = new String(buf, 0, len);
        return new SolarisNativePty(provider, null, master[0], SolarisNativePty.newDescriptor(master[0]), slave[0], SolarisNativePty.newDescriptor(slave[0]), name);
    }

    public SolarisNativePty(TerminalProvider provider, SystemStream systemStream, int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, String name) {
        super(provider, systemStream, master, masterFD, slave, slaveFD, name);
    }

    public SolarisNativePty(TerminalProvider provider, SystemStream systemStream, int master, FileDescriptor masterFD, int slave, FileDescriptor slaveFD, int slaveOut, FileDescriptor slaveOutFD, String name) {
        super(provider, systemStream, master, masterFD, slave, slaveFD, slaveOut, slaveOutFD, name);
    }

    @Override
    public Attributes getAttr() throws IOException {
        CLibrary.termios termios2 = new CLibrary.termios();
        C_LIBRARY.tcgetattr(this.getSlave(), termios2);
        return termios2.toAttributes();
    }

    @Override
    protected void doSetAttr(Attributes attr) throws IOException {
        CLibrary.termios termios2 = new CLibrary.termios(attr);
        C_LIBRARY.tcsetattr(this.getSlave(), 0, termios2);
    }

    @Override
    public Size getSize() throws IOException {
        CLibrary.winsize sz = new CLibrary.winsize();
        C_LIBRARY.ioctl(this.getSlave(), 21608L, sz);
        return sz.toSize();
    }

    @Override
    public void setSize(Size size) throws IOException {
        CLibrary.winsize sz = new CLibrary.winsize(size);
        C_LIBRARY.ioctl(this.getSlave(), 21607L, sz);
    }

    public static int isatty(int fd) {
        return C_LIBRARY.isatty(fd);
    }

    public static String ttyname(int slave) {
        byte[] buf = new byte[64];
        C_LIBRARY.ttyname_r(slave, buf, buf.length);
        int len = 0;
        while (buf[len] != 0) {
            ++len;
        }
        return new String(buf, 0, len);
    }
}

