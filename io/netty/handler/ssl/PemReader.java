/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.util.CharsetUtil;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PemReader {
    private static final Pattern CERT_HEADER = Pattern.compile("-+BEGIN\\s[^-\\r\\n]*CERTIFICATE[^-\\r\\n]*-+(?:\\s|\\r|\\n)+");
    private static final Pattern CERT_FOOTER = Pattern.compile("-+END\\s[^-\\r\\n]*CERTIFICATE[^-\\r\\n]*-+(?:\\s|\\r|\\n)*");
    private static final Pattern KEY_HEADER = Pattern.compile("-+BEGIN\\s[^-\\r\\n]*PRIVATE\\s+KEY[^-\\r\\n]*-+(?:\\s|\\r|\\n)+");
    private static final Pattern KEY_FOOTER = Pattern.compile("-+END\\s[^-\\r\\n]*PRIVATE\\s+KEY[^-\\r\\n]*-+(?:\\s|\\r|\\n)*");
    private static final Pattern BODY = Pattern.compile("[a-z0-9+/=][a-z0-9+/=\\r\\n]*", 2);

    static ByteBuf[] readCertificates(File file) throws CertificateException {
        ByteBuf[] byteBufArray;
        FileInputStream in = new FileInputStream(file);
        try {
            byteBufArray = PemReader.readCertificates(in);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)in).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new CertificateException("could not find certificate file: " + file);
            }
        }
        ((InputStream)in).close();
        return byteBufArray;
    }

    static ByteBuf[] readCertificates(InputStream in) throws CertificateException {
        String content;
        try {
            content = PemReader.readContent(in);
        }
        catch (IOException e) {
            throw new CertificateException("failed to read certificate input stream", e);
        }
        ArrayList<ByteBuf> certs = new ArrayList<ByteBuf>();
        Matcher m = CERT_HEADER.matcher(content);
        int start = 0;
        while (m.find(start)) {
            start = m.end();
            m.usePattern(BODY);
            if (!m.find(start)) break;
            ByteBuf base64 = Unpooled.copiedBuffer(m.group(0), CharsetUtil.US_ASCII);
            start = m.end();
            m.usePattern(CERT_FOOTER);
            if (!m.find(start)) break;
            ByteBuf der = Base64.decode(base64);
            base64.release();
            certs.add(der);
            start = m.end();
            m.usePattern(CERT_HEADER);
        }
        if (certs.isEmpty()) {
            throw new CertificateException("found no certificates in input stream");
        }
        return certs.toArray(new ByteBuf[0]);
    }

    static ByteBuf readPrivateKey(File file) throws KeyException {
        ByteBuf byteBuf;
        FileInputStream in = new FileInputStream(file);
        try {
            byteBuf = PemReader.readPrivateKey(in);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)in).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                throw new KeyException("could not find key file: " + file);
            }
        }
        ((InputStream)in).close();
        return byteBuf;
    }

    static ByteBuf readPrivateKey(InputStream in) throws KeyException {
        String content;
        try {
            content = PemReader.readContent(in);
        }
        catch (IOException e) {
            throw new KeyException("failed to read key input stream", e);
        }
        int start = 0;
        Matcher m = KEY_HEADER.matcher(content);
        if (!m.find(start)) {
            throw PemReader.keyNotFoundException();
        }
        start = m.end();
        m.usePattern(BODY);
        if (!m.find(start)) {
            throw PemReader.keyNotFoundException();
        }
        ByteBuf base64 = Unpooled.copiedBuffer(m.group(0), CharsetUtil.US_ASCII);
        start = m.end();
        m.usePattern(KEY_FOOTER);
        if (!m.find(start)) {
            throw PemReader.keyNotFoundException();
        }
        ByteBuf der = Base64.decode(base64);
        base64.release();
        return der;
    }

    private static KeyException keyNotFoundException() {
        return new KeyException("could not find a PKCS #8 private key in input stream (see https://netty.io/wiki/sslcontextbuilder-and-private-key.html for more information)");
    }

    private static String readContent(InputStream in) throws IOException {
        int ret;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while ((ret = in.read(buf)) >= 0) {
            out.write(buf, 0, ret);
        }
        return out.toString(CharsetUtil.US_ASCII.name());
    }

    private PemReader() {
    }
}

