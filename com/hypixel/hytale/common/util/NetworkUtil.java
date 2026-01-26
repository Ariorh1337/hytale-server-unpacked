/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Locale;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NetworkUtil {
    public static Inet6Address ANY_IPV6_ADDRESS;
    public static Inet4Address ANY_IPV4_ADDRESS;
    public static Inet6Address LOOPBACK_IPV6_ADDRESS;
    public static Inet4Address LOOPBACK_IPV4_ADDRESS;

    @Nullable
    public static InetAddress getFirstNonLoopbackAddress() throws SocketException {
        InetAddress firstInet6Address = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress.isLoopbackAddress() || inetAddress.isAnyLocalAddress() || inetAddress.isLinkLocalAddress()) continue;
                if (inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
                if (!(inetAddress instanceof Inet6Address) || firstInet6Address != null) continue;
                firstInet6Address = inetAddress;
            }
        }
        return firstInet6Address;
    }

    @Nullable
    public static InetAddress getFirstAddressWith(AddressType ... include) throws SocketException {
        return NetworkUtil.getFirstAddressWith(include, null);
    }

    @Nullable
    public static InetAddress getFirstAddressWithout(AddressType ... include) throws SocketException {
        return NetworkUtil.getFirstAddressWith(null, include);
    }

    @Nullable
    public static InetAddress getFirstAddressWith(@Nullable AddressType[] include, @Nullable AddressType[] exclude) throws SocketException {
        InetAddress firstInet6Address = null;
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            if (networkInterface.isLoopback() || !networkInterface.isUp()) continue;
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            block1: while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (include != null) {
                    for (AddressType addressType : include) {
                        if (!addressType.predicate.test(inetAddress)) continue block1;
                    }
                }
                if (exclude != null) {
                    for (AddressType addressType : exclude) {
                        if (addressType.predicate.test(inetAddress)) continue block1;
                    }
                }
                if (inetAddress instanceof Inet4Address) {
                    return inetAddress;
                }
                if (!(inetAddress instanceof Inet6Address) || firstInet6Address != null) continue;
                firstInet6Address = inetAddress;
            }
        }
        return firstInet6Address;
    }

    public static boolean addressMatchesAll(InetAddress address, AddressType ... types) {
        for (AddressType type : types) {
            if (type.predicate.test(address)) continue;
            return false;
        }
        return true;
    }

    public static boolean addressMatchesAny(InetAddress address) {
        return NetworkUtil.addressMatchesAny(address, AddressType.values());
    }

    public static boolean addressMatchesAny(InetAddress address, AddressType ... types) {
        for (AddressType type : types) {
            if (!type.predicate.test(address)) continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public static String toSocketString(@Nonnull InetSocketAddress address) {
        Object str;
        if (address.getAddress() instanceof Inet6Address) {
            String host = address.getHostString();
            str = host.indexOf(58) >= 0 ? "[" + host + "]" : host;
            str = (String)str + ":" + address.getPort();
        } else {
            str = address.getHostString() + ":" + address.getPort();
        }
        return str;
    }

    @Nullable
    public static String getHostName() {
        String localhost = null;
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            localhost = localHost.getHostName();
            if (NetworkUtil.isAcceptableHostName(localhost)) {
                return localhost;
            }
            String hostName = localHost.getCanonicalHostName();
            if (NetworkUtil.isAcceptableHostName(hostName)) {
                return hostName;
            }
        }
        catch (UnknownHostException localHost) {
            // empty catch block
        }
        String hostName = System.getenv("HOSTNAME");
        if (NetworkUtil.isAcceptableHostName(hostName)) {
            return hostName;
        }
        hostName = System.getenv("COMPUTERNAME");
        if (NetworkUtil.isAcceptableHostName(hostName)) {
            return hostName;
        }
        hostName = NetworkUtil.firstLineIfExists("/etc/hostname");
        if (NetworkUtil.isAcceptableHostName(hostName)) {
            return hostName;
        }
        hostName = NetworkUtil.firstLineIfExists("/proc/sys/kernel/hostname");
        if (NetworkUtil.isAcceptableHostName(hostName)) {
            return hostName;
        }
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                String name;
                NetworkInterface ni = en.nextElement();
                if (!ni.isUp() || ni.isLoopback() || ni.isPointToPoint() || (name = ni.getName().toLowerCase(Locale.ROOT)).startsWith("lo") || name.startsWith("docker") || name.startsWith("br-") || name.startsWith("veth") || name.startsWith("virbr") || name.startsWith("utun") || name.startsWith("wg") || name.startsWith("zt")) continue;
                Enumeration<InetAddress> e = ni.getInetAddresses();
                while (e.hasMoreElements()) {
                    InetAddress a = e.nextElement();
                    if (a.isLoopbackAddress() || a.isLinkLocalAddress() || a.isAnyLocalAddress()) continue;
                    String hostAddress = a.getHostAddress();
                    String addressHostName = a.getHostName();
                    if (addressHostName != null && !addressHostName.equals(hostAddress) && NetworkUtil.isAcceptableHostName(addressHostName)) {
                        return addressHostName;
                    }
                    String canonicalHostName = a.getCanonicalHostName();
                    if (canonicalHostName == null || canonicalHostName.equals(hostAddress) || !NetworkUtil.isAcceptableHostName(canonicalHostName)) continue;
                    return canonicalHostName;
                }
            }
        }
        catch (SocketException socketException) {
            // empty catch block
        }
        return null;
    }

    @Nullable
    private static String firstLineIfExists(String path) {
        String string;
        block9: {
            Path p = Path.of(path, new String[0]);
            if (!Files.isRegularFile(p, new LinkOption[0])) {
                return null;
            }
            BufferedReader reader = Files.newBufferedReader(p, StandardCharsets.UTF_8);
            try {
                String line = reader.readLine();
                String string2 = string = line == null ? null : line.trim();
                if (reader == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (reader != null) {
                        try {
                            reader.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    return null;
                }
            }
            reader.close();
        }
        return string;
    }

    private static boolean isAcceptableHostName(@Nullable String name) {
        if (name == null) {
            return false;
        }
        if ((name = name.trim()).isEmpty()) {
            return false;
        }
        String lower = name.toLowerCase(Locale.ROOT);
        if (NetworkUtil.isIPv4Literal(lower) || NetworkUtil.isLikelyIPv6Literal(lower)) {
            return false;
        }
        if ("localhost".equals(lower) || "ip6-localhost".equals(lower) || "ip6-loopback".equals(lower) || "docker-desktop".equals(lower)) {
            return false;
        }
        if (lower.contains("docker") || lower.contains("wsl") || lower.endsWith(".internal") || lower.endsWith(".localdomain")) {
            return false;
        }
        return !lower.endsWith(".local");
    }

    private static boolean isIPv4Literal(@Nonnull String name) {
        int dots = 0;
        int octet = -1;
        int val = 0;
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (ch >= '0' && ch <= '9') {
                if (octet == -1) {
                    octet = 0;
                }
                if ((val = val * 10 + (ch - 48)) > 255) {
                    return false;
                }
                if (++octet <= 3) continue;
                return false;
            }
            if (ch == '.') {
                if (octet <= 0) {
                    return false;
                }
                octet = -1;
                val = 0;
                if (++dots <= 3) continue;
                return false;
            }
            return false;
        }
        return dots == 3 && octet > 0;
    }

    private static boolean isLikelyIPv6Literal(@Nonnull String name) {
        boolean colon = false;
        for (int i = 0; i < name.length(); ++i) {
            char ch = name.charAt(i);
            if (ch == ':') {
                colon = true;
                continue;
            }
            if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F') continue;
            return false;
        }
        return colon;
    }

    static {
        try {
            ANY_IPV6_ADDRESS = Inet6Address.getByAddress("::", new byte[16], null);
            ANY_IPV4_ADDRESS = (Inet4Address)Inet4Address.getByAddress("0.0.0.0", new byte[4]);
            LOOPBACK_IPV6_ADDRESS = Inet6Address.getByAddress("::1", new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1}, null);
            LOOPBACK_IPV4_ADDRESS = (Inet4Address)Inet4Address.getByAddress("127.0.0.1", new byte[]{127, 0, 0, 1});
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static enum AddressType {
        MULTICAST(InetAddress::isMulticastAddress),
        ANY_LOCAL(InetAddress::isAnyLocalAddress),
        LOOPBACK(InetAddress::isLoopbackAddress),
        LINK_LOCAL(InetAddress::isLinkLocalAddress),
        SITE_LOCAL(InetAddress::isSiteLocalAddress),
        MC_GLOBAL(InetAddress::isMCGlobal),
        MC_SITE_LOCAL(InetAddress::isMCSiteLocal),
        MC_ORG_LOCAL(InetAddress::isMCOrgLocal);

        private final Predicate<InetAddress> predicate;

        private AddressType(Predicate<InetAddress> predicate) {
            this.predicate = predicate;
        }
    }
}

