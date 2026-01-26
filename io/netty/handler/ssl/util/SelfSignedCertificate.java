/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import io.netty.handler.ssl.util.BouncyCastleSelfSignedCertGenerator;
import io.netty.handler.ssl.util.CertificateBuilderCertGenerator;
import io.netty.handler.ssl.util.KeytoolSelfSignedCertGenerator;
import io.netty.handler.ssl.util.OpenJdkSelfSignedCertGenerator;
import io.netty.handler.ssl.util.ThreadLocalInsecureRandom;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

@Deprecated
public final class SelfSignedCertificate {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelfSignedCertificate.class);
    private static final Date DEFAULT_NOT_BEFORE = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotBefore", System.currentTimeMillis() - 31536000000L));
    private static final Date DEFAULT_NOT_AFTER = new Date(SystemPropertyUtil.getLong("io.netty.selfSignedCertificate.defaultNotAfter", 253402300799000L));
    private static final int DEFAULT_KEY_LENGTH_BITS = SystemPropertyUtil.getInt("io.netty.handler.ssl.util.selfSignedKeyStrength", 2048);
    private final File certificate;
    private final File privateKey;
    private final X509Certificate cert;
    private final PrivateKey key;

    public SelfSignedCertificate() throws CertificateException {
        this(new Builder());
    }

    public SelfSignedCertificate(Date notBefore, Date notAfter) throws CertificateException {
        this(new Builder().notBefore(notBefore).notAfter(notAfter));
    }

    public SelfSignedCertificate(Date notBefore, Date notAfter, String algorithm, int bits) throws CertificateException {
        this(new Builder().notBefore(notBefore).notAfter(notAfter).algorithm(algorithm).bits(bits));
    }

    public SelfSignedCertificate(String fqdn) throws CertificateException {
        this(new Builder().fqdn(fqdn));
    }

    public SelfSignedCertificate(String fqdn, String algorithm, int bits) throws CertificateException {
        this(new Builder().fqdn(fqdn).algorithm(algorithm).bits(bits));
    }

    public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter) throws CertificateException {
        this(new Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter));
    }

    public SelfSignedCertificate(String fqdn, Date notBefore, Date notAfter, String algorithm, int bits) throws CertificateException {
        this(new Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter).algorithm(algorithm).bits(bits));
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits) throws CertificateException {
        this(new Builder().fqdn(fqdn).random(random).bits(bits));
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, String algorithm, int bits) throws CertificateException {
        this(new Builder().fqdn(fqdn).random(random).algorithm(algorithm).bits(bits));
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter) throws CertificateException {
        this(new Builder().fqdn(fqdn).notBefore(notBefore).notAfter(notAfter).random(random).bits(bits));
    }

    public SelfSignedCertificate(String fqdn, SecureRandom random, int bits, Date notBefore, Date notAfter, String algorithm) throws CertificateException {
        this(new Builder().fqdn(fqdn).random(random).algorithm(algorithm).bits(bits).notBefore(notBefore).notAfter(notAfter));
    }

    private SelfSignedCertificate(Builder builder) throws CertificateException {
        if (!(builder.generateCertificateBuilder() || builder.generateBc() || builder.generateKeytool() || builder.generateSunMiscSecurity())) {
            throw (CertificateException)builder.failure;
        }
        this.certificate = new File(builder.paths[0]);
        this.privateKey = new File(builder.paths[1]);
        this.key = builder.privateKey;
        try (FileInputStream certificateInput = new FileInputStream(this.certificate);){
            this.cert = (X509Certificate)CertificateFactory.getInstance("X509").generateCertificate(certificateInput);
        }
        catch (Exception e) {
            throw new CertificateEncodingException(e);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public File certificate() {
        return this.certificate;
    }

    public File privateKey() {
        return this.privateKey;
    }

    public X509Certificate cert() {
        return this.cert;
    }

    public PrivateKey key() {
        return this.key;
    }

    public void delete() {
        SelfSignedCertificate.safeDelete(this.certificate);
        SelfSignedCertificate.safeDelete(this.privateKey);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static String[] newSelfSignedCertificate(String fqdn, PrivateKey key, X509Certificate cert) throws IOException, CertificateEncodingException {
        String certText;
        String keyText;
        ByteBuf encodedBuf;
        ByteBuf wrappedBuf = Unpooled.wrappedBuffer(key.getEncoded());
        try {
            encodedBuf = Base64.encode(wrappedBuf, true);
            try {
                keyText = "-----BEGIN PRIVATE KEY-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END PRIVATE KEY-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        fqdn = fqdn.replaceAll("[^\\w.-]", "x");
        File keyFile = PlatformDependent.createTempFile("keyutil_" + fqdn + '_', ".key", null);
        keyFile.deleteOnExit();
        FileOutputStream keyOut = new FileOutputStream(keyFile);
        try {
            ((OutputStream)keyOut).write(keyText.getBytes(CharsetUtil.US_ASCII));
            ((OutputStream)keyOut).close();
            keyOut = null;
        }
        finally {
            if (keyOut != null) {
                SelfSignedCertificate.safeClose(keyFile, keyOut);
                SelfSignedCertificate.safeDelete(keyFile);
            }
        }
        wrappedBuf = Unpooled.wrappedBuffer(cert.getEncoded());
        try {
            encodedBuf = Base64.encode(wrappedBuf, true);
            try {
                certText = "-----BEGIN CERTIFICATE-----\n" + encodedBuf.toString(CharsetUtil.US_ASCII) + "\n-----END CERTIFICATE-----\n";
            }
            finally {
                encodedBuf.release();
            }
        }
        finally {
            wrappedBuf.release();
        }
        File certFile = PlatformDependent.createTempFile("keyutil_" + fqdn + '_', ".crt", null);
        certFile.deleteOnExit();
        FileOutputStream certOut = new FileOutputStream(certFile);
        try {
            ((OutputStream)certOut).write(certText.getBytes(CharsetUtil.US_ASCII));
            ((OutputStream)certOut).close();
            certOut = null;
        }
        finally {
            if (certOut != null) {
                SelfSignedCertificate.safeClose(certFile, certOut);
                SelfSignedCertificate.safeDelete(certFile);
                SelfSignedCertificate.safeDelete(keyFile);
            }
        }
        return new String[]{certFile.getPath(), keyFile.getPath()};
    }

    private static void safeDelete(File certFile) {
        if (!certFile.delete() && logger.isWarnEnabled()) {
            logger.warn("Failed to delete a file: " + certFile);
        }
    }

    private static void safeClose(File keyFile, OutputStream keyOut) {
        block2: {
            try {
                keyOut.close();
            }
            catch (IOException e) {
                if (!logger.isWarnEnabled()) break block2;
                logger.warn("Failed to close a file: " + keyFile, e);
            }
        }
    }

    private static boolean isBouncyCastleAvailable() {
        try {
            Class.forName("org.bouncycastle.cert.X509v3CertificateBuilder");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    static /* synthetic */ int access$100() {
        return DEFAULT_KEY_LENGTH_BITS;
    }

    static /* synthetic */ Date access$200() {
        return DEFAULT_NOT_BEFORE;
    }

    static /* synthetic */ Date access$300() {
        return DEFAULT_NOT_AFTER;
    }

    public static final class Builder {
        String fqdn = "localhost";
        SecureRandom random;
        int bits = SelfSignedCertificate.access$100();
        Date notBefore = SelfSignedCertificate.access$200();
        Date notAfter = SelfSignedCertificate.access$300();
        String algorithm = "RSA";
        Throwable failure;
        KeyPair keypair;
        PrivateKey privateKey;
        String[] paths;

        private Builder() {
        }

        public Builder fqdn(String fqdn) {
            this.fqdn = ObjectUtil.checkNotNullWithIAE(fqdn, "fqdn");
            return this;
        }

        public Builder random(SecureRandom random) {
            this.random = random;
            return this;
        }

        public Builder bits(int bits) {
            this.bits = bits;
            return this;
        }

        public Builder notBefore(Date notBefore) {
            this.notBefore = ObjectUtil.checkNotNullWithIAE(notBefore, "notBefore");
            return this;
        }

        public Builder notAfter(Date notAfter) {
            this.notAfter = ObjectUtil.checkNotNullWithIAE(notAfter, "notAfter");
            return this;
        }

        public Builder algorithm(String algorithm) {
            if ("EC".equalsIgnoreCase(algorithm)) {
                this.algorithm = "EC";
            } else if ("RSA".equalsIgnoreCase(algorithm)) {
                this.algorithm = "RSA";
            } else {
                throw new IllegalArgumentException("Algorithm not valid: " + algorithm);
            }
            return this;
        }

        private SecureRandom randomOrDefault() {
            return this.random == null ? ThreadLocalInsecureRandom.current() : this.random;
        }

        private void generateKeyPairLocally() {
            if (this.keypair != null) {
                return;
            }
            try {
                KeyPairGenerator keyGen = KeyPairGenerator.getInstance(this.algorithm);
                keyGen.initialize(this.bits, this.randomOrDefault());
                this.keypair = keyGen.generateKeyPair();
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
            this.privateKey = this.keypair.getPrivate();
        }

        private void addFailure(Throwable t) {
            if (this.failure != null) {
                t.addSuppressed(this.failure);
            }
            this.failure = t;
        }

        boolean generateBc() {
            if (!SelfSignedCertificate.isBouncyCastleAvailable()) {
                logger.debug("Failed to generate a self-signed X.509 certificate because BouncyCastle PKIX is not available in classpath");
                return false;
            }
            this.generateKeyPairLocally();
            try {
                this.paths = BouncyCastleSelfSignedCertGenerator.generate(this.fqdn, this.keypair, this.randomOrDefault(), this.notBefore, this.notAfter, this.algorithm);
                return true;
            }
            catch (Throwable t) {
                logger.debug("Failed to generate a self-signed X.509 certificate using Bouncy Castle:", t);
                this.addFailure(t);
                return false;
            }
        }

        boolean generateKeytool() {
            if (!KeytoolSelfSignedCertGenerator.isAvailable()) {
                logger.debug("Not attempting to generate certificate with keytool because keytool is missing");
                return false;
            }
            if (this.random != null) {
                logger.debug("Not attempting to generate certificate with keytool because of explicitly set SecureRandom");
                return false;
            }
            try {
                KeytoolSelfSignedCertGenerator.generate(this);
                return true;
            }
            catch (Throwable t) {
                logger.debug("Failed to generate a self-signed X.509 certificate using keytool:", t);
                this.addFailure(t);
                return false;
            }
        }

        boolean generateCertificateBuilder() {
            if (!CertificateBuilderCertGenerator.isAvailable()) {
                logger.debug("Not attempting to generate a certificate with CertificateBuilder because it's not available on the classpath");
                return false;
            }
            try {
                CertificateBuilderCertGenerator.generate(this);
                return true;
            }
            catch (CertificateException ce) {
                logger.debug(ce);
                this.addFailure(ce);
            }
            catch (Exception e) {
                String msg = "Failed to generate a self-signed X.509 certificate using CertificateBuilder:";
                logger.debug(msg, e);
                this.addFailure(new CertificateException(msg, e));
            }
            return false;
        }

        boolean generateSunMiscSecurity() {
            this.generateKeyPairLocally();
            try {
                this.paths = OpenJdkSelfSignedCertGenerator.generate(this.fqdn, this.keypair, this.randomOrDefault(), this.notBefore, this.notAfter, this.algorithm);
                return true;
            }
            catch (Throwable t2) {
                logger.debug("Failed to generate a self-signed X.509 certificate using sun.security.x509:", t2);
                CertificateException certificateException = new CertificateException("No provider succeeded to generate a self-signed certificate. See debug log for the root cause.", t2);
                this.addFailure(certificateException);
                return false;
            }
        }

        public SelfSignedCertificate build() throws CertificateException {
            return new SelfSignedCertificate(this);
        }
    }
}

