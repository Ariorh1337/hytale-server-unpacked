/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util;

import io.netty.util.AsciiString;
import io.netty.util.NetUtilInitializations;
import io.netty.util.internal.BoundedInputStream;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.Collection;

public final class NetUtil {
    public static final Inet4Address LOCALHOST4;
    public static final Inet6Address LOCALHOST6;
    public static final InetAddress LOCALHOST;
    public static final NetworkInterface LOOPBACK_IF;
    public static final Collection<NetworkInterface> NETWORK_INTERFACES;
    public static final int SOMAXCONN;
    private static final int IPV6_WORD_COUNT = 8;
    private static final int IPV6_MAX_CHAR_COUNT = 39;
    private static final int IPV6_BYTE_COUNT = 16;
    private static final int IPV6_MAX_CHAR_BETWEEN_SEPARATOR = 4;
    private static final int IPV6_MIN_SEPARATORS = 2;
    private static final int IPV6_MAX_SEPARATORS = 8;
    private static final int IPV4_MAX_CHAR_BETWEEN_SEPARATOR = 3;
    private static final int IPV4_SEPARATORS = 3;
    private static final boolean IPV4_PREFERRED;
    private static final boolean IPV6_ADDRESSES_PREFERRED;
    private static final InternalLogger logger;

    /*
     * Exception decompiling
     */
    private static Integer sysctlGetInt(String sysctlKey) throws IOException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [10[FORLOOP]], but top level block is 3[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static boolean isIpV4StackPreferred() {
        return IPV4_PREFERRED;
    }

    public static boolean isIpV6AddressesPreferred() {
        return IPV6_ADDRESSES_PREFERRED;
    }

    public static byte[] createByteArrayFromIpAddressString(String ipAddressString) {
        if (NetUtil.isValidIpV4Address(ipAddressString)) {
            return NetUtil.validIpV4ToBytes(ipAddressString);
        }
        if (NetUtil.isValidIpV6Address(ipAddressString)) {
            int percentPos;
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            if ((percentPos = ipAddressString.indexOf(37)) >= 0) {
                ipAddressString = ipAddressString.substring(0, percentPos);
            }
            return NetUtil.getIPv6ByName(ipAddressString, true);
        }
        return null;
    }

    public static InetAddress createInetAddressFromIpAddressString(String ipAddressString) {
        if (NetUtil.isValidIpV4Address(ipAddressString)) {
            byte[] bytes = NetUtil.validIpV4ToBytes(ipAddressString);
            try {
                return InetAddress.getByAddress(bytes);
            }
            catch (UnknownHostException e) {
                throw new IllegalStateException(e);
            }
        }
        if (NetUtil.isValidIpV6Address(ipAddressString)) {
            int percentPos;
            if (ipAddressString.charAt(0) == '[') {
                ipAddressString = ipAddressString.substring(1, ipAddressString.length() - 1);
            }
            if ((percentPos = ipAddressString.indexOf(37)) >= 0) {
                try {
                    int scopeId = Integer.parseInt(ipAddressString.substring(percentPos + 1));
                    ipAddressString = ipAddressString.substring(0, percentPos);
                    byte[] bytes = NetUtil.getIPv6ByName(ipAddressString, true);
                    if (bytes == null) {
                        return null;
                    }
                    try {
                        return Inet6Address.getByAddress(null, bytes, scopeId);
                    }
                    catch (UnknownHostException e) {
                        throw new IllegalStateException(e);
                    }
                }
                catch (NumberFormatException e) {
                    return null;
                }
            }
            byte[] bytes = NetUtil.getIPv6ByName(ipAddressString, true);
            if (bytes == null) {
                return null;
            }
            try {
                return InetAddress.getByAddress(bytes);
            }
            catch (UnknownHostException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }

    private static int decimalDigit(String str, int pos) {
        return str.charAt(pos) - 48;
    }

    private static byte ipv4WordToByte(String ip, int from, int toExclusive) {
        int ret = NetUtil.decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        ret = ret * 10 + NetUtil.decimalDigit(ip, from);
        if (++from == toExclusive) {
            return (byte)ret;
        }
        return (byte)(ret * 10 + NetUtil.decimalDigit(ip, from));
    }

    static byte[] validIpV4ToBytes(String ip) {
        byte[] byArray = new byte[4];
        int i = ip.indexOf(46, 1);
        byArray[0] = NetUtil.ipv4WordToByte(ip, 0, i);
        int n = i + 1;
        i = ip.indexOf(46, i + 2);
        byArray[1] = NetUtil.ipv4WordToByte(ip, n, i);
        int n2 = i + 1;
        i = ip.indexOf(46, i + 2);
        byArray[2] = NetUtil.ipv4WordToByte(ip, n2, i);
        byArray[3] = NetUtil.ipv4WordToByte(ip, i + 1, ip.length());
        return byArray;
    }

    public static int ipv4AddressToInt(Inet4Address ipAddress) {
        byte[] octets = ipAddress.getAddress();
        return (octets[0] & 0xFF) << 24 | (octets[1] & 0xFF) << 16 | (octets[2] & 0xFF) << 8 | octets[3] & 0xFF;
    }

    public static String intToIpAddress(int i) {
        StringBuilder buf = new StringBuilder(15);
        buf.append(i >> 24 & 0xFF);
        buf.append('.');
        buf.append(i >> 16 & 0xFF);
        buf.append('.');
        buf.append(i >> 8 & 0xFF);
        buf.append('.');
        buf.append(i & 0xFF);
        return buf.toString();
    }

    public static String bytesToIpAddress(byte[] bytes) {
        return NetUtil.bytesToIpAddress(bytes, 0, bytes.length);
    }

    public static String bytesToIpAddress(byte[] bytes, int offset, int length) {
        switch (length) {
            case 4: {
                return new StringBuilder(15).append(bytes[offset] & 0xFF).append('.').append(bytes[offset + 1] & 0xFF).append('.').append(bytes[offset + 2] & 0xFF).append('.').append(bytes[offset + 3] & 0xFF).toString();
            }
            case 16: {
                return NetUtil.toAddressString(bytes, offset, false);
            }
        }
        throw new IllegalArgumentException("length: " + length + " (expected: 4 or 16)");
    }

    public static boolean isValidIpV6Address(String ip) {
        return NetUtil.isValidIpV6Address((CharSequence)ip);
    }

    public static boolean isValidIpV6Address(CharSequence ip) {
        int compressBegin;
        int colons;
        int start;
        int end = ip.length();
        if (end < 2) {
            return false;
        }
        char c = ip.charAt(0);
        if (c == '[') {
            if (ip.charAt(--end) != ']') {
                return false;
            }
            start = 1;
            c = ip.charAt(1);
        } else {
            start = 0;
        }
        if (c == ':') {
            if (ip.charAt(start + 1) != ':') {
                return false;
            }
            colons = 2;
            compressBegin = start;
            start += 2;
        } else {
            colons = 0;
            compressBegin = -1;
        }
        int wordLen = 0;
        block5: for (int i = start; i < end; ++i) {
            c = ip.charAt(i);
            if (NetUtil.isValidHexChar(c)) {
                if (wordLen < 4) {
                    ++wordLen;
                    continue;
                }
                return false;
            }
            switch (c) {
                case ':': {
                    if (colons > 7) {
                        return false;
                    }
                    if (ip.charAt(i - 1) == ':') {
                        if (compressBegin >= 0) {
                            return false;
                        }
                        compressBegin = i - 1;
                    } else {
                        wordLen = 0;
                    }
                    ++colons;
                    continue block5;
                }
                case '.': {
                    if (compressBegin < 0 && colons != 6 || colons == 7 && compressBegin >= start || colons > 7) {
                        return false;
                    }
                    int ipv4Start = i - wordLen;
                    int j = ipv4Start - 2;
                    if (NetUtil.isValidIPv4MappedChar(ip.charAt(j))) {
                        if (!(NetUtil.isValidIPv4MappedChar(ip.charAt(j - 1)) && NetUtil.isValidIPv4MappedChar(ip.charAt(j - 2)) && NetUtil.isValidIPv4MappedChar(ip.charAt(j - 3)))) {
                            return false;
                        }
                        j -= 5;
                    }
                    while (j >= start) {
                        char tmpChar = ip.charAt(j);
                        if (tmpChar != '0' && tmpChar != ':') {
                            return false;
                        }
                        --j;
                    }
                    int ipv4End = AsciiString.indexOf(ip, '%', ipv4Start + 7);
                    if (ipv4End < 0) {
                        ipv4End = end;
                    }
                    return NetUtil.isValidIpV4Address(ip, ipv4Start, ipv4End);
                }
                case '%': {
                    end = i;
                    break block5;
                }
                default: {
                    return false;
                }
            }
        }
        if (compressBegin < 0) {
            return colons == 7 && wordLen > 0;
        }
        return compressBegin + 2 == end || wordLen > 0 && (colons < 8 || compressBegin <= start);
    }

    private static boolean isValidIpV4Word(CharSequence word, int from, int toExclusive) {
        char c0;
        int len = toExclusive - from;
        if (len < 1 || len > 3 || (c0 = word.charAt(from)) < '0') {
            return false;
        }
        if (len == 3) {
            char c2;
            char c1 = word.charAt(from + 1);
            return c1 >= '0' && (c2 = word.charAt(from + 2)) >= '0' && (c0 <= '1' && c1 <= '9' && c2 <= '9' || c0 == '2' && c1 <= '5' && (c2 <= '5' || c1 < '5' && c2 <= '9'));
        }
        return c0 <= '9' && (len == 1 || NetUtil.isValidNumericChar(word.charAt(from + 1)));
    }

    private static boolean isValidHexChar(char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
    }

    private static boolean isValidNumericChar(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isValidIPv4MappedChar(char c) {
        return c == 'f' || c == 'F';
    }

    private static boolean isValidIPv4MappedSeparators(byte b0, byte b1, boolean mustBeZero) {
        return b0 == b1 && (b0 == 0 || !mustBeZero && b1 == -1);
    }

    private static boolean isValidIPv4Mapped(byte[] bytes, int currentIndex, int compressBegin, int compressLength) {
        boolean mustBeZero = compressBegin + compressLength >= 14;
        return currentIndex <= 12 && currentIndex >= 2 && (!mustBeZero || compressBegin < 12) && NetUtil.isValidIPv4MappedSeparators(bytes[currentIndex - 1], bytes[currentIndex - 2], mustBeZero) && PlatformDependent.isZero(bytes, 0, currentIndex - 3);
    }

    public static boolean isValidIpV4Address(CharSequence ip) {
        return NetUtil.isValidIpV4Address(ip, 0, ip.length());
    }

    public static boolean isValidIpV4Address(String ip) {
        return NetUtil.isValidIpV4Address(ip, 0, ip.length());
    }

    private static boolean isValidIpV4Address(CharSequence ip, int from, int toExcluded) {
        return ip instanceof String ? NetUtil.isValidIpV4Address((String)ip, from, toExcluded) : (ip instanceof AsciiString ? NetUtil.isValidIpV4Address((AsciiString)ip, from, toExcluded) : NetUtil.isValidIpV4Address0(ip, from, toExcluded));
    }

    private static boolean isValidIpV4Address(String ip, int from, int toExcluded) {
        int i;
        int len = toExcluded - from;
        return len <= 15 && len >= 7 && (i = ip.indexOf(46, from + 1)) > 0 && NetUtil.isValidIpV4Word(ip, from, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf(46, from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && NetUtil.isValidIpV4Word(ip, i + 1, toExcluded);
    }

    private static boolean isValidIpV4Address(AsciiString ip, int from, int toExcluded) {
        int i;
        int len = toExcluded - from;
        return len <= 15 && len >= 7 && (i = ip.indexOf('.', from + 1)) > 0 && NetUtil.isValidIpV4Word(ip, from, i) && (i = ip.indexOf('.', from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && (i = ip.indexOf('.', from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && NetUtil.isValidIpV4Word(ip, i + 1, toExcluded);
    }

    private static boolean isValidIpV4Address0(CharSequence ip, int from, int toExcluded) {
        int i;
        int len = toExcluded - from;
        return len <= 15 && len >= 7 && (i = AsciiString.indexOf(ip, '.', from + 1)) > 0 && NetUtil.isValidIpV4Word(ip, from, i) && (i = AsciiString.indexOf(ip, '.', from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && (i = AsciiString.indexOf(ip, '.', from = i + 2)) > 0 && NetUtil.isValidIpV4Word(ip, from - 1, i) && NetUtil.isValidIpV4Word(ip, i + 1, toExcluded);
    }

    public static Inet6Address getByName(CharSequence ip) {
        return NetUtil.getByName(ip, true);
    }

    public static Inet6Address getByName(CharSequence ip, boolean ipv4Mapped) {
        byte[] bytes = NetUtil.getIPv6ByName(ip, ipv4Mapped);
        if (bytes == null) {
            return null;
        }
        try {
            return Inet6Address.getByAddress(null, bytes, -1);
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] getIPv6ByName(CharSequence ip, boolean ipv4Mapped) {
        boolean isCompressed;
        int tmp;
        int i;
        byte[] bytes = new byte[16];
        int ipLength = ip.length();
        int compressBegin = 0;
        int compressLength = 0;
        int currentIndex = 0;
        int value = 0;
        int begin = -1;
        int ipv6Separators = 0;
        int ipv4Separators = 0;
        block4: for (i = 0; i < ipLength; ++i) {
            char c = ip.charAt(i);
            switch (c) {
                case ':': {
                    if (i - begin > 4 || ipv4Separators > 0 || ++ipv6Separators > 8 || currentIndex + 1 >= bytes.length) {
                        return null;
                    }
                    value <<= 4 - (i - begin) << 2;
                    if (compressLength > 0) {
                        compressLength -= 2;
                    }
                    bytes[currentIndex++] = (byte)((value & 0xF) << 4 | value >> 4 & 0xF);
                    bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | value >> 12 & 0xF);
                    tmp = i + 1;
                    if (tmp < ipLength && ip.charAt(tmp) == ':') {
                        if (compressBegin != 0 || ++tmp < ipLength && ip.charAt(tmp) == ':') {
                            return null;
                        }
                        ++ipv6Separators;
                        compressBegin = currentIndex;
                        compressLength = bytes.length - compressBegin - 2;
                        ++i;
                    }
                    value = 0;
                    begin = -1;
                    continue block4;
                }
                case '.': {
                    tmp = i - begin;
                    if (tmp > 3 || begin < 0 || ++ipv4Separators > 3 || ipv6Separators > 0 && currentIndex + compressLength < 12 || i + 1 >= ipLength || currentIndex >= bytes.length || ipv4Separators == 1 && (!ipv4Mapped || currentIndex != 0 && !NetUtil.isValidIPv4Mapped(bytes, currentIndex, compressBegin, compressLength) || tmp == 3 && (!NetUtil.isValidNumericChar(ip.charAt(i - 1)) || !NetUtil.isValidNumericChar(ip.charAt(i - 2)) || !NetUtil.isValidNumericChar(ip.charAt(i - 3))) || tmp == 2 && (!NetUtil.isValidNumericChar(ip.charAt(i - 1)) || !NetUtil.isValidNumericChar(ip.charAt(i - 2))) || tmp == 1 && !NetUtil.isValidNumericChar(ip.charAt(i - 1)))) {
                        return null;
                    }
                    if ((begin = ((value <<= 3 - tmp << 2) & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF)) > 255) {
                        return null;
                    }
                    bytes[currentIndex++] = (byte)begin;
                    value = 0;
                    begin = -1;
                    continue block4;
                }
                default: {
                    if (!NetUtil.isValidHexChar(c) || ipv4Separators > 0 && !NetUtil.isValidNumericChar(c)) {
                        return null;
                    }
                    if (begin < 0) {
                        begin = i;
                    } else if (i - begin > 4) {
                        return null;
                    }
                    value += StringUtil.decodeHexNibble(c) << (i - begin << 2);
                }
            }
        }
        boolean bl = isCompressed = compressBegin > 0;
        if (ipv4Separators > 0) {
            if (begin > 0 && i - begin > 3 || ipv4Separators != 3 || currentIndex >= bytes.length) {
                return null;
            }
            if (ipv6Separators != 0 && (ipv6Separators < 2 || (isCompressed || ipv6Separators != 6 || ip.charAt(0) == ':') && (!isCompressed || ipv6Separators >= 8 || ip.charAt(0) == ':' && compressBegin > 2))) {
                return null;
            }
            if ((begin = ((value <<= 3 - (i - begin) << 2) & 0xF) * 100 + (value >> 4 & 0xF) * 10 + (value >> 8 & 0xF)) > 255) {
                return null;
            }
            bytes[currentIndex++] = (byte)begin;
        } else {
            tmp = ipLength - 1;
            if (begin > 0 && i - begin > 4 || ipv6Separators < 2 || !isCompressed && (ipv6Separators + 1 != 8 || ip.charAt(0) == ':' || ip.charAt(tmp) == ':') || isCompressed && (ipv6Separators > 8 || ipv6Separators == 8 && (compressBegin <= 2 && ip.charAt(0) != ':' || compressBegin >= 14 && ip.charAt(tmp) != ':')) || currentIndex + 1 >= bytes.length || begin < 0 && ip.charAt(tmp - 1) != ':' || compressBegin > 2 && ip.charAt(0) == ':') {
                return null;
            }
            if (begin >= 0 && i - begin <= 4) {
                value <<= 4 - (i - begin) << 2;
            }
            bytes[currentIndex++] = (byte)((value & 0xF) << 4 | value >> 4 & 0xF);
            bytes[currentIndex++] = (byte)((value >> 8 & 0xF) << 4 | value >> 12 & 0xF);
        }
        if (currentIndex < bytes.length) {
            int toBeCopiedLength = currentIndex - compressBegin;
            int targetIndex = bytes.length - toBeCopiedLength;
            System.arraycopy(bytes, compressBegin, bytes, targetIndex, toBeCopiedLength);
            Arrays.fill(bytes, compressBegin, targetIndex, (byte)0);
        }
        if (ipv4Separators > 0) {
            bytes[11] = -1;
            bytes[10] = -1;
        }
        return bytes;
    }

    public static String toSocketAddressString(InetSocketAddress addr) {
        StringBuilder sb;
        String port = String.valueOf(addr.getPort());
        if (addr.isUnresolved()) {
            String hostname;
            sb = NetUtil.newSocketAddressStringBuilder(hostname, port, !NetUtil.isValidIpV6Address(hostname = NetUtil.getHostname(addr)));
        } else {
            InetAddress address = addr.getAddress();
            String hostString = NetUtil.toAddressString(address);
            sb = NetUtil.newSocketAddressStringBuilder(hostString, port, address instanceof Inet4Address);
        }
        return sb.append(':').append(port).toString();
    }

    public static String toSocketAddressString(String host, int port) {
        String portStr = String.valueOf(port);
        return NetUtil.newSocketAddressStringBuilder(host, portStr, !NetUtil.isValidIpV6Address(host)).append(':').append(portStr).toString();
    }

    private static StringBuilder newSocketAddressStringBuilder(String host, String port, boolean ipv4) {
        int hostLen = host.length();
        if (ipv4) {
            return new StringBuilder(hostLen + 1 + port.length()).append(host);
        }
        StringBuilder stringBuilder = new StringBuilder(hostLen + 3 + port.length());
        if (hostLen > 1 && host.charAt(0) == '[' && host.charAt(hostLen - 1) == ']') {
            return stringBuilder.append(host);
        }
        return stringBuilder.append('[').append(host).append(']');
    }

    public static String toAddressString(InetAddress ip) {
        return NetUtil.toAddressString(ip, false);
    }

    public static String toAddressString(InetAddress ip, boolean ipv4Mapped) {
        if (ip instanceof Inet4Address) {
            return ip.getHostAddress();
        }
        if (!(ip instanceof Inet6Address)) {
            throw new IllegalArgumentException("Unhandled type: " + ip);
        }
        return NetUtil.toAddressString(ip.getAddress(), 0, ipv4Mapped);
    }

    private static String toAddressString(byte[] bytes, int offset, boolean ipv4Mapped) {
        int currentLength;
        int[] words = new int[8];
        for (int i = 0; i < words.length; ++i) {
            int idx = (i << 1) + offset;
            words[i] = (bytes[idx] & 0xFF) << 8 | bytes[idx + 1] & 0xFF;
        }
        int currentStart = -1;
        int shortestStart = -1;
        int shortestLength = 0;
        for (int i = 0; i < words.length; ++i) {
            if (words[i] == 0) {
                if (currentStart >= 0) continue;
                currentStart = i;
                continue;
            }
            if (currentStart < 0) continue;
            currentLength = i - currentStart;
            if (currentLength > shortestLength) {
                shortestStart = currentStart;
                shortestLength = currentLength;
            }
            currentStart = -1;
        }
        if (currentStart >= 0 && (currentLength = words.length - currentStart) > shortestLength) {
            shortestStart = currentStart;
            shortestLength = currentLength;
        }
        if (shortestLength == 1) {
            shortestLength = 0;
            shortestStart = -1;
        }
        int shortestEnd = shortestStart + shortestLength;
        StringBuilder b = new StringBuilder(39);
        if (shortestEnd < 0) {
            b.append(Integer.toHexString(words[0]));
            for (int i = 1; i < words.length; ++i) {
                b.append(':');
                b.append(Integer.toHexString(words[i]));
            }
        } else {
            boolean isIpv4Mapped;
            if (NetUtil.inRangeEndExclusive(0, shortestStart, shortestEnd)) {
                b.append("::");
                isIpv4Mapped = ipv4Mapped && shortestEnd == 5 && words[5] == 65535;
            } else {
                b.append(Integer.toHexString(words[0]));
                isIpv4Mapped = false;
            }
            for (int i = 1; i < words.length; ++i) {
                if (!NetUtil.inRangeEndExclusive(i, shortestStart, shortestEnd)) {
                    if (!NetUtil.inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) {
                        if (!isIpv4Mapped || i == 6) {
                            b.append(':');
                        } else {
                            b.append('.');
                        }
                    }
                    if (isIpv4Mapped && i > 5) {
                        b.append(words[i] >> 8);
                        b.append('.');
                        b.append(words[i] & 0xFF);
                        continue;
                    }
                    b.append(Integer.toHexString(words[i]));
                    continue;
                }
                if (NetUtil.inRangeEndExclusive(i - 1, shortestStart, shortestEnd)) continue;
                b.append("::");
            }
        }
        return b.toString();
    }

    public static String getHostname(InetSocketAddress addr) {
        return addr.getHostString();
    }

    private static boolean inRangeEndExclusive(int value, int start, int end) {
        return value >= start && value < end;
    }

    private NetUtil() {
    }

    static {
        IPV4_PREFERRED = SystemPropertyUtil.getBoolean("java.net.preferIPv4Stack", false);
        logger = InternalLoggerFactory.getInstance(NetUtil.class);
        String prefer = SystemPropertyUtil.get("java.net.preferIPv6Addresses", "false");
        IPV6_ADDRESSES_PREFERRED = "true".equalsIgnoreCase(prefer.trim());
        logger.debug("-Djava.net.preferIPv4Stack: {}", (Object)IPV4_PREFERRED);
        logger.debug("-Djava.net.preferIPv6Addresses: {}", (Object)prefer);
        NETWORK_INTERFACES = NetUtilInitializations.networkInterfaces();
        LOCALHOST4 = NetUtilInitializations.createLocalhost4();
        LOCALHOST6 = NetUtilInitializations.createLocalhost6();
        NetUtilInitializations.NetworkIfaceAndInetAddress loopback = NetUtilInitializations.determineLoopback(NETWORK_INTERFACES, LOCALHOST4, LOCALHOST6);
        LOOPBACK_IF = loopback.iface();
        LOCALHOST = loopback.address();
        SOMAXCONN = AccessController.doPrivileged(new SoMaxConnAction());
    }

    private static final class SoMaxConnAction
    implements PrivilegedAction<Integer> {
        private SoMaxConnAction() {
        }

        @Override
        public Integer run() {
            int somaxconn;
            block14: {
                somaxconn = PlatformDependent.isWindows() ? 200 : (PlatformDependent.isOsx() ? 128 : 4096);
                File file = new File("/proc/sys/net/core/somaxconn");
                try {
                    if (file.exists()) {
                        try (BufferedReader in = new BufferedReader(new InputStreamReader(new BoundedInputStream(new FileInputStream(file))));){
                            somaxconn = Integer.parseInt(in.readLine());
                            if (logger.isDebugEnabled()) {
                                logger.debug("{}: {}", (Object)file, (Object)somaxconn);
                            }
                            break block14;
                        }
                    }
                    Integer tmp = null;
                    if (SystemPropertyUtil.getBoolean("io.netty.net.somaxconn.trySysctl", false)) {
                        tmp = NetUtil.sysctlGetInt("kern.ipc.somaxconn");
                        if (tmp == null) {
                            tmp = NetUtil.sysctlGetInt("kern.ipc.soacceptqueue");
                            if (tmp != null) {
                                somaxconn = tmp;
                            }
                        } else {
                            somaxconn = tmp;
                        }
                    }
                    if (tmp == null) {
                        logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", (Object)file, (Object)somaxconn);
                    }
                }
                catch (Exception e) {
                    if (!logger.isDebugEnabled()) break block14;
                    logger.debug("Failed to get SOMAXCONN from sysctl and file {}. Default: {}", file, somaxconn, e);
                }
            }
            return somaxconn;
        }
    }
}

