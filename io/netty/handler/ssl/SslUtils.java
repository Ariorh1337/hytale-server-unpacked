/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.codec.base64.Base64Dialect;
import io.netty.handler.ssl.SslHandshakeCompletionEvent;
import io.netty.util.NetUtil;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;

final class SslUtils {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslUtils.class);
    static final Set<String> TLSV13_CIPHERS = Collections.unmodifiableSet(new LinkedHashSet<String>(Arrays.asList("TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256", "TLS_AES_128_GCM_SHA256", "TLS_AES_128_CCM_8_SHA256", "TLS_AES_128_CCM_SHA256")));
    static final short DTLS_1_0 = -257;
    static final short DTLS_1_2 = -259;
    static final short DTLS_1_3 = -260;
    static final short DTLS_RECORD_HEADER_LENGTH = 13;
    private static final String DEFAULT_ENDPOINT_VERIFICATION_ALGORITHM_PROPERTY = "io.netty.handler.ssl.defaultEndpointVerificationAlgorithm";
    static final String defaultEndpointVerificationAlgorithm;
    static final int GMSSL_PROTOCOL_VERSION = 257;
    static final String INVALID_CIPHER = "SSL_NULL_WITH_NULL_NULL";
    static final int SSL_CONTENT_TYPE_CHANGE_CIPHER_SPEC = 20;
    static final int SSL_CONTENT_TYPE_ALERT = 21;
    static final int SSL_CONTENT_TYPE_HANDSHAKE = 22;
    static final int SSL_CONTENT_TYPE_APPLICATION_DATA = 23;
    static final int SSL_CONTENT_TYPE_EXTENSION_HEARTBEAT = 24;
    static final int SSL_RECORD_HEADER_LENGTH = 5;
    static final int NOT_ENOUGH_DATA = -1;
    static final int NOT_ENCRYPTED = -2;
    static final String[] DEFAULT_CIPHER_SUITES;
    static final String[] DEFAULT_TLSV13_CIPHER_SUITES;
    static final String[] TLSV13_CIPHER_SUITES;
    static final String PROBING_CERT = "-----BEGIN CERTIFICATE-----\nMIICrjCCAZagAwIBAgIIdSvQPv1QAZQwDQYJKoZIhvcNAQELBQAwFjEUMBIGA1UEAxMLZXhhbXBs\nZS5jb20wIBcNMTgwNDA2MjIwNjU5WhgPOTk5OTEyMzEyMzU5NTlaMBYxFDASBgNVBAMTC2V4YW1w\nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAggbWsmDQ6zNzRZ5AW8E3eoGl\nqWvOBDb5Fs1oBRrVQHuYmVAoaqwDzXYJ0LOwa293AgWEQ1jpcbZ2hpoYQzqEZBTLnFhMrhRFlH6K\nbJND8Y33kZ/iSVBBDuGbdSbJShlM+4WwQ9IAso4MZ4vW3S1iv5fGGpLgbtXRmBf/RU8omN0Gijlv\nWlLWHWijLN8xQtySFuBQ7ssW8RcKAary3pUm6UUQB+Co6lnfti0Tzag8PgjhAJq2Z3wbsGRnP2YS\nvYoaK6qzmHXRYlp/PxrjBAZAmkLJs4YTm/XFF+fkeYx4i9zqHbyone5yerRibsHaXZWLnUL+rFoe\nMdKvr0VS3sGmhQIDAQABMA0GCSqGSIb3DQEBCwUAA4IBAQADQi441pKmXf9FvUV5EHU4v8nJT9Iq\nyqwsKwXnr7AsUlDGHBD7jGrjAXnG5rGxuNKBQ35wRxJATKrUtyaquFUL6H8O6aGQehiFTk6zmPbe\n12Gu44vqqTgIUxnv3JQJiox8S2hMxsSddpeCmSdvmalvD6WG4NthH6B9ZaBEiep1+0s0RUaBYn73\nI7CCUaAtbjfR6pcJjrFk5ei7uwdQZFSJtkP2z8r7zfeANJddAKFlkaMWn7u+OIVuB4XPooWicObk\nNAHFtP65bocUYnDpTVdiyvn8DdqyZ/EO8n1bBKBzuSLplk2msW4pdgaFgY7Vw/0wzcFXfUXmL1uy\nG8sQD/wx\n-----END CERTIFICATE-----";
    static final String PROBING_KEY = "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCBtayYNDrM3NFnkBbwTd6gaWp\na84ENvkWzWgFGtVAe5iZUChqrAPNdgnQs7Brb3cCBYRDWOlxtnaGmhhDOoRkFMucWEyuFEWUfops\nk0PxjfeRn+JJUEEO4Zt1JslKGUz7hbBD0gCyjgxni9bdLWK/l8YakuBu1dGYF/9FTyiY3QaKOW9a\nUtYdaKMs3zFC3JIW4FDuyxbxFwoBqvLelSbpRRAH4KjqWd+2LRPNqDw+COEAmrZnfBuwZGc/ZhK9\nihorqrOYddFiWn8/GuMEBkCaQsmzhhOb9cUX5+R5jHiL3OodvKid7nJ6tGJuwdpdlYudQv6sWh4x\n0q+vRVLewaaFAgMBAAECggEAP8tPJvFtTxhNJAkCloHz0D0vpDHqQBMgntlkgayqmBqLwhyb18pR\ni0qwgh7HHc7wWqOOQuSqlEnrWRrdcI6TSe8R/sErzfTQNoznKWIPYcI/hskk4sdnQ//Yn9/Jvnsv\nU/BBjOTJxtD+sQbhAl80JcA3R+5sArURQkfzzHOL/YMqzAsn5hTzp7HZCxUqBk3KaHRxV7NefeOE\nxlZuWSmxYWfbFIs4kx19/1t7h8CHQWezw+G60G2VBtSBBxDnhBWvqG6R/wpzJ3nEhPLLY9T+XIHe\nipzdMOOOUZorfIg7M+pyYPji+ZIZxIpY5OjrOzXHciAjRtr5Y7l99K1CG1LguQKBgQDrQfIMxxtZ\nvxU/1cRmUV9l7pt5bjV5R6byXq178LxPKVYNjdZ840Q0/OpZEVqaT1xKVi35ohP1QfNjxPLlHD+K\niDAR9z6zkwjIrbwPCnb5kuXy4lpwPcmmmkva25fI7qlpHtbcuQdoBdCfr/KkKaUCMPyY89LCXgEw\n5KTDj64UywKBgQCNfbO+eZLGzhiHhtNJurresCsIGWlInv322gL8CSfBMYl6eNfUTZvUDdFhPISL\nUljKWzXDrjw0ujFSPR0XhUGtiq89H+HUTuPPYv25gVXO+HTgBFZEPl4PpA+BUsSVZy0NddneyqLk\n42Wey9omY9Q8WsdNQS5cbUvy0uG6WFoX7wKBgQDZ1jpW8pa0x2bZsQsm4vo+3G5CRnZlUp+XlWt2\ndDcp5dC0xD1zbs1dc0NcLeGDOTDv9FSl7hok42iHXXq8AygjEm/QcuwwQ1nC2HxmQP5holAiUs4D\nWHM8PWs3wFYPzE459EBoKTxeaeP/uWAn+he8q7d5uWvSZlEcANs/6e77eQKBgD21Ar0hfFfj7mK8\n9E0FeRZBsqK3omkfnhcYgZC11Xa2SgT1yvs2Va2n0RcdM5kncr3eBZav2GYOhhAdwyBM55XuE/sO\neokDVutNeuZ6d5fqV96TRaRBpvgfTvvRwxZ9hvKF4Vz+9wfn/JvCwANaKmegF6ejs7pvmF3whq2k\ndrZVAoGAX5YxQ5XMTD0QbMAl7/6qp6S58xNoVdfCkmkj1ZLKaHKIjS/benkKGlySVQVPexPfnkZx\np/Vv9yyphBoudiTBS9Uog66ueLYZqpgxlM/6OhYg86Gm3U2ycvMxYjBM1NFiyze21AqAhI+HX+Ot\nmraV2/guSgDgZAhukRZzeQ2RucI=\n-----END PRIVATE KEY-----";
    private static final boolean TLSV1_3_JDK_SUPPORTED;
    private static final boolean TLSV1_3_JDK_DEFAULT_ENABLED;

    static boolean isTLSv13SupportedByJDK(Provider provider) {
        if (provider == null) {
            return TLSV1_3_JDK_SUPPORTED;
        }
        return SslUtils.isTLSv13SupportedByJDK0(provider);
    }

    private static boolean isTLSv13SupportedByJDK0(Provider provider) {
        try {
            return SslUtils.arrayContains(SslUtils.newInitContext(provider).getSupportedSSLParameters().getProtocols(), "TLSv1.3");
        }
        catch (Throwable cause) {
            logger.debug("Unable to detect if JDK SSLEngine with provider {} supports TLSv1.3, assuming no", (Object)provider, (Object)cause);
            return false;
        }
    }

    static boolean isTLSv13EnabledByJDK(Provider provider) {
        if (provider == null) {
            return TLSV1_3_JDK_DEFAULT_ENABLED;
        }
        return SslUtils.isTLSv13EnabledByJDK0(provider);
    }

    private static boolean isTLSv13EnabledByJDK0(Provider provider) {
        try {
            return SslUtils.arrayContains(SslUtils.newInitContext(provider).getDefaultSSLParameters().getProtocols(), "TLSv1.3");
        }
        catch (Throwable cause) {
            logger.debug("Unable to detect if JDK SSLEngine with provider {} enables TLSv1.3 by default, assuming no", (Object)provider, (Object)cause);
            return false;
        }
    }

    private static SSLContext newInitContext(Provider provider) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext context = provider == null ? SSLContext.getInstance("TLS") : SSLContext.getInstance("TLS", provider);
        context.init(null, new TrustManager[0], null);
        return context;
    }

    static SSLContext getSSLContext(Provider provider) throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
        return SslUtils.getSSLContext(provider, null);
    }

    static SSLContext getSSLContext(Provider provider, SecureRandom secureRandom) throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException {
        SSLContext context = provider == null ? SSLContext.getInstance(SslUtils.getTlsVersion()) : SSLContext.getInstance(SslUtils.getTlsVersion(), provider);
        context.init(null, new TrustManager[0], secureRandom);
        return context;
    }

    private static String getTlsVersion() {
        return TLSV1_3_JDK_SUPPORTED ? "TLSv1.3" : "TLSv1.2";
    }

    static boolean arrayContains(String[] array, String value) {
        for (String v : array) {
            if (!value.equals(v)) continue;
            return true;
        }
        return false;
    }

    static void addIfSupported(Set<String> supported, List<String> enabled, String ... names) {
        for (String n : names) {
            if (!supported.contains(n)) continue;
            enabled.add(n);
        }
    }

    static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, Iterable<String> fallbackCiphers) {
        if (defaultCiphers.isEmpty()) {
            for (String cipher : fallbackCiphers) {
                if (cipher.startsWith("SSL_") || cipher.contains("_RC4_")) continue;
                defaultCiphers.add(cipher);
            }
        }
    }

    static void useFallbackCiphersIfDefaultIsEmpty(List<String> defaultCiphers, String ... fallbackCiphers) {
        SslUtils.useFallbackCiphersIfDefaultIsEmpty(defaultCiphers, Arrays.asList(fallbackCiphers));
    }

    static SSLHandshakeException toSSLHandshakeException(Throwable e) {
        if (e instanceof SSLHandshakeException) {
            return (SSLHandshakeException)e;
        }
        return (SSLHandshakeException)new SSLHandshakeException(e.getMessage()).initCause(e);
    }

    static int getEncryptedPacketLength(ByteBuf buffer, int offset, boolean probeSSLv2) {
        boolean tls;
        assert (offset >= buffer.readerIndex());
        int remaining = buffer.writerIndex() - offset;
        if (remaining < 5) {
            return -1;
        }
        int packetLength = 0;
        switch (buffer.getUnsignedByte(offset)) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                break;
            }
            default: {
                if (!probeSSLv2) {
                    return -2;
                }
                tls = false;
            }
        }
        if (tls) {
            short majorVersion = buffer.getUnsignedByte(offset + 1);
            short version = buffer.getShort(offset + 1);
            if (majorVersion == 3 || version == 257) {
                packetLength = SslUtils.unsignedShortBE(buffer, offset + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else if (version == -257 || version == -259 || version == -260) {
                if (remaining < 13) {
                    return -1;
                }
                packetLength = SslUtils.unsignedShortBE(buffer, offset + 13 - 2) + 13;
            } else {
                tls = false;
            }
        }
        if (!tls) {
            int headerLength = (buffer.getUnsignedByte(offset) & 0x80) != 0 ? 2 : 3;
            short majorVersion = buffer.getUnsignedByte(offset + headerLength + 1);
            if (majorVersion == 2 || majorVersion == 3) {
                int n = packetLength = headerLength == 2 ? (SslUtils.shortBE(buffer, offset) & Short.MAX_VALUE) + 2 : (SslUtils.shortBE(buffer, offset) & 0x3FFF) + 3;
                if (packetLength <= headerLength) {
                    return -2;
                }
            } else {
                return -2;
            }
        }
        return packetLength;
    }

    private static int unsignedShortBE(ByteBuf buffer, int offset) {
        int value = buffer.getUnsignedShort(offset);
        if (buffer.order() == ByteOrder.LITTLE_ENDIAN) {
            value = Integer.reverseBytes(value) >>> 16;
        }
        return value;
    }

    private static short shortBE(ByteBuf buffer, int offset) {
        short value = buffer.getShort(offset);
        if (buffer.order() == ByteOrder.LITTLE_ENDIAN) {
            value = Short.reverseBytes(value);
        }
        return value;
    }

    private static short unsignedByte(byte b) {
        return (short)(b & 0xFF);
    }

    private static int unsignedShortBE(ByteBuffer buffer, int offset) {
        return SslUtils.shortBE(buffer, offset) & 0xFFFF;
    }

    private static short shortBE(ByteBuffer buffer, int offset) {
        return buffer.order() == ByteOrder.BIG_ENDIAN ? buffer.getShort(offset) : ByteBufUtil.swapShort(buffer.getShort(offset));
    }

    static int getEncryptedPacketLength(ByteBuffer[] buffers, int offset) {
        ByteBuffer buffer = buffers[offset];
        if (buffer.remaining() >= 5) {
            return SslUtils.getEncryptedPacketLength(buffer);
        }
        ByteBuffer tmp = ByteBuffer.allocate(5);
        do {
            if ((buffer = buffers[offset++].duplicate()).remaining() > tmp.remaining()) {
                buffer.limit(buffer.position() + tmp.remaining());
            }
            tmp.put(buffer);
        } while (tmp.hasRemaining() && offset < buffers.length);
        tmp.flip();
        return SslUtils.getEncryptedPacketLength(tmp);
    }

    private static int getEncryptedPacketLength(ByteBuffer buffer) {
        boolean tls;
        int remaining = buffer.remaining();
        if (remaining < 5) {
            return -1;
        }
        int packetLength = 0;
        int pos = buffer.position();
        switch (SslUtils.unsignedByte(buffer.get(pos))) {
            case 20: 
            case 21: 
            case 22: 
            case 23: 
            case 24: {
                tls = true;
                break;
            }
            default: {
                tls = false;
            }
        }
        if (tls) {
            short majorVersion = SslUtils.unsignedByte(buffer.get(pos + 1));
            if (majorVersion == 3 || buffer.getShort(pos + 1) == 257) {
                packetLength = SslUtils.unsignedShortBE(buffer, pos + 3) + 5;
                if (packetLength <= 5) {
                    tls = false;
                }
            } else {
                tls = false;
            }
        }
        if (!tls) {
            int headerLength = (SslUtils.unsignedByte(buffer.get(pos)) & 0x80) != 0 ? 2 : 3;
            short majorVersion = SslUtils.unsignedByte(buffer.get(pos + headerLength + 1));
            if (majorVersion == 2 || majorVersion == 3) {
                int n = packetLength = headerLength == 2 ? (SslUtils.shortBE(buffer, pos) & Short.MAX_VALUE) + 2 : (SslUtils.shortBE(buffer, pos) & 0x3FFF) + 3;
                if (packetLength <= headerLength) {
                    return -2;
                }
            } else {
                return -2;
            }
        }
        return packetLength;
    }

    static void handleHandshakeFailure(ChannelHandlerContext ctx, Throwable cause, boolean notify) {
        ctx.flush();
        if (notify) {
            ctx.fireUserEventTriggered(new SslHandshakeCompletionEvent(cause));
        }
        ctx.close();
    }

    static void zeroout(ByteBuf buffer) {
        if (!buffer.isReadOnly()) {
            buffer.setZero(0, buffer.capacity());
        }
    }

    static void zerooutAndRelease(ByteBuf buffer) {
        SslUtils.zeroout(buffer);
        buffer.release();
    }

    static ByteBuf toBase64(ByteBufAllocator allocator, ByteBuf src) {
        ByteBuf dst = Base64.encode(src, src.readerIndex(), src.readableBytes(), true, Base64Dialect.STANDARD, allocator);
        src.readerIndex(src.writerIndex());
        return dst;
    }

    static boolean isValidHostNameForSNI(String hostname) {
        return hostname != null && hostname.indexOf(46) > 0 && !hostname.endsWith(".") && !hostname.startsWith("/") && !NetUtil.isValidIpV4Address(hostname) && !NetUtil.isValidIpV6Address(hostname);
    }

    static boolean isTLSv13Cipher(String cipher) {
        return TLSV13_CIPHERS.contains(cipher);
    }

    private SslUtils() {
    }

    static {
        TLSV13_CIPHER_SUITES = new String[]{"TLS_AES_128_GCM_SHA256", "TLS_AES_256_GCM_SHA384"};
        TLSV1_3_JDK_SUPPORTED = SslUtils.isTLSv13SupportedByJDK0(null);
        TLSV1_3_JDK_DEFAULT_ENABLED = SslUtils.isTLSv13EnabledByJDK0(null);
        DEFAULT_TLSV13_CIPHER_SUITES = TLSV1_3_JDK_SUPPORTED ? TLSV13_CIPHER_SUITES : EmptyArrays.EMPTY_STRINGS;
        LinkedHashSet<String> defaultCiphers = new LinkedHashSet<String>();
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_GCM_SHA256");
        defaultCiphers.add("TLS_RSA_WITH_AES_128_CBC_SHA");
        defaultCiphers.add("TLS_RSA_WITH_AES_256_CBC_SHA");
        Collections.addAll(defaultCiphers, DEFAULT_TLSV13_CIPHER_SUITES);
        DEFAULT_CIPHER_SUITES = defaultCiphers.toArray(EmptyArrays.EMPTY_STRINGS);
        String defaultEndpointVerification = SystemPropertyUtil.get(DEFAULT_ENDPOINT_VERIFICATION_ALGORITHM_PROPERTY);
        if ("LDAP".equalsIgnoreCase(defaultEndpointVerification)) {
            defaultEndpointVerificationAlgorithm = "LDAP";
        } else if ("NONE".equalsIgnoreCase(defaultEndpointVerification)) {
            logger.info("Default SSL endpoint verification has been disabled:  -D{}=\"{}\"", (Object)DEFAULT_ENDPOINT_VERIFICATION_ALGORITHM_PROPERTY, (Object)defaultEndpointVerification);
            defaultEndpointVerificationAlgorithm = null;
        } else {
            if (defaultEndpointVerification != null && !"HTTPS".equalsIgnoreCase(defaultEndpointVerification)) {
                logger.warn("Unknown default SSL endpoint verification algorithm: -D{}=\"{}\", will use \"HTTPS\" instead.", (Object)DEFAULT_ENDPOINT_VERIFICATION_ALGORITHM_PROPERTY, (Object)defaultEndpointVerification);
            }
            defaultEndpointVerificationAlgorithm = "HTTPS";
        }
    }
}

