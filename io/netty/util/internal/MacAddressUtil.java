/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal;

import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class MacAddressUtil {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MacAddressUtil.class);
    private static final int EUI64_MAC_ADDRESS_LENGTH = 8;
    private static final int EUI48_MAC_ADDRESS_LENGTH = 6;

    public static byte[] bestAvailableMac() {
        byte[] bestMacAddr = EmptyArrays.EMPTY_BYTES;
        InetAddress bestInetAddr = NetUtil.LOCALHOST4;
        LinkedHashMap<NetworkInterface, InetAddress> ifaces = new LinkedHashMap<NetworkInterface, InetAddress>();
        for (NetworkInterface networkInterface : NetUtil.NETWORK_INTERFACES) {
            InetAddress a;
            Enumeration<InetAddress> addrs = SocketUtils.addressesFromNetworkInterface(networkInterface);
            if (!addrs.hasMoreElements() || (a = addrs.nextElement()).isLoopbackAddress()) continue;
            ifaces.put(networkInterface, a);
        }
        for (Map.Entry entry : ifaces.entrySet()) {
            byte[] macAddr;
            NetworkInterface iface = (NetworkInterface)entry.getKey();
            InetAddress inetAddr = (InetAddress)entry.getValue();
            if (iface.isVirtual()) continue;
            try {
                macAddr = SocketUtils.hardwareAddressFromNetworkInterface(iface);
            }
            catch (SocketException e) {
                logger.debug("Failed to get the hardware address of a network interface: {}", (Object)iface, (Object)e);
                continue;
            }
            boolean replace = false;
            int res = MacAddressUtil.compareAddresses(bestMacAddr, macAddr);
            if (res < 0) {
                replace = true;
            } else if (res == 0) {
                res = MacAddressUtil.compareAddresses(bestInetAddr, inetAddr);
                if (res < 0) {
                    replace = true;
                } else if (res == 0 && bestMacAddr.length < macAddr.length) {
                    replace = true;
                }
            }
            if (!replace) continue;
            bestMacAddr = macAddr;
            bestInetAddr = inetAddr;
        }
        if (bestMacAddr == EmptyArrays.EMPTY_BYTES) {
            return null;
        }
        if (bestMacAddr.length == 6) {
            byte[] newAddr = new byte[8];
            System.arraycopy(bestMacAddr, 0, newAddr, 0, 3);
            newAddr[3] = -1;
            newAddr[4] = -2;
            System.arraycopy(bestMacAddr, 3, newAddr, 5, 3);
            bestMacAddr = newAddr;
        } else {
            bestMacAddr = Arrays.copyOf(bestMacAddr, 8);
        }
        return bestMacAddr;
    }

    public static byte[] defaultMachineId() {
        byte[] bestMacAddr = MacAddressUtil.bestAvailableMac();
        if (bestMacAddr == null) {
            bestMacAddr = new byte[8];
            ThreadLocalRandom.current().nextBytes(bestMacAddr);
            logger.warn("Failed to find a usable hardware address from the network interfaces; using random bytes: {}", (Object)MacAddressUtil.formatAddress(bestMacAddr));
        }
        return bestMacAddr;
    }

    public static byte[] parseMAC(String value) {
        byte[] machineId;
        char separator;
        switch (value.length()) {
            case 17: {
                separator = value.charAt(2);
                MacAddressUtil.validateMacSeparator(separator);
                machineId = new byte[6];
                break;
            }
            case 23: {
                separator = value.charAt(2);
                MacAddressUtil.validateMacSeparator(separator);
                machineId = new byte[8];
                break;
            }
            default: {
                throw new IllegalArgumentException("value is not supported [MAC-48, EUI-48, EUI-64]");
            }
        }
        int end = machineId.length - 1;
        int j = 0;
        int i = 0;
        while (i < end) {
            int sIndex = j + 2;
            machineId[i] = StringUtil.decodeHexByte(value, j);
            if (value.charAt(sIndex) != separator) {
                throw new IllegalArgumentException("expected separator '" + separator + " but got '" + value.charAt(sIndex) + "' at index: " + sIndex);
            }
            ++i;
            j += 3;
        }
        machineId[end] = StringUtil.decodeHexByte(value, j);
        return machineId;
    }

    private static void validateMacSeparator(char separator) {
        if (separator != ':' && separator != '-') {
            throw new IllegalArgumentException("unsupported separator: " + separator + " (expected: [:-])");
        }
    }

    public static String formatAddress(byte[] addr) {
        StringBuilder buf = new StringBuilder(24);
        for (byte b : addr) {
            buf.append(String.format("%02x:", b & 0xFF));
        }
        return buf.substring(0, buf.length() - 1);
    }

    static int compareAddresses(byte[] current, byte[] candidate) {
        if (candidate == null || candidate.length < 6) {
            return 1;
        }
        boolean onlyZeroAndOne = true;
        for (byte b : candidate) {
            if (b == 0 || b == 1) continue;
            onlyZeroAndOne = false;
            break;
        }
        if (onlyZeroAndOne) {
            return 1;
        }
        if ((candidate[0] & 1) != 0) {
            return 1;
        }
        if ((candidate[0] & 2) == 0) {
            if (current.length != 0 && (current[0] & 2) == 0) {
                return 0;
            }
            return -1;
        }
        if (current.length != 0 && (current[0] & 2) == 0) {
            return 1;
        }
        return 0;
    }

    private static int compareAddresses(InetAddress current, InetAddress candidate) {
        return MacAddressUtil.scoreAddress(current) - MacAddressUtil.scoreAddress(candidate);
    }

    private static int scoreAddress(InetAddress addr) {
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
            return 0;
        }
        if (addr.isMulticastAddress()) {
            return 1;
        }
        if (addr.isLinkLocalAddress()) {
            return 2;
        }
        if (addr.isSiteLocalAddress()) {
            return 3;
        }
        return 4;
    }

    private MacAddressUtil() {
    }
}

