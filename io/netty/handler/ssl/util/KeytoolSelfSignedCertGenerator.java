/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

final class KeytoolSelfSignedCertGenerator {
    private static final DateTimeFormatter DATE_FORMAT;
    private static final String ALIAS = "alias";
    private static final String PASSWORD = "insecurepassword";
    private static final Path KEYTOOL;
    private static final String KEY_STORE_TYPE;

    private KeytoolSelfSignedCertGenerator() {
    }

    static boolean isAvailable() {
        return KEYTOOL != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void generate(SelfSignedCertificate.Builder builder) throws IOException, GeneralSecurityException {
        String dirFqdn = builder.fqdn.replaceAll("[^\\w.-]", "x");
        Path directory = Files.createTempDirectory("keytool_" + dirFqdn, new FileAttribute[0]);
        Path keyStore = directory.resolve("keystore.jks");
        try {
            Process process = new ProcessBuilder(new String[0]).command(KEYTOOL.toAbsolutePath().toString(), "-genkeypair", "-keyalg", builder.algorithm, "-keysize", String.valueOf(builder.bits), "-startdate", DATE_FORMAT.format(builder.notBefore.toInstant().atZone(ZoneId.systemDefault())), "-validity", String.valueOf(builder.notBefore.toInstant().until(builder.notAfter.toInstant(), ChronoUnit.DAYS)), "-keystore", keyStore.toString(), "-alias", ALIAS, "-keypass", PASSWORD, "-storepass", PASSWORD, "-dname", "CN=" + builder.fqdn, "-storetype", KEY_STORE_TYPE).redirectErrorStream(true).start();
            try {
                if (!process.waitFor(60L, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                    throw new IOException("keytool timeout");
                }
            }
            catch (InterruptedException e) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
                throw new InterruptedIOException();
            }
            if (process.exitValue() != 0) {
                ByteBuf buffer = Unpooled.buffer();
                try {
                    try (InputStream stream = process.getInputStream();){
                        while (buffer.writeBytes(stream, 4096) != -1) {
                        }
                    }
                    String log = buffer.toString(StandardCharsets.UTF_8);
                    throw new IOException("Keytool exited with status " + process.exitValue() + ": " + log);
                }
                catch (Throwable throwable) {
                    buffer.release();
                    throw throwable;
                }
            }
            KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE);
            try (InputStream is = Files.newInputStream(keyStore, new OpenOption[0]);){
                ks.load(is, PASSWORD.toCharArray());
            }
            KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry)ks.getEntry(ALIAS, new KeyStore.PasswordProtection(PASSWORD.toCharArray()));
            builder.paths = SelfSignedCertificate.newSelfSignedCertificate(builder.fqdn, entry.getPrivateKey(), (X509Certificate)entry.getCertificate());
            builder.privateKey = entry.getPrivateKey();
        }
        finally {
            Files.deleteIfExists(keyStore);
            Files.delete(directory);
        }
    }

    static {
        Path likely;
        DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.ROOT);
        String home = System.getProperty("java.home");
        KEYTOOL = home == null ? null : (Files.exists(likely = Paths.get(home, new String[0]).resolve("bin").resolve("keytool"), new LinkOption[0]) ? likely : null);
        KEY_STORE_TYPE = PlatformDependent.javaVersion() >= 11 ? "PKCS12" : "JKS";
    }
}

