/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import io.netty.channel.ChannelException;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.epoll.EpollTcpInfo;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.epoll.VSockAddress;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.SocketProtocolFamily;
import io.netty.channel.unix.Errors;
import io.netty.channel.unix.NativeInetAddress;
import io.netty.channel.unix.PeerCredentials;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.SocketUtils;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

public final class LinuxSocket
extends Socket {
    private static final long MAX_UINT32_T = 0xFFFFFFFFL;

    LinuxSocket(int fd) {
        super(fd);
    }

    SocketProtocolFamily family() {
        return this.ipv6 ? SocketProtocolFamily.INET6 : SocketProtocolFamily.INET;
    }

    int sendmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.sendmmsg(this.intValue(), this.ipv6, msgs, offset, len);
    }

    int recvmmsg(NativeDatagramPacketArray.NativeDatagramPacket[] msgs, int offset, int len) throws IOException {
        return Native.recvmmsg(this.intValue(), this.ipv6, msgs, offset, len);
    }

    int recvmsg(NativeDatagramPacketArray.NativeDatagramPacket msg) throws IOException {
        return Native.recvmsg(this.intValue(), this.ipv6, msg);
    }

    void setTimeToLive(int ttl) throws IOException {
        LinuxSocket.setTimeToLive(this.intValue(), ttl);
    }

    void setInterface(InetAddress address) throws IOException {
        NativeInetAddress a = NativeInetAddress.newInstance(address);
        LinuxSocket.setInterface(this.intValue(), this.ipv6, a.address(), a.scopeId(), LinuxSocket.interfaceIndex(address));
    }

    void setNetworkInterface(NetworkInterface netInterface) throws IOException {
        InetAddress address = LinuxSocket.deriveInetAddress(netInterface, this.family() == SocketProtocolFamily.INET6);
        if (address.equals(this.family() == SocketProtocolFamily.INET ? Native.INET_ANY : Native.INET6_ANY)) {
            throw new IOException("NetworkInterface does not support " + this.family());
        }
        NativeInetAddress nativeAddress = NativeInetAddress.newInstance(address);
        LinuxSocket.setInterface(this.intValue(), this.ipv6, nativeAddress.address(), nativeAddress.scopeId(), LinuxSocket.interfaceIndex(netInterface));
    }

    InetAddress getInterface() throws IOException {
        Enumeration<InetAddress> addresses;
        NetworkInterface inf = this.getNetworkInterface();
        if (inf != null && (addresses = SocketUtils.addressesFromNetworkInterface(inf)).hasMoreElements()) {
            return addresses.nextElement();
        }
        return null;
    }

    NetworkInterface getNetworkInterface() throws IOException {
        int ret = LinuxSocket.getInterface(this.intValue(), this.ipv6);
        if (this.ipv6) {
            return NetworkInterface.getByIndex(ret);
        }
        InetAddress address = LinuxSocket.inetAddress(ret);
        return address != null ? NetworkInterface.getByInetAddress(address) : null;
    }

    private static InetAddress inetAddress(int value) {
        byte[] var1 = new byte[]{(byte)(value >>> 24 & 0xFF), (byte)(value >>> 16 & 0xFF), (byte)(value >>> 8 & 0xFF), (byte)(value & 0xFF)};
        try {
            return InetAddress.getByAddress(var1);
        }
        catch (UnknownHostException ignore) {
            return null;
        }
    }

    void joinGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
        NativeInetAddress g = NativeInetAddress.newInstance(group);
        boolean isIpv6 = group instanceof Inet6Address;
        NativeInetAddress i = NativeInetAddress.newInstance(LinuxSocket.deriveInetAddress(netInterface, isIpv6));
        if (source != null) {
            if (source.getClass() != group.getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
            NativeInetAddress s = NativeInetAddress.newInstance(source);
            LinuxSocket.joinSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), LinuxSocket.interfaceIndex(netInterface), s.address());
        } else {
            LinuxSocket.joinGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), LinuxSocket.interfaceIndex(netInterface));
        }
    }

    void leaveGroup(InetAddress group, NetworkInterface netInterface, InetAddress source) throws IOException {
        NativeInetAddress g = NativeInetAddress.newInstance(group);
        boolean isIpv6 = group instanceof Inet6Address;
        NativeInetAddress i = NativeInetAddress.newInstance(LinuxSocket.deriveInetAddress(netInterface, isIpv6));
        if (source != null) {
            if (source.getClass() != group.getClass()) {
                throw new IllegalArgumentException("Source address is different type to group");
            }
            NativeInetAddress s = NativeInetAddress.newInstance(source);
            LinuxSocket.leaveSsmGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), LinuxSocket.interfaceIndex(netInterface), s.address());
        } else {
            LinuxSocket.leaveGroup(this.intValue(), this.ipv6 && isIpv6, g.address(), i.address(), g.scopeId(), LinuxSocket.interfaceIndex(netInterface));
        }
    }

    private static int interfaceIndex(NetworkInterface networkInterface) {
        return networkInterface.getIndex();
    }

    private static int interfaceIndex(InetAddress address) throws IOException {
        NetworkInterface iface = NetworkInterface.getByInetAddress(address);
        if (iface != null) {
            return iface.getIndex();
        }
        return -1;
    }

    void setTcpDeferAccept(int deferAccept) throws IOException {
        LinuxSocket.setTcpDeferAccept(this.intValue(), deferAccept);
    }

    void setTcpQuickAck(boolean quickAck) throws IOException {
        LinuxSocket.setTcpQuickAck(this.intValue(), quickAck ? 1 : 0);
    }

    void setTcpCork(boolean tcpCork) throws IOException {
        LinuxSocket.setTcpCork(this.intValue(), tcpCork ? 1 : 0);
    }

    void setSoBusyPoll(int loopMicros) throws IOException {
        LinuxSocket.setSoBusyPoll(this.intValue(), loopMicros);
    }

    void setTcpNotSentLowAt(long tcpNotSentLowAt) throws IOException {
        if (tcpNotSentLowAt < 0L || tcpNotSentLowAt > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("tcpNotSentLowAt must be a uint32_t");
        }
        LinuxSocket.setTcpNotSentLowAt(this.intValue(), (int)tcpNotSentLowAt);
    }

    void setTcpFastOpen(int tcpFastopenBacklog) throws IOException {
        LinuxSocket.setTcpFastOpen(this.intValue(), tcpFastopenBacklog);
    }

    void setTcpKeepIdle(int seconds) throws IOException {
        LinuxSocket.setTcpKeepIdle(this.intValue(), seconds);
    }

    void setTcpKeepIntvl(int seconds) throws IOException {
        LinuxSocket.setTcpKeepIntvl(this.intValue(), seconds);
    }

    void setTcpKeepCnt(int probes) throws IOException {
        LinuxSocket.setTcpKeepCnt(this.intValue(), probes);
    }

    void setTcpUserTimeout(int milliseconds) throws IOException {
        LinuxSocket.setTcpUserTimeout(this.intValue(), milliseconds);
    }

    void setIpBindAddressNoPort(boolean enabled) throws IOException {
        LinuxSocket.setIpBindAddressNoPort(this.intValue(), enabled ? 1 : 0);
    }

    void setIpMulticastAll(boolean enabled) throws IOException {
        LinuxSocket.setIpMulticastAll(this.intValue(), this.ipv6, enabled ? 1 : 0);
    }

    void setIpFreeBind(boolean enabled) throws IOException {
        LinuxSocket.setIpFreeBind(this.intValue(), enabled ? 1 : 0);
    }

    void setIpTransparent(boolean enabled) throws IOException {
        LinuxSocket.setIpTransparent(this.intValue(), enabled ? 1 : 0);
    }

    void setIpRecvOrigDestAddr(boolean enabled) throws IOException {
        LinuxSocket.setIpRecvOrigDestAddr(this.intValue(), enabled ? 1 : 0);
    }

    int getTimeToLive() throws IOException {
        return LinuxSocket.getTimeToLive(this.intValue());
    }

    void getTcpInfo(EpollTcpInfo info) throws IOException {
        LinuxSocket.getTcpInfo(this.intValue(), info.info);
    }

    void setTcpMd5Sig(InetAddress address, byte[] key) throws IOException {
        NativeInetAddress a = NativeInetAddress.newInstance(address);
        LinuxSocket.setTcpMd5Sig(this.intValue(), this.ipv6, a.address(), a.scopeId(), key);
    }

    boolean isTcpCork() throws IOException {
        return LinuxSocket.isTcpCork(this.intValue()) != 0;
    }

    int getSoBusyPoll() throws IOException {
        return LinuxSocket.getSoBusyPoll(this.intValue());
    }

    int getTcpDeferAccept() throws IOException {
        return LinuxSocket.getTcpDeferAccept(this.intValue());
    }

    boolean isTcpQuickAck() throws IOException {
        return LinuxSocket.isTcpQuickAck(this.intValue()) != 0;
    }

    long getTcpNotSentLowAt() throws IOException {
        return (long)LinuxSocket.getTcpNotSentLowAt(this.intValue()) & 0xFFFFFFFFL;
    }

    int getTcpKeepIdle() throws IOException {
        return LinuxSocket.getTcpKeepIdle(this.intValue());
    }

    int getTcpKeepIntvl() throws IOException {
        return LinuxSocket.getTcpKeepIntvl(this.intValue());
    }

    int getTcpKeepCnt() throws IOException {
        return LinuxSocket.getTcpKeepCnt(this.intValue());
    }

    int getTcpUserTimeout() throws IOException {
        return LinuxSocket.getTcpUserTimeout(this.intValue());
    }

    boolean isIpBindAddressNoPort() throws IOException {
        return LinuxSocket.isIpBindAddressNoPort(this.intValue()) != 0;
    }

    boolean isIpMulticastAll() throws IOException {
        return LinuxSocket.isIpMulticastAll(this.intValue(), this.ipv6) != 0;
    }

    boolean isIpFreeBind() throws IOException {
        return LinuxSocket.isIpFreeBind(this.intValue()) != 0;
    }

    boolean isIpTransparent() throws IOException {
        return LinuxSocket.isIpTransparent(this.intValue()) != 0;
    }

    boolean isIpRecvOrigDestAddr() throws IOException {
        return LinuxSocket.isIpRecvOrigDestAddr(this.intValue()) != 0;
    }

    PeerCredentials getPeerCredentials() throws IOException {
        return LinuxSocket.getPeerCredentials(this.intValue());
    }

    boolean isLoopbackModeDisabled() throws IOException {
        return LinuxSocket.getIpMulticastLoop(this.intValue(), this.ipv6) == 0;
    }

    void setLoopbackModeDisabled(boolean loopbackModeDisabled) throws IOException {
        LinuxSocket.setIpMulticastLoop(this.intValue(), this.ipv6, loopbackModeDisabled ? 0 : 1);
    }

    boolean isUdpGro() throws IOException {
        return LinuxSocket.isUdpGro(this.intValue()) != 0;
    }

    void setUdpGro(boolean gro) throws IOException {
        LinuxSocket.setUdpGro(this.intValue(), gro ? 1 : 0);
    }

    long sendFile(DefaultFileRegion src, long baseOffset, long offset, long length) throws IOException {
        src.open();
        long res = LinuxSocket.sendFile(this.intValue(), src, baseOffset, offset, length);
        if (res >= 0L) {
            return res;
        }
        return Errors.ioResult("sendfile", (int)res);
    }

    public void bindVSock(VSockAddress address) throws IOException {
        int res = LinuxSocket.bindVSock(this.intValue(), address.getCid(), address.getPort());
        if (res < 0) {
            throw Errors.newIOException("bindVSock", res);
        }
    }

    public boolean connectVSock(VSockAddress address) throws IOException {
        int res = LinuxSocket.connectVSock(this.intValue(), address.getCid(), address.getPort());
        if (res < 0) {
            return Errors.handleConnectErrno("connectVSock", res);
        }
        return true;
    }

    public VSockAddress remoteVSockAddress() {
        byte[] addr = LinuxSocket.remoteVSockAddress(this.intValue());
        if (addr == null) {
            return null;
        }
        int cid = LinuxSocket.getIntAt(addr, 0);
        int port = LinuxSocket.getIntAt(addr, 4);
        return new VSockAddress(cid, port);
    }

    public VSockAddress localVSockAddress() {
        byte[] addr = LinuxSocket.localVSockAddress(this.intValue());
        if (addr == null) {
            return null;
        }
        int cid = LinuxSocket.getIntAt(addr, 0);
        int port = LinuxSocket.getIntAt(addr, 4);
        return new VSockAddress(cid, port);
    }

    private static int getIntAt(byte[] array, int startIndex) {
        return array[startIndex] << 24 | (array[startIndex + 1] & 0xFF) << 16 | (array[startIndex + 2] & 0xFF) << 8 | array[startIndex + 3] & 0xFF;
    }

    private static InetAddress deriveInetAddress(NetworkInterface netInterface, boolean ipv6) {
        InetAddress ipAny;
        InetAddress inetAddress = ipAny = ipv6 ? Native.INET6_ANY : Native.INET_ANY;
        if (netInterface != null) {
            Enumeration<InetAddress> ias = netInterface.getInetAddresses();
            while (ias.hasMoreElements()) {
                InetAddress ia = ias.nextElement();
                boolean isV6 = ia instanceof Inet6Address;
                if (isV6 != ipv6) continue;
                return ia;
            }
        }
        return ipAny;
    }

    public static LinuxSocket newSocket(int fd) {
        return new LinuxSocket(fd);
    }

    public static LinuxSocket newVSockStream() {
        return new LinuxSocket(LinuxSocket.newVSockStream0());
    }

    static int newVSockStream0() {
        int res = LinuxSocket.newVSockStreamFd();
        if (res < 0) {
            throw new ChannelException(Errors.newIOException("newVSockStream", res));
        }
        return res;
    }

    public static LinuxSocket newSocketStream(boolean ipv6) {
        return new LinuxSocket(LinuxSocket.newSocketStream0(ipv6));
    }

    @Deprecated
    public static LinuxSocket newSocketStream(InternetProtocolFamily protocol) {
        return new LinuxSocket(LinuxSocket.newSocketStream0(protocol));
    }

    public static LinuxSocket newSocketStream(SocketProtocolFamily protocol) {
        return new LinuxSocket(LinuxSocket.newSocketStream0(protocol));
    }

    public static LinuxSocket newSocketStream() {
        return LinuxSocket.newSocketStream(LinuxSocket.isIPv6Preferred());
    }

    public static LinuxSocket newSocketDgram(boolean ipv6) {
        return new LinuxSocket(LinuxSocket.newSocketDgram0(ipv6));
    }

    @Deprecated
    public static LinuxSocket newSocketDgram(InternetProtocolFamily family) {
        return new LinuxSocket(LinuxSocket.newSocketDgram0(family));
    }

    public static LinuxSocket newSocketDgram(SocketProtocolFamily family) {
        return new LinuxSocket(LinuxSocket.newSocketDgram0(family));
    }

    public static LinuxSocket newSocketDgram() {
        return LinuxSocket.newSocketDgram(LinuxSocket.isIPv6Preferred());
    }

    public static LinuxSocket newSocketDomain() {
        return new LinuxSocket(LinuxSocket.newSocketDomain0());
    }

    public static LinuxSocket newSocketDomainDgram() {
        return new LinuxSocket(LinuxSocket.newSocketDomainDgram0());
    }

    private static native int newVSockStreamFd();

    private static native int bindVSock(int var0, int var1, int var2);

    private static native int connectVSock(int var0, int var1, int var2);

    private static native byte[] remoteVSockAddress(int var0);

    private static native byte[] localVSockAddress(int var0);

    private static native void joinGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

    private static native void joinSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

    private static native void leaveGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5) throws IOException;

    private static native void leaveSsmGroup(int var0, boolean var1, byte[] var2, byte[] var3, int var4, int var5, byte[] var6) throws IOException;

    private static native long sendFile(int var0, DefaultFileRegion var1, long var2, long var4, long var6) throws IOException;

    private static native int getTcpDeferAccept(int var0) throws IOException;

    private static native int isTcpQuickAck(int var0) throws IOException;

    private static native int isTcpCork(int var0) throws IOException;

    private static native int getSoBusyPoll(int var0) throws IOException;

    private static native int getTcpNotSentLowAt(int var0) throws IOException;

    private static native int getTcpKeepIdle(int var0) throws IOException;

    private static native int getTcpKeepIntvl(int var0) throws IOException;

    private static native int getTcpKeepCnt(int var0) throws IOException;

    private static native int getTcpUserTimeout(int var0) throws IOException;

    private static native int getTimeToLive(int var0) throws IOException;

    private static native int isIpBindAddressNoPort(int var0) throws IOException;

    private static native int isIpMulticastAll(int var0, boolean var1) throws IOException;

    private static native int isIpFreeBind(int var0) throws IOException;

    private static native int isIpTransparent(int var0) throws IOException;

    private static native int isIpRecvOrigDestAddr(int var0) throws IOException;

    private static native void getTcpInfo(int var0, long[] var1) throws IOException;

    private static native PeerCredentials getPeerCredentials(int var0) throws IOException;

    private static native void setTcpDeferAccept(int var0, int var1) throws IOException;

    private static native void setTcpQuickAck(int var0, int var1) throws IOException;

    private static native void setTcpCork(int var0, int var1) throws IOException;

    private static native void setSoBusyPoll(int var0, int var1) throws IOException;

    private static native void setTcpNotSentLowAt(int var0, int var1) throws IOException;

    private static native void setTcpFastOpen(int var0, int var1) throws IOException;

    private static native void setTcpKeepIdle(int var0, int var1) throws IOException;

    private static native void setTcpKeepIntvl(int var0, int var1) throws IOException;

    private static native void setTcpKeepCnt(int var0, int var1) throws IOException;

    private static native void setTcpUserTimeout(int var0, int var1) throws IOException;

    private static native void setIpBindAddressNoPort(int var0, int var1) throws IOException;

    private static native void setIpMulticastAll(int var0, boolean var1, int var2) throws IOException;

    private static native void setIpFreeBind(int var0, int var1) throws IOException;

    private static native void setIpTransparent(int var0, int var1) throws IOException;

    private static native void setIpRecvOrigDestAddr(int var0, int var1) throws IOException;

    private static native void setTcpMd5Sig(int var0, boolean var1, byte[] var2, int var3, byte[] var4) throws IOException;

    private static native void setInterface(int var0, boolean var1, byte[] var2, int var3, int var4) throws IOException;

    private static native int getInterface(int var0, boolean var1);

    private static native int getIpMulticastLoop(int var0, boolean var1) throws IOException;

    private static native void setIpMulticastLoop(int var0, boolean var1, int var2) throws IOException;

    private static native void setTimeToLive(int var0, int var1) throws IOException;

    private static native int isUdpGro(int var0) throws IOException;

    private static native void setUdpGro(int var0, int var1) throws IOException;
}

