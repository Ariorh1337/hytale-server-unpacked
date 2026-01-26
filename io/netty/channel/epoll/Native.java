/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.DefaultFileRegion;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.epoll.NativeStaticallyReferencedJniMethods;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.channel.unix.Unix;
import io.netty.util.internal.ClassInitializerUtil;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;

public final class Native {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Native.class);
    static final InetAddress INET6_ANY;
    static final InetAddress INET_ANY;
    public static final int EPOLLIN;
    public static final int EPOLLOUT;
    public static final int EPOLLRDHUP;
    public static final int EPOLLET;
    public static final int EPOLLERR;
    public static final boolean IS_SUPPORTING_SENDMMSG;
    static final boolean IS_SUPPORTING_RECVMMSG;
    static final boolean IS_SUPPORTING_UDP_SEGMENT;
    private static final int TFO_ENABLED_CLIENT_MASK = 1;
    private static final int TFO_ENABLED_SERVER_MASK = 2;
    private static final int TCP_FASTOPEN_MODE;
    static final boolean IS_SUPPORTING_TCP_FASTOPEN_CLIENT;
    static final boolean IS_SUPPORTING_TCP_FASTOPEN_SERVER;
    @Deprecated
    public static final boolean IS_SUPPORTING_TCP_FASTOPEN;
    public static final int TCP_MD5SIG_MAXKEYLEN;
    public static final String KERNEL_VERSION;

    private static native int registerUnix();

    public static FileDescriptor newEventFd() {
        return new FileDescriptor(Native.eventFd());
    }

    public static FileDescriptor newTimerFd() {
        return new FileDescriptor(Native.timerFd());
    }

    private static native boolean isSupportingUdpSegment();

    private static native int eventFd();

    private static native int timerFd();

    public static native void eventFdWrite(int var0, long var1);

    public static native void eventFdRead(int var0);

    public static FileDescriptor newEpollCreate() {
        return new FileDescriptor(Native.epollCreate());
    }

    private static native int epollCreate();

    @Deprecated
    public static int epollWait(FileDescriptor epollFd, EpollEventArray events, FileDescriptor timerFd, int timeoutSec, int timeoutNs) throws IOException {
        long result = Native.epollWait(epollFd, events, timerFd, timeoutSec, timeoutNs, -1L);
        return Native.epollReady(result);
    }

    static long epollWait(FileDescriptor epollFd, EpollEventArray events, FileDescriptor timerFd, int timeoutSec, int timeoutNs, long millisThreshold) throws IOException {
        long result;
        int ready;
        if (timeoutSec == 0 && timeoutNs == 0) {
            return (long)Native.epollWait(epollFd, events, 0) << 32;
        }
        if (timeoutSec == Integer.MAX_VALUE) {
            timeoutSec = 0;
            timeoutNs = 0;
        }
        if ((ready = Native.epollReady(result = Native.epollWait0(epollFd.intValue(), events.memoryAddress(), events.length(), timerFd.intValue(), timeoutSec, timeoutNs, millisThreshold))) < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return result;
    }

    static int epollReady(long result) {
        return (int)(result >> 32);
    }

    static boolean epollTimerWasUsed(long result) {
        return (result & 0xFFL) != 0L;
    }

    static int epollWait(FileDescriptor epollFd, EpollEventArray events, boolean immediatePoll) throws IOException {
        return Native.epollWait(epollFd, events, immediatePoll ? 0 : -1);
    }

    static int epollWait(FileDescriptor epollFd, EpollEventArray events, int timeoutMillis) throws IOException {
        int ready = Native.epollWait(epollFd.intValue(), events.memoryAddress(), events.length(), timeoutMillis);
        if (ready < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return ready;
    }

    public static int epollBusyWait(FileDescriptor epollFd, EpollEventArray events) throws IOException {
        int ready = Native.epollBusyWait0(epollFd.intValue(), events.memoryAddress(), events.length());
        if (ready < 0) {
            throw Errors.newIOException("epoll_wait", ready);
        }
        return ready;
    }

    private static native long epollWait0(int var0, long var1, int var3, int var4, int var5, int var6, long var7);

    private static native int epollWait(int var0, long var1, int var3, int var4);

    private static native int epollBusyWait0(int var0, long var1, int var3);

    public static void epollCtlAdd(int efd, int fd, int flags) throws IOException {
        int res = Native.epollCtlAdd0(efd, fd, flags);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }

    private static native int epollCtlAdd0(int var0, int var1, int var2);

    public static void epollCtlMod(int efd, int fd, int flags) throws IOException {
        int res = Native.epollCtlMod0(efd, fd, flags);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }

    private static native int epollCtlMod0(int var0, int var1, int var2);

    public static void epollCtlDel(int efd, int fd) throws IOException {
        int res = Native.epollCtlDel0(efd, fd);
        if (res < 0) {
            throw Errors.newIOException("epoll_ctl", res);
        }
    }

    private static native int epollCtlDel0(int var0, int var1);

    public static int splice(int fd, long offIn, int fdOut, long offOut, long len) throws IOException {
        int res = Native.splice0(fd, offIn, fdOut, offOut, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("splice", res);
    }

    private static native int splice0(int var0, long var1, int var3, long var4, long var6);

    @Deprecated
    public static int sendmmsg(int fd, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.sendmmsg(fd, Socket.isIPv6Preferred(), msgs, offset, len);
    }

    static int sendmmsg(int fd, boolean ipv6, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        int res = Native.sendmmsg0(fd, ipv6, msgs, offset, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("sendmmsg", res);
    }

    private static native int sendmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

    static int recvmmsg(int fd, boolean ipv6, NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        int res = Native.recvmmsg0(fd, ipv6, msgs, offset, len);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("recvmmsg", res);
    }

    private static native int recvmmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket[] var2, int var3, int var4);

    static int recvmsg(int fd, boolean ipv6, NativeDatagramPacketArray.NativeDatagramPacket packet) throws IOException {
        int res = Native.recvmsg0(fd, ipv6, packet);
        if (res >= 0) {
            return res;
        }
        return Errors.ioResult("recvmsg", res);
    }

    private static native int recvmsg0(int var0, boolean var1, NativeDatagramPacketArray.NativeDatagramPacket var2);

    public static native int sizeofEpollEvent();

    public static native int offsetofEpollData();

    private static void loadNativeLibrary() {
        String name = PlatformDependent.normalizedOs();
        if (!"linux".equals(name)) {
            throw new IllegalStateException("Only supported on Linux");
        }
        String staticLibName = "netty_transport_native_epoll";
        String sharedLibName = staticLibName + '_' + PlatformDependent.normalizedArch();
        ClassLoader cl = PlatformDependent.getClassLoader(Native.class);
        try {
            NativeLibraryLoader.load(sharedLibName, cl);
        }
        catch (UnsatisfiedLinkError e1) {
            try {
                NativeLibraryLoader.load(staticLibName, cl);
                logger.debug("Failed to load {}", (Object)sharedLibName, (Object)e1);
            }
            catch (UnsatisfiedLinkError e2) {
                ThrowableUtil.addSuppressed((Throwable)e1, e2);
                throw e1;
            }
        }
    }

    private Native() {
    }

    static {
        Selector selector = null;
        try {
            selector = Selector.open();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        try {
            INET_ANY = InetAddress.getByName("0.0.0.0");
            INET6_ANY = InetAddress.getByName("::");
        }
        catch (UnknownHostException e) {
            throw new ExceptionInInitializerError(e);
        }
        ClassInitializerUtil.tryLoadClasses(Native.class, PeerCredentials.class, DefaultFileRegion.class, FileChannel.class, java.io.FileDescriptor.class, NativeDatagramPacketArray.NativeDatagramPacket.class);
        try {
            Native.offsetofEpollData();
        }
        catch (UnsatisfiedLinkError ignore) {
            Native.loadNativeLibrary();
        }
        finally {
            try {
                if (selector != null) {
                    selector.close();
                }
            }
            catch (IOException iOException) {}
        }
        Unix.registerInternal(new Runnable(){

            @Override
            public void run() {
                Native.registerUnix();
            }
        });
        EPOLLIN = NativeStaticallyReferencedJniMethods.epollin();
        EPOLLOUT = NativeStaticallyReferencedJniMethods.epollout();
        EPOLLRDHUP = NativeStaticallyReferencedJniMethods.epollrdhup();
        EPOLLET = NativeStaticallyReferencedJniMethods.epollet();
        EPOLLERR = NativeStaticallyReferencedJniMethods.epollerr();
        IS_SUPPORTING_SENDMMSG = NativeStaticallyReferencedJniMethods.isSupportingSendmmsg();
        IS_SUPPORTING_RECVMMSG = NativeStaticallyReferencedJniMethods.isSupportingRecvmmsg();
        IS_SUPPORTING_UDP_SEGMENT = Native.isSupportingUdpSegment();
        TCP_FASTOPEN_MODE = NativeStaticallyReferencedJniMethods.tcpFastopenMode();
        IS_SUPPORTING_TCP_FASTOPEN_CLIENT = (TCP_FASTOPEN_MODE & 1) == 1;
        IS_SUPPORTING_TCP_FASTOPEN_SERVER = (TCP_FASTOPEN_MODE & 2) == 2;
        IS_SUPPORTING_TCP_FASTOPEN = IS_SUPPORTING_TCP_FASTOPEN_CLIENT || IS_SUPPORTING_TCP_FASTOPEN_SERVER;
        TCP_MD5SIG_MAXKEYLEN = NativeStaticallyReferencedJniMethods.tcpMd5SigMaxKeyLen();
        KERNEL_VERSION = NativeStaticallyReferencedJniMethods.kernelVersion();
    }
}

