/*
 * Decompiled with CFR 0.152.
 */
package org.jline.terminal.impl.ffm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.impl.ffm.FfmNativePty;
import org.jline.terminal.impl.ffm.FfmTerminalProvider;
import org.jline.terminal.spi.Pty;
import org.jline.terminal.spi.TerminalProvider;
import org.jline.utils.OSUtils;

class CLibrary {
    private static final Logger logger = Logger.getLogger("org.jline");
    static final MethodHandle ioctl;
    static final MethodHandle isatty;
    static final MethodHandle openpty;
    static final MethodHandle tcsetattr;
    static final MethodHandle tcgetattr;
    static final MethodHandle ttyname_r;
    static LinkageError openptyError;
    private static final int TIOCGWINSZ;
    private static final int TIOCSWINSZ;
    private static final int TCSANOW;
    private static int TCSADRAIN;
    private static int TCSAFLUSH;
    private static final int VEOF;
    private static final int VEOL;
    private static final int VEOL2;
    private static final int VERASE;
    private static final int VWERASE;
    private static final int VKILL;
    private static final int VREPRINT;
    private static final int VERASE2;
    private static final int VINTR;
    private static final int VQUIT;
    private static final int VSUSP;
    private static final int VDSUSP;
    private static final int VSTART;
    private static final int VSTOP;
    private static final int VLNEXT;
    private static final int VDISCARD;
    private static final int VMIN;
    private static final int VSWTC;
    private static final int VTIME;
    private static final int VSTATUS;
    private static final int IGNBRK;
    private static final int BRKINT;
    private static final int IGNPAR;
    private static final int PARMRK;
    private static final int INPCK;
    private static final int ISTRIP;
    private static final int INLCR;
    private static final int IGNCR;
    private static final int ICRNL;
    private static int IUCLC;
    private static final int IXON;
    private static final int IXOFF;
    private static final int IXANY;
    private static final int IMAXBEL;
    private static int IUTF8;
    private static final int OPOST;
    private static int OLCUC;
    private static final int ONLCR;
    private static int OXTABS;
    private static int NLDLY;
    private static int NL0;
    private static int NL1;
    private static final int TABDLY;
    private static int TAB0;
    private static int TAB1;
    private static int TAB2;
    private static int TAB3;
    private static int CRDLY;
    private static int CR0;
    private static int CR1;
    private static int CR2;
    private static int CR3;
    private static int FFDLY;
    private static int FF0;
    private static int FF1;
    private static int XTABS;
    private static int BSDLY;
    private static int BS0;
    private static int BS1;
    private static int VTDLY;
    private static int VT0;
    private static int VT1;
    private static int CBAUD;
    private static int B0;
    private static int B50;
    private static int B75;
    private static int B110;
    private static int B134;
    private static int B150;
    private static int B200;
    private static int B300;
    private static int B600;
    private static int B1200;
    private static int B1800;
    private static int B2400;
    private static int B4800;
    private static int B9600;
    private static int B19200;
    private static int B38400;
    private static int EXTA;
    private static int EXTB;
    private static int OFDEL;
    private static int ONOEOT;
    private static final int OCRNL;
    private static int ONOCR;
    private static final int ONLRET;
    private static int OFILL;
    private static int CIGNORE;
    private static int CSIZE;
    private static final int CS5;
    private static final int CS6;
    private static final int CS7;
    private static final int CS8;
    private static final int CSTOPB;
    private static final int CREAD;
    private static final int PARENB;
    private static final int PARODD;
    private static final int HUPCL;
    private static final int CLOCAL;
    private static int CCTS_OFLOW;
    private static int CRTS_IFLOW;
    private static int CDTR_IFLOW;
    private static int CDSR_OFLOW;
    private static int CCAR_OFLOW;
    private static final int ECHOKE;
    private static final int ECHOE;
    private static final int ECHOK;
    private static final int ECHO;
    private static final int ECHONL;
    private static final int ECHOPRT;
    private static final int ECHOCTL;
    private static final int ISIG;
    private static final int ICANON;
    private static int XCASE;
    private static int ALTWERASE;
    private static final int IEXTEN;
    private static final int EXTPROC;
    private static final int TOSTOP;
    private static final int FLUSHO;
    private static int NOKERNINFO;
    private static final int PENDIN;
    private static final int NOFLSH;

    CLibrary() {
    }

    private static String readFully(InputStream in) throws IOException {
        int readLen = 0;
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buf = new byte[32];
        while ((readLen = in.read(buf, 0, buf.length)) >= 0) {
            b.write(buf, 0, readLen);
        }
        return b.toString();
    }

    static Size getTerminalSize(int fd) {
        try {
            winsize ws = new winsize();
            int res = ioctl.invoke(fd, TIOCGWINSZ, ws.segment());
            return new Size(ws.ws_col(), ws.ws_row());
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call ioctl(TIOCGWINSZ)", e);
        }
    }

    static void setTerminalSize(int fd, Size size) {
        try {
            winsize ws = new winsize();
            ws.ws_row((short)size.getRows());
            ws.ws_col((short)size.getColumns());
            int n = ioctl.invoke(fd, TIOCSWINSZ, ws.segment());
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call ioctl(TIOCSWINSZ)", e);
        }
    }

    static Attributes getAttributes(int fd) {
        try {
            termios t = new termios();
            int res = tcgetattr.invoke(fd, t.segment());
            return t.asAttributes();
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call tcgetattr()", e);
        }
    }

    static void setAttributes(int fd, Attributes attr) {
        try {
            termios t = new termios(attr);
            int n = tcsetattr.invoke(fd, TCSANOW, t.segment());
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call tcsetattr()", e);
        }
    }

    static boolean isTty(int fd) {
        try {
            return isatty.invoke(fd) == 1;
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call isatty()", e);
        }
    }

    static String ttyName(int fd) {
        try {
            MemorySegment buf = Arena.ofAuto().allocate(64L);
            int res = ttyname_r.invoke(fd, buf, buf.byteSize());
            byte[] data = buf.toArray(ValueLayout.JAVA_BYTE);
            int len = 0;
            while (data[len] != 0) {
                ++len;
            }
            return new String(data, 0, len);
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call ttyname_r()", e);
        }
    }

    static Pty openpty(TerminalProvider provider, Attributes attr, Size size) {
        if (openptyError != null) {
            throw openptyError;
        }
        try {
            MemorySegment buf = Arena.ofAuto().allocate(64L);
            MemorySegment master = Arena.ofAuto().allocate(ValueLayout.JAVA_INT);
            MemorySegment slave = Arena.ofAuto().allocate(ValueLayout.JAVA_INT);
            int res = openpty.invoke(master, slave, buf, attr != null ? new termios(attr).segment() : MemorySegment.NULL, size != null ? new winsize((short)size.getRows(), (short)size.getColumns()).segment() : MemorySegment.NULL);
            byte[] str = buf.toArray(ValueLayout.JAVA_BYTE);
            int len = 0;
            while (str[len] != 0) {
                ++len;
            }
            String device = new String(str, 0, len);
            return new FfmNativePty(provider, null, master.get(ValueLayout.JAVA_INT, 0L), slave.get(ValueLayout.JAVA_INT, 0L), device);
        }
        catch (Throwable e) {
            throw new RuntimeException("Unable to call openpty()", e);
        }
    }

    static {
        Linker linker = Linker.nativeLinker();
        SymbolLookup lookup = SymbolLookup.loaderLookup().or(linker.defaultLookup());
        ioctl = linker.downcallHandle(lookup.find("ioctl").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_LONG, ValueLayout.ADDRESS), Linker.Option.firstVariadicArg(2));
        isatty = linker.downcallHandle(lookup.find("isatty").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT), new Linker.Option[0]);
        tcsetattr = linker.downcallHandle(lookup.find("tcsetattr").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS), new Linker.Option[0]);
        tcgetattr = linker.downcallHandle(lookup.find("tcgetattr").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS), new Linker.Option[0]);
        ttyname_r = linker.downcallHandle(lookup.find("ttyname_r").get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.JAVA_LONG), new Linker.Option[0]);
        LinkageError error = null;
        Optional<MemorySegment> openPtyAddr = lookup.find("openpty");
        if (openPtyAddr.isEmpty()) {
            String libUtilPath;
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to find openpty native method in static libraries and unable to load the util library.");
            ArrayList<Throwable> suppressed = new ArrayList<Throwable>();
            try {
                System.loadLibrary("util");
                openPtyAddr = lookup.find("openpty");
            }
            catch (Throwable t) {
                suppressed.add(t);
            }
            if (openPtyAddr.isEmpty() && (libUtilPath = System.getProperty("org.jline.ffm.libutil")) != null && !libUtilPath.isEmpty()) {
                try {
                    System.load(libUtilPath);
                    openPtyAddr = lookup.find("openpty");
                }
                catch (Throwable t) {
                    suppressed.add(t);
                }
            }
            if (openPtyAddr.isEmpty() && OSUtils.IS_LINUX) {
                try {
                    Process p = Runtime.getRuntime().exec(new String[]{"uname", "-m"});
                    p.waitFor();
                    try (InputStream in = p.getInputStream();){
                        String hwName = CLibrary.readFully(in).trim();
                        Path libDir = Paths.get("/usr/lib", hwName + "-linux-gnu");
                        try (Stream<Path> stream = Files.list(libDir);){
                            List libs = stream.filter(l -> l.getFileName().toString().startsWith("libutil.so.")).collect(Collectors.toList());
                            for (Path lib : libs) {
                                try {
                                    System.load(lib.toString());
                                    openPtyAddr = lookup.find("openpty");
                                    if (!openPtyAddr.isPresent()) continue;
                                    break;
                                }
                                catch (Throwable t) {
                                    suppressed.add(t);
                                }
                            }
                        }
                    }
                }
                catch (Throwable t) {
                    suppressed.add(t);
                }
            }
            if (openPtyAddr.isEmpty()) {
                for (Throwable t : suppressed) {
                    sb.append("\n\t- ").append(t.toString());
                }
                error = new LinkageError(sb.toString());
                suppressed.forEach(error::addSuppressed);
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.WARNING, error.getMessage(), error);
                } else {
                    logger.log(Level.WARNING, error.getMessage());
                }
            }
        }
        if (openPtyAddr.isPresent()) {
            openpty = linker.downcallHandle(openPtyAddr.get(), FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS), new Linker.Option[0]);
            openptyError = null;
        } else {
            openpty = null;
            openptyError = error;
        }
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Linux")) {
            String arch = System.getProperty("os.arch");
            boolean isMipsPpcOrSparc = arch.equals("mips") || arch.equals("mips64") || arch.equals("mipsel") || arch.equals("mips64el") || arch.startsWith("ppc") || arch.startsWith("sparc");
            TIOCGWINSZ = isMipsPpcOrSparc ? 1074295912 : 21523;
            TIOCSWINSZ = isMipsPpcOrSparc ? -2146929561 : 21524;
            TCSANOW = 0;
            TCSADRAIN = 1;
            TCSAFLUSH = 2;
            VINTR = 0;
            VQUIT = 1;
            VERASE = 2;
            VKILL = 3;
            VEOF = 4;
            VTIME = 5;
            VMIN = 6;
            VSWTC = 7;
            VSTART = 8;
            VSTOP = 9;
            VSUSP = 10;
            VEOL = 11;
            VREPRINT = 12;
            VDISCARD = 13;
            VWERASE = 14;
            VLNEXT = 15;
            VEOL2 = 16;
            VERASE2 = -1;
            VDSUSP = -1;
            VSTATUS = -1;
            IGNBRK = 1;
            BRKINT = 2;
            IGNPAR = 4;
            PARMRK = 8;
            INPCK = 16;
            ISTRIP = 32;
            INLCR = 64;
            IGNCR = 128;
            ICRNL = 256;
            IUCLC = 512;
            IXON = 1024;
            IXANY = 2048;
            IXOFF = 4096;
            IMAXBEL = 8192;
            IUTF8 = 16384;
            OPOST = 1;
            OLCUC = 2;
            ONLCR = 4;
            OCRNL = 8;
            ONOCR = 16;
            ONLRET = 32;
            OFILL = 64;
            OFDEL = 128;
            NLDLY = 256;
            NL0 = 0;
            NL1 = 256;
            CRDLY = 1536;
            CR0 = 0;
            CR1 = 512;
            CR2 = 1024;
            CR3 = 1536;
            TABDLY = 6144;
            TAB0 = 0;
            TAB1 = 2048;
            TAB2 = 4096;
            TAB3 = 6144;
            XTABS = 6144;
            BSDLY = 8192;
            BS0 = 0;
            BS1 = 8192;
            VTDLY = 16384;
            VT0 = 0;
            VT1 = 16384;
            FFDLY = 32768;
            FF0 = 0;
            FF1 = 32768;
            CBAUD = 4111;
            B0 = 0;
            B50 = 1;
            B75 = 2;
            B110 = 3;
            B134 = 4;
            B150 = 5;
            B200 = 6;
            B300 = 7;
            B600 = 8;
            B1200 = 9;
            B1800 = 10;
            B2400 = 11;
            B4800 = 12;
            B9600 = 13;
            B19200 = 14;
            B38400 = 15;
            EXTA = B19200;
            EXTB = B38400;
            CSIZE = 48;
            CS5 = 0;
            CS6 = 16;
            CS7 = 32;
            CS8 = 48;
            CSTOPB = 64;
            CREAD = 128;
            PARENB = 256;
            PARODD = 512;
            HUPCL = 1024;
            CLOCAL = 2048;
            ISIG = 1;
            ICANON = 2;
            XCASE = 4;
            ECHO = 8;
            ECHOE = 16;
            ECHOK = 32;
            ECHONL = 64;
            NOFLSH = 128;
            TOSTOP = 256;
            ECHOCTL = 512;
            ECHOPRT = 1024;
            ECHOKE = 2048;
            FLUSHO = 4096;
            PENDIN = 8192;
            IEXTEN = 32768;
            EXTPROC = 65536;
        } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
            int _TIOC = 21504;
            TIOCGWINSZ = _TIOC | 0x68;
            TIOCSWINSZ = _TIOC | 0x67;
            TCSANOW = 0;
            TCSADRAIN = 1;
            TCSAFLUSH = 2;
            VINTR = 0;
            VQUIT = 1;
            VERASE = 2;
            VKILL = 3;
            VEOF = 4;
            VTIME = 5;
            VMIN = 6;
            VSWTC = 7;
            VSTART = 8;
            VSTOP = 9;
            VSUSP = 10;
            VEOL = 11;
            VREPRINT = 12;
            VDISCARD = 13;
            VWERASE = 14;
            VLNEXT = 15;
            VEOL2 = 16;
            VERASE2 = -1;
            VDSUSP = -1;
            VSTATUS = -1;
            IGNBRK = 1;
            BRKINT = 2;
            IGNPAR = 4;
            PARMRK = 16;
            INPCK = 32;
            ISTRIP = 64;
            INLCR = 256;
            IGNCR = 512;
            ICRNL = 1024;
            IUCLC = 4096;
            IXON = 8192;
            IXANY = 16384;
            IXOFF = 65536;
            IMAXBEL = 131072;
            IUTF8 = 262144;
            OPOST = 1;
            OLCUC = 2;
            ONLCR = 4;
            OCRNL = 16;
            ONOCR = 32;
            ONLRET = 64;
            OFILL = 256;
            OFDEL = 512;
            NLDLY = 1024;
            NL0 = 0;
            NL1 = 1024;
            CRDLY = 12288;
            CR0 = 0;
            CR1 = 4096;
            CR2 = 8192;
            CR3 = 12288;
            TABDLY = 81920;
            TAB0 = 0;
            TAB1 = 16384;
            TAB2 = 65536;
            TAB3 = 81920;
            XTABS = 81920;
            BSDLY = 131072;
            BS0 = 0;
            BS1 = 131072;
            VTDLY = 262144;
            VT0 = 0;
            VT1 = 262144;
            FFDLY = 0x100000;
            FF0 = 0;
            FF1 = 0x100000;
            CBAUD = 65559;
            B0 = 0;
            B50 = 1;
            B75 = 2;
            B110 = 3;
            B134 = 4;
            B150 = 5;
            B200 = 6;
            B300 = 7;
            B600 = 16;
            B1200 = 17;
            B1800 = 18;
            B2400 = 19;
            B4800 = 20;
            B9600 = 21;
            B19200 = 22;
            B38400 = 23;
            EXTA = 11637248;
            EXTB = 11764736;
            CSIZE = 96;
            CS5 = 0;
            CS6 = 32;
            CS7 = 64;
            CS8 = 96;
            CSTOPB = 256;
            CREAD = 512;
            PARENB = 1024;
            PARODD = 4096;
            HUPCL = 8192;
            CLOCAL = 16384;
            ISIG = 1;
            ICANON = 2;
            XCASE = 4;
            ECHO = 16;
            ECHOE = 32;
            ECHOK = 64;
            ECHONL = 256;
            NOFLSH = 512;
            TOSTOP = 1024;
            ECHOCTL = 4096;
            ECHOPRT = 8192;
            ECHOKE = 16384;
            FLUSHO = 65536;
            PENDIN = 262144;
            IEXTEN = 0x100000;
            EXTPROC = 0x200000;
        } else if (osName.startsWith("Mac") || osName.startsWith("Darwin")) {
            TIOCGWINSZ = 1074295912;
            TIOCSWINSZ = -2146929561;
            TCSANOW = 0;
            VEOF = 0;
            VEOL = 1;
            VEOL2 = 2;
            VERASE = 3;
            VWERASE = 4;
            VKILL = 5;
            VREPRINT = 6;
            VINTR = 8;
            VQUIT = 9;
            VSUSP = 10;
            VDSUSP = 11;
            VSTART = 12;
            VSTOP = 13;
            VLNEXT = 14;
            VDISCARD = 15;
            VMIN = 16;
            VTIME = 17;
            VSTATUS = 18;
            VERASE2 = -1;
            VSWTC = -1;
            IGNBRK = 1;
            BRKINT = 2;
            IGNPAR = 4;
            PARMRK = 8;
            INPCK = 16;
            ISTRIP = 32;
            INLCR = 64;
            IGNCR = 128;
            ICRNL = 256;
            IXON = 512;
            IXOFF = 1024;
            IXANY = 2048;
            IMAXBEL = 8192;
            IUTF8 = 16384;
            OPOST = 1;
            ONLCR = 2;
            OXTABS = 4;
            ONOEOT = 8;
            OCRNL = 16;
            ONOCR = 32;
            ONLRET = 64;
            OFILL = 128;
            NLDLY = 768;
            TABDLY = 3076;
            CRDLY = 12288;
            FFDLY = 16384;
            BSDLY = 32768;
            VTDLY = 65536;
            OFDEL = 131072;
            CIGNORE = 1;
            CS5 = 0;
            CS6 = 256;
            CS7 = 512;
            CS8 = 768;
            CSTOPB = 1024;
            CREAD = 2048;
            PARENB = 4096;
            PARODD = 8192;
            HUPCL = 16384;
            CLOCAL = 32768;
            CCTS_OFLOW = 65536;
            CRTS_IFLOW = 131072;
            CDTR_IFLOW = 262144;
            CDSR_OFLOW = 524288;
            CCAR_OFLOW = 0x100000;
            ECHOKE = 1;
            ECHOE = 2;
            ECHOK = 4;
            ECHO = 8;
            ECHONL = 16;
            ECHOPRT = 32;
            ECHOCTL = 64;
            ISIG = 128;
            ICANON = 256;
            ALTWERASE = 512;
            IEXTEN = 1024;
            EXTPROC = 2048;
            TOSTOP = 0x400000;
            FLUSHO = 0x800000;
            NOKERNINFO = 0x2000000;
            PENDIN = 0x20000000;
            NOFLSH = Integer.MIN_VALUE;
        } else if (osName.startsWith("FreeBSD")) {
            TIOCGWINSZ = 1074295912;
            TIOCSWINSZ = -2146929561;
            TCSANOW = 0;
            TCSADRAIN = 1;
            TCSAFLUSH = 2;
            VEOF = 0;
            VEOL = 1;
            VEOL2 = 2;
            VERASE = 3;
            VWERASE = 4;
            VKILL = 5;
            VREPRINT = 6;
            VERASE2 = 7;
            VINTR = 8;
            VQUIT = 9;
            VSUSP = 10;
            VDSUSP = 11;
            VSTART = 12;
            VSTOP = 13;
            VLNEXT = 14;
            VDISCARD = 15;
            VMIN = 16;
            VTIME = 17;
            VSTATUS = 18;
            VSWTC = -1;
            IGNBRK = 1;
            BRKINT = 2;
            IGNPAR = 4;
            PARMRK = 8;
            INPCK = 16;
            ISTRIP = 32;
            INLCR = 64;
            IGNCR = 128;
            ICRNL = 256;
            IXON = 512;
            IXOFF = 1024;
            IXANY = 2048;
            IMAXBEL = 8192;
            OPOST = 1;
            ONLCR = 2;
            TABDLY = 4;
            TAB0 = 0;
            TAB3 = 4;
            ONOEOT = 8;
            OCRNL = 16;
            ONLRET = 64;
            CIGNORE = 1;
            CSIZE = 768;
            CS5 = 0;
            CS6 = 256;
            CS7 = 512;
            CS8 = 768;
            CSTOPB = 1024;
            CREAD = 2048;
            PARENB = 4096;
            PARODD = 8192;
            HUPCL = 16384;
            CLOCAL = 32768;
            ECHOKE = 1;
            ECHOE = 2;
            ECHOK = 4;
            ECHO = 8;
            ECHONL = 16;
            ECHOPRT = 32;
            ECHOCTL = 64;
            ISIG = 128;
            ICANON = 256;
            ALTWERASE = 512;
            IEXTEN = 1024;
            EXTPROC = 2048;
            TOSTOP = 0x400000;
            FLUSHO = 0x800000;
            PENDIN = 0x2000000;
            NOFLSH = 0x8000000;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    static class winsize {
        static final GroupLayout LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_SHORT.withName("ws_row"), ValueLayout.JAVA_SHORT.withName("ws_col"), ValueLayout.JAVA_SHORT, ValueLayout.JAVA_SHORT);
        private static final VarHandle ws_col;
        private static final VarHandle ws_row;
        private final MemorySegment seg = Arena.ofAuto().allocate(LAYOUT);

        winsize() {
        }

        winsize(short ws_col, short ws_row) {
            this();
            this.ws_col(ws_col);
            this.ws_row(ws_row);
        }

        MemorySegment segment() {
            return this.seg;
        }

        short ws_col() {
            return ws_col.get(this.seg);
        }

        void ws_col(short col) {
            ws_col.set(this.seg, col);
        }

        short ws_row() {
            return ws_row.get(this.seg);
        }

        void ws_row(short row) {
            ws_row.set(this.seg, row);
        }

        static {
            ws_row = FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("ws_row"));
            ws_col = FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("ws_col"));
        }
    }

    static class termios {
        static final GroupLayout LAYOUT;
        private static final VarHandle c_iflag;
        private static final VarHandle c_oflag;
        private static final VarHandle c_cflag;
        private static final VarHandle c_lflag;
        private static final long c_cc_offset;
        private static final VarHandle c_ispeed;
        private static final VarHandle c_ospeed;
        private final MemorySegment seg = Arena.ofAuto().allocate(LAYOUT);

        private static VarHandle adjust2LinuxHandle(VarHandle v) {
            if (OSUtils.IS_LINUX) {
                MethodHandle id = MethodHandles.identity(Integer.TYPE);
                v = MethodHandles.filterValue(v, MethodHandles.explicitCastArguments(id, MethodType.methodType(Integer.TYPE, Long.TYPE)), MethodHandles.explicitCastArguments(id, MethodType.methodType(Long.TYPE, Integer.TYPE)));
            }
            return v;
        }

        termios() {
        }

        termios(Attributes t) {
            this();
            long c_iflag = 0L;
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IGNBRK), IGNBRK, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.BRKINT), BRKINT, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IGNPAR), IGNPAR, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.PARMRK), PARMRK, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.INPCK), INPCK, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.ISTRIP), ISTRIP, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.INLCR), INLCR, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IGNCR), IGNCR, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.ICRNL), ICRNL, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IXON), IXON, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IXOFF), IXOFF, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IXANY), IXANY, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IMAXBEL), IMAXBEL, c_iflag);
            c_iflag = termios.setFlag(t.getInputFlag(Attributes.InputFlag.IUTF8), IUTF8, c_iflag);
            this.c_iflag(c_iflag);
            long c_oflag = 0L;
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.OPOST), OPOST, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLCR), ONLCR, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.OXTABS), OXTABS, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOEOT), ONOEOT, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.OCRNL), OCRNL, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONOCR), ONOCR, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.ONLRET), ONLRET, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFILL), OFILL, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.NLDLY), NLDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.TABDLY), TABDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.CRDLY), CRDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.FFDLY), FFDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.BSDLY), BSDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.VTDLY), VTDLY, c_oflag);
            c_oflag = termios.setFlag(t.getOutputFlag(Attributes.OutputFlag.OFDEL), OFDEL, c_oflag);
            this.c_oflag(c_oflag);
            long c_cflag = 0L;
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CIGNORE), CIGNORE, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CS5), CS5, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CS6), CS6, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CS7), CS7, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CS8), CS8, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CSTOPB), CSTOPB, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CREAD), CREAD, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.PARENB), PARENB, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.PARODD), PARODD, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.HUPCL), HUPCL, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CLOCAL), CLOCAL, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CCTS_OFLOW), CCTS_OFLOW, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CRTS_IFLOW), CRTS_IFLOW, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CDTR_IFLOW), CDTR_IFLOW, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CDSR_OFLOW), CDSR_OFLOW, c_cflag);
            c_cflag = termios.setFlag(t.getControlFlag(Attributes.ControlFlag.CCAR_OFLOW), CCAR_OFLOW, c_cflag);
            this.c_cflag(c_cflag);
            long c_lflag = 0L;
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOKE), ECHOKE, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOE), ECHOE, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOK), ECHOK, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHO), ECHO, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHONL), ECHONL, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOPRT), ECHOPRT, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ECHOCTL), ECHOCTL, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ISIG), ISIG, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ICANON), ICANON, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.ALTWERASE), ALTWERASE, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.IEXTEN), IEXTEN, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.EXTPROC), EXTPROC, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.TOSTOP), TOSTOP, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.FLUSHO), FLUSHO, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.NOKERNINFO), NOKERNINFO, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.PENDIN), PENDIN, c_lflag);
            c_lflag = termios.setFlag(t.getLocalFlag(Attributes.LocalFlag.NOFLSH), NOFLSH, c_lflag);
            this.c_lflag(c_lflag);
            byte[] c_cc = new byte[20];
            c_cc[CLibrary.VEOF] = (byte)t.getControlChar(Attributes.ControlChar.VEOF);
            c_cc[CLibrary.VEOL] = (byte)t.getControlChar(Attributes.ControlChar.VEOL);
            c_cc[CLibrary.VEOL2] = (byte)t.getControlChar(Attributes.ControlChar.VEOL2);
            c_cc[CLibrary.VERASE] = (byte)t.getControlChar(Attributes.ControlChar.VERASE);
            c_cc[CLibrary.VWERASE] = (byte)t.getControlChar(Attributes.ControlChar.VWERASE);
            c_cc[CLibrary.VKILL] = (byte)t.getControlChar(Attributes.ControlChar.VKILL);
            c_cc[CLibrary.VREPRINT] = (byte)t.getControlChar(Attributes.ControlChar.VREPRINT);
            c_cc[CLibrary.VINTR] = (byte)t.getControlChar(Attributes.ControlChar.VINTR);
            c_cc[CLibrary.VQUIT] = (byte)t.getControlChar(Attributes.ControlChar.VQUIT);
            c_cc[CLibrary.VSUSP] = (byte)t.getControlChar(Attributes.ControlChar.VSUSP);
            if (VDSUSP != -1) {
                c_cc[CLibrary.VDSUSP] = (byte)t.getControlChar(Attributes.ControlChar.VDSUSP);
            }
            c_cc[CLibrary.VSTART] = (byte)t.getControlChar(Attributes.ControlChar.VSTART);
            c_cc[CLibrary.VSTOP] = (byte)t.getControlChar(Attributes.ControlChar.VSTOP);
            c_cc[CLibrary.VLNEXT] = (byte)t.getControlChar(Attributes.ControlChar.VLNEXT);
            c_cc[CLibrary.VDISCARD] = (byte)t.getControlChar(Attributes.ControlChar.VDISCARD);
            c_cc[CLibrary.VMIN] = (byte)t.getControlChar(Attributes.ControlChar.VMIN);
            c_cc[CLibrary.VTIME] = (byte)t.getControlChar(Attributes.ControlChar.VTIME);
            if (VSTATUS != -1) {
                c_cc[CLibrary.VSTATUS] = (byte)t.getControlChar(Attributes.ControlChar.VSTATUS);
            }
            this.c_cc().copyFrom(MemorySegment.ofArray(c_cc));
        }

        MemorySegment segment() {
            return this.seg;
        }

        long c_iflag() {
            return c_iflag.get(this.seg);
        }

        void c_iflag(long f) {
            c_iflag.set(this.seg, f);
        }

        long c_oflag() {
            return c_oflag.get(this.seg);
        }

        void c_oflag(long f) {
            c_oflag.set(this.seg, f);
        }

        long c_cflag() {
            return c_cflag.get(this.seg);
        }

        void c_cflag(long f) {
            c_cflag.set(this.seg, f);
        }

        long c_lflag() {
            return c_lflag.get(this.seg);
        }

        void c_lflag(long f) {
            c_lflag.set(this.seg, f);
        }

        MemorySegment c_cc() {
            return this.seg.asSlice(c_cc_offset, 20L);
        }

        long c_ispeed() {
            return c_ispeed.get(this.seg);
        }

        void c_ispeed(long f) {
            c_ispeed.set(this.seg, f);
        }

        long c_ospeed() {
            return c_ospeed.get(this.seg);
        }

        void c_ospeed(long f) {
            c_ospeed.set(this.seg, f);
        }

        private static long setFlag(boolean flag, long value, long org) {
            return flag ? org | value : org;
        }

        private static <T extends Enum<T>> void addFlag(long value, EnumSet<T> flags, T flag, int v) {
            if ((value & (long)v) != 0L) {
                flags.add(flag);
            }
        }

        public Attributes asAttributes() {
            Attributes attr = new Attributes();
            long c_iflag = this.c_iflag();
            EnumSet<Attributes.InputFlag> iflag = attr.getInputFlags();
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IGNBRK, IGNBRK);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IGNBRK, IGNBRK);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.BRKINT, BRKINT);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IGNPAR, IGNPAR);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.PARMRK, PARMRK);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.INPCK, INPCK);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.ISTRIP, ISTRIP);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.INLCR, INLCR);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IGNCR, IGNCR);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.ICRNL, ICRNL);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IXON, IXON);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IXOFF, IXOFF);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IXANY, IXANY);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IMAXBEL, IMAXBEL);
            termios.addFlag(c_iflag, iflag, Attributes.InputFlag.IUTF8, IUTF8);
            long c_oflag = this.c_oflag();
            EnumSet<Attributes.OutputFlag> oflag = attr.getOutputFlags();
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.OPOST, OPOST);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.ONLCR, ONLCR);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.OXTABS, OXTABS);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.ONOEOT, ONOEOT);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.OCRNL, OCRNL);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.ONOCR, ONOCR);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.ONLRET, ONLRET);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.OFILL, OFILL);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.NLDLY, NLDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.TABDLY, TABDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.CRDLY, CRDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.FFDLY, FFDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.BSDLY, BSDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.VTDLY, VTDLY);
            termios.addFlag(c_oflag, oflag, Attributes.OutputFlag.OFDEL, OFDEL);
            long c_cflag = this.c_cflag();
            EnumSet<Attributes.ControlFlag> cflag = attr.getControlFlags();
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CIGNORE, CIGNORE);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CS5, CS5);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CS6, CS6);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CS7, CS7);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CS8, CS8);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CSTOPB, CSTOPB);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CREAD, CREAD);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.PARENB, PARENB);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.PARODD, PARODD);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.HUPCL, HUPCL);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CLOCAL, CLOCAL);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CCTS_OFLOW, CCTS_OFLOW);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CRTS_IFLOW, CRTS_IFLOW);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CDSR_OFLOW, CDSR_OFLOW);
            termios.addFlag(c_cflag, cflag, Attributes.ControlFlag.CCAR_OFLOW, CCAR_OFLOW);
            long c_lflag = this.c_lflag();
            EnumSet<Attributes.LocalFlag> lflag = attr.getLocalFlags();
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOKE, ECHOKE);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOE, ECHOE);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOK, ECHOK);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHO, ECHO);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHONL, ECHONL);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOPRT, ECHOPRT);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ECHOCTL, ECHOCTL);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ISIG, ISIG);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ICANON, ICANON);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.ALTWERASE, ALTWERASE);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.IEXTEN, IEXTEN);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.EXTPROC, EXTPROC);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.TOSTOP, TOSTOP);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.FLUSHO, FLUSHO);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.NOKERNINFO, NOKERNINFO);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.PENDIN, PENDIN);
            termios.addFlag(c_lflag, lflag, Attributes.LocalFlag.NOFLSH, NOFLSH);
            byte[] c_cc = this.c_cc().toArray(ValueLayout.JAVA_BYTE);
            EnumMap<Attributes.ControlChar, Integer> cc = attr.getControlChars();
            cc.put(Attributes.ControlChar.VEOF, Integer.valueOf(c_cc[VEOF]));
            cc.put(Attributes.ControlChar.VEOL, Integer.valueOf(c_cc[VEOL]));
            cc.put(Attributes.ControlChar.VEOL2, Integer.valueOf(c_cc[VEOL2]));
            cc.put(Attributes.ControlChar.VERASE, Integer.valueOf(c_cc[VERASE]));
            cc.put(Attributes.ControlChar.VWERASE, Integer.valueOf(c_cc[VWERASE]));
            cc.put(Attributes.ControlChar.VKILL, Integer.valueOf(c_cc[VKILL]));
            cc.put(Attributes.ControlChar.VREPRINT, Integer.valueOf(c_cc[VREPRINT]));
            cc.put(Attributes.ControlChar.VINTR, Integer.valueOf(c_cc[VINTR]));
            cc.put(Attributes.ControlChar.VQUIT, Integer.valueOf(c_cc[VQUIT]));
            cc.put(Attributes.ControlChar.VSUSP, Integer.valueOf(c_cc[VSUSP]));
            if (VDSUSP != -1) {
                cc.put(Attributes.ControlChar.VDSUSP, Integer.valueOf(c_cc[VDSUSP]));
            }
            cc.put(Attributes.ControlChar.VSTART, Integer.valueOf(c_cc[VSTART]));
            cc.put(Attributes.ControlChar.VSTOP, Integer.valueOf(c_cc[VSTOP]));
            cc.put(Attributes.ControlChar.VLNEXT, Integer.valueOf(c_cc[VLNEXT]));
            cc.put(Attributes.ControlChar.VDISCARD, Integer.valueOf(c_cc[VDISCARD]));
            cc.put(Attributes.ControlChar.VMIN, Integer.valueOf(c_cc[VMIN]));
            cc.put(Attributes.ControlChar.VTIME, Integer.valueOf(c_cc[VTIME]));
            if (VSTATUS != -1) {
                cc.put(Attributes.ControlChar.VSTATUS, Integer.valueOf(c_cc[VSTATUS]));
            }
            return attr;
        }

        static {
            if (OSUtils.IS_OSX) {
                LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_LONG.withName("c_iflag"), ValueLayout.JAVA_LONG.withName("c_oflag"), ValueLayout.JAVA_LONG.withName("c_cflag"), ValueLayout.JAVA_LONG.withName("c_lflag"), MemoryLayout.sequenceLayout(32L, ValueLayout.JAVA_BYTE).withName("c_cc"), ValueLayout.JAVA_LONG.withName("c_ispeed"), ValueLayout.JAVA_LONG.withName("c_ospeed"));
            } else if (OSUtils.IS_LINUX) {
                LAYOUT = MemoryLayout.structLayout(ValueLayout.JAVA_INT.withName("c_iflag"), ValueLayout.JAVA_INT.withName("c_oflag"), ValueLayout.JAVA_INT.withName("c_cflag"), ValueLayout.JAVA_INT.withName("c_lflag"), ValueLayout.JAVA_BYTE.withName("c_line"), MemoryLayout.sequenceLayout(32L, ValueLayout.JAVA_BYTE).withName("c_cc"), MemoryLayout.paddingLayout(3L), ValueLayout.JAVA_INT.withName("c_ispeed"), ValueLayout.JAVA_INT.withName("c_ospeed"));
            } else {
                throw new IllegalStateException("Unsupported system!");
            }
            c_iflag = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_iflag")));
            c_oflag = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_oflag")));
            c_cflag = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_cflag")));
            c_lflag = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_lflag")));
            c_cc_offset = LAYOUT.byteOffset(MemoryLayout.PathElement.groupElement("c_cc"));
            c_ispeed = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_ispeed")));
            c_ospeed = termios.adjust2LinuxHandle(FfmTerminalProvider.lookupVarHandle(LAYOUT, MemoryLayout.PathElement.groupElement("c_ospeed")));
        }
    }
}

